package com.example.expensetracker

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.expensetracker.ui.theme.OnSurfaceVariant
import com.example.expensetracker.ui.theme.Primary

enum class BottomNavigationItem(val icon: Int, val navDestinationScreen: DestinationScreen) {
      Entry(R.drawable.entry,  DestinationScreen.ExpenseEntry),
      List(R.drawable.list, DestinationScreen.ExpenseList),
      Report(R.drawable.report,  DestinationScreen.ExpenseReport)
}

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(Modifier.fillMaxWidth()) {
        // outside top shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
                .zIndex(1f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White) // your light container; or Color.Transparent
                .padding(vertical = 12.dp)
                .zIndex(2f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavigationItem.entries.forEach { item ->
                val route = item.navDestinationScreen.route
                val selected = currentRoute == route

                val tint by animateColorAsState(
                    if (selected) Primary
                    else OnSurfaceVariant,
                    label = "iconTint"
                )
                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.12f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                    label = "iconScale"
                )
                Image(
                    painter = painterResource(item.icon),
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(25.dp) // touch target
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clickable {
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    colorFilter = ColorFilter.tint(tint)
                )
            }
        }
    }
}