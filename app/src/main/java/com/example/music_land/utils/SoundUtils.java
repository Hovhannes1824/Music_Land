package com.example.music_land.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class SoundUtils {
    
    // Генерация звука щелчка
    public static AudioTrack generateClick(int durationMs) {
        int sampleRate = 44100;
        int count = (int)(sampleRate * durationMs / 1000);
        short[] samples = new short[count];
        
        double freqHz = 2000; // Высокая частота для щелчка
        
        // Генерируем сигнал щелчка с быстрым затуханием
        for(int i = 0; i < count; i++) {
            double time = i / (double) sampleRate;
            double decay = Math.exp(-time * 50); // Коэффициент затухания
            samples[i] = (short) (Short.MAX_VALUE * Math.sin(2 * Math.PI * freqHz * time) * decay);
        }
        
        return createTrack(samples, sampleRate);
    }
    
    // Вспомогательный метод для создания AudioTrack из сэмплов
    private static AudioTrack createTrack(short[] samples, int sampleRate) {
        AudioTrack track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                samples.length * 2, // Размер в байтах (short = 2 байта)
                AudioTrack.MODE_STATIC);
        
        track.write(samples, 0, samples.length);
        
        return track;
    }
} 