package com.example.myapplication.components.contextProviderProcessor.cds

import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.myapplication.components.database.cache.AirPollution.AirPollution
import com.example.myapplication.components.database.cache.AirPollution.AirPollutionViewModel
import com.example.myapplication.lat
import com.example.myapplication.long
import org.json.JSONObject
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Get air pollution information from the API
 */

var aqi:Int = 0 //Stores the air quality index
var airIsSafe = false
private const val TAG = "AirPollution API"
var airPollutionInstance : AirPollution = AirPollution()
private var airPollutionViewModel: AirPollutionViewModel = AirPollutionViewModel()
var so2 =0.0
var no2=0.0
var pm10=0.0
var pm2_5=0.0
var o3=0.0
var co=0.0
//AsyncTask enables proper and easy use of the UI thread. This class allows you
// to perform background operations and publish results on the UI thread without
// having to manipulate threads and/or handlers.


/**
 * Context provider [Collects and processes raw context data]
 */
class AirPollutionTask() : AsyncTask<String, Void, String>() {
    override fun onPreExecute() {

    }
    override fun doInBackground(vararg params: String?): String? {
        var response:String?
        try{
            //http request to the URL of the API
            response =URL("http://api.openweathermap.org/data/2.5/air_pollution?lat=$lat&lon=$long&appid=$API").readText(
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
            val list = jsonObj.getJSONArray("list")
            val components = list.getJSONObject(0).getJSONObject("components")
            val aqi_ = list.getJSONObject(0).getJSONObject("main").getInt("aqi")
            val co_ = components.getDouble("co") //Concentration of CO (Carbon monoxide), μg/m3
            var no_ = components.getDouble("no") // Concentration of NO (Nitrogen monoxide), μg/m3
            val no2_ = components.getDouble("no2") //Concentration of NO2 (Nitrogen dioxide), μg/m3
            val o3_ = components.getDouble("o3") //Concentration of O3 (Ozone), μg/m3
            val so2_ = components.getDouble("so2") //Concentration of SO2 (Sulphur dioxide), μg/m3
            val pm2_5_ = components.getDouble("pm2_5") //Concentration of PM2.5 (Fine particles matter), μg/m3
            val pm10_ = components.getDouble("pm10") // Concentration of PM10 (Coarse particulate matter), μg/m3
            var nh3 = components.getDouble("nh3") //Concentration of NH3 (Ammonia), μg/m3

            aqi=aqi_
            so2=so2_
            no2=no2_
            pm10=pm10_
            pm2_5=pm2_5_
            o3=o3_
            co=co_
            Log.d(TAG, "Air is ${getAirQualityInfo()}, aqi: $aqi")

            // Create a new Weather object using the extracted data
            airPollutionInstance = AirPollution(
                id = 0, // Assuming 0 as the default id, you can set it according to your requirements
                aqi=aqi,
                so2=so2,
                no2=no2,
                pm10=pm10,
                pm2_5=pm2_5,
                o3=o3,
                co=co,
                lastRecordedDateTime =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")).toString().toLong()
            )
            if(airPollutionViewModel.getAll()!=null)
                airPollutionViewModel.deleteAll()
            airPollutionViewModel.addAirPollutionItem(airPollutionInstance)
            Log.d(TAG, "Offline Air Pollution data: ${airPollutionViewModel.getAll()}...")
            airPollutionSafe(aqi_,so2,no2,pm10,pm2_5,o3,co)
        } catch (e: Exception) {
            Log.d(TAG,"Error, will use Offline Air Pollution data. ${airPollutionViewModel.getAll()} Error solved:$e")
//            Using offline data
            if(airPollutionViewModel.getAll()!=null)
            {
                airPollutionSafe(
                    airPollutionViewModel.getAll().aqi,
                    airPollutionViewModel.getAll().so2,
                    airPollutionViewModel.getAll().no2,
                    airPollutionViewModel.getAll().pm10,
                    airPollutionViewModel.getAll().pm2_5,
                    airPollutionViewModel.getAll().o3,
                    airPollutionViewModel.getAll().co)
            }
        }
    }
}

/**
 * Context processor [Data is processed to higher level context information]
 * Provides foundation for the system's decision making capabilities
 * Assess the amount of air polluting gases of the outside environment of the user's location
 */
fun airPollutionSafe(aqi: Int, so2:Double,no2:Double,pm10:Double,pm2_5:Double,o3:Double,CO:Double ) : Boolean {
    var result = false
    if (aqi<4)
    {
        if(so2<250 && no2 < 150 && pm10<100 && pm2_5<50 && o3<140 && CO<12400)
        {
            result = true
        }
    }
    airIsSafe = result
    return result
}
/**
 * Only used for debugging!
 * Return the current air pollution grade
 * Air Quality Index.
 * Possible values: 1, 2, 3, 4, 5. Where 1 = Good, 2 = Fair, 3 = Moderate, 4 = Poor, 5 = Very Poor
 */
fun getAirQualityInfo(): String {
    var result = ""
    if (aqi ==1)
        result = "Good"
    else if (aqi ==2)
        result = "Fair"
    else if (aqi ==3)
        result = "Moderate"
    return result
}