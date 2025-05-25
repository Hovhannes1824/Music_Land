package com.example.music_land;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.music_land.adapter.NoteAdapter;
import com.example.music_land.data.model.UserNote;
import com.example.music_land.databinding.FragmentNotesBinding;
import com.example.music_land.firebase.FirebaseAuthManager;
import com.example.music_land.firebase.FirestoreManager;
import com.example.music_land.firebase.SupabaseManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesFragment extends Fragment implements NoteAdapter.OnNoteListener {

    private FragmentNotesBinding binding;
    
    // Firebase
    private FirestoreManager firestoreManager;
    private FirebaseAuthManager authManager;
    private SupabaseManager supabaseManager;
    
    // Адаптер
    private NoteAdapter noteAdapter;
    private List<UserNote> notes = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Инициализация Firebase менеджеров
        firestoreManager = FirestoreManager.getInstance();
        authManager = FirebaseAuthManager.getInstance();
        // Отключаем Supabase
        // supabaseManager = SupabaseManager.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        android.util.Log.d("NotesFragment", "onViewCreated: настройка RecyclerView");
        
        // Настройка RecyclerView для заметок
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.notesRecyclerView.setLayoutManager(layoutManager);
        binding.notesRecyclerView.setHasFixedSize(true); // Улучшает производительность
        
        // Инициализация адаптера
        notes = new ArrayList<>();
        noteAdapter = new NoteAdapter(notes, this);
        binding.notesRecyclerView.setAdapter(noteAdapter);
        
        // Настраиваем анимацию
        binding.notesRecyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());
        
        // Устанавливаем текст по умолчанию для пустого состояния
        binding.emptyNotesTextView.setText("У вас пока нет заметок");
        
        setupNotes();
        
        // Проверяем авторизацию и загружаем заметки
        if (authManager.isUserLoggedIn()) {
            // Загрузка данных из Firebase
            loadNotes();
        } else {
            // Пользователь не авторизован, показываем сообщение
            binding.emptyNotesTextView.setText("Для доступа к заметкам необходимо войти в аккаунт");
            binding.emptyNotesTextView.setVisibility(View.VISIBLE);
            binding.notesRecyclerView.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Для доступа к заметкам необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
        }
    }
    
    //---------------------- Заметки ----------------------//
    private void setupNotes() {
        // Настройка кнопки добавления заметки
        binding.addNoteButton.setOnClickListener(v -> {
            // Проверяем, авторизован ли пользователь
            if (!authManager.isUserLoggedIn()) {
                Toast.makeText(requireContext(), "Для создания заметок необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Показываем диалог для создания новой заметки
            showNoteDialog("", "", -1); // Создание новой заметки
        });
    }
    
    /**
     * Обновляет UI в зависимости от наличия заметок
     */
    private void updateEmptyState() {
        android.util.Log.d("NotesFragment", "updateEmptyState: список размером " + notes.size());
        
        if (notes.isEmpty()) {
            android.util.Log.d("NotesFragment", "updateEmptyState: список пуст, показываем сообщение");
            binding.emptyNotesTextView.setVisibility(View.VISIBLE);
            binding.notesRecyclerView.setVisibility(View.GONE);
        } else {
            android.util.Log.d("NotesFragment", "updateEmptyState: список не пуст, скрываем сообщение");
            binding.emptyNotesTextView.setVisibility(View.GONE);
            binding.notesRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void loadNotes() {
        // Показываем индикатор загрузки
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
        
        String userId = authManager.getCurrentUser() != null ? 
                authManager.getCurrentUser().getUid() : "anon_user";
        
        // Используем только Firebase с простым запросом
            firestoreManager.getUserNotes(new FirestoreManager.NotesCallback() {
                @Override
                public void onNotesLoaded(List<UserNote> loadedNotes) {
                if (binding != null) {
                    binding.loadingProgressBar.setVisibility(View.GONE);
                    notes.clear();
                    notes.addAll(loadedNotes);
                    noteAdapter.notifyDataSetChanged();
                    updateEmptyState();
                    }
                }
                
                @Override
                public void onError(String errorMessage) {
                if (binding != null) {
                    binding.loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Ошибка загрузки заметок: " + errorMessage, Toast.LENGTH_SHORT).show();
                    loadLocalNotes();
                }
                }
            });
    }
    
    private void showNoteDialog(String title, String content, int position) {
        // Создаем диалог для ввода данных
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        
        // Настраиваем заголовок в зависимости от операции
        if (position == -1) {
            builder.setTitle("Новая песня");
        } else {
            builder.setTitle("Редактирование песни");
        }
        
        // Создаем View для нашего диалога
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_song_note, null);
        builder.setView(dialogView);
        
        // Находим наши поля ввода
        final EditText titleInput = dialogView.findViewById(R.id.titleEditText);
        final EditText lyricsInput = dialogView.findViewById(R.id.lyricsEditText);
        
        // Устанавливаем текущие значения, если они есть
        titleInput.setText(title);
        lyricsInput.setText(content);
        
        // Добавляем кнопки
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String songTitle = titleInput.getText().toString().trim();
            String songLyrics = lyricsInput.getText().toString().trim();
            
            if (songLyrics.isEmpty()) {
                Toast.makeText(requireContext(), "Введите текст песни", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (position == -1) {
                // Создание новой заметки
                saveNote(songTitle, songLyrics);
            } else {
                // Обновление существующей заметки
                updateNote(notes.get(position), songTitle, songLyrics);
            }
        });
        
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        
        // Показываем диалог
        builder.show();
    }
    
    // Для совместимости с интерфейсом OnNoteListener
    private void showNoteDialog(String content, int position) {
        UserNote note = position >= 0 ? notes.get(position) : null;
        String title = note != null ? note.getTitle() : "";
        showNoteDialog(title, content, position);
    }
    
    private void saveNote(String title, String content) {
        // Проверяем авторизацию
        if (!authManager.isUserLoggedIn()) {
            Toast.makeText(requireContext(), "Для сохранения заметок необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Показываем индикатор загрузки
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
        
        android.util.Log.d("NotesFragment", "Начинаем сохранение песни: " + title);
        
        // Добавляем дополнительный пробел после названия
        String formattedContent = content;
        
        firestoreManager.saveNoteAndGetId(title, formattedContent, new FirestoreManager.SaveNoteCallback() {
            @Override
            public void onSuccess(String noteId) {
                android.util.Log.d("NotesFragment", "Песня успешно сохранена с ID: " + noteId);
                
                if (binding != null) {
                    binding.loadingProgressBar.setVisibility(View.GONE);
                    
                    // Создаем новый объект заметки
                    UserNote newNote = new UserNote(authManager.getCurrentUser().getUid(), title, formattedContent);
                    newNote.setId(noteId);
                    newNote.setTimestamp(System.currentTimeMillis());
                    
                    android.util.Log.d("NotesFragment", "Добавляем песню в список (текущий размер: " + notes.size() + ")");
                    
                    // Используем новый метод адаптера
                    noteAdapter.addNote(newNote);
                    
                    // Обновляем состояние пустого списка
                    updateEmptyState();
                    
                    // Прокручиваем список к началу
                    binding.notesRecyclerView.scrollToPosition(0);
                    
                    android.util.Log.d("NotesFragment", "Песня добавлена (новый размер списка: " + notes.size() + ")");
                    
                    Toast.makeText(requireContext(), "Песня сохранена", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                android.util.Log.e("NotesFragment", "Ошибка сохранения песни: " + errorMessage);
                
                if (binding != null) {
                    binding.loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), 
                        "Ошибка сохранения: " + errorMessage, 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    // For backward compatibility
    private void saveNote(String content) {
        saveNote("", content);
    }
    
    private void updateNote(UserNote note, String title, String content) {
        // Проверяем авторизацию
        if (!authManager.isUserLoggedIn()) {
            Toast.makeText(requireContext(), "Для обновления заметок необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Показываем индикатор загрузки
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
        
        // Сохраняем старые значения на случай ошибки
        String oldTitle = note.getTitle();
        String oldContent = note.getContent();
        
        // Обновляем содержимое заметки
        note.setTitle(title);
        note.setContent(content);
        note.setTimestamp(System.currentTimeMillis());
        
        firestoreManager.updateNote(note, new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess() {
                if (binding != null) {
                    binding.loadingProgressBar.setVisibility(View.GONE);
                    
                    // Используем новый метод адаптера
                    int position = noteAdapter.updateNote(note);
                    
                    // Если заметка была успешно обновлена
                    if (position >= 0) {
                        Toast.makeText(requireContext(), "Песня обновлена", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (binding != null) {
                    // Возвращаем старые значения при ошибке
                    note.setTitle(oldTitle);
                    note.setContent(oldContent);
                    
                    binding.loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), 
                        "Ошибка обновления песни: " + errorMessage, 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    // For backward compatibility
    private void updateNote(UserNote note, String content) {
        updateNote(note, note.getTitle(), content);
    }
    
    @Override
    public void onEditClicked(UserNote note, int position) {
        // Показываем диалог просмотра песни
        showSongViewDialog(note, position);
    }
    
    /**
     * Показывает диалог с подробным просмотром песни
     */
    private void showSongViewDialog(UserNote note, int position) {
        // Создаем диалог для просмотра песни
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        
        // Создаем View для нашего диалога
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_song, null);
        builder.setView(dialogView);
        
        // Находим элементы UI
        TextView titleTextView = dialogView.findViewById(R.id.songTitleTextView);
        TextView lyricsTextView = dialogView.findViewById(R.id.songLyricsTextView);
        Button editButton = dialogView.findViewById(R.id.editSongButton);
        Button dismissButton = dialogView.findViewById(R.id.dismissButton);
        
        // Устанавливаем данные
        String title = note.getTitle();
        if (title == null || title.isEmpty()) {
            title = "Песня без названия";
        }
        titleTextView.setText(title);
        lyricsTextView.setText(note.getContent());
        
        // Создаем диалог
        AlertDialog dialog = builder.create();
        
        // Настраиваем кнопки
        editButton.setOnClickListener(v -> {
            dialog.dismiss();
            showNoteDialog(note.getTitle(), note.getContent(), position);
        });
        
        dismissButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
        
        // Показываем диалог
        dialog.show();
    }
    
    @Override
    public void onDeleteClicked(UserNote note, int position) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Удаление заметки")
            .setMessage("Вы действительно хотите удалить эту заметку?")
            .setPositiveButton("Удалить", (dialog, which) -> {
                // Проверяем авторизацию
                if (!authManager.isUserLoggedIn()) {
                    Toast.makeText(requireContext(), "Для удаления заметок необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
                    return;
                }
                
                binding.loadingProgressBar.setVisibility(View.VISIBLE);
                
                firestoreManager.deleteNote(note.getId(), new FirebaseAuthManager.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        if (binding != null) {
                            binding.loadingProgressBar.setVisibility(View.GONE);
                            
                            // Используем новый метод адаптера
                            noteAdapter.removeNote(note.getId());
                            
                            // После удаления проверяем, не опустел ли список
                            updateEmptyState();
                            
                            Toast.makeText(requireContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        if (binding != null) {
                            binding.loadingProgressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), 
                                "Ошибка удаления заметки: " + errorMessage, 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            })
            .setNegativeButton("Отмена", null)
            .show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Добавляем метод для загрузки локальных заметок
    private void loadLocalNotes() {
        try {
            // Получаем директорию с заметками
            File notesDir = new File(requireContext().getFilesDir(), "notes");
            if (!notesDir.exists()) {
                binding.loadingProgressBar.setVisibility(View.GONE);
                binding.emptyNotesTextView.setVisibility(View.VISIBLE);
                return;
            }
            
            // Получаем список файлов заметок
            File[] noteFiles = notesDir.listFiles();
            if (noteFiles == null || noteFiles.length == 0) {
                binding.loadingProgressBar.setVisibility(View.GONE);
                binding.emptyNotesTextView.setVisibility(View.VISIBLE);
                return;
            }
            
            // Сортируем файлы по дате изменения (от новых к старым)
            Arrays.sort(noteFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            
            // Создаем список заметок
            notes.clear();
            for (File file : noteFiles) {
                // Читаем содержимое файла
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
                
                // Создаем объект заметки
                UserNote note = new UserNote();
                note.setId(file.getName());
                note.setUserId("local_user");
                note.setContent(content.toString().trim());
                note.setTimestamp(file.lastModified());
                
                notes.add(note);
            }
            
            // Обновляем UI
            noteAdapter.notifyDataSetChanged();
            binding.loadingProgressBar.setVisibility(View.GONE);
            
            if (notes.isEmpty()) {
                binding.emptyNotesTextView.setVisibility(View.VISIBLE);
            } else {
                binding.emptyNotesTextView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            binding.loadingProgressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Ошибка загрузки локальных заметок: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            binding.emptyNotesTextView.setVisibility(View.VISIBLE);
        }
    }
} 