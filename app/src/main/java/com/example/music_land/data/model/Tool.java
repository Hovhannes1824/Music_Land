package com.example.music_land.data.model;

/**
 * Класс модели для представления музыкальных инструментов
 */
public class Tool {
    private String name;
    private String description;
    private int type;
    private int iconResId;
    
    public static final int TYPE_METRONOME = 1;
    public static final int TYPE_NOTES = 2;
    public static final int TYPE_RECORDER = 3;
    
    public Tool(String name, String description, int type, int iconResId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.iconResId = iconResId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getType() {
        return type;
    }
    
    public int getIconResId() {
        return iconResId;
    }
} 