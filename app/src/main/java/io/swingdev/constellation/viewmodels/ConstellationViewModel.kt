package io.swingdev.constellation.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Base64
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.swingdev.constellation.services.ConstellationService
import io.swingdev.constellation.Data.Coordinates
import io.swingdev.constellation.Data.RequestDTO
import io.swingdev.constellation.Data.Message
import io.swingdev.constellation.models.Request
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.util.concurrent.TimeUnit

class ConstellationViewModel: ViewModel() {

    val coordinates: MutableLiveData<Coordinates> = MutableLiveData()
    private var isRequestingStarted = false

    fun startSendingRequests(requestDTO: RequestDTO) {
        if (isRequestingStarted) return
        val request = createRequest(requestDTO) ?: return

        val service = ConstellationService.getInstance(requestDTO.endpointUrl)
        Observable.interval(0, TimeUnit.SECONDS, Schedulers.io())
            .flatMap { service.postCoordinates(request) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                        response -> Log.d("onNext", response.errorMessage)
                },
                {
                        error -> Log.e("onError", error.localizedMessage)
                    print(error.localizedMessage)
                }
            )
        isRequestingStarted = true
    }

    private fun createRequest(requestDTO: RequestDTO): Request? {
        val coordinates = coordinates.value ?: return null
        val signature = createSignature(coordinates, requestDTO.privateKey)

        val message = Message(
            requestDTO.publicKey.replace("\\n".toRegex(), "").replace("\\s".toRegex(), ""),
            coordinates.latitude.toString(),
            coordinates.longitude.toString(),
            String(signature)
        )

        return Request(arrayOf(message.toString()), requestDTO.channelId)
    }

    private fun createSignature(coordinates: Coordinates, privateKeyString: String): ByteArray {
        val string = coordinates.latitude.toString() + coordinates.longitude.toString()
        val stringBytes = Base64.decode(string, Base64.DEFAULT)

        val cleanPrivateKey = privateKeyString.replace("\\n".toRegex(),"").replace("\\s".toRegex(), "")
        var privateKeyBytes = Base64.decode(cleanPrivateKey, Base64.DEFAULT)
        val keySpec =  PKCS8EncodedKeySpec(privateKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKey = keyFactory.generatePrivate(keySpec)

        val signatureInstance = Signature.getInstance("NONEwithRSA")
        signatureInstance.initSign(privateKey)
        signatureInstance.update(stringBytes)
        return signatureInstance.sign()
    }
}
