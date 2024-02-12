package com.example.myapplication.components.contextProviderProcessor.cds.activityRecognitionAPI

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myapplication.MainActivity.Constants.activity
import com.example.myapplication.components.other.REQUEST_CODE_ACTIVITY_TRANSITION
import com.example.myapplication.components.other.REQUEST_CODE_INTENT_ACTIVITY_TRANSITION
import com.google.android.gms.location.ActivityRecognitionClient
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class ActivityRecognitionAPI :EasyPermissions.PermissionCallbacks{
    lateinit var client: ActivityRecognitionClient
    lateinit var storage: SharedPreferences
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        requestForUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms)) {
            AppSettingsDialog.Builder(activity).build().show()
        } else {
            requestActivityTransitionPermission()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       activity.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("MissingPermission")
     fun requestForUpdates() {
        client
            .requestActivityTransitionUpdates(
                ActivityTransitionsUtil.getActivityTransitionRequest(),
                getPendingIntent()
            )
            .addOnSuccessListener {
                println("successful registration")
            }
            .addOnFailureListener { e: Exception ->
                println("Unsuccessful registration")
            }
    }

    @SuppressLint("MissingPermission")
     fun deregisterForUpdates() {
        client
            .removeActivityTransitionUpdates(getPendingIntent())
            .addOnSuccessListener {
                getPendingIntent().cancel()
                println("successful deregistration")
            }
            .addOnFailureListener { e: Exception ->
                println("unsuccessful deregistration")
            }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
     fun getPendingIntent(): PendingIntent {
        val intent = Intent(activity, ActivityTransitionReceiver::class.java)
        return PendingIntent.getBroadcast(
            activity,
            REQUEST_CODE_INTENT_ACTIVITY_TRANSITION,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestActivityTransitionPermission() {
        EasyPermissions.requestPermissions(
           activity,
            "You need to allow activity transition permissions in order to use this feature",
            REQUEST_CODE_ACTIVITY_TRANSITION,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }
}