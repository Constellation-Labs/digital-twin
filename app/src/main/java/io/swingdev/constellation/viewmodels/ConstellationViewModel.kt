package io.swingdev.constellation.viewmodels

import android.arch.lifecycle.ViewModel
import android.util.Base64
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.swingdev.constellation.data.Coordinates
import io.swingdev.constellation.data.Message
import io.swingdev.constellation.data.RequestDTO
import io.swingdev.constellation.models.CoordinatesRequest
import io.swingdev.constellation.services.ConstellationRepository
import io.swingdev.constellation.utils.DisposableManager
import io.swingdev.constellation.utils.LocationProvider
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.util.concurrent.TimeUnit

class ConstellationViewModel(
    private val constellationRepository: ConstellationRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private var isRequestingStarted = false

    fun subscribeLocationChanges() {
        locationProvider.startRetrievingLocation()
    }

    fun sendSingleRequest(requestDTO: RequestDTO) {
        try {
            val request = createRequest(requestDTO) ?: return

            val disposable = constellationRepository.sendRequest(requestDTO.endpointUrl, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        Log.i("onNext", response.errorMessage)
                        Log.d("onNext", response.errorMessage)
                    },
                    { error ->
                        Log.e("onError", error.localizedMessage)
                    }
                )
            DisposableManager.add(disposable)
        } catch (error: Exception) {
            throw error
        }
    }

    fun sendPeriodicallyRequests(requestDTO: RequestDTO) {
        if (isRequestingStarted) return

        Observable.interval(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                requestDTO.endpointUrl to (createRequest(requestDTO)
                    ?: throw IllegalArgumentException("Error to init Request"))
            }.flatMap { (url, request) ->
                Log.i("MSG", request.messages[0])
                constellationRepository.sendRequest(url, request)
            }.retry()
            .subscribe({ response ->
                Log.i("onNext", response.errorMessage)
            }, { error ->
                isRequestingStarted = false
                Log.e("onError", error.localizedMessage)
            }).let(DisposableManager::add)

        isRequestingStarted = true
    }

    private fun createRequest(requestDTO: RequestDTO): CoordinatesRequest? {
        try {
            val coordinates = locationProvider.currentCoordinates.value ?: return null
            val signature = createSignature(coordinates, requestDTO.privateKey)
            val message = Message(
                requestDTO.publicKey.replace("\\n".toRegex(), "").replace("\\s".toRegex(), ""),
                coordinates.latitude.toString(),
                coordinates.longitude.toString(),
                String(signature)
            )

            return CoordinatesRequest(listOf(message.toString()), requestDTO.channelId)
        } catch (error: Exception) {
            error.printStackTrace()
            throw error
        }
    }

    private fun createSignature(coordinates: Coordinates, privateKeyString: String): ByteArray {
        try {
            val coordinateBytes = (coordinates.latitude.toString() + coordinates.longitude.toString())
                .toByteArray(Charset.defaultCharset())
                .let { bytes ->
                    Base64.encode(bytes, Base64.DEFAULT)
                }

            val privateKey = privateKeyString.replace("\\n".toRegex(), "")
                .replace("\\s".toRegex(), "")
                .let { key ->
                    PKCS8EncodedKeySpec(Base64.decode(key, Base64.DEFAULT))
                }.let { keySpec ->
                    KeyFactory.getInstance("EC").generatePrivate(keySpec)
                }

            return Signature.getInstance("SHA512withECDSA", "SC").apply {
                initSign(privateKey)
                update(coordinateBytes)
            }.sign()
        } catch (error: Exception) {
            error.printStackTrace()
            throw error
        }
    }
}
