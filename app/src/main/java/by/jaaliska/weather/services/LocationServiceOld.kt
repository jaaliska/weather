package by.jaaliska.weather.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import by.jaaliska.weather.R
import by.jaaliska.weather.data.LocationModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.AsyncSubject
import kotlin.math.sin

class LocationServiceOld  {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var activity: Activity //убрать
    private var locationModel: LocationModel? = null
    //private var single: Observable<LocationModel> = Observable.just()

    fun getLocationModel(): LocationModel {
        if (locationModel != null) {
            return locationModel as LocationModel
        } else {
            throw ExceptionInInitializerError("Location is not initialised")
        }
    }

//    fun getLocation(activity: Activity): Observable<LocationModel> { //return Observeble<Location?>
//        locationModel = null
//        this.activity = activity
//        if (!checkPermissions()) {
//            requestPermissions()
//        } else {
//            getLastLocation()
//        }
//        return single
//    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() { //subject: AsyncSubject<LocationModel>
        LocationServices.getFusedLocationProviderClient(activity).lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location == null) {
                        Log.i(TAG, "------------------>>>>>>>>>>>>>location is null")
                        showSnackbar(
                                "Impossible to determine location. Try again later",
                                "update"
                        ) {
                            getLastLocation()
                        }
                        //return Error
                    } else {
                       // subject.onNext(LocationModel(location.));
                       // subject.onComplete();
                        Log.i(TAG, "-------------------------->>>>>>Local is \"$latitude and $longitude\"")
                    }
                }
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar("Location permission is needed for core functionality", "Okay",
                    View.OnClickListener {
                        startLocationPermissionRequest()
                    })
        } else {
            startLocationPermissionRequest()
        }
    }

    private fun showSnackbar(
            mainTextString: String, actionString: String,
            listener: View.OnClickListener
    ) {
        val contextView = activity.findViewById<View>(R.id.context_view)
        Snackbar.make(contextView, mainTextString, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionString) {
                    listener.onClick(contextView)
                }
                .show()
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    Log.i(TAG, "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    getLastLocation()
                }
                else -> {
                    showSnackbar("Permission was denied", "Settings",
                            View.OnClickListener {
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts(
                                        "package", activity.packageName,
                                        null
                                )
                                intent.data = uri
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                activity.startActivity(intent)
                            }
                    )
                }
            }
        }
    }

    companion object {
        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }
}