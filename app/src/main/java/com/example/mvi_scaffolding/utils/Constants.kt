package com.example.mvi_scaffolding.utils

import android.graphics.Color

class Constants {

    companion object{

        const val BASE_URL = "https://api.covid19india.org"

        const val NETWORK_TIMEOUT = 3000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

        //  home fragment
        const val GOVERNMENT_HELPLINE = "Government Helpline"
        const val COVID_19_TESTING_LAB = "CoVID-19 Testing Lab"
        const val POLICE = "Police"
        const val FIRE_BRIGADE = "Fire Brigade"
        const val FREE_FOOD = "Free Food"
        const val HOSPITALS_AND_CENTERS = "Hospitals and Centers"

        // Shared Preference Files:
        const val APP_PREFERENCES: String = "com.com.example.mvi_scaffolding.APP_PREFERENCES"
        const val LAST_NETWORK_REQUEST_TIME = "LAST_NETWORK_REQUEST_TIME"
        const val LAST_KNOW_CITY_AND_STATE = "LAST_KNOW_CITY_AND_STATE"
        const val USERNAME = "USERNAME"
        const val USER_LOGGED_IN = "USER_LOGGED_IN"
        const val PHONE_NUMBER = "PHONE_NUMBER"
        const val UID = "UID"

        //  Assessment fragment
        const val SAFE = "Safe"
        const val INDOOR = "Quarantine"
        const val DANGER = "Danger"
        const val VULNERABLE = "Vulnerable"
        const val SYMPTOMS = "Symptoms"
        const val QUARANTINE_TRAVEL_HISTORY = "Quarantine Travel History"
        const val DANGER_LEVEL = "DANGER_LEVEL"

        //  main activity
        const val LATEST_UPDATED_TIME = "LATEST_UPDATED_TIME"

        //  graphs
         var PRIMARY_RED = Color.rgb(255, 7, 58)
         var SECONDARY_RED = Color.argb(32, 255, 7, 58)
         var TEXT_RED = Color.argb(153, 255, 7, 58)
         var PRIMARY_GREEN = Color.rgb(40, 167, 69)
         var SECONDARY_GREEN = Color.argb(32, 40, 167, 69)
         var TEXT_GREEN = Color.argb(153, 40, 167, 69)
         var PRIMARY_GRAY = Color.rgb(108, 117, 125)
         var SECONDARY_GRAY = Color.argb(32, 108, 117, 125)
         var TEXT_GRAY = Color.argb(153, 108, 117, 125)

        //  firebase messaging service
        const val DATA_LANG = "DATA_LANG"
        const val DATA_LAT = "DATA_LAT"
        const val DATA_TIME = "DATA_TIME"
        const val CONTRACTION_DETAILS = "CONTRACTION_DETAILS"

    }
}