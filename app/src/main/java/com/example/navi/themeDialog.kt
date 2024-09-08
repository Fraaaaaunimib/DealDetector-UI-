// ThemeDialog.kt
package com.example.navi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//Dialogo del tema in Impostazioni
@Composable
fun ThemeDialog(
    onDismiss: () -> Unit,
    preferencesManager: PreferencesManager,
    onThemeChange: (String) -> Unit
) {
    val themes = listOf("Predefinito di sistema", "Chiaro", "Scuro")
    val currentTheme = preferencesManager.currentTheme

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleziona tema") },
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