package com.example.nutripal.ui.feature.bmi

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Scale
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutripal.ui.common.components.BMIProgressBar
import com.example.nutripal.ui.common.components.NutriPalButton
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Note: Only applying minimal paddingValues for the status bar
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Apply only the safe drawing area at the top (status bar)
                .padding(top = paddingValues.calculateTopPadding(), bottom = paddingValues.calculateBottomPadding())
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header dengan minimal padding dari atas
            Text(
                text = "Kalkulator BMI",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(vertical = 0.dp)
            )

            // Main content
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

            // Menambahkan spacer transparan untuk memastikan konten dapat di-scroll penuh
            // Ini memastikan area di bawah navbar dapat diakses dengan scroll
            Spacer(modifier = Modifier.height(56.dp))
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Jenis Kelamin",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        GenderSelector(
            selectedGender = uiState.gender,
            onGenderSelected = onGenderChanged,
            modifier = Modifier.fillMaxWidth()
        )

        NutriPalTextField(
            value = uiState.age,
            onValueChange = onAgeChanged,
            label = "Umur (tahun)",
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,
            modifier = Modifier.fillMaxWidth(),
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
            imeAction = ImeAction.Next,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
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
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Scale,
                    contentDescription = "Berat Badan"
                )
            }
        )

        NutriPalButton(
            text = "Hitung BMI",
            onClick = onCalculateClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(10.dp)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Informasi BMI dengan ukuran minimal dan tanpa padding bawah
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 10.dp) // Minimum height sangat kecil
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = "Apa itu BMI?",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Body Mass Index (BMI) atau Indeks Massa Tubuh (IMT) adalah pengukuran yang digunakan untuk menilai apakah berat badan Anda sehat berdasarkan tinggi badan Anda. BMI dihitung dengan membagi berat badan (kg) dengan kuadrat tinggi badan (m²).",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Kategori BMI:\n• Kurus: < 18.5\n• Normal: 18.5 - 24.9\n• Gemuk: 25 - 29.9\n• Obesitas: ≥ 30",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GenderSelector(
    selectedGender: com.example.nutripal.domain.model.Gender,
    onGenderSelected: (com.example.nutripal.domain.model.Gender) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GenderOption(
            label = "Laki-laki",
            icon = "♂",
            isSelected = selectedGender == com.example.nutripal.domain.model.Gender.MALE,
            onClick = { onGenderSelected(com.example.nutripal.domain.model.Gender.MALE) },
            modifier = Modifier.weight(1f)
        )
        GenderOption(
            label = "Perempuan",
            icon = "♀",
            isSelected = selectedGender == com.example.nutripal.domain.model.Gender.FEMALE,
            onClick = { onGenderSelected(com.example.nutripal.domain.model.Gender.FEMALE) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun GenderOption(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
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
        val decimalFormat = DecimalFormat("#.#")
        val formattedBMI = decimalFormat.format(result.bmiValue)

        val categoryColor = when (result.category) {
            com.example.nutripal.domain.model.BMICategory.UNDERWEIGHT -> UnderweightColor
            com.example.nutripal.domain.model.BMICategory.NORMAL -> NormalWeightColor
            com.example.nutripal.domain.model.BMICategory.OVERWEIGHT -> OverweightColor
            com.example.nutripal.domain.model.BMICategory.OBESE -> ObeseColor
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Hasil BMI Anda",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = formattedBMI,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = categoryColor
                )
            )

            Text(
                text = result.category.displayName,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = categoryColor
                )
            )

            BMIProgressBar(
                bmi = result.bmiValue,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = "Detail",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                DetailRow(label = "Jenis Kelamin", value = result.gender.displayName)
                DetailRow(label = "Umur", value = "${result.age} tahun")
                DetailRow(label = "Tinggi Badan", value = "${result.height} cm")
                DetailRow(label = "Berat Badan", value = "${result.weight} kg")
            }

            Text(
                text = getRecommendationText(result.category),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            NutriPalButton(
                text = "Hitung Ulang",
                onClick = onResetClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp)
            )
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
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
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