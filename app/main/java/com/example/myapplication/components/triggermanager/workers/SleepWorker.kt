package com.example.myapplication.components.triggermanager.workers

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.components.contextProviderProcessor.cds.activityRecognitionAPI.state
import com.example.myapplication.components.contextProviderProcessor.cds.airIsSafe
import com.example.myapplication.components.contextProviderProcessor.cds.weatherIsSuitable
import com.example.myapplication.components.other.API_TYPE_AR
import com.example.myapplication.components.other.API_TYPE_SLEEP
import com.example.myapplication.components.other.API_TYPE_WEATHER
import com.example.myapplication.components.triggermanager.APIPriority
import com.example.myapplication.components.triggermanager.ContextTrigger
import com.example.myapplication.components.triggermanager.TriggerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "SleepWorker"
class SleepWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            return@withContext try {
                var state = "AWAKE"
                //Do something such as get weather data and throw a notification if the situation is right
                val message = "You seem to be ${if (state =="AWAKE") "awake." else "in deep sleep."}."
                val contextTrigger = ContextTrigger(API_TYPE_SLEEP,message,APIPriority.SLEEP)
                if(state =="AWAKE")
                {
                    TriggerManager.addTrigger(contextTrigger)
                }
                Result.success()
            }

            catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    "Failed",
                    throwable)
                Result.failure()
            }
        }
    }
}