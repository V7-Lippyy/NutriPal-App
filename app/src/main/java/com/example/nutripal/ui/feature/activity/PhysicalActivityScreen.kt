package com.example.nutripal.ui.feature.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.nutripal.domain.model.PhysicalActivity
import com.example.nutripal.ui.common.ActivityIcons
import com.example.nutripal.ui.common.components.NutriPalButton
import com.example.nutripal.ui.common.components.NutriPalCard
import com.example.nutripal.ui.common.components.NutriPalTextField
import java.text.DecimalFormat

@Composable
fun PhysicalActivityScreen(
    viewModel: PhysicalActivityViewModel = hiltViewModel()
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
                    text = "Aktivitas Fisik & Pembakaran Kalori",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (!uiState.isCalculated) {
                    ActivityInputForm(
                        uiState = uiState,
                        onActivitySelected = viewModel::onActivitySelected,
                        onDurationChanged = viewModel::onDurationChanged,
                        onWeightChanged = viewModel::onWeightChanged,
                        onCalculateClicked = viewModel::calculateCaloriesBurned
                    )
                } else {
                    ActivityResultView(
                        uiState = uiState,
                        onResetClicked = viewModel::resetCalculation
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityInputForm(
    uiState: PhysicalActivityUiState,
    onActivitySelected: (PhysicalActivity) -> Unit,
    onDurationChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
    onCalculateClicked: () -> Unit
) {
    NutriPalCard(
        title = "Pilih Jenis Aktivitas",
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(220.dp)
        ) {
            items(uiState.activities) { activity ->
                ActivityItem(
                    activity = activity,
                    isSelected = activity.id == uiState.selectedActivity?.id,
                    onClick = { onActivitySelected(activity) }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    NutriPalCard(
        title = "Detail Aktivitas",
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NutriPalTextField(
                value = uiState.duration,
                onValueChange = onDurationChanged,
                label = "Durasi (menit)",
                keyboardType = KeyboardType.Number,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Durasi"
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
                    Icon(
                        imageVector = Icons.Default.Scale,
                        contentDescription = "Berat Badan"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NutriPalButton(
                text = "Hitung Kalori Terbakar",
                onClick = onCalculateClicked
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    NutriPalCard(
        title = "Informasi",
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Aktivitas fisik membantu membakar kalori dan meningkatkan kesehatan secara keseluruhan. Jumlah kalori yang terbakar bergantung pada jenis aktivitas, durasi, dan berat badan Anda.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Untuk hasil terbaik, lakukan aktivitas fisik secara teratur dan kombinasikan dengan pola makan seimbang.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ActivityItem(
    activity: PhysicalActivity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            activity.icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = activity.name,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = activity.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ActivityResultView(
    uiState: PhysicalActivityUiState,
    onResetClicked: () -> Unit
) {
    val result = uiState.result

    if (result != null) {
        val activity = result.activity
        val decimalFormat = DecimalFormat("#.##")
        val formattedCalories = decimalFormat.format(result.caloriesBurned)

        NutriPalCard(
            title = "Hasil Pembakaran Kalori",
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        activity.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = activity.name,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = activity.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "${result.durationMinutes} menit",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "$formattedCalories",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )

                        Text(
                            text = "Kalori terbakar",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = getBenefitsText(activity.name),
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

fun getBenefitsText(activityName: String): String {
    return when (activityName) {
        "Berlari" -> "Berlari meningkatkan kesehatan jantung, membakar kalori dengan efektif, dan melepaskan endorfin yang meningkatkan suasana hati."
        "Berjalan" -> "Berjalan adalah latihan rendah dampak yang bagus untuk semua tingkat kebugaran, membantu kesehatan jantung dan memperbaiki suasana hati."
        "Bersepeda" -> "Bersepeda memperkuat otot kaki, meningkatkan keseimbangan, dan merupakan latihan kardio yang bagus dengan dampak rendah pada sendi."
        "Berenang" -> "Berenang adalah latihan seluruh tubuh yang melatih hampir semua kelompok otot sekaligus, dengan dampak minimal pada sendi."
        "Yoga" -> "Yoga meningkatkan fleksibilitas, kekuatan, dan keseimbangan, serta membantu mengurangi stres dan meningkatkan kesadaran diri."
        "Angkat Beban" -> "Angkat beban membangun massa otot, meningkatkan metabolisme, dan memperkuat tulang, mendukung kesehatan jangka panjang."
        "HIIT" -> "HIIT (High Intensity Interval Training) sangat efektif untuk membakar kalori dalam waktu singkat dan meningkatkan kebugaran kardiovaskular."
        "Menari" -> "Menari adalah cara menyenangkan untuk berolahraga, meningkatkan koordinasi, fleksibilitas, dan kesehatan kardiovaskular."
        "Basket" -> "Basket melatih kelincahan, koordinasi, dan kebugaran kardiovaskular sambil membangun kekuatan otot dan stamina."
        "Sepak Bola" -> "Sepak bola meningkatkan kekuatan kardiovaskular, koordinasi, dan kerja tim, membakar banyak kalori dalam proses."
        else -> "Aktivitas fisik teratur meningkatkan kesehatan jantung, menjaga berat badan sehat, meningkatkan suasana hati, dan mengurangi risiko berbagai penyakit."
    }
}