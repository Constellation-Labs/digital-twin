package io.swingdev.constellation.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Base64
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.swingdev.constellation.data.Coordinates
import io.swingdev.constellation.data.RequestDTO
import io.swingdev.constellation.data.Message
import io.swingdev.constellation.models.CoordinatesRequest
import io.swingdev.constellation.services.ConstellationRepository
import io.swingdev.constellation.utils.DisposableManager
import java.lang.Exception
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

class ConstellationViewModel(private val constellationRepository: ConstellationRepository) : ViewModel() {

    val coordinates: MutableLiveData<Coordinates> = MutableLiveData()
    private var isRequestingStarted = false

    init {
        coordinates.postValue(Coordinates(0.0, 0.0))
    }

    fun sendSingleRequest(requestDTO: RequestDTO) {
        try {
            val request = createRequest(requestDTO) ?: return

            val disposable = constellationRepository.sendSingleRequest(requestDTO.endpointUrl, request)
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
        try {
            val request = createRequest(requestDTO) ?: return

            val disposable = constellationRepository.sendPeriodicallyRequest(requestDTO.endpointUrl, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        Log.i("onNext", response.errorMessage)
                    },
                    { error ->
                        Log.e("onError", error.localizedMessage)
                    }
                )
            DisposableManager.add(disposable)

            isRequestingStarted = true
        } catch (error: Exception) {
            throw error
            isRequestingStarted = false
        }
    }

    private fun createRequest(requestDTO: RequestDTO): CoordinatesRequest? {
        try {
            val coordinates = coordinates.value ?: return null
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
        val string = coordinates.latitude.toString() + coordinates.longitude.toString()

        try {
            val stringBytes = Base64.decode(string, Base64.DEFAULT)
            val cleanPrivateKey = privateKeyString.replace("\\n".toRegex(), "").replace("\\s".toRegex(), "")
            var privateKeyBytes = Base64.decode(cleanPrivateKey, Base64.DEFAULT)
            val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)

            val keyFactory = KeyFactory.getInstance("EC")
            val privateKey = keyFactory.generatePrivate(keySpec)

            val signatureInstance = Signature.getInstance("SHA512withECDSA", "SC")
            signatureInstance.initSign(privateKey)
            signatureInstance.update(stringBytes)
            return signatureInstance.sign()
        } catch (error: Exception) {
            error.printStackTrace()
            throw error
        }
    }
}
