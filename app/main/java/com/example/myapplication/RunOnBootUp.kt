package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Runs app as soon as device has finished booting up
 */
class RunOnBootUp : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // start app here
            val launchIntent = context?.packageManager?.getLaunchIntentForPackage(context.packageName)
            context?.startActivity(launchIntent)
            println("Fully booted")  //Message in the log to indicate boot has been completed

        }


    }
}
