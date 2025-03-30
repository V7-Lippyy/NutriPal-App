package com.example.nutripal.ui.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.nutripal.R
import com.example.nutripal.ui.common.components.NutriPalCard
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
    foodLogViewModel: FoodLogViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel() // Tambahkan UserViewModel
) {
    val foodLogUiState by foodLogViewModel.uiState.collectAsState()
    val userData by userViewModel.userData.collectAsState() // Dapatkan data pengguna
    val today = Date() // Use java.util.Date instead of LocalDate

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
            // Tambahkan pesan selamat datang dengan nama pengguna
            if (userData.name.isNotBlank()) {
                Text(
                    text = "Selamat Datang, ${userData.name}!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Replace HomeHeader with ImageSlider
            AutoScrollingImagePager()

            Spacer(modifier = Modifier.height(24.dp))

            // Today's food summary card
            FoodSummaryCard(
                totalCalories = foodLogUiState.totalCalories,
                date = today,
                onClickViewMore = onNavigateToFoodLog
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Feature cards
            Text(
                text = "Fitur NutriPal",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Feature grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FeatureCard(
                    icon = Icons.Filled.Calculate,
                    title = "BMI",
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToBMI
                )

                Spacer(modifier = Modifier.width(16.dp))

                FeatureCard(
                    icon = Icons.Filled.MonitorWeight,
                    title = "Kalori",
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCalorie
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FeatureCard(
                    icon = Icons.Filled.FitnessCenter,
                    title = "Aktivitas",
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToActivity
                )

                Spacer(modifier = Modifier.width(16.dp))

                FeatureCard(
                    icon = Icons.Filled.Restaurant,
                    title = "Nutrisi",
                    backgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToNutrition
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            FeatureCard(
                icon = Icons.Filled.MenuBook,
                title = "Catatan Makanan",
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToFoodLog
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

            // Recent activity
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
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoScrollingImagePager() {
    // Image URLs - high resolution banner images
    val imageUrls = listOf(
        "https://media-hosting.imagekit.io/ed00fcbbeb884cd2/banner1.png?Expires=1837886746&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=D3Mcf8zpS-Uh~axf7tyaRtBP0VPO9VWEFX1g~Te-snmrUCcCMP2KPO-tbGqyE4agqQPI3OekU3Urqp-j1V9I88lwtwDsh7mYyZyzi1VRUQAV~ugqE0Clje6hx3JhsYOcXyrfaGb6XY0oyZ7dorUO3TDdpRVWPChwTLoc~opX4e83y1akJPfGZrIrEArzIekOnGZKx1K17E6qrRkhYFsIYRCCYtqnwTIhvE6D5PcCWdJdZWaxRSw1G7Z1b0yIfuOkFAI~Ma6u6q3ZtBgg6NBDW5o4ZyxUgIuVIg6GOQtBDwP4E3WHbaNufVezIOqZRGru9-40JtMKSEfy00Q6AUS9OQ__",
        "https://media-hosting.imagekit.io/a8e0724c43084ed9/banner1%20(2).png?Expires=1837886746&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=VYyv37tQZpOcozO8HkdvtOg5bOt0CwW6tIIyRSEXvcOpjN9LM7J9YMrmLjln4KrrKAkc3nRLHukRUg6CqV2Pck3L-05XLjQZxGukKW4y3a18hR4vJgyYIoazm7AVLdtAEQtE-Cb3hdd9iGXLFkSL5tFSDcgjs8nNEavtZLjs23-skFmE5lld2pB6NQHJbfNKPl3D9hNTj5mY75kK6TlOlstkxcN3nXBNAK8mLLpxvxhCvLXEsGkK1Zl-QUwgJhnUtisjTr8MWSiLDjUwH3tW6WOOmJJXkN4ZS42Czxu9Qj8bJTCK4VEjlb4VSi4zEtEQX58sh5JVPsle1Cr9nv1xjw__",
        "https://media-hosting.imagekit.io/f0efc59469784291/banner1%20(3).png?Expires=1837886746&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=tNoT4yIhBJuvodkTZLntt9i7tOem4VeKDOVF926zgNKeIr7o2r-eU9V~0t4bRi43ikEOAVFD-c6VfhFv7vXdbPrqsNz5HuDw1YtPTTgwFuQWIRukh-cRkVjFyhX43J52Z3sHNMTxZpS-19Fwi3cIOMUW3EtNyYhn7co5vO1c47QQnn1tfNoftRNaxDSA6hYjoXk8coUCWOXekOivU5y-TGZClOq9U5wiPp6V46SEDeVAqPiQ5KGUU9885FlmX6YmhyO8kQnP-Hw-kaqQn230pjzw3vT~FqgOSmc-JEwabXTC~T7ffQSAty6XYrd0hWWtB9Vsg0P4d33cP0iz3xwebA__",
        "https://media-hosting.imagekit.io/cd5d2a047f1e4459/banner1%20(4).png?Expires=1837886746&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=Z5~BrIyc-CXWSQciJhPj45OVixOiqBFHKB9KU001CvvPt1HHJZ6ON6MikqN3A2pVHWW5zHn8ta5bh3JiTuQbW46VJR8NNe9vtLgp1npPTjS679MOhHeV7I0fHAqJ0Ps1K5Vxw8ZdsyVfx-vDMh4hWprA7SK4AebktDUW6-puKDewT7xEUtJiY2Spdej2u6GJX0LHFG6VLqBK7Ncbef49TvonNXsS0U4RzQCsooYi6KZRByqoWZp25glJ7ONhfuf5j46ip-atcPGurG6~QJ3avj60lTaoE62k5iR7hsxe2Ntn17lLfKA-TgrP8-EVMJgNThKEXYlqpvgVL7M9MiS1qQ__"
    )

    val pagerState = rememberPagerState { imageUrls.size }

    // Auto-scrolling effect
    LaunchedEffect(Unit) {
        while(true) {
            delay(3000) // 3 seconds delay
            val nextPage = (pagerState.currentPage + 1) % imageUrls.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Set height based on 1920x600 aspect ratio (600/1920 = 0.3125)
            .aspectRatio(1920f / 600f)
            .clip(RoundedCornerShape(16.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val context = LocalContext.current
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrls[page])
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "Banner image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // You can use a CircularProgressIndicator here if you want
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSummaryCard(
    totalCalories: Double,
    date: Date,
    onClickViewMore: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
    val formattedDate = dateFormatter.format(date)

    ElevatedCard(
        onClick = onClickViewMore,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Asupan Hari Ini",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "${totalCalories.toInt()} kkal",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Text(
                        text = "Total kalori hari ini",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Klik untuk melihat detail catatan makanan hari ini",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.End)
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = modifier
            .height(120.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

data class HealthTip(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun HealthTipCard(tip: HealthTip) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(160.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = tip.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodySmall
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
            icon = Icons.Default.MonitorWeight
        )
    )
}