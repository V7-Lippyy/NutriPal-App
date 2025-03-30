package com.example.nutripal.ui.feature.foodlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutripal.data.local.entity.FoodEntry
import com.example.nutripal.domain.model.MealType
import com.example.nutripal.domain.model.NutritionItem
import com.example.nutripal.domain.repository.IFoodEntryRepository
import com.example.nutripal.data.remote.api.NutritionApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FoodLogViewModel @Inject constructor(
    private val repository: IFoodEntryRepository,
    private val nutritionApiService: NutritionApiService,
    private val apiKey: String
) : ViewModel() {

    // Date formatter for debugging
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Simpan tanggal yang dipilih dalam variabel terpisah untuk menjaga konsistensi
    private val _currentSelectedDate = MutableStateFlow(standardizeDate(Date()))

    private val _uiState = MutableStateFlow(FoodLogUiState(selectedDate = _currentSelectedDate.value))
    val uiState: StateFlow<FoodLogUiState> = _uiState.asStateFlow()

    // Food entries untuk tanggal yang dipilih
    val foodEntriesForSelectedDate = _currentSelectedDate
        .combine(repository.getAllFoodEntries()) { date, allEntries ->
            println("Filtering entries for date: ${dateFormatter.format(date)}")
            val filtered = allEntries.filter { isSameDay(it.date, date) }
            println("Found ${filtered.size} entries for date: ${dateFormatter.format(date)}")
            filtered
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            // Debug: print initial selected date
            println("Initial selected date: ${dateFormatter.format(_currentSelectedDate.value)}")

            // Observer untuk tanggal yang dipilih
            _currentSelectedDate.collect { date ->
                println("Selected date changed to: ${dateFormatter.format(date)}")
                refreshDataForDate(date)
            }
        }
    }

    // Fungsi untuk menyegarkan data berdasarkan tanggal yang dipilih
    private suspend fun refreshDataForDate(date: Date) {
        try {
            // Ambil semua entri dari repository
            val allEntries = repository.getAllFoodEntries().first()

            // Filter entri untuk tanggal yang dipilih
            val entriesForSelectedDate = allEntries.filter { entry ->
                isSameDay(entry.date, date)
            }

            // Hitung total kalori untuk tanggal tersebut
            val totalCalories = entriesForSelectedDate.sumOf { it.calories }
            println("Total calories for ${dateFormatter.format(date)}: $totalCalories")

            // Hitung total kalori bulanan
            val monthlyCalories = calculateMonthlyCalories(date, allEntries)
            println("Total monthly calories for ${getMonthYearString(date)}: $monthlyCalories")

            // Update UI state
            _uiState.update { currentState ->
                currentState.copy(
                    foodEntries = entriesForSelectedDate,
                    totalCalories = totalCalories,
                    monthlyCalories = monthlyCalories,
                    selectedDate = date,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            println("Error refreshing data: ${e.message}")
            _uiState.update { it.copy(error = "Error refreshing data: ${e.message}") }
        }
    }

    // Menghitung total kalori bulanan dari entri yang ada
    private fun calculateMonthlyCalories(date: Date, entries: List<FoodEntry>): Double {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val entriesForMonth = entries.filter { entry ->
            calendar.time = entry.date
            calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month
        }

        return entriesForMonth.sumOf { it.calories }
    }

    // Mendapatkan string untuk bulan dan tahun (untuk tampilan UI)
    private fun getMonthYearString(date: Date): String {
        val formatter = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        return formatter.format(date)
    }

    // Fungsi untuk membuat standarisasi tanggal (tengah hari)
    private fun standardizeDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    // Fungsi untuk membuat clone dari Date untuk menghindari masalah referensi
    private fun cloneDate(date: Date): Date {
        return Date(date.time)
    }

    fun selectDate(date: Date) {
        val standardizedDate = standardizeDate(date)
        println("Selecting date: ${dateFormatter.format(date)}, standardized to: ${dateFormatter.format(standardizedDate)}")

        // Update tanggal yang dipilih (ini akan memicu refreshDataForDate melalui collector)
        _currentSelectedDate.value = standardizedDate
    }

    // Helper function to check if two dates are on the same day
    private fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) return false

        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        val result = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)

        // Debug log
        if (result) {
            println("Dates match: ${dateFormatter.format(date1)} == ${dateFormatter.format(date2)}")
        }

        return result
    }

    fun deleteFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            try {
                repository.deleteFoodEntry(foodEntry)
                // Refresh data dengan tanggal yang sama
                refreshDataForDate(_currentSelectedDate.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error: ${e.localizedMessage}") }
            }
        }
    }

    suspend fun getFoodEntryById(id: Long): FoodEntry? {
        return repository.getFoodEntryById(id)
    }

    fun searchFoodNutrition(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(searchError = "Please enter a food name to search") }
            return
        }

        _uiState.update { it.copy(isSearching = true, searchError = null) }

        viewModelScope.launch {
            try {
                val response = nutritionApiService.getNutritionInfo(apiKey, query)
                _uiState.update {
                    it.copy(
                        searchResults = response.items,
                        isSearching = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        searchError = "Error: ${e.localizedMessage}",
                        isSearching = false
                    )
                }
            }
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchResults = emptyList(), searchError = null) }
    }

    fun updateFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            try {
                // Standardisasi tanggal ke jam 12 siang
                val updatedEntry = foodEntry.copy(
                    date = standardizeDate(foodEntry.date)
                )

                repository.updateFoodEntry(updatedEntry)
                println("Updated entry with date: ${dateFormatter.format(updatedEntry.date)}")

                // Refresh data
                refreshDataForDate(_currentSelectedDate.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun addFoodEntry(
        name: String,
        servingSize: Double,
        servingUnit: String,
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double,
        fiber: Double,
        sugar: Double,
        mealType: MealType,
        notes: String? = null,
        entryDate: Date? = null
    ) {
        viewModelScope.launch {
            try {
                // Standarkan tanggal
                val standardizedDate = standardizeDate(entryDate ?: _currentSelectedDate.value)

                // Debug log
                println("Adding food entry with date: ${dateFormatter.format(standardizedDate)}")

                // Create entry with standardized date
                val entry = FoodEntry(
                    name = name,
                    servingSize = servingSize,
                    servingUnit = servingUnit,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    fiber = fiber,
                    sugar = sugar,
                    mealType = mealType.name,
                    date = standardizedDate,
                    time = Date(), // Current time for display purposes
                    notes = notes
                )

                // Save to repository
                val id = repository.addFoodEntry(entry)
                println("Added entry with ID: $id for date: ${dateFormatter.format(standardizedDate)}")

                // Refresh data
                refreshDataForDate(_currentSelectedDate.value)
            } catch (e: Exception) {
                println("Error adding food entry: ${e.message}")
                _uiState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }
}

data class FoodLogUiState(
    val foodEntries: List<FoodEntry> = emptyList(),
    val searchResults: List<NutritionItem> = emptyList(),
    val selectedDate: Date = Date(),
    val totalCalories: Double = 0.0,
    val monthlyCalories: Double = 0.0,
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val searchError: String? = null
)