package com.example.music_land;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_land.adapter.GenreAdapter;
import com.example.music_land.data.model.GenreItem;
import com.example.music_land.firebase.FirebaseAuthManager;
import com.example.music_land.firebase.CloudinaryManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView genreRecyclerView;
    private GenreAdapter genreAdapter;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "MainActivity onCreate start");
            setContentView(R.layout.activity_main);
            
            // Настраиваем Toolbar
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            // Убираем заголовок
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("");
            }

            // Инициализируем Firebase Auth Manager
            authManager = FirebaseAuthManager.getInstance();
            
            // ВРЕМЕННО ОТКЛЮЧАЕМ ПРОВЕРКУ АВТОРИЗАЦИИ
            // Проверяем авторизацию пользователя
            /*
            if (!authManager.isUserLoggedIn()) {
                // Если пользователь не авторизован, перенаправляем на экран входа
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            // Проверяем, подтвердил ли пользователь email
            checkEmailVerification();
            */

            // Initialize views
            genreRecyclerView = findViewById(R.id.genreRecyclerView);
            bottomNavigationView = findViewById(R.id.bottomNavigationView);

            // Setup genre grid with music history focus
            List<GenreItem> genreItems = createGenreItems();
            
            // Создаем адаптер с правильными параметрами: контекст, список жанров, обработчик клика
            genreAdapter = new GenreAdapter(this, genreItems, this::startQuizWithGenreItem);
            genreRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            genreRecyclerView.setAdapter(genreAdapter);

            // Setup bottom navigation
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_quizzes) {
                    // Already on quizzes screen
                    showMainQuizUI();
                    return true;
                } else if (itemId == R.id.navigation_lessons) {
                    loadFragment(new LessonsFragment());
                    return true;
                } else if (itemId == R.id.navigation_tools) {
                    loadFragment(new ToolsFragment());
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    loadFragment(new ProfileFragment());
                    return true;
                }
                return false;
            });

            // Проверяем инициализацию Cloudinary
            boolean isCloudinaryInitialized = CloudinaryManager.getInstance().isInitialized();
            Log.d(TAG, "Cloudinary initialized: " + isCloudinaryInitialized);
            
            // Убираем сообщение об успешном запуске
            // Toast.makeText(this, "Приложение запущено успешно", Toast.LENGTH_LONG).show();
            Log.d(TAG, "MainActivity onCreate complete");

            // Устанавливаем цвет status bar явно
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при запуске MainActivity: " + e.getMessage(), e);
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    // Создаем список элементов жанра
    private List<GenreItem> createGenreItems() {
        List<GenreItem> items = new ArrayList<>();
        
        items.add(new GenreItem("rock", "Рок", R.drawable.ic_rock_genre));
        items.add(new GenreItem("pop", "Поп", R.drawable.ic_pop_genre));
        items.add(new GenreItem("hiphop", "Хип-хоп", R.drawable.ic_hiphop_genre));
        items.add(new GenreItem("jazz", "Джаз", R.drawable.ic_jazz_genre));
        items.add(new GenreItem("classical", "Классика", R.drawable.ic_classical_genre));
        items.add(new GenreItem("metal", "Метал", R.drawable.ic_metal_genre));
        
        return items;
    }
    
    // Обработчик клика по жанру
    private void startQuizWithGenreItem(GenreItem genre) {
        // Вместо запуска теста открываем экран выбора сложности
        Intent intent = new Intent(this, DifficultySelectionActivity.class);
        intent.putExtra("genre", genre.getId()); // английский ключ
        intent.putExtra("genre_name", genre.getName()); // русское название
        startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Проверяем авторизацию пользователя
        if (!authManager.isUserLoggedIn()) {
            // Если пользователь не авторизован, перенаправляем на экран входа
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        // Обновляем информацию о пользователе
        authManager.reloadUser(new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess() {
                // Пользователь обновлен успешно, проверяем верификацию
                checkEmailVerification();
            }

            @Override
            public void onError(String errorMessage) {
                // Игнорируем ошибки обновления
            }
        });
    }
    
    private void checkEmailVerification() {
        // Пропускаем проверку для демо-аккаунта
        if (authManager.isDemoAccount()) {
            return;
        }

        if (!authManager.isEmailVerified()) {
            // Показываем уведомление о необходимости подтверждения email
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), 
                    "Пожалуйста, подтвердите ваш email для полного доступа", 
                    Snackbar.LENGTH_LONG);
            
            snackbar.setAction("Подтвердить", v -> {
                showVerificationDialog();
            });
            
            snackbar.show();
        }
    }
    
    private void showVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтверждение email");
        builder.setMessage("Для полного доступа к приложению необходимо подтвердить email. Проверьте вашу почту или отправьте ссылку повторно.");
        
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        
        builder.setNeutralButton("Отправить ссылку снова", (dialog, which) -> {
            sendVerificationEmail();
        });
        
        builder.show();
    }
    
    private void sendVerificationEmail() {
        authManager.sendEmailVerification(new FirebaseAuthManager.RegisterCallback() {
            @Override
            public void onSuccess(boolean needsVerification) {
                Toast.makeText(MainActivity.this, 
                        "Ссылка для подтверждения отправлена. Проверьте вашу почту.", 
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MainActivity.this, 
                        "Ошибка: " + errorMessage, 
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void showMainQuizUI() {
        // Show the main Quiz UI and hide any fragments
        genreRecyclerView.setVisibility(RecyclerView.VISIBLE);
        findViewById(R.id.titleTextView).setVisibility(RecyclerView.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Fragment()).commit();
    }
    
    private void loadFragment(Fragment fragment) {
        // Hide the main Quiz UI
        genreRecyclerView.setVisibility(RecyclerView.GONE);
        findViewById(R.id.titleTextView).setVisibility(RecyclerView.GONE);
        
        // Load the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void startQuiz(String genre) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("genre", genre);
        startActivity(intent);
    }
} 