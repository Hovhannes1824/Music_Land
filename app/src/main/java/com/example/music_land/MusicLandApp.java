package com.example.music_land;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.music_land.firebase.CloudinaryManager;
import com.example.music_land.firebase.FirestoreManager;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MusicLandApp extends Application {
    private static MusicLandApp instance;
    private static final String TAG = "MusicLandApp";
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = getApplicationContext();
        
        // Инициализация Google Play Services
        try {
            Log.d(TAG, "Проверка Google Play Services...");
            int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            if (resultCode != ConnectionResult.SUCCESS) {
                Log.e(TAG, "Google Play Services недоступен. Код ошибки: " + resultCode);
                // Пытаемся исправить проблему
                if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                    Log.d(TAG, "Пытаемся исправить проблему с Google Play Services...");
                    android.app.Activity currentActivity = getCurrentActivity();
                    if (currentActivity != null) {
                        GoogleApiAvailability.getInstance().getErrorDialog(currentActivity, resultCode, 9000).show();
                    }
                }
                return;
            }
            Log.d(TAG, "Google Play Services доступен и работает корректно");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при инициализации Google Play Services: " + e.getMessage(), e);
        }
        
        // Инициализация Firebase
        try {
            Log.d(TAG, "Инициализация Firebase...");
            FirebaseApp.initializeApp(this);
            
            // Настройка Firestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            firestore.setFirestoreSettings(settings);
            
            // Инициализация FirestoreManager
            FirestoreManager.getInstance(this);
            
            Log.d(TAG, "Firebase успешно инициализирован");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при инициализации Firebase: " + e.getMessage(), e);
        }
        
        try {
            // Инициализация Cloudinary из ресурсов
            Log.d(TAG, "Инициализация Cloudinary...");
            String cloudName = getString(R.string.cloudinary_cloud_name);
            String apiKey = getString(R.string.cloudinary_api_key);
            String apiSecret = getString(R.string.cloudinary_api_secret);
            
            Log.d(TAG, "Cloudinary параметры: cloudName=" + cloudName);
            
            if ("ВСТАВЬТЕ_СЮДА_ВАШ_CLOUD_NAME".equals(cloudName)) {
                Log.e(TAG, "Cloudinary cloud_name не настроен! Используется значение по умолчанию.");
            }
            
            CloudinaryManager.getInstance().init(cloudName, apiKey, apiSecret);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при инициализации Cloudinary: " + e.getMessage(), e);
        }
    }
    
    public static MusicLandApp getInstance() {
        return instance;
    }
    
    public static Context getAppContext() {
        return appContext;
    }
    
    private android.app.Activity getCurrentActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Object activities = activityThreadClass.getMethod("getActivities").invoke(activityThread);
            java.lang.reflect.Field field = activities.getClass().getDeclaredField("arrayList");
            field.setAccessible(true);
            java.util.ArrayList list = (java.util.ArrayList) field.get(activities);
            if (list != null && !list.isEmpty()) {
                Object activityClientRecord = list.get(0);
                java.lang.reflect.Field activityField = activityClientRecord.getClass().getDeclaredField("activity");
                activityField.setAccessible(true);
                return (android.app.Activity) activityField.get(activityClientRecord);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении текущей Activity: " + e.getMessage());
        }
        return null;
    }
} 