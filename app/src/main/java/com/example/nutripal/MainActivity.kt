package com.example.nutripal

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.nutripal.ui.common.theme.NutriPalTheme
import com.example.nutripal.ui.navigation.AppNavigation
import com.example.nutripal.util.DataMigrationService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataMigrationService: DataMigrationService

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Log app startup
        Log.d("MainActivity", "App starting, enabling Firebase network")

        // Enable Firestore network
        try {
            FirebaseFirestore.getInstance().enableNetwork()
                .addOnSuccessListener {
                    Log.d("MainActivity", "Firebase network enabled successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Failed to enable Firebase network: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("MainActivity", "Exception enabling Firebase network: ${e.message}", e)
        }

        splashScreen.setKeepOnScreenCondition { false }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        val darkTheme = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
        controller.isAppearanceLightStatusBars = !darkTheme

        setContent {
            NutriPalTheme {
                AppNavigation(dataMigrationService = dataMigrationService)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-enable Firebase network on resume to ensure connectivity
        try {
            FirebaseFirestore.getInstance().enableNetwork()
                .addOnSuccessListener {
                    Log.d("MainActivity", "Firebase network re-enabled on resume")
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "Failed to re-enable Firebase network on resume: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("MainActivity", "Exception re-enabling Firebase network on resume: ${e.message}", e)
        }
    }
}