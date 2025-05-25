package com.example.music_land;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.music_land.adapter.LessonAdapter;
import com.example.music_land.data.LessonData;
import com.example.music_land.data.model.Lesson;
import com.example.music_land.database.LessonDao;
import com.example.music_land.databinding.FragmentLessonsBinding;

import java.util.ArrayList;
import java.util.List;

public class LessonsFragment extends Fragment {

    private FragmentLessonsBinding binding;
    private LessonAdapter lessonAdapter;
    private List<Lesson> lessons = new ArrayList<>();
    private Spinner genreSpinner;
    private List<String> genres;
    private LessonDao lessonDao;
    private static final String TAG = "LessonsFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView вызван");
        binding = FragmentLessonsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated вызван");
        
        // Инициализируем DAO для работы с базой данных
        lessonDao = new LessonDao(requireContext());
        
        // Настройка RecyclerView
        binding.lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        lessonAdapter = new LessonAdapter(lessons, lesson -> {
            try {
                // Обработка нажатия на урок - открытие детальной информации
                Intent intent = new Intent(requireContext(), LessonDetailActivity.class);
                intent.putExtra("lesson_id", lesson.getId());
                intent.putExtra("lesson_title", lesson.getTitle());
                intent.putExtra("lesson_content", lesson.getContent());
                startActivity(intent);
                
                // Для отладки
                Toast.makeText(requireContext(), "Открывается урок: " + lesson.getTitle(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при открытии урока: " + e.getMessage());
                Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.lessonsRecyclerView.setAdapter(lessonAdapter);
        
        // Загружаем данные уроков в базу и настраиваем спиннер
        loadLessonsToDatabase();
    }
    
    private void loadLessonsToDatabase() {
        Log.d(TAG, "loadLessonsToDatabase вызван");
        
        // Всегда загружаем данные из локального репозитория, чтобы получить обновленные уроки
        List<Lesson> allLessons = LessonData.getAllLessons();
        boolean success = lessonDao.saveLessons(allLessons);
        
        if (success) {
            Log.d(TAG, "Уроки успешно сохранены в базу данных");
            // Получаем список жанров/категорий
            List<String> allGenres = LessonData.getAllGenres();
            setupGenreSpinner(allGenres);
        } else {
            Log.e(TAG, "Ошибка при сохранении уроков в базу данных");
            Toast.makeText(requireContext(), "Ошибка при загрузке уроков", Toast.LENGTH_SHORT).show();
            
            // Используем локальные данные из репозитория
            setupGenreSpinner(LessonData.getAllGenres());
        }
    }
    
    private void setupGenreSpinner(List<String> genreList) {
        Log.d(TAG, "setupGenreSpinner вызван");
        genreSpinner = binding.genreSpinner;

        // Получаем только реальные жанры из LessonData
        genres = new ArrayList<>(genreList); // Например: [Rock, Pop, Hip Hop, Jazz, Classical, Metal]
        // Для отображения в Spinner добавляем "Все жанры" в начало
        List<String> spinnerGenres = new ArrayList<>(genres);
        spinnerGenres.add(0, "Все жанры");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.item_spinner_white,
                spinnerGenres
        );
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_white);
        genreSpinner.setAdapter(spinnerAdapter);

        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    loadLessonsByGenre("Все жанры");
                } else {
                    // Фильтруем по точному ключу жанра из LessonData
                    String selectedGenre = genres.get(position - 1);
                    loadLessonsByGenre(selectedGenre);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Загружаем уроки для первоначального отображения
        loadLessonsByGenre("Все жанры");
    }
    
    private void loadLessonsByGenre(String genre) {
        Log.d(TAG, "loadLessonsByGenre вызван для жанра: " + genre);
        lessons.clear();
        
        // Загружаем уроки из базы данных
        if (genre.equals("Все жанры")) {
            lessons.addAll(lessonDao.getAllLessons());
            Log.d(TAG, "Загружены все уроки из базы данных: " + lessons.size());
        } else {
            lessons.addAll(lessonDao.getLessonsByCategory(genre));
            Log.d(TAG, "Загружены уроки по категории " + genre + ": " + lessons.size());
        }
        
        // Если база пуста, используем локальные данные
        if (lessons.isEmpty() && !genre.equals("Все жанры")) {
            lessons.addAll(LessonData.getLessonsForGenre(genre));
            Log.d(TAG, "Загружены уроки из локального репозитория: " + lessons.size());
        } else if (lessons.isEmpty()) {
            lessons.addAll(LessonData.getAllLessons());
            Log.d(TAG, "Загружены все уроки из локального репозитория: " + lessons.size());
        }
        
        // Выводим детали уроков для отладки
        if (!lessons.isEmpty()) {
            Lesson firstLesson = lessons.get(0);
            Log.d(TAG, "Первый урок: " + firstLesson.getTitle());
            Log.d(TAG, "Содержимое начало: " + firstLesson.getContent().substring(0, Math.min(100, firstLesson.getContent().length())));
        }
        
        // Обновляем адаптер
        lessonAdapter.notifyDataSetChanged();
        
        // Отображаем сообщение, если уроки не найдены
        if (lessons.isEmpty()) {
            binding.emptyView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Уроки не найдены");
        } else {
            binding.emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView вызван");
        binding = null;
    }
} 