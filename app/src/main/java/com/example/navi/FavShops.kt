package com.example.navi

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.navi.ui.theme.Test3Theme

data class Shop(val name: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavShopsScreen(onBack: () -> Unit, preferencesManager: PreferencesManager) {
    val shops = listOf(
        Shop("Esselunga", Icons.Filled.Store),
        Shop("Lidl", Icons.Filled.Store),
        Shop("Aldi", Icons.Filled.Store),
        Shop("Carrefour", Icons.Filled.Store)
    )
    var checkedState by remember { mutableStateOf(preferencesManager.favoriteShops) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Supermercati preferiti",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                text = "Le offerte dei supermercati che hai selezionato in questa lista " +
                        "verranno mostrate nella pagina \" Le mie offerte\". " +
                        "Per vedere tutte le offerte disponibili, vai in Categorie.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(shops) { shop ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                val isChecked = !checkedState.contains(shop.name)
                                checkedState = if (isChecked) {
                                    checkedState + shop.name
                                } else {
                                    checkedState - shop.name
                                }
                                preferencesManager.favoriteShops = checkedState
                            }
                            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    ) {
                        Checkbox(
                            checked = checkedState.contains(shop.name),
                            onCheckedChange = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = shop.icon,
                            contentDescription = shop.name,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = shop.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { /* Handle settings click */ }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    }
                }
            }
        }
    }
}

class FavShopsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferencesManager = PreferencesManager(this)
        val currentTheme = preferencesManager.currentTheme
        setContent {
            Test3Theme(theme = currentTheme) {
                FavShopsScreen(onBack = { finish() }, preferencesManager = preferencesManager)
            }
        }
    }
}