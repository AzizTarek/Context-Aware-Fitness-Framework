package com.example.myapplication.components.contextProviderProcessor.cds.activityRecognitionAPI

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.work.*
import com.example.myapplication.MainActivity
import com.example.myapplication.components.triggermanager.workers.ActivityRecognitionWorker
import com.google.android.gms.location.ActivityTransitionResult
import java.text.SimpleDateFormat
import java.util.*

var state = ""
class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            result?.let {
                result.transitionEvents.forEach { event ->
                    //Info for debugging purposes
                    val info =
                        "Transition: " + ActivityTransitionsUtil.toActivityString(event.activityType) +
                                " (" + ActivityTransitionsUtil.toTransitionType(event.transitionType) + ")" + "   " +
                                SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())

                    // Send data to worker
                    val inputData = workDataOf(
                        "activity_type" to ActivityTransitionsUtil.toActivityString(event.activityType),
                        "transition_type" to ActivityTransitionsUtil.toTransitionType(event.transitionType),
                        "timestamp" to System.currentTimeMillis()
                    )

                    Log.d("ActivityLog", "Activity is $inputData")

                    // Start the worker
//                    val worker = OneTimeWorkRequestBuilder<ActivityRecognitionWorker>()
//                        .setInputData(inputData)
//                        .build()
//
//                    WorkManager.getInstance(MainActivity.Constants.context).enqueue(worker)

                    state = ActivityTransitionsUtil.toActivityString(event.activityType)
                    Log.d("ActivityLog","Activity Detected I can see you are in" +
                            " ${ActivityTransitionsUtil.toActivityString(event.activityType)} state")
                    Toast.makeText(context, info, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}