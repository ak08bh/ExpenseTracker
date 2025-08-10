package com.example.expensetracker

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

const val table_name : String = "Tracker"
const val database_name : String = "TrackerDatabase"


@RequiresApi(Build.VERSION_CODES.O)
fun getLast7DaysFormatted(): List<String> {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    val today = LocalDate.now()
    return (0..6).map { offset ->
        today.minusDays(offset.toLong()).format(formatter)
    }
}

@Composable
fun NotificationMessage(viewModel: TrackerViewModel) {
    val notifState = viewModel.popupNotification.value
    val notifMessage = notifState?.getContentIfNotHandled()

    if(notifMessage != null) {
        Toast.makeText(LocalContext.current,notifMessage,Toast.LENGTH_LONG).show()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatLocalDate(localDate: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.getDefault())
    return localDate.format(formatter)
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("MMM d yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}

fun getCurrentTime(): String {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    return timeFormat.format(Date())
}


fun navigateTo(navController: NavController, destinationScreen: DestinationScreen){
    navController.navigate(destinationScreen.route){
        popUpTo(destinationScreen.route)
        launchSingleTop = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonProgressSpinner(){
    Row(
        modifier = Modifier
            .height(height = 25.dp)
            .width(width = 25.dp)
            .clickable(enabled = false) { },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = ProgressIndicatorDefaults.CircularIndicatorTrackGapSize
        )
    }
}

enum class GroupByCategoryOrTime(val type: String){
    Category("Category"),
    Time("Time")
}

enum class ExpenseCategory(val label: String, val icon: Int?) {
    Staff("Staff",R.drawable.staff),
    Travel("Travel",R.drawable.travel),
    Food("Food",R.drawable.food),
    Utility("Utility",R.drawable.utility),
}

@Entity(tableName = "expenses")
data class ExpenseItem(
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String? = null,
    val time: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int? = null
)


