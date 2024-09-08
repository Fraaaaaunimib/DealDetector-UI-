package com.example.navi

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

//icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.font.FontWeight
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions

// Constants for strings
const val PROFILE_ICON_DESC = "Profile Icon"
const val USERNAME = "Username"
const val USER_EMAIL = "user@example.com"
const val SUPERMARKETS_OFFERS = "Supermercati e offerte"
const val HISTORY = "Cronologia"
const val FAVORITE_SUPERMARKETS = "Supermercati preferiti"
const val USER_ACCOUNT_SECURITY = "Account utente e sicurezza"
const val REQUIRE_AUTHENTICATION = "Richiedi autenticazione quando apro l'app"
const val PASSWORD = "Modifica la password del tuo account"
const val LOGOUT = "Logout"
const val APP_SETTINGS = "Impostazioni dell'app"
const val NOTIFICATIONS = "Notifiche"
const val THEME = "Tema"

val CATEGORY_SPACING = 16.dp

//Pagina impostazioni - praticamente finita
@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    onLogout: () -> Unit,
    onThemeChange: (String) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    var showThemeDialog by remember { mutableStateOf(false) }
    var requireIdentification by remember { mutableStateOf(preferencesManager.requireIdentification) }
    var currentTheme by rememberSaveable { mutableStateOf(preferencesManager.currentTheme) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item { ProfileSection() }
        item { SettingsCategory(title = "Supermercati e offerte") }
        item { SettingsEntry(icon = Icons.Default.Store, title = "Supermercati preferiti",
            onClick = {
                val intent = Intent(context, FavShopsActivity::class.java)
                context.startActivity(intent)
            }
            ) }
        item { Spacer(modifier = Modifier.height(CATEGORY_SPACING)) }
        item { SettingsCategory(title = "Impostazioni dell'app") }
        item {
            SettingsEntry(
                icon = Icons.Filled.Notifications,
                title = "Notifiche",
                onClick = {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                }
            )
        }
        item {
            SettingsEntry(
                icon = Icons.Filled.Palette,
                title = "Tema",
                subtitle = currentTheme,
                onClick = { showThemeDialog = true }
            )
        }
        item {
            SettingsEntry(
                icon = Icons.Default.Build,
                title = "Changelog",
                onClick = {
                    val intent = Intent(context, ChangelogActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
        item { Spacer(modifier = Modifier.height(CATEGORY_SPACING)) }
        item { SettingsCategory(title = "Account utente e sicurezza") }
        item {
            SettingsEntryToggle(
                icon = Icons.Default.Security,
                title = "Richiedi autenticazione quando apro l'app",
                checked = requireIdentification,
                onCheckedChange = {
                    showBiometricPrompt(
                        activity = context as FragmentActivity,
                        onSuccess = {
                            requireIdentification = it
                            preferencesManager.requireIdentification = it
                        },
                        onFailure = {},
                        onCancel = {}
                    )
                },
                onClick = {
                    val intent = Intent(context, AuthenticationExplanationActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
        item {
            SettingsEntry(
                icon = Icons.Default.Lock,
                title = "Modifica la password del tuo account",
                onClick = {
                    showBiometricPrompt(
                        activity = context as FragmentActivity,
                        onSuccess = {},
                        onFailure = {},
                        onCancel = {}
                    )
                }
            )
        }
        item {
            SettingsEntry(
                icon = Icons.Default.ExitToApp,
                title = "Esci dal tuo account",
                onClick = { showLogoutDialog = true }
            )
        }
        item { Spacer(modifier = Modifier.height(CATEGORY_SPACING)) }
    }

    if (showThemeDialog) {
        ThemeDialog(
            onDismiss = { showThemeDialog = false },
            preferencesManager = preferencesManager,
            onThemeChange = {
                currentTheme = it
                preferencesManager.currentTheme = it
                onThemeChange(it)
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Esci dal tuo account") },
            text = { Text("Sei sicuro di voler uscire dal tuo account?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancella")
                }
            }
        )
    }
}

@Composable
fun ProfileSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = PROFILE_ICON_DESC,
            modifier = Modifier.size(70.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = USERNAME,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = USER_EMAIL,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { /* Handle edit profile action */ },
            modifier = Modifier
                .size(24.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit Profile",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun SettingsEntryToggle(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val isLightTheme = !isSystemInDarkTheme()
    val switchColors = if (isLightTheme) {
        SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    } else {
        SwitchDefaults.colors()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier
                .height(24.dp)
                .width(1.dp)
                .padding(end = 8.dp)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(36.dp, 20.dp),
            colors = switchColors
        )
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(4.dp))
    Divider()
}

@Composable
fun SettingsEntry(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}