package com.example.myapplication.components.database.cache.Weather


import androidx.room.*

@Dao
interface WeatherDAO {
    /**
     * Insert weather item
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(weather: Weather)

    /**
     * Update weather item
     */
    @Update
    suspend fun update(weather: Weather)

    /**
     * Delete weather item
     */
    @Delete
    suspend fun delete(weather: Weather)

    /**
     * Get all weather items
     */
    @Query("SELECT * FROM weather ORDER BY lastRecordedDatetime DESC")
    fun getAllWeatherData(): Weather

    /**
     * Delete all weather items
     */
    @Query("DELETE  FROM weather")
    fun deleteAllWeatherData()


}