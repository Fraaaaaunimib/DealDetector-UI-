// java/com/example/navi/AuthenticationExplanationActivity.kt
package com.example.navi

import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.navi.ui.theme.Test3Theme
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.core.view.WindowInsetsControllerCompat

class AuthenticationExplanationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val preferencesManager = remember { PreferencesManager(context) }
            val currentTheme = preferencesManager.currentTheme

            Test3Theme(theme = currentTheme) {
                AuthenticationExplanationScreen(
                    preferencesManager = preferencesManager,
                    currentTheme = preferencesManager.currentTheme,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun ConfigureWindow(currentTheme: String) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    SideEffect {
        val window = (context as AppCompatActivity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.background.toArgb()
    }
}

@Composable
fun UpdateStatusBarTextColor(window: Window, selectedTheme: String) {
    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
    insetsController.isAppearanceLightStatusBars = selectedTheme == stringResource(R.string.light_theme) || (selectedTheme == DEFAULT_THEME && (window.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationExplanationScreen(preferencesManager: PreferencesManager, onBack: () -> Unit, currentTheme: String) {
    val context = LocalContext.current
    val sharedViewModel: SharedViewModel = viewModel(factory = SharedViewModelFactory(preferencesManager))
    val requireIdentification by sharedViewModel.requireIdentification.collectAsState()

    Test3Theme(theme = currentTheme) {
        ConfigureWindow(currentTheme)

        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "") },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back),
                                    tint = MaterialTheme.colorScheme.onBackground)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.request_authentication),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true)
                            ) {
                                showBiometricPrompt(
                                    activity = context as AppCompatActivity,
                                    onSuccess = {
                                        sharedViewModel.setRequireIdentification(!requireIdentification)
                                    },
                                    onCancel = {}
                                )
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.request_authentication_option),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = requireIdentification,
                                onCheckedChange = {
                                    showBiometricPrompt(
                                        activity = context as AppCompatActivity,
                                        onSuccess = {
                                            sharedViewModel.setRequireIdentification(it)
                                        },
                                        onCancel = {}
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.authentication_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}