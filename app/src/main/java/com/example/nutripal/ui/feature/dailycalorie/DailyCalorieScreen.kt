package com.example.nutripal.ui.feature.dailycalorie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutripal.domain.model.WeightGoal
import com.example.nutripal.ui.common.components.GenderSelector
import com.example.nutripal.ui.common.components.NutriPalButton
import com.example.nutripal.ui.common.components.NutriPalCard
import com.example.nutripal.ui.common.components.NutriPalTextField
import kotlin.math.abs

@Composable
fun DailyCalorieScreen(
    viewModel: DailyCalorieViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error message in snackbar if any
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Kalkulator Kalori Harian",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (!uiState.isCalculated) {
                    DailyCalorieInputForm(
                        uiState = uiState,
                        onGenderChanged = viewModel::onGenderChanged,
                        onAgeChanged = viewModel::onAgeChanged,
                        onHeightChanged = viewModel::onHeightChanged,
                        onCurrentWeightChanged = viewModel::onCurrentWeightChanged,
                        onTargetWeightChanged = viewModel::onTargetWeightChanged,
                        onTargetWeeksChanged = viewModel::onTargetWeeksChanged,
                        onActivityLevelChanged = viewModel::onActivityLevelChanged,
                        onCalculateClicked = viewModel::calculateDailyCalories
                    )
                } else {
                    DailyCalorieResultView(
                        uiState = uiState,
                        onResetClicked = viewModel::resetCalculation
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCalorieInputForm(
    uiState: DailyCalorieUiState,
    onGenderChanged: (com.example.nutripal.domain.model.Gender) -> Unit,
    onAgeChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onCurrentWeightChanged: (String) -> Unit,
    onTargetWeightChanged: (String) -> Unit,
    onTargetWeeksChanged: (String) -> Unit,
    onActivityLevelChanged: (ActivityLevel) -> Unit,
    onCalculateClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    NutriPalCard(
        title = "Masukkan Data Anda",
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GenderSelector(
                selectedGender = uiState.gender,
                onGenderSelected = onGenderChanged
            )

            Spacer(modifier = Modifier.height(8.dp))

            NutriPalTextField(
                value = uiState.age,
                onValueChange = onAgeChanged,
                label = "Umur (tahun)",
                keyboardType = KeyboardType.Number,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Numbers,
                        contentDescription = "Umur"
                    )
                }
            )

            NutriPalTextField(
                value = uiState.height,
                onValueChange = onHeightChanged,
                label = "Tinggi Badan (cm)",
                keyboardType = KeyboardType.Decimal,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Height,
                        contentDescription = "Tinggi Badan"
                    )
                }
            )

            NutriPalTextField(
                value = uiState.currentWeight,
                onValueChange = onCurrentWeightChanged,
                label = "Berat Badan Saat Ini (kg)",
                keyboardType = KeyboardType.Decimal,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Scale,
                        contentDescription = "Berat Badan Saat Ini"
                    )
                }
            )

            NutriPalTextField(
                value = uiState.targetWeight,
                onValueChange = onTargetWeightChanged,
                label = "Berat Badan Target (kg)",
                keyboardType = KeyboardType.Decimal,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "Berat Badan Target"
                    )
                }
            )

            NutriPalTextField(
                value = uiState.targetWeeks,
                onValueChange = onTargetWeeksChanged,
                label = "Target Waktu (minggu)",
                keyboardType = KeyboardType.Number,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Target Waktu"
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Level Aktivitas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = uiState.activityLevel.displayName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = "Level Aktivitas"
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ActivityLevel.values().forEach { level ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = level.displayName)
                                    Text(
                                        text = level.description,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            onClick = {
                                onActivityLevelChanged(level)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NutriPalButton(
                text = "Hitung Kalori Harian",
                onClick = onCalculateClicked
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    NutriPalCard(
        title = "Tentang Kalori Harian",
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Kalori harian adalah jumlah energi yang dibutuhkan tubuh Anda untuk mempertahankan fungsi dasar dan mendukung aktivitas sehari-hari. Kebutuhan kalori ini bervariasi tergantung pada jenis kelamin, usia, tinggi, berat, dan level aktivitas Anda.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Kalkulator ini akan memberikan perkiraan kebutuhan kalori harian untuk mencapai target berat badan Anda dalam jangka waktu tertentu. Sangat disarankan untuk berkonsultasi dengan ahli gizi atau profesional kesehatan untuk rencana diet yang lebih personal.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun DailyCalorieResultView(
    uiState: DailyCalorieUiState,
    onResetClicked: () -> Unit
) {
    val result = uiState.result

    if (result != null) {
        val goalColor = when (result.goal) {
            WeightGoal.LOSE -> MaterialTheme.colorScheme.error
            WeightGoal.MAINTAIN -> MaterialTheme.colorScheme.primary
            WeightGoal.GAIN -> MaterialTheme.colorScheme.tertiary
        }

        NutriPalCard(
            title = "Kebutuhan Kalori Harian Anda",
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${result.dailyCalories}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = goalColor
                    )
                )

                Text(
                    text = "Kalori per hari",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = goalColor.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Target: ${result.goal.displayName}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = goalColor
                            )
                        )

                        val weightDiff = abs(result.currentWeight - result.targetWeight)
                        val diffText = when (result.goal) {
                            WeightGoal.LOSE -> "menurunkan"
                            WeightGoal.MAINTAIN -> "mempertahankan"
                            WeightGoal.GAIN -> "menaikkan"
                        }

                        Text(
                            text = "Anda ingin $diffText berat badan sebanyak ${String.format("%.1f", weightDiff)} kg dalam ${result.targetWeeks} minggu",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                NutriPalCard(
                    title = "Detail",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        DetailRow(label = "Jenis Kelamin", value = result.gender.displayName)
                        DetailRow(label = "Umur", value = "${result.age} tahun")
                        DetailRow(label = "Tinggi Badan", value = "${result.height} cm")
                        DetailRow(label = "Berat Badan Saat Ini", value = "${result.currentWeight} kg")
                        DetailRow(label = "Berat Badan Target", value = "${result.targetWeight} kg")
                        DetailRow(label = "Target Waktu", value = "${result.targetWeeks} minggu")
                        DetailRow(label = "BMR (Basal Metabolic Rate)", value = "${result.bmr.toInt()} kalori")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = getRecommendationText(result.goal),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                NutriPalButton(
                    text = "Hitung Ulang",
                    onClick = onResetClicked
                )
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

fun getRecommendationText(goal: WeightGoal): String {
    return when (goal) {
        WeightGoal.LOSE ->
            "Untuk menurunkan berat badan dengan cara yang sehat, fokuslah pada makanan bergizi, kontrol porsi, dan rutinitas olahraga. Hindari diet ketat dan pastikan defisit kalori tidak lebih dari 500-1000 kalori per hari."

        WeightGoal.MAINTAIN ->
            "Untuk mempertahankan berat badan, konsumsilah jumlah kalori yang seimbang dengan aktivitas fisik Anda. Tetap jaga pola makan sehat dan rutin berolahraga untuk menjaga metabolisme tetap optimal."

        WeightGoal.GAIN ->
            "Untuk menaikkan berat badan dengan sehat, fokus pada peningkatan massa otot bukan lemak. Konsumsi makanan bergizi padat kalori dan lakukan latihan kekuatan. Surplus kalori yang disarankan adalah 300-500 kalori per hari."
    }
}