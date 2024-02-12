package com.example.myapplication.components.database.cache.AirPollution
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "airpollution")
data class AirPollution(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name="aqi")
    val aqi: Int = 0,
    @ColumnInfo(name="so2")
    val so2:Double = 0.0,
    @ColumnInfo(name="no2")
    val no2:Double = 0.0,
    @ColumnInfo(name="pm10")
    val pm10:Double = 0.0,
    @ColumnInfo(name="pm2_5")
    val pm2_5:Double = 0.0,
    @ColumnInfo(name="o3")
    val o3:Double =0.0,
    @ColumnInfo(name="co")
    val co:Double=0.0,
    @ColumnInfo(name="lastRecordedDateTime")
    var lastRecordedDateTime: Long = 0
    )
