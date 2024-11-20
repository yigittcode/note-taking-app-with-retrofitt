package com.yigit.finalproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    fun getWeather(@Query("q") city: String, @Query("appid") apiKey: String): Call<WeatherResponse>
}
