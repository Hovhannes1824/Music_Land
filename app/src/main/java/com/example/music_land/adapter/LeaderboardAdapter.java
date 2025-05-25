package com.example.music_land.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music_land.data.model.UserScore;
import com.example.music_land.databinding.ItemLeaderboardBinding;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private List<UserScore> scores;
    private static final String TAG = "LeaderboardAdapter";

    public LeaderboardAdapter(List<UserScore> scores) {
        Log.d(TAG, "Создан адаптер с " + scores.size() + " элементами");
        this.scores = new ArrayList<>(scores);
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder вызван");
        ItemLeaderboardBinding binding = ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.getContext()),
            parent,
            false
        );
        return new LeaderboardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder для позиции: " + position);
        UserScore score = scores.get(position);
        holder.bind(score);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount вернул: " + scores.size());
        return scores.size();
    }

    public void updateScores(List<UserScore> newScores) {
        Log.d(TAG, "Обновление списка с " + newScores.size() + " элементами");
        this.scores = new ArrayList<>(newScores);
        notifyDataSetChanged();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        private final ItemLeaderboardBinding binding;

        LeaderboardViewHolder(ItemLeaderboardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(UserScore score) {
            Log.d(TAG, "Привязка данных: " + score.getDisplayName() + ", ранг: " + score.getRank());
            binding.rankTextView.setText(String.valueOf(score.getRank()));
            binding.usernameTextView.setText(score.getDisplayName());
            binding.scoreTextView.setText(String.valueOf(score.getScore()));
        }
    }
} 