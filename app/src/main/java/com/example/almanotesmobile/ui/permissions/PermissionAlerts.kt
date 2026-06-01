package com.example.almanotesmobile.ui.permissions

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun PermissionDeniedAlert(
    show: Boolean,
    title: String,
    message: String,
    actionLabel: String = "Concedi",
    dismissLabel: String = "Annulla",
    hideAfterAction: Boolean = true,
    onAction: () -> Unit,
    onHide: () -> Unit
) {
    if (show) {
        AlertDialog(
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    onAction()
                    if (hideAfterAction) {
                        onHide()
                    }
                }) {
                    Text(actionLabel)
                }
            },
            dismissButton = {
                TextButton(onClick = onHide) {
                    Text(dismissLabel)
                }
            },
            onDismissRequest = onHide
        )
    }
}

@Composable
fun PermissionPermanentlyDeniedSnackbar(
    snackbarHostState: SnackbarHostState,
    show: Boolean,
    message: String,
    actionLabel: String = "Impostazioni",
    onAction: () -> Unit,
    onHide: () -> Unit
) {
    if (show) {
        LaunchedEffect(snackbarHostState, message) {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                onAction()
            }
            onHide()
        }
    }
}