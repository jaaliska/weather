package by.jaaliska.weather.services

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import by.jaaliska.weather.data.LocationModel
import by.jaaliska.weather.exceptions.LocationAccessDeniedException
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider


class LocationServiceImpl: LocationService {


    @SuppressLint("MissingPermission")
    override fun getLocationModel(activity: AppCompatActivity): Observable<LocationModel> {
        val rxPermissions = RxPermissions(activity)
        return rxPermissions
            .request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .flatMap { granted -> if (granted)
                    ReactiveLocationProvider(activity)
                        .lastKnownLocation
                        .map {location ->
                            LocationModel(location.latitude, location.longitude)}
                else
                    Observable.error(LocationAccessDeniedException(
                            "Allow getting the location in the \"settings -> permissions\""
                    ))
            }
    }
}