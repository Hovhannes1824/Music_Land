package com.example.music_land.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String TAG = "DatabaseHelper";
    
    // Информация о базе данных
    private static final String DATABASE_NAME = "music_land.db";
    private static final int DATABASE_VERSION = 2;
    
    // Таблица пользователей
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_EMAIL = "user_email";
    public static final String COLUMN_USER_SCORE = "user_score";
    
    // Таблица рейтинга
    public static final String TABLE_LEADERBOARD = "leaderboard";
    public static final String COLUMN_LEADERBOARD_ID = "id";
    public static final String COLUMN_LEADERBOARD_USER_ID = "user_id";
    public static final String COLUMN_LEADERBOARD_DISPLAY_NAME = "display_name";
    public static final String COLUMN_LEADERBOARD_SCORE = "score";
    public static final String COLUMN_LEADERBOARD_RANK = "rank";
    
    // Таблица уроков
    public static final String TABLE_LESSONS = "lessons";
    public static final String COLUMN_LESSON_ID = "id";
    public static final String COLUMN_LESSON_TITLE = "title";
    public static final String COLUMN_LESSON_DESCRIPTION = "description";
    public static final String COLUMN_LESSON_CONTENT = "content";
    public static final String COLUMN_LESSON_IMAGE_URL = "image_url";
    public static final String COLUMN_LESSON_ORDER = "lesson_order";
    public static final String COLUMN_LESSON_CATEGORY = "category";
    public static final String COLUMN_LESSON_DURATION = "duration";
    public static final String COLUMN_LESSON_HAS_QUIZ = "has_quiz";
    
    // SQL запросы для создания таблиц
    private static final String SQL_CREATE_USERS_TABLE = 
            "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
            COLUMN_USER_NAME + " TEXT, " +
            COLUMN_USER_EMAIL + " TEXT, " +
            COLUMN_USER_SCORE + " INTEGER DEFAULT 0)";
    
    private static final String SQL_CREATE_LEADERBOARD_TABLE = 
            "CREATE TABLE " + TABLE_LEADERBOARD + " (" +
            COLUMN_LEADERBOARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_LEADERBOARD_USER_ID + " TEXT, " +
            COLUMN_LEADERBOARD_DISPLAY_NAME + " TEXT, " +
            COLUMN_LEADERBOARD_SCORE + " INTEGER, " +
            COLUMN_LEADERBOARD_RANK + " INTEGER)";
    
    private static final String SQL_CREATE_LESSONS_TABLE = 
            "CREATE TABLE " + TABLE_LESSONS + " (" +
            COLUMN_LESSON_ID + " TEXT PRIMARY KEY, " +
            COLUMN_LESSON_TITLE + " TEXT, " +
            COLUMN_LESSON_DESCRIPTION + " TEXT, " +
            COLUMN_LESSON_CONTENT + " TEXT, " +
            COLUMN_LESSON_IMAGE_URL + " TEXT, " +
            COLUMN_LESSON_ORDER + " INTEGER, " +
            COLUMN_LESSON_CATEGORY + " TEXT, " +
            COLUMN_LESSON_DURATION + " INTEGER, " +
            COLUMN_LESSON_HAS_QUIZ + " INTEGER)";
    
    private static DatabaseHelper instance;
    
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }
    
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_USERS_TABLE);
            db.execSQL(SQL_CREATE_LEADERBOARD_TABLE);
            db.execSQL(SQL_CREATE_LESSONS_TABLE);
            Log.d(TAG, "База данных успешно создана");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при создании базы данных: " + e.getMessage());
        }
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // При обновлении версии базы данных удаляем старые таблицы и создаем новые
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEADERBOARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        onCreate(db);
    }
} 