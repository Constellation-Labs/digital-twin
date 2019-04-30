package io.swingdev.constellation.utils

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import io.swingdev.constellation.data.Coordinates

class LocationProvider(private val locationManager: LocationManager) {

    var currentLocation: MutableLiveData<Coordinates> = MutableLiveData()
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation.value = Coordinates(location.latitude, location.longitude)
        }

        override fun onProviderEnabled(provider: String?) {}
        override fun onProviderDisabled(provider: String?) {}
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }


    @SuppressLint("MissingPermission")
    fun startRetrievingLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
    }
}