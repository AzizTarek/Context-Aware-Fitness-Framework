package com.example.myapplication.components.database.cache.Weather

class WeatherRepository(private val weatherDAO: WeatherDAO) {

    val allWeatherData: Weather = weatherDAO.getAllWeatherData()

    suspend fun insertWeather(weather: Weather){
        weatherDAO.insert(weather)
    }
    suspend fun updateWeather(weather: Weather){
        weatherDAO.update(weather)
    }
    suspend fun deleteWeather(weather: Weather){
        weatherDAO.delete(weather)
    }
    fun getAllWeatherItems() {
        weatherDAO.getAllWeatherData()
    }
    fun deleteAllWeatherItems() {
        weatherDAO.deleteAllWeatherData()
    }
}