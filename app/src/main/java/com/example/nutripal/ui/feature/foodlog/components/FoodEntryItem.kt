package com.example.nutripal.ui.feature.foodlog.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.nutripal.data.local.entity.FoodEntry
import com.example.nutripal.domain.model.MealType
import com.example.nutripal.ui.common.theme.ChartBlue
import com.example.nutripal.ui.common.theme.ChartGreen
import com.example.nutripal.ui.common.theme.ChartRed
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FoodEntryItem(
    foodEntry: FoodEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    animationDelay: Int = 0
) {
    var showMenu by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val decimalFormat = DecimalFormat("#,##0.0")
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Animation state for staggered appearance
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    // Get meal type icon and color
    val mealTypeIcon = getFoodEntryMealTypeIcon(MealType.fromString(foodEntry.mealType))
    val mealTypeColor = getFoodEntryMealTypeColor(MealType.fromString(foodEntry.mealType))

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 2.dp)
                .clickable { expanded = !expanded }
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
            ) {
                // Header row with meal type icon, name, time, and options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Meal type icon in circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(mealTypeColor.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = mealTypeIcon,
                            contentDescription = foodEntry.mealType,
                            tint = mealTypeColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = foodEntry.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${foodEntry.servingSize} ${foodEntry.servingUnit}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = timeFormatter.format(foodEntry.time),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${foodEntry.calories.toInt()} kkal",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    IconButton(
                        onClick = { showMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opsi",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onEditClick()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Hapus") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }

                // Expanded details section
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        Divider(modifier = Modifier.padding(bottom = 16.dp))

                        // Macronutrient bars
                        Text(
                            text = "Macronutrients",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Protein progress bar
                        MacroNutrientBar(
                            label = "Protein",
                            value = foodEntry.protein,
                            maxValue = foodEntry.protein + foodEntry.carbs + foodEntry.fat,
                            color = ChartBlue,
                            suffix = "g"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Carbs progress bar
                        MacroNutrientBar(
                            label = "Carbs",
                            value = foodEntry.carbs,
                            maxValue = foodEntry.protein + foodEntry.carbs + foodEntry.fat,
                            color = ChartGreen,
                            suffix = "g"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fat progress bar
                        MacroNutrientBar(
                            label = "Fat",
                            value = foodEntry.fat,
                            maxValue = foodEntry.protein + foodEntry.carbs + foodEntry.fat,
                            color = ChartRed,
                            suffix = "g"
                        )

                        // Additional nutrients if present
                        if (foodEntry.fiber > 0 || foodEntry.sugar > 0) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Additional Nutrients",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Fiber",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "${decimalFormat.format(foodEntry.fiber)}g",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Sugar",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "${decimalFormat.format(foodEntry.sugar)}g",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        // Notes section if present
                        if (!foodEntry.notes.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            Text(
                                text = foodEntry.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroNutrientBar(
    label: String,
    value: Double,
    maxValue: Double,
    color: Color,
    suffix: String = ""
) {
    val decimalFormat = DecimalFormat("#,##0.0")
    val progress = if (maxValue > 0) (value / maxValue).coerceIn(0.0, 1.0) else 0.0

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "${decimalFormat.format(value)}$suffix",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = progress.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

// Renamed these functions to avoid conflicts with MealTypeSelector.kt
@Composable
private fun getFoodEntryMealTypeIcon(mealType: MealType): ImageVector {
    return when (mealType) {
        MealType.BREAKFAST -> Icons.Default.Coffee
        MealType.LUNCH -> Icons.Default.Restaurant
        MealType.DINNER -> Icons.Default.LocalDining
        MealType.SNACK -> Icons.Default.Fastfood
    }
}

@Composable
private fun getFoodEntryMealTypeColor(mealType: MealType): Color {
    return when (mealType) {
        MealType.BREAKFAST -> MaterialTheme.colorScheme.primary
        MealType.LUNCH -> MaterialTheme.colorScheme.secondary
        MealType.DINNER -> MaterialTheme.colorScheme.tertiary
        MealType.SNACK -> MaterialTheme.colorScheme.error
    }
}