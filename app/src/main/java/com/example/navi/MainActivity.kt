package com.example.navi

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.navi.ui.theme.Test3Theme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.Crossfade
import java.util.concurrent.Executor
import com.google.firebase.FirebaseApp
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput

//icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.saveable.rememberSaveable

const val DEFAULT_THEME = "Predefinito di sistema"

class MainActivity : FragmentActivity() {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    private var selectedTheme by mutableStateOf(DEFAULT_THEME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        preferencesManager = PreferencesManager(this)
        selectedTheme = preferencesManager.currentTheme
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                // Handle error
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                // Handle success
            }

            override fun onAuthenticationFailed() {
                // Handle failure
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setSubtitle(getString(R.string.biometric_prompt_subtitle))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        if (preferencesManager.requireIdentification) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            setupContent()
        }
    }

    override fun onResume() {
        super.onResume()
        if (preferencesManager.requireIdentification) {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    override fun onPause() {
        super.onPause()
        if (preferencesManager.requireIdentification) {
            biometricPrompt.cancelAuthentication()
        }
    }

    private fun setupContent() {
        Log.d("MainActivity", getString(R.string.setup_content_log))
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            val tabs = resources.getStringArray(R.array.tabs).toList()
            val filledIcons = listOf(
                Icons.Filled.ShoppingCart,
                Icons.Filled.Home,
                Icons.Filled.Search,
                Icons.Filled.Settings
            )
            val outlinedIcons = listOf(
                Icons.Outlined.ShoppingCart,
                Icons.Outlined.Home,
                Icons.Outlined.Search,
                Icons.Outlined.Settings
            )
            MainContent(
                selectedTheme = selectedTheme,
                onThemeChange = { selectedTheme = it },
                defaultTab = 1,
                tabs = tabs,
                filledIcons = filledIcons,
                outlinedIcons = outlinedIcons
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val container = findViewById<FrameLayout>(R.id.container)
        val rotateIn = AnimationUtils.loadAnimation(this, R.anim.rotate_in)
        val rotateOut = AnimationUtils.loadAnimation(this, R.anim.rotate_out)

        container.startAnimation(rotateOut)
        container.startAnimation(rotateIn)
    }
}

@Composable
fun MainContent(
    selectedTheme: String,
    onThemeChange: (String) -> Unit,
    defaultTab: Int = 1,
    tabs: List<String>,
    filledIcons: List<ImageVector>,
    outlinedIcons: List<ImageVector>
) {
    Test3Theme(selectedTheme) {
        val preferencesManager = PreferencesManager(LocalContext.current)
        val viewOption = preferencesManager.viewOption
        val sortOption = preferencesManager.sortOption
        var selectedTab by rememberSaveable { mutableIntStateOf(defaultTab) }

        val configuration = LocalConfiguration.current
        val context = LocalContext.current
        val backgroundColor = MaterialTheme.colorScheme.background
        val surfaceContainerColor = MaterialTheme.colorScheme.surfaceContainer

        SideEffect {
            val window = (context as FragmentActivity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = backgroundColor.toArgb()
            window.navigationBarColor = surfaceContainerColor.toArgb()
        }

        Scaffold(
            bottomBar = {
                if (configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    HorizontalNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        tabs = tabs,
                        filledIcons = filledIcons,
                        outlinedIcons = outlinedIcons
                    )
                }
            },
            content = { innerPadding ->
                Row(
                    modifier = Modifier
                        .padding(innerPadding)
                        .pointerInput(Unit) {
                            var totalDragAmount = 0f
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    totalDragAmount += dragAmount
                                },
                                onDragEnd = {
                                    if (totalDragAmount > 300) { // Threshold for swipe right
                                        if (selectedTab > 0) {
                                            selectedTab -= 1
                                        }
                                    } else if (totalDragAmount < -300) { // Threshold for swipe left
                                        if (selectedTab < tabs.size - 1) {
                                            selectedTab += 1
                                        }
                                    }
                                    totalDragAmount = 0f // Reset drag amount after page change
                                }
                            )
                        }
                ) {
                    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        VerticalNavigationBar(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            tabs = tabs,
                            filledIcons = filledIcons,
                            outlinedIcons = outlinedIcons,
                            preferencesManager = preferencesManager,
                            viewOption = viewOption,
                            onViewOptionChange = { option, rows ->
                                preferencesManager.viewOption = option
                                preferencesManager.offersPerRow = rows
                            },
                            onSortOptionChange = { preferencesManager.sortOption = it },
                        )
                    }
                    Crossfade(targetState = selectedTab, label = "") { tab ->
                        when (tab) {
                            0 -> CategoriesScreen()
                            1 -> DealsScreen(
                                viewOption = viewOption,
                                onViewOptionChange = { option, rows ->
                                    preferencesManager.viewOption = option
                                    preferencesManager.offersPerRow = rows
                                },
                                onSortOptionChange = { preferencesManager.sortOption = it },
                                preferencesManager = preferencesManager
                            )
                            2 -> SearchScreen()
                            3 -> SettingsScreen(
                                preferencesManager = preferencesManager,
                                onLogout = { /* Handle logout */ },
                                onThemeChange = onThemeChange
                            )
                        }
                    }
                }
            }
        )
    }
}