package com.example.mvi_scaffolding.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvi_scaffolding.R


class NationWideDataDisplayFragment : BaseMainFragment() {
    lateinit var adapter: CovidUpdatesAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nation_wide_data_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        subscribeObservers()
    }

    private fun setRecyclerView() {
        recyclerView = view!!.findViewById<RecyclerView>(R.id.all_data_rv)
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { mainViewState ->
                mainViewState.nationalData?.let { nationalData ->

                    //  getting strings with up/down arrows prepended
                    adapter = CovidUpdatesAdapter { position ->
                        val currentItem = nationalData.nationWideDataList[position]
                        addIncDecSymbol(currentItem)
                    }

                    recyclerView.adapter = adapter
                    adapter.submitList(nationalData.nationWideDataList)
                }
            }
        })
    }

}
