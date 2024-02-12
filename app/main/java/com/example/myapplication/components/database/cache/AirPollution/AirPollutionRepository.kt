package com.example.myapplication.components.database.cache.AirPollution

class AirPollutionRepository(private val airPollutionDAO: AirPollutionDAO) {

    val allAirPollutionData: AirPollution = airPollutionDAO.getAllAirPollutionData()

    suspend fun insertAirPollution(airPollution: AirPollution){
        airPollutionDAO.insert(airPollution)
    }
    suspend fun updateAirPollution(airPollution: AirPollution){
        airPollutionDAO.update(airPollution)
    }
    suspend fun deleteAirPollution(airPollution: AirPollution){
        airPollutionDAO.delete(airPollution)
    }
    fun getAllAirPollutionItems() {
        airPollutionDAO.getAllAirPollutionData()
    }
    fun deleteAllAirPollutionItems() {
        airPollutionDAO.deleteAllAirPollutionData()
    }
}