package com.example.almanotesmobile.utils

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

object GoogleSignInHelper {

    data class GoogleUser(
        val id: String,
        val displayName: String,
        val email: String,
        val photoUrl: String?
    )

    private fun oneTapClient(context: Context): SignInClient = Identity.getSignInClient(context)

    private fun signInRequest(context: Context): BeginSignInRequest {
        val serverClientIdRes = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )
        require(serverClientIdRes != 0) { "default_web_client_id non configurato" }
        val serverClientId = context.getString(serverClientIdRes)

        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(serverClientId)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }

    fun beginSignIn(context: Context): Task<com.google.android.gms.auth.api.identity.BeginSignInResult> {
        return try {
            oneTapClient(context).beginSignIn(signInRequest(context))
        } catch (e: IllegalArgumentException) {
            Tasks.forException(e)
        }
    }

    fun parseResult(context: Context, data: Intent?): GoogleUser? = try {
        val credential: SignInCredential = oneTapClient(context).getSignInCredentialFromIntent(data)
        GoogleUser(
            id = credential.id,
            displayName = credential.displayName.orEmpty(),
            email = credential.id,
            photoUrl = credential.profilePictureUri?.toString()
        )
    } catch (_: ApiException) {
        null
    }

    fun signOut(context: Context) {
        oneTapClient(context).signOut()
    }
}