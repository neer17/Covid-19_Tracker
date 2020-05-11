package com.example.mvi_scaffolding.nearby

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mvi_scaffolding.firebase.FirebaseHandler
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener


class BeaconMessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Nearby.getMessagesClient(context!!).handleIntent(intent!!, object : MessageListener() {
            override fun onFound(message: Message) {
                // store data in firebase database
                FirebaseHandler(context).updateDataOnNearby(
                    intent.getStringExtra("uid")!!,
                    String(message.content),
                    intent.getDoubleExtra("lat", 0.0),
                    intent.getDoubleExtra("lang", 0.0)
                )
            }

            override fun onLost(message: Message) {
            }
        })
    }
}
