package com.example.navi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.navi.ui.theme.Test3Theme

//Search screen - to do
class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val preferencesManager = remember { PreferencesManager(context) }
            val currentTheme = preferencesManager.currentTheme

            Test3Theme(theme = currentTheme) {
                SearchScreen()
            }
        }
    }
}