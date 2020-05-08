package com.example.mvi_scaffolding.ui.main

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.session.SessionManager
import com.example.mvi_scaffolding.utils.BottomNavController
import com.example.mvi_scaffolding.utils.setUpNavigation
import com.example.mvi_scaffolding.viewmodels.ViewModelProviderFactory
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.android.support.DaggerAppCompatActivity

import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity(),
    BottomNavController.NavGraphProvider,
    BottomNavController.OnNavigationGraphChanged,
    BottomNavController.OnNavigationReselectedListener {
    private val TAG = "AppDebug: " + MainActivity::class.java.simpleName

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var sessionManager: SessionManager

    lateinit var alterDialog: AlertDialog

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_home,
            this,
            this
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)

        //  TODO: get permisions might delete later on
        buildAlertDialog()
        setupActionBar()
        getPermissions()
        getUsersLocation()
        subscribeObservers()
    }


    private fun subscribeObservers() {
        //  datastate would get the data and set the data in MainViewState
        viewModel.dataState.observe(this, Observer { dataState ->
            dataState.data?.let {
                it.data?.let {
                    it.getContentIfNotHandled()?.let {
                        // set  NationalData
                        it.nationalData?.let { nationalData ->
                            //  TODO: refactor, use datastatelistener in BaseActivity instead
                            if (nationalData.nationWideDataList.isNotEmpty()) {
                                viewModel.setNationalData(nationalData) //  updating view state
                                viewModel.setInternetConnectivity(true)
                            } else {
                                val isAirplaneModeOn = Settings.System.getInt(
                                    contentResolver,
                                    Settings.Global.AIRPLANE_MODE_ON,
                                    0
                                ) != 0
                                val isInternetOn = sessionManager.isConnectedToTheInternet()
                                if (!isAirplaneModeOn || !isInternetOn) {
                                    viewModel.setInternetConnectivity(false)
                                }
                            }
                        }

                        //  set NationalResource
                        it.nationalResource?.let {
                            viewModel.setNationalResource(it)
                        }
                    }
                }
            }
        })

        //  TODO: move this logic to datastatelistener
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

    //  used in "onNavigationItemSelected"
    override fun getNavGraphId(itemId: Int) = when (itemId) {
        R.id.nav_home -> {
            R.navigation.home_navigation_graph
        }
        R.id.nav_update -> {
            R.navigation.updates_nav_graph
        }
        else -> {
            R.navigation.home_navigation_graph
        }
    }

    override fun onGraphChange(itemId: Int) {
//        cancelActiveJobs()
        expandAppBar()
    }

    //  on reselection navigate back to the NavHostFragment of each item
    override fun onReselectNavItem(
        navController: NavController,
        fragment: Fragment
    ) {
        when (fragment) {
            is NationWideDataDisplayFragment -> {
                navController.navigate(R.id.action_nationWideDataDisplayFragment_to_covidUpdatesFragment)
            }
            else -> {
                // do nothing
            }
        }

    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }


    private fun getUsersLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(TAG, "getUsersLocation: location $location")

                //  setting location in MainViewState
                location?.let {
                    viewModel.setCurrentLocation(it)
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "getUsersLocation: error on getting location", exception)

            }
    }

    private fun buildAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alterDialog = alertDialogBuilder.setMessage("Enable connections")
            .setPositiveButton("Settings") { p0, p1 ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivityForResult(settingsIntent, 9003)
            }.setCancelable(false).create()

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
}
