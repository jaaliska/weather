package by.jaaliska.weather.repository.server

import by.jaaliska.weather.data.yandexData.YandexWeatherModel
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface YandexWeatherService {

   val DOMAIN: String
       get() = "https://api.weather.yandex.ru/"


    @Headers("X-Yandex-API-Key: 68035504-1059-4a1f-b374-6f82bb5a34aa")
    @GET("v2/forecast?")
    //fun loadRepo(): Call<WeatherModel>
    fun getWeatherDataByCity(
            @Query("lat") lat: Float,
            @Query("lon") lon: Float,
            @Query("hours") hours: Boolean,
            @Query("limit") limit: Int
    ): Observable<YandexWeatherModel>

    companion object Instance {
        private fun getRetrofit(): Retrofit {
            val okHttpClient = OkHttpClient()
            val DOMAIN = "https://api.weather.yandex.ru/"
            val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
                    .baseUrl(DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
            return retrofitBuilder.build()
        }

        fun getApiYandexWeather(): YandexWeatherService {
            return getRetrofit().create(YandexWeatherService::class.java)
        }
    }
}