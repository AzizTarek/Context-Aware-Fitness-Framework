package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.myapplication.components.other.WorkManagerFrameworkRepository
import com.example.myapplication.components.database.cache.AGSRapp.AgsrApp
import com.example.myapplication.components.database.cache.AGSRapp.AgsrAppViewModel
import com.example.myapplication.components.contextProviderProcessor.cds.AirPollutionTask
import com.example.myapplication.components.contextProviderProcessor.cds.WeatherTask
import com.example.myapplication.components.contextProviderProcessor.cds.activityRecognitionAPI.ActivityTransitionReceiver
import com.example.myapplication.components.contextProviderProcessor.cds.activityRecognitionAPI.ActivityTransitionsUtil
import com.example.myapplication.components.contextProviderProcessor.cds.activityRecognitionAPI.state
import com.example.myapplication.components.contextProviderProcessor.cds.sleepAPI.SleepRequestsManager
import com.example.myapplication.components.other.REQUEST_CODE_ACTIVITY_TRANSITION
import com.example.myapplication.components.other.REQUEST_CODE_INTENT_ACTIVITY_TRANSITION
import com.example.myapplication.components.other.identifyUserType
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import firebase.com.protolitewrapper.BuildConfig.APPLICATION_ID
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.BuildConfig
import pub.devrel.easypermissions.EasyPermissions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.MainActivity.Constants.context
import com.example.myapplication.components.notificationservice.alertUser
import com.example.myapplication.components.triggermanager.workers.ActivityRecognitionWorker
import com.example.myapplication.components.triggermanager.workers.AirPollutionWorker
import com.example.myapplication.components.triggermanager.workers.CombinedWorker.Companion.ACTIVITY_WORK_TAG
import com.example.myapplication.components.triggermanager.workers.CombinedWorker.Companion.AIR_POLLUTION_WORK_TAG
import com.example.myapplication.components.triggermanager.workers.CombinedWorker.Companion.WEATHER_WORK_TAG
import com.example.myapplication.components.triggermanager.workers.WeatherWorker

