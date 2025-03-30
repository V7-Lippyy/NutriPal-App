package com.example.nutripal.ui.feature.foodlog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nutripal.ui.common.components.NutriPalCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun EnhancedSummaryCard(
    selectedDate: Date,
    dailyCalories: Double,
    monthlyCalories: Double,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
    val formattedDate = dateFormatter.format(selectedDate)

    // Format tanggal yang dipilih untuk label
    val dayFormatter = SimpleDateFormat("d MMMM", Locale("id", "ID"))
    val formattedDay = dayFormatter.format(selectedDate)

    // Mendapatkan nama bulan dari tanggal yang dipilih
    val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
    val formattedMonth = monthFormatter.format(selectedDate)

    // Cek apakah tanggal yang dipilih adalah hari ini
    val isToday = isSameDay(selectedDate, Date())
    val summaryTitle = "Ringkasan Tanggal"

    NutriPalCard(
        title = summaryTitle,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Tanggal yang dipilih
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Tanggal",
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Total Kalori untuk tanggal yang dipilih
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Tanggal Dipilih",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = "Total Kalori Tanggal $formattedDay",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "${dailyCalories.toInt()} kkal",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Total Kalori Bulan Ini
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Bulan",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = "Total Kalori $formattedMonth",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "${monthlyCalories.toInt()} kkal",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
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