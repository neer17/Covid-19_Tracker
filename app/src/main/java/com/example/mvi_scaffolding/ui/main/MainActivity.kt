package com.example.mvi_scaffolding.ui.main

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.os.Build
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
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.models.ContractionDetails
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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.util.*
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity(),
    BottomNavController.NavGraphProvider,
    BottomNavController.OnNavigationGraphChanged,
    BottomNavController.OnNavigationReselectedListener,
    SharedPreferences.OnSharedPreferenceChangeListener {
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
    lateinit var jsonAdapter: JsonAdapter<ContractionDetails>


    private val mainScope = CoroutineScope(Main)

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
        attachSharedPreferenceListener()
        createMoshiAdapter()
        subscribeObservers()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        sharedPreferences?.let {
            if (key == Constants.CONTRACTION_DETAILS) {
                val contractionDetailsJson =
                    sharedPreferences.getString(Constants.CONTRACTION_DETAILS, null)
                contractionDetailsJson?.let {
                    val contractionDetails = jsonAdapter.fromJson(contractionDetailsJson)
                    viewModel.setContractionLocation(
                        arrayOf(
                            contractionDetails!!.lat,
                            contractionDetails.lang
                        )
                    )
                    viewModel.setContractionTime(contractionDetails.time)
                }
            }
            if (key == Constants.DANGER_LEVEL) {
                Log.d(TAG, "sharedPrefChangedListener: key == DANGER_LEVEL")

                //  UPDATE VIEW STATE
                if (!sharedPreferences.contains(Constants.CONTRACTION_DETAILS)) {
                    val threatLevel = sharedPreferences.getString(Constants.DANGER_LEVEL, null)
                    threatLevel?.let {
                        viewModel.setThreatLevel(threatLevel)
                    }
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()

        getPermissions()
        getNationalDataAndResources()
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            val contractionDetailsJson = it.getStringExtra(Constants.CONTRACTION_DETAILS)
            editor.putString(Constants.CONTRACTION_DETAILS, contractionDetailsJson)
            editor.commit()
        }
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

                    delay(3000)
                    viewModel.setStateEvent(GetTimeSeriesNetworkEvent())

                    editor.putLong(Constants.LATEST_UPDATED_TIME, System.currentTimeMillis())
                    editor.commit()
                }
            } else {
                GlobalScope.launch(Main) {
                    viewModel.setStateEvent(GetNationalResourceCacheEvent())

                    delay(3000)
                    viewModel.setStateEvent(GetNationalDataCacheEvent())

                    delay(3000)
                    viewModel.setStateEvent(GetTimeSeriesCacheEvent())
                }
            }

            //  when app runs for the first time
        } else {
            editor.putLong(Constants.LAST_NETWORK_REQUEST_TIME, System.currentTimeMillis())
            editor.commit()

            GlobalScope.launch(Main) {
                //  SET STATE
                viewModel.setStateEvent(GetNationalResourceNetworkEvent())

                delay(3000)
                viewModel.setStateEvent(GetNationalDataNetworkEvent())

                delay(3000)
                viewModel.setStateEvent(GetTimeSeriesNetworkEvent())

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
        mainScope.launch {
            coroutineScope {
                val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this@MainActivity)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        //  UPDATE VIEW STATE
                        location?.let {
                            val (city, state) = getCityAndState(location)
                            viewModel.setCurrentLocation(arrayOf(city, state))

                        }

                    }.addOnFailureListener { exception ->
                        Log.e(TAG, "getUsersLocation: error on getting location", exception)
                    }
            }

        }
    }

    private fun createMoshiAdapter() {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        jsonAdapter =
            moshi.adapter<ContractionDetails>(ContractionDetails::class.java)
    }

    private fun attachSharedPreferenceListener() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun removeSharedPreferenceListener() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
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

    override fun onGraphChange(fragment: Fragment) {
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

    private fun expandAppBar() {
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
                    if (it.areAllPermissionsGranted()) {
                        getUsersLocation()

                        //  start bluetooth
                        BluetoothAdapter.getDefaultAdapter().let {
                            Log.d(TAG, "onPermissionsChecked: bluetooth")

                            if (!it.isEnabled) it.enable()
                        }
                        actionOnService(Actions.START)
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

    //  start the service
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

    override fun onDestroy() {
        super.onDestroy()
        removeSharedPreferenceListener()
        mainScope.cancel()
    }
}
