package com.example.navi

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.navi.ui.theme.Test3Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Label
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import com.example.navi.CategoryItem
import com.example.navi.ImageCarousel

/* File per i dettagli delle offerte quando si clicca su un'offerta
Da fare: vedere se le info che ci sono vanno bene
Quando lo schermo è abbastanza largo, visualizzare le info in una griglia piuttosto che in una
lista (o almeno permettere all'utente di scegliere il tipo di visualizzazione)
IMPORTANTE: lasciare l'implementazione con "Intent" - in questo modo, non viene visualizzata
la navbar dell'app, ma viene visualizzata come una nuova pagina
* */
class DealsActivity : FragmentActivity() {
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
                    val window = (context as FragmentActivity).window
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                    window.statusBarColor = backgroundColor.toArgb()
                    insetsController.isAppearanceLightStatusBars = !isDarkTheme
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    DealsScreen(
                        viewOption = "grid",
                        onViewOptionChange = { _, _ -> },
                        sortOption = "price",
                        onSortOptionChange = {},
                        showDettagliMenu = false,
                        onShowDettagliMenuChange = {},
                        showOrdinaMenu = false,
                        onShowOrdinaMenuChange = {},
                        preferencesManager = preferencesManager
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DealDetailScreen(
    deal: Deal,
    preferencesManager: PreferencesManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme = preferencesManager.currentTheme
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
    var showHorizontalNavigationBar by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()
    var isTopBarVisible by remember { mutableStateOf(true) }

    val currencySymbol = java.util.Currency.getInstance(java.util.Locale.getDefault()).symbol
    val formattedPriceDeal = if (deal.priceDeal % 1.0 == 0.0) {
        "${deal.priceDeal.toInt()},- $currencySymbol"
    } else {
        "${deal.priceDeal.toString().replace(".", ",")}0 $currencySymbol"
    }

    val formattedDiscount = if (deal.dealPerc % 1.0 == 0.0) {
        "${deal.dealPerc.toInt()}%"
    } else {
        "${deal.dealPerc}%"
    }

    BackHandler {
        onBack()
    }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemScrollOffset }
            .collect { offset ->
                isTopBarVisible = offset <= 100 // Adjust the threshold as needed
            }
    }

    Test3Theme(theme = currentTheme) {
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Scaffold(
                topBar = {
                    AnimatedVisibility(
                        visible = isTopBarVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TopAppBar(
                            title = { Text(text = "") },
                            navigationIcon = {
                                IconButton(onClick = onBack) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }
            ) { paddingValues ->
                BoxWithConstraints {
                    orientation = if (maxWidth > maxHeight) {
                        Configuration.ORIENTATION_LANDSCAPE
                    } else {
                        Configuration.ORIENTATION_PORTRAIT
                    }

                    showHorizontalNavigationBar = orientation == Configuration.ORIENTATION_LANDSCAPE

                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = deal.title,
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                    Text(
                                        text = deal.subtitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.inversePrimary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    val categories = deal.category.split(",")
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        categories.forEach { category ->
                                            AssistChip(
                                                onClick = { /* Handle chip click */ },
                                                label = { Text(text = category) },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Label,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = AssistChipDefaults.assistChipColors(
                                                    containerColor = MaterialTheme.colorScheme.surface,
                                                    labelColor = MaterialTheme.colorScheme.onSurface,
                                                    leadingIconContentColor = MaterialTheme.colorScheme.onSurface
                                                ),
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = deal.detail,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(bottom = 24.dp)
                                    )
                                }
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier.padding(end = 16.dp)
                                ) {
                                    Text(
                                        text = formattedPriceDeal,
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Sconto: $formattedDiscount",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.inversePrimary
                                    )
                                }
                            }
                            ImageCarousel()
                            Spacer(modifier = Modifier.height(24.dp))
                            CategoryList(deal)
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Extra padding at the bottom
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageCarousel() {
    val images = listOf(
        R.drawable.surface,
        R.drawable.surface,
        R.drawable.surface
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images.size) { index ->
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp)) // Make images rounded rectangles
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(2.dp) // Add padding to create a border effect
                    .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(16.dp)) // Border color
            ) {
                Image(
                    painter = painterResource(id = images[index]),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)) // Clip image to rounded rectangle
                )
            }
        }
    }
}

data class CategoryItem(
    val name: String,
    val data: Any,
    val icon: ImageVector
)

@Composable
fun CategoryList(deal: Deal) {
    val categories = listOf(
        CategoryItem("Negozio", deal.supermarket, Icons.Default.ShoppingCart),
        CategoryItem("Prezzo per chilo", deal.priceKilo, Icons.Default.AttachMoney),
        CategoryItem("Prezzo normale", deal.priceNormal, Icons.Default.AttachMoney),
        CategoryItem("Tipo di offerta", deal.dealType, Icons.Default.Info)
    )

    val currencySymbol = java.util.Currency.getInstance(java.util.Locale.getDefault()).symbol

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.forEach { categoryItem ->
            val formattedData = if (categoryItem.name == "Prezzo per chilo" || categoryItem.name == "Prezzo normale") {
                if (categoryItem.data is Double) {
                    val dataString = categoryItem.data.toString()
                    if (dataString.endsWith(".0")) {
                        "${dataString.dropLast(2)},- $currencySymbol"
                    } else {
                        "${dataString.replace(".", ",")}0 $currencySymbol"
                    }
                } else {
                    "${categoryItem.data} $currencySymbol"
                }
            } else {
                categoryItem.data.toString()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryItem.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                if (formattedData.length > 8) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = categoryItem.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = formattedData,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Text(
                        text = categoryItem.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formattedData,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}