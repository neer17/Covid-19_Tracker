package com.example.mvi_scaffolding.nearby

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.mvi_scaffolding.BaseApplication
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.ui.main.MainActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.MessagesOptions
import com.google.android.gms.nearby.messages.NearbyPermissions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class NearbyIntentService : Service() {
    private val TAG = NearbyIntentService::class.java.simpleName

    companion object {
        private const val CHANNEL_ID = "ForegroundServiceChannel"
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    private lateinit var nearbyUtils: NearbyUtils

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        
        val notification = createNotification()
        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent != null) {
            when (intent.action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
            }
        }

        return START_STICKY
    }

    private fun startService() {
        if (isServiceStarted) return
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }

        // we're starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    doNearbySearching()
                }
                delay(30 * 1000)
            }
        }
    }

    private fun stopService() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

    private fun doNearbySearching() {
        val context = (applicationContext as BaseApplication).mCurrentActivity!!

        // permission for nearby api
        val messagesClient = Nearby.getMessagesClient(
            context, MessagesOptions.Builder()
                .setPermissions(NearbyPermissions.DEFAULT)
                .build()
        )

        // get last known location
        LocationServices
            .getFusedLocationProviderClient(this)
            .lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(TAG, "doNearbySearching: location ${location!!.latitude}")

                FirebaseAuth.getInstance().uid?.let { uid ->
                    Log.d(TAG, "doNearbySearching: service uid $uid")

                    nearbyUtils = NearbyUtils(
                        context,
                        messagesClient,
                        uid
                    )
                    val lat = location?.latitude
                    val lang = location?.longitude

                    nearbyUtils.backgroundSubscribe(lat, lang)
                    nearbyUtils.publish()
                }
            }
    }

    private fun createNotification(): Notification {
        createNotificationChannel()
        val pendingIntent = Intent(this, MainActivity::class.java)
            .let {
                PendingIntent.getActivity(
                    this,
                    0, it, 0
                )
            }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Covid-19 Tracker")
            .setContentText("Tracking service running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(
                NotificationManager::class.java
            ).also {
                it?.createNotificationChannel(serviceChannel)
            }
        }
    }
}
