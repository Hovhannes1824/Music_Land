package com.example.music_land.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_land.R;
import com.example.music_land.data.model.Tool;

import java.util.List;

public class ToolsAdapter extends RecyclerView.Adapter<ToolsAdapter.ToolViewHolder> {
    
    private List<Tool> tools;
    private OnToolClickListener listener;
    
    public interface OnToolClickListener {
        void onToolClick(Tool tool, int position);
    }
    
    public ToolsAdapter(List<Tool> tools, OnToolClickListener listener) {
        this.tools = tools;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tool, parent, false);
        return new ToolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolViewHolder holder, int position) {
        Tool tool = tools.get(position);
        holder.bind(tool, position);
    }

    @Override
    public int getItemCount() {
        return tools.size();
    }
    
    class ToolViewHolder extends RecyclerView.ViewHolder {
        private TextView toolNameTextView;
        private TextView toolDescriptionTextView;
        private ImageView toolIconImageView;
        
        public ToolViewHolder(@NonNull View itemView) {
            super(itemView);
            toolNameTextView = itemView.findViewById(R.id.toolNameTextView);
            toolDescriptionTextView = itemView.findViewById(R.id.toolDescriptionTextView);
            toolIconImageView = itemView.findViewById(R.id.toolIconImageView);
        }
        
        public void bind(Tool tool, int position) {
            toolNameTextView.setText(tool.getName());
            toolDescriptionTextView.setText(tool.getDescription());
            toolIconImageView.setImageResource(tool.getIconResId());
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onToolClick(tool, position);
                }
            });
        }
    }
} 