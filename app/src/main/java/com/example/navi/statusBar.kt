// com/example/navi/statusBar.kt
package com.example.navi

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.res.stringResource

@SuppressLint("ComposableNaming")
@Composable
fun updateStatusBarTextColor(window: Window, selectedTheme: String) {
    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
    insetsController.isAppearanceLightStatusBars = selectedTheme == stringResource(R.string.light_theme) || (selectedTheme == DEFAULT_THEME && (window.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO)
}