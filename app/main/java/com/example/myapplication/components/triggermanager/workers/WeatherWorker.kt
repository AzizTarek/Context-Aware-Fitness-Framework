package com.example.myapplication.components.triggermanager.workers
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.ContentUriTriggers.Trigger
import com.example.myapplication.components.other.API_TYPE_WEATHER
import com.example.myapplication.MainActivity
import com.example.myapplication.MainActivity.Constants.context
import com.example.myapplication.components.triggermanager.ContextTrigger
import com.example.myapplication.components.contextProviderProcessor.cds.airIsSafe
import com.example.myapplication.components.contextProviderProcessor.cds.weatherIsSuitable
import com.example.myapplication.components.contextProviderProcessor.cds.weatherViewModel
import com.example.myapplication.components.notificationservice.makeStatusNotification
import com.example.myapplication.components.triggermanager.APIPriority
import com.example.myapplication.components.triggermanager.TriggerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "WeatherWorker"
class WeatherWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            return@withContext try {
                //Do something such as get weather data and throw a notification if the situation is right
                var message = "The weather is optimal for walking."
                var contextTrigger = ContextTrigger(API_TYPE_WEATHER,message,APIPriority.WEATHER)
                if(weatherIsSuitable())
                {
                    TriggerManager.addTrigger(contextTrigger)
                }
                val current = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                Log.d(TAG, "$TAG executed at $current...")
                Result.success()
            } catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    "Failed",
                    throwable)
                Result.failure()
            }
        }
    }
}