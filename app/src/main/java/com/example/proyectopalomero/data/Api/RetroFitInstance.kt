package com.example.proyectopalomero.data.Api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Objeto que contiene la información de la API
object RetroFitInstance {

    //URL de la API
    private val baseUrl = "https://api.weatherapi.com"

    //Instancia de la API
    val weatherApi : WeatherApi = getInstance().create(WeatherApi::class.java)

    //Función que devuelve la instancia de la API
    private fun getInstance() : Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}