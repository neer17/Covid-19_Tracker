package com.example.mvi_scaffolding

import android.app.Activity
import com.example.mvi_scaffolding.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class BaseApplication : DaggerApplication() {
    var mCurrentActivity: Activity? = null

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }
}