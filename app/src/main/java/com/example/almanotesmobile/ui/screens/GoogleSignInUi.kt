package com.example.almanotesmobile.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.almanotesmobile.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

private const val GOOGLE_CREDENTIAL_TAG = "GoogleCredentialAuth"

sealed interface GoogleCredentialManagerResult {
    data class Success(
        val googleId: String,
        val displayName: String?,
        val email: String?,
        val photoUrl: String?
    ) : GoogleCredentialManagerResult

    data class Error(val message: String) : GoogleCredentialManagerResult
}

suspend fun requestGoogleCredential(context: Context): GoogleCredentialManagerResult {
    val webClientId = context.getString(R.string.google_web_client_id)
    if (webClientId.isBlank()) {
        return GoogleCredentialManagerResult.Error(
            "Configura google_web_client_id con il client ID OAuth 2.0 di tipo Web"
        )
    }

    val credentialManager = CredentialManager.create(context)
    val googleOption = GetSignInWithGoogleOption.Builder(serverClientId = webClientId).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleOption)
        .build()

    return try {
        val result = credentialManager.getCredential(
            context = context,
            request = request
        )
        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            GoogleCredentialManagerResult.Success(
                googleId = googleCredential.uniqueId,
                displayName = googleCredential.displayName,
                email = googleCredential.email,
                photoUrl = googleCredential.profilePictureUri?.toString()
            )
        } else {
            GoogleCredentialManagerResult.Error("Credenziale Google non riconosciuta")
        }
    } catch (exception: GoogleIdTokenParsingException) {
        Log.w(GOOGLE_CREDENTIAL_TAG, "Unable to parse Google ID token", exception)
        GoogleCredentialManagerResult.Error("Token Google non valido: aggiorna la libreria Google Identity")
    } catch (exception: GetCredentialCancellationException) {
        Log.w(GOOGLE_CREDENTIAL_TAG, "Credential Manager did not return Google authorization", exception)
        GoogleCredentialManagerResult.Error(
            "Accesso Google non completato: se hai selezionato un account, controlla che " +
                    "google_web_client_id sia il client ID Web e che package name/SHA-1 siano configurati in Google Cloud."
        )
    } catch (exception: NoCredentialException) {
        Log.w(GOOGLE_CREDENTIAL_TAG, "No Google credential available", exception)
        GoogleCredentialManagerResult.Error("Nessun account Google disponibile sul dispositivo")
    } catch (exception: GetCredentialException) {
        Log.w(GOOGLE_CREDENTIAL_TAG, "Credential Manager Google sign-in failed", exception)
        GoogleCredentialManagerResult.Error(
            exception.message ?: "Accesso Google non riuscito con Credential Manager"
        )
    }
}

@Composable
fun GoogleSignInButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF3C4043)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(Color(0xFF4285F4))
            )
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}