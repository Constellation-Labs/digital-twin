package io.swingdev.constellation

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.swingdev.constellation.data.RequestDTO
import io.swingdev.constellation.utils.InjectorUtils
import io.swingdev.constellation.utils.LocationProvider
import io.swingdev.constellation.viewmodels.ConstellationViewModel
import kotlinx.android.synthetic.main.activity_constellation.*
import java.security.Security
import android.content.DialogInterface



class ConstellationActivity : AppCompatActivity() {

    private var locationProvider: LocationProvider? = null
    private var viewModel: ConstellationViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_constellation)

        Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider (), 1)

        locationProvider = InjectorUtils.provideLocationProvider(this)
        Dexter
            .withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        locationProvider?.startRetrievingLocation(this@ConstellationActivity)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {}
            })

        locationProvider?.currentLocation?.observe(this, Observer { coordinates ->
            viewModel?.coordinates?.postValue(coordinates)
        })

        subscribeUi()
    }

    private fun subscribeUi() {
        val factory = InjectorUtils.provideConstellationViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(ConstellationViewModel::class.java)

        periodicButton.setOnClickListener {
            val publicKey = publicKeyText.text.toString()
            val privateKey = privateKeyText.text.toString()
            val endpointUrl = endpointText.text.toString()
            val channelId = channelIdText.text.toString()

            if (publicKey.isNotEmpty() && privateKey.isNotEmpty() && endpointUrl.isNotEmpty() && channelId.isNotEmpty()) {
                val requestDto = RequestDTO(publicKey, privateKey, endpointUrl, channelId)
                viewModel?.startSendingPeriodicRequests(requestDto)
            }
        }

        button.setOnClickListener {
            val publicKey = publicKeyText.text.toString()
            val privateKey = privateKeyText.text.toString()
            val endpointUrl = endpointText.text.toString()
            val channelId = channelIdText.text.toString()

            if (publicKey.isNotEmpty() && privateKey.isNotEmpty() && endpointUrl.isNotEmpty() && channelId.isNotEmpty()) {
                val requestDto = RequestDTO(publicKey, privateKey, endpointUrl, channelId)
                try {
                    viewModel?.sendSingleRequest(requestDto)
                } catch (error: Exception) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(error.toString())
                    builder.setCancelable(true)
                    builder.setNegativeButton(
                        "Ok"
                    ) { dialog, id -> dialog.cancel() }
                    builder.create().show()
                }
            }
        }
    }
}
