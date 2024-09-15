// ThemeDialog.kt
package com.example.navi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ThemeDialog(
    onDismiss: () -> Unit,
    preferencesManager: PreferencesManager,
    onThemeChange: (String) -> Unit
) {
    val themes = listOf(
        stringResource(R.string.default_theme),
        stringResource(R.string.light_theme),
        stringResource(R.string.dark_theme)
    )
    val currentTheme = preferencesManager.currentTheme

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_theme)) },
        text = {
            Column {
                themes.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onThemeChange(theme)
                                preferencesManager.currentTheme = theme
                                onDismiss()
                            }
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = theme == currentTheme,
                            onClick = {
                                onThemeChange(theme)
                                preferencesManager.currentTheme = theme
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = theme)
                    }
                }
            }
        },
        confirmButton = {}
    )
}