var lat:Double = 0.0 //Latitude of the user location
var long:Double = 0.0 //Longitude of the user location
private const val TAG = "MainActivity"
var agsrAppInstance : AgsrApp = AgsrApp()
private var agsrAppViewModel: AgsrAppViewModel = AgsrAppViewModel()
//private val UPDATE_INTERVAL: Long = 10000 // 10 seconds
private lateinit var mActivityRecognitionClient: ActivityRecognitionClient
var totalSteps = 0
var totalProgress = 0f
var currentSteps = 0
var currentProgress =0f
var target = 0
class MainActivity : ComponentActivity()
    , EasyPermissions.PermissionCallbacks
{
    private val sleepRequestManager by lazy{
        SleepRequestsManager(this)
    }

    private val permissionRequester: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                requestActivityRecognitionPermission()
            } else {
                sleepRequestManager.subscribeToSleepUpdates()
            }
        }
    @SuppressLint("StaticFieldLeak")
    object Constants {
        lateinit var context: Context //Reusable context for notifications
        var activity: ComponentActivity = ComponentActivity()
    }
    lateinit var client: ActivityRecognitionClient
    lateinit var storage: SharedPreferences
    var LOCATION_REQUEST_CODE = 29034
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

     @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
       sleepRequestManager.requestSleepUpdates(requestPermission = {
            permissionRequester.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        })

        Constants.context = this
        client = ActivityRecognition.getClient(this)
        storage = PreferenceManager.getDefaultSharedPreferences(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mActivityRecognitionClient = ActivityRecognition.getClient(Constants.context);

        super.onCreate(savedInstanceState)
        requestPermissions()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        }
        else
            askLocationPermission()

        shouldShowRequestPermissionRationale(Manifest.permission.GET_ACCOUNTS)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.GET_ACCOUNTS), 3)
        } else {
            println("Account permission Granted!")
        }

        AirPollutionTask().execute()
        WeatherTask().execute()

        //Work manager
        val wkRepository = WorkManagerFrameworkRepository(context = context)
        wkRepository.performWork()
        //Content provider
        contentProvider()

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.READ_PHONE_STATE
        )

        if (areAllPermissionsGranted()) {
            // All permissions are already granted
            sleepRequestManager.subscribeToSleepUpdates()
            sleepRequestManager.subscribeToSleepUpdates()
            getLastLocation()
        } else {
            // Request permissions using ActivityResultLauncher
            permissionLauncher.launch(permissions)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun areAllPermissionsGranted(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.READ_PHONE_STATE
        )
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    val weatherWorkRequest = OneTimeWorkRequestBuilder<WeatherWorker>().build()
    val airPollutionWorkRequest = OneTimeWorkRequestBuilder<AirPollutionWorker>().build()
    val activityWorkRequest = OneTimeWorkRequestBuilder<ActivityRecognitionWorker>().build()

    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                requestForUpdates()
                sleepRequestManager.subscribeToSleepUpdates()
                getLastLocation()

                // Enable workers that were cancelled previously
                WorkManager.getInstance(context).enqueueUniqueWork(
                    WEATHER_WORK_TAG,
                    ExistingWorkPolicy.REPLACE,
                    weatherWorkRequest
                )
                WorkManager.getInstance(context).enqueueUniqueWork(
                    AIR_POLLUTION_WORK_TAG,
                    ExistingWorkPolicy.REPLACE,
                    airPollutionWorkRequest
                )
                WorkManager.getInstance(context).enqueueUniqueWork(
                    ACTIVITY_WORK_TAG,
                    ExistingWorkPolicy.REPLACE,
                    activityWorkRequest
                )
            }
            else {
                //Disable workers gracefully
                if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == false) {
                    // ACCESS_COARSE_LOCATION permission is not granted
                    // Cancel the Workers
                    WorkManager.getInstance(context).cancelAllWorkByTag(WEATHER_WORK_TAG)
                    WorkManager.getInstance(context).cancelAllWorkByTag(AIR_POLLUTION_WORK_TAG)
                }
                if (permissions[Manifest.permission.ACTIVITY_RECOGNITION,] == false) {
                    //  Manifest.permission.ACTIVITY_RECOGNITION, permission is not granted
                    // Cancel the Worker
                    WorkManager.getInstance(context).cancelAllWorkByTag(ACTIVITY_WORK_TAG)
                }
            }
        }



    //Content Provider as CDS
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    fun contentProvider()
    {
        val CONTENT_URI = Uri.parse("content://com.example.user.provider/history")
        // creating a cursor object of the
        // content URI
        val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)

        var index = 0
        // iteration of the cursor
        // to print whole table
        if ( cursor!=null)
        {
            if (cursor!!.moveToFirst()) {
                val strBuild = StringBuilder()
                while (!cursor.isAfterLast)
                {
                    val steps = cursor.getString(cursor.getColumnIndex("steps")).toInt()
                    val progress = (steps/cursor.getString(cursor.getColumnIndex("target")).toFloat())*100f
                    val dataItemCreationDate = cursor.getString(cursor.getColumnIndex("date")).toInt()
                    val comparisonCondition = ((LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))).toString().toInt()-dataItemCreationDate)
                    if(comparisonCondition in 1..7)
                    {
                        totalSteps+=steps
                        totalProgress+=progress
                        index+=1
                    }
                    if(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt() ==dataItemCreationDate)
                    {
                        currentSteps = cursor.getString(cursor.getColumnIndex("steps")).toInt()
                        target = cursor.getString(cursor.getColumnIndex("target")).toInt()
                        currentProgress= cursor.getString(cursor.getColumnIndex("progress")).toFloat()
                    }

                    strBuild.append("""   
                            
    ${cursor.getString(cursor.getColumnIndex("id"))}-${cursor.getString(cursor.getColumnIndex("name"))}-${cursor.getString(cursor.getColumnIndex("steps"))}
    """.trimIndent())
                    cursor.moveToNext()
                }
                totalProgress /= index
                totalSteps /= index
                val userType =identifyUserType(totalSteps,totalProgress)
                if(agsrAppViewModel.getAll()!=null)
                    agsrAppViewModel.deleteAll()
                agsrAppViewModel.addAgsrAppItem(agsrAppInstance.copy(id=0,  userType = userType ))
                Log.d(TAG, "AGSR data from Database: ${agsrAppViewModel.getAll()}...")
