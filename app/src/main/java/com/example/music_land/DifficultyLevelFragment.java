package com.example.music_land;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DifficultyLevelFragment extends Fragment {

    private static final String ARG_GENRE = "genre";
    private static final String ARG_GENRE_NAME = "genre_name";
    private static final String ARG_DIFFICULTY = "difficulty";

    private String genre;
    private String genreName;
    private String difficulty;

    public static DifficultyLevelFragment newInstance(String genre, String genreName, String difficulty) {
        DifficultyLevelFragment fragment = new DifficultyLevelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GENRE, genre);
        args.putString(ARG_GENRE_NAME, genreName);
        args.putString(ARG_DIFFICULTY, difficulty);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genre = getArguments().getString(ARG_GENRE);
            genreName = getArguments().getString(ARG_GENRE_NAME);
            difficulty = getArguments().getString(ARG_DIFFICULTY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_difficulty_level, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Настраиваем описание сложности
        TextView descriptionTextView = view.findViewById(R.id.difficultyDescriptionTextView);
        TextView questionsCountTextView = view.findViewById(R.id.questionsCountTextView);
        TextView completionStatusTextView = view.findViewById(R.id.completionStatusTextView);
        TextView lastScoreTextView = view.findViewById(R.id.lastScoreTextView);
        TextView achievementStatusTextView = view.findViewById(R.id.achievementStatusTextView);
        Button startQuizButton = view.findViewById(R.id.startQuizButton);

        // Устанавливаем текст описания в зависимости от сложности
        if (difficulty.equals("Легкий")) {
            descriptionTextView.setText("В этом уровне собраны простые вопросы для начинающих любителей " + genreName);
            questionsCountTextView.setText("Всего вопросов: 5");
        } else if (difficulty.equals("Средний")) {
            descriptionTextView.setText("Вопросы среднего уровня сложности для знатоков " + genreName);
            questionsCountTextView.setText("Всего вопросов: 10");
        } else {
            descriptionTextView.setText("Самые сложные вопросы для настоящих экспертов " + genreName);
            questionsCountTextView.setText("Всего вопросов: 15");
        }

        // Получаем данные о прохождении (в реальном приложении здесь будет запрос к базе данных)
        // boolean completed = false;
        // int lastScore = 0;
        // ...
        // Устанавливаем статус прохождения
        // if (completed) { ... } else { ... }
        loadProgressAndUpdateUI(completionStatusTextView, lastScoreTextView);
        
        // Проверяем наличие достижения для данного уровня сложности
        if (achievementStatusTextView != null) {
            // Получаем тип достижения для данного жанра и уровня сложности
            String achievementType = com.example.music_land.firebase.AchievementManager.getAchievementType(genre, difficulty);
            
            if (achievementType != null) {
                // Получаем текущего пользователя
                com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                
                if (currentUser != null) {
                    // Проверяем, есть ли у пользователя это достижение
                    com.example.music_land.firebase.AchievementManager achievementManager = 
                        com.example.music_land.firebase.AchievementManager.getInstance();
                    
                    achievementManager.hasAchievement(currentUser.getUid(), achievementType, 
                        new com.example.music_land.firebase.AchievementManager.HasAchievementCallback() {
                            @Override
                            public void onResult(boolean hasAchievement) {
                                // Выполняется в другом потоке, поэтому обновляем UI через runOnUiThread
                                requireActivity().runOnUiThread(() -> {
                                    if (hasAchievement) {
                                        String achievementName = com.example.music_land.firebase.AchievementManager
                                            .getAchievementName(achievementType);
                                        achievementStatusTextView.setText("Достижение: " + achievementName);
                                        achievementStatusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                    } else {
                                        achievementStatusTextView.setText("Достижение не получено");
                                        achievementStatusTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                                    }
                                    achievementStatusTextView.setVisibility(View.VISIBLE);
                                });
                            }
                            
                            @Override
                            public void onError(String errorMessage) {
                                android.util.Log.e("ACHIEVEMENT", "Ошибка при проверке достижения: " + errorMessage);
                                // Скрываем статус достижения в случае ошибки
                                requireActivity().runOnUiThread(() -> {
                                    achievementStatusTextView.setVisibility(View.GONE);
                                });
                            }
                        });
                } else {
                    // Пользователь не авторизован
                    achievementStatusTextView.setText("Авторизуйтесь для отслеживания достижений");
                    achievementStatusTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    achievementStatusTextView.setVisibility(View.VISIBLE);
                }
            } else {
                // Нет достижения для данного уровня сложности
                achievementStatusTextView.setVisibility(View.GONE);
            }
        }

        // Настраиваем кнопку начала теста
        startQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), QuizActivity.class);
            intent.putExtra("genre", genre);
            intent.putExtra("genre_name", genreName);
            intent.putExtra("difficulty", difficulty);
            startActivity(intent);
        });
    }

    private void loadProgressAndUpdateUI(TextView completionStatusTextView, TextView lastScoreTextView) {
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        String docId = userId + "_" + genre + "_" + difficulty;
        db.collection("user_progress")
          .document(docId)
          .get()
          .addOnSuccessListener(documentSnapshot -> {
              if (documentSnapshot.exists()) {
                  Long score = documentSnapshot.getLong("score");
                  int maxScore = 5 * 10;
                  if ("Средний".equals(difficulty)) maxScore = 10 * 10;
                  else if ("Сложный".equals(difficulty)) maxScore = 15 * 10;

                  String status;
                  if (score != null && score == maxScore) {
                      status = "Пройдено";
                  } else if (score != null && score > 0) {
                      status = "Пройдено частично";
                  } else {
                      status = "Не пройдено";
                  }
                  completionStatusTextView.setText(status);
                  lastScoreTextView.setText("Последний результат: " + (score != null ? score : "--") + " баллов");
              } else {
                  completionStatusTextView.setText("Не пройдено");
                  lastScoreTextView.setText("Последний результат: --");
              }
          });
    }
} 