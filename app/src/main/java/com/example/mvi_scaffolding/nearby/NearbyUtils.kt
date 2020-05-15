package com.example.mvi_scaffolding.nearby

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mvi_scaffolding.utils.Constants
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessagesClient
import com.google.android.gms.nearby.messages.Strategy.BLE_ONLY
import com.google.android.gms.nearby.messages.SubscribeOptions
import com.google.android.gms.tasks.Task


class NearbyUtils(
    context: Context,
    messagesClient: MessagesClient,
    uid: String
) {
    private val TAG = NearbyUtils::class.java.simpleName

    companion object {
        private var isSubscribed = false
    }

    private val isPositive: Message by lazy {
        val sp = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        val isCovidPositive = sp.getString(Constants.IS_COVID_POSITIVE, "false").toString()
        Message(isCovidPositive.toByteArray(), "positiveStatus")
    }

    private val mMessagesClient = messagesClient
    private val mContext = context
    private val currentUid = uid
    private var mMessage = Message(currentUid.toByteArray(), "uid")


    private fun publishUid() {
        mMessagesClient.publish(mMessage)
    }

    private fun publishPositive() {
        mMessagesClient.publish(isPositive)
    }

    fun publish() {
        publishUid()
        publishPositive()
    }

    fun backgroundSubscribe(lat: Double?, lang: Double?) {
        // already subscribed then first unsubscribe it
        if (isSubscribed) {
            backgroundUnsubscribe(lat, lang)
                .addOnSuccessListener {
                    Log.d(TAG, "backgroundSubscribe: ")

                    isSubscribed = false
                    unPublish()
                    subscribe(lat, lang)
                }.addOnFailureListener {
                    Log.e(TAG, "backgroundSubscribe: ", it)

                }
        }
        // if not subscribed then directly subscribe
        if (!isSubscribed) {
            subscribe(lat, lang)
        }
    }

    private fun subscribe(lat: Double?, lang: Double?) {
        val options = SubscribeOptions.Builder()
            .setStrategy(BLE_ONLY)
            .build()
        mMessagesClient.subscribe(getPendingIntent(lat, lang)!!, options)
            .addOnSuccessListener {
                isSubscribed = true
            }
    }

    private fun unPublish() {
        unPublishPositive()
        unPublishUid()
    }

    private fun unPublishUid() {
        mMessagesClient.unpublish(mMessage)
            .addOnSuccessListener {
                publishUid()
            }
    }

    private fun unPublishPositive() {
        mMessagesClient.unpublish(isPositive)
            .addOnSuccessListener {
                publishPositive()
            }
    }

    private fun backgroundUnsubscribe(lat: Double?, lang: Double?): Task<Void> {
        return mMessagesClient.unsubscribe(getPendingIntent(lat, lang)!!)
    }

    private fun getPendingIntent(lat: Double?, lang: Double?): PendingIntent? {
        val intent = Intent(mContext, BeaconMessageReceiver::class.java)
        intent.putExtra("lat", lat)
        intent.putExtra("lang", lang)
        return PendingIntent.getBroadcast(
            mContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
