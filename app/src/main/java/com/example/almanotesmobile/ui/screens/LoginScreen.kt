package com.example.almanotesmobile.ui.screens

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

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBiometricLogin: () -> Unit,   // mantenuto per compatibilità, non usato direttamente
    viewModel: AuthViewModel
) {
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var errorMessage   by remember { mutableStateOf<String?>(null) }

    val biometricEnabled by viewModel.biometricEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val almaRed = Color(0xFFBB2E29)
    val cardBg  = Color(0xFFFAFAFA)

    // ── Controlla se la biometria è disponibile sul dispositivo ──────────────
    val biometricAvailable = remember(context) {
        val bm = BiometricManager.from(context)
        bm.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    // ── Costruisce e lancia il BiometricPrompt ────────────────────────────────
    fun launchBiometric() {
        val executor = ContextCompat.getMainExecutor(context)
        val activity = context as FragmentActivity

        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    viewModel.loginWithBiometric { success ->
                        if (success) onLoginSuccess()
                        else errorMessage = "Utente non trovato"
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    // L'utente ha annullato o c'è stato un errore hardware: non mostriamo errore
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        errorMessage = "Errore biometrico: $errString"
                    }
                }

                override fun onAuthenticationFailed() {
                    errorMessage = "Autenticazione non riconosciuta, riprova"
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Accedi ad AlmaNotes")
            .setSubtitle("Usa la tua impronta o il riconoscimento facciale")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        prompt.authenticate(promptInfo)
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.sfondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Accedi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = almaRed,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    placeholder = { Text("Email address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    placeholder = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Non sei registrato? Registrati qui",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
                    modifier = Modifier.fillMaxWidth().height(45.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Accedi", color = Color.White, fontSize = 16.sp)
                }

                // Bottone biometria — visibile solo se il dispositivo la supporta
                // e l'utente ha già fatto almeno un login con password
                if (biometricAvailable && biometricEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { launchBiometric() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = almaRed.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.fillMaxWidth().height(45.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Accedi con biometria", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}