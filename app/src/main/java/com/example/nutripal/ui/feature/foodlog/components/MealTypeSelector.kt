package com.example.nutripal.ui.feature.foodlog.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nutripal.domain.model.MealType

@Composable
fun MealTypeSelector(
    selectedMealType: MealType,
    onMealTypeSelected: (MealType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MealType.values().forEach { mealType ->
            MealTypeItem(
                mealType = mealType,
                isSelected = mealType == selectedMealType,
                onClick = { onMealTypeSelected(mealType) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MealTypeItem(
    mealType: MealType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Get meal type icon and color
    val icon = getMealTypeIcon(mealType)
    val baseColor = getMealTypeColor(mealType)

    // Animated scale for selection
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mealTypeScaleAnimation"
    )

    // Animated colors
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) baseColor.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "mealTypeBackgroundAnimation"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) baseColor else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "mealTypeIconAnimation"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) baseColor else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(durationMillis = 300),
        label = "mealTypeTextAnimation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    color = if (isSelected) baseColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) baseColor else Color.Transparent,
                    shape = CircleShape
                )
                .shadow(
                    elevation = if (isSelected) 2.dp else 0.dp,
                    shape = CircleShape,
                    clip = true
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = mealType.displayName,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .height(20.dp), // Fixed height for text to prevent layout jumps
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = mealType.displayName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun getMealTypeIcon(mealType: MealType): ImageVector {
    return when (mealType) {
        MealType.BREAKFAST -> Icons.Default.Coffee
        MealType.LUNCH -> Icons.Default.Restaurant
        MealType.DINNER -> Icons.Default.LocalDining
        MealType.SNACK -> Icons.Default.Fastfood
    }
}

@Composable
fun getMealTypeColor(mealType: MealType): Color {
    return when (mealType) {
        MealType.BREAKFAST -> MaterialTheme.colorScheme.primary
        MealType.LUNCH -> MaterialTheme.colorScheme.secondary
        MealType.DINNER -> MaterialTheme.colorScheme.tertiary
        MealType.SNACK -> MaterialTheme.colorScheme.error
    }
}