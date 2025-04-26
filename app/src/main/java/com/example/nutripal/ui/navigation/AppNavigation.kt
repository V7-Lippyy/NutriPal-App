package com.example.nutripal.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nutripal.ui.feature.activity.PhysicalActivityScreen
import com.example.nutripal.ui.feature.bmi.BMIScreen
import com.example.nutripal.ui.feature.dailycalorie.DailyCalorieScreen
import com.example.nutripal.ui.feature.foodlog.AddEditFoodEntryScreen
import com.example.nutripal.ui.feature.foodlog.FoodLogScreen
import com.example.nutripal.ui.feature.home.HomeScreen
import com.example.nutripal.ui.feature.nutrition.NutritionScreen
import com.example.nutripal.ui.feature.onboarding.OnboardingScreen
import com.example.nutripal.ui.feature.onboarding.UserViewModel
import com.example.nutripal.ui.feature.splash.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    userViewModel: UserViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // State untuk mengontrol arah navigasi setelah splash screen
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Cek apakah pengguna sudah menyelesaikan onboarding saat komponen dimuat
    LaunchedEffect(Unit) {
        startDestination = try {
            if (userViewModel.hasCompletedOnboarding()) {
                Screen.Home.route
            } else {
                Screen.Onboarding.route
            }
        } catch (e: Exception) {
            // Jika terjadi error, default ke Onboarding
            android.util.Log.e("AppNavigation", "Error checking onboarding status", e)
            Screen.Onboarding.route
        }
    }

    if (startDestination == null) {
        // Tampilkan loading atau placeholder sampai pengecekan selesai
        return
    }

    Scaffold(
        // Menambahkan insets untuk system bars
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            // Only show bottom navigation bar on main screens
            val isMainScreen = when (currentDestination?.route) {
                Screen.Splash.route,
                Screen.Onboarding.route,
                Screen.FoodLogAdd.route,
                "${Screen.FoodLogEdit.route}/{entryId}" -> false
                else -> true
            }

            if (isMainScreen) {
                // Custom floating bottom navigation bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 6.dp) // Remove padding to avoid visible "box"
                ) {
                    // Navigation row without visible container box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp) // Slightly taller
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hardcoded navigation items untuk keamanan
                        val navItems = listOf(
                            Screen.Home,
                            Screen.BMI,
                            Screen.DailyCalorie,
                            Screen.PhysicalActivity,
                            Screen.Nutrition,
                            Screen.FoodLog
                        )

                        navItems.forEach { screen ->
                            val hasIcon = screen.icon != null
                            if (hasIcon) {
                                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                                // Custom nav item
                                CustomBottomNavItem(
                                    icon = screen.icon!!,
                                    label = stringResource(id = screen.resourceId),
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onNavigateToHome = {
                    scope.launch {
                        val destination = try {
                            if (userViewModel.hasCompletedOnboarding()) {
                                Screen.Home.route
                            } else {
                                Screen.Onboarding.route
                            }
                        } catch (e: Exception) {
                            Screen.Onboarding.route
                        }

                        navController.navigate(destination) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                })
            }

            composable(
                route = Screen.Onboarding.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                }
            ) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.Home.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                }
            ) {
                HomeScreen(
                    onNavigateToBMI = { navController.navigate(Screen.BMI.route) },
                    onNavigateToCalorie = { navController.navigate(Screen.DailyCalorie.route) },
                    onNavigateToActivity = { navController.navigate(Screen.PhysicalActivity.route) },
                    onNavigateToNutrition = { navController.navigate(Screen.Nutrition.route) },
                    onNavigateToFoodLog = { navController.navigate(Screen.FoodLog.route) }
                )
            }

            composable(
                route = Screen.BMI.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                }
            ) {
                BMIScreen()
            }

            composable(
                route = Screen.DailyCalorie.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                }
            ) {
                DailyCalorieScreen()
            }

            composable(
                route = Screen.PhysicalActivity.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                }
            ) {
                PhysicalActivityScreen()
            }

            composable(
                route = Screen.Nutrition.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                }
            ) {
                NutritionScreen()
            }

            composable(
                route = Screen.FoodLog.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                }
            ) {
                FoodLogScreen(
                    onNavigateToAddEntry = { navController.navigate(Screen.FoodLogAdd.route) },
                    onNavigateToEditEntry = { entryId ->
                        navController.navigate("${Screen.FoodLogEdit.route}/$entryId")
                    }
                )
            }

            composable(
                route = Screen.FoodLogAdd.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(300)
                    )
                }
            ) {
                AddEditFoodEntryScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "${Screen.FoodLogEdit.route}/{entryId}",
                arguments = listOf(navArgument("entryId") { type = NavType.LongType }),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(300)
                    )
                }
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getLong("entryId") ?: -1L
                AddEditFoodEntryScreen(
                    foodEntryId = entryId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun CustomBottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
            .width(56.dp) // Lebih kecil karena navbar floating
    ) {
        // Icon dengan indikator saat dipilih
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Icon dengan background lingkaran saat dipilih
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .then(
                        if (selected) {
                            Modifier.background(
                                color = primaryColor.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                        } else Modifier
                    )
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected) primaryColor else unselectedColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Label yang kompak
        if (selected) {
            // Hanya tampilkan label jika item dipilih
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = primaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
} s