package com.example.mvi_scaffolding.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mvi_scaffolding.models.MainEntity


@Database(entities = [MainEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

//    abstract fun getAuthTokenDao(): AuthTokenDao

    companion object{
        val DATABASE_NAME: String = "app_db"
    }
}








