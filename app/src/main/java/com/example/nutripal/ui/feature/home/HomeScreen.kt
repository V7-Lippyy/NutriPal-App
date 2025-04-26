package com.example.nutripal.ui.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.nutripal.ui.common.components.NutriPalButton
import com.example.nutripal.ui.common.components.NutriPalCard
import com.example.nutripal.ui.feature.auth.AuthViewModel
import com.example.nutripal.ui.feature.foodlog.FoodLogViewModel
import com.example.nutripal.ui.feature.onboarding.UserViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToBMI: () -> Unit,
    onNavigateToCalorie: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToNutrition: () -> Unit,
    onNavigateToFoodLog: () -> Unit,
    onLogout: () -> Unit,
    foodLogViewModel: FoodLogViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val foodLogUiState by foodLogViewModel.uiState.collectAsState()
    val userData by userViewModel.userData.collectAsState()
    val today = Date()

    // Ensure we're looking at today's entries
    if (!isSameDay(foodLogUiState.selectedDate, today)) {
        foodLogViewModel.selectDate(today)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header with welcome message and logout button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Welcome message with user name
                if (userData.name.isNotBlank()) {
                    Text(
                        text = "Selamat Datang, ${userData.name}!",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                    )
                }

                // Logout button
                IconButton(
                    onClick = onLogout,
                    contentDescription = "Logout",
                    iconTint = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Banner image carousel - pure images with correct aspect ratio
            BannerCarousel()

            Spacer(modifier = Modifier.height(24.dp))

            // Summary cards row (daily & monthly intake)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Daily intake card
                SummaryCard(
                    title = "Asupan Hari Ini",
                    value = "${foodLogUiState.totalCalories.toInt()}",
                    unit = "kkal",
                    date = today,
                    icon = Icons.Default.LocalFireDepartment,
                    onClick = onNavigateToFoodLog,
                    modifier = Modifier.weight(1f)
                )

                // Monthly intake card
                SummaryCard(
                    title = "Asupan Bulan Ini",
                    value = "${foodLogUiState.monthlyCalories.toInt()}",
                    unit = "kkal",
                    date = today,
                    icon = Icons.Default.TrendingUp,
                    onClick = onNavigateToFoodLog,
                    modifier = Modifier.weight(1f),
                    isMonthly = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Feature cards section
            Text(
                text = "Fitur NutriPal",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // First row of feature cards (2 cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    icon = Icons.Filled.Calculate,
                    title = "BMI",
                    backgroundColor = Color(0xFF4682B4),
                    onClick = onNavigateToBMI,
                    description = "Cek indeks massa tubuh",
                    modifier = Modifier.weight(1f)
                )

                FeatureCard(
                    icon = Icons.Filled.MonitorWeight,
                    title = "Kalori",
                    backgroundColor = Color(0xFF4CAF50),
                    onClick = onNavigateToCalorie,
                    description = "Hitung kebutuhan kalori",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Second row of feature cards (2 cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    icon = Icons.Filled.FitnessCenter,
                    title = "Aktivitas",
                    backgroundColor = Color(0xFFFF9800),
                    onClick = onNavigateToActivity,
                    description = "Catat aktivitas fisik",
                    modifier = Modifier.weight(1f)
                )

                FeatureCard(
                    icon = Icons.Filled.Restaurant,
                    title = "Nutrisi",
                    backgroundColor = Color(0xFFE91E63),
                    onClick = onNavigateToNutrition,
                    description = "Info kandungan nutrisi",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Third row with a single card
            FeatureCard(
                icon = Icons.Filled.MenuBook,
                title = "Catatan Makanan",
                backgroundColor = Color(0xFF673AB7),
                onClick = onNavigateToFoodLog,
                description = "Catat asupan makanan harian"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick tips
            Text(
                text = "Tips Gaya Hidup Sehat",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(getHealthTips()) { tip ->
                    HealthTipCard(tip = tip)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Health reminder card
            NutriPalCard(
                title = "Jaga Kesehatan Anda",
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hidup sehat diawali dengan pemahaman yang baik tentang tubuh Anda dan kebutuhan nutrisinya.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "Mulai perjalanan kesehatan Anda",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Gunakan fitur-fitur NutriPal untuk memantau kesehatan",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            NutriPalButton(
                text = "Logout",
                onClick = onLogout,
                backgroundColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                modifier = Modifier.fillMaxWidth()
            )

            // Add bottom padding to avoid FAB overlap
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * A custom IconButton with icon and text for the logout functionality
 */
@Composable
fun IconButton(
    onClick: () -> Unit,
    contentDescription: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * A simpler banner carousel that just shows the images without
 * extra decorations or containers.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerCarousel() {
    // Image URLs - high resolution banner images
    val imageUrls = listOf(
        "https://media-hosting.imagekit.io/ed00fcbbeb884cd2/banner1.png?Expires=1837886746&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=D3Mcf8zpS-Uh~axf7tyaRtBP0VPO9VWEFX1g~Te-snmrUCcCMP2KPO-tbGqyE4agqQPI3OekU3Urqp-j1V9I88lwtwDsh7mYyZyzi1VRUQAV~ugqE0Clje6hx3JhsYOcXyrfaGb6XY0oyZ7dorUO3TDdpRVWPChwTLoc~opX4e83y1akJPfGZrIrEArzIekOnGZKx1K17E6qrRkhYFsIYRCCYtqnwTIhvE6D5PcCWdJdZWaxRSw1G7Z1b0yIfuOkFAI~Ma6u6q3ZtBgg6NBDW5o4ZyxUgIuVIg6GOQtBDwP4E3WHbaNufVezIOqZRGru9-40JtMKSEfy00Q6AUS9OQ__",
        "https://media-hosting.imagekit.io/a8e0724c43084ed9/banner1%20(2).png?Expires=1837886746&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=VYyv37tQZpOcozO8HkdvtOg5bOt0CwW6tIIyRSEXvcOpjN9LM7J9YMrmLjln4KrrKAkc3nRLHukRUg6CqV2Pck3L-05XLjQZxGukKW4y3a18hR4vJgyYIoazm7AVLdtAEQtE-Cb3hdd9iGXLFkSL5tFSDcgjs8nNEavtZLjs23-skFmE5lld2pB6NQHJbfNKPl3D9hNTj5mY75kK6TlOlstkxcN3nXBNAK8mLLpxvxhCvLXEsGkK1Zl-QUwgJhnUtisjTr8MWSiLDjUwH3tW6WOOmJJXkN4ZS42Czxu9Qj8bJTCK4VEjlb4VSi4zEtEQX58sh5JVPsle1Cr9nv1xjw__",
        "https://media-hosting.imagekit.io/f0efc59469784291/banner1%20(3).png?Expires=1837886746&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=tNoT4yIhBJuvodkTZLntt9i7tOem4VeKDOVF926zgNKeIr7o2r-eU9V~0t4bRi43ikEOAVFD-c6VfhFv7vXdbPrqsNz5HuDw1YtPTTgwFuQWIRukh-cRkVjFyhX43J52Z3sHNMTxZpS-19Fwi3cIOMUW3EtNyYhn7co5vO1c47QQnn1tfNoftRNaxDSA6hYjoXk8coUCWOXekOivU5y-TGZClOq9U5wiPp6V46SEDeVAqPiQ5KGUU9885FlmX6YmhyO8kQnP-Hw-kaqQn230pjzw3vT~FqgOSmc-JEwabXTC~T7ffQSAty6XYrd0hWWtB9Vsg0P4d33cP0iz3xwebA__",
        "https://media-hosting.imagekit.io/cd5d2a047f1e4459/banner1%20(4).png?Expires=1837886746&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=Z5~BrIyc-CXWSQciJhPj45OVixOiqBFHKB9KU001CvvPt1HHJZ6ON6MikqN3A2pVHWW5zHn8ta5bh3JiTuQbW46VJR8NNe9vtLgp1npPTjS679MOhHeV7I0fHAqJ0Ps1K5Vxw8ZdsyVfx-vDMh4hWprA7SK4AebktDUW6-puKDewT7xEUtJiY2Spdej2u6GJX0LHFG6VLqBK7Ncbef49TvonNXsS0U4RzQCsooYi6KZRByqoWZp25glJ7ONhfuf5j46ip-atcPGurG6~QJ3avj60lTaoE62k5iR7hsxe2Ntn17lLfKA-TgrP8-EVMJgNThKEXYlqpvgVL7M9MiS1qQ__"
    )

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    // Auto-scrolling effect
    LaunchedEffect(Unit) {
        while(true) {
            delay(3000) // 3 seconds delay
            val nextPage = (pagerState.currentPage + 1) % imageUrls.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    // Just the pager with images at the correct aspect ratio, no card or other decorations
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1920f / 600f)
            .clip(RoundedCornerShape(8.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = imageUrls[page],
                contentDescription = "Banner image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    description: String,
    modifier: Modifier = Modifier
) {
    // Gunakan isSystemInDarkTheme untuk mendeteksi mode gelap atau terang
    val isDarkMode = isSystemInDarkTheme()

    // Pilih warna kontainer berdasarkan mode
    val containerColor = if (isDarkMode) {
        // Warna gelap yang hampir sama dengan latar belakang di dark mode
        // Gunakan opacity yang sangat rendah agar tidak terlihat seperti kotak dalam kotak
        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    } else {
        // Warna putih untuk light mode
        Color.White
    }

    Card(
        modifier = modifier
            .height(70.dp)
            .border(
                width = 1.dp,
                color = if (isDarkMode)
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkMode) 0.dp else 0.5.dp,
            pressedElevation = if (isDarkMode) 0.5.dp else 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp)
        ) {
            // Icon with background
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(backgroundColor)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryCard(
    title: String,
    value: String,
    unit: String,
    date: Date,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isMonthly: Boolean = false
) {
    val dateFormatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
    val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))

    val formattedDate = if (isMonthly) {
        monthFormatter.format(date)
    } else {
        dateFormatter.format(date)
    }

    ElevatedCard(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Bottom)
                )
            }
        }
    }
}

// Helper function to check if two dates are on the same day
private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = date1
    cal2.time = date2
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

data class HealthTip(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun HealthTipCard(tip: HealthTip) {
    // Gunakan isSystemInDarkTheme untuk mendeteksi mode gelap atau terang
    val isDarkMode = isSystemInDarkTheme()

    // Pilih warna kontainer berdasarkan mode - dengan sentuhan warna berbeda
    val containerColor = if (isDarkMode) {
        // Sedikit warna primer dengan transparansi tinggi untuk mode gelap
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    } else {
        // Warna primer yang sangat ringan untuk mode terang
        MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
    }

    Card(
        modifier = Modifier
            .width(280.dp)
            .height(160.dp)
            .border(
                width = 1.dp,
                color = if (isDarkMode)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        // Highlight di bagian atas card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon dengan style yang berbeda dari FeatureCard
                Icon(
                    imageVector = tip.icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Garis pembatas tipis
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun getHealthTips(): List<HealthTip> {
    return listOf(
        HealthTip(
            title = "Konsumsi Cukup Air",
            description = "Minum minimal 8 gelas air per hari untuk menjaga tubuh tetap terhidrasi dan mendukung fungsi metabolisme.",
            icon = Icons.Default.LocalFireDepartment
        ),
        HealthTip(
            title = "Olahraga Teratur",
            description = "Lakukan aktivitas fisik setidaknya 30 menit per hari, 5 kali seminggu untuk kesehatan jantung dan kesejahteraan secara keseluruhan.",
            icon = Icons.Default.DirectionsRun
        ),
        HealthTip(
            title = "Pola Makan Seimbang",
            description = "Pastikan makanan Anda mengandung karbohidrat, protein, lemak sehat, serat, vitamin, dan mineral dalam jumlah yang tepat.",
            icon = Icons.Default.Restaurant
        ),
        HealthTip(
            title = "Cukup Istirahat",
            description = "Tidur 7-9 jam setiap malam untuk membantu tubuh pulih dan menjaga keseimbangan hormon.",
            icon = Icons.Default.Bedtime // Menggunakan icon tempat tidur yang lebih sesuai
        )
    )
}