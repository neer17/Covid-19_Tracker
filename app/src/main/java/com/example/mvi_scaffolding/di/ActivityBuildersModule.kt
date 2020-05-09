package com.example.mvi_scaffolding.di

import com.example.mvi_scaffolding.di.main.MainFragmentBuildersModule
import com.example.mvi_scaffolding.di.main.MainModule
import com.example.mvi_scaffolding.di.main.MainScope
import com.example.mvi_scaffolding.di.main.MainViewModelModule
import com.example.mvi_scaffolding.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {
    @MainScope
    @ContributesAndroidInjector(
        modules = [
            MainModule::class,
            MainFragmentBuildersModule::class,
            MainViewModelModule::class
        ]
    )
    abstract fun contributeMainActivity(): MainActivity
}