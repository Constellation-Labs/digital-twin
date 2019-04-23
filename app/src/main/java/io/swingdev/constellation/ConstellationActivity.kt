package io.swingdev.constellation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import io.swingdev.constellation.Data.RequestDTO
import io.swingdev.constellation.utils.InjectorUtils
import io.swingdev.constellation.utils.LocationProvider
import io.swingdev.constellation.viewmodels.ConstellationViewModel

class ConstellationActivity : AppCompatActivity() {

    private var locationProvider: LocationProvider? = null
    private var viewModel: ConstellationViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consteallation)

        locationProvider = InjectorUtils.provideLocationProvider(this).also { it.startRetrievingLocation(this) }
        locationProvider?.currentLocation?.observe(this, Observer { coordinates ->
            viewModel?.coordinates?.value = coordinates
        })

        subscribeUi()
    }

    private fun subscribeUi() {
        val factory = InjectorUtils.provideConstellationViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(ConstellationViewModel::class.java)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val publicKey = findViewById<TextView>(R.id.publicKeyText).text.toString()
            val privateKey = findViewById<TextView>(R.id.privateKeyText).text.toString()
            val endpointUrl = findViewById<TextView>(R.id.endpointText).text.toString()
            val channelId = findViewById<TextView>(R.id.channelIdText).text.toString()

            if (!publicKey.isEmpty() && !privateKey.isEmpty() && !endpointUrl.isEmpty() && !channelId.isEmpty()) {
                val requestDto = RequestDTO(publicKey, privateKey, endpointUrl, channelId)
                viewModel?.startSendingRequests(requestDto)
            }
        }
    }
}
