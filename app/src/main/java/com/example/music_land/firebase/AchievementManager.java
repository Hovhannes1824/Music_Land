package com.example.music_land.firebase;

import com.example.music_land.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления достижениями пользователей
 */
public class AchievementManager {
    private static AchievementManager instance;
    private FirebaseFirestore firestore;
    
    // Константы для типов достижений
    public static final String ACHIEVEMENT_FIRST_LESSON = "first_lesson";
    public static final String ACHIEVEMENT_FIVE_LESSONS = "five_lessons";
    public static final String ACHIEVEMENT_TEN_LESSONS = "ten_lessons";
    public static final String ACHIEVEMENT_FIRST_QUIZ = "first_quiz";
    public static final String ACHIEVEMENT_HIGH_SCORE = "high_score";
    public static final String ACHIEVEMENT_PRACTICE_MASTER = "practice_master";
    
    // Константы для достижений по жанрам и сложностям
    public static final String ACHIEVEMENT_CLASSICAL_EASY = "classical_easy";
    public static final String ACHIEVEMENT_CLASSICAL_MEDIUM = "classical_medium";
    public static final String ACHIEVEMENT_CLASSICAL_HARD = "classical_hard";
    public static final String ACHIEVEMENT_ROCK_EASY = "rock_easy";
    public static final String ACHIEVEMENT_ROCK_MEDIUM = "rock_medium";
    public static final String ACHIEVEMENT_ROCK_HARD = "rock_hard";
    public static final String ACHIEVEMENT_JAZZ_EASY = "jazz_easy";
    public static final String ACHIEVEMENT_JAZZ_MEDIUM = "jazz_medium";
    public static final String ACHIEVEMENT_JAZZ_HARD = "jazz_hard";
    public static final String ACHIEVEMENT_POP_EASY = "pop_easy";
    public static final String ACHIEVEMENT_POP_MEDIUM = "pop_medium";
    public static final String ACHIEVEMENT_POP_HARD = "pop_hard";
    public static final String ACHIEVEMENT_FOLK_EASY = "folk_easy";
    public static final String ACHIEVEMENT_FOLK_MEDIUM = "folk_medium";
    public static final String ACHIEVEMENT_FOLK_HARD = "folk_hard";
    public static final String ACHIEVEMENT_ELECTRONIC_EASY = "electronic_easy";
    public static final String ACHIEVEMENT_ELECTRONIC_MEDIUM = "electronic_medium";
    public static final String ACHIEVEMENT_ELECTRONIC_HARD = "electronic_hard";
    public static final String ACHIEVEMENT_METAL_EASY = "metal_easy";
    public static final String ACHIEVEMENT_METAL_MEDIUM = "metal_medium";
    public static final String ACHIEVEMENT_METAL_HARD = "metal_hard";
    public static final String ACHIEVEMENT_HIPHOP_EASY = "hiphop_easy";
    public static final String ACHIEVEMENT_HIPHOP_MEDIUM = "hiphop_medium";
    public static final String ACHIEVEMENT_HIPHOP_HARD = "hiphop_hard";
    
    // Коллекция для достижений
    private static final String ACHIEVEMENTS_COLLECTION = "achievements";
    
    // Интерфейс для обратных вызовов
    public interface AchievementCallback {
        void onSuccess(String achievementType);
        void onError(String errorMessage);
    }
    
    private AchievementManager() {
        firestore = FirebaseFirestore.getInstance();
    }
    
