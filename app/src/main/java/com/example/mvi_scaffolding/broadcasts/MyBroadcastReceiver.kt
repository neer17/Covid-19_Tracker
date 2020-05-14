package com.example.mvi_scaffolding.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mvi_scaffolding.utils.Constants

class MyBroadcastReceiver : BroadcastReceiver() {
    private val TAG = MyBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: $intent.extras")
        val sharedPreferences =
            context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        intent.let {
            val lat = it.getDoubleExtra(Constants.DATA_LAT, 0.0)
            val lang = it.getDoubleExtra(Constants.DATA_LANG, 0.0)
            val time = it.getLongExtra(Constants.DATA_TIME, 0L)

            editor.putString(Constants.DATA_LAT, lat.toString())
            editor.putString(Constants.DATA_LANG, lang.toString())
            editor.putLong(Constants.DATA_TIME, time)
            editor.commit()
        }
    }
}