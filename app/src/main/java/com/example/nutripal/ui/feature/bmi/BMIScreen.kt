package com.example.nutripal.ui.feature.bmi

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
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Scale
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutripal.ui.common.components.BMIProgressBar
import com.example.nutripal.ui.common.components.GenderSelector
import com.example.nutripal.ui.common.components.NutriPalButton
import com.example.nutripal.ui.common.components.NutriPalCard
import com.example.nutripal.ui.common.components.NutriPalTextField
import com.example.nutripal.ui.common.theme.NormalWeightColor
import com.example.nutripal.ui.common.theme.ObeseColor
import com.example.nutripal.ui.common.theme.OverweightColor
import com.example.nutripal.ui.common.theme.UnderweightColor
import java.text.DecimalFormat

@Composable
fun BMIScreen(
    viewModel: BMIViewModel = hiltViewModel()
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
                    text = "Kalkulator BMI",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (!uiState.isCalculated) {
                    BMIInputForm(
                        uiState = uiState,
                        onGenderChanged = viewModel::onGenderChanged,
                        onAgeChanged = viewModel::onAgeChanged,
                        onHeightChanged = viewModel::onHeightChanged,
                        onWeightChanged = viewModel::onWeightChanged,
                        onCalculateClicked = viewModel::calculateBMI
                    )
                } else {
                    BMIResultView(
                        uiState = uiState,
                        onResetClicked = viewModel::resetCalculation
                    )
                }
            }
        }
    }
}

@Composable
fun BMIInputForm(
    uiState: BMIUiState,
    onGenderChanged: (gender: com.example.nutripal.domain.model.Gender) -> Unit,
    onAgeChanged: (age: String) -> Unit,
    onHeightChanged: (height: String) -> Unit,
    onWeightChanged: (weight: String) -> Unit,
    onCalculateClicked: () -> Unit
) {
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
                    androidx.compose.material3.Icon(
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
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Height,
                        contentDescription = "Tinggi Badan"
                    )
                }
            )

            NutriPalTextField(
                value = uiState.weight,
                onValueChange = onWeightChanged,
                label = "Berat Badan (kg)",
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Scale,
                        contentDescription = "Berat Badan"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NutriPalButton(
                text = "Hitung BMI",
                onClick = onCalculateClicked
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    NutriPalCard(
        title = "Apa itu BMI?",
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Body Mass Index (BMI) atau Indeks Massa Tubuh (IMT) adalah pengukuran yang digunakan untuk menilai apakah berat badan Anda sehat berdasarkan tinggi badan Anda. BMI dihitung dengan membagi berat badan (kg) dengan kuadrat tinggi badan (m²).",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Kategori BMI:\n• Kurus: < 18.5\n• Normal: 18.5 - 24.9\n• Gemuk: 25 - 29.9\n• Obesitas: ≥ 30",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun BMIResultView(
    uiState: BMIUiState,
    onResetClicked: () -> Unit
) {
    val result = uiState.result

    if (result != null) {
        val decimalFormat = DecimalFormat("#.##")
        val formattedBMI = decimalFormat.format(result.bmiValue)

        val categoryColor = when (result.category) {
            com.example.nutripal.domain.model.BMICategory.UNDERWEIGHT -> UnderweightColor
            com.example.nutripal.domain.model.BMICategory.NORMAL -> NormalWeightColor
            com.example.nutripal.domain.model.BMICategory.OVERWEIGHT -> OverweightColor
            com.example.nutripal.domain.model.BMICategory.OBESE -> ObeseColor
        }

        NutriPalCard(
            title = "Hasil BMI Anda",
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formattedBMI,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                )

                Text(
                    text = result.category.displayName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = categoryColor
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                BMIProgressBar(
                    bmi = result.bmiValue,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                NutriPalCard(
                    title = "Detail",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        DetailRow(label = "Jenis Kelamin", value = result.gender.displayName)
                        DetailRow(label = "Umur", value = "${result.age} tahun")
                        DetailRow(label = "Tinggi Badan", value = "${result.height} cm")
                        DetailRow(label = "Berat Badan", value = "${result.weight} kg")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = getRecommendationText(result.category),
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
    androidx.compose.foundation.layout.Row(
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

fun getRecommendationText(category: com.example.nutripal.domain.model.BMICategory): String {
    return when (category) {
        com.example.nutripal.domain.model.BMICategory.UNDERWEIGHT ->
            "Anda memiliki berat badan yang kurang. Pertimbangkan untuk meningkatkan asupan kalori secara sehat dan berkonsultasi dengan dokter atau ahli gizi."

        com.example.nutripal.domain.model.BMICategory.NORMAL ->
            "Anda memiliki berat badan yang sehat. Pertahankan pola makan seimbang dan tetap aktif secara fisik."

        com.example.nutripal.domain.model.BMICategory.OVERWEIGHT ->
            "Anda memiliki kelebihan berat badan. Pertimbangkan untuk membuat perubahan kecil pada pola makan dan tingkatkan aktivitas fisik Anda."

        com.example.nutripal.domain.model.BMICategory.OBESE ->
            "Anda mengalami obesitas. Pertimbangkan untuk berkonsultasi dengan dokter atau ahli gizi untuk mendiskusikan rencana penurunan berat badan yang aman dan berkelanjutan."
    }
}