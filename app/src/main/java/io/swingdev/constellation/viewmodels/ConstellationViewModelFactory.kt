package io.swingdev.constellation.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.swingdev.constellation.services.ConstellationRepository

@Suppress("UNCHECKED_CAST")
class ConstellationViewModelFactory(
    private val repository: ConstellationRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ConstellationViewModel(repository) as T
    }
}