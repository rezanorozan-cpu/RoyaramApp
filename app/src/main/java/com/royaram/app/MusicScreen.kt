package com.royaram.app

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

private val MusicPink = Color(0xFFE85D86)
private val MusicDeepPink = Color(0xFFB83D63)
private val MusicDark = Color(0xFF33252B)
private val MusicSoftText = Color(0xFF82747A)

data class LoveSong(
    val id: String = "",
    val title: String = "",
    val artist: String = "",
    val note: String = "",
    val date: String = "",
    val createdAt: Long = 0L
)

@Composable
fun MusicScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val db = remember {
        FirebaseFirestore.getInstance()
    }

    var songs by remember {
        mutableStateOf<List<LoveSong>>(emptyList())
    }

    var showEditor by remember {
        mutableStateOf(false)
    }

    var editingSong by remember {
        mutableStateOf<LoveSong?>(null)
    }

    var playingId by remember {
        mutableStateOf<String?>(null)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var loading by remember {
        mutableStateOf(true)
    }

    DisposableEffect(db) {
        val registration: ListenerRegistration =
            db.collection("loveSongs")
                .orderBy(
                    "createdAt",
                    Query.Direction.DESCENDING
                )
                .addSnapshotListener { snapshot, error ->

                    loading = false

                    if (error != null) {
                        errorMessage =
                            "دریافت آهنگ‌ها ناموفق بود:\n" +
                            (error.message ?: "خطای نامشخص")
                        return@addSnapshotListener
                    }

                    songs = snapshot?.documents?.map { document ->
                        LoveSong(
                            id = document.id,
                            title = document.getString("title") ?: "",
                            artist = document.getString("artist") ?: "",
                            note = document.getString("note") ?: "",
                            date = document.getString("date") ?: "",
                            createdAt = document.getLong("createdAt") ?: 0L
                        )
                    } ?: emptyList()
                }

        onDispose {
            registration.remove()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFDDE8),
                        Color(0xFFFFEEF4),
                        Color(0xFFFFF7F9),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = MusicDeepPink
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "آهنگ ما 🎵",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MusicDark
                    )

                    Text(
                        text = "صداهایی که قصه‌ی ما رو یادآوری می‌کنن",
                        fontSize = 10.sp,
                        color = MusicSoftText
                    )
                }

                IconButton(
                    onClick = {
                        editingSong = null
                        showEditor = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "افزودن آهنگ",
                        tint = MusicDeepPink
                    )
                }
            }

            when {
                loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "در حال دریافت آهنگ‌ها... 💗",
                            color = MusicDeepPink
                        )
                    }
                }

                songs.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.White.copy(alpha = 0.72f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = MusicPink,
                                modifier = Modifier.size(45.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "هنوز آهنگی برای قصه‌مون ثبت نشده 🎵",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MusicDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = "از دکمه‌ی + برای اضافه کردن اولین آهنگ استفاده کن ❤️",
                            fontSize = 12.sp,
                            color = MusicSoftText,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = songs,
                            key = { it.id }
                        ) { song ->
                            MusicCard(
                                song = song,
                                isPlaying = playingId == song.id,
                                onPlay = {
                                    playingId =
                                        if (playingId == song.id) null
                                        else song.id
                                },
                                onEdit = {
                                    editingSong = song
                                    showEditor = true
                                },
                                onDelete = {
                                    db.collection("loveSongs")
                                        .document(song.id)
                                        .delete()
                                        .addOnSuccessListener {
                                            if (playingId == song.id) {
                                                playingId = null
                                            }

                                            Toast.makeText(
                                                context,
                                                "آهنگ حذف شد ❤️",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { error ->
                                            errorMessage =
                                                "حذف آهنگ ناموفق بود:\n" +
                                                (error.message ?: "خطای نامشخص")
                                        }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showEditor) {
            MusicEditorDialog(
                song = editingSong,
                onDismiss = {
                    showEditor = false
                    editingSong = null
                },
                onSave = { title, artist, note ->

                    val cleanTitle = title.trim()
                    val cleanArtist = artist.trim()
                    val cleanNote = note.trim()

                    if (cleanTitle.isBlank()) {
                        errorMessage = "لطفاً نام آهنگ رو وارد کن."
                        return@MusicEditorDialog
                    }

                    val data = hashMapOf(
                        "title" to cleanTitle,
                        "artist" to cleanArtist,
                        "note" to cleanNote
                    )

                    if (editingSong == null) {
                        data["date"] = jalaliTodayForMusic()
                        data["createdAt"] =
                            System.currentTimeMillis().toString()
                    }

                    val task = if (editingSong == null) {
                        db.collection("loveSongs")
                            .document()
                            .set(
                                mapOf(
                                    "title" to cleanTitle,
                                    "artist" to cleanArtist,
                                    "note" to cleanNote,
                                    "date" to jalaliTodayForMusic(),
                                    "createdAt" to System.currentTimeMillis()
                                )
                            )
                    } else {
                        db.collection("loveSongs")
                            .document(editingSong!!.id)
                            .update(data)
                    }

                    task.addOnSuccessListener {
                        showEditor = false
                        editingSong = null

                        Toast.makeText(
                            context,
                            if (editingSong == null)
                                "آهنگ با موفقیت ذخیره شد ❤️"
                            else
                                "آهنگ ویرایش شد ❤️",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener { error ->
                        errorMessage =
                            "ذخیره‌سازی انجام نشد:\n" +
                            (error.message ?: "خطای نامشخص")
                    }
                }
            )
        }
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = {
                errorMessage = null
            },
            title = {
                Text(
                    text = "پیام رویارام 💗",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(message)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        errorMessage = null
                    }
                ) {
                    Text(
                        text = "باشه",
                        color = MusicDeepPink
                    )
                }
            }
        )
    }
}

@Composable
private fun MusicCard(
    song: LoveSong,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFFE0EA),
                            Color(0xFFF4E7FF),
                            Color.White.copy(alpha = 0.82f)
                        )
                    ),
                    RoundedCornerShape(25.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(19.dp))
                        .background(Color.White.copy(alpha = 0.78f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MusicDeepPink,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MusicDark,
                        maxLines = 1
                    )

                    if (song.artist.isNotBlank()) {
                        Text(
                            text = song.artist,
                            fontSize = 10.sp,
                            color = MusicDeepPink,
                            maxLines = 1
                        )
                    }

                    if (song.note.isNotBlank()) {
                        Text(
                            text = song.note,
                            fontSize = 10.sp,
                            color = MusicSoftText,
                            maxLines = 3
                        )
                    }

                    if (song.date.isNotBlank()) {
                        Text(
                            text = "ثبت شده در ${song.date}",
                            fontSize = 8.sp,
                            color = MusicSoftText
                        )
                    }
                }

                IconButton(onClick = onPlay) {
                    Icon(
                        imageVector = if (isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = "پخش",
                        tint = MusicPink
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ویرایش",
                        tint = MusicSoftText
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = MusicDeepPink
                    )
                }
            }
        }
    }
}

