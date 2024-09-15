package com.example.navi

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navi.ui.theme.Test3Theme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

fun getDefaultWebClientId(): String {
    return System.getenv("DEFAULT_WEB_CLIENT_ID") ?: "YOUR_DEFAULT_WEB_CLIENT_ID"
}

class LoginActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)
        auth = FirebaseAuth.getInstance()
        val appName = getString(R.string.app_name)
        val currentTheme = preferencesManager.currentTheme

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getDefaultWebClientId())
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

        setContent {
            Test3Theme(theme = currentTheme) {
                val isDarkTheme = isSystemInDarkTheme()
                val backgroundColor = MaterialTheme.colorScheme.background

                SideEffect {
                    val window = window
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                    window.statusBarColor = backgroundColor.toArgb()
                    insetsController.isAppearanceLightStatusBars = !isDarkTheme
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController,
                        startDestination = "login", modifier = Modifier.fillMaxSize()) {
                        composable("login") {
                            LoginScreen(appName = appName, onLoginSuccess = {
                                navController.navigate("main_menu")
                            }, onRegisterClick = {
                                navController.navigate("register")
                            }, signInWithGoogle = { signInWithGoogle() })
                        }
                        composable("register") {
                            RegistrationScreen(onBack = {
                                navController.popBackStack()
                            }, auth = auth)
                        }
                    }
                }
            }
        }
    }

    private fun onLoginSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this@LoginActivity) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            checkIfUserIsRegistered(user.email) { isRegistered ->
                                if (isRegistered) {
                                    onLoginSuccess()
                                } else {
                                    setContent {
                                        showRegistrationDialog(user.email)
                                    }
                                }
                            }
                        }
                    } else {
                        showError(getString(R.string.error_message, task.exception?.message))
                    }
                }
        } catch (e: ApiException) {
            showError(getString(R.string.error_message, e.message))
        }
    }

    private fun checkIfUserIsRegistered(email: String?, callback: (Boolean) -> Unit) {
        callback(false) //placeholder
    }

    private fun showError(message: String) {
        setContent {
            ErrorDialog(message)
        }
    }

    @Composable
    fun ErrorDialog(message: String) {
        var showDialog by remember { mutableStateOf(true) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.error)) },
                text = { Text(message) },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.ok))
                    }
                }
            )
        }
    }

    @Composable
    fun showRegistrationDialog(email: String?) {
        var showDialog by remember { mutableStateOf(true) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.registration)) },
                text = { Text(stringResource(R.string.registration_prompt, email ?: "")) },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text(stringResource(R.string.yes))
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.no))
                    }
                }
            )
        }
    }
}

@Composable
fun LoginScreen(appName: String, onLoginSuccess: () -> Unit,
                onRegisterClick: () -> Unit, signInWithGoogle: () -> Unit) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(
            modifier = Modifier.fillMaxWidth(),
            startColor = primaryColor.copy(alpha = 0.8f),
            endColor = backgroundColor,
            isHorizontal = isLandscape,
            height = if (!isLandscape) 200.dp else Dp.Unspecified
        )
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.welcome),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.welcome_to, appName),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LoginForm(
                        username = username,
                        onUsernameChange = { username = it },
                        email = email,
                        onEmailChange = { email = it },
                        password = password,
                        onPasswordChange = { password = it },
                        passwordVisible = passwordVisible,
                        onPasswordVisibleChange = { passwordVisible = it },
                        onLoginSuccess = onLoginSuccess,
                        onRegisterClick = onRegisterClick
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { signInWithGoogle() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(R.string.google_logo),
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.continue_with_google),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.welcome),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.welcome_to, appName),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
                Spacer(modifier = Modifier.height(32.dp))
                LoginForm(
                    username = username,
                    onUsernameChange = { username = it },
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    passwordVisible = passwordVisible,
                    onPasswordVisibleChange = { passwordVisible = it },
                    onLoginSuccess = onLoginSuccess,
                    onRegisterClick = onRegisterClick
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { signInWithGoogle() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = stringResource(R.string.google_logo),
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.continue_with_google),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun LoginForm(
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        label = { Text(stringResource(R.string.username)) },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(stringResource(R.string.email)) },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(stringResource(R.string.password)) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else
                        Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else
                        stringResource(R.string.show_password)
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else
            PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
        keyboardActions = KeyboardActions.Default,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = { onLoginSuccess() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.login))
    }
    Spacer(modifier = Modifier.height(32.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.no_account),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = { onRegisterClick() }) {
            Text(stringResource(R.string.register))
        }
        Button(onClick = { /* Handle use without account */ }) {
            Text(stringResource(R.string.use_without_account))
        }
    }
}

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    startColor: Color,
    endColor: Color,
    isHorizontal: Boolean = false,
    height: Dp = Dp.Unspecified
) {
    Box(
        modifier = modifier
            .height(height)
            .background(
                brush = if (isHorizontal) {
                    Brush.horizontalGradient(listOf(startColor, endColor))
                } else {
                    Brush.verticalGradient(listOf(startColor, endColor))
                }
            )
    )
}