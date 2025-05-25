package com.example.music_land.firebase;

import androidx.annotation.NonNull;

import com.example.music_land.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class FirebaseAuthManager {
    private static FirebaseAuthManager instance;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    
    // Интерфейс для обратных вызовов
    public interface AuthCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
    
    // Интерфейс для обратных вызовов с информацией о необходимости верификации
    public interface RegisterCallback {
        void onSuccess(boolean needsVerification);
        void onError(String errorMessage);
    }
    
    private FirebaseAuthManager() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }
    
    public static synchronized FirebaseAuthManager getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthManager();
        }
        return instance;
    }
    
    // Получение текущего пользователя
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
    
    // Получение ID текущего пользователя
    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    // Проверка, вошел ли пользователь
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }
    
    // Проверка, подтвержден ли email текущего пользователя
    public boolean isEmailVerified() {
        FirebaseUser user = getCurrentUser();
        return user != null && user.isEmailVerified();
    }
    
    // Отправка email для подтверждения
    public void sendEmailVerification(RegisterCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(true);
                    } else {
                        callback.onError("Ошибка отправки подтверждения: " + 
                            (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"));
                    }
                });
        } else {
            callback.onError("Пользователь не авторизован");
        }
    }
    
    // Перепроверка статуса верификации email
    public void reloadUser(AuthCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            user.reload()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Ошибка обновления данных пользователя: " + 
                            (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"));
                    }
                });
        } else {
            callback.onError("Пользователь не авторизован");
        }
    }
    
    // Регистрация нового пользователя
    public void registerUser(String email, String password, String displayName, RegisterCallback callback) {
        android.util.Log.d("FirebaseAuth", "Начало регистрации пользователя: " + email);
        
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        android.util.Log.d("FirebaseAuth", "Пользователь успешно создан в Firebase Auth");
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            android.util.Log.d("FirebaseAuth", "ID пользователя: " + firebaseUser.getUid());
                            // Создаем объект пользователя
                            User user = new User(firebaseUser.getUid(), email, displayName);
                            // Отправляем email для подтверждения
                            firebaseUser.sendEmailVerification()
                                .addOnCompleteListener(verificationTask -> {
                                    boolean needsVerification = verificationTask.isSuccessful();
                                    android.util.Log.d("FirebaseAuth", "Email верификация отправлена: " + needsVerification);
                                    // Сохраняем данные пользователя в Firestore
                                    saveUserToFirestoreWithRetry(firebaseUser, user, needsVerification, callback, 1);
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("FirebaseAuth", "Ошибка отправки верификации", e);
                                });
                        } else {
                            android.util.Log.e("FirebaseAuth", "FirebaseUser равен null после успешной регистрации");
                            callback.onError("Ошибка получения данных пользователя");
                        }
                    } else {
                        android.util.Log.e("FirebaseAuth", "Ошибка регистрации", task.getException());
                        callback.onError("Ошибка регистрации: " + 
                                (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"));
                    }
                });
    }
    
    // Повторная попытка сохранения пользователя в Firestore
    private void saveUserToFirestoreWithRetry(FirebaseUser firebaseUser, User user, boolean needsVerification, RegisterCallback callback, int attempt) {
        android.util.Log.d("FirebaseAuth", "Попытка " + attempt + " сохранения пользователя в Firestore");
        android.util.Log.d("FirebaseAuth", "Данные пользователя: uid=" + user.getUid() + 
            ", email=" + user.getEmail() + ", displayName=" + user.getDisplayName());
        
        Map<String, Object> userData = user.toMap();
        android.util.Log.d("FirebaseAuth", "Данные для сохранения: " + userData.toString());
        
        firestore.collection("users")
                .document(firebaseUser.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    android.util.Log.d("FirebaseAuth", "Пользователь успешно сохранен в Firestore");
                    callback.onSuccess(needsVerification);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("FirebaseAuth", "Ошибка при сохранении данных (попытка " + attempt + ")", e);
                    android.util.Log.e("FirebaseAuth", "Детали ошибки: " + e.getMessage());
                    if (attempt < 3) {
                        android.util.Log.d("FirebaseAuth", "Повторная попытка через 1 секунду");
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            saveUserToFirestoreWithRetry(firebaseUser, user, needsVerification, callback, attempt + 1);
                        }, 1000);
                    } else {
                        android.util.Log.e("FirebaseAuth", "Все попытки сохранения не удались, удаляем пользователя");
                        firebaseUser.delete();
                        String errorMsg = "Ошибка при сохранении данных: " + e.getMessage() + 
                            "\nПроверьте интернет и настройки Firestore (rules)." +
                            "\nПолные данные пользователя: " + userData.toString();
                        callback.onError(errorMsg);
                    }
                });
    }
    
    // Вход пользователя
    public void loginUser(String email, String password, RegisterCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = getCurrentUser();
                        if (user != null) {
                            // Проверяем, подтвержден ли email
                            if (user.isEmailVerified()) {
                                callback.onSuccess(false); // не требуется верификация
                            } else {
                                // Если email не подтвержден, выходим из аккаунта
                                firebaseAuth.signOut();
                                callback.onSuccess(true); // требуется верификация
                            }
                        } else {
                            callback.onError("Ошибка получения данных пользователя");
                        }
                    } else {
                        callback.onError("Ошибка входа: " + 
                                (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"));
                    }
                });
    }
    
    // Специальный метод для входа в демо-аккаунт (без проверки верификации)
    public void loginDemoAccount(String email, String password, RegisterCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = getCurrentUser();
                        if (user != null) {
                            callback.onSuccess(false); // Всегда успех, без проверки верификации
                        } else {
                            callback.onError("Ошибка получения данных пользователя");
                        }
                    } else {
                        callback.onError("Ошибка входа: " + 
                                (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"));
                    }
                });
    }
    
    // Проверка статуса верификации email
    public void checkEmailVerification(AuthCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (user.isEmailVerified()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Email не подтвержден. Пожалуйста, проверьте вашу почту и перейдите по ссылке для подтверждения.");
                    }
                } else {
                    callback.onError("Ошибка проверки статуса email: " + 
                        (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"));
                }
            });
        } else {
            callback.onError("Пользователь не авторизован");
        }
    }
    
    // Выход пользователя
    public void logoutUser() {
        firebaseAuth.signOut();
    }
    
    // Сброс пароля
    public void resetPassword(String email, AuthCallback callback) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Ошибка сброса пароля: " + 
                                (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"));
                    }
                });
    }
    
    // Проверка, является ли текущий пользователь демо-аккаунтом
    public boolean isDemoAccount() {
        FirebaseUser user = getCurrentUser();
        return user != null && user.getEmail() != null && 
               user.getEmail().equals("individualproject2025@gmail.com");
    }
} 