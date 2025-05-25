package com.example.music_land.firebase;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;

public class StorageManager {

    private static final String TAG = "StorageManager";
    private static StorageManager instance;
    private FirebaseStorage storage;
    private final String RECORDINGS_FOLDER = "recordings";
    private final int MAX_UPLOAD_SIZE_MB = 5; // Максимальный размер загружаемого файла в мегабайтах

    // Интерфейс для обратных вызовов при загрузке файлов
    public interface UploadCallback {
        void onSuccess(String downloadUrl);
        void onError(String errorMessage);
    }

    // Интерфейс для обратных вызовов при скачивании файлов
    public interface DownloadCallback {
        void onSuccess(File localFile);
        void onError(String errorMessage);
    }

    private StorageManager() {
        try {
            storage = FirebaseStorage.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка инициализации Firebase Storage: " + e.getMessage(), e);
        }
    }

    public static synchronized StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    // Загрузка аудиофайла в Firebase Storage
    public void uploadRecording(File localFile, UploadCallback callback) {
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                String errorMsg = "Пользователь не авторизован";
                Log.e(TAG, errorMsg);
                callback.onError(errorMsg);
                return;
            }
            
            // Проверяем, существует ли файл
            if (!localFile.exists()) {
                String errorMsg = "Файл не существует: " + localFile.getAbsolutePath();
                Log.e(TAG, errorMsg);
                callback.onError(errorMsg);
                return;
            }
            
            // Проверяем размер файла
            if (localFile.length() == 0) {
                String errorMsg = "Файл пуст: " + localFile.getAbsolutePath();
                Log.e(TAG, errorMsg);
                callback.onError(errorMsg);
                return;
            }
            
            // Проверяем максимальный размер
            long fileSizeMB = localFile.length() / (1024 * 1024);
            if (fileSizeMB > MAX_UPLOAD_SIZE_MB) {
                String errorMsg = "Файл слишком большой: " + fileSizeMB + "MB (максимум " + MAX_UPLOAD_SIZE_MB + "MB)";
                Log.e(TAG, errorMsg);
                callback.onError(errorMsg);
                return;
            }

            // Проверяем права доступа к файлу
            if (!localFile.canRead()) {
                String errorMsg = "Нет прав на чтение файла: " + localFile.getAbsolutePath();
                Log.e(TAG, errorMsg);
                callback.onError(errorMsg);
                return;
            }
            
            // Проверяем, что Firebase Storage инициализирован
            if (storage == null) {
                try {
                    storage = FirebaseStorage.getInstance();
                } catch (Exception e) {
                    String errorMsg = "Не удалось инициализировать Firebase Storage: " + e.getMessage();
                    Log.e(TAG, errorMsg, e);
                    callback.onError(errorMsg);
                    return;
                }
            }

            String userId = currentUser.getUid();
            String fileName = userId + "_" + UUID.randomUUID().toString() + 
                              (localFile.getName().toLowerCase().endsWith(".m4a") ? ".m4a" : ".audio");
            final StorageReference recordingRef = storage.getReference()
                    .child(RECORDINGS_FOLDER)
                    .child(userId)
                    .child(fileName);

            Log.d(TAG, "Начинаем загрузку файла в Firebase Storage: " + localFile.getAbsolutePath() + 
                  " (размер: " + localFile.length() + " байт)");
            
            Uri fileUri = Uri.fromFile(localFile);
            UploadTask uploadTask = recordingRef.putFile(fileUri);

            // Добавляем слушатели с использованием методов addOn... для совместимости
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    Log.d(TAG, "Прогресс загрузки: " + (int)progress + "%");
                }
            });
            
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Файл успешно загружен, получаем URL");
                    
                    // Получаем URL загруженного файла
                    Task<Uri> urlTask = recordingRef.getDownloadUrl();
                    
                    urlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            Log.d(TAG, "URL успешно получен: " + downloadUrl);
                            callback.onSuccess(downloadUrl);
                        }
                    });
                    
                    urlTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorMsg = "Ошибка получения URL: " + e.getMessage();
                            Log.e(TAG, errorMsg, e);
                            callback.onError(errorMsg);
                        }
                    });
                }
            });
            
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String errorMsg = "Ошибка загрузки файла: " + e.getMessage();
                    Log.e(TAG, errorMsg, e);
                    callback.onError(errorMsg);
                }
            });
            
        } catch (Exception e) {
            String errorMsg = "Непредвиденная ошибка при загрузке файла: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            callback.onError(errorMsg);
        }
    }

    // Проверка, существует ли файл в локальном хранилище
    public boolean isFileExistsLocally(File file) {
        boolean exists = file != null && file.exists() && file.length() > 0;
        Log.d(TAG, "Проверка локального файла: " + (file != null ? file.getAbsolutePath() : "null") + 
              " - существует: " + exists + (file != null ? ", размер: " + file.length() + " байт" : ""));
        return exists;
    }

    // Скачивание файла из Firebase Storage
    public void downloadRecording(String storageUrl, File localFile, DownloadCallback callback) {
        if (storageUrl == null || storageUrl.trim().isEmpty()) {
            String errorMsg = "URL для скачивания пуст или null";
            Log.e(TAG, errorMsg);
            callback.onError(errorMsg);
            return;
        }
        
        try {
            Log.d(TAG, "Начинаем скачивание файла из Firebase Storage: " + storageUrl);
            StorageReference fileRef = storage.getReferenceFromUrl(storageUrl);
            
            fileRef.getFile(localFile)
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, "Прогресс скачивания: " + (int)progress + "%");
                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "Файл успешно скачан в: " + localFile.getAbsolutePath() + 
                              ", размер: " + localFile.length() + " байт");
                        callback.onSuccess(localFile);
                    })
                    .addOnFailureListener(e -> {
                        String errorMsg = "Ошибка скачивания файла: " + e.getMessage();
                        Log.e(TAG, errorMsg, e);
                        callback.onError(errorMsg);
                    });
        } catch (Exception e) {
            String errorMsg = "Непредвиденная ошибка при скачивании файла: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            callback.onError(errorMsg);
        }
    }

    // Удаление файла из Firebase Storage
    public void deleteRecording(String storageUrl, FirebaseAuthManager.AuthCallback callback) {
        if (storageUrl == null || storageUrl.trim().isEmpty()) {
            String errorMsg = "URL для удаления пуст или null";
            Log.e(TAG, errorMsg);
            callback.onError(errorMsg);
            return;
        }
        
        try {
            Log.d(TAG, "Удаление файла из Firebase Storage: " + storageUrl);
            StorageReference fileRef = storage.getReferenceFromUrl(storageUrl);
            
            fileRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Файл успешно удален из Firebase Storage");
                        callback.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        String errorMsg = "Ошибка удаления файла: " + e.getMessage();
                        Log.e(TAG, errorMsg, e);
                        callback.onError(errorMsg);
                    });
        } catch (Exception e) {
            String errorMsg = "Непредвиденная ошибка при удалении файла: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            callback.onError(errorMsg);
        }
    }
} 