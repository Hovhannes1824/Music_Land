package com.example.music_land;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.music_land.adapter.LeaderboardAdapter;
import com.example.music_land.data.model.UserScore;
import com.example.music_land.databinding.ActivityLeaderboardBinding;
import com.example.music_land.firebase.FirestoreManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private ActivityLeaderboardBinding binding;
    private final List<String> genres = Arrays.asList(
        "Rock", "Jazz", "Metal", "Classical", "Pop", "Hip Hop"
    );
    
    private FirestoreManager firestoreManager;
    private static final int LEADERBOARD_LIMIT = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaderboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        firestoreManager = FirestoreManager.getInstance();

        setupGenreSpinner();
        setupRecyclerView();
        
        // Настраиваем кнопку назад
        binding.backButton.setOnClickListener(v -> finish());
        
        // Показываем прогресс при загрузке
        showLoading(true);
    }

    private void setupGenreSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            R.layout.item_spinner_white,
            genres
        );
        adapter.setDropDownViewResource(R.layout.item_spinner_white);
        binding.genreSpinner.setAdapter(adapter);

        binding.genreSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedGenre = genres.get(position);
                loadLeaderboard(selectedGenre);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupRecyclerView() {
        binding.leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.leaderboardRecyclerView.setAdapter(new LeaderboardAdapter(new ArrayList<>()));
    }

    private void loadLeaderboard(String genre) {
        showLoading(true);
        
        firestoreManager.getLeaderboardByGenre(genre, LEADERBOARD_LIMIT, new FirestoreManager.LeaderboardCallback() {
            @Override
            public void onLeaderboardLoaded(List<UserScore> scores) {
                runOnUiThread(() -> {
                    ((LeaderboardAdapter) binding.leaderboardRecyclerView.getAdapter()).updateScores(scores);
                    showLoading(false);
                    
                    // Показываем сообщение, если нет результатов
                    if (scores.isEmpty()) {
                        binding.emptyStateText.setVisibility(View.VISIBLE);
                    } else {
                        binding.emptyStateText.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(LeaderboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    showLoading(false);
                    binding.emptyStateText.setVisibility(View.VISIBLE);
                });
            }
        });
    }
    
    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.leaderboardRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
} 