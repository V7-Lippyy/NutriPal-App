package com.example.nutripal.ui.feature.foodlog.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DateSelector(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember {
        val cal = Calendar.getInstance()
        cal.time = selectedDate
        cal.set(Calendar.DAY_OF_MONTH, 1)
        mutableStateOf(cal.time)
    }

    // Pastikan kalender selalu sesuai dengan tanggal yang dipilih
    LaunchedEffect(selectedDate) {
        val cal = Calendar.getInstance()
        cal.time = selectedDate
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)

        val currentCal = Calendar.getInstance()
        currentCal.time = currentMonth
        val currentYear = currentCal.get(Calendar.YEAR)
        val currentMonthValue = currentCal.get(Calendar.MONTH)

        // Update currentMonth jika bulan di selectedDate berbeda
        if (year != currentYear || month != currentMonthValue) {
            val newCal = Calendar.getInstance()
            newCal.time = selectedDate
            newCal.set(Calendar.DAY_OF_MONTH, 1)
            currentMonth = newCal.time
        }
    }

    val calendar = Calendar.getInstance()
    calendar.time = currentMonth
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val daysInMonthList = remember(currentMonth) {
        (1..daysInMonth).map { day ->
            val cal = Calendar.getInstance()
            cal.time = currentMonth
            cal.set(Calendar.DAY_OF_MONTH, day)
            standardizeDate(cal.time) // Standardize to noon
        }
    }

    val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Month selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val cal = Calendar.getInstance()
                    cal.time = currentMonth
                    cal.add(Calendar.MONTH, -1)
                    currentMonth = cal.time
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowLeft,
                        contentDescription = "Bulan Sebelumnya"
                    )
                }

                Text(
                    text = monthFormatter.format(currentMonth),
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = {
                    val cal = Calendar.getInstance()
                    cal.time = currentMonth
                    cal.add(Calendar.MONTH, 1)
                    currentMonth = cal.time
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowRight,
                        contentDescription = "Bulan Berikutnya"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day of week headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val daysOfWeek = arrayOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
                for (dayName in daysOfWeek) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            val firstCalendar = Calendar.getInstance().apply {
                time = currentMonth
            }
            val firstDayOfMonth = firstCalendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 is Sunday

            // Calculate weeks
            val totalDays = firstDayOfMonth + daysInMonth
            val totalWeeks = (totalDays + 6) / 7

            for (week in 0 until totalWeeks) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (dayOfWeek in 0 until 7) {
                        val dayIndex = week * 7 + dayOfWeek - firstDayOfMonth

                        if (dayIndex in 0 until daysInMonth) {
                            val date = daysInMonthList[dayIndex]

                            val isSelected = isSameDay(date, selectedDate)
                            val isToday = isSameDay(date, Date())

                            DayCell(
                                date = date,
                                isSelected = isSelected,
                                isToday = isToday,
                                onClick = {
                                    // Debugging
                                    println("Day cell clicked: $date")

                                    // Panggil callback onDateSelected dengan tanggal yang dipilih
                                    onDateSelected(date)
                                }
                            )
                        } else {
                            // Empty cell
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun DayCell(
    date: Date,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    calendar.time = date
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    // Tambahkan animasi scale saat tanggal dipilih
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .padding(4.dp)
            .scale(scale) // Tambahkan efek scale untuk animasi
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = if (isToday && !isSelected) 1.dp else 0.dp,
                color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
            .clickable { onClick() }
    ) {
        Text(
            text = dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
            ),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center
        )
    }
}

// Helper function to standardize date to noon
private fun standardizeDate(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 12)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

// Helper function to check if two dates are on the same day
private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = date1
    cal2.time = date2
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}