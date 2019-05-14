package io.swingdev.constellation.utils

import android.content.Context
import android.location.LocationManager
import io.swingdev.constellation.services.ConstellationRepository
import io.swingdev.constellation.viewmodels.ConstellationViewModelFactory

object InjectorUtils {

    fun provideConstellationRepository(): ConstellationRepository {
        return ConstellationRepository.getInstance()
    }

    fun provideConstellationViewModelFactory(context: Context): ConstellationViewModelFactory {
        val repo = provideConstellationRepository()
        val locationProvider = provideLocationProvider(context)
        return ConstellationViewModelFactory(repo, locationProvider)
    }

    fun provideLocationProvider(context: Context): LocationProvider {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationProvider(locationManager)
    }
}