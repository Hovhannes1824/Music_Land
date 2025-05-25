package com.example.music_land;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.music_land.databinding.ActivityLoginBinding;
import com.example.music_land.firebase.FirebaseAuthManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = FirebaseAuthManager.getInstance();

        // Если пользователь уже вошел, перенаправляем на главный экран
        if (authManager.isUserLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Настройка обработчиков кнопок
        binding.loginButton.setOnClickListener(v -> loginUser());
        binding.registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
        binding.forgotPasswordButton.setOnClickListener(v -> showForgotPasswordDialog());
        
        // Добавляем обработчик для демо-кнопки
        binding.demoButton.setOnClickListener(v -> loginWithDemoAccount());
    }

    private void loginUser() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        // Валидация полей
        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Введите email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Введите пароль");
            return;
        }

        // Показываем прогресс
        showLoading(true);

        // Выполняем вход
        authManager.loginUser(email, password, new FirebaseAuthManager.RegisterCallback() {
            @Override
            public void onSuccess(boolean needsVerification) {
                runOnUiThread(() -> {
                    showLoading(false);
                    
                    // Переходим на главный экран независимо от статуса верификации
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    
                    // Если email не подтвержден, показываем тост с напоминанием
                    if (needsVerification) {
                        Toast.makeText(LoginActivity.this, 
                                "Пожалуйста, подтвердите ваш email для полного доступа", 
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showForgotPasswordDialog() {
        String email = binding.emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Введите email для сброса пароля");
            return;
        }

        showLoading(true);

        authManager.resetPassword(email, new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this, 
                            "Инструкции по сбросу пароля отправлены на " + email, 
                            Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loginWithDemoAccount() {
        // Показываем прогресс
        showLoading(true);

        // Выполняем вход с демо-аккаунтом
        authManager.loginDemoAccount("individualproject2025@gmail.com", "Samsung2025", 
            new FirebaseAuthManager.RegisterCallback() {
                @Override
                public void onSuccess(boolean needsVerification) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(LoginActivity.this, 
                            "Ошибка входа в демо-аккаунт: " + errorMessage, 
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.loginButton.setEnabled(!isLoading);
        binding.registerButton.setEnabled(!isLoading);
        binding.forgotPasswordButton.setEnabled(!isLoading);
        binding.demoButton.setEnabled(!isLoading);
        binding.emailEditText.setEnabled(!isLoading);
        binding.passwordEditText.setEnabled(!isLoading);
    }
} 