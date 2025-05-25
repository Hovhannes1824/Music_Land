package com.example.music_land.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_land.R;
import com.example.music_land.data.model.UserNote;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<UserNote> notes;
    private OnNoteListener listener;

    public interface OnNoteListener {
        void onEditClicked(UserNote note, int position);
        void onDeleteClicked(UserNote note, int position);
    }

    public NoteAdapter(List<UserNote> notes, OnNoteListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        UserNote note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void updateData(List<UserNote> newNotes) {
        this.notes = newNotes;
        notifyDataSetChanged();
    }
    
    /**
     * Обновляет отдельную заметку в списке
     * @param updatedNote Обновленная заметка
     * @return Позиция заметки в списке или -1, если не найдена
     */
    public int updateNote(UserNote updatedNote) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(updatedNote.getId())) {
                notes.set(i, updatedNote);
                notifyItemChanged(i);
                return i;
            }
        }
        return -1;
    }

    /**
     * Добавляет новую заметку в начало списка
     * @param note Новая заметка
     */
    public void addNote(UserNote note) {
        notes.add(0, note);
        notifyItemInserted(0);
    }

    /**
     * Удаляет заметку из списка
     * @param noteId ID заметки для удаления
     * @return Позиция удаленной заметки или -1, если не найдена
     */
    public int removeNote(String noteId) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(noteId)) {
                notes.remove(i);
                notifyItemRemoved(i);
                return i;
            }
        }
        return -1;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView contentTextView;
        private TextView dateTextView;
        private ImageButton deleteButton;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditClicked(notes.get(position), position);
                }
            });
        }

        void bind(UserNote note) {
            // Форматирование даты
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            String dateStr = sdf.format(new Date(note.getTimestamp()));
            
            // Отображаем заголовок (если он есть)
            if (note.getTitle() != null && !note.getTitle().isEmpty()) {
                titleTextView.setText(note.getTitle());
                titleTextView.setVisibility(View.VISIBLE);
            } else {
                titleTextView.setVisibility(View.GONE);
            }
            
            // Ограничиваем текст для предпросмотра
            String preview = note.getContent();
            if (preview.length() > 150) {
                preview = preview.substring(0, 147) + "...";
            }
            
            contentTextView.setText(preview);
            dateTextView.setText(dateStr);
            
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClicked(note, getAdapterPosition());
                }
            });
        }
    }
} 