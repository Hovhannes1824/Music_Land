package com.example.music_land.firebase;

import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CloudinaryManager {
    private static CloudinaryManager instance;
    private boolean isInitialized = false;
    private static final String TAG = "CloudinaryManager";

    // Интерфейс для обратных вызовов при загрузке файла
    public interface UploadCallback {
        void onSuccess(String publicUrl);
        void onError(String errorMessage);
        void onProgress(int progress);
    }

    // Интерфейс для обратных вызовов при удалении файла
    public interface DeleteCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    private CloudinaryManager() {
        // Приватный конструктор для Singleton
    }

    public static synchronized CloudinaryManager getInstance() {
        if (instance == null) {
            instance = new CloudinaryManager();
        }
        return instance;
    }

    // Проверка инициализации
    public boolean isInitialized() {
        return isInitialized;
    }

    // Инициализация Cloudinary (вызывать в Application или MainActivity)
    public void init(String cloudName, String apiKey, String apiSecret) {
        try {
            // Проверка параметров
            if (cloudName == null || cloudName.isEmpty() || 
                cloudName.equals("your_cloud_name") || 
                cloudName.equals("ВСТАВЬТЕ_СЮДА_ВАШ_CLOUD_NAME")) {
                Log.e(TAG, "Cloudinary cloud_name не указан или имеет значение по умолчанию");
                return;
            }
            
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_api_key")) {
                Log.e(TAG, "Cloudinary API key не указан или имеет значение по умолчанию");
                return;
            }
            
            if (apiSecret == null || apiSecret.isEmpty() || apiSecret.equals("your_api_secret")) {
                Log.e(TAG, "Cloudinary API secret не указан или имеет значение по умолчанию");
                return;
            }
            
            if (!isInitialized) {
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", cloudName);
                config.put("api_key", apiKey);
                config.put("api_secret", apiSecret);
                
                try {
                    MediaManager.init(com.example.music_land.MusicLandApp.getInstance(), config);
                    isInitialized = true;
                    Log.d(TAG, "Cloudinary успешно инициализирован с cloud_name: " + cloudName);
                } catch (Exception e) {
                    Log.e(TAG, "Ошибка при инициализации MediaManager: " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Непредвиденная ошибка при инициализации Cloudinary: " + e.getMessage(), e);
        }
    }

    // Загрузка аудиофайла в Cloudinary
    public void uploadAudio(File audioFile, final UploadCallback callback) {
        if (!isInitialized) {
            Log.e(TAG, "Cloudinary не инициализирован. Загрузка невозможна.");
            callback.onError("Cloudinary не инициализирован");
            return;
        }

        if (audioFile == null || !audioFile.exists()) {
            Log.e(TAG, "Файл для загрузки не существует");
            callback.onError("Файл для загрузки не существует");
            return;
        }

        try {
            // Создаем уникальное имя файла
            String fileName = "audio_" + UUID.randomUUID().toString();
            
            Log.d(TAG, "Начинаем загрузку файла: " + audioFile.getAbsolutePath() + 
                  " (размер: " + audioFile.length() + " байт)");
            
            // Настраиваем параметры загрузки
            String requestId = MediaManager.get().upload(audioFile.getAbsolutePath())
                    .option("resource_type", "auto") // Автоопределение типа файла
                    .option("folder", "music_land_recordings") // Папка в Cloudinary
                    .callback(new com.cloudinary.android.callback.UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d(TAG, "Начало загрузки файла: " + audioFile.getName());
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            int progress = (int) ((bytes * 100) / totalBytes);
                            Log.d(TAG, "Прогресс загрузки: " + progress + "%");
                            callback.onProgress(progress);
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String publicUrl = (String) resultData.get("secure_url");
                            Log.d(TAG, "Файл успешно загружен: " + publicUrl);
                            callback.onSuccess(publicUrl);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e(TAG, "Ошибка загрузки файла: " + error.getDescription());
                            callback.onError(error.getDescription());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            Log.d(TAG, "Загрузка перенесена: " + error.getDescription());
                        }
                    })
                    .dispatch();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при отправке запроса на загрузку: " + e.getMessage(), e);
            callback.onError("Ошибка при отправке запроса на загрузку: " + e.getMessage());
        }
    }
} 