package com.example.music_land.data.model;

/**
 * Модель данных для элементов жанра музыки
 */
public class GenreItem {
    private String id;          // Уникальный идентификатор жанра
    private String name;        // Название жанра
    private int iconResourceId; // Идентификатор ресурса иконки
    
    /**
     * Конструктор по умолчанию
     */
    public GenreItem() {
    }
    
    /**
     * Конструктор с параметрами
     * 
     * @param id Идентификатор жанра
     * @param name Название жанра
     * @param iconResourceId Идентификатор ресурса иконки
     */
    public GenreItem(String id, String name, int iconResourceId) {
        this.id = id;
        this.name = name;
        this.iconResourceId = iconResourceId;
    }
    
    /**
     * Получение идентификатора жанра
     */
    public String getId() {
        return id;
    }
    
    /**
     * Установка идентификатора жанра
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Получение названия жанра
     */
    public String getName() {
        return name;
    }
    
    /**
     * Установка названия жанра
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Получение идентификатора ресурса иконки
     */
    public int getIconResourceId() {
        return iconResourceId;
    }
    
    /**
     * Установка идентификатора ресурса иконки
     */
    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }
} 