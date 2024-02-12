package com.example.myapplication.components.database.cache.Weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainActivity
import com.example.myapplication.components.other.FrameworkDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {

    private val allWeatherData :Weather
    private val repository: WeatherRepository

    init {
        val weatherDAO = FrameworkDatabase.getDatabase(MainActivity.Constants.context).weatherDao()
        repository = WeatherRepository(weatherDAO)
        allWeatherData = repository.allWeatherData
    }

    fun addWeatherItem(weather: Weather)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertWeather(weather)
        }
    }
    fun getAll(): Weather {
        return repository.allWeatherData
    }
    fun deleteAll() {
        repository.deleteAllWeatherItems()
    }

}