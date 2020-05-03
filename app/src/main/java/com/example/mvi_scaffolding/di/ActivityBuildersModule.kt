package com.example.mvi_scaffolding.di

import com.example.mvi_scaffolding.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {
    //  scope required
    @ContributesAndroidInjector(
        modules = [
        ]
    )
    abstract fun contributeMainActivity(): MainActivity
}