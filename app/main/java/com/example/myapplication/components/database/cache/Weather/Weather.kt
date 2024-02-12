package com.example.myapplication.components.database.cache.Weather
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class Weather(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo(name="visibility")
    var visibility: Int = 0,
    @ColumnInfo(name="humidity")
    var humidity: Int = 0,
    @ColumnInfo(name="windSpeed")
    var windSpeed: Double = 0.0,
    @ColumnInfo(name="clouds")
    var clouds: Double = 0.0,
    @ColumnInfo(name="temperature")
    var temperature: Double = 0.0,
    @ColumnInfo(name="lastRecordedDateTime")
    var lastRecordedDateTime: Long = 0
    )
