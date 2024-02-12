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

// Notification Channel constants

// Name of Notification Channel for verbose notifications of background work
val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
        "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
        "Shows notifications whenever work starts"
val NOTIFICATION_TITLE: CharSequence = "New Triggered Notification"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
var NOTIFICATION_ID = 1

//Constants for activity recognition api
const val REQUEST_CODE_ACTIVITY_TRANSITION = 123
const val REQUEST_CODE_INTENT_ACTIVITY_TRANSITION = 122
const val ACTIVITY_TRANSITION_NOTIFICATION_ID = 111
const val ACTIVITY_TRANSITION_STORAGE = "ACTIVITY_TRANSITION_STORAGE"

// The name of the work
const val WEATHER_WORK_NAME = "weather_work"

// Other keys
const val OUTPUT_PATH = "blur_filter_outputs"
const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
const val TAG_OUTPUT = "OUTPUT"
const val KEY_BLUR_LEVEL = "KEY_BLUR_LEVEL"

const val DELAY_TIME_MILLIS: Long = 3000

const val API_TYPE_WEATHER = "API_WEATHER"
const val API_TYPE_AIRPOLLUTION = "API_AIRPOLLUTION"
const val API_TYPE_SYSTEM = "API_SYSTEM"
const val API_TYPE_CALENDAR = "API_CALENDAR"
const val CONTPROV_TYPE_AGSR = "CONTPROV_AGSR"
const val API_TYPE_SLEEP = "API_SLEEP"
const val API_TYPE_AR = "API_AR"