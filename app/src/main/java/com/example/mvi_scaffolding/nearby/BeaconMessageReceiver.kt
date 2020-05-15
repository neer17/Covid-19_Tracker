package com.example.mvi_scaffolding.nearby

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.firebase.FirebaseHandler
import com.example.mvi_scaffolding.ui.main.MainActivity
import com.example.mvi_scaffolding.utils.Constants
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener


class BeaconMessageReceiver : BroadcastReceiver() {
    companion object {
        private const val CHANNEL_ID = "ForegroundServiceChannel"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Nearby.getMessagesClient(context!!).handleIntent(intent!!, object : MessageListener() {
            override fun onFound(message: Message) {
                // store data in firebase database
                if (message.namespace == "positiveStatus") {
                    val data = String(message.content)
                    if (data == "true") {
                        createNotification(context)
                        return
                    }
                }
                FirebaseHandler(context).updateDataOnNearby(
                    String(message.content),
                    intent.getDoubleExtra("lat", 0.0),
                    intent.getDoubleExtra("lang", 0.0)
                )
            }

            override fun onLost(message: Message) {
            }
        })
    }

    private fun createNotification(context: Context) {
        createNotificationChannel(context)

        val mainActivityPendingIntent = Intent(context, MainActivity::class.java)
            .let {
                it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                it.putExtra(Constants.CONTRACTION_DETAILS, "Someone in your range is COVID-19 positive")

                PendingIntent.getActivity(
                    context,
                    101, it, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val notification = NotificationCompat.Builder(context, BeaconMessageReceiver.CHANNEL_ID)
            .setContentTitle("DANGER")
            .setContentText("Someone in your range is COVID-19 positive")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(mainActivityPendingIntent)
            .setAutoCancel(true)
            .build()

        val nm: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(1001, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                BeaconMessageReceiver.CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            context.getSystemService(
                NotificationManager::class.java
            ).also {
                it?.createNotificationChannel(serviceChannel)
            }
        }
    }
}
