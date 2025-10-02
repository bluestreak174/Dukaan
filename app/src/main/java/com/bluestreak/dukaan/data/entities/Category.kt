package com.bluestreak.dukaan.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_master")
data class Category (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
)
