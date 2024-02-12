package com.example.myapplication.components.database.cache.AGSRapp

class AgsrAppRepository(private val agsrAppDAO: AgsrAppDAO) {

    val agsrAppData: AgsrApp = agsrAppDAO.getAllAgsrAppData()

    suspend fun insertAgsrApp(agsrApp: AgsrApp){
        agsrAppDAO.insert(agsrApp)
    }
    suspend fun updateAgsrApp(agsrApp: AgsrApp){
        agsrAppDAO.update(agsrApp)
    }
    suspend fun deleteAgsrApp(agsrApp: AgsrApp){
        agsrAppDAO.delete(agsrApp)
    }
    fun getAllAgsrAppItems() {
        agsrAppDAO.getAllAgsrAppData()
    }
    fun deleteAllAgsrAppItems() {
        agsrAppDAO.deleteAllAgsrAppData()
    }
}