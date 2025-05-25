package com.example.music_land.data.model;

public class MusicTool {
    private String name;
    private String description;

    public MusicTool(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
} 