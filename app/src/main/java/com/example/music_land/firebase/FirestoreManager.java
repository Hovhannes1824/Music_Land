package com.example.music_land.firebase;

import android.content.Context;
import com.example.music_land.data.model.Lesson;
import com.example.music_land.data.model.User;
import com.example.music_land.data.model.UserNote;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreManager {
    private static FirestoreManager instance;
    private FirebaseFirestore firestore;
    private Context context;
    
    // Коллекции в базе данных
    private static final String USERS_COLLECTION = "users";
    private static final String NOTES_COLLECTION = "notes";
    
    // Интерфейс для обратных вызовов при получении данных пользователя
    public interface UserCallback {
        void onUserLoaded(User user);
        void onError(String errorMessage);
    }
    
    // Интерфейс для обратных вызовов при получении заметок
    public interface NotesCallback {
        void onNotesLoaded(List<UserNote> notes);
        void onError(String errorMessage);
    }
    
    // Интерфейс для обратных вызовов при получении уроков
    public interface LessonsCallback {
        void onLessonsLoaded(List<Lesson> lessons);
        void onError(String errorMessage);
    }
    
    // Интерфейс для обратных вызовов при сохранении заметок
    public interface SaveNoteCallback {
        void onSuccess(String noteId);
        void onError(String errorMessage);
    }
    
    private FirestoreManager(Context context) {
        this.context = context.getApplicationContext();
        firestore = FirebaseFirestore.getInstance();
    }
    
    public static synchronized FirestoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirestoreManager(context);
        }
        return instance;
    }
    
    public static synchronized FirestoreManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FirestoreManager не инициализирован. Сначала вызовите getInstance(Context)");
        }
        return instance;
    }
    
    // Получение данных текущего пользователя
    public void getCurrentUserData(UserCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        firestore.collection(USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onUserLoaded(user);
                    } else {
                        callback.onError("Данные пользователя не найдены");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Ошибка получения данных: " + e.getMessage()));
    }
    
    // Обновление данных пользователя
    public Task<Void> updateUserData(User user) {
        return firestore.collection(USERS_COLLECTION)
                .document(user.getUid())
                .set(user.toMap());
    }
    
    // =================== Методы для работы с заметками ===================
    
    // Сохранение заметки
    public void saveNote(String content, FirebaseAuthManager.AuthCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("userId", currentUser.getUid());
        noteData.put("content", content);
        noteData.put("timestamp", System.currentTimeMillis());
        
        firestore.collection(NOTES_COLLECTION)
                .add(noteData)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка сохранения заметки: " + e.getMessage());
                });
    }
    
    // Получение заметок пользователя
    public void getUserNotes(NotesCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        firestore.collection(NOTES_COLLECTION)
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserNote> notesList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        UserNote note = document.toObject(UserNote.class);
                        note.setId(document.getId());
                        notesList.add(note);
                    }
                    callback.onNotesLoaded(notesList);
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка загрузки заметок: " + e.getMessage());
                });
    }
    
    // Обновление заметки
    public void updateNote(UserNote note, FirebaseAuthManager.AuthCallback callback) {
        if (note.getId() == null) {
            callback.onError("Некорректный ID заметки");
            return;
        }
        
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("userId", note.getUserId());
        noteData.put("content", note.getContent());
        noteData.put("timestamp", note.getTimestamp());
        
        firestore.collection(NOTES_COLLECTION)
                .document(note.getId())
                .update(noteData)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка обновления заметки: " + e.getMessage());
                });
    }
    
    // Удаление заметки
    public void deleteNote(String noteId, FirebaseAuthManager.AuthCallback callback) {
        firestore.collection(NOTES_COLLECTION)
                .document(noteId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка удаления заметки: " + e.getMessage());
                });
    }

    // Получение списка уроков
    public void getLessons(LessonsCallback callback) {
        firestore.collection("lessons")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Lesson> lessons = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Lesson lesson = document.toObject(Lesson.class);
                        if (lesson != null) {
                            lesson.setId(document.getId());
                            lessons.add(lesson);
                        }
                    }
                    callback.onLessonsLoaded(lessons);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Сохранение заметки с возвращением ID
    public void saveNoteAndGetId(String title, String content, SaveNoteCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("userId", currentUser.getUid());
        noteData.put("title", title);
        noteData.put("content", content);
        noteData.put("timestamp", System.currentTimeMillis());
        
        firestore.collection(NOTES_COLLECTION)
                .add(noteData)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    callback.onError("Ошибка сохранения заметки: " + e.getMessage());
                });
    }
    
    // For backward compatibility
    public void saveNoteAndGetId(String content, SaveNoteCallback callback) {
        saveNoteAndGetId("", content, callback);
    }
} 