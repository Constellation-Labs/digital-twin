package io.swingdev.constellation

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.swingdev.constellation.data.RequestDTO
import io.swingdev.constellation.enums.RequestQuantityType
import io.swingdev.constellation.utils.DisposableManager
import io.swingdev.constellation.utils.InjectorUtils
import io.swingdev.constellation.viewmodels.ConstellationViewModel
import kotlinx.android.synthetic.main.activity_constellation.*
import java.security.Security


class ConstellationActivity : AppCompatActivity() {
    private var viewModel: ConstellationViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_constellation)

        Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider (), 1)

        Dexter
            .withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        viewModel?.subscribeLocationChanges()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {}
            }).check()

        subscribeUi()
    }

    override fun onDestroy() {
        DisposableManager.dispose()
        super.onDestroy()
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun subscribeUi() {
        val factory = InjectorUtils.provideConstellationViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(ConstellationViewModel::class.java)

        periodicButton.setOnClickListener { sendRequest(RequestQuantityType.PERIODIC) }
        button.setOnClickListener { sendRequest(RequestQuantityType.SINGLE) }
    }

    private fun sendRequest(type: RequestQuantityType) {
        val publicKey = publicKeyText.text.toString()
        val privateKey = privateKeyText.text.toString()
        val endpointUrl = endpointText.text.toString()
        val channelId = channelIdText.text.toString()

        if (publicKey.isNotEmpty() && privateKey.isNotEmpty() && endpointUrl.isNotEmpty() && channelId.isNotEmpty()) {
            val requestDto = RequestDTO(publicKey, privateKey, endpointUrl, channelId)
            try {
                if (type == RequestQuantityType.SINGLE) {
                    viewModel?.sendSingleRequest(requestDto)
                } else if (type == RequestQuantityType.PERIODIC) {
                    viewModel?.sendPeriodicallyRequests(requestDto)
                }
            } catch (error: Exception) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(error.toString())
                builder.setCancelable(true)
                builder.setNegativeButton(
                    "Ok"
                ) { dialog, _ -> dialog.cancel() }

                builder.create().show()
            }
        }
    }
}
