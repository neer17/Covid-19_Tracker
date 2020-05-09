package com.example.mvi_scaffolding.firebase

import android.content.Context
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseHandler(context: Context) {

    fun updateDataOnNearby(uid: String, receivedUid: String, lat: Double?, lang: Double?) {
        val ref = Firebase.database.getReference("cross_location")
            .child(uid).child(receivedUid)
        val data = HashMap<String, Any>()
        val locationData = HashMap<String, Double?>()
        locationData["lat"] = lat
        locationData["lang"] = lang
        data["location"] = locationData
        data["timestamp"] = ServerValue.TIMESTAMP
        ref.setValue(data)

        // subscribe to topic for notifications
        FirebaseMessaging.getInstance().subscribeToTopic(receivedUid)
    }
}
