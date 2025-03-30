package com.example.nutripal.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nutripal.domain.model.BMICategory
import com.example.nutripal.ui.common.theme.NormalWeightColor
import com.example.nutripal.ui.common.theme.ObeseColor
import com.example.nutripal.ui.common.theme.OverweightColor
import com.example.nutripal.ui.common.theme.UnderweightColor
import com.example.nutripal.util.Constants

@Composable
fun BMIProgressBar(
    bmi: Double,
    modifier: Modifier = Modifier
) {
    val underweightWeight = 0.25f
    val normalWeight = 0.25f
    val overweightWeight = 0.25f
    val obeseWeight = 0.25f

    // Calculate position of indicator (0.0 to 1.0)
    val position = when {
        bmi < Constants.UNDERWEIGHT_THRESHOLD -> {
            // 0 - 18.5 (map to 0.0 - 0.25)
            val normalizedValue = bmi / Constants.UNDERWEIGHT_THRESHOLD
            normalizedValue * underweightWeight
        }
        bmi < Constants.NORMAL_WEIGHT_THRESHOLD -> {
            // 18.5 - 24.9 (map to 0.25 - 0.5)
            val range = Constants.NORMAL_WEIGHT_THRESHOLD - Constants.UNDERWEIGHT_THRESHOLD
            val normalizedValue = (bmi - Constants.UNDERWEIGHT_THRESHOLD) / range
            underweightWeight + (normalizedValue * normalWeight)
        }
        bmi < Constants.OVERWEIGHT_THRESHOLD -> {
            // 24.9 - 29.9 (map to 0.5 - 0.75)
            val range = Constants.OVERWEIGHT_THRESHOLD - Constants.NORMAL_WEIGHT_THRESHOLD
            val normalizedValue = (bmi - Constants.NORMAL_WEIGHT_THRESHOLD) / range
            underweightWeight + normalWeight + (normalizedValue * overweightWeight)
        }
        else -> {
            // 29.9+ (map to 0.75 - 1.0, capping at 40 BMI for 1.0)
            val maxObese = 40.0
            val range = maxObese - Constants.OVERWEIGHT_THRESHOLD
            val normalizedValue = ((bmi - Constants.OVERWEIGHT_THRESHOLD) / range).coerceAtMost(1.0)
            underweightWeight + normalWeight + overweightWeight + (normalizedValue * obeseWeight)
        }
    }

    Box(modifier = modifier.padding(vertical = 16.dp)) {
        // Progress bar background
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .weight(underweightWeight)
                    .height(12.dp)
                    .background(UnderweightColor)
            )
            Box(
                modifier = Modifier
                    .weight(normalWeight)
                    .height(12.dp)
                    .background(NormalWeightColor)
            )
            Box(
                modifier = Modifier
                    .weight(overweightWeight)
                    .height(12.dp)
                    .background(OverweightColor)
            )
            Box(
                modifier = Modifier
                    .weight(obeseWeight)
                    .height(12.dp)
                    .background(ObeseColor)
            )
        }

        // Indicator
        Box(
            modifier = Modifier
                .fillMaxWidth(position.toFloat())
                .align(Alignment.CenterStart)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(4.dp)
                    .height(20.dp)
                    .background(Color.Black)
            )
        }

        // Categories
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Kurus",
                style = MaterialTheme.typography.bodySmall,
                color = UnderweightColor,
                modifier = Modifier.weight(underweightWeight),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Normal",
                style = MaterialTheme.typography.bodySmall,
                color = NormalWeightColor,
                modifier = Modifier.weight(normalWeight),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Gemuk",
                style = MaterialTheme.typography.bodySmall,
                color = OverweightColor,
                modifier = Modifier.weight(overweightWeight),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Obesitas",
                style = MaterialTheme.typography.bodySmall,
                color = ObeseColor,
                modifier = Modifier.weight(obeseWeight),
                textAlign = TextAlign.Center
            )
        }
    }
}