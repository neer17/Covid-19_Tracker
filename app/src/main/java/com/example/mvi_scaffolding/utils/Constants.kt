package com.example.mvi_scaffolding.utils

class Constants {

    companion object{

        const val BASE_URL = "https://api.covid19india.org"

        const val NETWORK_TIMEOUT = 3000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing
    }
}