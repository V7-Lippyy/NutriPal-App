package com.example.nutripal.ui.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nutripal.domain.model.BMICategory
import com.example.nutripal.ui.common.theme.NormalWeightColor
import com.example.nutripal.ui.common.theme.ObeseColor
import com.example.nutripal.ui.common.theme.OverweightColor
import com.example.nutripal.ui.common.theme.UnderweightColor
import com.example.nutripal.util.Constants
import kotlin.math.roundToInt

@Composable
fun BMIProgressBar(
    bmi: Double,
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true
) {
    val underweightWeight = 0.25f
    val normalWeight = 0.25f
    val overweightWeight = 0.25f
    val obeseWeight = 0.25f

    // Calculate the position of the indicator (0.0 to 1.0)
    val targetPosition = when {
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
    }.toFloat()

    // Animated position
    val position = remember { Animatable(0f) }

    // Animate the position from 0 to the target value if animation is enabled
    LaunchedEffect(bmi) {
        if (showAnimation) {
            position.animateTo(
                targetValue = targetPosition,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearEasing
                )
            )
        } else {
            position.snapTo(targetPosition)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 4.dp)
    ) {
        // Modern gradient progress bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .shadow(2.dp, RoundedCornerShape(8.dp))
        ) {
            // Background
            drawRoundRect(
                color = Color.LightGray.copy(alpha = 0.2f),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                size = Size(size.width, size.height)
            )

            // Gradient progress bar segments
            val segmentWidth = size.width / 4

            // Underweight segment
            drawRoundRect(
                color = UnderweightColor,
                topLeft = Offset(0f, 0f),
                size = Size(segmentWidth, size.height),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Normal segment
            drawRect(
                color = NormalWeightColor,
                topLeft = Offset(segmentWidth, 0f),
                size = Size(segmentWidth, size.height)
            )

            // Overweight segment
            drawRect(
                color = OverweightColor,
                topLeft = Offset(segmentWidth * 2, 0f),
                size = Size(segmentWidth, size.height)
            )

            // Obese segment
            drawRoundRect(
                color = ObeseColor,
                topLeft = Offset(segmentWidth * 3, 0f),
                size = Size(segmentWidth, size.height),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        }

        // Indicator
        Box(
            modifier = Modifier
                .fillMaxWidth(position.value)
                .align(Alignment.CenterStart)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(24.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .padding(2.dp)
                    .background(
                        when {
                            position.value <= 0.25f -> UnderweightColor
                            position.value <= 0.5f -> NormalWeightColor
                            position.value <= 0.75f -> OverweightColor
                            else -> ObeseColor
                        },
                        CircleShape
                    )
            )
        }

        // Display BMI value above indicator
        Box(
            modifier = Modifier
                .fillMaxWidth(position.value)
                .align(Alignment.TopStart)
                .padding(top = 8.dp)
        ) {
            Text(
                text = "${bmi.roundToInt()}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .shadow(2.dp),
                color = when {
                    position.value <= 0.25f -> UnderweightColor
                    position.value <= 0.5f -> NormalWeightColor
                    position.value <= 0.75f -> OverweightColor
                    else -> ObeseColor
                }
            )
        }

        // Categories
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp)
        ) {
            Text(
                text = "Kurus",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = UnderweightColor,
                modifier = Modifier.weight(underweightWeight),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Normal",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = NormalWeightColor,
                modifier = Modifier.weight(normalWeight),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Gemuk",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = OverweightColor,
                modifier = Modifier.weight(overweightWeight),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Obesitas",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = ObeseColor,
                modifier = Modifier.weight(obeseWeight),
                textAlign = TextAlign.Center
            )
        }
    }
}