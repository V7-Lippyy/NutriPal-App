package com.example.nutripal.ui.feature.foodlog.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.nutripal.domain.model.NutritionItem
import com.example.nutripal.ui.common.theme.ChartBlue
import com.example.nutripal.ui.common.theme.ChartGreen
import com.example.nutripal.ui.common.theme.ChartRed
import com.example.nutripal.ui.common.theme.ChartYellow
import java.text.DecimalFormat

@Composable
fun NutritionSearchResultItem(
    nutritionItem: NutritionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val decimalFormat = DecimalFormat("#,##0.0")

    // Animation state for staggered appearance
    val visibleState = remember {
        MutableTransitionState(false).apply {
            // Start invisible
            targetState = true
        }
    }

    // Calculate small delays for staggered animation (currently not used)
    val staggerDelay = index * 50 // 50ms stagger between items

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            expandFrom = Alignment.Top
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 2.dp)
                .clickable(onClick = onClick)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header with food name, serving size, and add button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Food icon in circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(getFoodIconBackground(nutritionItem.name))
                    ) {
                        Icon(
                            imageVector = getFoodIcon(nutritionItem.name),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = nutritionItem.name.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Serving size: ${nutritionItem.servingSizeGram}g",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    // Add button
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Calories
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Calories",
                        tint = ChartYellow,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${nutritionItem.calories.toInt()} calories",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Macronutrient grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Protein
                    MacronutrientItem(
                        name = "Protein",
                        value = "${decimalFormat.format(nutritionItem.proteinGram)}g",
                        icon = Icons.Default.Egg,
                        iconTint = ChartBlue,
                        modifier = Modifier.weight(1f)
                    )

                    // Carbs
                    MacronutrientItem(
                        name = "Carbs",
                        value = "${decimalFormat.format(nutritionItem.totalCarbohydratesGram)}g",
                        icon = Icons.Default.LocalDrink,
                        iconTint = ChartGreen,
                        modifier = Modifier.weight(1f)
                    )

                    // Fat
                    MacronutrientItem(
                        name = "Fat",
                        value = "${decimalFormat.format(nutritionItem.totalFatGram)}g",
                        icon = Icons.Default.Grass,
                        iconTint = ChartRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MacronutrientItem(
    name: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        // Icon in a circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun getFoodIcon(foodName: String): ImageVector {
    // Simple logic to determine which icon to show based on food name
    return when {
        foodName.contains("chicken") || foodName.contains("meat") || foodName.contains("fish") -> Icons.Default.Egg
        foodName.contains("rice") || foodName.contains("bread") || foodName.contains("pasta") -> Icons.Default.LocalDrink
        foodName.contains("oil") || foodName.contains("butter") -> Icons.Default.Grass
        else -> Icons.Default.Bolt
    }
}

@Composable
fun getFoodIconBackground(foodName: String): Color {
    // Simple logic to determine background color based on food name
    return when {
        foodName.contains("chicken") || foodName.contains("meat") || foodName.contains("fish") -> ChartBlue
        foodName.contains("rice") || foodName.contains("bread") || foodName.contains("pasta") -> ChartGreen
        foodName.contains("oil") || foodName.contains("butter") -> ChartRed
        else -> ChartYellow
    }
}