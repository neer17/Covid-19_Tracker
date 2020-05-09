package com.example.mvi_scaffolding.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "national_resource_table")
data class NationalResourceTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val pk: Int,
    @ColumnInfo
    @Expose
    val category: String?,
    @ColumnInfo
    @Expose
    val city: String?,
    @ColumnInfo
    @Expose
    val phonenumber: String?,
    @ColumnInfo
    @Expose
    val state: String?
)