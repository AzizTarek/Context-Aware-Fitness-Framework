package com.example.myapplication.components.triggermanager

//Current proposed priority hierarchy in descending order
/*
Calendar
Weather + Air quality
Location
System
ActivityState
Sleep
*/

enum class APIPriority(i: Int) {
     CALENDAR(1),
     WEATHER(2),
     AIRPOLLUTION(3),
     CONTPROV(4),
     LOCATION(5),
     SYSTEM(6),
     ACTIVITY(7),
     SLEEP(8),
}

class ContextTrigger(
     var type: String = "",
     var message: String = "",
     var priority: APIPriority
    ) {

}