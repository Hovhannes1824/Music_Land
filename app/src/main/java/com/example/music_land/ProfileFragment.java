package com.example.music_land;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.music_land.data.model.User;
import com.example.music_land.databinding.FragmentProfileBinding;
import com.example.music_land.firebase.FirebaseAuthManager;
import com.example.music_land.firebase.FirestoreManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuthManager authManager;
    private FirestoreManager firestoreManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            authManager = FirebaseAuthManager.getInstance();
            firestoreManager = FirestoreManager.getInstance(requireContext());

            // Загрузка данных пользователя
            loadUserData();
            
            // Установка обработчика для кнопки выхода
            if (binding.logoutButton != null) {
                binding.logoutButton.setOnClickListener(v -> {
                    authManager.logoutUser();
                    // Перенаправление на экран входа
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            }
            
            // Добавление обработчика для кнопки достижений
            if (binding.achievementsButton != null) {
                binding.achievementsButton.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), AchievementsActivity.class);
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка при инициализации профиля: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Загружает данные пользователя из Firestore
     */
    private void loadUserData() {
        try {
            if (binding == null) return;
            binding.progressBar.setVisibility(View.VISIBLE);
            if (authManager != null && authManager.isUserLoggedIn()) {
                firestoreManager.getCurrentUserData(new FirestoreManager.UserCallback() {
                    @Override
                    public void onUserLoaded(User user) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                binding.progressBar.setVisibility(View.GONE);
                                if (user != null) {
                                    if (binding.usernameTextView != null)
                                        binding.usernameTextView.setText(user.getDisplayName() != null ? user.getDisplayName() : "-");
                                    if (binding.emailTextView != null)
                                        binding.emailTextView.setText(user.getEmail() != null ? user.getEmail() : "-");
                                } else {
                                    Toast.makeText(getContext(), "Нет данных пользователя в базе", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                    @Override
                    public void onError(String errorMessage) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                binding.progressBar.setVisibility(View.GONE);
                                if (errorMessage != null && errorMessage.contains("не найдены")) {
                                    // Автоматически создаём профиль пользователя
                                    String uid = authManager.getCurrentUserId();
                                    String email = authManager.getCurrentUser() != null ? authManager.getCurrentUser().getEmail() : "";
                                    String displayName = authManager.getCurrentUser() != null ? authManager.getCurrentUser().getDisplayName() : "";
                                    User newUser = new User(uid, email, displayName);
                                    firestoreManager.updateUserData(newUser)
                                        .addOnSuccessListener(aVoid -> {
                                            // Убираем сообщение об успешном создании профиля
                                            // Toast.makeText(getContext(), "Профиль создан автоматически", Toast.LENGTH_SHORT).show();
                                            loadUserData();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Ошибка автосоздания профиля: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                                } else {
                                    Toast.makeText(getContext(), "Ошибка загрузки профиля: " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            } else {
                binding.progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            if (binding != null) binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Ошибка профиля: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 