package com.example.music_land.data.model;

import com.google.firebase.firestore.Exclude;

public class Lesson {
    private String id;
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private int order;
    private String category;
    private int durationMinutes;
    private boolean hasQuiz;

    // Empty constructor for Firestore
    public Lesson() {
    }

    public Lesson(String title, String description, String content, String imageUrl,
                 int order, String category, int durationMinutes, boolean hasQuiz) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.imageUrl = imageUrl;
        this.order = order;
        this.category = category;
        this.durationMinutes = durationMinutes;
        this.hasQuiz = hasQuiz;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isHasQuiz() {
        return hasQuiz;
    }

    public void setHasQuiz(boolean hasQuiz) {
        this.hasQuiz = hasQuiz;
    }
} 