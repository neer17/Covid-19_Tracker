package com.example.mvi_scaffolding.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "main_table")
data class MainEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "account_pk")
    var account_pk: Int
)













