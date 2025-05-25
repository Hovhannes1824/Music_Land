package com.example.music_land;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioTrack;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.music_land.databinding.FragmentMetronomeBinding;
import com.example.music_land.utils.SoundUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MetronomeFragment extends Fragment {

    private FragmentMetronomeBinding binding;
    
    // Метроном
    private boolean isMetronomeRunning = false;
    private int tempo = 120;
    private ScheduledExecutorService metronomeExecutor;
    private ScheduledFuture<?> metronomeScheduledFuture;
    private AudioTrack tickTrack;
    
    // Настройки
    private SharedPreferences sharedPreferences;
    private static final String METRONOME_PREF = "music_land_metronome";
    private static final String TEMPO_KEY = "metronome_tempo";
    
    // Константы для темпа
    private static final int MIN_TEMPO = 40;
    private static final int MAX_TEMPO = 248;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMetronomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Инициализация исполнителя метронома
        metronomeExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Хранилище для настроек
        sharedPreferences = requireActivity().getSharedPreferences(METRONOME_PREF, Context.MODE_PRIVATE);
        
        // Загружаем сохраненные настройки метронома
        tempo = sharedPreferences.getInt(TEMPO_KEY, 120);
        
        setupMetronome();
    }
    
    private void setupMetronome() {
        // Инициализация звука метронома
        tickTrack = SoundUtils.generateClick(50);
        
        // Настройка SeekBar для темпа
        binding.tempoSeekBar.setProgress(tempo - MIN_TEMPO);
        updateTempoText();
        
        // Настройка поля ввода темпа
        binding.tempoEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        int newTempo = Integer.parseInt(s.toString());
                        if (newTempo >= MIN_TEMPO && newTempo <= MAX_TEMPO) {
                            // Обновляем только если значение изменилось
                            if (tempo != newTempo) {
                                tempo = newTempo;
                                binding.tempoSeekBar.setProgress(tempo - MIN_TEMPO);
                                
                                // Сохраняем настройки
                                sharedPreferences.edit().putInt(TEMPO_KEY, tempo).apply();
                                
                                // Если метроном запущен, обновляем его темп
                                if (isMetronomeRunning) {
                                    restartMetronome();
                                }
                            }
                        } else {
                            // Если значение вне допустимого диапазона, показываем сообщение
                            Toast.makeText(requireContext(), 
                                "Темп должен быть от " + MIN_TEMPO + " до " + MAX_TEMPO, 
                                Toast.LENGTH_SHORT).show();
                            // Возвращаем предыдущее корректное значение
                            updateTempoText();
                        }
                    } catch (NumberFormatException e) {
                        // Если введено некорректное число, возвращаем предыдущее значение
                        updateTempoText();
                    }
                }
            }
        });
        
        binding.tempoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tempo = progress + MIN_TEMPO;
                updateTempoText();
                
                // Если метроном запущен, обновляем его темп
                if (isMetronomeRunning) {
                    restartMetronome();
                }
                
                // Сохраняем настройки
                sharedPreferences.edit().putInt(TEMPO_KEY, tempo).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Кнопка запуска/остановки метронома
        binding.startStopButton.setOnClickListener(v -> {
            if (isMetronomeRunning) {
                stopMetronome();
                binding.startStopButton.setText("Запустить");
            } else {
                startMetronome();
                binding.startStopButton.setText("Остановить");
            }
        });
    }
    
    private void updateTempoText() {
        binding.tempoEditText.setText(String.valueOf(tempo));
    }
    
    private void startMetronome() {
        isMetronomeRunning = true;
        
        // Вычисляем интервал между ударами в миллисекундах
        long intervalMs = (long) (60000.0 / tempo);
        
        // Запускаем задачу метронома с фиксированной периодичностью
        metronomeScheduledFuture = metronomeExecutor.scheduleAtFixedRate(() -> {
            if (tickTrack != null) {
                tickTrack.stop();
                tickTrack.reloadStaticData();
                tickTrack.play();
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
    }
    
    private void stopMetronome() {
        isMetronomeRunning = false;
        
        if (metronomeScheduledFuture != null) {
            metronomeScheduledFuture.cancel(true);
            metronomeScheduledFuture = null;
        }
        
        if (tickTrack != null) {
            tickTrack.stop();
        }
    }
    
    private void restartMetronome() {
        stopMetronome();
        startMetronome();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopMetronome();
        if (tickTrack != null) {
            tickTrack.release();
            tickTrack = null;
        }
        binding = null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (metronomeExecutor != null) {
            metronomeExecutor.shutdownNow();
            metronomeExecutor = null;
        }
    }
} 