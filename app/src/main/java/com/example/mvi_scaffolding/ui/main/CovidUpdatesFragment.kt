package com.example.mvi_scaffolding.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent.GetNationalDataEvent

/**
 * A simple [Fragment] subclass.
 */
class CovidUpdatesFragment : BaseMainFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_covid_updates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()

        viewModel.setStateEvent(GetNationalDataEvent())
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "subscribeObservers: data state ${it.data}")
            
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelActiveJobs()
    }

}
