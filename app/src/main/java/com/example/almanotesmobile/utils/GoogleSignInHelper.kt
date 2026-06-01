package com.example.almanotesmobile.utils

import android.content.Context
import android.content.Intent
import com.example.almanotesmobile.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

object GoogleSignInHelper {

    private fun client(context: Context): com.google.android.gms.auth.api.signin.GoogleSignInClient {
        val webClientId = context.getString(R.string.google_web_client_id)
        val gsoBuilder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()

        // Se abbiamo un Web Client ID, lo usiamo per ottenere il token ID
        if (webClientId.isNotBlank()) {
            gsoBuilder.requestIdToken(webClientId)
        }

        return GoogleSignIn.getClient(context, gsoBuilder.build())
    }

    fun signInIntent(context: Context): Intent = client(context).signInIntent

    fun parseResult(data: Intent?): GoogleSignInAccount? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        return task.getResult(ApiException::class.java)
    }

    fun signOut(context: Context) {
        client(context).signOut()
    }
}
