/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.example.myapplication.components.other

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.lifecycle.asFlow
import androidx.work.*
import com.example.myapplication.components.other.*
import com.example.myapplication.components.triggermanager.workers.ActivityRecognitionWorker
import com.example.myapplication.components.triggermanager.workers.SystemWorker
import com.example.myapplication.components.triggermanager.workers.WeatherWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import androidx.lifecycle.Observer
import com.example.myapplication.MainActivity
import com.example.myapplication.MainActivity.Constants.activity
import com.example.myapplication.MainActivity.Constants.context
import com.example.myapplication.components.notificationservice.makeStatusNotification
import com.example.myapplication.components.triggermanager.workers.CombinedWorker

class WorkManagerFrameworkRepository(context: Context) : FrameworkRepository {
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * Create the WorkRequests to perform work
     */
    override fun performWork() {
        // Create constraint
        val constraints = Constraints.Builder().
        setRequiredNetworkType(NetworkType.UNMETERED) //Wifi connection is needed
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<CombinedWorker>(15,TimeUnit.MINUTES).addTag("COMBINED_WORKER").build()
        workManager.enqueueUniquePeriodicWork(
            "combined_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
    }


    /**
     * Cancel any ongoing WorkRequests
     * */
    override fun cancelWork() {
        workManager.cancelUniqueWork(WEATHER_WORK_NAME)
    }

    /**
     * Creates the input data bundle which includes the blur level to
     * update the amount of blur to be applied and the Uri to operate on
     * @return Data which contains the Image Uri as a String and blur level as an Integer
     */
    private fun createInputDataForWorkRequest(blurLevel: Int, imageUri: Uri): Data {
        val builder = Data.Builder()
        builder.putString(KEY_IMAGE_URI, imageUri.toString()).putInt(KEY_BLUR_LEVEL, blurLevel)
        return builder.build()
    }
}
