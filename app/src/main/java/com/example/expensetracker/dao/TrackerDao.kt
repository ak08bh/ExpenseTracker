package com.example.expensetracker.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expensetracker.model.CategoryAmountSum
import com.example.expensetracker.model.CategoryDateAmount
import com.example.expensetracker.model.TrackerModel
import com.example.expensetracker.table_name
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseData(trackerModel: TrackerModel)

    @Query("SELECT COUNT(*) FROM $table_name WHERE title = :title AND amount = :amount AND category = :category")
    suspend fun getDuplicateExpense(title: String, amount: Double, category: String): Int

    @Query("SELECT SUM(amount) FROM $table_name WHERE date = :currentDate")
    suspend fun getTotalAmountForDate(currentDate: String): Double?

    @Query("SELECT SUM(amount) FROM $table_name  WHERE date = :selectedDate")
    fun getTotalAmountForDateFlow(selectedDate: String): Flow<Double>

    @Query("SELECT COUNT(*) FROM $table_name WHERE date = :selectedDate")
    fun getExpenseCountForDate(selectedDate: String): Flow<Int>

    @Query("SELECT * FROM $table_name WHERE date = :selectedDate")
    fun getAllExpensesByDate(selectedDate: String): Flow<List<TrackerModel>>

    @Query("SELECT category, SUM(amount) as total_amount FROM $table_name WHERE date = :date GROUP BY category")
    fun getSumByCategoryForDate(date: String): Flow<List<CategoryAmountSum>>

    @Query("""
    SELECT date, category, SUM(amount) as total_amount
    FROM $table_name
    WHERE date IN (:last7Days)
    GROUP BY date, category
""")
    fun getSumByDateAndCategoryLast7Days(last7Days: List<String>): Flow<List<CategoryDateAmount>>

}