package com.example.music_land;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.music_land.databinding.ActivityAchievementsBinding;
import com.example.music_land.firebase.AchievementManager;
import com.example.music_land.firebase.FirebaseAuthManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementsActivity extends AppCompatActivity {
    private ActivityAchievementsBinding binding;
    private AchievementManager achievementManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAchievementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        achievementManager = AchievementManager.getInstance();
        
        // Настраиваем RecyclerView
        setupRecyclerView();
        
        // Настраиваем кнопку назад
        binding.backButton.setOnClickListener(v -> finish());
        
        // Загружаем достижения
        loadAchievementsDirectly();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем список при возврате к экрану
        loadAchievementsDirectly();
    }
    
    private void setupRecyclerView() {
        binding.achievementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.achievementsRecyclerView.setAdapter(new AchievementAdapter(new ArrayList<>()));
    }
    
    /**
     * Загружает достижения напрямую из Firestore
     */
    private void loadAchievementsDirectly() {
        // Показываем прогресс
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.achievementsRecyclerView.setVisibility(View.GONE);
        binding.emptyStateText.setVisibility(View.GONE);
        
        // Получаем текущего пользователя
        String userId = FirebaseAuthManager.getInstance().getCurrentUserId();
        if (userId == null) {
            binding.progressBar.setVisibility(View.GONE);
            binding.emptyStateText.setText("Необходимо авторизоваться для просмотра достижений");
            binding.emptyStateText.setVisibility(View.VISIBLE);
            return;
        }
        
        // Загружаем достижения
        achievementManager.loadAchievements(userId, new AchievementManager.AchievementsListCallback() {
            @Override
            public void onAchievementsLoaded(Map<String, Long> achievements) {
                // Конвертируем Map в список для отображения в RecyclerView
                List<AchievementItem> achievementItems = new ArrayList<>();
                Map<String, List<AchievementItem>> genreAchievements = new HashMap<>();
                if (achievements == null || achievements.isEmpty()) {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.emptyStateText.setText("У вас пока нет достижений");
                        binding.emptyStateText.setVisibility(View.VISIBLE);
                    });
                    return;
                }
                for (String achievementType : achievements.keySet()) {
                    if (achievementType == null || achievementType.trim().isEmpty()) continue;
                    String achievementName = AchievementManager.getAchievementName(achievementType);
                    String achievementDescription = AchievementManager.getAchievementDescription(achievementType);
                    long timestamp = achievements.get(achievementType) != null ? achievements.get(achievementType) : 0L;
                    if (achievementName == null || achievementDescription == null) continue;
                    String genre = getGenreFromAchievementType(achievementType);
                    AchievementItem item = new AchievementItem(
                        achievementType,
                        achievementName,
                        achievementDescription,
                        timestamp
                    );
                    if (genre != null) {
                        if (!genreAchievements.containsKey(genre)) {
                            genreAchievements.put(genre, new ArrayList<>());
                        }
                        genreAchievements.get(genre).add(item);
                    } else {
                        achievementItems.add(item);
                    }
                }
                for (String genre : genreAchievements.keySet()) {
                    AchievementItem genreHeader = new AchievementItem(
                        "header_" + genre.toLowerCase(),
                        "Достижения: " + genre,
                        "Прогресс изучения жанра",
                        0
                    );
                    genreHeader.setHeader(true);
                    achievementItems.add(genreHeader);
                    List<AchievementItem> genreItems = genreAchievements.get(genre);
                    Collections.sort(genreItems, (a1, a2) -> {
                        int diffLevel1 = getDifficultyLevel(a1.getType());
                        int diffLevel2 = getDifficultyLevel(a2.getType());
                        return diffLevel1 - diffLevel2;
                    });
                    achievementItems.addAll(genreItems);
                }
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (achievementItems.isEmpty()) {
                        binding.emptyStateText.setText("У вас пока нет достижений");
                        binding.emptyStateText.setVisibility(View.VISIBLE);
                    } else {
                        AchievementAdapter adapter = (AchievementAdapter) binding.achievementsRecyclerView.getAdapter();
                        if (adapter != null) {
                            adapter.updateAchievements(achievementItems);
                        }
                        binding.achievementsRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.emptyStateText.setText("Ошибка загрузки достижений: " + errorMessage);
                    binding.emptyStateText.setVisibility(View.VISIBLE);
                });
            }
        });
    }
    
    /**
     * Возвращает жанр на основе типа достижения
     */
    private String getGenreFromAchievementType(String achievementType) {
        if (achievementType == null) return null;
        
        if (achievementType.contains("classical") || 
            achievementType.startsWith("classical_")) {
            return "Классическая музыка";
        } else if (achievementType.contains("rock") || 
                  achievementType.startsWith("rock_")) {
            return "Рок";
        } else if (achievementType.contains("jazz") || 
                  achievementType.startsWith("jazz_")) {
            return "Джаз";
        } else if (achievementType.contains("pop") || 
                  achievementType.startsWith("pop_")) {
            return "Поп-музыка";
        } else if (achievementType.contains("folk") || 
                  achievementType.startsWith("folk_")) {
            return "Народная музыка";
        } else if (achievementType.contains("electronic") || 
                  achievementType.startsWith("electronic_")) {
            return "Электронная музыка";
        } else if (achievementType.contains("metal") || 
                  achievementType.startsWith("metal_")) {
            return "Метал";
        } else if (achievementType.contains("hiphop") || 
                  achievementType.startsWith("hiphop_")) {
            return "Хип-хоп";
        }
        
        return null;
    }
    
    /**
     * Возвращает уровень сложности достижения (0 - идеальное, 1 - легкий, 2 - средний, 3 - сложный)
     */
    private int getDifficultyLevel(String achievementType) {
        if (achievementType == null) return 0;
        
        if (achievementType.contains("perfect_")) {
            return 0;
        } else if (achievementType.endsWith("_easy")) {
            return 1;
        } else if (achievementType.endsWith("_medium")) {
            return 2;
        } else if (achievementType.endsWith("_hard")) {
            return 3;
        }
        
        return 0;
    }
    
    /**
     * Модель данных для элемента списка достижений
     */
    public static class AchievementItem {
        private final String type;
        private final String name;
        private final String description;
        private final long date;
        private boolean isHeader;
        
        public AchievementItem(String type, String name, String description, long date) {
            this.type = type;
            this.name = name;
            this.description = description;
            this.date = date;
        }
        
        public String getType() {
            return type;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public long getDate() {
            return date;
        }
        
        public void setHeader(boolean isHeader) {
            this.isHeader = isHeader;
        }
        
        public boolean isHeader() {
            return isHeader;
        }
    }
    
    /**
     * Адаптер для списка достижений
     */
    private static class AchievementAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {
        private List<AchievementItem> achievements;
        
        public AchievementAdapter(List<AchievementItem> achievements) {
            this.achievements = new ArrayList<>(achievements);
        }
        
        @Override
        public void onBindViewHolder(AchievementViewHolder holder, int position) {
            holder.bind(achievements.get(position));
        }
        
        @Override
        public int getItemCount() {
            return achievements.size();
        }
        
        public void updateAchievements(List<AchievementItem> newAchievements) {
            this.achievements = new ArrayList<>(newAchievements);
            notifyDataSetChanged();
        }
        
        @Override
        public int getItemViewType(int position) {
            return achievements.get(position).isHeader() ? 1 : 0;
        }
        
        @Override
        public AchievementViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(parent.getContext());
            android.view.View view;
            
            if (viewType == 1) {
                // Заголовок жанра
                view = inflater.inflate(R.layout.item_achievement_header, parent, false);
            } else {
                // Обычное достижение
                view = inflater.inflate(R.layout.item_achievement, parent, false);
            }
            
            return new AchievementViewHolder(view, viewType);
        }
        
        static class AchievementViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            private final android.widget.TextView nameTextView;
            private final android.widget.TextView descTextView;
            private final android.widget.TextView dateTextView;
            private final android.widget.ImageView iconImageView;
            private final android.view.View iconBackground;
            private final int viewType;
            
            AchievementViewHolder(android.view.View itemView, int viewType) {
                super(itemView);
                this.viewType = viewType;
                nameTextView = itemView.findViewById(R.id.achievementNameTextView);
                descTextView = itemView.findViewById(R.id.achievementDescTextView);
                dateTextView = itemView.findViewById(R.id.achievementDateTextView);
                if (viewType == 1) {
                    iconImageView = null;
                    iconBackground = null;
                } else {
                    iconImageView = itemView.findViewById(R.id.achievementIcon);
                    iconBackground = null;
                }
            }
            
            void bind(AchievementItem achievement) {
                if (achievement == null) return;
                if (nameTextView != null) nameTextView.setText(achievement.getName() != null ? achievement.getName() : "");
                if (descTextView != null) descTextView.setText(achievement.getDescription() != null ? achievement.getDescription() : "");
                if (viewType == 1) return;
                if (achievement.getDate() > 0 && dateTextView != null) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault());
                    String dateStr = sdf.format(new java.util.Date(achievement.getDate()));
                    dateTextView.setText("Получено: " + dateStr);
                    dateTextView.setVisibility(android.view.View.VISIBLE);
                } else if (dateTextView != null) {
                    dateTextView.setVisibility(android.view.View.GONE);
                }
                if (iconImageView != null) {
                    setupIconAndColor(achievement.getType());
                }
            }
            
            private void setupIconAndColor(String achievementType) {
                int iconResId = android.R.drawable.btn_star_big_on;
                int backgroundColor = android.graphics.Color.parseColor("#4CAF50"); // Зеленый по умолчанию
                if (achievementType != null) {
                    if (achievementType.endsWith("_easy")) {
                        iconResId = android.R.drawable.ic_media_play;
                        backgroundColor = android.graphics.Color.parseColor("#4CAF50");
                    } else if (achievementType.endsWith("_medium")) {
                        iconResId = android.R.drawable.ic_media_play;
                        backgroundColor = android.graphics.Color.parseColor("#FFC107");
                    } else if (achievementType.endsWith("_hard")) {
                        iconResId = android.R.drawable.ic_media_play;
                        backgroundColor = android.graphics.Color.parseColor("#F44336");
                    } else if (achievementType.startsWith("perfect_")) {
                        iconResId = android.R.drawable.btn_star_big_on;
                        backgroundColor = android.graphics.Color.parseColor("#FFEB3B");
                    } else if (achievementType.equals(com.example.music_land.firebase.AchievementManager.ACHIEVEMENT_FIRST_LESSON) ||
                            achievementType.equals(com.example.music_land.firebase.AchievementManager.ACHIEVEMENT_FIVE_LESSONS) ||
                            achievementType.equals(com.example.music_land.firebase.AchievementManager.ACHIEVEMENT_TEN_LESSONS)) {
                        iconResId = android.R.drawable.ic_menu_edit;
                        backgroundColor = android.graphics.Color.parseColor("#FF9800");
                    } else if (achievementType.equals(com.example.music_land.firebase.AchievementManager.ACHIEVEMENT_FIRST_QUIZ)) {
                        iconResId = android.R.drawable.ic_menu_help;
                        backgroundColor = android.graphics.Color.parseColor("#03A9F4");
                    } else if (achievementType.equals(com.example.music_land.firebase.AchievementManager.ACHIEVEMENT_HIGH_SCORE)) {
                        iconResId = android.R.drawable.btn_star_big_on;
                        backgroundColor = android.graphics.Color.parseColor("#FFEB3B");
                    } else if (achievementType.equals(com.example.music_land.firebase.AchievementManager.ACHIEVEMENT_PRACTICE_MASTER)) {
                        iconResId = android.R.drawable.ic_menu_manage;
                        backgroundColor = android.graphics.Color.parseColor("#607D8B");
                    }
                }
                iconImageView.setImageResource(iconResId);
                // Можно добавить установку backgroundColor, если нужно
            }
        }
    }
} 