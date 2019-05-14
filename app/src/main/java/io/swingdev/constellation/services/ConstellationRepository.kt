package io.swingdev.constellation.services

import io.reactivex.Observable
import io.swingdev.constellation.models.CoordinatesRequest
import io.swingdev.constellation.models.Response

class ConstellationRepository private constructor() {
    private val serviceMap: MutableMap<String, ConstellationService> = mutableMapOf()

    fun sendRequest(endpointUrl: String, request: CoordinatesRequest): Observable<Response> {
        try {
            return (serviceMap[endpointUrl] ?: ConstellationService.create(endpointUrl)
                .also {
                    serviceMap[endpointUrl] = it
                }).postCoordinates(request)
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