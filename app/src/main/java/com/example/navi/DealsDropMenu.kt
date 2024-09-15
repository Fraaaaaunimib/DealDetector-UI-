package com.example.navi

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.automirrored.filled.ViewList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DettagliBottomSheet(
    preferencesManager: PreferencesManager,
    showDettagliSheet: Boolean,
    onShowDettagliSheetChange: (Boolean) -> Unit,
    viewOption: String,
    onViewOptionChange: (String, Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val bottomSheetState = remember {
        SheetState(
            skipPartiallyExpanded = true,
            initialValue = if (showDettagliSheet) SheetValue.Expanded else SheetValue.Hidden,
            density = density
        )
    }

    LaunchedEffect(showDettagliSheet) {
        if (showDettagliSheet) {
            scope.launch { bottomSheetState.expand() }
        } else {
            scope.launch { bottomSheetState.hide() }
        }
    }

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { onShowDettagliSheetChange(false) },
        modifier = Modifier.graphicsLayer {
            translationY = if (bottomSheetState.isVisible) 0f else 1000f
        }
    ) {
        AnimatedVisibility(
            visible = showDettagliSheet,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.offers_per_row),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                var sliderValue by remember { mutableFloatStateOf(preferencesManager.offersPerRow.toFloat()) }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (sliderValue > 1) {
                                sliderValue -= 1
                                onViewOptionChange(viewOption, sliderValue.toInt())
                                preferencesManager.offersPerRow = sliderValue.toInt()
                            }
                        },
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ViewList,
                            contentDescription = stringResource(id = R.string.list)
                        )
                    }
                    Text(
                        text = if (sliderValue.toInt() == 1) stringResource(id = R.string.list_view) else
                            stringResource(id = R.string.offers_per_row_count, sliderValue.toInt()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = {
                            if (sliderValue < 4) {
                                sliderValue += 1
                                onViewOptionChange(viewOption, sliderValue.toInt())
                                preferencesManager.offersPerRow = sliderValue.toInt()
                            }
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GridView,
                            contentDescription = stringResource(id = R.string.grid)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdinaBottomSheet(
    showOrdinaSheet: Boolean,
    onShowOrdinaSheetChange: (Boolean) -> Unit,
    onSortOptionChange: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = remember {
        SheetState(
            skipPartiallyExpanded = true,
            initialValue = if (showOrdinaSheet) SheetValue.Expanded else SheetValue.Hidden
        )
    }

    LaunchedEffect(showOrdinaSheet) {
        if (showOrdinaSheet) {
            scope.launch { bottomSheetState.expand() }
        } else {
            scope.launch { bottomSheetState.hide() }
        }
    }

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { onShowOrdinaSheetChange(false) },
        modifier = Modifier.graphicsLayer {
            translationY = if (bottomSheetState.isVisible) 0f else 1000f
        }
    ) {
        AnimatedVisibility(
            visible = showOrdinaSheet,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.sort_by),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { onSortOptionChange("Option 1") }) {
                    Text(stringResource(id = R.string.option_1))
                }
                TextButton(onClick = { onSortOptionChange("Option 2") }) {
                    Text(stringResource(id = R.string.option_2))
                }
            }
        }
    }
}