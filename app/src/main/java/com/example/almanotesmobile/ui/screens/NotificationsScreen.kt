package com.example.almanotesmobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.data.notifications.AppNotification
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel = koinViewModel()) {
    val notifications = viewModel.notifications.collectAsStateWithLifecycle()
    
    if (notifications.value.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text("Nessuna notifica al momento.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(notifications.value, key = { it.id }) { notification ->
            NotificationItem(notification)
        }
    }
}

@Composable
private fun NotificationItem(notification: AppNotification) {
    Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(notification.title, fontWeight = FontWeight.Bold, color = Color(0xFFBB2E29))
            Spacer(Modifier.height(6.dp))
            Text(notification.message, color = MaterialTheme.colorScheme.onSurface)
            if (notification.isPushNotification) {
                Spacer(Modifier.height(8.dp))
                Text("Inviata anche come notifica push", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}