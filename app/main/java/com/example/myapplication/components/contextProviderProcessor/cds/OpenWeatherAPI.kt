package com.example.myapplication.components.contextProviderProcessor.cds

import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.myapplication.MainActivity
import com.example.myapplication.MainActivity.Constants.context
import com.example.myapplication.components.database.cache.Weather.Weather
import com.example.myapplication.components.database.cache.Weather.WeatherViewModel
import com.example.myapplication.lat
import com.example.myapplication.long
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Get weather information from the API
 */

val API: String = "a0107c44de41bcad16066761df23d7cd"
var visibility: Int = 0
var humidity: Int = 0
var windSpeed: Double = 0.0
var clouds: Double = 0.0
var temperature: Double = 0.0
private  const val  TAG = "OpenWeather API"
var weatherInstance : Weather = Weather(0,0,0,0.0,0.0,0.0,0)
var weatherViewModel: WeatherViewModel = WeatherViewModel()
//AsyncTask enables proper and easy use of the UI thread. This class allows you
// to perform background operations and publish results on the UI thread without
// having to manipulate threads and/or handlers.
/**
 * Context provider [Collects and processes raw context data]
 */
class WeatherTask() : AsyncTask<String, Void, String>() {
    override fun onPreExecute() {

    }
    override fun doInBackground(vararg params: String?): String? {
        var response:String?
        try{
            //http request to the URL of the API
            response =URL("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&units=metric&appid=$API").readText(
                Charsets.UTF_8
            )
        }catch (e: Exception){
            response = null
            Log.d(TAG,"No Response $e")
        }
        return response
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        try {
            /* Extracting JSON returns from the API */
            val jsonObj = JSONObject(result)
            val main = jsonObj.getJSONObject("main")
            val visibility_ = jsonObj.getInt("visibility")
            val clouds_ = jsonObj.getJSONObject("clouds").getDouble("all")
            val sys = jsonObj.getJSONObject("sys")
            val wind_ = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val updatedAt:Long = jsonObj.getLong("dt")
            val updatedAtText =  SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
            val temp = main.getDouble("temp")
            val tempMin = main.getString("temp_min")
            val tempMax = main.getString("temp_max")
            val pressure = main.getString("pressure")
            val humidity_ = main.getInt("humidity")
            val sunrise:Long = sys.getLong("sunrise")
            val sunset:Long = sys.getLong("sunset")
            val windSpeed_ = wind_.getDouble("speed")
            val weatherDescription = weather.getString("description")

            windSpeed = windSpeed_
            visibility = visibility_
            clouds = clouds_
            humidity = humidity_
            temperature = temp

            Log.d(TAG,"Temperature:$temp Humidity:$humidity_ $clouds_% cloudy Visibility:$visibility_ m ...")
            // Create a new Weather object using the extracted data
            weatherInstance = Weather(
                id = 0, // Assuming 0 as the default id, you can set it according to your requirements
                visibility = visibility_,
                humidity = humidity_,
                windSpeed = windSpeed_,
                clouds = clouds_,
                temperature = temp,
                lastRecordedDateTime =  ((LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")).toString()).toLong())

            )
            if(weatherViewModel.getAll()!=null)
                weatherViewModel.deleteAll()
            weatherViewModel.addWeatherItem(weatherInstance)
            Log.d(TAG, "Offline Weather data: ${weatherViewModel.getAll()}...")
        } catch (e: Exception) {
            Log.d(TAG,"Error, will use Offline Weather data. Error solved:$e ")
            //Using offline data
            if(weatherViewModel.getAll()!=null)
            {
                windSpeed = weatherViewModel.getAll().windSpeed
                visibility = weatherViewModel.getAll().visibility
                clouds = weatherViewModel.getAll().clouds
                humidity = weatherViewModel.getAll().humidity
                temperature = weatherViewModel.getAll().temperature
            }


        }
    }
}

/**
 * Context processor [Data is processed to higher level context information]
 * Provides foundation for the system's decision making capabilities
 * Assess the weather condition by checking specified weather indexes if they're suitable for walking
 */
fun weatherIsSuitable(): Boolean
{
    var result = false
    if (windSpeed <=25000   //Moderate wind branches may move
        && visibility >=2000 //Haze is 2-4km
//        && clouds<=70 // Partly sunny/cloudy
        && humidity >=25
        && temperature >=5 && temperature <=30
    )
        result = true
    return result
}

