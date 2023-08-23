package com.qi.billiards.http

import android.util.Log
import com.qi.billiards.bean.Game
import com.qi.billiards.ui.main.SettingsFragment
import com.qi.billiards.util.get
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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

val apiHost: String
    get() = get(SettingsFragment.SETTINGS_KEY + SettingsFragment.KEY_HOST, "34.64.103.91:8181")


interface Api {

    @GET("http://{host}/getAll/{name}")
    suspend fun getAll(@Path("host") host: String, @Path("name") name: String): AjaxResponse<Game>

    @POST("http://{host}/addGame")
    suspend fun addGame(@Path("host") host: String, @Body game: Game): AjaxResponse<*>

    @POST("http://{host}/addAllGame")
    suspend fun addAllGame(@Path("host") host: String, @Body games: List<Game>): AjaxResponse<*>

    @DELETE("http://{host}/removeAllGames/{name}")
    suspend fun removeAllGames(@Path("host") host: String, @Path("name") name: String): AjaxResponse<*>

    @GET("http://{host}/getSize/{name}")
    suspend fun getSize(@Path("host") host: String, @Path("name") name: String): AjaxResponse<*>


}