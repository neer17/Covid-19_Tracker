package com.example.mvi_scaffolding.utils

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class ErrorHandling{
    companion object{
        private val TAG = "AppDebug: " + ErrorHandling::class.java.simpleName

        const val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
        const val UNABLE_TODO_OPERATION_WO_INTERNET = "Can't do that operation without an internet connection"
        const val ERROR_CHECK_NETWORK_CONNECTION = "Check network connection."
        const val ERROR_UNKNOWN = "Unknown error"

        fun isNetworkError(msg: String): Boolean{
            when{
                msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
                else-> return false
            }
        }
    }

}