package io.swingdev.constellation.utils

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import io.swingdev.constellation.Data.Coordinates

class LocationProvider(val locationManager: LocationManager){

    var currentLocation: MutableLiveData<Coordinates> = MutableLiveData()
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation.value = Coordinates(location.latitude, location.longitude)
        }
        override fun onProviderEnabled(provider: String?) { }
        override fun onProviderDisabled(provider: String?) { }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }
    }

    fun startRetrievingLocation(activity: Activity) {
        if (!checkAccessCoarseLocationPermission(activity)) { return }
        if (!checkAccessFineLocationPermission(activity)) { return }
        if (!checkInternetPermission(activity)) { return }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
    }

    private  fun checkAccessCoarseLocationPermission(activity: Activity): Boolean {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 21)
            return false
        }
        return true
    }

    private fun checkAccessFineLocationPermission(activity: Activity): Boolean {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 21)
            return false
        }
        return true
    }

    private fun checkInternetPermission(activity: Activity): Boolean {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.INTERNET), 21)
            return false
        }
        return true
    }
}