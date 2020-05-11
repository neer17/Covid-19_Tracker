package com.example.mvi_scaffolding.firebase

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseHandler(context: Context) {

    fun updateUserBasicInfo(uid: String, name: String, phone: String, isPositive: Boolean): Task<Void> {
        val ref = Firebase.database.getReference("users")
            .child(uid)
        val data = HashMap<String, Any>()
        data["name"] = name
        data["phone"] = phone
        data["isPositive"] = isPositive
       return ref.setValue(data)
    }

    fun updateCovidPositiveStatus(uid: String, isPositive: Boolean): Task<Void> {
        return Firebase.database.getReference("users")
            .child(uid).child("isPositive").setValue(isPositive)
    }



    fun updateDataOnNearby(receivedUid: String, lat: Double?, lang: Double?) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {currentUid ->
            val ref = Firebase.database.getReference("cross_location")
                .child(currentUid).child(receivedUid)
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
}
