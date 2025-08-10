package com.example.expensetracker

import android.content.Context
import androidx.room.Room
import com.example.expensetracker.dao.TrackerDao
import com.example.expensetracker.database.TrackerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrackerDatabase =
        Room.databaseBuilder(
            context,
            TrackerDatabase::class.java,
            database_name
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideExpenseDao(database: TrackerDatabase): TrackerDao =
        database.trackerDao()

}


