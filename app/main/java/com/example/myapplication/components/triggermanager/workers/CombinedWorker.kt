package com.example.myapplication.components.triggermanager.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.*
import com.example.myapplication.MainActivity
import kotlin.collections.first
import com.example.myapplication.MainActivity.Constants.context

class CombinedWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {        //Tag will be used to identify each worker and gracefully cancel their work when their permission has been revoked
         val WEATHER_WORK_TAG = "weather_work_tag"
         val SYSTEM_WORK_TAG = "system_work_tag"
         val ACTIVITY_WORK_TAG = "activity_work_tag"
         val AIR_POLLUTION_WORK_TAG = "air_pollution_work_tag"
         val NOTIFICATION_WORK_TAG = "notification_work_tag"
         val CALENDAR_WORK_TAG = "calendar_work_tag"
    }
    @SuppressLint("EnqueueWork")
    override fun doWork(): Result {

        val oneTimeWeatherWork = OneTimeWorkRequestBuilder<WeatherWorker>()
            .addTag("weather_work_tag") // Add a tag for WeatherWorker
            .build()

        val oneTimeSystemWork = OneTimeWorkRequestBuilder<SystemWorker>()
            .addTag("system_work_tag") // Add a tag for SystemWorker
            .build()

        val oneTimeActivityWork = OneTimeWorkRequestBuilder<ActivityRecognitionWorker>()
            .addTag("activity_work_tag") // Add a tag for ActivityRecognitionWorker
            .build()

        val oneTimeAirPollutionWork = OneTimeWorkRequestBuilder<AirPollutionWorker>()
            .addTag("air_pollution_work_tag") // Add a tag for AirPollutionWorker
            .build()

        val oneTimeContProvWork = OneTimeWorkRequestBuilder<CompleteGoalWorker>()
            .build()

        val oneTimeNotificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .addTag("notification_work_tag") // Add a tag for NotificationWorker
            .build()

        val oneTimeCalendarWork = OneTimeWorkRequestBuilder<CalendarWorker>()
            .addTag("calendar_work_tag") // Add a tag for CalendarWorker
            .build()
        val oneTimeSleepWork = OneTimeWorkRequestBuilder<SleepWorker>()
            .addTag("sleep_work_tag") // Add a tag for CalendarWorker
            .build()

        WorkManager.getInstance(context)
            .beginWith(oneTimeWeatherWork)
            .then(oneTimeAirPollutionWork)
            .then(oneTimeSystemWork)
            .then(oneTimeActivityWork)
            .then(oneTimeContProvWork)
            .then(oneTimeCalendarWork)
            .then(oneTimeSleepWork)
            .then(oneTimeNotificationWork)
            .enqueue()
        return Result.success()
    }
}