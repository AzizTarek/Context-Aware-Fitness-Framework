package com.example.myapplication.components.database.cache.AirPollution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainActivity
import com.example.myapplication.components.other.FrameworkDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AirPollutionViewModel: ViewModel() {

    private val allAirPollutionData : AirPollution
    private val repository: AirPollutionRepository

    init {
        val airPollutionDAO = FrameworkDatabase.getDatabase(MainActivity.Constants.context).airPollutionDao()
        repository = AirPollutionRepository(airPollutionDAO)
        allAirPollutionData = repository.allAirPollutionData
    }

    fun addAirPollutionItem(airPollution: AirPollution)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAirPollution(airPollution)
        }
    }
    fun getAll(): AirPollution {
        return repository.allAirPollutionData
    }
    fun deleteAll() {
        repository.deleteAllAirPollutionItems()
    }

}