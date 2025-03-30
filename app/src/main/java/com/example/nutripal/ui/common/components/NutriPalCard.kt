package com.example.nutripal.ui.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun NutriPalCard(
    title: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified,
    titleColor: Color = Color(0xFF4CAF50), // Hijau solid yang konsisten
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    elevation: Int = 0, // Tanpa elevasi untuk menghindari efek kotak dalam kotak
    cornerShape: Shape = RoundedCornerShape(16.dp),
    animated: Boolean = true,
    content: @Composable () -> Unit
) {
    // Deteksi mode gelap/terang
    val isDarkMode = isSystemInDarkTheme()

    // Warna hijau yang disesuaikan berdasarkan tema - menggunakan warna dari card di atasnya
    val greenColor = if (isDarkMode) {
        Color(0xFF42B147) // Warna hijau yang serasi dengan card di atasnya untuk dark mode
    } else {
        Color(0xFF4CAF50) // Hijau standard untuk light mode
    }

    // Warna untuk border yang menyesuaikan mode tema
    val borderColor = if (isDarkMode) {
        greenColor.copy(alpha = 0.3f) // Border lebih lembut di dark mode
    } else {
        greenColor.copy(alpha = 0.25f) // Border lebih lembut di light mode
    }

    // Warna background dengan opacity rendah - menyesuaikan dengan card di atasnya
    val actualContainerColor = when {
        containerColor != Color.Unspecified -> containerColor
        isDarkMode -> Color(0xFF0F1F0E).copy(alpha = 0.85f) // Hijau gelap seperti card tips di atasnya
        else -> Color(0xFFE8F5E9).copy(alpha = 0.85f) // Hijau ringan dengan opacity
    }

    // Animation state for entry
    val visibleState = remember {
        MutableTransitionState(animated).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = if (animated) {
            slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                initialOffsetY = { 50 }
            ) + fadeIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            fadeIn(tween(0))
        }
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 0.dp) // Mengurangi padding horizontal
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = cornerShape
                ),
            shape = cornerShape,
            colors = CardDefaults.cardColors(
                containerColor = actualContainerColor,
                contentColor = contentColor
            ),
            // Tidak ada shadow atau border untuk menghindari efek kotak dalam kotak
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header dengan garis hijau solid di samping
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    // Garis vertikal sebagai aksen dengan warna hijau solid
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(24.dp)
                            .background(
                                greenColor,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Judul dengan warna hijau solid
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = greenColor
                    )
                }

                // Konten dengan padding yang konsisten
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)  // Sejajarkan dengan text judul
                ) {
                    content()
                }
            }
        }
    }
}