@Composable
private fun MusicEditorDialog(
    song: LoveSong?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember(song?.id) {
        mutableStateOf(song?.title ?: "")
    }

    var artist by remember(song?.id) {
        mutableStateOf(song?.artist ?: "")
    }

    var note by remember(song?.id) {
        mutableStateOf(song?.note ?: "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (song == null) {
                    "🎵 افزودن آهنگ ما"
                } else {
                    "✏️ ویرایش آهنگ"
                },
                fontWeight = FontWeight.Bold,
                color = MusicDark
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("نام آهنگ *") }
                )

                Spacer(modifier = Modifier.height(9.dp))

                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("خواننده") }
                )

                Spacer(modifier = Modifier.height(9.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    label = { Text("یادداشت عاشقانه") }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = {
                    onSave(title, artist, note)
                }
            ) {
                Text(
                    text = "ذخیره",
                    color = MusicDeepPink,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "انصراف",
                    color = MusicSoftText
                )
            }
        }
    )
}

private fun jalaliTodayForMusic(): String {
    val calendar = java.util.Calendar.getInstance()

    val gy = calendar.get(java.util.Calendar.YEAR)
    val gm = calendar.get(java.util.Calendar.MONTH) + 1
    val gd = calendar.get(java.util.Calendar.DAY_OF_MONTH)

    val gDays = intArrayOf(
        31, 28, 31, 30, 31, 30,
        31, 31, 30, 31, 30, 31
    )

    val gyTemp = gy - 1600
    val gmTemp = gm - 1
    val gdTemp = gd - 1

    var gDayNo =
        365 * gyTemp +
            (gyTemp + 3) / 4 -
            (gyTemp + 99) / 100 +
            (gyTemp + 399) / 400

    for (i in 0 until gmTemp) {
        gDayNo += gDays[i]
    }

    if (
        gmTemp > 1 &&
        gy % 4 == 0 &&
        (gy % 100 != 0 || gy % 400 == 0)
    ) {
        gDayNo++
    }

    gDayNo += gdTemp

    val jDayNo = gDayNo - 79
    val jNp = jDayNo / 12053
    var jDay = jDayNo % 12053

    var jy = 979 + 33 * jNp + 4 * (jDay / 1461)

    jDay %= 1461

    if (jDay >= 366) {
        jy += (jDay - 1) / 365
        jDay = (jDay - 1) % 365
    }

    val jm: Int
    val jd: Int

    if (jDay < 186) {
        jm = 1 + jDay / 31
        jd = 1 + jDay % 31
    } else {
        jm = 7 + (jDay - 186) / 30
        jd = 1 + (jDay - 186) % 30
    }

    return "${jy.toString().padStart(4, '0')}/" +
        "${jm.toString().padStart(2, '0')}/" +
        jd.toString().padStart(2, '0')
}
