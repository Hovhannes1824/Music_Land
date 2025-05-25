package com.example.music_land;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DifficultySelectionActivity extends AppCompatActivity {

    private static final String[] TAB_NAMES = new String[]{"Легкий", "Средний", "Сложный"};
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private String selectedGenre;
    private String selectedGenreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_selection);

        // Получаем выбранный жанр из Intent
        selectedGenre = getIntent().getStringExtra("genre");
        selectedGenreName = getIntent().getStringExtra("genre_name");
        if (selectedGenre == null) {
            selectedGenre = "unknown";
        }
        if (selectedGenreName == null) {
            selectedGenreName = "Жанр";
        }

        // Настраиваем toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Выбор сложности");
        }

        // Отображаем название жанра
        TextView genreNameTextView = findViewById(R.id.genreNameTextView);
        genreNameTextView.setText("Жанр: " + selectedGenreName);

        // Настраиваем ViewPager и TabLayout
        viewPager = findViewById(R.id.difficultyViewPager);
        tabLayout = findViewById(R.id.difficultyTabLayout);

        // Создаем адаптер для ViewPager
        DifficultyPagerAdapter pagerAdapter = new DifficultyPagerAdapter(this, selectedGenre, selectedGenreName);
        viewPager.setAdapter(pagerAdapter);

        // Связываем TabLayout с ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(TAB_NAMES[position]);
        }).attach();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Адаптер для ViewPager
    private static class DifficultyPagerAdapter extends FragmentStateAdapter {
        private final String genre;
        private final String genreName;

        public DifficultyPagerAdapter(FragmentActivity fa, String genre, String genreName) {
            super(fa);
            this.genre = genre;
            this.genreName = genreName;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            String difficulty;
            switch (position) {
                case 0:
                    difficulty = "Легкий";
                    break;
                case 1:
                    difficulty = "Средний";
                    break;
                case 2:
                default:
                    difficulty = "Сложный";
                    break;
            }
            return DifficultyLevelFragment.newInstance(genre, genreName, difficulty);
        }

        @Override
        public int getItemCount() {
            return TAB_NAMES.length;
        }
    }
} 