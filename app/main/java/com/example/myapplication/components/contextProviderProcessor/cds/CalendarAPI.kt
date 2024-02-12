package com.example.myapplication.components.contextProviderProcessor.cds

import android.database.Cursor
import android.provider.CalendarContract
import com.example.myapplication.MainActivity
import java.util.*

class CalendarAPI {
    companion object {
        public fun getEvents(): MutableList<CalendarEvent> {

            val events = mutableListOf<CalendarEvent>()

            val projection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART
            )

            val uri = CalendarContract.Events.CONTENT_URI
            val selection =
                "(${CalendarContract.Events.DTSTART} >= ?) AND (${CalendarContract.Events.DTSTART} <= ?)"
            val selectionArgs =
                arrayOf(getStartOfDayInMillis().toString(), getEndOfDayInMillis().toString())

            val cursor = MainActivity.Constants.context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
            )

            var eventCount = cursor?.count
            println("Total Events found for the day: $eventCount")

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val eventId = cursor.getLong(0)
                    val title = cursor.getString(1)
                    val startDate = cursor.getLong(2)
                    println("EVENT ID:::::::::::::::$eventId")
                    println("EVENT title:::::::::::::::$title")
                    println("EVENT date:::::::::::::::$startDate")
                    val event = CalendarEvent(eventId, title, startDate)
                    events.add(event)

                } while (cursor.moveToNext())
                cursor.close()
            }
            return events
        }

        fun getStartOfDayInMillis(): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

        fun getEndOfDayInMillis(): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            return calendar.timeInMillis

        }
        data class CalendarEvent(
            val id: Long,
            val title: String?,
            val startDate: Long
        )
    }
}