package com.example.navi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment

// Menu Dettagli e Ordina per lo schermo Le Mie Offerte
@Composable
fun DettagliDropdownMenu(
    preferencesManager: PreferencesManager,
    showDettagliMenu: Boolean,
    onShowDettagliMenuChange: (Boolean) -> Unit,
    viewOption: String,
    onViewOptionChange: (String, Int) -> Unit
) {
    var sliderValue by remember { mutableStateOf(preferencesManager.offersPerRow.toFloat()) }

    DropdownMenu(
        expanded = showDettagliMenu,
        onDismissRequest = { onShowDettagliMenuChange(false) },
        modifier = Modifier
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Offerte per riga",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = sliderValue,
                onValueChange = { newValue ->
                    sliderValue = newValue
                    onViewOptionChange(viewOption, newValue.toInt())
                    preferencesManager.offersPerRow = newValue.toInt()
                },
                valueRange = 1f..4f,
                steps = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ViewList,
                    contentDescription = "List View",
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = if (sliderValue.toInt() == 1) "Visualizzazione a lista" else
                        "${sliderValue.toInt()} offerte per riga",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Filled.GridView,
                    contentDescription = "Grid View",
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    }
}

@Composable
fun OrdinaDropdownMenu(
    preferencesManager: PreferencesManager,
    showOrdinaMenu: Boolean,
    onShowOrdinaMenuChange: (Boolean) -> Unit,
    sortOption: String,
    onSortOptionChange: (String) -> Unit
) {
    DropdownMenu(
        expanded = showOrdinaMenu,
        onDismissRequest = { onShowOrdinaMenuChange(false) },
        modifier = Modifier
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ordina per",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            //sorting options
            TextButton(onClick = { onSortOptionChange("Option 1") }) {
                Text("Option 1")
            }
            TextButton(onClick = { onSortOptionChange("Option 2") }) {
                Text("Option 2")
            }
        }
    }
}