    public static synchronized AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }
    
    /**
     * Проверяет и обновляет достижения на основе действий пользователя
     */
    public void checkAndUpdateAchievements(User user, AchievementCallback callback) {
        // Проверка достижений на основе количества пройденных уроков
        if (user.getLessonsCompleted() == 1) {
            unlockAchievement(user.getUid(), ACHIEVEMENT_FIRST_LESSON, callback);
        } else if (user.getLessonsCompleted() == 5) {
            unlockAchievement(user.getUid(), ACHIEVEMENT_FIVE_LESSONS, callback);
        } else if (user.getLessonsCompleted() == 10) {
            unlockAchievement(user.getUid(), ACHIEVEMENT_TEN_LESSONS, callback);
        }
        
        // Проверка достижений на основе количества пройденных тестов
        if (user.getQuizzesCompleted() == 1) {
            unlockAchievement(user.getUid(), ACHIEVEMENT_FIRST_QUIZ, callback);
        }
        
        // Проверка достижений на основе набранных очков
        if (user.getTotalScore() >= 1000) {
            unlockAchievement(user.getUid(), ACHIEVEMENT_HIGH_SCORE, callback);
        }
    }
    
    /**
     * Регистрирует достижение для пользователя
     */
    public void unlockAchievement(String userId, String achievementType, AchievementCallback callback) {
        if (userId == null || achievementType == null) {
            callback.onError("Неверные параметры");
            return;
        }
        
        // Для отладки
        android.util.Log.d("ACHIEVEMENT_UNLOCK", "Попытка разблокировать: " + achievementType + 
                " для пользователя " + userId);
        
        // Проверяем, не получено ли уже достижение
        firestore.collection(ACHIEVEMENTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", achievementType)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Достижение еще не получено, добавляем его
                        Map<String, Object> achievement = new HashMap<>();
                        achievement.put("userId", userId);
                        achievement.put("type", achievementType);
                        achievement.put("date", System.currentTimeMillis());
                        
                        // Для отладки
                        android.util.Log.d("ACHIEVEMENT_UNLOCK", "Сохраняем новое достижение: " + achievementType);
                        
                        firestore.collection(ACHIEVEMENTS_COLLECTION)
                                .add(achievement)
                                .addOnSuccessListener(documentReference -> {
                                    android.util.Log.d("ACHIEVEMENT_UNLOCK", "Успешно сохранено: " + 
                                            documentReference.getId());
                                    callback.onSuccess(achievementType);
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("ACHIEVEMENT_UNLOCK", "Ошибка сохранения: " + e.getMessage());
                                    callback.onError("Ошибка при сохранении достижения: " + e.getMessage());
                                });
                    } else {
                        // Достижение уже получено, просто возвращаем успех
                        android.util.Log.d("ACHIEVEMENT_UNLOCK", "Достижение уже разблокировано ранее");
                        callback.onSuccess(achievementType);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("ACHIEVEMENT_UNLOCK", "Ошибка проверки: " + e.getMessage());
                    callback.onError("Ошибка при проверке достижений: " + e.getMessage());
                });
    }
    
    /**
     * Получает все достижения пользователя
     */
    public void getUserAchievements(AchievementsListCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        String userId = currentUser.getUid();
        
        // Для отладки
        android.util.Log.d("ACHIEVEMENTS_LOAD", "Загрузка достижений для пользователя: " + userId);
        
        firestore.collection(ACHIEVEMENTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Long> achievements = new HashMap<>();
                    
                    // Для отладки
                    android.util.Log.d("ACHIEVEMENTS_LOAD", "Найдено документов: " + 
                            queryDocumentSnapshots.size());
                    
                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String type = document.getString("type");
                        Long date = document.getLong("date");
                        
                        // Для отладки
                        android.util.Log.d("ACHIEVEMENTS_LOAD", "Документ: " + document.getId() + 
                                ", тип: " + type + ", дата: " + date);
                        
                        if (type != null && date != null) {
                            achievements.put(type, date);
                        }
                    }
                    
                    // Для проверки, смотрим, есть ли достижение за металл
                    if (achievements.containsKey(ACHIEVEMENT_METAL_EASY)) {
                        android.util.Log.d("ACHIEVEMENTS_LOAD", "Найдено достижение за металл!");
                    } else {
                        android.util.Log.d("ACHIEVEMENTS_LOAD", "Достижения за металл НЕ найдено");
                    }
                    
                    callback.onAchievementsLoaded(achievements);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("ACHIEVEMENTS_LOAD", "Ошибка: " + e.getMessage());
                    callback.onError("Ошибка при загрузке достижений: " + e.getMessage());
                });
    }
    
    /**
     * Загружает достижения пользователя по указанному ID
     * @param userId ID пользователя
     * @param callback Callback для получения списка достижений
     */
    public void loadAchievements(String userId, AchievementsListCallback callback) {
        if (userId == null) {
            callback.onError("Не указан ID пользователя");
            return;
        }
        
        // Для отладки
        android.util.Log.d("ACHIEVEMENTS_LOAD", "Загрузка достижений для пользователя: " + userId);
        
        firestore.collection(ACHIEVEMENTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Long> achievements = new HashMap<>();
                    
                    // Для отладки
                    android.util.Log.d("ACHIEVEMENTS_LOAD", "Найдено документов: " + 
                            queryDocumentSnapshots.size());
                    
                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String type = document.getString("type");
                        Long date = document.getLong("date");
                        
                        // Для отладки
                        android.util.Log.d("ACHIEVEMENTS_LOAD", "Документ: " + document.getId() + 
                                ", тип: " + type + ", дата: " + date);
                        
                        if (type != null && date != null) {
                            achievements.put(type, date);
                        }
                    }
                    
                    callback.onAchievementsLoaded(achievements);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("ACHIEVEMENTS_LOAD", "Ошибка: " + e.getMessage());
                    callback.onError("Ошибка при загрузке достижений: " + e.getMessage());
                });
    }
    
    /**
     * Интерфейс для обратных вызовов при получении списка достижений
     */
    public interface AchievementsListCallback {
        void onAchievementsLoaded(Map<String, Long> achievements);
        void onError(String errorMessage);
    }
    
    /**
     * Возвращает название достижения на русском языке
     */
    public static String getAchievementName(String achievementType) {
        switch (achievementType) {
            case ACHIEVEMENT_FIRST_LESSON: return "Первый урок";
            case ACHIEVEMENT_FIVE_LESSONS: return "5 уроков";
            case ACHIEVEMENT_TEN_LESSONS: return "10 уроков";
            case ACHIEVEMENT_FIRST_QUIZ: return "Первый тест";
            case ACHIEVEMENT_HIGH_SCORE: return "Высокий результат";
            case ACHIEVEMENT_PRACTICE_MASTER: return "Мастер практики";
            case ACHIEVEMENT_CLASSICAL_EASY: return "Классика: Легко";
            case ACHIEVEMENT_CLASSICAL_MEDIUM: return "Классика: Средне";
            case ACHIEVEMENT_CLASSICAL_HARD: return "Классика: Сложно";
            case ACHIEVEMENT_ROCK_EASY: return "Рок: Легко";
            case ACHIEVEMENT_ROCK_MEDIUM: return "Рок: Средне";
            case ACHIEVEMENT_ROCK_HARD: return "Рок: Сложно";
            case ACHIEVEMENT_JAZZ_EASY: return "Джаз: Легко";
            case ACHIEVEMENT_JAZZ_MEDIUM: return "Джаз: Средне";
            case ACHIEVEMENT_JAZZ_HARD: return "Джаз: Сложно";
            case ACHIEVEMENT_POP_EASY: return "Поп: Легко";
            case ACHIEVEMENT_POP_MEDIUM: return "Поп: Средне";
            case ACHIEVEMENT_POP_HARD: return "Поп: Сложно";
            case ACHIEVEMENT_FOLK_EASY: return "Народная: Легко";
            case ACHIEVEMENT_FOLK_MEDIUM: return "Народная: Средне";
            case ACHIEVEMENT_FOLK_HARD: return "Народная: Сложно";
            case ACHIEVEMENT_ELECTRONIC_EASY: return "Электронная: Легко";
            case ACHIEVEMENT_ELECTRONIC_MEDIUM: return "Электронная: Средне";
            case ACHIEVEMENT_ELECTRONIC_HARD: return "Электронная: Сложно";
            case ACHIEVEMENT_METAL_EASY: return "Метал: Легко";
            case ACHIEVEMENT_METAL_MEDIUM: return "Метал: Средне";
            case ACHIEVEMENT_METAL_HARD: return "Метал: Сложно";
            case ACHIEVEMENT_HIPHOP_EASY: return "Хип-хоп: Легко";
            case ACHIEVEMENT_HIPHOP_MEDIUM: return "Хип-хоп: Средне";
            case ACHIEVEMENT_HIPHOP_HARD: return "Хип-хоп: Сложно";
            default: return "Достижение";
        }
    }
    
    /**
     * Возвращает описание достижения
     */
    public static String getAchievementDescription(String achievementType) {
        switch (achievementType) {
            case ACHIEVEMENT_FIRST_LESSON: return "Пройден первый урок";
            case ACHIEVEMENT_FIVE_LESSONS: return "Пройдено 5 уроков";
            case ACHIEVEMENT_TEN_LESSONS: return "Пройдено 10 уроков";
            case ACHIEVEMENT_FIRST_QUIZ: return "Выполнен первый тест";
            case ACHIEVEMENT_HIGH_SCORE: return "Набрано 1000 очков";
            case ACHIEVEMENT_PRACTICE_MASTER: return "Использованы все инструменты практики";
            case ACHIEVEMENT_CLASSICAL_EASY: return "Пройден тест по классике (легко)";
            case ACHIEVEMENT_CLASSICAL_MEDIUM: return "Пройден тест по классике (средне)";
            case ACHIEVEMENT_CLASSICAL_HARD: return "Пройден тест по классике (сложно)";
            case ACHIEVEMENT_ROCK_EASY: return "Пройден тест по року (легко)";
            case ACHIEVEMENT_ROCK_MEDIUM: return "Пройден тест по року (средне)";
            case ACHIEVEMENT_ROCK_HARD: return "Пройден тест по року (сложно)";
            case ACHIEVEMENT_JAZZ_EASY: return "Пройден тест по джазу (легко)";
            case ACHIEVEMENT_JAZZ_MEDIUM: return "Пройден тест по джазу (средне)";
            case ACHIEVEMENT_JAZZ_HARD: return "Пройден тест по джазу (сложно)";
            case ACHIEVEMENT_POP_EASY: return "Пройден тест по поп-музыке (легко)";
            case ACHIEVEMENT_POP_MEDIUM: return "Пройден тест по поп-музыке (средне)";
            case ACHIEVEMENT_POP_HARD: return "Пройден тест по поп-музыке (сложно)";
            case ACHIEVEMENT_FOLK_EASY: return "Пройден тест по народной музыке (легко)";
            case ACHIEVEMENT_FOLK_MEDIUM: return "Пройден тест по народной музыке (средне)";
            case ACHIEVEMENT_FOLK_HARD: return "Пройден тест по народной музыке (сложно)";
            case ACHIEVEMENT_ELECTRONIC_EASY: return "Пройден тест по электронной музыке (легко)";
            case ACHIEVEMENT_ELECTRONIC_MEDIUM: return "Пройден тест по электронной музыке (средне)";
            case ACHIEVEMENT_ELECTRONIC_HARD: return "Пройден тест по электронной музыке (сложно)";
            case ACHIEVEMENT_METAL_EASY: return "Пройден тест по металу (легко)";
            case ACHIEVEMENT_METAL_MEDIUM: return "Пройден тест по металу (средне)";
            case ACHIEVEMENT_METAL_HARD: return "Пройден тест по металу (сложно)";
            case ACHIEVEMENT_HIPHOP_EASY: return "Пройден тест по хип-хопу (легко)";
            case ACHIEVEMENT_HIPHOP_MEDIUM: return "Пройден тест по хип-хопу (средне)";
            case ACHIEVEMENT_HIPHOP_HARD: return "Пройден тест по хип-хопу (сложно)";
            default: return "Достижение";
        }
    }
    
    /**
     * Метод для выдачи достижения по жанру и сложности
     */
    public void checkAndUnlockGenreAchievement(String userId, String genre, String difficulty, AchievementCallback callback) {
        if (userId == null || genre == null || difficulty == null) return;
        String achievementType = null;
        String genreKey = getGenreKey(genre);
        switch (genreKey.toLowerCase()) {
            case "classical":
                if (difficulty.equals("Легкий")) achievementType = ACHIEVEMENT_CLASSICAL_EASY;
                else if (difficulty.equals("Средний")) achievementType = ACHIEVEMENT_CLASSICAL_MEDIUM;
                else if (difficulty.equals("Сложный")) achievementType = ACHIEVEMENT_CLASSICAL_HARD;
                break;
            case "rock":
                if (difficulty.equals("Легкий")) achievementType = ACHIEVEMENT_ROCK_EASY;
                else if (difficulty.equals("Средний")) achievementType = ACHIEVEMENT_ROCK_MEDIUM;
                else if (difficulty.equals("Сложный")) achievementType = ACHIEVEMENT_ROCK_HARD;
                break;
            case "jazz":
                if (difficulty.equals("Легкий")) achievementType = ACHIEVEMENT_JAZZ_EASY;
                else if (difficulty.equals("Средний")) achievementType = ACHIEVEMENT_JAZZ_MEDIUM;
                else if (difficulty.equals("Сложный")) achievementType = ACHIEVEMENT_JAZZ_HARD;
                break;
            case "pop":
                if (difficulty.equals("Легкий")) achievementType = ACHIEVEMENT_POP_EASY;
                else if (difficulty.equals("Средний")) achievementType = ACHIEVEMENT_POP_MEDIUM;
                else if (difficulty.equals("Сложный")) achievementType = ACHIEVEMENT_POP_HARD;
                break;
            case "folk":
                if (difficulty.equals("Легкий")) achievementType = ACHIEVEMENT_FOLK_EASY;
                else if (difficulty.equals("Средний")) achievementType = ACHIEVEMENT_FOLK_MEDIUM;
                else if (difficulty.equals("Сложный")) achievementType = ACHIEVEMENT_FOLK_HARD;
                break;
            case "electronic":
                if (difficulty.equals("Легкий")) achievementType = ACHIEVEMENT_ELECTRONIC_EASY;
                else if (difficulty.equals("Средний")) achievementType = ACHIEVEMENT_ELECTRONIC_MEDIUM;
                else if (difficulty.equals("Сложный")) achievementType = ACHIEVEMENT_ELECTRONIC_HARD;
                break;
            case "metal":
                if (difficulty.equals("Легкий")) achievementType = ACHIEVEMENT_METAL_EASY;
                else if (difficulty.equals("Средний")) achievementType = ACHIEVEMENT_METAL_MEDIUM;
                else if (difficulty.equals("Сложный")) achievementType = ACHIEVEMENT_METAL_HARD;
                break;
            case "hip hop":
            case "hiphop":
                if (difficulty.equals("Легкий")) achievementType = ACHIEVEMENT_HIPHOP_EASY;
                else if (difficulty.equals("Средний")) achievementType = ACHIEVEMENT_HIPHOP_MEDIUM;
                else if (difficulty.equals("Сложный")) achievementType = ACHIEVEMENT_HIPHOP_HARD;
                break;
        }
        if (achievementType == null) return;
        unlockAchievement(userId, achievementType, callback);
    }
    
    /**
     * Проверяет, имеет ли пользователь конкретное достижение
     * 
     * @param userId ID пользователя
     * @param achievementType Тип достижения
     * @param callback Обратный вызов с результатом (true = достижение уже получено)
     */
    public void hasAchievement(String userId, String achievementType, HasAchievementCallback callback) {
        firestore.collection(ACHIEVEMENTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", achievementType)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean hasAchievement = !queryDocumentSnapshots.isEmpty();
                    callback.onResult(hasAchievement);
                })
                .addOnFailureListener(e -> callback.onError("Ошибка при проверке достижения: " + e.getMessage()));
    }
    
    /**
     * Интерфейс для обратного вызова проверки наличия достижения
     */
    public interface HasAchievementCallback {
        void onResult(boolean hasAchievement);
        void onError(String errorMessage);
    }
    
    // Сопоставление id жанра и ключа для достижений
    public static String getGenreKey(String genreId) {
        if (genreId == null) return null;
        switch (genreId.toLowerCase()) {
            case "rock": return "Rock";
            case "pop": return "Pop";
            case "hiphop": return "Hip Hop";
            case "jazz": return "Jazz";
            case "classical": return "Classical";
            case "metal": return "Metal";
            default: return genreId;
        }
    }

    /**
     * Возвращает тип достижения в зависимости от жанра и уровня сложности
     * @param genre Жанр музыки (id или ключ)
     * @param difficulty Уровень сложности ("Легкий", "Средний", "Сложный")
     * @return Тип достижения или null, если достижение не найдено
     */
    public static String getAchievementType(String genre, String difficulty) {
        if (genre == null || difficulty == null) return null;
        String normalizedGenre = getGenreKey(genre).toLowerCase();
        String normalizedDifficulty = difficulty.toLowerCase();
        
        // Проверяем жанр и выбираем соответствующее достижение
        if (normalizedGenre.equals("классическая") || normalizedGenre.equals("классика") || 
            normalizedGenre.equals("classical")) {
            if (normalizedDifficulty.equals("легкий")) {
                return ACHIEVEMENT_CLASSICAL_EASY;
            } else if (normalizedDifficulty.equals("средний")) {
                return ACHIEVEMENT_CLASSICAL_MEDIUM;
            } else if (normalizedDifficulty.equals("сложный")) {
                return ACHIEVEMENT_CLASSICAL_HARD;
            }
        } else if (normalizedGenre.equals("рок") || normalizedGenre.equals("rock")) {
            if (normalizedDifficulty.equals("легкий")) {
                return ACHIEVEMENT_ROCK_EASY;
            } else if (normalizedDifficulty.equals("средний")) {
                return ACHIEVEMENT_ROCK_MEDIUM;
            } else if (normalizedDifficulty.equals("сложный")) {
                return ACHIEVEMENT_ROCK_HARD;
            }
        } else if (normalizedGenre.equals("джаз") || normalizedGenre.equals("jazz")) {
            if (normalizedDifficulty.equals("легкий")) {
                return ACHIEVEMENT_JAZZ_EASY;
            } else if (normalizedDifficulty.equals("средний")) {
                return ACHIEVEMENT_JAZZ_MEDIUM;
            } else if (normalizedDifficulty.equals("сложный")) {
                return ACHIEVEMENT_JAZZ_HARD;
            }
        } else if (normalizedGenre.equals("поп") || normalizedGenre.equals("pop")) {
            if (normalizedDifficulty.equals("легкий")) {
                return ACHIEVEMENT_POP_EASY;
            } else if (normalizedDifficulty.equals("средний")) {
                return ACHIEVEMENT_POP_MEDIUM;
            } else if (normalizedDifficulty.equals("сложный")) {
                return ACHIEVEMENT_POP_HARD;
            }
        } else if (normalizedGenre.equals("народная") || normalizedGenre.equals("folk")) {
            if (normalizedDifficulty.equals("легкий")) {
                return ACHIEVEMENT_FOLK_EASY;
            } else if (normalizedDifficulty.equals("средний")) {
                return ACHIEVEMENT_FOLK_MEDIUM;
            } else if (normalizedDifficulty.equals("сложный")) {
                return ACHIEVEMENT_FOLK_HARD;
            }
        } else if (normalizedGenre.equals("электронная") || normalizedGenre.equals("electronic")) {
            if (normalizedDifficulty.equals("легкий")) {
                return ACHIEVEMENT_ELECTRONIC_EASY;
            } else if (normalizedDifficulty.equals("средний")) {
                return ACHIEVEMENT_ELECTRONIC_MEDIUM;
            } else if (normalizedDifficulty.equals("сложный")) {
                return ACHIEVEMENT_ELECTRONIC_HARD;
            }
        } else if (normalizedGenre.equals("металл") || normalizedGenre.equals("метал") || 
                  normalizedGenre.equals("metal")) {
            if (normalizedDifficulty.equals("легкий")) {
                return ACHIEVEMENT_METAL_EASY;
            } else if (normalizedDifficulty.equals("средний")) {
                return ACHIEVEMENT_METAL_MEDIUM;
            } else if (normalizedDifficulty.equals("сложный")) {
                return ACHIEVEMENT_METAL_HARD;
            }
        } else if (normalizedGenre.equals("хип-хоп") || normalizedGenre.equals("hip-hop") || 
                  normalizedGenre.equals("hiphop") || normalizedGenre.equals("hip hop")) {
            if (normalizedDifficulty.equals("легкий")) {
                return ACHIEVEMENT_HIPHOP_EASY;
            } else if (normalizedDifficulty.equals("средний")) {
                return ACHIEVEMENT_HIPHOP_MEDIUM;
            } else if (normalizedDifficulty.equals("сложный")) {
                return ACHIEVEMENT_HIPHOP_HARD;
            }
        }
        
        return null;
    }
} 