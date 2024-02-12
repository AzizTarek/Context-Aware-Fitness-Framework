package com.example.myapplication.components.contextProviderProcessor.cds

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

var batteryPct: Float? = 0f

suspend fun getBatteryPercentage(context: Context): Float {
    return withContext(Dispatchers.IO) {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }

        batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        } ?: -1f
    }
}

fun getScreenOnTimestamp(context: Context): Float {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val startTime = System.currentTimeMillis() - 86400000
    val endTime = System.currentTimeMillis()

    val usageEvents = usageStatsManager.queryEvents(startTime, endTime)

    var lastScreenOnTime: Long = 0
    var lastScreenOffTime: Long = 0
    var totalScreenOnTime: Long = 0
    var interval: Long = 0
    while (usageEvents.hasNextEvent()) {
        val event = UsageEvents.Event()
        usageEvents.getNextEvent(event)

        if (event.eventType == UsageEvents.Event.SCREEN_INTERACTIVE) {
            lastScreenOnTime = event.timeStamp
        }
        if (event.eventType == UsageEvents.Event.SCREEN_NON_INTERACTIVE){
            lastScreenOffTime = event.timeStamp
        }
        totalScreenOnTime = System.currentTimeMillis() - lastScreenOnTime
        interval = lastScreenOnTime - lastScreenOffTime

    }

    Log.d("Last screen on time", "Last screen on time: $lastScreenOnTime")
    Log.d("Last screen off time", "Last screen off time: $lastScreenOffTime")
//
    Log.d("Total Screen on time", "Total Screen on time: $totalScreenOnTime")
    Log.d("Interval ", "Interval :  $interval")
    if ( (interval/3600000f ) < 0.15f)
        totalScreenOnTime += 540000
    return totalScreenOnTime/3600000f

}

// Function to check if internet connection is available and turned on
fun isInternetConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}