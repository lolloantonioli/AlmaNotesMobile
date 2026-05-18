package com.example.almanotesmobile.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.utils.getLocallyDownloadedNoteIds
import com.example.almanotesmobile.utils.saveImageToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import kotlinx.coroutines.launch

// ─── Utility ──────────────────────────────────────────────────────────────────

private fun Long.toRelativeTimeString(): String {
    val diff = System.currentTimeMillis() - this
    val min  = diff / 60_000; val h = diff / 3_600_000; val d = diff / 86_400_000
    return when {
        diff < 60_000 -> "Adesso"
        min  < 60     -> "$min minut${if (min  == 1L) "o" else "i"} fa"
        h    < 24     -> "$h or${if (h == 1L) "a" else "e"} fa"
        d    < 7      -> "$d giorn${if (d == 1L) "o" else "i"} fa"
        else          -> "${d / 7} settiman${if (d / 7 == 1L) "a" else "e"} fa"
    }
}

private fun Int.toFormattedCount(): String = when {
    this >= 1_000_000 -> String.format(Locale.ITALIAN, "%.1fM", this / 1_000_000f)
    this >= 1_000     -> String.format(Locale.ITALIAN, "%.1fk", this / 1_000f)
    else              -> toString()
}

private fun achievementText(points: Long): String {
    val fmt = NumberFormat.getNumberInstance(Locale("it", "IT")).format(points)
    return when {
        points == 0L        -> "Carica il tuo primo appunto per guadagnare punti!"
        points < 1_000L     -> "Hai $fmt punti, sei agli inizi!"
        points < 10_000L    -> "Hai $fmt punti, continua così!"
        points < 100_000L   -> "Hai $fmt punti, ottimo lavoro!"
        points < 500_000L   -> "Hai $fmt punti, stai diventando popolare!"
        else                -> "Hai $fmt punti, sei figo!"
    }
}

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    authViewModel:  AuthViewModel,
    onOpenNote:     (Long) -> Unit,
    profileViewModel: ProfileViewModel = koinViewModel(),
    onShowUploadedNotes: () -> Unit,
    onShowDownloadedNotes: () -> Unit
) {
    val almaRed = Color(0xFFBB2E29)
    val context = LocalContext.current

    val username        by authViewModel.username.collectAsStateWithLifecycle()
    val email           by authViewModel.email.collectAsStateWithLifecycle()
    val profileImageUri by authViewModel.profileImageUri.collectAsStateWithLifecycle()

    // Note localmente in cache → "file scaricati"
    // Ottieni i riferimenti al ciclo di vita e al coroutine scope
    val lifecycleOwner = LocalLifecycleOwner.current
    val profileScope = rememberCoroutineScope()

    // Note localmente in cache → "file scaricati".
    // Aggiorna anche quando si torna dal visualizzatore PDF, perché la schermata
    // profilo può restare nello stack e LaunchedEffect(Unit) non verrebbe rilanciato.
    DisposableEffect(lifecycleOwner, context) {
        fun refreshDownloadedNotes() {
            profileScope.launch {
                val ids = withContext(Dispatchers.IO) { getLocallyDownloadedNoteIds(context) }
                profileViewModel.setDownloadedNoteIds(ids)
            }
        }

        refreshDownloadedNotes()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshDownloadedNotes()
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val uploadedNotes   by profileViewModel.uploadedNotes.collectAsStateWithLifecycle()
    val uploadedCount   by profileViewModel.uploadedCount.collectAsStateWithLifecycle()
    val downloadedNotes by profileViewModel.downloadedNotes.collectAsStateWithLifecycle()
    val downloadedCount by profileViewModel.downloadedCount.collectAsStateWithLifecycle()
    val topDownloaded   by profileViewModel.topDownloaded.collectAsStateWithLifecycle()
    val topRated        by profileViewModel.topRated.collectAsStateWithLifecycle()
    val totalPoints     by profileViewModel.totalPoints.collectAsStateWithLifecycle()

    var selectedNote          by remember { mutableStateOf<Note?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // ── Camera / Gallery ────────────────────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { saveImageToInternalStorage(context, it)?.let { p -> authViewModel.updateProfileImage(p) } }
    }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok && tempImageUri != null)
            saveImageToInternalStorage(context, tempImageUri!!)?.let { p -> authViewModel.updateProfileImage(p) }
    }
    val startCamera = {
        try {
            val f   = File(File(context.cacheDir, "images").apply { mkdirs() }, "profile_temp.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", f)
            tempImageUri = uri; cameraLauncher.launch(uri)
        } catch (e: Exception) { Toast.makeText(context, "Errore: ${e.message}", Toast.LENGTH_LONG).show() }
    }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) startCamera() else Toast.makeText(context, "Permesso negato", Toast.LENGTH_SHORT).show()
    }
    fun launchCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            startCamera()
        else permLauncher.launch(Manifest.permission.CAMERA)
    }

    // ── Layout ──────────────────────────────────────────────────────────────
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8)),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        // Titolo
        item {
            Row(
                modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PersonOutline, null, tint = almaRed, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Text("Profilo", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = almaRed)
            }
        }

        // ── Card credenziali ────────────────────────────────────────────────
        item {
            Card(
                modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(90.dp).clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
                            .clickable { showImageSourceDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUri != null) {
                            AsyncImage(profileImageUri, "Foto Profilo",
                                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddAPhoto, null, tint = Color.Gray, modifier = Modifier.size(30.dp))
                                Text("Aggiungi\nimmagine", fontSize = 9.sp, lineHeight = 11.sp,
                                    textAlign = TextAlign.Center, color = Color.Gray)
                            }
                        }
                    }
                    Spacer(Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        CredentialRow(Icons.Default.AlternateEmail, "Username", username.ifEmpty { "Utente" })
                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color(0xFFF0F0F0))
                        CredentialRow(Icons.Outlined.Email, "E-mail", email.ifEmpty { "non impostata" })
                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color(0xFFF0F0F0))
                        CredentialRow(Icons.Outlined.Lock, "Password", "••••••••••••", isPassword = true)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }

        // ── I file che hai caricato ─────────────────────────────────────────
        item {
            ProfileSectionHeader(Icons.Default.Upload, "I file che hai caricato",
                count = uploadedCount, showVedi = true, onVediClick = onShowUploadedNotes, modifier = Modifier.padding(horizontal = 16.dp))
        }
        item { Spacer(Modifier.height(8.dp)) }
        item {
            if (uploadedNotes.isEmpty())
                ProfileEmptyState("Non hai ancora caricato appunti", Modifier.padding(horizontal = 16.dp))
            else
                ProfileNoteCard(uploadedNotes, Modifier.padding(horizontal = 16.dp)) { note ->
                    UploadedProfileRow(note) { selectedNote = note }
                }
        }

        item { Spacer(Modifier.height(24.dp)) }

        // ── I file che hai scaricato ────────────────────────────────────────
        item {
            ProfileSectionHeader(Icons.Default.Download, "I file che hai scaricato",
                count = downloadedCount, showVedi = true, modifier = Modifier.padding(horizontal = 16.dp), onVediClick = onShowDownloadedNotes)
        }
        item { Spacer(Modifier.height(8.dp)) }
        item {
            if (downloadedNotes.isEmpty())
                ProfileEmptyState("Non hai ancora scaricato appunti", Modifier.padding(horizontal = 16.dp))
            else
                ProfileNoteCard(downloadedNotes, Modifier.padding(horizontal = 16.dp)) { note ->
                    DownloadedProfileRow(note) { selectedNote = note }
                }
        }

        item { Spacer(Modifier.height(24.dp)) }

        // ── I più popolari ──────────────────────────────────────────────────
        item {
            ProfileSectionHeader(Icons.Default.TrendingUp, "I più popolari",
                count = null, showVedi = false, modifier = Modifier.padding(horizontal = 16.dp))
        }
        item { Spacer(Modifier.height(8.dp)) }
        item {
            ProfileNoteCard(topDownloaded, Modifier.padding(horizontal = 16.dp)) { note ->
                PopularProfileRow(note) { selectedNote = note }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }

        // ── I fan favourites ────────────────────────────────────────────────
        item {
            ProfileSectionHeader(Icons.Outlined.Star, "I fan favourites",
                count = null, showVedi = false, modifier = Modifier.padding(horizontal = 16.dp))
        }
        item { Spacer(Modifier.height(8.dp)) }
        item {
            ProfileNoteCard(topRated, Modifier.padding(horizontal = 16.dp)) { note ->
                PopularProfileRow(note) { selectedNote = note }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }

        // ── I tuoi traguardi ────────────────────────────────────────────────
        item {
            ProfileSectionHeader(Icons.Default.EmojiEvents, "I tuoi traguardi",
                count = null, showVedi = true, modifier = Modifier.padding(horizontal = 16.dp))
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { AchievementCard(totalPoints, Modifier.padding(horizontal = 16.dp)) }

        item { Spacer(Modifier.height(32.dp)) }

        // ── Logout ──────────────────────────────────────────────────────────
        item {
            Button(
                onClick   = { authViewModel.logout() },
                modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = almaRed),
                shape     = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, null)
                Spacer(Modifier.width(8.dp))
                Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }

    // ── Dialog nota ─────────────────────────────────────────────────────────
    selectedNote?.let { note ->
        NoteDetailDialog(
            note      = note,
            onDismiss = { selectedNote = null },
            onDownload = { selectedNote = null; onOpenNote(note.id) }
        )
    }

    // ── Dialog foto profilo ─────────────────────────────────────────────────
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Foto Profilo", fontWeight = FontWeight.Bold) },
            text  = { Text("Scegli come inserire la tua foto:") },
            confirmButton = {
                Button(onClick = { showImageSourceDialog = false; galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(almaRed)) { Text("Galleria") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showImageSourceDialog = false; launchCamera() }) {
                    Text("Fotocamera", color = almaRed)
                }
            }
        )
    }
}

