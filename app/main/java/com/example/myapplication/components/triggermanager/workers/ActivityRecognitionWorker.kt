package com.example.myapplication.components.triggermanager.workers

import android.content.Context
import android.util.Log
import androidx.work.ContentUriTriggers.Trigger
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.components.other.API_TYPE_AR
import com.example.myapplication.MainActivity
import com.example.myapplication.components.contextProviderProcessor.cds.activityRecognitionAPI.ActivityTransitionsUtil
import com.example.myapplication.components.contextProviderProcessor.cds.activityRecognitionAPI.state
import com.example.myapplication.components.notificationservice.makeStatusNotification
import com.example.myapplication.components.triggermanager.APIPriority
import com.example.myapplication.components.triggermanager.ContextTrigger
import com.example.myapplication.components.triggermanager.TriggerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val TAG = "ActivityRecognitionWorker"
class ActivityRecognitionWorker (ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params){
       override suspend fun doWork(): Result {

            return withContext(Dispatchers.IO) {
                return@withContext try {
                    val message = "You seem to be ${if (state=="RUNNING") "running" else "in a vehicle"}."
                    val contextTrigger = ContextTrigger(API_TYPE_AR,message,APIPriority.ACTIVITY)
                    if(state =="IN_VEHICLE" || state =="RUNNING")
                    {
                        TriggerManager.addTrigger(contextTrigger)
                    }
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