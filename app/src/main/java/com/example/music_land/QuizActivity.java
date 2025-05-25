package com.example.music_land;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.music_land.data.QuizData;
import com.example.music_land.data.model.QuizQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private Button submitButton;
    private TextView scoreTextView;
    private TextView timerTextView;
    
    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private ArrayList<String> wrongAnswers = new ArrayList<>();
    
    private static final int QUESTION_TIMEOUT_SECONDS = 15;
    private CountDownTimer questionTimer;
    
    private String genre;
    private String genreName;
    private String difficulty;
    private int shuffledCorrectIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        genre = getIntent().getStringExtra("genre");
        genreName = getIntent().getStringExtra("genre_name");
        difficulty = getIntent().getStringExtra("difficulty");
        if (genreName == null) genreName = genre;
        
        // Получаем вопросы с учетом жанра и сложности
        questions = getQuestionsByDifficulty(genre, difficulty);

        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        submitButton = findViewById(R.id.submitButton);
        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        
        // Устанавливаем заголовок с указанием жанра и сложности
        setTitle(genreName + " - " + difficulty);

        displayQuestion();

        submitButton.setOnClickListener(v -> checkAnswer());
    }
    
    // Получение вопросов с учетом сложности
    private List<QuizQuestion> getQuestionsByDifficulty(String genre, String difficulty) {
        String quizDataKey = getQuizDataKey(genre);
        List<QuizQuestion> allQuestions = QuizData.getQuestionsForGenre(quizDataKey);
        List<QuizQuestion> filteredQuestions = new ArrayList<>();
        
        // Фильтруем вопросы по сложности и ограничиваем их количество
        int maxQuestions;
        if ("Легкий".equals(difficulty)) {
            maxQuestions = 5;
        } else if ("Средний".equals(difficulty)) {
            maxQuestions = 10;
        } else {
            maxQuestions = 15;
        }
        
        int count = 0;
        if (allQuestions != null) {
            for (QuizQuestion question : allQuestions) {
                filteredQuestions.add(question);
                count++;
                if (count >= maxQuestions) {
                    break;
                }
            }
        }
        
        return filteredQuestions;
    }
    
    // Сопоставление id жанра и ключа QuizData
    private String getQuizDataKey(String genreId) {
        if (genreId == null) return null;
        switch (genreId.toLowerCase()) {
            case "rock": return "Rock";
            case "pop": return "Pop";
            case "hiphop": return "Hip Hop";
            case "jazz": return "Jazz";
            case "classical": return "Classical";
            case "metal": return "Metal";
            default: return genreId;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (questionTimer != null) {
            questionTimer.cancel();
        }
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            QuizQuestion currentQuestion = questions.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestion());

            optionsRadioGroup.removeAllViews();
            String[] options = currentQuestion.getOptions();
            int correctIndex = currentQuestion.getCorrectAnswerIndex();

            // Перемешиваем варианты и определяем новый индекс правильного ответа
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < options.length; i++) indices.add(i);
            Collections.shuffle(indices, new Random(System.nanoTime()));
            shuffledCorrectIndex = -1;
            for (int i = 0; i < indices.size(); i++) {
                int origIdx = indices.get(i);
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(options[origIdx]);
                radioButton.setId(i);
                radioButton.setTextColor(android.graphics.Color.parseColor("#5D4037"));
                radioButton.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#5D4037")));
                optionsRadioGroup.addView(radioButton);
                if (origIdx == correctIndex) {
                    shuffledCorrectIndex = i;
                }
            }

            // Устанавливаем обработчик выбора RadioButton после их создания
            optionsRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) group.getChildAt(i);
                    if (rb.getId() == checkedId) {
                        rb.setTextColor(android.graphics.Color.parseColor("#000000"));
                        rb.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#000000")));
                    } else {
                        rb.setTextColor(android.graphics.Color.parseColor("#5D4037"));
                        rb.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#5D4037")));
                    }
                }
            });

            scoreTextView.setText("Счет: " + score);
            startQuestionTimer();
        } else {
            finishQuiz();
        }
    }
    
    private void startQuestionTimer() {
        // Отменить предыдущий таймер, если был
        if (questionTimer != null) {
            questionTimer.cancel();
        }
        
        questionTimer = new CountDownTimer(QUESTION_TIMEOUT_SECONDS * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                timerTextView.setText(secondsRemaining + " сек");
                
                // Меняем цвет, когда остается мало времени
                if (secondsRemaining <= 5) {
                    timerTextView.setTextColor(android.graphics.Color.parseColor("#B3261E")); // коричнево-красный
                } else {
                    timerTextView.setTextColor(android.graphics.Color.parseColor("#8D6748")); // коричневый
                }
            }

            @Override
            public void onFinish() {
                timerTextView.setText("0 сек");
                Toast.makeText(QuizActivity.this, "Время истекло!", Toast.LENGTH_SHORT).show();
                
                // Считаем, что пользователь не ответил, и переходим к следующему вопросу
                QuizQuestion currentQuestion = questions.get(currentQuestionIndex);
                String wrongAnswer = "Вопрос: " + currentQuestion.getQuestion() + "\n" +
                                   "Ваш ответ: Нет ответа (время истекло)\n" +
                                   "Правильный ответ: " + currentQuestion.getOptions()[currentQuestion.getCorrectAnswerIndex()];
                wrongAnswers.add(wrongAnswer);
                
                currentQuestionIndex++;
                optionsRadioGroup.clearCheck();
                displayQuestion();
            }
        };
        
        questionTimer.start();
    }

    private void checkAnswer() {
        int selectedId = optionsRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Пожалуйста, выберите ответ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (questionTimer != null) {
            questionTimer.cancel();
        }
        QuizQuestion currentQuestion = questions.get(currentQuestionIndex);
        if (selectedId == shuffledCorrectIndex) {
            score += currentQuestion.getPoints();
            Toast.makeText(this, "Правильно! +" + currentQuestion.getPoints() + " очков", Toast.LENGTH_SHORT).show();
            // Подсветить выбранный кружочек и текст зелёным
            RadioButton selectedRadio = (RadioButton) optionsRadioGroup.getChildAt(selectedId);
            if (selectedRadio != null) {
                selectedRadio.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
                selectedRadio.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
                selectedRadio.invalidate();
            }
            submitButton.setEnabled(false);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                currentQuestionIndex++;
                optionsRadioGroup.clearCheck();
                displayQuestion();
                submitButton.setEnabled(true);
            }, 1000); // 1 секунда
            return;
        } else {
            String[] options = currentQuestion.getOptions();
            // Найти правильный текст ответа
            String correctText = options[currentQuestion.getCorrectAnswerIndex()];
            String wrongAnswer = "Вопрос: " + currentQuestion.getQuestion() + "\n" +
                               "Ваш ответ: " + ((RadioButton) optionsRadioGroup.getChildAt(selectedId)).getText() + "\n" +
                               "Правильный ответ: " + correctText;
            wrongAnswers.add(wrongAnswer);
            Toast.makeText(this, "Неправильно! Правильный ответ: " + correctText, Toast.LENGTH_SHORT).show();
            // Подсветить кружочек правильного ответа зелёным
            RadioButton correctRadio = (RadioButton) optionsRadioGroup.getChildAt(shuffledCorrectIndex);
            if (correctRadio != null) {
                correctRadio.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
                correctRadio.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
                correctRadio.invalidate();
            }
            // Подсветить выбранный неправильный кружочек красным
            RadioButton selectedRadio = (RadioButton) optionsRadioGroup.getChildAt(selectedId);
            if (selectedRadio != null) {
                selectedRadio.setTextColor(android.graphics.Color.parseColor("#FF5252"));
                selectedRadio.setButtonTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF5252")));
                selectedRadio.invalidate();
            }
            submitButton.setEnabled(false);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                currentQuestionIndex++;
                optionsRadioGroup.clearCheck();
                displayQuestion();
                submitButton.setEnabled(true);
            }, 1000); // 1 секунда
            return;
        }
    }

    private void finishQuiz() {
        Intent intent = new Intent(this, QuizResultsActivity.class);
        intent.putExtra("finalScore", score);
        intent.putStringArrayListExtra("wrongAnswers", wrongAnswers);
        intent.putExtra("genre", genre);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("genre_name", genreName);
        startActivity(intent);
        finish();
    }
} 