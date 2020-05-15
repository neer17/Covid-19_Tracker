package com.example.mvi_scaffolding.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mvi_scaffolding.utils.Constants

class MyBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "AppDebug: " + MyBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        intent.let {
            val contractionDetailsJson = it.getStringExtra(Constants.CONTRACTION_DETAILS)
            editor.putString(Constants.CONTRACTION_DETAILS, contractionDetailsJson)
            editor.apply()
        }
    }
}