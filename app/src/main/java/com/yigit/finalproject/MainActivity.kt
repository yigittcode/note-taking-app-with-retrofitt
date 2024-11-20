package com.yigit.finalproject

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yigit.finalproject.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var myDatabase: SQLiteDatabase
    private lateinit var weatherApiService: WeatherApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApiService = retrofit.create(WeatherApiService::class.java)

        prepareNoteList()

        getWeatherData("Ankara", "9a01e93b3db618df7df80736953bb9a1")
    }

    private fun prepareNoteList() {
        val noteList: ArrayList<String> = ArrayList()
        val idList: ArrayList<Int> = ArrayList()

        myDatabase = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null)
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes (noteID INTEGER PRIMARY KEY, title VARCHAR, content VARCHAR)")
        val cursor = myDatabase.rawQuery("SELECT * FROM notes", null)
        val titleIX = cursor.getColumnIndex("title")
        val idIX = cursor.getColumnIndex("noteID")

        while (cursor.moveToNext()) {
            idList.add(cursor.getInt(idIX))
            noteList.add(cursor.getString(titleIX))
        }

        cursor.close()

        binding.recylerView.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter(noteList, idList)
        binding.recylerView.adapter = noteAdapter
    }

    private fun getWeatherData(cityName: String, apiKey: String) {
        val call = weatherApiService.getWeather(cityName, apiKey)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    val temperature = weatherResponse?.main?.temp
                    updateWeatherInfo(temperature)
                } else {
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
            }
        })
    }

    private fun kelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    private fun celsiusToFahrenheit(celsius: Double): Double {
        return celsius * 9 / 5 + 32
    }

    private fun updateWeatherInfo(temperature: Double?) {
        if (temperature != null) {
            val temperatureInCelsius = kelvinToCelsius(temperature)
            val temperatureInFahrenheit = celsiusToFahrenheit(temperatureInCelsius)
            binding.weatherInfo.text =
                "Temperature: ${String.format("%.2f", temperatureInCelsius)}°C / ${String.format("%.2f", temperatureInFahrenheit)}°F"
        } else {
            binding.weatherInfo.text = "Temperature information not available"
        }
    }

    fun goAddNote(view: View) {
        val intent: Intent = Intent(this, AddNoteActivity::class.java)
        startActivity(intent)
    }
}
