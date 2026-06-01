package com.example.almanotesmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.ui.viewmodel.BadgesViewModel
import org.koin.androidx.compose.koinViewModel

data class BadgeUi(val title: String, val subtitle: String, val icon: ImageVector, val achieved: Boolean)

@Composable
fun BadgesScreen(
    viewModel: BadgesViewModel = koinViewModel()
) {
    val almaRed = Color(0xFFBB2E29)
    val progress by viewModel.progress.collectAsStateWithLifecycle()

    val badges = listOf(
        BadgeUi("Uploader Lv.1", "Carica 1 file", Icons.Default.Upload, progress.uploadedCount >= 1),
        BadgeUi("Uploader Lv.2", "Carica 5 file", Icons.Default.Upload, progress.uploadedCount >= 5),
        BadgeUi("Uploader Lv.3", "Carica 10 file", Icons.Default.Upload, progress.uploadedCount >= 10),
        BadgeUi("Downloader Lv.1", "Scarica 1 file", Icons.Default.Download, progress.downloadedCount >= 1),
        BadgeUi("Downloader Lv.2", "Scarica 5 file", Icons.Default.Download, progress.downloadedCount >= 5),
        BadgeUi("Downloader Lv.3", "Scarica 10 file", Icons.Default.Download, progress.downloadedCount >= 10),
        BadgeUi("Camera", "Aggiungi la foto profilo", Icons.Default.Person, progress.hasProfileImage),
        BadgeUi("Recensore Lv.1", "Metti 1 recensione", Icons.Default.Star, progress.reviewCount >= 1),
        BadgeUi("Recensore Lv.2", "Metti 5 recensioni", Icons.Default.Star, progress.reviewCount >= 5),
        BadgeUi("Recensore Lv.3", "Metti 10 recensioni", Icons.Default.Star, progress.reviewCount >= 10)
    )

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = almaRed)
            Spacer(Modifier.size(8.dp))
            Text("Traguardi ottenuti", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = almaRed)
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            items(badges) { badge -> BadgeCard(badge = badge) }
        }
    }
}

@Composable
private fun BadgeCard(badge: BadgeUi) {
    val almaRed = Color(0xFFBB2E29)
    val muted = Color(0xFF909090)
    val primary = if (badge.achieved) almaRed else muted

    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, primary, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(badge.icon, contentDescription = null, tint = primary, modifier = Modifier.size(34.dp))
            Spacer(Modifier.size(14.dp))
            Box {
                Column {
                    Text(badge.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primary)
                    Spacer(Modifier.height(4.dp))
                    Text(badge.subtitle, fontSize = 14.sp, color = if (badge.achieved) Color(0xFF4A4A4A) else muted)
                }
            }
        }
    }
}