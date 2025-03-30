package com.example.nutripal

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.nutripal.ui.common.theme.NutriPalTheme
import com.example.nutripal.ui.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the splash screen visible for the duration of app initialization
        splashScreen.setKeepOnScreenCondition { false }

        // Konfigurasi untuk membuat tampilan edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Hapus flag translucent yang mungkin menghalangi transparansi
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // Aktifkan flag untuk menggambar di belakang system bars
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // Membuat status bar benar-benar transparan
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        // Kontrol warna ikon pada status bar
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        val darkTheme = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
        controller.isAppearanceLightStatusBars = !darkTheme

        setContent {
            NutriPalTheme {
                AppNavigation()
            }
        }
    }
}