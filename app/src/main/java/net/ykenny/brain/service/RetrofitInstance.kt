package net.ykenny.brain.service

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.openai.com/v1/"
    private const val TOKEN = "API_KEY"

    private val interceptor = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private var client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor).addInterceptor { chain ->
            val newRequest: Request =
                chain.request().newBuilder().addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer $TOKEN").build()
            chain.proceed(newRequest)
        }.build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val openAiService: OpenAiService by lazy {
        retrofit.create(OpenAiService::class.java)
    }
}