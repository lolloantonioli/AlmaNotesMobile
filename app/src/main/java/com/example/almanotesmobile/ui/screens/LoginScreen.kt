package com.example.almanotesmobile.ui.screens

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.R

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

    val biometricEnabled by viewModel.biometricEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val almaRed = Color(0xFFBB2E29)
    val cardBg  = Color(0xFFFAFAFA)

    val biometricAvailable = remember(context) {
        val bm = BiometricManager.from(context)
        bm.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun launchBiometric() {
        val activity = context.findFragmentActivity()
        if (activity == null) {
            errorMessage = "Errore: Impossibile avviare la biometria"
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    viewModel.loginWithBiometric { success ->
                        if (success) onLoginSuccess()
                        else errorMessage = "Utente non trovato"
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        errorMessage = "Errore biometrico: $errString"
                    }
                }

                override fun onAuthenticationFailed() {
                    errorMessage = "Autenticazione fallita"
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Accedi ad AlmaNotes")
            .setSubtitle("Usa l'impronta o il PIN del dispositivo")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        prompt.authenticate(promptInfo)
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
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    placeholder = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
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
                            if (success) onLoginSuccess()
                            else errorMessage = "Email o password errati"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = almaRed),
                    modifier = Modifier.fillMaxWidth().height(45.dp)
                ) {
                    Text("Accedi", color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Oppure accedi con", color = Color.DarkGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Spacer(modifier = Modifier.height(10.dp))
                Text("Oppure accedi con", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(context.getString(R.string.web_client_id))
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(signInWithGoogleOption)
                                    .build()

                                val result = credentialManager.getCredential(request = request, context = context)
                                val credential = result.credential

                                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                    viewModel.loginWithGoogle(
                                        googleId = googleIdTokenCredential.id,
                                        displayName = googleIdTokenCredential.displayName.orEmpty(),
                                        email = googleIdTokenCredential.id,
                                        photoUrl = null
                                    ) { success ->
                                        if (success) onLoginSuccess() else errorMessage = "Accesso con Google non riuscito"
                                    }
                                } else {
                                    errorMessage = "Credenziale Google non valida"
                                }
                            } catch (e: Exception) {
                                Log.e("LoginScreen", "errore durante l'accesso con Google", e)
                                Toast.makeText(context, "Accesso con Google annullato", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(45.dp)
                ) {
                    Text("Continua con Google")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { errorMessage = "Accesso con Apple non ancora disponibile" },
                    modifier = Modifier.fillMaxWidth().height(45.dp),
                    enabled = false
                ) {
                    Text("Continua con Apple", color = Color.Gray)
                }

                if (biometricAvailable && biometricEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { launchBiometric() },
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