// ─── Section header ───────────────────────────────────────────────────────────

@Composable
private fun ProfileSectionHeader(
    icon:     ImageVector,
    title:    String,
    count:    Int?,
    showVedi: Boolean,
    modifier: Modifier = Modifier,
    onVediClick: () -> Unit = {}
) {
    val almaRed = Color(0xFFBB2E29)
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = almaRed, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text     = if (count != null) "$title ($count)" else title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color    = almaRed,
            modifier = Modifier.weight(1f)
        )
        if (showVedi) {
            OutlinedButton(
                onClick        = onVediClick,
                shape          = RoundedCornerShape(8.dp),
                border         = androidx.compose.foundation.BorderStroke(1.dp, almaRed),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier       = Modifier.height(30.dp)
            ) { Text("Vedi", color = almaRed, fontSize = 12.sp) }
        }
    }
}

// ─── Note card wrapper ────────────────────────────────────────────────────────

@Composable
private fun ProfileNoteCard(
    notes:    List<Note>,
    modifier: Modifier = Modifier,
    row:      @Composable (Note) -> Unit
) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            notes.forEachIndexed { i, note ->
                row(note)
                if (i < notes.lastIndex)
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray.copy(alpha = 0.4f), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun ProfileEmptyState(message: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Box(Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(message, color = Color.Gray, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}

// ─── Note rows ────────────────────────────────────────────────────────────────

/** File caricati: titolo + prof/materia | tempo fa */
@Composable
private fun UploadedProfileRow(note: Note, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f)) {
            Text(note.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${note.professorName} - ${note.subject}", fontSize = 11.sp, color = Color.Gray,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(note.uploadedAt.toRelativeTimeString(),
            fontSize = 11.sp, color = Color(0xFFBB2E29).copy(alpha = 0.85f))
    }
}

/** File scaricati: titolo + prof/materia + uploader | tempo fa */
@Composable
private fun DownloadedProfileRow(note: Note, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f)) {
            Text(note.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${note.professorName} - ${note.subject}", fontSize = 11.sp, color = Color.Gray,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(10.dp), tint = Color.Gray)
                Spacer(Modifier.width(3.dp))
                Text("Caricato da ${note.uploaderName}", fontSize = 10.sp, color = Color.Gray)
            }
        }
        Text(note.uploadedAt.toRelativeTimeString(),
            fontSize = 11.sp, color = Color(0xFFBB2E29).copy(alpha = 0.85f))
    }
}

