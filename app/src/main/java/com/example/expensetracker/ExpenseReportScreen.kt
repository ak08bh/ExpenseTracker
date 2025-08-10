package com.example.expensetracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expensetracker.ui.theme.Chip
import com.example.expensetracker.ui.theme.OnSurfaceVariant
import com.example.expensetracker.ui.theme.Primary
import com.example.expensetracker.ui.theme.SecondContainer
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseReportScreen(viewModel: TrackerViewModel, navController: NavController) {
    val dummyData = getDummyExpenses()
    viewModel.getSumByCategoryForDate(getCurrentDate())
    val expenseSums by viewModel.getSumByCategoryForDate(getCurrentDate()).collectAsState(initial = emptyList())

    val amountMap = expenseSums.associateBy({ it.category }, { it.total_amount })

    // Collect last 7 days data once from ViewModel
    val dummyExpenseMap = mapOf(
        "Aug 10 2025" to mapOf(
            "Food" to 120.0,
            "Travel" to 80.0,
            "Shopping" to 200.0
        ),
        "Aug 09 2025" to mapOf(
            "Food" to 150.0,
            "Travel" to 60.0
        ),
        "Aug 08 2025" to mapOf(
            "Food" to 90.0,
            "Shopping" to 300.0
        )
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        WeeklyExpenseBarChart(expenses = dummyData, modifier = Modifier.padding(top = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        WeeklyExpenseTableDynamicScrollable(dummyExpenseMap)
        Spacer(modifier = Modifier.height(16.dp))
        DailyTotalsHeader(amountMap)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyExpenseTableDynamicScrollable(
    expenseMap: Map<String, Map<String, Double>>
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today = LocalDate.now()

    // Generate last 7 dates from today backwards
    val dates = (0..6).map { offset ->
        today.minusDays(offset.toLong()).format(formatter)
    }

    val horizontalScrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
            Column {
                // Header row
                Row(
                    modifier = Modifier
                        .background(color = Primary)
                        .padding(vertical = 8.dp)
                ) {
                    Text("Date", color = Color.White, modifier = Modifier.width(100.dp))
                    ExpenseCategory.entries.forEach { category ->
                        Text(
                            category.label,
                            color = Color.White,
                            modifier = Modifier.width(100.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Data rows
                dates.forEach { date ->
                    Row(
                        modifier = Modifier
                            .border(0.5.dp, Color.Gray)
                            .padding(vertical = 4.dp)
                    ) {
                        Text(date, modifier = Modifier.width(100.dp))
                        ExpenseCategory.entries.forEach { category ->
                            val amount = expenseMap[date]?.get(category.label)
                            Text(
                                text = amount?.let { "₹${"%.2f".format(it)}" } ?: "-",
                                modifier = Modifier.width(100.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyTotalsHeader(totalsByCategory: Map<String, Double>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecondContainer) // Purple background for header
            .padding(16.dp)
    ) {
        // Date
        Text(
            text = getCurrentDate(),
            color = OnSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Row for categories
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExpenseCategory.entries.forEach { category ->
                val amount = totalsByCategory[category.label] ?: 0.0
                Column(
                    modifier = Modifier
                        .width(100.dp)
                        .background(Chip, shape = MaterialTheme.shapes.medium)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    category.icon?.let { painterResource(id = it) }?.let {
                        Image(
                            painter = it,
                            contentDescription = category.label,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = category.label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "₹${String.format("%.2f", amount)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A1B9A)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyExpenseBarChart(
    expenses: List<ExpenseItem>,
    modifier: Modifier = Modifier,
    barWidthDp: Int = 28,
    barMaxHeightDp: Int = 140,
    barSpacingDp: Int = 20 // increased default spacing
) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }

    val sumsByDate = expenses
        .groupBy { Instant.ofEpochMilli(it.time).atZone(ZoneId.systemDefault()).toLocalDate() }
        .mapValues { (_, list) -> list.sumOf { it.amount } }

    val points = days.map { date -> date to (sumsByDate[date] ?: 0.0) }
    val maxTotal = points.maxOfOrNull { it.second } ?: 0.0
    val maxIndex = points.indexOfFirst { it.second == maxTotal && maxTotal > 0.0 }

    val barWidth = barWidthDp.dp
    val barMaxHeight = barMaxHeightDp.dp
    val barSpacing = barSpacingDp.dp

    LazyRow(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(barSpacing),
        verticalAlignment = Alignment.Bottom,
        userScrollEnabled = true
    ) {
        itemsIndexed(points) { index, (date, total) ->
            val ratio = if (maxTotal <= 0.0) 0f else (total / maxTotal).toFloat()
            val height = if (maxTotal <= 0.0) 6.dp else (barMaxHeight * ratio).coerceAtLeast(6.dp)
            val isMax = index == maxIndex

            Column(
                modifier = Modifier.width(barWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = if (total > 0) "₹${total.toInt()}" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(barWidth),
                    maxLines = 1,
                )
                Spacer(Modifier.height(8.dp)) // a bit more space above the bar
                Box(
                    modifier = Modifier
                        .height(height)
                        .width(barWidth)
                        .background(
                            color = if (isMax) Primary else SecondContainer,
                            shape = MaterialTheme.shapes.small
                        )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDummyExpenses(): List<ExpenseItem> {
    val today = LocalDate.now()
    val random = java.util.Random()

    // Create expenses for last 7 days for each category
    return (0..6).flatMap { offset ->
        val date = today.minusDays(offset.toLong())
        val timeMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        ExpenseCategory.entries.map { category ->
            ExpenseItem(
                title = "${category.label} Expense",
                amount = (50..500).random().toDouble(),
                category = category.label,
                notes = "Note for ${category.label}",
                time = timeMillis
            )
        }
    }
}

