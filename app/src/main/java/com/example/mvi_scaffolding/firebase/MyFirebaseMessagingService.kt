package com.example.mvi_scaffolding.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.broadcasts.MyBroadcastReceiver
import com.example.mvi_scaffolding.ui.main.MainActivity
import com.example.mvi_scaffolding.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = MyFirebaseMessagingService::class.java.simpleName

    companion object {
        private const val CHANNEL_ID = "ForegroundServiceChannel"
    }

    private var notificationTitle: String? = null
    private var notificationBody: String? = null
    lateinit var broadcastManager: LocalBroadcastManager

    override fun onCreate() {
        super.onCreate()
        broadcastManager = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: $remoteMessage.")

        // Check if message contains a notification payload.

        remoteMessage.data.let {
            val positiveUid = it["uid"]!!

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            uid?.let { currentUid ->
                getLocationAndTimeOfContact(positiveUid, currentUid)
            }
        }
    }

    private fun getLocationAndTimeOfContact(positiveUid: String, currentUid: String) {
        Firebase.database.reference.child("cross_location")
            .child(positiveUid).child(currentUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG, "onCancelled: ", p0.toException())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.value as HashMap<*, *>
                    val dataTimestamp = data["timestamp"] as Long
                    val location = data["location"] as HashMap<*, *>
                    val lat = location["lat"] as Double
                    val lang = location["lang"] as Double

                    Log.d(TAG, "onDataChange: lat $lat, lang $lang, time $dataTimestamp")
                    

                    Log.d(TAG, "onDataChange: data class name ${data}")
                    createNotification(lat, lang, dataTimestamp)
                }
            })
    }

    private fun createNotification(lat: Double, lang: Double, time: Long) {
        createNotificationChannel()
        val mainActivityPendingIntent = Intent(this, MainActivity::class.java)
            .let {
                it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                it.putExtra(Constants.DATA_LANG, lang)
                it.putExtra(Constants.DATA_LAT, lat)
                it.putExtra(Constants.DATA_TIME, time)

                PendingIntent.getActivity(
                    this,
                    101, it, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val broadcastPendingIntent = Intent(this, MyBroadcastReceiver::class.java)
            .let {
                PendingIntent.getBroadcast(this, 102, it, PendingIntent.FLAG_UPDATE_CURRENT)
            }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("DANGER")
            .setContentText("You came in contact of a Covid-19 positive person, click to see the details")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(mainActivityPendingIntent)
            .setDeleteIntent(broadcastPendingIntent)
            .setAutoCancel(true)
            .build()

        val nm: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(1001, notification)
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
