package com.example.mvi_scaffolding.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.mvi_scaffolding.BaseApplication
import com.example.mvi_scaffolding.BuildConfig
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.nearby.Actions
import com.example.mvi_scaffolding.nearby.NearbyIntentService
import com.example.mvi_scaffolding.nearby.ServiceState
import com.example.mvi_scaffolding.nearby.getServiceState
import com.example.mvi_scaffolding.session.SessionManager
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent.*
import com.example.mvi_scaffolding.utils.BottomNavController
import com.example.mvi_scaffolding.utils.Constants
import com.example.mvi_scaffolding.utils.setUpNavigation
import com.example.mvi_scaffolding.viewmodels.ViewModelProviderFactory
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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

        setupActionBar()

        subscribeObservers()
    }

    override fun onStart() {
        super.onStart()

        getPermissions()
        getNationalDataAndResources()
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
                    // SET STATE
                    viewModel.setStateEvent(GetNationalResourceNetworkEvent())

                    delay(3000)

                    viewModel.setStateEvent(GetNationalDataNetworkEvent())

                    editor.putLong(Constants.LATEST_UPDATED_TIME, System.currentTimeMillis())
                    editor.commit()
                }
            } else {
                GlobalScope.launch(Main) {
                    viewModel.setStateEvent(GetNationalResourceCacheEvent())
                    delay(3000)

                    viewModel.setStateEvent(GetNationalDataCacheEvent())
//                    delay(3000)
                    viewModel.setStateEvent(GetTimeSeriesCacheEvent())
                }
            }

            //  when app runs for the first time
        } else {
            /*//  Internet has to be active for the first time
            val connectivityResult = checkInternetAndAirplaneMode()
            if (!connectivityResult[0] || !connectivityResult[1])
                showConnectionAlertDialog(connectivityResult[0], connectivityResult[1])*/

            editor.putLong(Constants.LAST_NETWORK_REQUEST_TIME, System.currentTimeMillis())
            editor.commit()

            GlobalScope.launch(Main) {
                //  SET STATE
                viewModel.setStateEvent(GetNationalResourceNetworkEvent())

                delay(3000)

                viewModel.setStateEvent(GetNationalDataNetworkEvent())

                editor.putLong(Constants.LATEST_UPDATED_TIME, System.currentTimeMillis())
                editor.commit()
            }
        }
    }


    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            dataState.data?.let {
                it.data?.let {
                    it.getContentIfNotHandled()?.let {
                        // UPDATE VIEW STATE
                        it.nationalData?.let { nationalData ->
                            if (nationalData.nationWideDataList.isNotEmpty()) {
                                Log.d(TAG, "subscribeObservers: national data is not null")

                                viewModel.setNationalData(nationalData) //  updating view state
                            }
                        }

                        // UPDATE VIEW STATE
                        it.timeSeries?.let {
                            viewModel.setTimeSeries(it)
                        }

                        // UPDATE VIEW STATE
                        it.nationalResource?.let {
                            viewModel.setNationalResource(it)
                        }
                    }
                }
            }
        })
    }

    private fun getUsersLocation() {
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

    private fun navigateToSettingAlertDialog() {
        AlertDialog.Builder(this)
            .setMessage("Go to Settings and give the location permission")
            .setPositiveButton("Go to Settings") { p0, p1 ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, 100)
            }.setCancelable(false)
            .create()
            .show()
    }

    private fun getPermissions() {
        val multiplePermissionsListener = object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                p0?.let {
                    Log.d(
                        TAG,
                        "onPermissionsChecked: all permissions ${it.areAllPermissionsGranted()}"
                    )

                    if (it.areAllPermissionsGranted()) {
                        getUsersLocation()
                    }
                    if (!it.areAllPermissionsGranted())
                        navigateToSettingAlertDialog()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }
        }

        val errorListener =
            PermissionRequestErrorListener { p0 -> Log.e(TAG, "onError: $p0") }

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(multiplePermissionsListener)
            .withErrorListener(errorListener)
            .check()
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
