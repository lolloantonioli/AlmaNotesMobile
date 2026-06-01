package com.example.almanotesmobile.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

enum class PermissionStatus {
    Unknown,
    Granted,
    Denied,
    PermanentlyDenied;

    val isGranted get() = this == Granted
    val isDenied get() = this == Denied || this == PermanentlyDenied
}

interface MultiplePermissionHandler {
    val statuses: Map<String, PermissionStatus>
    fun launchPermissionRequest()
}

@Composable
fun rememberMultiplePermissions(
    permissions: List<String>,
    onResult: (status: Map<String, PermissionStatus>) -> Unit
): MultiplePermissionHandler {
    val context = LocalContext.current
    val activity = context.findActivity()

    var statuses by remember(permissions) {
        mutableStateOf(
            permissions.associateWith { permission ->
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    PermissionStatus.Granted
                } else {
                    PermissionStatus.Unknown
                }
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { newPermissions ->
        val updatedStatuses = newPermissions.mapValues { (permission, isGranted) ->
            when {
                isGranted -> PermissionStatus.Granted
                activity?.shouldShowRequestPermissionRationale(permission) == true -> PermissionStatus.Denied
                else -> PermissionStatus.PermanentlyDenied
            }
        }
        statuses = updatedStatuses
        onResult(updatedStatuses)
    }

    return remember(permissionLauncher, permissions, statuses) {
        object : MultiplePermissionHandler {
            override val statuses get() = statuses
            override fun launchPermissionRequest() {
                if (permissions.isEmpty()) {
                    val updatedStatuses = emptyMap<String, PermissionStatus>()
                    statuses = updatedStatuses
                    onResult(updatedStatuses)
                } else {
                    permissionLauncher.launch(permissions.toTypedArray())
                }
            }
        }
    }
}

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

fun requiredDocumentReadPermissions(): List<String> = when {
    Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 -> listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    else -> emptyList()
}

fun requiredImageReadPermissions(): List<String> = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(android.Manifest.permission.READ_MEDIA_IMAGES)
    else -> listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
}