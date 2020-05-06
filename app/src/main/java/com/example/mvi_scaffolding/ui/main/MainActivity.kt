package com.example.mvi_scaffolding.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.session.SessionManager
import com.example.mvi_scaffolding.viewmodels.ViewModelProviderFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {
    private val TAG = "AppDebug: " + MainActivity::class.java.simpleName

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var sessionManager: SessionManager

    lateinit var alterDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  setting up nav controller
        val navController = findNavController(R.id.main_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)


        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)

        //  TODO: get permisions might delete later on
        buildAlertDialog()
        getPermissions()
        subscribeObservers()

    }

    private fun buildAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alterDialog = alertDialogBuilder.setMessage("Enable connections")
            .setPositiveButton("Settings") { p0, p1 ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivityForResult(settingsIntent, 9003)
            }.setCancelable(false).create()

    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(this, Observer { mainViewState ->
            Log.d(TAG, "subscribeObservers: viewState: $mainViewState")

            mainViewState.internetConnectivity?.let {
                if (!it) {
                    alterDialog.show()
                } else
                    alterDialog.dismiss()
            }
        })
    }

    private fun getPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                }
            }).check()
    }

    override fun onResume() {
        super.onResume()

    }
}
