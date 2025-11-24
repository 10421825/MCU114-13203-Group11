package com.example.majorcitytemp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    data class WeatherResponse(val main: MainData)
    data class MainData(val temp: Double, val humidity: Int)

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private val weatherList = mutableListOf<CityWeather>()
    private lateinit var adapter: WeatherAdapter

    // 🔥🔥 城市名稱對照表（中文→OpenWeather 英文）
    private val cityMap = linkedMapOf(
        "查詢全部縣市" to "ALL",
        "臺北市" to "Taipei",
        "新北市" to "New Taipei",
        "桃園市" to "Taoyuan",
        "新竹市" to "Hsinchu",
        "新竹縣" to "Hsinchu County",
        "苗栗縣" to "Miaoli",
        "台中市" to "Taichung",
        "彰化縣" to "Changhua",
        "南投縣" to "Nantou",
        "雲林縣" to "Yunlin",
        "嘉義市" to "Chiayi",
        "嘉義縣" to "Puzi",
        "台南市" to "Tainan",
        "高雄市" to "Kaohsiung",
        "屏東縣" to "Pingtung",
        "宜蘭縣" to "Yilan",
        "花蓮縣" to "Hualien",
        "台東縣" to "Taitung",
        "基隆市" to "Keelung",
        "澎湖縣" to "Penghu",
        "金門縣" to "Jincheng",
        "連江縣" to "Nangan"   // 馬祖
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinner = findViewById(R.id.spinnerCities)
        recyclerView = findViewById(R.id.recyclerWeather)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = WeatherAdapter(weatherList)
        recyclerView.adapter = adapter

        // 設定下拉式選單（中文縣市）
        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            cityMap.keys.toList()
        )

        val btnGetTemp = findViewById<Button>(R.id.btnGetTemp)

        btnGetTemp.setOnClickListener {
            val chinese = spinner.selectedItem.toString()
            val english = cityMap[chinese]!!

            if (english == "ALL") {
                fetchAllCitiesWeather()
            } else {
                fetchSingleCityWeather(chinese, english)
            }
        }
    }

    // 查單一城市
    private fun fetchSingleCityWeather(chinese: String, english: String) {
        val apiKey = BuildConfig.WEATHER_API_KEY
        val client = OkHttpClient()
        val gson = Gson()

        CoroutineScope(Dispatchers.IO).launch {
            weatherList.clear()

            val url =
                "https://api.openweathermap.org/data/2.5/weather?q=$english&units=metric&appid=$apiKey"

            try {
                val response = client.newCall(Request.Builder().url(url).build()).execute()
                val json = response.body?.string()

                if (json != null) {
                    val data = gson.fromJson(json, WeatherResponse::class.java)
                    weatherList.add(CityWeather(chinese, data.main.temp, data.main.humidity))
                }
            } catch (_: Exception) { }

            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    // 查全部城市
    private fun fetchAllCitiesWeather() {
        val apiKey = BuildConfig.WEATHER_API_KEY
        val client = OkHttpClient()
        val gson = Gson()

        CoroutineScope(Dispatchers.IO).launch {
            weatherList.clear()

            for ((chinese, english) in cityMap) {
                if (english == "ALL") continue

                val url =
                    "https://api.openweathermap.org/data/2.5/weather?q=$english&units=metric&appid=$apiKey"

                try {
                    val response = client.newCall(Request.Builder().url(url).build()).execute()
                    val json = response.body?.string()

                    if (json != null) {
                        val data = gson.fromJson(json, WeatherResponse::class.java)
                        weatherList.add(CityWeather(chinese, data.main.temp, data.main.humidity))
                    }
                } catch (_: Exception) { }
            }

            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }
}
