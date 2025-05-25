package com.example.music_land.firebase;

import android.content.Context;
import android.util.Log;

import com.example.music_land.MusicLandApp;
import com.example.music_land.R;
import com.example.music_land.network.OkHttpProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.music_land.data.model.UserNote;
import com.example.music_land.data.model.UserScore;

public class SupabaseManager {
    private static final String TAG = "SupabaseManager";
    private static SupabaseManager instance;
    
    // Таблицы
    private static final String NOTES_TABLE = "notes";
    private static final String SCORES_TABLE = "scores";
    
    private String supabaseUrl;
    private String supabaseKey;
    private OkHttpClient client;
    private boolean isInitialized = false;
    
    // Интерфейс для обратных вызовов при получении заметок
    public interface NotesCallback {
        void onNotesLoaded(List<UserNote> notes);
        void onError(String errorMessage);
    }
    
    // Интерфейс для обратных вызовов при получении списка лучших результатов
    public interface LeaderboardCallback {
        void onLeaderboardLoaded(List<UserScore> leaderboardUsers);
        void onError(String errorMessage);
    }
    
    // Интерфейс для обратных вызовов при сохранении
    public interface SaveCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
    
    private SupabaseManager() {
        // Инициализация OkHttpClient через Kotlin-провайдер
        try {
            // Используем нашу Kotlin-обертку
            client = OkHttpProvider.INSTANCE.getClient();
            
            Context context = MusicLandApp.getInstance();
            supabaseUrl = context.getString(R.string.supabase_url);
            supabaseKey = context.getString(R.string.supabase_key);
            
            // Проверяем, что URL и ключ не являются значениями по умолчанию
            if (!supabaseUrl.contains("YOUR_PROJECT_ID") && !supabaseKey.equals("YOUR_API_KEY")) {
                isInitialized = true;
                Log.d(TAG, "Supabase успешно инициализирован с URL: " + supabaseUrl);
            } else {
                Log.e(TAG, "Supabase не инициализирован: URL или ключ имеют значения по умолчанию");
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка инициализации Supabase: " + e.getMessage(), e);
        }
    }
    
    public static synchronized SupabaseManager getInstance() {
        if (instance == null) {
            instance = new SupabaseManager();
        }
        return instance;
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
    
    // Сохранение заметки
    public void saveNote(String userId, String content, SaveCallback callback) {
        if (!isInitialized) {
            callback.onError("Supabase не инициализирован");
            return;
        }
        
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);
            jsonObject.put("content", content);
            jsonObject.put("timestamp", System.currentTimeMillis());
            
            // Исправленный вызов для OkHttp3
            final MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            
            Request request = new Request.Builder()
                .url(supabaseUrl + "/rest/v1/" + NOTES_TABLE)
                .post(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();
            
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Ошибка сохранения заметки: " + e.getMessage());
                    callback.onError("Ошибка сети: " + e.getMessage());
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String error = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                        Log.e(TAG, "Ошибка сохранения заметки: " + error);
                        callback.onError("Ошибка сервера: " + error);
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Ошибка создания JSON: " + e.getMessage());
            callback.onError("Ошибка создания запроса: " + e.getMessage());
        }
    }
    
    // Получение заметок пользователя
    public void getUserNotes(String userId, NotesCallback callback) {
        if (!isInitialized) {
            callback.onError("Supabase не инициализирован");
            return;
        }
        
        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + NOTES_TABLE + "?user_id=eq." + userId + "&order=timestamp.desc")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Ошибка получения заметок: " + e.getMessage());
                callback.onError("Ошибка сети: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        List<UserNote> notes = new ArrayList<>();
                        
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            
                            UserNote note = new UserNote();
                            note.setId(jsonObject.getString("id"));
                            note.setUserId(jsonObject.getString("user_id"));
                            note.setContent(jsonObject.getString("content"));
                            note.setTimestamp(jsonObject.getLong("timestamp"));
                            
                            notes.add(note);
                        }
                        
                        callback.onNotesLoaded(notes);
                    } catch (JSONException e) {
                        Log.e(TAG, "Ошибка парсинга JSON: " + e.getMessage());
                        callback.onError("Ошибка обработки данных: " + e.getMessage());
                    }
                } else {
                    String error = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                    Log.e(TAG, "Ошибка получения заметок: " + error);
                    callback.onError("Ошибка сервера: " + error);
                }
            }
        });
    }
    
    // Удаление заметки
    public void deleteNote(String noteId, SaveCallback callback) {
        if (!isInitialized) {
            callback.onError("Supabase не инициализирован");
            return;
        }
        
        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + NOTES_TABLE + "?id=eq." + noteId)
            .delete()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Ошибка удаления заметки: " + e.getMessage());
                callback.onError("Ошибка сети: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    String error = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                    Log.e(TAG, "Ошибка удаления заметки: " + error);
                    callback.onError("Ошибка сервера: " + error);
                }
            }
        });
    }
    
    // Сохранение результата теста
    public void addUserScore(String userId, String userName, String genre, int score, SaveCallback callback) {
        if (!isInitialized) {
            callback.onError("Supabase не инициализирован");
            return;
        }
        
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);
            jsonObject.put("user_name", userName);
            jsonObject.put("genre", genre);
            jsonObject.put("score", score);
            jsonObject.put("date", System.currentTimeMillis());
            
            // Исправленный вызов для OkHttp3
            final MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            
            Request request = new Request.Builder()
                .url(supabaseUrl + "/rest/v1/" + SCORES_TABLE)
                .post(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();
            
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Ошибка сохранения результата: " + e.getMessage());
                    callback.onError("Ошибка сети: " + e.getMessage());
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String error = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                        Log.e(TAG, "Ошибка сохранения результата: " + error);
                        callback.onError("Ошибка сервера: " + error);
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Ошибка создания JSON: " + e.getMessage());
            callback.onError("Ошибка создания запроса: " + e.getMessage());
        }
    }
    
    // Получение таблицы лидеров по жанру
    public void getLeaderboardByGenre(String genre, int limit, LeaderboardCallback callback) {
        if (!isInitialized) {
            callback.onError("Supabase не инициализирован");
            return;
        }
        
        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + SCORES_TABLE + "?genre=eq." + genre + "&order=score.desc&limit=" + limit)
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Ошибка получения лидерборда: " + e.getMessage());
                callback.onError("Ошибка сети: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        List<UserScore> scores = new ArrayList<>();
                        
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            
                            String userId = jsonObject.getString("user_id");
                            String userName = jsonObject.getString("user_name");
                            int score = jsonObject.getInt("score");
                            int rank = i + 1;
                            
                            UserScore userScore = new UserScore(userId, userName, score, rank);
                            scores.add(userScore);
                        }
                        
                        callback.onLeaderboardLoaded(scores);
                    } catch (JSONException e) {
                        Log.e(TAG, "Ошибка парсинга JSON: " + e.getMessage());
                        callback.onError("Ошибка обработки данных: " + e.getMessage());
                    }
                } else {
                    String error = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                    Log.e(TAG, "Ошибка получения лидерборда: " + error);
                    callback.onError("Ошибка сервера: " + error);
                }
            }
        });
    }
} 