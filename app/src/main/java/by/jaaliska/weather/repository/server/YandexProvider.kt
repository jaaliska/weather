package by.jaaliska.weather.repository.server

import by.jaaliska.weather.data.LocationModel
import by.jaaliska.weather.data.WeatherModel
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class YandexProvider : WeatherProvider {

    private val yandexService: YandexService = YandexService.getYandexApi()

    override fun getWeather(location: LocationModel): Observable<WeatherModel> {
        return yandexService.getWeatherDataByLocation(
            location.getLatitude(),
            location.getLongitude(),
            false,
            1
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { yw -> WeatherModel("Yandex", yw.getTemp().toDouble()) }
    }
}