package com.example.music_land.data.model;

public class QuizQuestion {
    private String question;
    private String[] options;
    private int correctAnswerIndex;
    private int points;

    public QuizQuestion(String question, String[] options, int correctAnswerIndex, int points) {
        this.question = question;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.points = points;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public int getPoints() {
        return points;
    }
} 