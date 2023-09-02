package com.example.dz15.response

import com.example.dz15.model.Weather
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("forecast.json")
    fun getWeatherTodayRX(
        @Query("key") apikey: String,
        @Query("q") city: String,
        @Query("days") days: String = "1",
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no",
    ): Single<Weather>
}