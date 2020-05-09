package com.example.mvi_scaffolding.ui.main

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import com.example.mvi_scaffolding.BaseApplication
import com.example.mvi_scaffolding.BuildConfig
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
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

import com.example.mvi_scaffolding.session.SessionManager
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent.*
import com.example.mvi_scaffolding.utils.BottomNavController
import com.example.mvi_scaffolding.utils.Constants
import com.example.mvi_scaffolding.utils.setUpNavigation
import com.example.mvi_scaffolding.viewmodels.ViewModelProviderFactory
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
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

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var editor: SharedPreferences.Editor

    lateinit var alterDialog: AlertDialog

    private lateinit var bottomNavigationView: BottomNavigationView

    lateinit var geocoder: Geocoder

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

        //  bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }

        //  initializations
        geocoder = Geocoder(this, Locale.getDefault())

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)

        //  TODO: get permisions might delete later on
        getPermissions()
        buildAlertDialog()
        setupActionBar()
        subscribeObservers()
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

    //  make network req or read data from cache
    private fun getNationalDataAndResources() {
        val lastNetworkRequestTime =
            sharedPreferences.getLong(Constants.LAST_NETWORK_REQUEST_TIME, 0)

        if (lastNetworkRequestTime != 0L) {
            val hoursElapsed =
                System.currentTimeMillis().minus(lastNetworkRequestTime).div(1000 * 60 * 60)
            if (hoursElapsed > 6) {
                GlobalScope.launch(Main) {
                    viewModel.setStateEvent(GetNationalResourceNetworkEvent())
                    delay(3000)

                    // SET STATE
                    viewModel.setStateEvent(GetNationalDataNetworkEvent())
                }
            } else {
                GlobalScope.launch(Main) {
                    viewModel.setStateEvent(GetNationalResourceCacheEvent())
                    delay(3000)

                    viewModel.setStateEvent(GetNationalDataCacheEvent())
                }
            }

        } else {
            editor.putLong(Constants.LAST_NETWORK_REQUEST_TIME, System.currentTimeMillis())
            editor.commit()

            GlobalScope.launch(Main) {
                viewModel.setStateEvent(GetNationalResourceNetworkEvent())
                //  for the FIRST TIME, make network request
                //  SET STATE
                delay(3000)
                viewModel.setStateEvent(GetNationalDataNetworkEvent())
            }
        }
    }


    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            dataState.data?.let {
                it.data?.let {
                    it.getContentIfNotHandled()?.let {
                        // UPDATE VIEWSTATE
                        it.nationalData?.let { nationalData ->
                            //  TODO: refactor, use datastatelistener in BaseActivity instead
                            if (nationalData.nationWideDataList.isNotEmpty()) {
                                Log.d(TAG, "subscribeObservers: national data is not null")

                                viewModel.setNationalData(nationalData) //  updating view state

                                //  TODO: remove it
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

                        // UPDATE VIEW STATE
                        it.nationalResource?.let {
                            viewModel.setNationalResource(it)
                        }
                    }
                }
            }
        })

        //  TODO: move this logic to datastatelistener
        viewModel.viewState.observe(this, Observer { mainViewState ->
//            Log.d(TAG, "subscribeObservers: viewState : $mainViewState")

            mainViewState.internetConnectivity?.let {
                if (!it) {
                    alterDialog.show()
                } else
                    alterDialog.dismiss()
            }

//            mainViewState.cityAndState?.let {
//                getNationalResources(it)
//            }
        })
    }

    //  TODO: use coroutines
    private fun getUsersLocation() {
        val lm: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!locationEnabled)
        //  TODO: show alert dialog
        else {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->

                    //  UPDATE VIEW STATE
                    val (city, state) = getCityAndState(location!!)
                    viewModel.setCurrentLocation(arrayOf(city, state))

                }.addOnFailureListener { exception ->
                    Log.e(TAG, "getUsersLocation: error on getting location", exception)
                }
        }
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
            is AssessmentFragment -> {
                navController.navigate(R.id.action_assessmentFragment_to_homeFragment)
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


    private fun buildAlertDialog() {
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        alterDialog = alertDialogBuilder.setMessage("Enable connections")
            .setPositiveButton("Settings") { p0, p1 ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivityForResult(settingsIntent, 9003)
            }.setCancelable(false).create()

    }

    //  TODO: Refactor to AuthActivity
    private fun getPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    Log.d(
                        TAG,
                        "onPermissionsChecked allpermissionsgranted?: ${p0?.areAllPermissionsGranted()}"
                    )
                    getUsersLocation()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                }
            }).check()
    }

    private fun getCityAndState(location: Location): Array<String> {
        val city = geocoder.getFromLocation(location.latitude, location.longitude, 1)[0].locality
        val state = geocoder.getFromLocation(location.latitude, location.longitude, 1)[0].adminArea
        return arrayOf(city, state)
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
