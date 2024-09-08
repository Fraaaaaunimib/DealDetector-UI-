package com.example.navi

import android.content.Intent
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
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity

/* Le mie offerte
Da fare: quando il telefono è in orizzontale e cambio il numero di offerte, la griglia non si
aggiorna
* */
@Composable
fun DealsScreen(
    viewOption: String,
    onViewOptionChange: (String, Int) -> Unit,
    sortOption: String,
    onSortOptionChange: (String) -> Unit,
    showDettagliMenu: Boolean,
    onShowDettagliMenuChange: (Boolean) -> Unit,
    showOrdinaMenu: Boolean,
    onShowOrdinaMenuChange: (Boolean) -> Unit,
    preferencesManager: PreferencesManager
) {
    var showDettagliMenuState by remember { mutableStateOf(false) }
    var showOrdinaMenuState by remember { mutableStateOf(false) }
    var offersPerRow by remember { mutableStateOf(preferencesManager.offersPerRow) }
    var selectedDeal by remember { mutableStateOf<Deal?>(null) }
    var isTitleVisible by remember { mutableStateOf(true) }
    var isFloatingBarVisible by remember { mutableStateOf(true) }


    //Esempio di offerte, per vedere come stanno e come si comporta
    val deals = listOf(
        Deal(
            title = "Deal 1",
            subtitle = "Subtitle 1",
            detail = "Detail 1",
            supermarket = "Supermarket 1444444444444444444444444444444444444444",
            category = "Category 1",
            priceKilo = 10.0,
            priceDeal = 8.0,
            priceNormal = 12.0,
            dealPerc = 20.0,
            dealType = "Type 1"
        )
    )

    val gridState = rememberLazyGridState()

    LaunchedEffect(gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset) {
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
                        text = "Le mie offerte",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Scopri le offerte dedicate a te",
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
                    sortOption = sortOption,
                    onSortOptionChange = onSortOptionChange,
                    modifier = Modifier.padding(bottom = 0.dp)
                )
            }
        }
    }
}

//Barra sotto con Dettagli e Ordina - si possono aggiungere altri chip
@Composable
fun FloatingBar(
    showDettagliMenu: Boolean,
    onShowDettagliMenuChange: (Boolean) -> Unit,
    showOrdinaMenu: Boolean,
    onShowOrdinaMenuChange: (Boolean) -> Unit,
    preferencesManager: PreferencesManager,
    viewOption: String,
    onViewOptionChange: (String, Int) -> Unit,
    sortOption: String,
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
                    Row {
                        Text("Dettagli")
                        Icon(
                            imageVector = Icons.Filled.ArrowDropUp,
                            contentDescription = "Open Dettagli Menu"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            AssistChip(
                onClick = { onShowOrdinaMenuChange(!showOrdinaMenu) },
                label = {
                    Row {
                        Text("Ordina")
                        Icon(
                            imageVector = Icons.Filled.ArrowDropUp,
                            contentDescription = "Open Ordina Menu"
                        )
                    }
                }
            )
        }

        if (showDettagliMenu) {
            DettagliDropdownMenu(
                preferencesManager = preferencesManager,
                showDettagliMenu = showDettagliMenu,
                onShowDettagliMenuChange = onShowDettagliMenuChange,
                viewOption = viewOption,
                onViewOptionChange = onViewOptionChange
            )
        }

        if (showOrdinaMenu) {
            OrdinaDropdownMenu(
                preferencesManager = preferencesManager,
                showOrdinaMenu = showOrdinaMenu,
                onShowOrdinaMenuChange = onShowOrdinaMenuChange,
                sortOption = sortOption,
                onSortOptionChange = onSortOptionChange
            )
        }
    }
}