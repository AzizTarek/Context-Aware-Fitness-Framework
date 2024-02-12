package com.example.myapplication.components.other

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.components.database.cache.AGSRapp.AgsrApp
import com.example.myapplication.components.database.cache.AGSRapp.AgsrAppDAO
import com.example.myapplication.components.database.cache.AirPollution.AirPollution
import com.example.myapplication.components.database.cache.AirPollution.AirPollutionDAO
import com.example.myapplication.components.database.cache.Weather.Weather
import com.example.myapplication.components.database.cache.Weather.WeatherDAO

@Database(entities = [Weather::class, AirPollution::class, AgsrApp::class], version = 1, exportSchema = false)
abstract class FrameworkDatabase: RoomDatabase() {

    abstract fun weatherDao(): WeatherDAO
    abstract fun airPollutionDao(): AirPollutionDAO
    abstract fun agsrAppDao(): AgsrAppDAO

    companion object {
        @Volatile
        private var Instance: FrameworkDatabase? = null
        fun getDatabase(context: Context): FrameworkDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FrameworkDatabase::class.java, "framework_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() //This allows for queries to return goals instead of flow variables
                    .build()
                    .also { Instance = it }
            }
        }
    }
}