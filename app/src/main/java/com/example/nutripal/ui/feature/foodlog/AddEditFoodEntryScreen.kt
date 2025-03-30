package com.example.nutripal.ui.feature.foodlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutripal.domain.model.MealType
import com.example.nutripal.ui.common.components.NutriPalButton
import com.example.nutripal.ui.common.components.NutriPalTextField
import com.example.nutripal.ui.feature.foodlog.components.MealTypeSelector
import com.example.nutripal.ui.feature.foodlog.components.NutritionSearchResultItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFoodEntryScreen(
    viewModel: FoodLogViewModel = hiltViewModel(),
    foodEntryId: Long? = null,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    // Form state
    var foodName by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("100") }
    var servingUnit by remember { mutableStateOf("g") }
    var calories by remember { mutableStateOf("0") }
    var protein by remember { mutableStateOf("0") }
    var carbs by remember { mutableStateOf("0") }
    var fat by remember { mutableStateOf("0") }
    var fiber by remember { mutableStateOf("0") }
    var sugar by remember { mutableStateOf("0") }
    var selectedMealType by remember { mutableStateOf(MealType.BREAKFAST) }
    var notes by remember { mutableStateOf("") }

    // Tanggal untuk entri makanan - default ke tanggal yang dipilih di ViewModel
    var selectedDate by remember { mutableStateOf(uiState.selectedDate) }

    // Date picker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.time
    )

    // Format tanggal
    val dateFormatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))

    // Search state
    var searchQuery by remember { mutableStateOf("") }

    // If editing existing entry, load its data
    LaunchedEffect(foodEntryId) {
        if (foodEntryId != null && foodEntryId > 0) {
            val entry = viewModel.getFoodEntryById(foodEntryId)
            entry?.let {
                foodName = it.name
                servingSize = it.servingSize.toString()
                servingUnit = it.servingUnit
                calories = it.calories.toString()
                protein = it.protein.toString()
                carbs = it.carbs.toString()
                fat = it.fat.toString()
                fiber = it.fiber.toString()
                sugar = it.sugar.toString()
                selectedMealType = MealType.fromString(it.mealType)
                notes = it.notes ?: ""
                selectedDate = it.date
            }
        }
    }

    // Handle errors
    LaunchedEffect(uiState.error, uiState.searchError) {
        val error = uiState.error ?: uiState.searchError
        error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (foodEntryId != null) "Edit Makanan" else "Tambah Makanan",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Date Selector Card
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pilih Tanggal",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.padding(8.dp))

                        Column {
                            Text(
                                text = "Tanggal",
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                text = dateFormatter.format(selectedDate),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    // Standardisasi tanggal ke jam 12 siang
                                    val calendar = Calendar.getInstance()
                                    calendar.timeInMillis = millis
                                    calendar.set(Calendar.HOUR_OF_DAY, 12)
                                    calendar.set(Calendar.MINUTE, 0)
                                    calendar.set(Calendar.SECOND, 0)
                                    calendar.set(Calendar.MILLISECOND, 0)

                                    selectedDate = calendar.time
                                }
                                showDatePicker = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Batal")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Food search
                Text(
                    text = "Cari Makanan",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari makanan...") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                    )

                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.searchFoodNutrition(searchQuery)
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Cari")
                    }
                }

                // Search results
                if (uiState.isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.searchResults.isNotEmpty()) {
                    Text(
                        text = "Hasil Pencarian",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        uiState.searchResults.forEach { nutritionItem ->
                            NutritionSearchResultItem(
                                nutritionItem = nutritionItem,
                                onClick = {
                                    // Populate form with search result
                                    foodName = nutritionItem.name
                                    servingSize = nutritionItem.servingSizeGram.toString()
                                    servingUnit = "g"
                                    calories = nutritionItem.calories.toString()
                                    protein = nutritionItem.proteinGram.toString()
                                    carbs = nutritionItem.totalCarbohydratesGram.toString()
                                    fat = nutritionItem.totalFatGram.toString()
                                    fiber = nutritionItem.fiberGram.toString()
                                    sugar = nutritionItem.sugarGram.toString()

                                    // Clear search
                                    searchQuery = ""
                                    viewModel.clearSearch()
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Manual entry form
                Text(
                    text = "Detail Makanan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                NutriPalTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = "Nama Makanan",
                    isError = foodName.isBlank(),
                    errorMessage = "Nama makanan harus diisi"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NutriPalTextField(
                        value = servingSize,
                        onValueChange = { servingSize = it },
                        label = "Porsi",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(0.6f)
                    )

                    NutriPalTextField(
                        value = servingUnit,
                        onValueChange = { servingUnit = it },
                        label = "Unit",
                        modifier = Modifier.weight(0.4f)
                    )
                }

                NutriPalTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = "Kalori",
                    keyboardType = KeyboardType.Decimal
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NutriPalTextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = "Protein (g)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )

                    NutriPalTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = "Karbohidrat (g)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )

                    NutriPalTextField(
                        value = fat,
                        onValueChange = { fat = it },
                        label = "Lemak (g)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NutriPalTextField(
                        value = fiber,
                        onValueChange = { fiber = it },
                        label = "Serat (g)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )

                    NutriPalTextField(
                        value = sugar,
                        onValueChange = { sugar = it },
                        label = "Gula (g)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Waktu Makan",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                MealTypeSelector(
                    selectedMealType = selectedMealType,
                    onMealTypeSelected = { selectedMealType = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                NutriPalTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Catatan (opsional)"
                )

                Spacer(modifier = Modifier.height(24.dp))

                NutriPalButton(
                    text = if (foodEntryId != null) "Perbarui" else "Simpan",
                    onClick = {
                        if (foodName.isBlank()) {
                            // Show error
                            return@NutriPalButton
                        }

                        if (foodEntryId != null) {
                            // Update existing entry
                            viewModel.updateFoodEntry(
                                com.example.nutripal.data.local.entity.FoodEntry(
                                    id = foodEntryId,
                                    name = foodName,
                                    servingSize = servingSize.toDoubleOrNull() ?: 0.0,
                                    servingUnit = servingUnit,
                                    calories = calories.toDoubleOrNull() ?: 0.0,
                                    protein = protein.toDoubleOrNull() ?: 0.0,
                                    carbs = carbs.toDoubleOrNull() ?: 0.0,
                                    fat = fat.toDoubleOrNull() ?: 0.0,
                                    fiber = fiber.toDoubleOrNull() ?: 0.0,
                                    sugar = sugar.toDoubleOrNull() ?: 0.0,
                                    mealType = selectedMealType.name,
                                    date = selectedDate, // Gunakan tanggal yang dipilih melalui DatePicker
                                    time = Date(), // Current time
                                    notes = notes.ifBlank { null },
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                        } else {
                            // Add new entry
                            viewModel.addFoodEntry(
                                name = foodName,
                                servingSize = servingSize.toDoubleOrNull() ?: 0.0,
                                servingUnit = servingUnit,
                                calories = calories.toDoubleOrNull() ?: 0.0,
                                protein = protein.toDoubleOrNull() ?: 0.0,
                                carbs = carbs.toDoubleOrNull() ?: 0.0,
                                fat = fat.toDoubleOrNull() ?: 0.0,
                                fiber = fiber.toDoubleOrNull() ?: 0.0,
                                sugar = sugar.toDoubleOrNull() ?: 0.0,
                                mealType = selectedMealType,
                                notes = notes.ifBlank { null },
                                entryDate = selectedDate // Gunakan tanggal yang dipilih melalui DatePicker
                            )
                        }

                        onNavigateBack()
                    }
                )
            }
        }
    }
}