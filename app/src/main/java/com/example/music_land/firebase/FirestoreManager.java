package com.example.music_land.firebase;

import android.content.Context;
import com.example.music_land.data.model.Lesson;
import com.example.music_land.data.model.User;
import com.example.music_land.data.model.UserNote;
import com.example.music_land.data.model.UserScore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreManager {
    private static FirestoreManager instance;
    private FirebaseFirestore firestore;
    private Context context;
    
    // Коллекции в базе данных
    private static final String USERS_COLLECTION = "users";
    private static final String SCORES_COLLECTION = "scores";
    private static final String NOTES_COLLECTION = "notes";
    
    // Интерфейс для обратных вызовов при получении данных пользователя
    public interface UserCallback {
        void onUserLoaded(User user);
        void onError(String errorMessage);
    }
    
    // Интерфейс для обратных вызовов при получении списка лучших результатов
    public interface LeaderboardCallback {
        void onLeaderboardLoaded(List<UserScore> leaderboardUsers);
        void onError(String errorMessage);
    }
    
    // Интерфейс для обратных вызовов при получении заметок
    public interface NotesCallback {
        void onNotesLoaded(List<UserNote> notes);
        void onError(String errorMessage);
    }
    
    // Интерфейс для обратных вызовов при получении уроков
    public interface LessonsCallback {
        void onLessonsLoaded(List<Lesson> lessons);
        void onError(String errorMessage);
    }
    
    // Интерфейс для обратных вызовов при сохранении заметок
    public interface SaveNoteCallback {
        void onSuccess(String noteId);
        void onError(String errorMessage);
    }
    
    private FirestoreManager(Context context) {
        this.context = context.getApplicationContext();
        firestore = FirebaseFirestore.getInstance();
    }
    
    public static synchronized FirestoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirestoreManager(context);
        }
        return instance;
    }
    
    public static synchronized FirestoreManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FirestoreManager не инициализирован. Сначала вызовите getInstance(Context)");
        }
        return instance;
    }
    
    // Получение данных текущего пользователя
    public void getCurrentUserData(UserCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        firestore.collection(USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onUserLoaded(user);
                    } else {
                        callback.onError("Данные пользователя не найдены");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Ошибка получения данных: " + e.getMessage()));
    }
    
    // Обновление данных пользователя
    public Task<Void> updateUserData(User user) {
        return firestore.collection(USERS_COLLECTION)
                .document(user.getUid())
                .set(user.toMap());
    }
    
    // Добавление очков пользователю
    public void addUserScore(String genre, int score, FirebaseAuthManager.AuthCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        // Проверка на пустой жанр
        if (genre == null || genre.isEmpty()) {
            callback.onError("Не указан жанр");
            return;
        }
        
        // Получаем текущие данные пользователя
        firestore.collection(USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        
                        if (user == null) {
                            callback.onError("Ошибка получения данных пользователя");
                            return;
                        }
                        
                        // Обновляем счет и количество пройденных тестов
                        user.addScore(score);
                        user.incrementQuizzesCompleted();
                        
                        // Сохраняем обновленные данные пользователя
                        updateUserData(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Добавляем запись в таблицу рейтинга
                                    Map<String, Object> scoreData = new HashMap<>();
                                    scoreData.put("userId", currentUser.getUid());
                                    scoreData.put("userName", user.getDisplayName());
                                    scoreData.put("genre", genre);
                                    scoreData.put("score", score);
                                    scoreData.put("date", System.currentTimeMillis());
                                    
                                    firestore.collection(SCORES_COLLECTION)
                                            .add(scoreData)
                                            .addOnSuccessListener(documentReference -> {
                                                callback.onSuccess();
                                            })
                                            .addOnFailureListener(e -> {
                                                callback.onError("Ошибка сохранения результата: " + e.getMessage());
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    callback.onError("Ошибка обновления данных: " + e.getMessage());
                                });
                    } else {
                        callback.onError("Данные пользователя не найдены");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка получения данных: " + e.getMessage());
                });
    }
    
    // Получение таблицы лидеров по жанру
    public void getLeaderboardByGenre(String genre, int limit, LeaderboardCallback callback) {
        // Проверка на пустой жанр
        if (genre == null || genre.isEmpty()) {
            callback.onError("Не указан жанр");
            return;
        }
        
        firestore.collection(SCORES_COLLECTION)
                .whereEqualTo("genre", genre)
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserScore> scores = new ArrayList<>();
                    
                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onLeaderboardLoaded(scores);
                        return;
                    }
                    
                    int rank = 1;
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String userId = document.getId();
                        String displayName = document.getString("userName");
                        int score = document.getLong("score") != null ? document.getLong("score").intValue() : 0;
                        
                        UserScore scoreEntry = new UserScore(userId, displayName, score, rank++);
                        scores.add(scoreEntry);
                    }
                    callback.onLeaderboardLoaded(scores);
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка загрузки рейтинга: " + e.getMessage());
                });
    }
    
    // Отметка урока как пройденного
    public void markLessonAsCompleted(FirebaseAuthManager.AuthCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        // Получаем текущие данные пользователя
        firestore.collection(USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        
                        // Увеличиваем количество пройденных уроков
                        user.incrementLessonsCompleted();
                        
                        // Сохраняем обновленные данные пользователя
                        updateUserData(user)
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onError("Ошибка обновления данных: " + e.getMessage()));
                    } else {
                        callback.onError("Данные пользователя не найдены");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Ошибка получения данных: " + e.getMessage()));
    }
    
    // =================== Методы для работы с заметками ===================
    
    // Сохранение заметки
    public void saveNote(String content, FirebaseAuthManager.AuthCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("userId", currentUser.getUid());
        noteData.put("content", content);
        noteData.put("timestamp", System.currentTimeMillis());
        
        firestore.collection(NOTES_COLLECTION)
                .add(noteData)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка сохранения заметки: " + e.getMessage());
                });
    }
    
    // Получение заметок пользователя
    public void getUserNotes(NotesCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        firestore.collection(NOTES_COLLECTION)
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserNote> notesList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        UserNote note = document.toObject(UserNote.class);
                        note.setId(document.getId());
                        notesList.add(note);
                    }
                    callback.onNotesLoaded(notesList);
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка загрузки заметок: " + e.getMessage());
                });
    }
    
    // Обновление заметки
    public void updateNote(UserNote note, FirebaseAuthManager.AuthCallback callback) {
        if (note.getId() == null) {
            callback.onError("Некорректный ID заметки");
            return;
        }
        
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("userId", note.getUserId());
        noteData.put("content", note.getContent());
        noteData.put("timestamp", note.getTimestamp());
        
        firestore.collection(NOTES_COLLECTION)
                .document(note.getId())
                .update(noteData)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка обновления заметки: " + e.getMessage());
                });
    }
    
    // Удаление заметки
    public void deleteNote(String noteId, FirebaseAuthManager.AuthCallback callback) {
        firestore.collection(NOTES_COLLECTION)
                .document(noteId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка удаления заметки: " + e.getMessage());
                });
    }

    public void getLeaderboardData(LeaderboardCallback callback) {
        firestore.collection(SCORES_COLLECTION)
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserScore> userScores = new ArrayList<>();
                    int rank = 1;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userId = document.getId();
                        String displayName = document.getString("userName");
                        int score = document.getLong("score").intValue();
                        userScores.add(new UserScore(userId, displayName, score, rank++));
                    }
                    callback.onLeaderboardLoaded(userScores);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Получение списка уроков
    public void getLessons(LessonsCallback callback) {
        firestore.collection("lessons")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Lesson> lessons = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Lesson lesson = document.toObject(Lesson.class);
                        if (lesson != null) {
                            lesson.setId(document.getId());
                            lessons.add(lesson);
                        }
                    }
                    callback.onLessonsLoaded(lessons);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Сохранение заметки с возвращением ID
    public void saveNoteAndGetId(String title, String content, SaveNoteCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("userId", currentUser.getUid());
        noteData.put("title", title);
        noteData.put("content", content);
        noteData.put("timestamp", System.currentTimeMillis());
        
        firestore.collection(NOTES_COLLECTION)
                .add(noteData)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка сохранения заметки: " + e.getMessage());
                });
    }
    
    // For backward compatibility
    public void saveNoteAndGetId(String content, SaveNoteCallback callback) {
        saveNoteAndGetId("", content, callback);
    }
} 