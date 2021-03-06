package com.example.mvi_scaffolding.di.main

import com.example.mvi_scaffolding.api.main.OpenApiMainService
import com.example.mvi_scaffolding.persistence.AppDatabase
import com.example.mvi_scaffolding.persistence.MainDao
import com.example.mvi_scaffolding.repository.main.MainRepository
import com.example.mvi_scaffolding.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {
    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideMainRepository(
        openApiMainService: OpenApiMainService,
        sessionManager: SessionManager,
        mainDao: MainDao
    ): MainRepository {
        return MainRepository(openApiMainService, sessionManager, mainDao)
    }

//    @MainScope
//    @Provides
//    fun provideLocationRequest(): LocationRequest {
//        return LocationServices.getFusedLocationProviderClient()
//    }

    @MainScope
    @Provides
    fun provideMainDao(db: AppDatabase): MainDao {
        return db.getMainDao()
    }

}