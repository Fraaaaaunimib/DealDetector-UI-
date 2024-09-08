package com.example.navi

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.navi.ui.theme.Test3Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.draw.clip

/* AuthExplain: questo file serve per spiegare l'autenticazione biometrica, si apre quando schiacci
non sul toggle ma sulla parte di testo nelle Impostazioni.
Altre cose da metterci: toggle per selezionare quando chiedere autenticazione biometrica
Da mettere a posto: se io cambio l'impostazione in questa pagina, non si cambia anche in Settings
NOTA PER TUTTI GLI ALTRI FILE: l'errore "Composable invocations can only happen..." non è un errore
importante
* */
class AuthenticationExplanationActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val preferencesManager = remember { PreferencesManager(context) }
            val currentTheme = preferencesManager.currentTheme

            Test3Theme(theme = currentTheme) {
                val isDarkTheme = isSystemInDarkTheme()
                val backgroundColor = MaterialTheme.colorScheme.background

                SideEffect {
                    val window = (context as FragmentActivity).window
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                    window.statusBarColor = backgroundColor.toArgb()
                    insetsController.isAppearanceLightStatusBars = !isDarkTheme
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    AuthenticationExplanationScreen(
                        preferencesManager = preferencesManager,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationExplanationScreen(
    preferencesManager: PreferencesManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var requireIdentification by remember { mutableStateOf(preferencesManager.
    requireIdentification) }
    val currentTheme = preferencesManager.currentTheme

    Test3Theme(theme = currentTheme) {
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
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back",
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
                        text = "Richiedi autenticazione quando apro l'app",
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
                activity = context as FragmentActivity,
                onSuccess = {
                    requireIdentification = !requireIdentification
                    preferencesManager.requireIdentification = requireIdentification
                },
                onFailure = {},
                onCancel = {
                    requireIdentification = preferencesManager.requireIdentification
                }
            )
        }
        .padding(16.dp)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Richiedi autenticazione",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = requireIdentification,
            onCheckedChange = {
                showBiometricPrompt(
                    activity = context as FragmentActivity,
                    onSuccess = {
                        requireIdentification = it
                        preferencesManager.requireIdentification = it
                    },
                    onFailure = {},
                    onCancel = {
                        requireIdentification = preferencesManager.requireIdentification
                    }
                )
            }
        )
    }
}
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Questa opzione richiede l'autenticazione" +
                                " biometrica o il PIN ogni volta che apri l'app " +
                                "per garantire la sicurezza del tuo account.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}