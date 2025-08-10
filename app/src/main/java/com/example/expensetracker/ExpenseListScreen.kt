package com.example.expensetracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expensetracker.model.TrackerModel
import com.example.expensetracker.ui.theme.Chip
import com.example.expensetracker.ui.theme.OnSurfaceVariant
import com.example.expensetracker.ui.theme.Primary
import com.example.expensetracker.ui.theme.SecondContainer
import com.example.expensetracker.ui.theme.Surface
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseListScreen(viewModel: TrackerViewModel, navController: NavController) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var displayedYearMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var totalCount by remember { mutableStateOf(0) }

    var totalAmount by remember { mutableStateOf(0.0) }

    LaunchedEffect(selectedDate) {
        launch {
            viewModel.observeTotalAmountForSelectedDate(
                selectedDate.format(DateTimeFormatter.ofPattern("MMM d yyyy"))
            ).collect { amount ->
                totalAmount = amount
            }
        }

        launch {
            viewModel.observeGetExpenseCountForDate(
                selectedDate.format(DateTimeFormatter.ofPattern("MMM d yyyy"))
            ).collect { count ->
                totalCount = count
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            CustomCalendar(
                displayYearMonth = displayedYearMonth,
                selectedDate = selectedDate,
                onYearMonthChange = { newYm ->
                    displayedYearMonth = newYm
                    val day = minOf(selectedDate.dayOfMonth, newYm.lengthOfMonth())
                    selectedDate = LocalDate.of(newYm.year, newYm.month, day)
                },
                onDateSelected = { picked ->
                    selectedDate = picked
                },
                viewModel
            )
        }

        CommonDivider()

        Spacer(modifier = Modifier.height(16.dp))

        ExpenseAndCountRow(
            date = selectedDate,
            totalAmount = totalAmount,
            totalCount = totalCount
        )

        CommonDivider()

        Spacer(modifier = Modifier.height(16.dp))

        ToggleButtonsRow(viewModel,selectedDate)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomCalendar(
    displayYearMonth: YearMonth,
    selectedDate: LocalDate,
    onYearMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    viewModel: TrackerViewModel
) {
    val years = remember { (2025..2040).toList() } // adjust if you meant 20240
    val months = remember { java.time.Month.values().toList() }

    var monthMenuExpanded by remember { mutableStateOf(false) }
    var yearMenuExpanded by remember { mutableStateOf(false) }

    val monthFormatter = remember { DateTimeFormatter.ofPattern("LLLL") } // full month name
    val monthLabel = displayYearMonth.atDay(1).format(monthFormatter)
    val yearLabel = displayYearMonth.year.toString()

    val daysInMonth = displayYearMonth.lengthOfMonth()
    val firstDayWeekday = displayYearMonth.atDay(1).dayOfWeek.value // 1=Mon ... 7=Sun

    val calendarDays = remember(displayYearMonth) {
        buildList<Int?> {
            repeat(firstDayWeekday - 1) { add(null) }
            for (day in 1..daysInMonth) add(day)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Month / Year selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Month dropdown
            ExposedDropdownMenuBox(
                expanded = monthMenuExpanded,
                onExpandedChange = { monthMenuExpanded = !monthMenuExpanded },
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    readOnly = true,
                    value = monthLabel.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    label = { Text("Month") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthMenuExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = monthMenuExpanded,
                    onDismissRequest = { monthMenuExpanded = false }
                ) {
                    months.forEach { m ->
                        DropdownMenuItem(
                            text = { Text(m.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                monthMenuExpanded = false
                                val newYm = YearMonth.of(displayYearMonth.year, m)
                                onYearMonthChange(newYm)
                            }
                        )
                    }
                }
            }

            // Year dropdown
            ExposedDropdownMenuBox(
                expanded = yearMenuExpanded,
                onExpandedChange = { yearMenuExpanded = !yearMenuExpanded },
                modifier = Modifier.width(120.dp)
            ) {
                TextField(
                    readOnly = true,
                    value = yearLabel,
                    onValueChange = {},
                    label = { Text("Year") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearMenuExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = yearMenuExpanded,
                    onDismissRequest = { yearMenuExpanded = false }
                ) {
                    years.forEach { y ->
                        DropdownMenuItem(
                            text = { Text(y.toString()) },
                            onClick = {
                                yearMenuExpanded = false
                                val newYm = YearMonth.of(y, displayYearMonth.month)
                                onYearMonthChange(newYm)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weekday labels (Mon-Sun)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(32.dp),
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Days grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(200.dp),
            userScrollEnabled = false
        ) {
            items(calendarDays) { day ->
                if (day == null) {
                    Box(modifier = Modifier.size(32.dp)) {}
                } else {
                    val thisDate = displayYearMonth.atDay(day)
                    val isSelected = thisDate == selectedDate

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) SecondContainer else Color.Transparent)
                            .clickable {
                                onDateSelected(thisDate)
                                viewModel.observeTotalAmountForSelectedDate(formatLocalDate(thisDate))
                                       },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.toString(),
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseAndCountRow(
    date: LocalDate,
    totalAmount: Double,
    totalCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(IntrinsicSize.Min), // Make Row height based on tallest child
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Expense Box
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight() // Fill parent's height
                .background(color = Primary, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Expenses",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                color = Surface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .background(
                        color = SecondContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "₹",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = totalAmount.toString(),
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Total Count Box
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight() // Fill parent's height
                .background(color = Primary, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total Count",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .background(
                        color = SecondContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = totalCount.toString(),
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ToggleButtonsRow(viewModel: TrackerViewModel, selectedDate: LocalDate) {
    var selectedToggle by remember { mutableStateOf<GroupByCategoryOrTime?>(GroupByCategoryOrTime.Category) }
    val expensesByCategory by viewModel.getExpensesGroupedByCategoryForDate(formatLocalDate(selectedDate))
        .collectAsState(initial = emptyMap())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp) // space between buttons
    ) {
        ToggleButton(
            text = GroupByCategoryOrTime.Category.type,
            selected = selectedToggle == GroupByCategoryOrTime.Category,
            onClick = { selectedToggle = GroupByCategoryOrTime.Category },
            modifier = Modifier.weight(1f) // takes half width
        )

        ToggleButton(
            text = GroupByCategoryOrTime.Time.type,
            selected = selectedToggle == GroupByCategoryOrTime.Time,
            onClick = { selectedToggle = GroupByCategoryOrTime.Time },
            modifier = Modifier.weight(1f) // takes half width
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (selectedToggle == GroupByCategoryOrTime.Category) {

        GroupByCategory(expensesByCategory = expensesByCategory)
    } else {
        GroupByTime(expensesByCategory = expensesByCategory)
    }
}

@Composable
fun ToggleButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .height(48.dp) // consistent height
            .border(
                width = if (selected) 2.dp else 1.dp, // border thickness
                color = if (selected) Primary else Color.Gray, // border color
                shape = RoundedCornerShape(8.dp) // match button/container rounding
            )
    ) {
        Text(text = text, color = OnSurfaceVariant)
    }
}

@Composable
fun CommonDivider(){
    Divider(
        color = Color.Gray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 8.dp)
    )
}

@Composable
fun GroupByCategory(
    expensesByCategory: Map<ExpenseCategory, List<TrackerModel>>
) {
    var expandedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ExpenseCategory.entries.forEach { category ->
            val expenses = expensesByCategory[category] ?: emptyList()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Category Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            SecondContainer,
                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    IconButton(onClick = {
                        expandedCategory = if (expandedCategory == category) null else category
                    }) {
                        Icon(
                            imageVector = if (expandedCategory == category)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle",
                            tint = OnSurfaceVariant
                        )
                    }
                }

                // Expanded expense list
                if (expandedCategory == category) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Chip,
                                RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        if (expenses.isEmpty()) {
                            Text(
                                "No expenses for this category",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            expenses.forEach { expense ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = expense.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceVariant
                                    )
                                    Text(
                                        text = "₹${expense.amount}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupByTime(
    expensesByCategory: Map<ExpenseCategory, List<TrackerModel>>
) {
    val hasExpenses = expensesByCategory.values.any { it.isNotEmpty() }

    if (!hasExpenses) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No expenses for these date",
                color = OnSurfaceVariant,  // Try Color.Black for visibility
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            expensesByCategory.forEach { (category, expenses) ->
                expenses.forEach { expenseItem ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .background(color = SecondContainer, RoundedCornerShape(8.dp))
                            .padding(12.dp) // internal padding
                    ) {
                        Text(text = "Category: ${expenseItem.category}", color = OnSurfaceVariant)
                        Text(text = "Time: ${expenseItem.time}", color = OnSurfaceVariant)
                        Text(text = "Title: ${expenseItem.title}", color = OnSurfaceVariant)
                        Text(text = "Amount: ₹${expenseItem.amount}", color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}
