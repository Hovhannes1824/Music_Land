package com.example.music_land;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.music_land.databinding.ActivityRegisterBinding;
import com.example.music_land.firebase.FirebaseAuthManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = FirebaseAuthManager.getInstance();

        // Настройка обработчиков кнопок
        binding.registerButton.setOnClickListener(v -> registerUser());
        binding.backButton.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();
        String displayName = binding.nameEditText.getText().toString().trim();

        // Валидация полей
        if (TextUtils.isEmpty(displayName)) {
            binding.nameEditText.setError("Введите имя");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Введите email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Введите пароль");
            return;
        }

        if (password.length() < 6) {
            binding.passwordEditText.setError("Пароль должен содержать не менее 6 символов");
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.confirmPasswordEditText.setError("Пароли не совпадают");
            return;
        }

        // Показываем прогресс
        showLoading(true);

        // Выполняем регистрацию
        authManager.registerUser(email, password, displayName, new FirebaseAuthManager.RegisterCallback() {
            @Override
            public void onSuccess(boolean needsVerification) {
                runOnUiThread(() -> {
                    showLoading(false);
                    
                    // Показываем диалог с информацией о подтверждении email
                    if (needsVerification) {
                        showEmailVerificationNotice(email);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void showEmailVerificationNotice(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтверждение email");
        builder.setMessage("На ваш email " + email + " отправлено письмо со ссылкой для подтверждения. " +
                "Пожалуйста, перейдите по ссылке в письме для полного доступа к приложению.");
        builder.setPositiveButton("Продолжить", (dialog, which) -> {
            // Переходим на главный экран
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finishAffinity();
        });
        
        builder.setCancelable(false);
        builder.show();
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.registerButton.setEnabled(!isLoading);
        binding.backButton.setEnabled(!isLoading);
        binding.nameEditText.setEnabled(!isLoading);
        binding.emailEditText.setEnabled(!isLoading);
        binding.passwordEditText.setEnabled(!isLoading);
        binding.confirmPasswordEditText.setEnabled(!isLoading);
    }
} 