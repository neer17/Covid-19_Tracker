package com.example.mvi_scaffolding.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "national_data_table")
data class NationalDataTable(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    var account_pk: Int,
    @ColumnInfo
    @Expose
    val active: String,
    @ColumnInfo
    @Expose
    val confirmed: String,
    @ColumnInfo
    @Expose
    val deaths: String,
    @ColumnInfo
    @Expose
    val deltaconfirmed: String,
    @ColumnInfo
    @Expose
    val deltadeaths: String,
    @ColumnInfo
    @Expose
    val deltarecovered: String,
    @ColumnInfo
    @Expose
    val lastupdatedtime: String,
    @ColumnInfo
    @Expose
    val recovered: String,
    @ColumnInfo
    @Expose
    val state: String,
    @ColumnInfo
    @Expose
    val statecode: String
)













