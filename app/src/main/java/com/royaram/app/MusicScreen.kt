package com.royaram.app

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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

    LaunchedEffect(Unit) {

        db.collection("loveSongs")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {

                    songs = snapshot.documents.map { document ->

                        LoveSong(
                            id = document.id,
                            title = document.getString("title") ?: "",
                            artist = document.getString("artist") ?: "",
                            note = document.getString("note") ?: "",
                            date = document.getString("date") ?: "",
                            createdAt = document.getLong("createdAt") ?: 0L
                        )
                    }
                }
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
                        tint = Color(0xFFB83D63)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "آهنگ ما 🎵",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF33252B)
                    )

                    Text(
                        text = "صداهایی که قصه‌ی ما رو یادآوری می‌کنن",
                        fontSize = 10.sp,
                        color = Color(0xFF82747A)
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
                        tint = Color(0xFFB83D63)
                    )
                }
            }

            if (songs.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 30.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(
                                RoundedCornerShape(30.dp)
                            )
                            .background(
                                Color.White.copy(
                                    alpha = 0.72f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color(0xFFE85D86),
                            modifier = Modifier.size(45.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "هنوز آهنگی برای قصه‌مون ثبت نشده 🎵",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF33252B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Text(
                        text = "اولین آهنگ خاطره‌انگیزمون رو اضافه کن ❤️",
                        fontSize = 11.sp,
                        color = Color(0xFF82747A),
                        textAlign = TextAlign.Center
                    )
                }

            } else {

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
                                    if (playingId == song.id) {
                                        null
                                    } else {
                                        song.id
                                    }
                            },
                            onEdit = {
                                editingSong = song
                                showEditor = true
                            },
                            onDelete = {

                                db.collection("loveSongs")
                                    .document(song.id)
                                    .delete()

                                if (playingId == song.id) {
                                    playingId = null
                                }
                            }
                        )
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

                    val date = jalaliTodayForMusic()

                    if (editingSong == null) {

                        val document =
                            db.collection("loveSongs")
                                .document()

                        document.set(
                            mapOf(
                                "title" to title,
                                "artist" to artist,
                                "note" to note,
                                "date" to date,
                                "createdAt" to System.currentTimeMillis()
                            )
                        )

                    } else {

                        db.collection("loveSongs")
                            .document(editingSong!!.id)
                            .update(
                                mapOf(
                                    "title" to title,
                                    "artist" to artist,
                                    "note" to note
                                )
                            )
                    }

                    showEditor = false
                    editingSong = null
                }
            )
        }
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
                        .clip(
                            RoundedCornerShape(19.dp)
                        )
                        .background(
                            Color.White.copy(
                                alpha = 0.78f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            if (isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.MusicNote
                            },
                        contentDescription = null,
                        tint = Color(0xFFB83D63),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.size(10.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = song.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF33252B),
                        maxLines = 1
                    )

                    if (song.artist.isNotBlank()) {

                        Text(
                            text = song.artist,
                            fontSize = 10.sp,
                            color = Color(0xFFB83D63),
                            maxLines = 1
                        )
                    }

                    if (song.note.isNotBlank()) {

                        Text(
                            text = song.note,
                            fontSize = 9.sp,
                            color = Color(0xFF82747A),
                            maxLines = 2
                        )
                    }

                    if (song.date.isNotBlank()) {

                        Text(
                            text = "ثبت شده در ${song.date}",
                            fontSize = 7.sp,
                            color = Color(0xFF9B8E94)
                        )
                    }
                }

                IconButton(
                    onClick = onPlay
                ) {

                    Icon(
                        imageVector =
                            if (isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                        contentDescription = "پخش",
                        tint = Color(0xFFE85D86)
                    )
                }

                IconButton(
                    onClick = onEdit
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ویرایش",
                        tint = Color(0xFF82747A)
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = Color(0xFFB83D63)
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

    var title by remember(song) {
        mutableStateOf(
            song?.title ?: ""
        )
    }

    var artist by remember(song) {
        mutableStateOf(
            song?.artist ?: ""
        )
    }

    var note by remember(song) {
        mutableStateOf(
            song?.note ?: ""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {

            Text(
                text =
                    if (song == null) {
                        "🎵 افزودن آهنگ ما"
                    } else {
                        "✏️ ویرایش آهنگ"
                    },
                fontWeight = FontWeight.Bold,
                color = Color(0xFF33252B)
            )
        },
        text = {

            Column {

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("نام آهنگ")
                    }
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                OutlinedTextField(
                    value = artist,
                    onValueChange = {
                        artist = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("خواننده")
                    }
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    label = {
                        Text("یادداشت عاشقانه")
                    }
                )
            }
        },
        confirmButton = {

            TextButton(
                onClick = {

                    if (title.isNotBlank()) {
                        onSave(
                            title.trim(),
                            artist.trim(),
                            note.trim()
                        )
                    }
                }
            ) {

                Text(
                    text = "ذخیره",
                    color = Color(0xFFB83D63),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "انصراف",
                    color = Color(0xFF82747A)
                )
            }
        }
    )
}


private fun jalaliTodayForMusic(): String {

    val calendar = java.util.Calendar.getInstance()

    val gy = calendar.get(
        java.util.Calendar.YEAR
    )

    val gm = calendar.get(
        java.util.Calendar.MONTH
    ) + 1

    val gd = calendar.get(
        java.util.Calendar.DAY_OF_MONTH
    )

    return gregorianToPersianForMusic(
        gy,
        gm,
        gd
    )
}


private fun gregorianToPersianForMusic(
    gy: Int,
    gm: Int,
    gd: Int
): String {

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

    var i = 0

    while (i < gmTemp) {
        gDayNo += gDays[i]
        i++
    }

    if (
        gmTemp > 1 &&
        gy % 4 == 0 &&
        (gy % 100 != 0 || gy % 400 == 0)
    ) {
        gDayNo++
    }

    gDayNo += gdTemp

    var jDayNo = gDayNo - 79

    val jNp = jDayNo / 12053

    var jDay = jDayNo % 12053

    var jy =
        979 +
            33 * jNp +
            4 * (jDay / 1461)

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

    return "${persianDigitsForMusic(jy.toString())}/" +
        "${persianDigitsForMusic(jm.toString().padStart(2, '0'))}/" +
        persianDigitsForMusic(
            jd.toString().padStart(2, '0')
        )
}


private fun persianDigitsForMusic(
    value: String
): String {

    return value
        .replace("0", "۰")
        .replace("1", "۱")
        .replace("2", "۲")
        .replace("3", "۳")
        .replace("4", "۴")
        .replace("5", "۵")
        .replace("6", "۶")
        .replace("7", "۷")
        .replace("8", "۸")
        .replace("9", "۹")
}
