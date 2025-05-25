package com.example.music_land;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.music_land.databinding.ActivityLessonDetailBinding;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;

public class LessonDetailActivity extends AppCompatActivity {

    private ActivityLessonDetailBinding binding;
    private static final String TAG = "LessonDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLessonDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Настройка Toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        try {
            // Получение данных из Intent
            String lessonId = getIntent().getStringExtra("lesson_id");
            String lessonTitle = getIntent().getStringExtra("lesson_title");
            String lessonContent = getIntent().getStringExtra("lesson_content");

            // Отладочная информация
            Log.d(TAG, "Получены данные урока. ID: " + lessonId + ", Заголовок: " + lessonTitle);

            // Проверяем данные
            if (lessonTitle == null || lessonContent == null) {
                Toast.makeText(this, "Ошибка: данные урока отсутствуют", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Данные урока отсутствуют");
                finish();
                return;
            }

            // Устанавливаем данные в представления
            getSupportActionBar().setTitle(lessonTitle);
            
            // Создаем экземпляр Markwon с поддержкой Markdown и загрузкой изображений через Glide
            final Markwon markwon = Markwon.builder(this)
                .usePlugin(GlideImagesPlugin.create(this))
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureTheme(@androidx.annotation.NonNull MarkwonTheme.Builder builder) {
                        builder
                            .codeTextColor(getResources().getColor(R.color.text_color_primary))
                            .headingBreakHeight(0)
                            .headingTextSizeMultipliers(new float[]{1.5f, 1.3f, 1.2f, 1.1f, 1f, 1f})
                            .bulletWidth(8)
                            .linkColor(getResources().getColor(R.color.purple_500))
                            .codeBlockMargin(16)
                            .codeTextSize(16);
                    }
                })
                .build();
                
            // Применяем форматирование Markdown к содержимому урока
            markwon.setMarkdown(binding.lessonContentTextView, lessonContent);

            // Устанавливаем увеличенный межстрочный интервал для лучшей читаемости
            binding.lessonContentTextView.setLineSpacing(0, 1.2f);

        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке урока: " + e.getMessage());
            Toast.makeText(this, "Ошибка при загрузке урока: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 