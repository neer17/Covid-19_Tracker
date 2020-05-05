package com.example.mvi_scaffolding.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent.GetNationalDataEvent

/**
 * A simple [Fragment] subclass.
 */
class CovidUpdatesFragment : BaseMainFragment() {
    lateinit var adapter : CovidUpdatesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_covid_updates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        subscribeObservers()

        viewModel.setStateEvent(GetNationalDataEvent())
    }

    private fun setRecyclerView() {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.covid_updates_frag_rv)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = CovidUpdatesAdapter()
        recyclerView.adapter = adapter
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer {dataState -> 
            dataState.data?.let { 
                it.data?.let {
                    it.getContentIfNotHandled()?.let {
                        it.nationalData?.let {nationalData ->
                            viewModel.setNationalData(nationalData)
                        }
                    }
                }
            }
        })
        
        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState ->  
            viewState?.let {viewState -> 
               viewState.nationalData?.let {
                   Log.d(TAG, "subscribeObservers: nation wide data ${it.nationWideData}")

                   //   submitting data to adapter
                   adapter.submitList(it.nationWideData)
               }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelActiveJobs()
    }

}
