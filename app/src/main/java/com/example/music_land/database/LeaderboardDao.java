package com.example.music_land.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.music_land.data.model.UserScore;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardDao {
    
    private static final String TAG = "LeaderboardDao";
    private DatabaseHelper dbHelper;
    
    public LeaderboardDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }
    
    /**
     * Сохраняет записи в таблицу лидеров
     */
    public boolean saveUserScores(List<UserScore> scores) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = true;
        
        try {
            // Начинаем транзакцию
            db.beginTransaction();
            
            // Очищаем таблицу перед добавлением новых данных
            db.delete(DatabaseHelper.TABLE_LEADERBOARD, null, null);
            
            for (UserScore score : scores) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_LEADERBOARD_USER_ID, score.getUserId());
                values.put(DatabaseHelper.COLUMN_LEADERBOARD_DISPLAY_NAME, score.getDisplayName());
                values.put(DatabaseHelper.COLUMN_LEADERBOARD_SCORE, score.getScore());
                values.put(DatabaseHelper.COLUMN_LEADERBOARD_RANK, score.getRank());
                
                long insertId = db.insert(DatabaseHelper.TABLE_LEADERBOARD, null, values);
                if (insertId == -1) {
                    Log.e(TAG, "Ошибка при сохранении записи лидерборда для пользователя: " + score.getDisplayName());
                    success = false;
                }
            }
            
            // Если все хорошо, подтверждаем транзакцию
            if (success) {
                db.setTransactionSuccessful();
                Log.d(TAG, "Успешно сохранено " + scores.size() + " записей в таблицу лидеров");
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при сохранении записей в таблицу лидеров: " + e.getMessage());
            success = false;
        } finally {
            // Завершаем транзакцию
            db.endTransaction();
        }
        
        return success;
    }
    
    /**
     * Получает все записи из таблицы лидеров
     */
    public List<UserScore> getAllScores() {
        List<UserScore> scores = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_LEADERBOARD,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_LEADERBOARD_RANK + " ASC"
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String userId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEADERBOARD_USER_ID));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEADERBOARD_DISPLAY_NAME));
                    int score = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEADERBOARD_SCORE));
                    int rank = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEADERBOARD_RANK));
                    
                    UserScore userScore = new UserScore(userId, displayName, score, rank);
                    scores.add(userScore);
                } while (cursor.moveToNext());
                
                Log.d(TAG, "Загружено " + scores.size() + " записей из таблицы лидеров");
            } else {
                Log.d(TAG, "Таблица лидеров пуста");
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке записей из таблицы лидеров: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return scores;
    }
    
    /**
     * Добавляет новую запись в таблицу лидеров
     */
    public boolean addScore(UserScore score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LEADERBOARD_USER_ID, score.getUserId());
        values.put(DatabaseHelper.COLUMN_LEADERBOARD_DISPLAY_NAME, score.getDisplayName());
        values.put(DatabaseHelper.COLUMN_LEADERBOARD_SCORE, score.getScore());
        values.put(DatabaseHelper.COLUMN_LEADERBOARD_RANK, score.getRank());
        
        long insertId = db.insert(DatabaseHelper.TABLE_LEADERBOARD, null, values);
        if (insertId != -1) {
            Log.d(TAG, "Успешно добавлена запись в таблицу лидеров для пользователя: " + score.getDisplayName());
            return true;
        } else {
            Log.e(TAG, "Ошибка при добавлении записи в таблицу лидеров для пользователя: " + score.getDisplayName());
            return false;
        }
    }
    
    /**
     * Очищает таблицу лидеров
     */
    public boolean clearLeaderboard() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete(DatabaseHelper.TABLE_LEADERBOARD, null, null);
            Log.d(TAG, "Таблица лидеров очищена");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при очистке таблицы лидеров: " + e.getMessage());
            return false;
        }
    }
} 