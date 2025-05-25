package com.example.music_land.data.model;

import java.util.HashMap;
import java.util.Map;

public class UserNote {
    private String id;
    private String userId;
    private String title; // Название песни
    private String content; // Текст песни
    private long timestamp;

    // Empty constructor for Firestore
    public UserNote() {
    }

    public UserNote(String userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    // For backward compatibility
    public UserNote(String userId, String content) {
        this.userId = userId;
        this.title = ""; // Empty title for old notes
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    // Convert to Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("title", title);
        map.put("content", content);
        map.put("timestamp", timestamp);
        return map;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 