package com.example.music_land.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_land.R;
import com.example.music_land.data.model.GenreItem;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    
    private List<GenreItem> genreList;
    private Context context;
    private OnGenreClickListener listener;
    
    public interface OnGenreClickListener {
        void onGenreClick(GenreItem genre);
    }
    
    public GenreAdapter(Context context, List<GenreItem> genreList, OnGenreClickListener listener) {
        this.context = context;
        this.genreList = genreList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        GenreItem genre = genreList.get(position);
        holder.genreName.setText(genre.getName());
        
        // Здесь используем безопасную установку изображения с проверкой
        try {
            holder.genreIcon.setImageResource(genre.getIconResourceId());
        } catch (Exception e) {
            // В случае ошибки используем стандартное изображение
            holder.genreIcon.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onGenreClick(genre);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return genreList != null ? genreList.size() : 0;
    }
    
    static class GenreViewHolder extends RecyclerView.ViewHolder {
        ImageView genreIcon;
        TextView genreName;
        
        GenreViewHolder(View itemView) {
            super(itemView);
            genreIcon = itemView.findViewById(R.id.genreIcon);
            genreName = itemView.findViewById(R.id.genreName);
        }
    }
} 