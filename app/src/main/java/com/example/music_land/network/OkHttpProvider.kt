package com.example.music_land.network

import android.util.Log
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Kotlin-класс провайдер для OkHttp клиента.
 * Kotlin лучше работает с новыми библиотеками и правильно импортирует их.
 */
object OkHttpProvider {
    private const val TAG = "OkHttpProvider"
    
    private var client: OkHttpClient? = null
    
    /**
     * Получение единственного экземпляра OkHttpClient
     */
    fun getClient(): OkHttpClient {
        if (client == null) {
            try {
                client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
                Log.d(TAG, "OkHttpClient успешно инициализирован")
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при инициализации OkHttpClient: ${e.message}", e)
                // Создаем клиент с минимальными настройками в случае ошибки
                client = OkHttpClient()
            }
        }
        return client!!
    }
} 