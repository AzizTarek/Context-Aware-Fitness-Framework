package com.example.myapplication.components.database.cache.AGSRapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainActivity
import com.example.myapplication.components.other.FrameworkDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AgsrAppViewModel: ViewModel() {

    private val allAgsrAppData : AgsrApp
    private val repository: AgsrAppRepository

    init {
        val agsrAppDAO = FrameworkDatabase.getDatabase(MainActivity.Constants.context).agsrAppDao()
        repository = AgsrAppRepository(agsrAppDAO)
        allAgsrAppData = repository.agsrAppData
    }

    fun addAgsrAppItem(agsrApp: AgsrApp)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAgsrApp(agsrApp)
        }
    }
    fun getAll(): AgsrApp {
        return repository.agsrAppData
    }
    fun deleteAll() {
        repository.deleteAllAgsrAppItems()
    }

}