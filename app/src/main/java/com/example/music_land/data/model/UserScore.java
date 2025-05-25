package com.example.music_land.data.model;

public class UserScore {
    private String userId;
    private String displayName;
    private int score;
    private int rank;

    public UserScore(String userId, String displayName, int score, int rank) {
        this.userId = userId;
        this.displayName = displayName;
        this.score = score;
        this.rank = rank;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getScore() {
        return score;
    }

    public int getRank() {
        return rank;
    }
} 