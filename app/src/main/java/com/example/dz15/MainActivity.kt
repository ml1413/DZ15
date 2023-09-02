package com.example.dz15

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dz15.model.Weather
import com.example.dz15.response.ApiClient
import com.example.dz15.response.ApiInterface
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

private const val API_KEY = "ec25a1b2e674407295082419230206"

class MainActivity : AppCompatActivity() {

    private lateinit var disposable: Disposable
    private lateinit var disposableAppendText: Disposable
    private lateinit var tvCity: TextView
    private lateinit var tvCountry: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvData: TextView
    private lateinit var ivIcon: ImageView
    private lateinit var btRandomCity: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        getWeather()
        /** по клику отправляем один из 5 городов в запрос */
        btRandomCity.setOnClickListener {
            val city = resources.getStringArray(R.array.city_Ukraine)[(1..5).random()]
            getWeather(city = city)
        }
    }

    /** находим view по id */
    private fun initView() {
        tvCity = findViewById(R.id.tv_city)
        tvCountry = findViewById(R.id.tv_country)
        tvTemp = findViewById(R.id.tv_temp)
        tvCondition = findViewById(R.id.tv_condition_weather)
        tvData = findViewById(R.id.tv_data)
        ivIcon = findViewById(R.id.iv_icon)
        btRandomCity = findViewById(R.id.bt_random_city)
    }

    /** получаем информацию с сермера (по умолчанию Киев)*/
    private fun getWeather(city: String = "Kiev") {
        val client = ApiClient.retrofit.create(ApiInterface::class.java)
        disposable = client.getWeatherTodayRX(
            apikey = API_KEY,
            city = city
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    setInformationInView(response)
                }, { error ->
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    throw Exception(error.message)
                })
    }

    /** заполняем елементы на экране */
    private fun setInformationInView(response: Weather) {
        tvCity.text = response.location.name
        tvCountry.appendText("Страна  : ${response.location.country}")
        tvTemp.appendText("Температура  : ${response.current.temp_c}")
        tvCondition.appendText("Погодные условия  : ${response.current.condition.text}")
        tvData.appendText("Дата  : ${response.location.localtime}")
        Glide.with(this)
            .load("https:${response.current.condition.icon}")
            .circleCrop()
            .into(ivIcon)
    }

    fun TextView.appendText(string: String) {
        this.text =""
        val charArray = string.toCharArray()
        disposableAppendText = Observable
            .intervalRange(
            0,
            charArray.size.toLong(),
            0,
            20,
            TimeUnit.MILLISECONDS
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.append(charArray[it.toInt()].toString())
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
        disposableAppendText.dispose()
    }
}
