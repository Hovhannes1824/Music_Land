package com.example.music_land;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_land.adapter.LessonAdapter;
import com.example.music_land.data.LessonData;
import com.example.music_land.data.model.Lesson;

import java.util.List;

public class LessonActivity extends AppCompatActivity implements LessonAdapter.OnLessonClickListener {
    private RecyclerView lessonsRecyclerView;
    private Spinner genreSpinner;
    private String currentGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        lessonsRecyclerView = findViewById(R.id.lessonsRecyclerView);
        genreSpinner = findViewById(R.id.genreSpinner);

        setupGenreSpinner();
        setupRecyclerView();
    }

    private void setupGenreSpinner() {
        List<String> genres = LessonData.getAllGenres();
        genres.add(0, "Все жанры");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            R.layout.item_spinner_white,
            genres
        );
        adapter.setDropDownViewResource(R.layout.item_spinner_white);
        genreSpinner.setAdapter(adapter);

        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentGenre = genres.get(position);
                if (currentGenre.equals("Все жанры")) {
                    loadAllLessons();
                } else {
                    loadLessons(currentGenre);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupRecyclerView() {
        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadLessons(String genre) {
        List<Lesson> lessons = LessonData.getLessonsForGenre(genre);
        LessonAdapter adapter = new LessonAdapter(lessons, this);
        lessonsRecyclerView.setAdapter(adapter);
    }

    private void loadAllLessons() {
        List<Lesson> lessons = LessonData.getAllLessons();
        LessonAdapter adapter = new LessonAdapter(lessons, this);
        lessonsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLessonClick(Lesson lesson) {
        Intent intent = new Intent(this, LessonDetailActivity.class);
        intent.putExtra("lesson_title", lesson.getTitle());
        intent.putExtra("lesson_description", lesson.getDescription());
        intent.putExtra("lesson_content", lesson.getContent());
        startActivity(intent);
    }
} 