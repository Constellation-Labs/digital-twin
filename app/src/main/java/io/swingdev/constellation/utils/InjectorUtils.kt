package io.swingdev.constellation.utils

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import io.swingdev.constellation.services.ConstellationRepository
import io.swingdev.constellation.viewmodels.ConstellationViewModelFactory

object InjectorUtils {

    fun provideConstellationRepository(): ConstellationRepository {
        return ConstellationRepository.getInstance()
    }

    fun provideConstellationViewModelFactory(): ConstellationViewModelFactory {
        val repo = provideConstellationRepository()
        return ConstellationViewModelFactory(repo)
    }

    fun provideLocationProvider(activity: Activity): LocationProvider {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationProvider(locationManager)
    }
}