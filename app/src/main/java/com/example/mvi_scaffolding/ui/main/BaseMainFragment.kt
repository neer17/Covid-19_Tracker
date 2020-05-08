package com.example.mvi_scaffolding.ui.main

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.mvi_scaffolding.models.NationalDataTable
import com.example.mvi_scaffolding.session.SessionManager
import com.example.mvi_scaffolding.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import java.util.*
import javax.inject.Inject

abstract class BaseMainFragment : DaggerFragment() {
    val TAG = "AppDebug: " + BaseMainFragment::class.java.simpleName

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var sessionManager: SessionManager

    lateinit var geocoder: Geocoder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        geocoder = Geocoder(activity, Locale.getDefault())

        viewModel = activity?.run {
            ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    fun cancelActiveJobs(){
        // When a fragment is destroyed make sure to cancel any on-going requests.
        // Note: If you wanted a particular request to continue even if the fragment was destroyed, you could write a
        //       special condition in the repository or something.
        viewModel.cancelActiveJobs()
    }

    fun addIncDecSymbol(item: NationalDataTable): Array<String> {
        var finalStringDeltaConfirmed = ""
        var finalStringDeltaRecovered = ""
        var finalStringDeltaDeceased = ""

        if (item.deltaconfirmed.toInt() > 0) {
            val upArrowHexCode = "A71B".toInt(16).toChar()
            finalStringDeltaConfirmed = upArrowHexCode + " " + item.deltaconfirmed
        } else if (item.deltaconfirmed.toInt() < 0) {
            val downArrowHexCode = "A71C".toInt(16).toChar()
            finalStringDeltaConfirmed = downArrowHexCode + " " + item.deltaconfirmed
        } else
            finalStringDeltaConfirmed = item.deltaconfirmed

        if (item.deltarecovered.toInt() > 0) {
            val upArrowHexCode = "A71B".toInt(16).toChar()
            finalStringDeltaRecovered = upArrowHexCode + " " + item.deltarecovered
        } else if (item.deltaconfirmed.toInt() < 0) {
            val downArrowHexCode = "A71C".toInt(16).toChar()
            finalStringDeltaRecovered = downArrowHexCode + " " + item.deltarecovered
        }
        else
            finalStringDeltaRecovered = item.deltarecovered

        if (item.deltadeaths.toInt() > 0) {
            val upArrowHexCode = "A71B".toInt(16).toChar()
            finalStringDeltaDeceased = upArrowHexCode + " " + item.deltadeaths
        } else if (item.deltadeaths.toInt() < 0) {
            val downArrowHexCode = "A71C".toInt(16).toChar()
            finalStringDeltaDeceased = downArrowHexCode + " " + item.deltadeaths
        }
        else
            finalStringDeltaDeceased = item.deltadeaths

       return arrayOf(finalStringDeltaConfirmed, finalStringDeltaRecovered, finalStringDeltaDeceased)
    }

}
