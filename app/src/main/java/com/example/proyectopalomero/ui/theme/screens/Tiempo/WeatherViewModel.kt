package com.example.proyectopalomero.ui.theme.screens.Tiempo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.Constant
import com.example.proyectopalomero.data.Api.RetroFitInstance
import com.example.proyectopalomero.data.model.WeatherModel
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.errorGeneral
import com.example.proyectopalomero.data.utils.errorSnackBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//Funcion para obtener los datos de la API
class WeatherViewModel: ViewModel() {

    private  val weatherApi = RetroFitInstance.weatherApi

    private val _estadoUI = MutableStateFlow<EstadoUI<WeatherModel>>(EstadoUI.Vacio)
    val estadoUI: StateFlow<EstadoUI<WeatherModel>> = _estadoUI.asStateFlow()

    //Funcion para obtener los datos
    fun getData(ciudad: String){
        _estadoUI.value = EstadoUI.Cargando
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey,ciudad)
                //Si la respuesta es exitosa cargamos los datos
                if(response.isSuccessful){
                    response.body()?.let {
                        _estadoUI.value = EstadoUI.Exito(it)
                    }
                }else{
                    _estadoUI.value = EstadoUI.Error("Error al cargar los datos", errorGeneral)
                }
            }catch (e : Exception){
                _estadoUI.value = EstadoUI.Error("Error al cargar los datos",errorGeneral)
            }
        }
    }

    fun limpiarDatos(){
        _estadoUI.value = EstadoUI.Vacio
    }


}