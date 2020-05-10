package com.example.mvi_scaffolding.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose


@Entity(tableName = "time_series_table")
data class TimeSeriesTable(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    var account_pk: Int,
    @ColumnInfo
    @Expose
    val dailyconfirmed: String,
    @ColumnInfo
    @Expose
    val dailydeceased: String,
    @ColumnInfo
    @Expose
    val dailyrecovered: String,
    @ColumnInfo
    @Expose
    val date: String,
    @ColumnInfo
    @Expose
    val totalconfirmed: String,
    @ColumnInfo
    @Expose
    val totaldeceased: String,
    @ColumnInfo
    @Expose
    val totalrecovered: String
)
