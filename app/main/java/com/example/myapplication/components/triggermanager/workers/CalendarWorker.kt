package com.example.myapplication.components.triggermanager.workers

import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.MainActivity.Constants.context
import com.example.myapplication.components.other.API_TYPE_CALENDAR
import com.example.myapplication.components.triggermanager.APIPriority
import com.example.myapplication.components.triggermanager.ContextTrigger
import com.example.myapplication.components.triggermanager.TriggerManager
import com.example.myapplication.components.contextProviderProcessor.cds.CalendarAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

private const val TAG = "CalendarWorker"
class CalendarWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            return@withContext try {
                var message = "You have a clear schedule today! How about you plan for a long walk? \n"
                var contextTrigger = ContextTrigger(API_TYPE_CALENDAR, message, APIPriority.CALENDAR)

                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                println("Current time is:::" + hour + ":" + minute)
                val calendarEventList = CalendarAPI.getEvents()
                var eventCount = calendarEventList.size
                println("Total Events found for the day: $eventCount")
                if (eventCount == 0 && hour == 10 && minute < 20) {
                    println("Triggering a Date Time Notification:::" + hour + ":" + minute)
                    TriggerManager.addTrigger(contextTrigger)
                }
                Result.success()
            } catch (throwable: Throwable) {
                Log.e(
                    com.example.myapplication.components.triggermanager.workers.TAG,
                    "Failed",
                    throwable
                )
                Result.failure()
            }
        }
    }
}