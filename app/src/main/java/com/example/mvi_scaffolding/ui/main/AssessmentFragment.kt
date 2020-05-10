package com.example.mvi_scaffolding.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mvi_scaffolding.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_assessment.*

class AssessmentFragment : BaseMainFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assessment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.submit_btn).setOnClickListener {
            Log.d(TAG, "onViewCreated: chips checked ids ${chip_group_1.checkedChipId}")
            
            findNavController().popBackStack()
        }

       view.findViewById<ChipGroup>(R.id.chip_group_1).setOnCheckedChangeListener { group, checkedId ->
           Log.d(TAG, "onViewCreated: checked ids $checkedId")
       }
    }
}
