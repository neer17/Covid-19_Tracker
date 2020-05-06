package com.example.mvi_scaffolding.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mvi_scaffolding.models.NationalDataTable


@Database(entities = [NationalDataTable::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getMainDao(): MainDao

    companion object{
        val DATABASE_NAME: String = "app_db"
    }
}