//                Log.d(TAG,strBuild.toString())

            } else { Log.d(TAG,"\"No Records Found in Content Provider\"")}
            cursor.close()
        }
    }
    //Fused Location API
            @SuppressLint("MissingPermission")
            fun getLastLocation()
            {
                val locationTask: Task<Location> = fusedLocationProviderClient.lastLocation

                locationTask.addOnSuccessListener { location ->
                    location?.let {
                        Log.d(TAG, "OnLocationRetrievalSuccess: $location")
                        lat =  location.latitude
                        long =  location.longitude
                        Log.d(TAG, "OnLocationRetrievalSuccess Accuracy: ${location.accuracy}")
                    } ?: run {
                        Log.d(TAG, "OnSuccess Location was null...")
                    }
                }.addOnFailureListener { e ->
                    Log.e(TAG, "OnFailure: ${e.localizedMessage}")
                }

            }

            private fun askLocationPermission()
            {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION))
                    {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
                    }
                }
            }


    //Place the service in the background
    override fun onResume() {
        super.onResume()
        moveTaskToBack(true)
    }



//Methods for ACTIVITY RECOGNITION API
            override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
                requestForUpdates()

            }
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    AppSettingsDialog.Builder(this).build().show()
                } else {
                    requestActivityTransitionPermission()
                }
            }
            override fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<out String>,
                grantResults: IntArray
            ) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
            }
            @SuppressLint("MissingPermission")
            private fun requestForUpdates() {
                client
                    .requestActivityTransitionUpdates(
                        ActivityTransitionsUtil.getActivityTransitionRequest(),
                        getPendingIntent()
                    )
                    .addOnSuccessListener {
                       println("Successful registration for Activity Recognition...")
                    }
                    .addOnFailureListener { e: Exception ->
                       println("Unsuccessful registration for Activity Recognition...")
                    }
            }
            @SuppressLint("MissingPermission")
            private fun deregisterForUpdates() {
                client
                    .removeActivityTransitionUpdates(getPendingIntent())
                    .addOnSuccessListener {
                        getPendingIntent().cancel()
                        println("Successful deregistration...")
                    }
                    .addOnFailureListener { e: Exception ->
                        println("unsuccessful deregistration...")
                    }
            }
            @SuppressLint("UnspecifiedImmutableFlag")
            private fun getPendingIntent(): PendingIntent {
                val intent = Intent(this, ActivityTransitionReceiver::class.java)
                return PendingIntent.getBroadcast(
                    this,
                    REQUEST_CODE_INTENT_ACTIVITY_TRANSITION,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
            @RequiresApi(Build.VERSION_CODES.Q)
            private fun requestActivityTransitionPermission() {
                EasyPermissions.requestPermissions(
                    this,
                    "You need to allow activity transition permissions in order to use this feature",
                    REQUEST_CODE_ACTIVITY_TRANSITION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            }



    //Sleep API
    override fun onDestroy() {
        super.onDestroy()
        sleepRequestManager.unsubscribeFromSleepUpdates()
    }
    private fun requestActivityRecognitionPermission() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", APPLICATION_ID, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(intent)
    }
}

fun Fragment.vibratePhone() {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }
}


// Function to make the phone vibrate for a given duration
fun vibratePhone(context: Context, durationMillis: Long) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // For Android Oreo (API level 26) and above
            vibrator.vibrate(VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // For devices below Android Oreo (API level 26)
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMillis)
        }
    }
}


