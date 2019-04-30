package io.swingdev.constellation.services

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.swingdev.constellation.models.CoordinatesRequest
import io.swingdev.constellation.models.Response
import java.util.concurrent.TimeUnit

class ConstellationRepository private constructor() {

    fun sendSingleRequest(endpointUrl: String, request: CoordinatesRequest): Observable<Response> {
        try {
            return ConstellationService
                .create(endpointUrl)
                .postCoordinates(request)
        } catch (error: Exception) {
            throw error
        }
    }

    fun sendPeriodicallyRequest(endpointUrl: String, request: CoordinatesRequest): Observable<Response> {
        try {
            val service = ConstellationService.create(endpointUrl)
            return Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                .flatMap { service.postCoordinates(request) }
        } catch (error: Exception) {
            throw error
        }
    }

    companion object {
        private var instance: ConstellationRepository? = null

        fun getInstance(): ConstellationRepository {
            return instance ?: synchronized(this) {
                instance ?: ConstellationRepository().also { instance = it }
            }
        }
    }
}