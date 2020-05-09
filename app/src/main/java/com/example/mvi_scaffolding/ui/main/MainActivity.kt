package com.example.mvi_scaffolding.ui.main

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.mvi_scaffolding.BaseApplication
import com.example.mvi_scaffolding.BuildConfig
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.nearby.Actions
import com.example.mvi_scaffolding.nearby.NearbyIntentService
import com.example.mvi_scaffolding.nearby.ServiceState
import com.example.mvi_scaffolding.nearby.getServiceState
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (applicationContext as BaseApplication).mCurrentActivity = this

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (!report.areAllPermissionsGranted()) return showLinkToSettingsDialog()

                    BluetoothAdapter.getDefaultAdapter().let {
                        if (!it.isEnabled) it.enable()
                    }

                    actionOnService(Actions.START)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun showLinkToSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Alert !")
            .setMessage("Location Permission is required")
            .setCancelable(false)
            .setPositiveButton(
                "Settings"
            ) { _, _ -> // When the user click yes button
                // open settings
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID, null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .create()
            .show()
    }

    private fun actionOnService(action: Actions) {
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, NearbyIntentService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
                return
            }
            startService(it)
        }
    }
}
