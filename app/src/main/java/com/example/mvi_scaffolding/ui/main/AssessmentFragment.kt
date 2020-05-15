package com.example.mvi_scaffolding.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.utils.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
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

        Log.d(TAG, "onViewCreated: auth fragment")
        
        view.findViewById<MaterialButton>(R.id.submit_btn).setOnClickListener {

            getThreatLevel()
            findNavController().popBackStack()
        }
    }

    private fun getThreatLevel() {
        var cg1CheckedCount = 0
        val cg1Count = chip_group_1.childCount
        for (i in 0 until cg1Count) {
            val chip = chip_group_1.getChildAt(i) as Chip
            if (chip.isChecked) {
                Log.d(TAG, "getThreatLevel: chip ${chip.text}")
                cg1CheckedCount++
            }
        }
        var cg2CheckedCount = 0
        val cg2Count = chip_group_2.childCount
        for (i in 0 until cg2Count) {
            val chip = chip_group_2.getChildAt(i) as Chip
            if (chip.isChecked) {
                Log.d(TAG, "getThreatLevel: chip ${chip.text}")
                cg2CheckedCount++
            }
        }

        var cg3OptionSelected = "No"
        if (chip_group_3.checkedChipId == R.id.cg_3_choice_1)
            cg3OptionSelected = "Yes"

        val radioBtnId = radio_group.checkedRadioButtonId
        var radioBtnSelected: Int = 0
        if (radioBtnId == R.id.rb_1)
            radioBtnSelected = 1
        else if (radioBtnId == R.id.rb_2)
            radioBtnSelected = 2

        //  logic for threat level
        var threatLevel = Constants.SAFE
        if (cg1CheckedCount > 1)
            threatLevel = Constants.SYMPTOMS
        else if (cg2CheckedCount > 1)
            threatLevel = Constants.VULNERABLE
        else if (cg3OptionSelected == "Yes")
            threatLevel = Constants.QUARANTINE_TRAVEL_HISTORY

        //  TODO: make request for push notifications
        if (radioBtnSelected == 1)
            threatLevel = Constants.DANGER
        if (radioBtnSelected == 2)
            threatLevel = Constants.DANGER

        editor.putString(Constants.DANGER_LEVEL, threatLevel)
        editor.apply()
    }
}