/** Popolari / Fan favourites: titolo + prof/materia + download | rating */
@Composable
private fun PopularProfileRow(note: Note, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f)) {
            Text(note.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${note.professorName} - ${note.subject}", fontSize = 11.sp, color = Color.Gray,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Download, null, modifier = Modifier.size(10.dp), tint = Color.Gray)
                Spacer(Modifier.width(3.dp))
                Text("${note.downloadCount.toFormattedCount()} download", fontSize = 10.sp, color = Color.Gray)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, null, modifier = Modifier.size(12.dp),
                tint = Color(0xFFBB2E29).copy(alpha = 0.8f))
            Spacer(Modifier.width(2.dp))
            Text(String.format(Locale.ITALIAN, "%.1f", note.rating),
                fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── Achievement card ─────────────────────────────────────────────────────────

@Composable
private fun AchievementCard(points: Long, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.EmojiEvents, null,
                tint = Color(0xFFFFB800), modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))
            Text(achievementText(points), fontSize = 14.sp,
                fontWeight = FontWeight.Medium, lineHeight = 20.sp)
        }
    }
}

// ─── Riga credenziale ────────────────────────────────────────────────────────

@Composable
fun CredentialRow(icon: ImageVector, label: String, value: String, isPassword: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.DarkGray, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
        }
        if (isPassword) Icon(Icons.Outlined.Visibility, null,
            tint = Color.LightGray, modifier = Modifier.size(18.dp))
    }
}