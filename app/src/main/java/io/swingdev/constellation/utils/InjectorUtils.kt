package io.swingdev.constellation.utils

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import io.swingdev.constellation.viewmodels.ConstellationViewModelFactory

object InjectorUtils {

    fun provideConstellationViewModelFactory(): ConstellationViewModelFactory {
        return ConstellationViewModelFactory()
    }

    fun provideLocationProvider(activity: Activity): LocationProvider {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationProvider(locationManager)
    }
}