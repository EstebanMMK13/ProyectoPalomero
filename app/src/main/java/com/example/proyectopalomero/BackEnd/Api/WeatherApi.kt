package com.example.proyectopalomero.BackEnd.Api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//Interfaz de la API donde se obtienen los datos del clima mediante una consulta
interface WeatherApi {

    @GET("/v1/current.json")
    suspend fun getWeather(
        @Query("key") apikey : String,
        @Query("q") ciudad : String
    ): Response<WeatherModel>
}