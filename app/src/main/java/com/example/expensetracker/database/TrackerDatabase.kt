package com.example.expensetracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.expensetracker.dao.TrackerDao
import com.example.expensetracker.model.TrackerModel

@Database(entities = [TrackerModel::class], version = 1,exportSchema = false)
abstract class TrackerDatabase : RoomDatabase() {

    abstract fun trackerDao() : TrackerDao

}