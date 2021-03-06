package by.jaaliska.weather.data.yandexData

import com.google.gson.annotations.SerializedName

data class Fact(
    @SerializedName("temp")
    private val temp: Float
) {
    fun getTemp() = temp
}