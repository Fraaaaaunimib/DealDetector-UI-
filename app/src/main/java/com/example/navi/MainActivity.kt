package com.example.navi

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navi.ui.theme.Test3Theme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import java.util.concurrent.Executor
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.PagerState
import android.app.Application
import com.google.firebase.FirebaseApp

//icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.coroutineScope

//tabs names
val tabs = listOf("Categorie", "Le mie offerte", "Ricerca", "Impostazioni")

//icons for each tab
val filledIcons = listOf(
    Icons.Filled.ShoppingCart,
    Icons.Filled.Home,
    Icons.Filled.Search,
    Icons.Filled.Settings
)

val outlinedIcons = listOf(
    Icons.Outlined.ShoppingCart,
    Icons.Outlined.Home,
    Icons.Outlined.Search,
    Icons.Outlined.Settings
)

//default app theme
const val DEFAULT_THEME = "Predefinito di sistema"

/* File principale dell'app
Da non modificare lo stile dell'app pls
Vertical navigation bar: quando il telefono è in orizzontale, c'è una barra di nav speciale
Horizontal navigation bar: per quando il telefono è in portrait
Da fare: quando il telefono è in orizzontale, non dovresti poter scorrere tra le schede con le
gestures
* */
class MainActivity : FragmentActivity() {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor
    private lateinit var navController: NavHostController

    private var selectedTheme by mutableStateOf(DEFAULT_THEME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        preferencesManager = PreferencesManager(this)
        selectedTheme = preferencesManager.currentTheme
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                handleAuthenticationFailure()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                setupContent()
            }

