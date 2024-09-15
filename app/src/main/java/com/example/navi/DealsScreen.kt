package com.example.navi

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity

@Composable
fun DealsScreen(
    viewOption: String,
    onViewOptionChange: (String, Int) -> Unit,
    onSortOptionChange: (String) -> Unit,
    preferencesManager: PreferencesManager
) {
    var showDettagliMenuState by remember { mutableStateOf(false) }
    var showOrdinaMenuState by remember { mutableStateOf(false) }
    var offersPerRow by remember { mutableIntStateOf(preferencesManager.offersPerRow) }
    var selectedDeal by remember { mutableStateOf<Deal?>(null) }
    var isTitleVisible by remember { mutableStateOf(true) }
    var isFloatingBarVisible by remember { mutableStateOf(true) }

    val deals = listOf(
        Deal(
            title = "Deal 1",
            subtitle = "Subtitle 1",
            detail = "Detail 1",
            supermarket = "Supermarket 1",
            category = "Category 1",
            priceKilo = 10.0,
            priceDeal = 8.0,
            priceNormal = 12.0,
            dealPerc = 20.0,
            dealType = "Type 1"
        )
    )

    val gridState = rememberLazyGridState()

    LaunchedEffect(remember { derivedStateOf { gridState.firstVisibleItemIndex } },
        remember { derivedStateOf { gridState.firstVisibleItemScrollOffset } }) {
        val isScrollingDown = gridState.firstVisibleItemScrollOffset > 0
        val isAtBottom = gridState.firstVisibleItemIndex +
                gridState.layoutInfo.visibleItemsInfo.size >= gridState.layoutInfo.totalItemsCount
        val isAtTop = gridState.firstVisibleItemIndex == 0 &&
                gridState.firstVisibleItemScrollOffset == 0
        isTitleVisible = isAtTop || (!isScrollingDown && !isAtBottom)
        isFloatingBarVisible = !isScrollingDown || isAtTop
    }

    val context = LocalContext.current
    val window = (context as FragmentActivity).window
    val insetsController = WindowInsetsControllerCompat(window, window.decorView)

    LaunchedEffect(selectedDeal) {
        if (selectedDeal != null) {
        } else {
            insetsController.show(WindowInsetsCompat.Type.navigationBars())
        }
    }

    val configuration = LocalConfiguration.current
    DisposableEffect(configuration.orientation) {
        offersPerRow = preferencesManager.offersPerRow
        onDispose { }
    }

    LaunchedEffect(offersPerRow) {
        gridState.scrollToItem(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedVisibility(
            visible = selectedDeal == null,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.my_offers),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.discover_offers),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Box(modifier =
                Modifier.weight(1f).fillMaxSize().background(MaterialTheme.colorScheme.background))
                {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(offersPerRow),
                        state = gridState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(deals) { deal ->
                            DealCard(
                                deal = deal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedDeal = deal }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }

        if (selectedDeal != null) {
            BackHandler {
                selectedDeal = null
            }
            DealDetailScreen(
                deal = selectedDeal!!,
                preferencesManager = preferencesManager,
                onBack = { selectedDeal = null }
            )
        }

        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            AnimatedVisibility(
                visible = isFloatingBarVisible,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 0.dp)
            ) {
                FloatingBar(
                    showDettagliMenu = showDettagliMenuState,
                    onShowDettagliMenuChange = { showDettagliMenuState = it },
                    showOrdinaMenu = showOrdinaMenuState,
                    onShowOrdinaMenuChange = { showOrdinaMenuState = it },
                    preferencesManager = preferencesManager,
                    viewOption = viewOption,
                    onViewOptionChange = { option, offers ->
                        onViewOptionChange(option, offers)
                        offersPerRow = offers
                        preferencesManager.offersPerRow = offers
                    },
                    onSortOptionChange = onSortOptionChange,
                    modifier = Modifier.padding(bottom = 0.dp)
                )
            }
        }
    }
}

@Composable
fun FloatingBar(
    showDettagliMenu: Boolean,
    onShowDettagliMenuChange: (Boolean) -> Unit,
    showOrdinaMenu: Boolean,
    onShowOrdinaMenuChange: (Boolean) -> Unit,
    preferencesManager: PreferencesManager,
    viewOption: String,
    onViewOptionChange: (String, Int) -> Unit,
    onSortOptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            AssistChip(
                onClick = { onShowDettagliMenuChange(!showDettagliMenu) },
                label = {
                    Row(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(stringResource(id = R.string.details))
                        Icon(
                            imageVector = Icons.Filled.ArrowDropUp,
                            contentDescription = stringResource(id = R.string.open_details_menu)
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            AssistChip(
                onClick = { onShowOrdinaMenuChange(!showOrdinaMenu) },
                label = {
                    Row(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(stringResource(id = R.string.sort))
                        Icon(
                            imageVector = Icons.Filled.ArrowDropUp,
                            contentDescription = stringResource(id = R.string.open_sort_menu)
                        )
                    }
                }
            )
        }

        if (showDettagliMenu) {
            DettagliBottomSheet(
                preferencesManager = preferencesManager,
                showDettagliSheet = showDettagliMenu,
                onShowDettagliSheetChange = onShowDettagliMenuChange,
                viewOption = viewOption,
                onViewOptionChange = onViewOptionChange
            )
        }

        if (showOrdinaMenu) {
            OrdinaBottomSheet(
                showOrdinaSheet = showOrdinaMenu,
                onShowOrdinaSheetChange = onShowOrdinaMenuChange,
                onSortOptionChange = onSortOptionChange
            )
        }
    }
}