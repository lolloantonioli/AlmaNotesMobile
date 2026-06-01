package com.example.almanotesmobile.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.R
import com.example.almanotesmobile.ui.permissions.PermissionDeniedAlert
import com.example.almanotesmobile.ui.permissions.PermissionPermanentlyDeniedSnackbar
import com.example.almanotesmobile.ui.viewmodel.AuthViewModel
import com.example.almanotesmobile.utils.findActivity

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBiometricLogin: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBiometricPermissionAlert by remember { mutableStateOf(false) }
    var showBiometricSettingsSnackbar by remember { mutableStateOf(false) }

    val biometricEnabled by viewModel.biometricEnabled.collectAsStateWithLifecycle()
    val biometricConsentAsked by viewModel.biometricConsentAsked.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val biometricSnackbarHostState = remember { SnackbarHostState() }

    val almaRed = Color(0xFFBB2E29)
    val cardBg = Color(0xFFFAFAFA)

    val biometricAuthenticators = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else {
            BIOMETRIC_STRONG
        }
    }
    val biometricStatus = remember(context, biometricAuthenticators) {
        BiometricManager.from(context).canAuthenticate(biometricAuthenticators)
    }
    val biometricAvailable = biometricStatus == BiometricManager.BIOMETRIC_SUCCESS

    fun launchBiometric(
        title: String,
        subtitle: String,
        onAuthenticated: () -> Unit,
        onCanceled: () -> Unit = {},
        onFailed: () -> Unit = { errorMessage = "Autenticazione fallita" }
    ) {
        val activity = context.findActivity() as? FragmentActivity
        if (activity == null) {
            errorMessage = "Errore: impossibile avviare la biometria"
            onCanceled()
            return
        }

        val executor = ContextCompat.getMainExecutor(context)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onAuthenticated()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        onCanceled()
                    } else {
                        errorMessage = "Errore biometrico: $errString"
                        onCanceled()
                    }
                }

                override fun onAuthenticationFailed() {
                    onFailed()
                }
            }
        )

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptInfoBuilder.setAllowedAuthenticators(biometricAuthenticators)
        } else {
            promptInfoBuilder.setNegativeButtonText("Annulla")
        }

        prompt.authenticate(promptInfoBuilder.build())
    }

    fun completeLoginAfterBiometricConsent(enabled: Boolean) {
        viewModel.setBiometricEnabled(enabled)
        onLoginSuccess()
    }

    fun requestBiometricConsent() {
        if (biometricAvailable) {
            showBiometricPermissionAlert = true
        } else {
            showBiometricSettingsSnackbar = true
        }
    }

    fun openBiometricSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun handlePasswordLoginSuccess() {
        if (biometricAvailable && !biometricConsentAsked) {
            requestBiometricConsent()
        } else {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.sfondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Card(
            modifier = Modifier.align(Alignment.Center).padding(32.dp).fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Accedi", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = almaRed)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    placeholder = { Text("Email address") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color.DarkGray,
                        unfocusedPlaceholderColor = Color.DarkGray,
                        cursorColor = almaRed
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    placeholder = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color.DarkGray,
                        unfocusedPlaceholderColor = Color.DarkGray,
                        cursorColor = almaRed
                    )
                )

                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToRegister) {
                    Text("Non sei registrato? Registrati qui", color = Color.Black, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.login(email, password) { success ->
                            if (success) handlePasswordLoginSuccess() else errorMessage = "Email o password errati"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = almaRed),
                    modifier = Modifier.fillMaxWidth().height(45.dp)
                ) {
                    Text("Accedi", color = Color.White)
                }

                if (biometricEnabled) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (biometricAvailable) {
                                launchBiometric(
                                    title = "Accedi ad AlmaNotes",
                                    subtitle = "Usa l'impronta o il PIN del dispositivo",
                                    onAuthenticated = {
                                        viewModel.loginWithBiometric { success ->
                                            if (success) onLoginSuccess() else errorMessage = "Utente non trovato"
                                        }
                                    }
                                )
                            } else {
                                showBiometricSettingsSnackbar = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = almaRed.copy(alpha = 0.8f)),
                        modifier = Modifier.fillMaxWidth().height(45.dp)
                    ) {
                        Text("Accedi con biometria", color = Color.White)
                    }
                }
            }
        }

        SnackbarHost(
            hostState = biometricSnackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        PermissionDeniedAlert(
            show = showBiometricPermissionAlert,
            title = "Permesso biometrico",
            message = "Conferma con biometria o credenziali del dispositivo per abilitare l'accesso rapido ad AlmaNotes.",
            actionLabel = "Abilita",
            dismissLabel = "Non ora",
            hideAfterAction = false,
            onAction = {
                showBiometricPermissionAlert = false
                launchBiometric(
                    title = "Abilita accesso biometrico",
                    subtitle = "Conferma con impronta, volto o PIN del dispositivo",
                    onAuthenticated = { completeLoginAfterBiometricConsent(true) },
                    onCanceled = { completeLoginAfterBiometricConsent(false) }
                )
            },
            onHide = {
                showBiometricPermissionAlert = false
                completeLoginAfterBiometricConsent(false)
            }
        )

        PermissionPermanentlyDeniedSnackbar(
            snackbarHostState = biometricSnackbarHostState,
            show = showBiometricSettingsSnackbar,
            message = "Configura biometria o blocco schermo per usare l'accesso rapido.",
            onAction = ::openBiometricSettings,
            onHide = { showBiometricSettingsSnackbar = false }
        )
    }
}