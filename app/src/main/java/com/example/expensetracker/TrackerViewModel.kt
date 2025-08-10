package com.example.expensetracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.dao.TrackerDao
import com.example.expensetracker.model.CategoryAmountSum
import com.example.expensetracker.model.CategoryDateAmount
import com.example.expensetracker.model.TrackerModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(val dao: TrackerDao) : ViewModel() {
    val isProgress = mutableStateOf(false)
    val titleError = mutableStateOf("")
    val amountError = mutableStateOf("")
    val categoryError = mutableStateOf("")
    val popupNotification = mutableStateOf<Event<String>?>(null)
    val totalSpentToday =   mutableStateOf(0.0)

    private val _expenseMap = MutableStateFlow<Map<String, Map<String, Double>>>(emptyMap())
    val expenseMap: StateFlow<Map<String, Map<String, Double>>> = _expenseMap

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.getDefault())

    init {
        totalSpentToday()
    }

    //validation for title and amount
    fun checkValidation(title: String, amount: String, category: String): Boolean {
        var valid = true

        if (title.isBlank()) {
            titleError.value = "Title cannot be empty"
            valid = false
        } else {
            titleError.value = ""
        }

        val amountValue = amount.toDoubleOrNull()
        if (amountValue == null) {
            amountError.value = "Amount is invalid"
            valid = false
        } else if (amountValue <= 0) {
            amountError.value = "Amount must be greater than 0"
            valid = false
        } else {
            amountError.value = ""
        }

        if(category.isEmpty()){
            categoryError.value = "Please select a category"
            valid = false
        }else{
            categoryError.value = ""
        }

        return valid
    }

    fun checkDuplicate(title: String, amount: String, category: String, notes: String, callBack : (Boolean) -> Unit)  {
        val amountValue = amount.toDouble()
        viewModelScope.launch {
            val count = dao.getDuplicateExpense(title,amountValue,category)
            callBack(count >= 1)
        }
    }

    fun insertExpense(title: String, amount: String, category: String, notes: String) {
        val amountValue = amount.toDouble()
        val trackerModel = TrackerModel(
            title = title,
            amount = amountValue,
            category = category,
            notes = notes,
            date = getCurrentDate(),   // "Aug 8 2025"
            time = getCurrentTime(),   // "9:12 PM"
            id = null
        )
        viewModelScope.launch {
            dao.insertExpenseData(trackerModel)
        }
    }

    fun totalSpentToday() {
        viewModelScope.launch {
            val totalToday = dao.getTotalAmountForDate(getCurrentDate()) ?: 0.0
            totalSpentToday.value = totalToday
        }
    }

    fun observeTotalAmountForSelectedDate(date: String): Flow<Double> {
        return dao.getTotalAmountForDateFlow(date)
    }

    fun observeGetExpenseCountForDate(date: String): Flow<Int> {
        return dao.getExpenseCountForDate(date)
    }

    fun getExpensesGroupedByCategoryForDate(date: String): Flow<Map<ExpenseCategory, List<TrackerModel>>> {
        return dao.getAllExpensesByDate(date).map { expenseList ->
            val groupedMap = expenseList.mapNotNull { expense ->
                val category = ExpenseCategory.entries.find {
                    it.label.equals(expense.category, ignoreCase = true)
                }
                category?.let { it to expense }
            }
                .groupBy(
                    keySelector = { it.first },
                    valueTransform = { it.second }
                )

            ExpenseCategory.entries.associateWith { category ->
                groupedMap[category] ?: emptyList()
            }
        }
    }

    fun getSumByCategoryForDate(date: String): Flow<List<CategoryAmountSum>>{
       return dao.getSumByCategoryForDate(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLast7Days(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()
        return (0L..6L).map { today.minusDays(it).format(formatter) }.reversed()
    }
    @RequiresApi(Build.VERSION_CODES.O)
     fun loadLast7DaysExpenses() {
        viewModelScope.launch {
            val last7Dates = (0..6).map {
                LocalDate.now().minusDays(it.toLong()).format(formatter)
            }

            val expenses = dao.getExpensesForDates(last7Dates)

            val map = expenses.groupBy { it.date }
                .mapValues { entry ->
                    entry.value.groupBy { it.category }
                        .mapValues { catEntry ->
                            catEntry.value.sumOf { it.amount }
                        }
                }

            _expenseMap.value = map
        }
    }


}