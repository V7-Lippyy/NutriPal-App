package com.example.nutripal.ui.common.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun NutriPalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabledBackgroundColor: Color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
    disabledContentColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
    elevation: Int = 2,
    shape: Shape = RoundedCornerShape(12.dp),
    includeHapticFeedback: Boolean = true
) {
    // Animation properties
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }

    // Animate scale when pressed
    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(
                targetValue = 0.95f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    // Color animation
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (enabled) backgroundColor else disabledBackgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "backgroundColorAnimation"
    )

    Box(
        modifier = modifier
            .scale(scale.value)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = animatedBackgroundColor,
                contentColor = contentColor,
                disabledContainerColor = disabledBackgroundColor,
                disabledContentColor = disabledContentColor
            ),
            interactionSource = interactionSource,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = elevation.dp,
                pressedElevation = (elevation * 1.5).dp,
                disabledElevation = 0.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}