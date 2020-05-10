package com.example.mvi_scaffolding.di.auth

import com.example.mvi_scaffolding.ui.auth.AuthFragment
import com.example.mvi_scaffolding.ui.auth.SplashScreenFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeAuthFragment(): AuthFragment

    @ContributesAndroidInjector()
    abstract fun contributeSplashScreenFragment(): SplashScreenFragment

}