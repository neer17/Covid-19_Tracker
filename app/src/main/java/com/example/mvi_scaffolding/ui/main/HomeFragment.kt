package com.example.mvi_scaffolding.ui.main

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.api.main.network_responses.NationalResource
import com.example.mvi_scaffolding.firebase.FirebaseHandler
import com.example.mvi_scaffolding.utils.Constants
import kotlinx.android.synthetic.main.frag_home_layout_middle.*
import kotlinx.android.synthetic.main.frag_home_layout_part_end.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_home_frag_card.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : BaseMainFragment() {
    var fromDegrees: Float = 0f
    var toDegrees: Float = 90f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        home_frag_card_tv.setOnClickListener {
            setAnimationOnArrow()
        }
        arrow.setOnClickListener {
            setAnimationOnArrow()
        }
        assessment_btn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_assessmentFragment)
        }
        call_helpline_btn.setOnClickListener {
            Intent(Intent.ACTION_DIAL).let {
                it.data = Uri.parse("tel:1075")
                startActivity(it)
            }
        }

        subscribeObservers()
    }


    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->

            viewState.nationalResource?.let { nationalResource ->
                viewState.cityAndState?.let {
                    val city = it[0]
                    GlobalScope.launch(Main) {
                        coroutineScope {
                            //  set data and start shimmer
                            shimmer_container_frag_home_end.startShimmer()

                            setCityName(city)
                            setResourcesNumbers(city, nationalResource)

                            shimmer_container_frag_home_end.stopShimmer()
                        }
                    }
                }
            }

             viewState.contractionLocation?.let {
                 val (lat, lang) = it
                 val location = getReadableLocation(lat, lang)

                 viewState.contractionTime?.let {time ->
                     val formattedTime = convertTime(time)
                     home_frag_color_card_tv.text = "You got contracted at $location at time $formattedTime"
                     home_frag_color_card.setBackgroundColor(resources.getColor(R.color.red))
                 }
             }

            viewState.threatLevel?.let {
                val username = sharedPreferences.getString(Constants.USERNAME, null)
                when (it) {
                    Constants.SYMPTOMS -> {
                        home_frag_color_card_tv.text =
                            "$username, you may have symptoms, STAY INDOORS"
                        home_frag_color_card.setBackgroundColor(resources.getColor(R.color.yellow))
                    }
                    Constants.QUARANTINE_TRAVEL_HISTORY -> {
                        home_frag_color_card_tv.text =
                            "$username, You have a travel history, stay in QUARANTINE for 14 days"
                        home_frag_color_card.setBackgroundColor(resources.getColor(R.color.yellow))
                    }
                    Constants.VULNERABLE -> {
                        home_frag_color_card_tv.text =
                            "$username, You are most vulnerable to COVID-19, STAY INDOORS"
                        home_frag_color_card.setBackgroundColor(resources.getColor(R.color.red))
                    }
                    Constants.DANGER -> {
                        home_frag_color_card_tv.text =
                            "$username, You might have been contracted with COVID-19, GET HELP NOW"
                        home_frag_color_card.setBackgroundColor(resources.getColor(R.color.red))

                        //  updating the database
                        val uid = sharedPreferences.getString(Constants.UID, null)
                        uid?.let {
                            FirebaseHandler(activity!!).updateCovidPositiveStatus(it, true)
                                .addOnSuccessListener {
                                    Log.d(TAG, "subscribeObservers: data uploaded success")
                                }.addOnFailureListener {
                                    Log.e(TAG, "subscribeObservers: ", it)
                                }
                        }
                    }
                    else -> {
                        home_frag_color_card_tv.text = "$username, You are safe"
                    }
                }
            }
        })
    }

    private fun convertTime(timeInMillis: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.UK)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return simpleDateFormat.format(calendar.time)
    }

    private fun getReadableLocation(lat: Double, lang: Double): String {
        geocoder = Geocoder(activity, Locale.getDefault())
        return geocoder.getFromLocation(lat, lang, 1)[0].getAddressLine(0)
    }


    private fun setCityName(city: String) {
        city_name.text = city
    }

    private fun setResourcesNumbers(city: String, nationalResource: NationalResource) {
        val cityBasedResources = nationalResource.nationalResource.filter { it.city == city }
        val testingLabsList =
            cityBasedResources.filter { it.category == Constants.COVID_19_TESTING_LAB }
        val freeFoodList = cityBasedResources.filter { it.category == Constants.FREE_FOOD }
        val police = cityBasedResources.filter { it.category == Constants.POLICE }
        val fireBrigadeList = cityBasedResources.filter { it.category == Constants.FIRE_BRIGADE }
        val governmentHelpline =
            cityBasedResources.filter { it.category == Constants.GOVERNMENT_HELPLINE }
        val hospitalsAndCenters =
            cityBasedResources.filter { it.category == Constants.HOSPITALS_AND_CENTERS }

        //  updating UI
        var concatenatingString = ""
        testingLabsList.forEach {
            if (!it.phonenumber.isNullOrEmpty())
                concatenatingString += it.phonenumber + ", "
        }
        testing_labs_numbers.text = concatenatingString
        testing_labs_numbers.isSelected = true


        concatenatingString = ""
        governmentHelpline.forEach {
            if (!it.phonenumber.isNullOrEmpty())
                concatenatingString += it.phonenumber + ", "
        }
        government_helpline_numbers.text = concatenatingString
        government_helpline_numbers.isSelected = true


        concatenatingString = ""
        freeFoodList.forEach {
            if (!it.phonenumber.isNullOrEmpty())
                concatenatingString += it.phonenumber + ", "
        }
        free_food_numbers.text = concatenatingString
        free_food_numbers.isSelected = true


        concatenatingString = ""
        police.forEach {
            if (!it.phonenumber.isNullOrEmpty())
                concatenatingString += it.phonenumber + ", "
        }
        police_numbers.text = concatenatingString
        police_numbers.isSelected = true


        concatenatingString = ""
        fireBrigadeList.forEach {
            if (!it.phonenumber.isNullOrEmpty())
                concatenatingString += it.phonenumber + ", "
        }
        fire_brigade_numbers.text = concatenatingString
        fire_brigade_numbers.isSelected = true


        concatenatingString = ""
        hospitalsAndCenters.forEach {
            if (!it.phonenumber.isNullOrEmpty())
                concatenatingString += it.phonenumber + ", "
        }
        hospitals_numbers.text = concatenatingString
        hospitals_numbers.isSelected = true
    }

    private fun setAnimationOnArrow() {
        val anim = RotateAnimation(
            fromDegrees,
            toDegrees,
            Animation.RELATIVE_TO_SELF,
            .5f,
            Animation.RELATIVE_TO_SELF,
            .5f
        )
        anim.interpolator = LinearInterpolator()
        anim.fillAfter = true
        anim.duration = 200
        arrow.startAnimation(anim)
        toDegrees = if (toDegrees == 0f) 90f
        else 0f

        //  making linear layout visible
        val visibility = frag_home_layout_to_be_hidden.isVisible
        if (visibility)
            frag_home_layout_to_be_hidden.visibility = View.GONE
        else
            frag_home_layout_to_be_hidden.visibility = View.VISIBLE

    }

}
