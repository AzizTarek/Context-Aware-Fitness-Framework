/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication.components.notificationservice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.*
import com.example.myapplication.MainActivity.Constants.context
import com.example.myapplication.components.other.*
import com.example.myapplication.components.triggermanager.ContextTrigger
import com.example.myapplication.components.triggermanager.TriggerManager
import java.util.*


private const val TAG = "NotificationService"
// Define flags for PendingIntent
val FLAGS = PendingIntent.FLAG_UPDATE_CURRENT
var timesUserDidNotRespond=0
var timesUserResponded=0
// Define request codes for PendingIntent
const val NOTIFICATION_REQUEST_CODE_OK = 23
const val NOTIFICATION_REQUEST_CODE_SKIP = 21
// Define your BroadcastReceiver
class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "ACTION_OK" -> {
                // Handle "Ok" action click here
                // Call the appropriate method or perform any other action
                userResponded()
                Log.d(TAG,"user response times $timesUserResponded")
            }
            "ACTION_SKIP" -> {
                // Handle "Skip" action click here
                // Call the appropriate method or perform any other action
                userDidNotRespond()
                alertUser()
            }
        }
    }
}
/**
 * Create a Notification that is shown as a heads-up notification if possible.
 *
 * For this codelab, this is used to show a notification so that you know when different steps
 * of the background work chain are starting
 *
 * @param message Message shown on the notification
 * @param context Context needed to create Toast
 */

@SuppressLint("UnspecifiedImmutableFlag")
fun makeStatusNotification(context: Context) {

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    // Create pending intent, mention the Activity which needs to be
//    triggered when user clicks on notification(StopScript.class in this case)
    val pIntent = PendingIntent.getActivity(
        context, 51,
        Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
    )


    val okIntent = Intent(context, MyBroadcastReceiver::class.java)
//    okIntent.action = "ACTION_OK" // Set action for "Ok" action
    val okPendingIntent = PendingIntent.getBroadcast(
        context,
        NOTIFICATION_REQUEST_CODE_OK,
        okIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val skipIntent = Intent(context, MyBroadcastReceiver::class.java)
//    skipIntent.action = "ACTION_SKIP" // Set action for "Skip" action
    val skipPendingIntent = PendingIntent.getBroadcast(
        context,
        NOTIFICATION_REQUEST_CODE_SKIP,
        skipIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    //Add condition check here for setting appropriate icon

    // Create the notification
    Log.d(TAG,"No. of Triggers : ${TriggerManager.getTriggerListSize()}")
    val trigger = TriggerManager.getTrigger()
    val notif_icon = getNotifIcon(trigger)
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(notif_icon)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(trigger.message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(trigger.message + userPersonalisedMessage()))
            .addAction(R.drawable.ic_launcher_foreground, "Ok", okPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Skip", skipPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))


    // Show the notification
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    TriggerManager.clearTriggers()
}

fun getNotifIcon(trigger: ContextTrigger): Int {
    val triggerType = trigger.type

    if (triggerType == API_TYPE_WEATHER)
        return R.drawable.ic_sunny_day
    if (triggerType == API_TYPE_SYSTEM)
        if (trigger.message.lowercase().contains("battery"))
            return R.drawable.ic_battery_low
        else if (trigger.message.lowercase().contains("screen"))
            return R.drawable.outline_security_update_warning_black_20
    if (trigger.message.lowercase().contains("schedule"))
        return R.drawable.ic_free_calender
    if (triggerType == API_TYPE_SLEEP)
        return R.drawable.ic_sleep
    if (triggerType == API_TYPE_AR)
        return R.drawable.ic_walk_man

    return R.drawable.ic_launcher_foreground

}


/**
 * Vibrate the phone after missing the notification 4 times
 */
fun alertUser() {
    if (timesUserDidNotRespond >= 4) {
        vibratePhone(MainActivity.Constants.context, 10000) // Vibrate for 10 secs
        Log.d("NotificationService", "Alerting the user, because user did not respond ${timesUserDidNotRespond} times")
        reset()
    }
}

// Method to handle "Ok" action click
fun userResponded() {
    // Perform actions when "Ok" action is clicked
    // For example, update a counter, start a service, etc.
    timesUserResponded += 1
}

// Method to handle "Skip" action click
fun userDidNotRespond() {
    // Perform actions when "Skip" action is clicked
    // For example, update a counter, show a different notification, etc.
    timesUserDidNotRespond += 1 // Increment the counter
    alertUser()
}

fun reset() {
    timesUserResponded = 0
    timesUserDidNotRespond = 0
}
