// ChangelogActivity.kt
package com.example.navi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.navi.ui.theme.Test3Theme

//Changelog per le varie versioni - non preoccuparti dei vari codename particolari :D
//forse sarà meglio rimuoverlo alla fine...
class ChangelogActivity : ComponentActivity() {
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
                    val window = (context as ComponentActivity).window
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                    window.statusBarColor = backgroundColor.toArgb()
                    insetsController.isAppearanceLightStatusBars = !isDarkTheme
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    ChangelogScreen(onBackClick = { finish() })
                }
            }
        }
    }
}

@Composable
fun ChangelogItem(
    categoryTitle: String,
    itemTitle: String,
    itemSubtitle: String,
    itemText: String
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = categoryTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            text = itemTitle,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = itemSubtitle,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = itemText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogScreen(onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Changelog") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text(
                text = "Developer changelog",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(getChangelogItems()) { item ->
                    ChangelogItem(
                        categoryTitle = item.categoryTitle,
                        itemTitle = item.itemTitle,
                        itemSubtitle = item.itemSubtitle,
                        itemText = item.itemText
                    )
                }
            }
        }
    }
}

data class ChangelogItemData(
    val categoryTitle: String,
    val itemTitle: String,
    val itemSubtitle: String,
    val itemText: String
)

fun getChangelogItems(): List<ChangelogItemData> {
    return listOf(
        ChangelogItemData(
            categoryTitle = "6th September 2024",
            itemTitle = "Version Phantom",
            itemSubtitle = "Build 0.0.17.220",
            itemText = "Tested the app on foldables/tablets. Now when opening a deal, it doesn't show the navigation bar of the app, and the text inside wraps automatically if it's too long to fit. Fixed also a thing in the favorites shops section. Made a few changes to the login screen, and introduced the Register screen."
        ),
        ChangelogItemData(
            categoryTitle = "3rd September 2024",
            itemTitle = "Version Espresso",
            itemSubtitle = "Build 0.0.16.215",
            itemText = "Various improvements to the deal detail page. Improved also the floating bar for deals."
        ),
        ChangelogItemData(
            categoryTitle = "2nd September 2024",
            itemTitle = "Version Gourmet",
            itemSubtitle = "Build 0.0.15.205",
            itemText = "Fixed a few bugs regarding the horizontal navigation bar. Added a new deal detail screen, with a brand new layout too."
        ),
        ChangelogItemData(
            categoryTitle = "28th August 2024",
            itemTitle = "Version Spacejunk",
            itemSubtitle = "Build 0.0.14.180",
            itemText = "Fixed yet again the chips. Added a new text which tells in which view mode you are. Added your favourite supermarkets list. Now also there's at least a prototype of a detail screen for deals."
        ),
        ChangelogItemData(
            categoryTitle = "27th August 2024",
            itemTitle = "Version Soleanna",
            itemSubtitle = "Build 0.0.13.160",
            itemText = "Finally fixed the chips in horizontal. Added view options in the horizontal navigation bar. Now you can use gestures to navigate between pages, and it has better animations overall."
        ),
        ChangelogItemData(
            categoryTitle = "25-26th August 2024",
            itemTitle = "Version Shibuya",
            itemSubtitle = "Build 0.0.12.140",
            itemText = "New view and sorting options under the deals. Added predictive back (just like it is on Android 14 and 15). Changed some wording here and there to make it more user friendly. Added a new screen to explain the biometric authentication. Changed the navbar colour to be consistent with Material You. Now when switching to Search, it auto focuses to the text box. Now when undoing a biometric authentication, it doesn't just bring you back where you were but it cancels the operation."
        ),
        ChangelogItemData(
            categoryTitle = "24th August 2024",
            itemTitle = "Version Kokiri",
            itemSubtitle = "Build 0.0.11.125",
            itemText = "Settings are now remembered when you change orientation of the device, and when you close the app. Introduced this changelog screen. When changing rotation of the device, it now animates correctly. The titles now hide correctly when scrolling, letting you see everything, both in portrait and horizontal. Implemented biometrical authentication."
        ),
        ChangelogItemData(
            categoryTitle = "23rd August 2024",
            itemTitle = "Version Raw",
            itemSubtitle = "Build 0.0.1.100",
            itemText = "This release includes a new Settings page, new login screen, adapted horizontal navigation, new design for all pages (e.g. top bars for each category), animations for pages, better design for everything essentially."
        )
    )
}