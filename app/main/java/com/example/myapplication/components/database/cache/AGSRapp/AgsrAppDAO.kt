package com.example.myapplication.components.database.cache.AGSRapp


import androidx.room.*

@Dao
interface AgsrAppDAO {
    /**
     * Insert item
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(agsrApp: AgsrApp)

    /**
     * Update item
     */
    @Update
    suspend fun update(agsrApp: AgsrApp)

    /**
     * Delete item
     */
    @Delete
    suspend fun delete(agsrApp: AgsrApp)

    /**
     * Get all items
     */
    @Query("SELECT * FROM agsrapp ")
    fun getAllAgsrAppData(): AgsrApp

    /**
     * Delete all items
     */
    @Query("DELETE  FROM agsrapp")
    fun deleteAllAgsrAppData()


}