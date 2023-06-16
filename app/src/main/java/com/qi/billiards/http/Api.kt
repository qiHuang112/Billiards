package com.qi.billiards.http

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import java.util.concurrent.TimeUnit

val api by lazy {
    val retrofit = retrofit2.Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(10, TimeUnit.SECONDS) // 设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS) // 设置写入超时时间
                .addInterceptor(
                    HttpLoggingInterceptor {
                        Log.d("githubApi", it)
                    }.setLevel(HttpLoggingInterceptor.Level.BODY)
                ).build()
        )
        .baseUrl("https://github.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    retrofit.create(Api::class.java)
}

interface Api {

    @Streaming
    @GET("https://github.com/qiHuang112/Billiards/releases/download/v1.0/{key}")
    suspend fun getHistory(@Path("key") key: String): ResponseBody

}