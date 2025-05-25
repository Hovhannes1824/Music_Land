package com.example.music_land;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_land.adapter.ToolsAdapter;
import com.example.music_land.data.model.Tool;
import com.example.music_land.databinding.FragmentToolsMenuBinding;

import java.util.ArrayList;
import java.util.List;

public class ToolsFragment extends Fragment implements ToolsAdapter.OnToolClickListener {

    private FragmentToolsMenuBinding binding;
    private List<Tool> tools = new ArrayList<>();
    private ToolsAdapter toolsAdapter;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentToolsMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareToolsList();
        toolsAdapter = new ToolsAdapter(tools, this);
        binding.toolsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.toolsRecyclerView.setAdapter(toolsAdapter);
    }
    
    private void prepareToolsList() {
        tools.clear();
        tools.add(new Tool("Метроном", "Ритмический инструмент для музыкантов", Tool.TYPE_METRONOME, R.drawable.ic_metronome));
        tools.add(new Tool("Заметки", "Пиши тексты песен и сохраняй их", Tool.TYPE_NOTES, R.drawable.ic_note));
    }
    
    @Override
    public void onToolClick(Tool tool, int position) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        
        switch (tool.getType()) {
            case Tool.TYPE_METRONOME:
                transaction.replace(R.id.fragmentContainer, new MetronomeFragment());
                break;
            case Tool.TYPE_NOTES:
                transaction.replace(R.id.fragmentContainer, new NotesFragment());
                break;
        }
        
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 