package com.example.proyectopalomero.ui.theme.screens.Tiempo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.Constant
import com.example.proyectopalomero.data.Api.NetworkResponse
import com.example.proyectopalomero.data.Api.RetroFitInstance
import com.example.proyectopalomero.data.model.WeatherModel
import kotlinx.coroutines.launch

//Funcion para obtener los datos de la API
class WeatherViewModel: ViewModel() {

    private  val weatherApi = RetroFitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult : LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    //Funcion para obtener los datos
    fun getData(ciudad: String){
        _weatherResult.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey,ciudad)
                //Si la respuesta es exitosa cargamos los datos
                if(response.isSuccessful){
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                    //Si la respuesta no es exitosa se muestra un error
                }else{
                    _weatherResult.value = NetworkResponse.Error("Fallo al cargar los datos")
                }
            }catch (e : Exception){
                _weatherResult.value = NetworkResponse.Error("Fallo al cargar los datos")
            }
        }
    }

    fun limpiarDatos(){
        _weatherResult.value = NetworkResponse.Empty
    }


}