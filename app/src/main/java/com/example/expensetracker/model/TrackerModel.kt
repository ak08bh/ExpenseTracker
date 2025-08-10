package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.table_name


@Entity(tableName = table_name)
data class TrackerModel(
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String? = null,
    val date: String,   // e.g. "Aug 8 2025"
    val time: String,   // e.g. "9:12 PM"
    @PrimaryKey(autoGenerate = true) val id: Int? = null
)
