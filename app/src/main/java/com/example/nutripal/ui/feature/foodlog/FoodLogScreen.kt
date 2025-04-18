package com.example.nutripal.ui.feature.foodlog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutripal.domain.model.MealType
import com.example.nutripal.ui.feature.foodlog.components.DateSelector
import com.example.nutripal.ui.feature.foodlog.components.EnhancedSummaryCard
import com.example.nutripal.ui.feature.foodlog.components.FoodEntryItem

@Composable
fun FoodLogScreen(
    viewModel: FoodLogViewModel = hiltViewModel(),
    onNavigateToAddEntry: () -> Unit,
    onNavigateToEditEntry: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val foodEntries by viewModel.foodEntriesForSelectedDate.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Show error message in snackbar if any
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddEntry,
                icon = { Icon(Icons.Default.Add, contentDescription = "Tambah") },
                text = { Text("Tambah Makanan") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        // Content wrapper
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
                .animateContentSize(),
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            Text(
                text = "Catatan Makanan Harian",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            // Date Selector
            DateSelector(
                selectedDate = uiState.selectedDate,
                onDateSelected = { viewModel.selectDate(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Enhanced Summary Card with target calories
            EnhancedSummaryCard(
                selectedDate = uiState.selectedDate,
                dailyCalories = uiState.totalCalories,
                monthlyCalories = uiState.monthlyCalories,
                targetDailyCalories = uiState.targetDailyCalories, // Pass the target calories
                onTargetCaloriesChanged = { viewModel.updateTargetCalories(it) } // Callback to update target
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Main content area
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (foodEntries.isEmpty()) {
                EmptyFoodLogState()
            } else {
                // Group food entries by meal type
                val entriesByMealType = foodEntries.groupBy { MealType.fromString(it.mealType) }

                // Display entries grouped by meal type
                MealType.values().forEach { mealType ->
                    val entries = entriesByMealType[mealType] ?: emptyList()
                    if (entries.isNotEmpty()) {
                        Text(
                            text = mealType.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )

                        entries.forEach { entry ->
                            FoodEntryItem(
                                foodEntry = entry,
                                onEditClick = { onNavigateToEditEntry(entry.id) },
                                onDeleteClick = { viewModel.deleteFoodEntry(entry) }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }

            // Spacer untuk memastikan konten bisa di-scroll melewati FAB dan navbar
            // 56dp untuk navbar + extra space untuk FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun EmptyFoodLogState(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Belum ada catatan makanan",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Tekan tombol + untuk menambahkan makanan yang dikonsumsi",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}