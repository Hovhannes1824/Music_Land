package com.example.music_land.data.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private int totalScore;
    private int quizzesCompleted;
    private int lessonsCompleted;

    // Пустой конструктор для Firebase
    public User() {
    }

    public User(String uid, String email, String displayName) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.totalScore = 0;
        this.quizzesCompleted = 0;
        this.lessonsCompleted = 0;
    }

    // Конвертация в Map для сохранения в Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("email", email);
        map.put("displayName", displayName);
        map.put("totalScore", totalScore);
        map.put("quizzesCompleted", quizzesCompleted);
        map.put("lessonsCompleted", lessonsCompleted);
        return map;
    }

    // Геттеры и сеттеры
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getQuizzesCompleted() {
        return quizzesCompleted;
    }

    public void setQuizzesCompleted(int quizzesCompleted) {
        this.quizzesCompleted = quizzesCompleted;
    }

    public int getLessonsCompleted() {
        return lessonsCompleted;
    }

    public void setLessonsCompleted(int lessonsCompleted) {
        this.lessonsCompleted = lessonsCompleted;
    }

    // Метод для увеличения очков
    public void addScore(int points) {
        this.totalScore += points;
    }

    // Метод для увеличения счетчика пройденных тестов
    public void incrementQuizzesCompleted() {
        this.quizzesCompleted++;
    }

    // Метод для увеличения счетчика изученных уроков
    public void incrementLessonsCompleted() {
        this.lessonsCompleted++;
    }
} 