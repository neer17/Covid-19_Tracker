package com.example.mvi_scaffolding.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvi_scaffolding.models.NationalDataTable


@Dao
interface MainDao {

    @Query("SELECT * FROM national_data_table")
    fun getAllData(): LiveData<List<NationalDataTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<NationalDataTable>)
}













