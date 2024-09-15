package com.example.navi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
//icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.res.stringResource

@Composable
fun VerticalNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    filledIcons: List<ImageVector>,
    outlinedIcons: List<ImageVector>,
    preferencesManager: PreferencesManager,
    viewOption: String,
    onViewOptionChange: (String, Int) -> Unit,
    onSortOptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDettagliMenu by remember { mutableStateOf(false) }
    var showOrdinaMenu by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(80.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = scrollState
        ) {
            items(tabs.size) { index ->
                NavigationRailItem(
                    selected = selectedTab == index,
                    onClick = {
                        onTabSelected(index)
                    },
                    icon = {
                        val icon = if (selectedTab == index) filledIcons[index] else outlinedIcons[index]
                        Icon(imageVector = icon, contentDescription = tabs[index])
                    },
                    label = null
                )
            }
        }

        if (selectedTab == 1) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { showDettagliMenu = !showDettagliMenu },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = stringResource(R.string.details))
                    }
                    if (showDettagliMenu) {
                        DettagliBottomSheet(
                            preferencesManager = preferencesManager,
                            showDettagliSheet = showDettagliMenu,
                            onShowDettagliSheetChange = { showDettagliMenu = it },
                            viewOption = viewOption,
                            onViewOptionChange = onViewOptionChange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { showOrdinaMenu = !showOrdinaMenu },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.SortByAlpha, contentDescription = stringResource(R.string.sort))
                    }
                    if (showOrdinaMenu) {
                        OrdinaBottomSheet(
                            showOrdinaSheet = showOrdinaMenu,
                            onShowOrdinaSheetChange = { showOrdinaMenu = it },
                            onSortOptionChange = onSortOptionChange
                        )
                    }
                }
            }
        }
    }
}