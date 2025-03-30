package com.example.nutripal.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
                NavigationBar(
                    modifier = Modifier.shadow(8.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
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

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = screen.icon!!,  // Safe karena sudah dicek dengan hasIcon
                                        contentDescription = stringResource(id = screen.resourceId)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(id = screen.resourceId),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
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