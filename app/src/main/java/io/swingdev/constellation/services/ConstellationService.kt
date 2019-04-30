package io.swingdev.constellation.services

import io.reactivex.Observable
import io.swingdev.constellation.models.CoordinatesRequest
import io.swingdev.constellation.models.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ConstellationService {
    @POST("channel/send")
    fun postCoordinates(@Body coordinatesRequest: CoordinatesRequest): Observable<Response>

    companion object {
        fun create(endpointUrl: String): ConstellationService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create())
                .baseUrl(endpointUrl)
                .build()

            return retrofit.create(ConstellationService::class.java)
        }
    }
}