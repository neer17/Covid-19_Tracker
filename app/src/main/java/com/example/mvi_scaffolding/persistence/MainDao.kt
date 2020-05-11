package com.example.mvi_scaffolding.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvi_scaffolding.models.NationalDataTable
import com.example.mvi_scaffolding.models.NationalResourceTable
import com.example.mvi_scaffolding.models.TimeSeriesTable


@Dao
interface MainDao {

    //  for national data
    @Query("SELECT * FROM national_data_table")
    fun getAllData(): LiveData<List<NationalDataTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllData(data: List<NationalDataTable>)

    //  for time series
    @Query("SELECT * FROM time_series_table")
    fun getTimeSeries(): LiveData<List<TimeSeriesTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeSeries(data: List<TimeSeriesTable>)

    //  for national resource
    @Query("SELECT * FROM national_resource_table")
    fun getAllResources(): LiveData<List<NationalResourceTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllResources(data: List<NationalResourceTable>)

}













