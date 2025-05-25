package com.example.music_land;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.music_land.firebase.AchievementManager;
import com.example.music_land.firebase.FirebaseAuthManager;
import com.example.music_land.firebase.FirestoreManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QuizResultsActivity extends AppCompatActivity {
    private TextView finalScoreTextView;
    private TextView percentageTextView;
    private TextView difficultyTextView;
    private LinearLayout wrongAnswersContainer;
    private Button backToMainButton;
    private FirestoreManager firestoreManager;
    private AchievementManager achievementManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        // Инициализируем UI элементы
        finalScoreTextView = findViewById(R.id.finalScoreTextView);
        percentageTextView = findViewById(R.id.percentageTextView);
        difficultyTextView = findViewById(R.id.difficultyTextView);
        wrongAnswersContainer = findViewById(R.id.wrongAnswersContainer);
        backToMainButton = findViewById(R.id.backToMainButton);
        
        firestoreManager = FirestoreManager.getInstance();
        achievementManager = AchievementManager.getInstance();

        // Получаем данные из Intent
        int finalScore = getIntent().getIntExtra("finalScore", 0);
        ArrayList<String> wrongAnswers = getIntent().getStringArrayListExtra("wrongAnswers");
        String genre = getIntent().getStringExtra("genre");
        String difficulty = getIntent().getStringExtra("difficulty");

        // По умолчанию скрываем карточку достижения
        CardView achievementCard = findViewById(R.id.achievementCard);
        if (achievementCard != null) {
            achievementCard.setVisibility(View.GONE);
        }

        // Отображаем финальный счет и сложность
        finalScoreTextView.setText("Ваш результат: " + finalScore + " баллов");
        
        int totalQuestions = wrongAnswers.size() + (finalScore / 10);
        int percentage = (int) ((float) finalScore / (totalQuestions * 10) * 100);
        
        percentageTextView.setText("Правильных ответов: " + percentage + "%");
        
        if (difficulty != null && !difficulty.isEmpty()) {
            difficultyTextView.setText("Жанр: " + genre + " | Сложность: " + difficulty);
            difficultyTextView.setVisibility(View.VISIBLE);
        } else {
            difficultyTextView.setVisibility(View.GONE);
        }
        
        android.util.Log.d("QUIZ_RESULTS", "Жанр: " + genre + ", Результат: " + percentage + "%");
        
        // Скрываем или показываем секцию неправильных ответов в зависимости от результата
        TextView wrongAnswersTitle = findViewById(R.id.wrongAnswersTitle);
        if (percentage == 100) {
            // Если результат 100%, скрываем секцию неправильных ответов
            if (wrongAnswersTitle != null) {
                wrongAnswersTitle.setVisibility(View.GONE);
            }
            wrongAnswersContainer.setVisibility(View.GONE);
        } else {
            // В противном случае показываем
            if (wrongAnswersTitle != null) {
                wrongAnswersTitle.setVisibility(View.VISIBLE);
            }
            wrongAnswersContainer.setVisibility(View.VISIBLE);
        }
        
        // После подсчёта результата теста:
        String userId = com.example.music_land.firebase.FirebaseAuthManager.getInstance().getCurrentUserId();
        if (userId != null && genre != null && difficulty != null) {
            // Проверяем достижение только за прохождение теста по жанру и сложности
            if (percentage == 100) {
                achievementManager.checkAndUnlockGenreAchievement(userId, genre, difficulty, 
                    new AchievementManager.AchievementCallback() {
                        @Override
                        public void onSuccess(String achievementType) {
                            showGenreAchievement(
                                genre + " - " + difficulty, 
                                genre,
                                false,
                                percentage
                            );
                            
                            // После успешного получения достижения по жанру проверяем на получение всех достижений
                            checkAllGenresCompletion(userId);
                        }
                        @Override
                        public void onError(String errorMessage) {
                            android.util.Log.e("ACHIEVEMENT", "Ошибка при выдаче достижения за прохождение теста: " + errorMessage);
                        }
                    });
                }
        }

        // Сохраняем результаты в Firebase с учетом сложности
        if (genre != null && !genre.isEmpty()) {
            saveScoreToFirebase(genre, difficulty, finalScore);
            saveProgressToFirestore(genre, difficulty, finalScore);
        }

        // Отображаем неправильные ответы
        for (String wrongAnswer : wrongAnswers) {
            // Создаем карточку для каждого неправильного ответа
            CardView cardView = new CardView(this);
            cardView.setCardElevation(4);
            cardView.setRadius(8);
            cardView.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 16);
            cardView.setLayoutParams(cardParams);

            // Создаем контейнер для текста внутри карточки
            LinearLayout cardContent = new LinearLayout(this);
            cardContent.setOrientation(LinearLayout.VERTICAL);
            cardContent.setPadding(16, 16, 16, 16);

            // Разбиваем строку на части
            String[] parts = wrongAnswer.split("\n");
            
            // Добавляем вопрос
            TextView questionView = new TextView(this);
            questionView.setText(parts[0]);
            questionView.setTextSize(16);
            questionView.setTextColor(Color.WHITE);
            questionView.setPadding(0, 0, 0, 8);
            cardContent.addView(questionView);

            // Добавляем ответ пользователя
            TextView userAnswerView = new TextView(this);
            userAnswerView.setText(parts[1]);
            userAnswerView.setTextSize(14);
            userAnswerView.setTextColor(Color.parseColor("#FF5252")); // Красный
            userAnswerView.setPadding(0, 0, 0, 8);
            cardContent.addView(userAnswerView);

            // Добавляем правильный ответ
            TextView correctAnswerView = new TextView(this);
            correctAnswerView.setText(parts[2]);
            correctAnswerView.setTextSize(14);
            correctAnswerView.setTextColor(Color.parseColor("#4CAF50")); // Зеленый
            cardContent.addView(correctAnswerView);

            cardView.addView(cardContent);
            wrongAnswersContainer.addView(cardView);
        }

        // Обработчик кнопки возврата к выбору сложности
        Button retryQuizButton = findViewById(R.id.retryQuizButton);
        if (retryQuizButton != null) {
            retryQuizButton.setOnClickListener(v -> {
                Intent intent = new Intent(QuizResultsActivity.this, QuizActivity.class);
                intent.putExtra("genre", genre);
                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
                finish();
            });
        }
        backToMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResultsActivity.this, DifficultySelectionActivity.class);
            intent.putExtra("genre", genre);
            intent.putExtra("genre_name", getIntent().getStringExtra("genre_name"));
            startActivity(intent);
            finish();
        });
    }
    
    private void saveScoreToFirebase(String genre, String difficulty, int score) {
        // Добавляем множитель в зависимости от сложности
        float difficultyMultiplier = 1.0f;
        if (difficulty != null) {
            if (difficulty.equals("Средний")) {
                difficultyMultiplier = 1.5f;
            } else if (difficulty.equals("Сложный")) {
                difficultyMultiplier = 2.0f;
            }
        }
        
        // Применяем множитель к счету
        int adjustedScore = (int) (score * difficultyMultiplier);
        
        firestoreManager.addUserScore(genre, adjustedScore, new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(QuizResultsActivity.this, 
                    "Результаты сохранены в лидерборде", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(QuizResultsActivity.this, 
                    "Ошибка при сохранении результатов: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProgressToFirestore(String genre, String difficulty, int score) {
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        java.util.Map<String, Object> progress = new java.util.HashMap<>();
        progress.put("genre", genre);
        progress.put("difficulty", difficulty);
        progress.put("score", score);
        progress.put("timestamp", System.currentTimeMillis());

        db.collection("user_progress")
          .document(userId + "_" + genre + "_" + difficulty)
          .set(progress);
    }

    /**
     * Показывает карточку достижения за прохождение теста по жанру
     */
    private void showGenreAchievement(String achievementName, String genre, boolean alreadyHasAchievement, int percentage) {
        // Находим карточку достижения
        androidx.cardview.widget.CardView achievementCard = findViewById(R.id.achievementCard);
        if (achievementCard == null) {
            android.util.Log.e("ACHIEVEMENT", "Не найдена карточка достижения в разметке!");
            return;
        }
        
        android.util.Log.d("ACHIEVEMENT", "Настраиваем карточку достижения");
        
        // Настраиваем текст
        TextView titleTextView = findViewById(R.id.achievementTitleTextView);
        TextView nameTextView = findViewById(R.id.achievementNameTextView);
        TextView descTextView = findViewById(R.id.achievementDescTextView);
        
        // Настраиваем заголовок в зависимости от статуса достижения
        if (titleTextView != null) {
            if (alreadyHasAchievement) {
                titleTextView.setText("Достижение получено ранее");
                titleTextView.setTextColor(android.graphics.Color.parseColor("#BCAAA4")); // Бледно-коричневый
            } else {
                titleTextView.setText("Новое достижение!");
                titleTextView.setTextColor(android.graphics.Color.parseColor("#FFD700")); // Золотой цвет
            }
        }
        
        if (nameTextView != null) {
            nameTextView.setText(achievementName);
            android.util.Log.d("ACHIEVEMENT", "Установлено имя достижения");
        }
        
        if (descTextView != null) {
            String difficulty = achievementName.contains(" - ") ? achievementName.split(" - ")[1] : null;
            descTextView.setText(getAchievementDescriptionForGenre(genre, percentage, difficulty));
            android.util.Log.d("ACHIEVEMENT", "Установлено описание достижения");
        }
        
        // Настраиваем иконку
        ImageView iconView = findViewById(R.id.achievementIconView);
        if (iconView != null) {
            iconView.setImageResource(android.R.drawable.btn_star_big_on);
            if (alreadyHasAchievement) {
                iconView.setColorFilter(android.graphics.Color.parseColor("#BCAAA4")); // Бледно-коричневый
            } else {
                iconView.setColorFilter(android.graphics.Color.parseColor("#FFD700")); // Золотой цвет
            }
            android.util.Log.d("ACHIEVEMENT", "Установлена иконка достижения");
        } else {
            android.util.Log.e("ACHIEVEMENT", "Не найден ImageView для иконки достижения!");
        }
        
        // Настраиваем кнопку перехода в достижения
        Button viewAchievementsButton = findViewById(R.id.viewAchievementsButton);
        if (viewAchievementsButton != null) {
            viewAchievementsButton.setVisibility(View.VISIBLE);
            viewAchievementsButton.setOnClickListener(v -> {
                android.util.Log.d("ACHIEVEMENT", "Нажата кнопка перехода к списку достижений");
                // Открываем экран достижений
                Intent intent = new Intent(this, AchievementsActivity.class);
                intent.putExtra("genre", genre);
                startActivity(intent);
            });
            android.util.Log.d("ACHIEVEMENT", "Настроена кнопка перехода к достижениям");
        } else {
            android.util.Log.e("ACHIEVEMENT", "Не найдена кнопка перехода к достижениям!");
        }
        
        // Показываем карточку с анимацией
        achievementCard.setVisibility(View.VISIBLE);
        achievementCard.setAlpha(0f);
        achievementCard.animate()
            .alpha(1f)
            .setDuration(500)
            .start();
        
        android.util.Log.d("ACHIEVEMENT", "Карточка достижения показана с анимацией");
    }

    /**
     * Получает описание достижения для указанного жанра
     */
    private String getAchievementDescriptionForGenre(String genre, int percentage, String difficulty) {
        if (genre == null) return "";
        
        if (genre.equals("Все жанры")) {
            return "Пройдены все тесты по всем жанрам на всех уровнях сложности! Вы настоящий эксперт!";
        }
        
        if (difficulty != null) {
            return "Идеальное прохождение теста по жанру \"" + genre + "\" на уровне сложности \"" + difficulty + "\"!";
        }
        
        return "Идеальное прохождение теста по жанру \"" + genre + "\"!";
    }

    /**
     * Проверяет, получил ли пользователь все достижения по всем жанрам
     */
    private void checkAllGenresCompletion(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("achievements")
          .whereEqualTo("userId", userId)
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              // Создаем множество для хранения уникальных жанров, по которым есть все достижения
              Map<String, Set<String>> completedGenres = new HashMap<>();
              
              // Проходим по всем достижениям пользователя
              for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots) {
                  String genre = document.getString("genre");
                  String difficulty = document.getString("difficulty");
                  
                  if (genre != null && difficulty != null) {
                      // Добавляем сложность в множество для данного жанра
                      completedGenres.computeIfAbsent(genre, k -> new HashSet<>()).add(difficulty);
                  }
              }
              
              // Проверяем, есть ли жанры с полным набором сложностей (легкий, средний, сложный)
              boolean allGenresCompleted = true;
              for (Set<String> difficulties : completedGenres.values()) {
                  if (difficulties.size() < 3 || 
                      !difficulties.contains("Легкий") || 
                      !difficulties.contains("Средний") || 
                      !difficulties.contains("Сложный")) {
                      allGenresCompleted = false;
                      break;
                  }
              }
              
              // Если все жанры пройдены на всех сложностях
              if (allGenresCompleted && !completedGenres.isEmpty()) {
                  achievementManager.unlockAchievement(userId, "ALL_GENRES_COMPLETED",
                      new AchievementManager.AchievementCallback() {
                          @Override
                          public void onSuccess(String achievementType) {
                              showGenreAchievement(
                                  "Мастер музыкальных жанров",
                                  "Все жанры",
                                  false,
                                  100
                              );
                          }
                          @Override
                          public void onError(String errorMessage) {
                              android.util.Log.e("ACHIEVEMENT", 
                                  "Ошибка при выдаче достижения за прохождение всех жанров: " + errorMessage);
                          }
                      });
              }
          })
          .addOnFailureListener(e -> 
              android.util.Log.e("ACHIEVEMENT", "Ошибка при проверке достижений: " + e.getMessage())
          );
    }
} 