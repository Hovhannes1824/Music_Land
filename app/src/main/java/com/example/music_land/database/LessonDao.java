package com.example.music_land.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.music_land.data.model.Lesson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class LessonDao {
    
    private static final String TAG = "LessonDao";
    private DatabaseHelper dbHelper;
    
    public LessonDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }
    
    /**
     * Сохраняет список уроков в базу данных
     */
    public boolean saveLessons(List<Lesson> lessons) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = true;
        
        try {
            // Начинаем транзакцию
            db.beginTransaction();
            
            // Очищаем таблицу перед добавлением новых данных
            int deletedCount = db.delete(DatabaseHelper.TABLE_LESSONS, null, null);
            Log.d(TAG, "Очищено " + deletedCount + " уроков из базы данных");
            
            // Используем HashSet для проверки дубликатов названий
            HashSet<String> lessonTitles = new HashSet<>();
            
            for (Lesson lesson : lessons) {
                // Пропускаем дубликаты по названию
                if (lessonTitles.contains(lesson.getTitle())) {
                    Log.d(TAG, "Дубликат урока пропущен: " + lesson.getTitle());
                    continue;
                }
                
                // Добавляем название в HashSet
                lessonTitles.add(lesson.getTitle());
                
                ContentValues values = new ContentValues();
                
                // Если у урока нет id, генерируем его
                String lessonId = lesson.getId();
                if (lessonId == null || lessonId.isEmpty()) {
                    lessonId = UUID.randomUUID().toString();
                }
                
                values.put(DatabaseHelper.COLUMN_LESSON_ID, lessonId);
                values.put(DatabaseHelper.COLUMN_LESSON_TITLE, lesson.getTitle());
                values.put(DatabaseHelper.COLUMN_LESSON_DESCRIPTION, lesson.getDescription());
                values.put(DatabaseHelper.COLUMN_LESSON_CONTENT, lesson.getContent());
                values.put(DatabaseHelper.COLUMN_LESSON_IMAGE_URL, lesson.getImageUrl());
                values.put(DatabaseHelper.COLUMN_LESSON_ORDER, lesson.getOrder());
                values.put(DatabaseHelper.COLUMN_LESSON_CATEGORY, lesson.getCategory());
                values.put(DatabaseHelper.COLUMN_LESSON_DURATION, lesson.getDurationMinutes());
                values.put(DatabaseHelper.COLUMN_LESSON_HAS_QUIZ, lesson.isHasQuiz() ? 1 : 0);
                
                long insertId = db.insert(DatabaseHelper.TABLE_LESSONS, null, values);
                if (insertId == -1) {
                    Log.e(TAG, "Ошибка при сохранении урока: " + lesson.getTitle());
                    success = false;
                }
            }
            
            // Если все хорошо, подтверждаем транзакцию
            if (success) {
                db.setTransactionSuccessful();
                Log.d(TAG, "Успешно сохранено " + lessons.size() + " уроков");
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при сохранении уроков: " + e.getMessage());
            success = false;
        } finally {
            // Завершаем транзакцию
            db.endTransaction();
        }
        
        return success;
    }
    
    /**
     * Получает все уроки из базы данных
     */
    public List<Lesson> getAllLessons() {
        List<Lesson> lessons = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_LESSONS,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_LESSON_ORDER + " ASC"
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Lesson lesson = cursorToLesson(cursor);
                    lessons.add(lesson);
                } while (cursor.moveToNext());
                
                Log.d(TAG, "Загружено " + lessons.size() + " уроков");
            } else {
                Log.d(TAG, "Таблица уроков пуста");
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке уроков: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return lessons;
    }
    
    /**
     * Получает уроки по определенной категории
     */
    public List<Lesson> getLessonsByCategory(String category) {
        List<Lesson> lessons = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_LESSONS,
                    null,
                    DatabaseHelper.COLUMN_LESSON_CATEGORY + " = ?",
                    new String[]{category},
                    null,
                    null,
                    DatabaseHelper.COLUMN_LESSON_ORDER + " ASC"
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Lesson lesson = cursorToLesson(cursor);
                    lessons.add(lesson);
                } while (cursor.moveToNext());
                
                Log.d(TAG, "Загружено " + lessons.size() + " уроков для категории: " + category);
            } else {
                Log.d(TAG, "Нет уроков для категории: " + category);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке уроков для категории: " + category + ", ошибка: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return lessons;
    }
    
    /**
     * Получает урок по ID
     */
    public Lesson getLessonById(String lessonId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_LESSONS,
                    null,
                    DatabaseHelper.COLUMN_LESSON_ID + " = ?",
                    new String[]{lessonId},
                    null,
                    null,
                    null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                return cursorToLesson(cursor);
            } else {
                Log.d(TAG, "Урок не найден с ID: " + lessonId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке урока с ID: " + lessonId + ", ошибка: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return null;
    }
    
    /**
     * Получает список всех категорий уроков
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = null;
        try {
            cursor = db.query(
                    true, // distinct
                    DatabaseHelper.TABLE_LESSONS,
                    new String[]{DatabaseHelper.COLUMN_LESSON_CATEGORY},
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LESSON_CATEGORY));
                    categories.add(category);
                } while (cursor.moveToNext());
                
                Log.d(TAG, "Загружено " + categories.size() + " категорий");
            } else {
                Log.d(TAG, "Нет категорий в базе данных");
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке категорий: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return categories;
    }
    
    /**
     * Преобразует курсор в объект Lesson
     */
    private Lesson cursorToLesson(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LESSON_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LESSON_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LESSON_DESCRIPTION));
        String content = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LESSON_CONTENT));
        String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LESSON_IMAGE_URL));
        int order = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LESSON_ORDER));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LESSON_CATEGORY));
        int duration = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LESSON_DURATION));
        boolean hasQuiz = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LESSON_HAS_QUIZ)) == 1;
        
        Lesson lesson = new Lesson(title, description, content, imageUrl, order, category, duration, hasQuiz);
        lesson.setId(id);
        
        return lesson;
    }
} 