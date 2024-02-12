package com.example.myapplication.components.triggermanager.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.myapplication.components.contextProviderProcessor.cds.CalendarAPI
import com.example.myapplication.components.other.API_TYPE_CALENDAR
import com.example.myapplication.components.other.CONTPROV_TYPE_AGSR
import com.example.myapplication.components.triggermanager.APIPriority
import com.example.myapplication.components.triggermanager.ContextTrigger
import com.example.myapplication.components.triggermanager.TriggerManager
import com.example.myapplication.currentProgress
import com.example.myapplication.currentSteps
import com.example.myapplication.target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

private const val TAG = "CompleteGoalWorker"
class CompleteGoalWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params)  {
    override suspend fun doWork():Result {

        return withContext(Dispatchers.IO) {
            return@withContext try {
                var message = "Complete your goal, only {$target-$currentSteps} left"
                var contextTrigger = ContextTrigger(CONTPROV_TYPE_AGSR, message, APIPriority.CONTPROV)

                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val calendarEventList = CalendarAPI.getEvents()
                var eventCount = calendarEventList.size
                if(currentProgress<100)
                {
                    if (eventCount == 0 && hour ==18) { //If its 18:00 and the user hasnt completed their goal and their schedule is empty

                        TriggerManager.addTrigger(contextTrigger)
                    }
                }
                Result.success()
            } catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    "Failed",
                    throwable
                )
                Result.failure()
            }
        }
    }
}