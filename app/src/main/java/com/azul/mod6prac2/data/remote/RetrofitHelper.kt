package com.azul.mod6prac2.data.remote

import com.azul.mod6prac2.utils.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    fun getRetrofit(): Retrofit {

        val apiKey = "AIzaSyDW3zbpWFqDjDQruTpvTQYI0r_zcJpqg6M"

        val interceptor = HttpLoggingInterceptor().apply {
            //Para que el interceptor me dé mensajes a nivel del
            //body de la operación de la red
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            addInterceptor(ApiKeyInterceptor(apiKey))
        }.build()

        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
