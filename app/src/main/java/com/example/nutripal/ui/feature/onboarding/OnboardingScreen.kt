package com.example.nutripal.ui.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutripal.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Log

// Add this composable in your OnboardingScreen.kt file
@Composable
fun TypingText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    typingSpeed: Long = 50,
    initialDelay: Long = 0
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        displayedText = ""
        delay(initialDelay)

        text.forEachIndexed { index, _ ->
            displayedText = text.substring(0, index + 1)
            delay(typingSpeed)
        }
    }

    Text(
        text = displayedText,
        style = style,
        modifier = modifier
    )
}

@Composable
fun OnboardingScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onOnboardingComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    // State untuk menampilkan halaman welcome
    var showWelcomePage by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    // Efek untuk menampilkan error dalam snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Show loading indicator when saving data
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.isLoading) {
        isLoading = uiState.isLoading
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tombol Back hanya muncul di halaman selamat datang
            if (showWelcomePage) {
                IconButton(
                    onClick = {
                        // Kembali ke halaman input nama
                        showWelcomePage = false
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .zIndex(2f) // Pastikan tombol berada di atas semua elemen
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = if (isDarkTheme) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Background image dengan ukuran lebih besar
            Image(
                painter = painterResource(id = R.drawable.onboarding2),
                contentDescription = "Background Illustration",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(490.dp)
                    .offset(x = 60.dp, y = 22.dp)
                    .zIndex(0f)
            )

            if (!showWelcomePage) {
                // Form input nama
                ExactMatchForm(
                    name = uiState.name,
                    onNameChanged = viewModel::updateName,
                    onSaveClicked = {
                        if (!isLoading) {
                            coroutineScope.launch {
                                isLoading = true
                                viewModel.saveUserDataAndCompleteOnboarding {
                                    // Simpan nama untuk halaman welcome
                                    userName = uiState.name
                                    // Tampilkan halaman welcome
                                    showWelcomePage = true
                                    isLoading = false
                                }
                            }
                        }
                    },
                    isDarkTheme = isDarkTheme,
                    isLoading = isLoading
                )
            } else {
                // Halaman welcome
                ExactMatchWelcomePage(
                    name = userName,
                    onContinueClicked = {
                        try {
                            onOnboardingComplete()
                        } catch (e: Exception) {
                            Log.e("OnboardingScreen", "Error navigating after onboarding: ${e.message}", e)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Terjadi kesalahan, silakan coba lagi")
                            }
                        }
                    },
                    isDarkTheme = isDarkTheme
                )
            }

            // Show loading overlay if loading
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .zIndex(3f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ExactMatchForm(
    name: String,
    onNameChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    isDarkTheme: Boolean,
    isLoading: Boolean = false
) {
    // State untuk animasi
    var showContent by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Use different logo resource based on theme
    val logoResource = if (isDarkTheme) {
        R.drawable.logo_dark
    } else {
        R.drawable.logo
    }

    // Colors based on theme
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val placeholderColor = if (isDarkTheme) Color(0xFFAAAAAA) else Color(0xFF888888)
    val borderColor = if (isDarkTheme) Color.DarkGray else Color.LightGray
    val iconTint = if (isDarkTheme) Color.LightGray else Color.Gray
    val accentTextColor = if (isDarkTheme) Color(0xFF81A1E3) else Color(0xFF4B5D8C)

    // Trigger animasi setelah komposisi
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 28.dp, end = 28.dp, top = 70.dp) // Posisi konten lebih ke atas
            .zIndex(1f), // Ensure form stays above the background image
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Logo dengan ukuran yang konsisten (sama dengan halaman welcome)
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(800))
        ) {
            Image(
                painter = painterResource(id = logoResource),
                contentDescription = "NutriPal Logo",
                modifier = Modifier.size(45.dp) // Ukuran yang sama dengan welcome page
            )
        }

        Spacer(modifier = Modifier.height(40.dp)) // Kurangi jarak

        // Judul persis seperti gambar dengan typing animation
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(900))
        ) {
            TypingText(
                text = "Halo, boleh\nkenalan\ndengan mu?",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = textColor,
                    lineHeight = 42.sp
                ),
                typingSpeed = 40
            )
        }

        Spacer(modifier = Modifier.height(35.dp)) // Kurangi jarak

        // Input yang 100% berfungsi - dengan custom basic input
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(1000))
        ) {
            // Gunakan Text dan BasicTextField untuk kontrol lebih baik
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Basic TextField tanpa padding internal untuk alignment yang lebih baik
                androidx.compose.foundation.text.BasicTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    textStyle = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onSaveClicked()
                        }
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(textColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (name.isEmpty()) {
                                Text(
                                    text = "Alipp",
                                    style = TextStyle(
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = placeholderColor
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tombol panah dalam kotak berlatar abu-abu muda
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(1100))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(enabled = !isLoading) { onSaveClicked() }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = iconTint
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Konfirmasi",
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // Fokus otomatis ke input field
    LaunchedEffect(showContent) {
        if (showContent) {
            delay(500)
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                // Handle potential error when requesting focus
                Log.e("OnboardingScreen", "Error requesting focus: ${e.message}")
            }
        }
    }
}

@Composable
fun ExactMatchWelcomePage(
    name: String,
    onContinueClicked: () -> Unit,
    isDarkTheme: Boolean
) {
    // State untuk animasi
    var showContent by remember { mutableStateOf(false) }

    // Use different logo resource based on theme
    val logoResource = if (isDarkTheme) {
        R.drawable.logo_dark
    } else {
        R.drawable.logo
    }

    // Colors based on theme
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val descriptionColor = if (isDarkTheme) Color.LightGray else Color.DarkGray
    val borderColor = if (isDarkTheme) Color.DarkGray else Color.LightGray
    val iconTint = if (isDarkTheme) Color.LightGray else Color.Gray
    val accentTextColor = if (isDarkTheme) Color(0xFF81A1E3) else Color(0xFF4B5D8C)

    // Efek untuk menampilkan animasi
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 28.dp, end = 28.dp, top = 70.dp) // Posisi konten lebih ke atas
            .zIndex(1f), // Ensure form stays above the background image
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Logo saja tanpa teks
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(800))
        ) {
            Image(
                painter = painterResource(id = logoResource),
                contentDescription = "NutriPal Logo",
                modifier = Modifier.size(45.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp)) // Kurangi jarak

        // Pesan selamat datang dengan typing animation
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(900))
        ) {
            TypingText(
                text = "Selamat Datang di NutriPal,",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = textColor,
                    lineHeight = 42.sp
                ),
                typingSpeed = 40
            )
        }

        // Nama pengguna dengan typing animation
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(950))
        ) {
            TypingText(
                text = name,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = accentTextColor,
                    lineHeight = 42.sp
                ),
                modifier = Modifier.padding(top = 8.dp),
                typingSpeed = 60,
                initialDelay = 800  // Start typing name after welcome message
            )
        }

        Spacer(modifier = Modifier.height(35.dp)) // Kurangi jarak

        // Deskripsi aplikasi dengan typing animation
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(1000))
        ) {
            TypingText(
                text = "NutriPal akan membantu Anda mengelola nutrisi dan gaya hidup sehat setiap hari.",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = descriptionColor,
                    lineHeight = 26.sp
                ),
                modifier = Modifier.padding(end = 20.dp),
                typingSpeed = 20,
                initialDelay = 1500 // Start typing description after name
            )
        }

        Spacer(modifier = Modifier.height(35.dp)) // Kurangi jarak

        // Tombol panah minimalis
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(1100))
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onContinueClicked() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Mulai Perjalanan",
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}