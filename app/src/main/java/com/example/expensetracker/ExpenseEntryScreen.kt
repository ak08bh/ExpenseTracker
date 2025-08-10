package com.example.expensetracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expensetracker.ui.theme.Chip
import com.example.expensetracker.ui.theme.OnSurfaceVariant
import com.example.expensetracker.ui.theme.Outline
import com.example.expensetracker.ui.theme.Primary
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.expensetracker.ui.theme.ImageContainer
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.zIndex

@Composable
fun ExpenseEntryScreen(viewModel: TrackerViewModel, navController: NavController) {
    val focus = LocalFocusManager.current

    val title = remember { mutableStateOf(TextFieldValue()) }
    val amount = remember { mutableStateOf(TextFieldValue()) }
    val notes = remember { mutableStateOf(TextFieldValue()) }
    val selectedCategory = remember { mutableStateOf<ExpenseCategory?>(null) }

    // Animation state
    val animateOnAdd = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f, fill = true)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(20.dp))
            Container(viewModel.totalSpentToday.value)
            Spacer(Modifier.height(10.dp))
            TextFieldInput(title,
                "Title",
                viewModel.titleError.value,
                onTextChange = {
                viewModel.titleError.value = ""
            })
            TextFieldInput(
                amount,
                "Amount",
                viewModel.amountError.value,
                onTextChange = {
                    viewModel.amountError.value = ""
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Select Category",
                fontWeight = FontWeight.Medium,
                color = Primary,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(2.dp))
            CategoryChipGrid(
                selected = selectedCategory.value,
                onSelect = {
                    selectedCategory.value = it
                    viewModel.categoryError.value = ""
                },
                viewModel.categoryError.value
            )
            TextFieldInput(
                state = notes,
                title = "Notes (optional)",
                singleLine = false,
                maxChars = 100
            )
            Spacer(Modifier.height(16.dp)) // bottom padding inside scroll

            ImageUpload()
        }
        Button(
            onClick = {
                focus.clearFocus(true)
                val selectedLabel = selectedCategory.value?.label ?: ""
                val valid = viewModel.checkValidation(
                    title.value.text,
                    amount.value.text,
                    selectedLabel)

                if(valid){
                     viewModel.checkDuplicate(
                        title.value.text,
                        amount.value.text,
                        selectedLabel,
                        notes.value.text,
                    ){duplicate ->
                        if(duplicate){
                            viewModel.popupNotification.value = Event("These record is already added")

                        }else{
                            viewModel.insertExpense(title.value.text,
                                amount.value.text,
                                selectedLabel,
                                notes.value.text
                            )
                            viewModel.popupNotification.value = Event("Expense for $selectedLabel is Added")
                            animateOnAdd.value = true

                            title.value = TextFieldValue("")
                            notes.value = TextFieldValue("")
                            amount.value = TextFieldValue("")
                            selectedCategory.value = null
                            // Show animation only when animateOnAdd is true
                            animateOnAdd.value = true
                        }
                    }
                }
                      },
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(5.dp)
        ) {
            if (viewModel.isProgress.value) {
                CommonProgressSpinner()
            } else {
                Text("Submit")
            }
        }
        Spacer(Modifier.height(8.dp))
    }
    // Animation overlay (example: fade + scale in/out)
    AnimatedVisibility(
        visible = animateOnAdd.value,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)) // semi-transparent black background
                .wrapContentSize(Alignment.Center)
                .zIndex(1f) // to appear on top
        ) {
            AnimatedCircleNotes(notes.value.text)
        }
    }
    // Reset animation after some delay
    if (animateOnAdd.value) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1500) // 1.5 seconds animation display
            animateOnAdd.value = false
            viewModel.totalSpentToday()
        }
    }
}

@Composable
fun AnimatedCircleNotes(notes: String, modifier: Modifier = Modifier) {
    // Animate the scale from 0.8f to 1.2f repeatedly
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Define 3 overlapping circles with different colors and positions relative to center
    val colors = listOf(Color(0xFFEF5350), Color(0xFF42A5F5), Color(0xFF66BB6A))

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(180.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 4

            // Draw 3 overlapping circles arranged in a triangle shape
            val centerX = size.width / 2
            val centerY = size.height / 2

            val offsets = listOf(
                Offset(centerX - radius, centerY),
                Offset(centerX + radius, centerY),
                Offset(centerX, centerY - radius)
            )

            for (i in colors.indices) {
                drawCircle(
                    color = colors[i],
                    radius = radius,
                    center = offsets[i],
                    alpha = 0.7f
                )
            }
        }

        // Notes text in center
        Text(
            text = notes.ifEmpty { "notes added" },
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun TextFieldInput(
    state: MutableState<TextFieldValue>,
    title: String,
    errorMessage: String = "",
    onTextChange: (TextFieldValue) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxChars: Int? = null // optional max char limit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = state.value,
            onValueChange = { newValue ->
                if (maxChars == null || newValue.text.length <= maxChars) {
                    state.value = newValue
                    onTextChange(newValue)
                }
            },
            label = { Text(text = title, color = OnSurfaceVariant) },
            isError = errorMessage.isNotEmpty(),
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 3,
            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Outline,
                errorContainerColor = Color.White,
                cursorColor = Outline,
                errorCursorColor = Outline
            ),
        )
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
            )
        }
        if (maxChars != null) {
            Text(
                text = "${state.value.text.length} / $maxChars",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryChipGrid(
    selected: ExpenseCategory?,
    onSelect: (ExpenseCategory) -> Unit,
    errorMessage: String = "",
    categories: List<ExpenseCategory> = ExpenseCategory.entries,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.forEach { cat ->
            FilterChip(
                selected = selected == cat,
                onClick = { onSelect(cat) },
                label = { Text(cat.label) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Chip),
            )
        }
    }
    if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = Color.Red,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun Container(value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Chip, shape = RoundedCornerShape(10.dp))
            .height(30.dp)
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = "Total Spent Today : ", fontSize = 16.sp,color = Outline)
        Image(
            painter = painterResource(id = R.drawable.rupee), // your rupee icon drawable name here
            contentDescription = "Rupee",
            modifier = Modifier.size(16.dp) ,
            colorFilter = ColorFilter.tint(color = OnSurfaceVariant)// adjust size here
        )
        Text(text = value.toString(), fontSize = 16.sp,color = OnSurfaceVariant)
    }
}

@Composable
fun ImageUpload(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = ImageContainer, shape = RoundedCornerShape(10.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.upload), // your rupee icon drawable name here
            contentDescription = "upload image",
            modifier = Modifier.size(40.dp) ,
            colorFilter = ColorFilter.tint(color = OnSurfaceVariant)// adjust size here
        )
        Text(text = "Upload Image (optional)", fontSize = 16.sp,color = OnSurfaceVariant)
    }
}

