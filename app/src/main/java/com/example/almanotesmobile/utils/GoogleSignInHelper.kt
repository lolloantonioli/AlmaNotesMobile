package com.example.almanotesmobile.utils

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

object GoogleSignInHelper {

    private fun client(context: Context) = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
    )

    /** Intent da passare al launcher */
    fun signInIntent(context: Context): Intent = client(context).signInIntent

    /**
     * Parsa il risultato dell'ActivityResult.
     * Restituisce l'account Google oppure null se l'utente ha annullato o c'è stato un errore.
     */
    fun parseResult(data: Intent?): GoogleSignInAccount? = try {
        GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
    } catch (_: ApiException) {
        null
    }

    /**
     * Sign-out da Google: necessario perché alla prossima apertura
     * venga mostrato di nuovo il selettore account.
     */
    fun signOut(context: Context) {
        client(context).signOut()
    }
}