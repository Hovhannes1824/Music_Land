package com.example.music_land;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.music_land.adapter.LeaderboardAdapter;
import com.example.music_land.data.model.UserScore;
import com.example.music_land.database.LeaderboardDao;
import com.example.music_land.databinding.FragmentLeaderboardBinding;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private LeaderboardAdapter leaderboardAdapter;
    private List<UserScore> userScores = new ArrayList<>();
    private LeaderboardDao leaderboardDao;
    private static final String TAG = "LeaderboardFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView вызван");
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated вызван");
        
        if (binding == null) {
            Log.e(TAG, "binding is null!");
            return;
        }
        
        if (binding.leaderboardRecyclerView == null) {
            Log.e(TAG, "RecyclerView is null!");
            return;
        }
        
        // Инициализируем DAO для работы с базой данных
        leaderboardDao = new LeaderboardDao(requireContext());
        
        try {
            // Настройка RecyclerView
            binding.leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            
            // Создаем и устанавливаем адаптер
            leaderboardAdapter = new LeaderboardAdapter(userScores);
            binding.leaderboardRecyclerView.setAdapter(leaderboardAdapter);
            
            // Загружаем данные из базы
            loadLeaderboardData();
            
            Log.d(TAG, "Адаптер установлен с " + userScores.size() + " элементами");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при настройке RecyclerView: " + e.getMessage());
        }
    }

    private void loadLeaderboardData() {
        Log.d(TAG, "loadLeaderboardData вызван");
        
        // Сначала пробуем загрузить данные из локальной базы
        List<UserScore> savedScores = leaderboardDao.getAllScores();
        
        if (savedScores != null && !savedScores.isEmpty()) {
            // Если в базе есть данные, используем их
            userScores.clear();
            userScores.addAll(savedScores);
            leaderboardAdapter.notifyDataSetChanged();
            
            binding.infoTextView.setText("Количество записей: " + userScores.size());
            Log.d(TAG, "Загружено " + userScores.size() + " записей из базы данных");
        } else {
            // Если базы пуста, создаем тестовые данные и сохраняем в базу
            loadDummyData();
        }
        
        // Показываем сообщение, если данные отсутствуют
        if (userScores.isEmpty()) {
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.infoTextView.setText("Данные отсутствуют");
        } else {
            binding.emptyView.setVisibility(View.GONE);
        }
    }

    private void loadDummyData() {
        Log.d(TAG, "loadDummyData вызван");
        userScores.clear();
        
        // Добавление тестовых данных
        List<UserScore> dummyScores = new ArrayList<>();
        dummyScores.add(new UserScore("user1", "Иван Петров", 950, 1));
        dummyScores.add(new UserScore("user2", "Мария Иванова", 840, 2));
        dummyScores.add(new UserScore("user3", "Алексей Сидоров", 780, 3));
        dummyScores.add(new UserScore("user4", "Екатерина Кузнецова", 720, 4));
        dummyScores.add(new UserScore("user5", "Дмитрий Соколов", 690, 5));
        dummyScores.add(new UserScore("user6", "Ольга Новикова", 650, 6));
        dummyScores.add(new UserScore("user7", "Артем Морозов", 620, 7));
        dummyScores.add(new UserScore("user8", "Наталья Волкова", 590, 8));
        dummyScores.add(new UserScore("user9", "Сергей Павлов", 570, 9));
        dummyScores.add(new UserScore("user10", "Анна Королева", 520, 10));
        
        // Сохраняем тестовые данные в базу
        boolean success = leaderboardDao.saveUserScores(dummyScores);
        if (success) {
            Log.d(TAG, "Тестовые данные успешно сохранены в базу");
            
            // Добавляем данные в список и обновляем адаптер
            userScores.addAll(dummyScores);
            if (leaderboardAdapter != null) {
                leaderboardAdapter.notifyDataSetChanged();
                binding.infoTextView.setText("Количество записей: " + userScores.size());
            }
        } else {
            Log.e(TAG, "Ошибка при сохранении тестовых данных в базу");
            binding.infoTextView.setText("Ошибка при загрузке данных");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView вызван");
        binding = null;
    }
} 