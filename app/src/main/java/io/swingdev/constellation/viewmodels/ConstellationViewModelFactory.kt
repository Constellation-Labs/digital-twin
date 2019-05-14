package io.swingdev.constellation.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.swingdev.constellation.services.ConstellationRepository
import io.swingdev.constellation.utils.LocationProvider

@Suppress("UNCHECKED_CAST")
class ConstellationViewModelFactory(
    private val repository: ConstellationRepository,
    private val locationProvider: LocationProvider
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ConstellationViewModel(repository, locationProvider) as T
    }
}