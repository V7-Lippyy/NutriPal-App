package com.example.nutripal.ui.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.example.nutripal.ui.feature.auth.AuthEvent
import com.example.nutripal.ui.feature.auth.AuthViewModel
import com.example.nutripal.ui.feature.auth.ForgotPasswordScreen
import com.example.nutripal.ui.feature.auth.LoginScreen
import com.example.nutripal.ui.feature.auth.RegisterScreen
import com.example.nutripal.ui.feature.bmi.BMIScreen
import com.example.nutripal.ui.feature.dailycalorie.DailyCalorieScreen
import com.example.nutripal.ui.feature.foodlog.AddEditFoodEntryScreen
import com.example.nutripal.ui.feature.foodlog.FoodLogScreen
import com.example.nutripal.ui.feature.home.HomeScreen
import com.example.nutripal.ui.feature.nutrition.NutritionScreen
import com.example.nutripal.ui.feature.onboarding.OnboardingScreen
import com.example.nutripal.ui.feature.onboarding.UserViewModel
import com.example.nutripal.ui.feature.splash.SplashScreen
import com.example.nutripal.util.DataMigrationService
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "AppNavigation"

@Composable
fun AppNavigation(
    userViewModel: UserViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    dataMigrationService: DataMigrationService
) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsState()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        Log.d(TAG, "Setting up auth events collector")
        authViewModel.authEvents.collectLatest { event ->
            Log.d(TAG, "Received auth event: $event")
            when (event) {
                is AuthEvent.LoginSuccess -> {
                    Log.d(TAG, "Login success, navigating to Home")
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
                is AuthEvent.RegisterSuccess -> {
                    Log.d(TAG, "Registration success, navigating to Onboarding")
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
                is AuthEvent.LogoutSuccess -> {
                    Log.d(TAG, "Logout success, navigating to Login")
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
                is AuthEvent.AuthError -> {
                    Log.e(TAG, "Auth error: ${event.message}")
                }
                else -> {
                    Log.d(TAG, "Other auth event: $event")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d(TAG, "Checking authentication state and onboarding status")
        startDestination = try {
            dataMigrationService.migrateDataIfNeeded()
            if (authState.isAuthenticated) {
                Log.d(TAG, "User authenticated, checking onboarding status")
                if (userViewModel.hasCompletedOnboarding()) {
                    Log.d(TAG, "Onboarding completed, start destination is Home")
                    Screen.Home.route
                } else {
                    Log.d(TAG, "Onboarding not completed, start destination is Onboarding")
                    Screen.Onboarding.route
                }
            } else {
                Log.d(TAG, "User not authenticated, start destination is Login")
                Screen.Login.route
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error determining start destination: ${e.message}", e)
            Screen.Login.route
        }
    }

    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated && startDestination != null) {
            if (startDestination == Screen.Home.route ||
                startDestination == Screen.Onboarding.route) {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute == Screen.Login.route || currentRoute == Screen.Register.route) {
                    Log.d(TAG, "Authentication state changed, navigating to $startDestination")
                    navController.navigate(startDestination!!) {
                        popUpTo(currentRoute) { inclusive = true }
                    }
                }
            }
        }
    }

    if (startDestination == null) {
        Log.d(TAG, "Start destination is null, showing loading")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val isMainScreen = when (currentDestination?.route) {
                Screen.Splash.route,
                Screen.Login.route,
                Screen.Register.route,
                Screen.ForgotPassword.route,
                Screen.Onboarding.route,
                Screen.FoodLogAdd.route,
                "${Screen.FoodLogEdit.route}/{entryId}" -> false
                else -> true
            }

            if (isMainScreen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val navItems = Screen.bottomNavItems
                        navItems.forEach { screen ->
                            Log.d(TAG, "Nav item: ${screen?.route}, icon: ${screen?.icon}")
                            if (screen != null && screen.icon != null) {
                                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } ?: false
                                CustomBottomNavItem(
                                    icon = screen.icon,
                                    label = screen.route,
                                    selected = selected,
                                    onClick = {
                                        Log.d(TAG, "Bottom nav clicked: ${screen.route}")
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            } else {
                                Log.w(TAG, "Skipping nav item ${screen?.route ?: "null"} due to null screen or icon")
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
                Log.d(TAG, "Navigating to Splash screen")
                SplashScreen(onNavigateToHome = {
                    val destination = startDestination ?: Screen.Login.route
                    Log.d(TAG, "Splash screen finished, navigating to $destination")
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }

            composable(
                route = Screen.Login.route,
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
                Log.d(TAG, "Navigating to Login screen")
                var needsOnboarding by remember { mutableStateOf<Boolean?>(null) }
                LaunchedEffect(Unit) {
                    needsOnboarding = !userViewModel.hasCompletedOnboarding()
                    Log.d(TAG, "Needs onboarding: $needsOnboarding")
                }

                if (needsOnboarding != null) {
                    LoginScreen(
                        onNavigateToRegister = {
                            Log.d(TAG, "Navigating to Register from Login")
                            navController.navigate(Screen.Register.route)
                        },
                        onNavigateToForgotPassword = {
                            Log.d(TAG, "Navigating to ForgotPassword from Login")
                            navController.navigate(Screen.ForgotPassword.route)
                        },
                        onNavigateToHome = {
                            val destination = if (needsOnboarding!!) Screen.Onboarding.route else Screen.Home.route
                            Log.d(TAG, "Login successful via manual callback, navigating to $destination")
                            navController.navigate(destination) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            composable(
                route = Screen.Register.route,
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
                Log.d(TAG, "Navigating to Register screen")
                RegisterScreen(
                    onNavigateToLogin = {
                        Log.d(TAG, "Navigating to Login from Register")
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        Log.d(TAG, "Register success via manual callback, navigating to Onboarding")
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.ForgotPassword.route,
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
                Log.d(TAG, "Navigating to ForgotPassword screen")
                ForgotPasswordScreen(
                    onNavigateToLogin = {
                        Log.d(TAG, "Navigating to Login from ForgotPassword")
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                        }
                    }
                )
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
                Log.d(TAG, "Navigating to Onboarding screen")
                OnboardingScreen(
                    onOnboardingComplete = {
                        Log.d(TAG, "Onboarding complete, navigating to Home")
                        try {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error navigating after onboarding: ${e.message}", e)
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
                Log.d(TAG, "Navigating to Home screen")
                HomeScreen(
                    onNavigateToBMI = {
                        Log.d(TAG, "Navigating to BMI from Home")
                        navController.navigate(Screen.BMI.route)
                    },
                    onNavigateToCalorie = {
                        Log.d(TAG, "Navigating to DailyCalorie from Home")
                        navController.navigate(Screen.DailyCalorie.route)
                    },
                    onNavigateToActivity = {
                        Log.d(TAG, "Navigating to PhysicalActivity from Home")
                        navController.navigate(Screen.PhysicalActivity.route)
                    },
                    onNavigateToNutrition = {
                        Log.d(TAG, "Navigating to Nutrition from Home")
                        navController.navigate(Screen.Nutrition.route)
                    },
                    onNavigateToFoodLog = {
                        Log.d(TAG, "Navigating to FoodLog from Home")
                        navController.navigate(Screen.FoodLog.route)
                    },
                    onLogout = {
                        Log.d(TAG, "Logout requested from Home")
                        authViewModel.logout()
                    }
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
                Log.d(TAG, "Navigating to BMI screen")
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
                Log.d(TAG, "Navigating to DailyCalorie screen")
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
                Log.d(TAG, "Navigating to PhysicalActivity screen")
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
                Log.d(TAG, "Navigating to Nutrition screen")
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
                Log.d(TAG, "Navigating to FoodLog screen")
                FoodLogScreen(
                    onNavigateToAddEntry = {
                        Log.d(TAG, "Navigating to FoodLogAdd from FoodLog")
                        navController.navigate(Screen.FoodLogAdd.route)
                    },
                    onNavigateToEditEntry = { entryId ->
                        Log.d(TAG, "Navigating to FoodLogEdit with entryId $entryId from FoodLog")
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
                Log.d(TAG, "Navigating to FoodLogAdd screen")
                AddEditFoodEntryScreen(
                    onNavigateBack = {
                        Log.d(TAG, "Navigating back from FoodLogAdd")
                        navController.popBackStack()
                    }
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
                Log.d(TAG, "Navigating to FoodLogEdit screen with entryId $entryId")
                AddEditFoodEntryScreen(
                    foodEntryId = entryId,
                    onNavigateBack = {
                        Log.d(TAG, "Navigating back from FoodLogEdit")
                        navController.popBackStack()
                    }
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
            .width(56.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
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

        if (selected) {
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
}