package com.example.navi

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.navi.ui.theme.Test3Theme

class DealDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val deal = intent.getParcelableExtra<Deal>("deal")
            val context = LocalContext.current
            val preferencesManager = remember { PreferencesManager(context) }
            val currentTheme = preferencesManager.currentTheme
            Test3Theme(theme = currentTheme) {
                DealDetailScreen(
                    deal = deal!!,
                    preferencesManager = preferencesManager,
                    onBack = { finish() }
                )
            }
        }
    }
}