package io.swingdev.constellation.Services

import io.reactivex.Observable
import io.swingdev.constellation.Models.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ConstellationService {
    @POST("channel/send")
    fun postCoordinates(@Body request: Request): Observable<String>

    companion object {
        private fun create(endpointUrl: String): ConstellationService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create())
                .baseUrl(endpointUrl)
                .build()

            return retrofit.create(ConstellationService::class.java)
        }

        private var instance: ConstellationService? = null

        fun getInstance(endpointUrl: String): ConstellationService {
            return instance ?: synchronized(this) {
                instance
                    ?: create(endpointUrl)
            }
        }
    }
}