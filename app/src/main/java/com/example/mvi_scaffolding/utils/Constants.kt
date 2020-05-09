package com.example.mvi_scaffolding.utils

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
    }
}