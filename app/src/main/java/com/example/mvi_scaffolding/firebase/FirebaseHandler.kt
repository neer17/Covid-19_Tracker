package com.example.mvi_scaffolding.firebase

import android.content.Context
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseHandler(context: Context) {

    fun updateUserBasicInfo(uid: String, name: String, phone: Long, isPositive: Boolean) {
        val ref = Firebase.database.getReference("users")
            .child(uid)
        val data = HashMap<String, Any>()
        data["name"] = name
        data["phone"] = phone
        data["isPositive"] = isPositive
        ref.setValue(data)
    }

    fun updateCovidPositiveStatus(uid: String, isPositive: Boolean) {
        Firebase.database.getReference("users")
            .child(uid).child("isPositive").setValue(isPositive)
    }

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