            override fun onAuthenticationFailed() {
                handleAuthenticationFailure()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Usa il tuo volto, l'impronta o il PIN")
            .setSubtitle("Per usare questa funzionalità, è necessario autenticarsi")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        if (preferencesManager.requireIdentification) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            setupContent()
        }
    }

    override fun onResume() {
        super.onResume()
        if (preferencesManager.requireIdentification) {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    override fun onPause() {
        super.onPause()
        if (preferencesManager.requireIdentification) {
            biometricPrompt.cancelAuthentication()
        }
    }

    private fun setupContent() {
        Log.d("MainActivity", "Setting up content")
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            navController = rememberNavController()
            MainContent(
                navController = navController,
                selectedTheme = selectedTheme,
                onThemeChange = { newTheme ->
                    selectedTheme = newTheme
                    preferencesManager.currentTheme = newTheme
                    recreate()
                },
                defaultTab = 1
            )
        }
    }

    private fun handleAuthenticationFailure() {
        finish()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val container = findViewById<FrameLayout>(R.id.container)
        val rotateIn = AnimationUtils.loadAnimation(this, R.anim.rotate_in)
        val rotateOut = AnimationUtils.loadAnimation(this, R.anim.rotate_out)

        container.startAnimation(rotateOut)
        container.startAnimation(rotateIn)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainContent(
    navController: NavHostController,
    selectedTheme: String,
    onThemeChange: (String) -> Unit,
    defaultTab: Int
) {
    Test3Theme(selectedTheme) {
        val preferencesManager = PreferencesManager(LocalContext.current)
        val currentTheme = preferencesManager.currentTheme
        val viewOption = preferencesManager.viewOption
        val sortOption = preferencesManager.sortOption
        val showDettagliMenu = preferencesManager.showDettagliMenu
        val showOrdinaMenu = preferencesManager.showOrdinaMenu
        val offersPerRow = preferencesManager.offersPerRow
        val pagerState = rememberPagerState(initialPage = defaultTab)
        var selectedTab by rememberSaveable { mutableStateOf(defaultTab) }

        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val configuration = LocalConfiguration.current

        Scaffold(
            bottomBar = {
                if (currentRoute != "DealsActivity" && currentRoute != "authExplain" &&
                    configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    HorizontalNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        tabs = tabs,
                        filledIcons = filledIcons,
                        outlinedIcons = outlinedIcons,
                        pagerState = pagerState
                    )
                }
            }
        ) {
            MainScreen(
                navController = navController,
                preferencesManager = preferencesManager,
                currentTheme = currentTheme,
                onThemeChange = onThemeChange,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                tabs = tabs,
                filledIcons = filledIcons,
                outlinedIcons = outlinedIcons,
                onLogout = { /* Handle logout */ },
                viewOption = viewOption,
                onViewOptionChange = { option, rows ->
                    preferencesManager.viewOption = option
                    preferencesManager.offersPerRow = rows
                },
                sortOption = sortOption,
                onSortOptionChange = { preferencesManager.sortOption = it },
                showDettagliMenu = showDettagliMenu,
                onShowDettagliMenuChange = { preferencesManager.showDettagliMenu = it },
                showOrdinaMenu = showOrdinaMenu,
                onShowOrdinaMenuChange = { preferencesManager.showOrdinaMenu = it },
                offersPerRow = offersPerRow,
                pagerState = pagerState
            )
        }
    }
}

@Composable
fun SearchScreen(navController: NavHostController) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .focusRequester(focusRequester)
    ) {
        Text("Search Screen Content")
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    preferencesManager: PreferencesManager,
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    filledIcons: List<ImageVector>,
    outlinedIcons: List<ImageVector>,
    onLogout: () -> Unit,
    viewOption: String,
    onViewOptionChange: (String, Int) -> Unit,
    sortOption: String,
    onSortOptionChange: (String) -> Unit,
    showDettagliMenu: Boolean,
    onShowDettagliMenuChange: (Boolean) -> Unit,
    showOrdinaMenu: Boolean,
    onShowOrdinaMenuChange: (Boolean) -> Unit,
    offersPerRow: Int,
    pagerState: com.google.accompanist.pager.PagerState
) {
    val configuration = LocalConfiguration.current
    val coroutineScope = rememberCoroutineScope()
    var isDealDetailVisible by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        onTabSelected(pagerState.currentPage)
    }

    Scaffold(
        bottomBar = {
            if (!isDealDetailVisible && configuration.orientation !=
                Configuration.ORIENTATION_LANDSCAPE) {
                HorizontalNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { page ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    },
                    tabs = tabs,
                    filledIcons = filledIcons,
                    outlinedIcons = outlinedIcons,
                    pagerState = pagerState
                )
            }
        },
        content = { innerPadding ->
            Row(modifier = Modifier.padding(innerPadding)) {
                if (!isDealDetailVisible && configuration.orientation ==
                    Configuration.ORIENTATION_LANDSCAPE) {
                    VerticalNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { page ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page)
                            }
                        },
                        tabs = tabs,
                        filledIcons = filledIcons,
                        outlinedIcons = outlinedIcons,
                        preferencesManager = preferencesManager,
                        viewOption = viewOption,
                        onViewOptionChange = onViewOptionChange,
                        sortOption = sortOption,
                        onSortOptionChange = onSortOptionChange
                    )
                }
                HorizontalPager(
                    count = tabs.size,
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    when (page) {
                        0 -> CategoriesScreen()
                        1 -> DealsScreen(
                            viewOption = viewOption,
                            onViewOptionChange = onViewOptionChange,
                            sortOption = sortOption,
                            onSortOptionChange = onSortOptionChange,
                            showDettagliMenu = showDettagliMenu,
                            onShowDettagliMenuChange = onShowDettagliMenuChange,
                            showOrdinaMenu = showOrdinaMenu,
                            onShowOrdinaMenuChange = onShowOrdinaMenuChange,
                            preferencesManager = preferencesManager
                        )
                        2 -> SearchScreen(navController)
                        3 -> SettingsScreen(preferencesManager, onLogout, onThemeChange, navController)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HorizontalNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    filledIcons: List<ImageVector>,
    outlinedIcons: List<ImageVector>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                    onTabSelected(index)
                },
                label = { Text(text = tab, fontSize = 12.sp, fontWeight = if (selectedTab == index)
                    FontWeight.Bold else FontWeight.Normal) },
                icon = {
                    val icon = if (selectedTab == index) filledIcons[index] else outlinedIcons[index]
                    Icon(imageVector = icon, contentDescription = tab)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

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
    sortOption: String,
    onSortOptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDettagliMenu by remember { mutableStateOf(false) }
    var showOrdinaMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(80.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        // Navigation items
        NavigationRail(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.weight(1f)
        ) {
            tabs.forEachIndexed { index, tab ->
                NavigationRailItem(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    icon = {
                        val icon = if (selectedTab == index) filledIcons[index] else
                            outlinedIcons[index]
                        Icon(imageVector = icon, contentDescription = tab)
                    },
                    label = null
                )
            }
        }

        if (selectedTab == 1) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .width(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium)
                    .align(Alignment.CenterHorizontally)
            ) {
                IconButton(
                    onClick = {
                        showDettagliMenu = !showDettagliMenu
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = "Dettagli")
                }
                if (showDettagliMenu) {
                    DettagliDropdownMenu(
                        preferencesManager = preferencesManager,
                        showDettagliMenu = showDettagliMenu,
                        onShowDettagliMenuChange = { showDettagliMenu = it },
                        viewOption = viewOption,
                        onViewOptionChange = onViewOptionChange
                    )
                }
            }

            // Ordina Chip
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .width(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium)
                    .align(Alignment.CenterHorizontally)
            ) {
                IconButton(
                    onClick = {
                        showOrdinaMenu = !showOrdinaMenu
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = Icons.Filled.SortByAlpha, contentDescription = "Ordina")
                }
                if (showOrdinaMenu) {
                    OrdinaDropdownMenu(
                        preferencesManager = preferencesManager,
                        showOrdinaMenu = showOrdinaMenu,
                        onShowOrdinaMenuChange = { showOrdinaMenu = it },
                        sortOption = sortOption,
                        onSortOptionChange = onSortOptionChange
                    )
                }
            }
        }
    }
}