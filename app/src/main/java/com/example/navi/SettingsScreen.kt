// com/example/navi/SettingsScreen.kt
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

//icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.font.FontWeight
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider

val CATEGORY_SPACING = 16.dp

@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    onLogout: () -> Unit,
    onThemeChange: (String) -> Unit
) {
    val context = LocalContext.current
    val sharedViewModel: SharedViewModel =
        ViewModelProvider(context as FragmentActivity, SharedViewModelFactory(preferencesManager))[SharedViewModel::class.java]
    var showThemeDialog by remember { mutableStateOf(false) }
    val requireIdentification by sharedViewModel.requireIdentification.collectAsState()
    var currentTheme by rememberSaveable { mutableStateOf(preferencesManager.currentTheme) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item { ProfileSection() }
        item { SettingsCategory(title = stringResource(R.string.supermarkets_offers)) }
        item { SettingsEntry(icon = Icons.Default.Store, title = stringResource(R.string.favorite_shops),
            onClick = {
                val intent = Intent(context, FavShopsActivity::class.java)
                context.startActivity(intent)
            }
        ) }
        item { Spacer(modifier = Modifier.height(CATEGORY_SPACING)) }
        item { SettingsCategory(title = stringResource(R.string.app_settings)) }
        item {
            SettingsEntry(
                icon = Icons.Filled.Notifications,
                title = stringResource(R.string.notifications),
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
                title = stringResource(R.string.theme),
                subtitle = currentTheme,
                onClick = { showThemeDialog = true }
            )
        }
        item {
            SettingsEntry(
                icon = Icons.Default.Build,
                title = stringResource(R.string.changelog),
                onClick = {
                    val intent = Intent(context, ChangelogActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
        item { Spacer(modifier = Modifier.height(CATEGORY_SPACING)) }
        item { SettingsCategory(title = stringResource(R.string.user_account_security)) }
        item {
            SettingsEntryToggle(
                icon = Icons.Default.Security,
                title = stringResource(R.string.require_authentication),
                checked = requireIdentification,
                onCheckedChange = {
                    showBiometricPrompt(
                        activity = context,
                        onSuccess = {
                            sharedViewModel.setRequireIdentification(it)
                        },
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
                title = stringResource(R.string.password),
                onClick = {
                    showBiometricPrompt(
                        activity = context,
                        onSuccess = {},
                        onCancel = {}
                    )
                }
            )
        }
        item {
            SettingsEntry(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = stringResource(R.string.logout),
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
            title = { Text(stringResource(R.string.confirm_logout)) },
            text = { Text(stringResource(R.string.confirm_logout_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel))
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
            contentDescription = stringResource(R.string.profile_icon_desc),
            modifier = Modifier.size(70.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = stringResource(R.string.username),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.user_email),
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
                contentDescription = stringResource(R.string.edit_profile),
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
        HorizontalDivider(
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
   HorizontalDivider()
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