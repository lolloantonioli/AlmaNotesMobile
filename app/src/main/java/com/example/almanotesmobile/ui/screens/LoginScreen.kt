package com.example.almanotesmobile.ui.screens

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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
import com.example.almanotesmobile.ui.viewmodel.AuthViewModel

// Funzione helper per trovare la FragmentActivity nel contesto di Compose
fun Context.findFragmentActivity(): FragmentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBiometricLogin: () -> Unit,
    viewModel: AuthViewModel
) {
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var errorMessage   by remember { mutableStateOf<String?>(null) }
    var showBiometricConsentDialog by remember { mutableStateOf(false) }

    val biometricEnabled by viewModel.biometricEnabled.collectAsStateWithLifecycle()
    val biometricConsentAsked by viewModel.biometricConsentAsked.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val almaRed = Color(0xFFBB2E29)
    val cardBg  = Color(0xFFFAFAFA)

    val biometricAvailable = remember(context) {
        val bm = BiometricManager.from(context)
        bm.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun launchBiometric(
        title: String,
        subtitle: String,
        onAuthenticated: () -> Unit,
        onCanceled: () -> Unit = {},
        onFailed: () -> Unit = { errorMessage = "Autenticazione fallita" }
    ) {
        val activity = context.findFragmentActivity()
        if (activity == null) {
            errorMessage = "Errore: Impossibile avviare la biometria"
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
                    }
                    else {
                        errorMessage = "Errore biometrico: $errString"
                        onCanceled()
                    }
                }

                override fun onAuthenticationFailed() {
                    onFailed()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        prompt.authenticate(promptInfo)
    }

    fun completeLoginAfterBiometricConsent(enabled: Boolean) {
        viewModel.setBiometricEnabled(enabled)
        onLoginSuccess()
    }

    fun requestBiometricConsent() {
        launchBiometric(
            title = "Abilita accesso biometrico",
            subtitle = "Conferma con impronta, volto o PIN del dispositivo",
            onAuthenticated = { completeLoginAfterBiometricConsent(true) },
            onCanceled = { completeLoginAfterBiometricConsent(false) }
        )
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
                            if (success) handlePasswordLoginSuccess()
                            else errorMessage = "Email o password errati"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = almaRed),
                    modifier = Modifier.fillMaxWidth().height(45.dp)
                ) {
                    Text("Accedi", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (biometricAvailable && biometricEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            launchBiometric(
                                title = "Accedi ad AlmaNotes",
                                subtitle = "Usa l'impronta o il PIN del dispositivo",
                                onAuthenticated = {
                                    viewModel.loginWithBiometric { success ->
                                        if (success) onLoginSuccess()
                                        else errorMessage = "Utente non trovato"
                                    }
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = almaRed.copy(alpha = 0.8f)),
                        modifier = Modifier.fillMaxWidth().height(45.dp)
                    ) {
                        Text("Accedi con biometria", color = Color.White)
                    }
                }
            }
        }
    }
}