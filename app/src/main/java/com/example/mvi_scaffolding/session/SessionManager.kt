package com.example.mvi_scaffolding.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvi_scaffolding.models.NationalDataTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/*
*   to manage the session
* */
@Singleton
class SessionManager
@Inject
constructor(
    val application: Application
) {
    private val TAG = "AppDebug: " + SessionManager::class.java.simpleName

    private val _cachedToken = MutableLiveData<NationalDataTable>()

    val cachedToken: LiveData<NationalDataTable>
        get() = _cachedToken

    fun login(newValue: NationalDataTable){
        setValue(newValue)
    }

    fun logout(){
        Log.d(TAG, "logout: ")


        CoroutineScope(Dispatchers.IO).launch{
           //   delete the token
        }
    }

    fun setValue(newValue: NationalDataTable?) {
        GlobalScope.launch(Dispatchers.Main) {
            if (_cachedToken.value != newValue) {
                _cachedToken.value = newValue
            }
        }
    }

    fun isConnectedToTheInternet(): Boolean{
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try{
            return cm.activeNetworkInfo.isConnected
        }catch (e: Exception){
            Log.e(TAG, "isConnectedToTheInternet: ${e.message}")
        }
        return false
    }
}