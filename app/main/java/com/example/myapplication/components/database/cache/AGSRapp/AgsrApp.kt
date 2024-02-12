package com.example.myapplication.components.database.cache.AGSRapp
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agsrapp")
data class AgsrApp(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name="userNotificationType")
    val userType: Int = 0,  // 1->Facilitator , 2->Spark, 3-> Signal
    )
