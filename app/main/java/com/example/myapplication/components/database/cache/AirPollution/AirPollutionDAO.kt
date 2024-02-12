package com.example.myapplication.components.database.cache.AirPollution


import androidx.room.*
import com.example.myapplication.components.database.cache.AirPollution.AirPollution

@Dao
interface AirPollutionDAO {
    /**
     * Insert item
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(airPollution: AirPollution)

    /**
     * Update item
     */
    @Update
    suspend fun update(airPollution: AirPollution)

    /**
     * Delete item
     */
    @Delete
    suspend fun delete(airPollution: AirPollution)

    /**
     * Get all items
     */
    @Query("SELECT * FROM airpollution ORDER BY lastRecordedDatetime DESC")
    fun getAllAirPollutionData(): AirPollution

    /**
     * Delete all items
     */
    @Query("DELETE  FROM airpollution")
    fun deleteAllAirPollutionData()


}