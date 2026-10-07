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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

private val SadPink = Color(0xFFE85D86)
private val SadLightPink = Color(0xFFFFE7EF)
private val SadDeepPink = Color(0xFFB83D63)
private val SadTextDark = Color(0xFF33252B)
private val SadSoftText = Color(0xFF82747A)

data class SadMoment(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val date: String = "",
    val createdAt: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun SadMomentsScreen(
    onBack: () -> Unit
) {
    val db = remember {
        FirebaseFirestore.getInstance()
    }

    var moments by remember {
        mutableStateOf<List<SadMoment>>(emptyList())
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var editingMoment by remember {
        mutableStateOf<SadMoment?>(null)
    }

    LaunchedEffect(Unit) {

        db.collection("sadMoments")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                moments = snapshot.documents.mapNotNull { document ->

                    try {
                        SadMoment(
                            id = document.id,
                            title = document.getString("title") ?: "",
                            text = document.getString("text") ?: "",
                            date = document.getString("date") ?: "",
                            createdAt = document.getLong("createdAt") ?: 0L
                        )
                    } catch (_: Exception) {
                        null
                    }
                }
            }
    }

    Scaffold(
        containerColor = Color.Transparent,

        topBar = {

            TopAppBar(
                title = {
                    Column {

                        Text(
                            text = "وقتی دلمون گرفت ❤️",
                            color = SadTextDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "اینجا همیشه جای امن ماست",
                            color = SadSoftText,
                            fontSize = 12.sp
                        )
                    }
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = SadDeepPink
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },

        floatingActionButton = {

            androidx.compose.material3.FloatingActionButton(
                onClick = {
                    editingMoment = null
                    showDialog = true
                },
                containerColor = SadPink,
                contentColor = Color.White
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "افزودن"
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFE4ED),
                            Color(0xFFFFF4F7),
                            Color.White
                        )
                    )
                )
                .padding(paddingValues)
        ) {

            if (moments.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(SadLightPink),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = SadPink,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    Text(
                        text = "اگر دلت گرفته...",
                        color = SadTextDark,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "حست رو اینجا برای خودمون بنویس ❤️",
                        color = SadSoftText,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    TextButton(
                        onClick = {
                            editingMoment = null
                            showDialog = true
                        }
                    ) {

                        Text(
                            text = "اولین دل‌نوشته رو بنویس",
                            color = SadDeepPink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = 90.dp
                        ),

                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    items(
                        items = moments,
                        key = { it.id }
                    ) { moment ->

                        SadMomentCard(
                            moment = moment,

                            onEdit = {
                                editingMoment = moment
                                showDialog = true
                            },

                            onDelete = {

                                db.collection("sadMoments")
                                    .document(moment.id)
                                    .delete()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {

        SadMomentDialog(
            moment = editingMoment,

            onDismiss = {
                showDialog = false
                editingMoment = null
            },

            onSave = { title, text, date ->

                val collection = db.collection("sadMoments")

                if (editingMoment == null) {

                    val data = hashMapOf(
                        "title" to title,
                        "text" to text,
                        "date" to date,
                        "createdAt" to System.currentTimeMillis()
                    )

                    collection
                        .add(data)

                } else {

                    val data = hashMapOf(
                        "title" to title,
                        "text" to text,
                        "date" to date
                    )

                    collection
                        .document(editingMoment!!.id)
                        .update(data)
                }

                showDialog = false
                editingMoment = null
            }
        )
    }
}

@androidx.compose.runtime.Composable
private fun SadMomentCard(
    moment: SadMoment,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },

        shape = RoundedCornerShape(26.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.90f)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(SadLightPink),

                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = SadPink,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.size(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = moment.title.ifBlank {
                            "یه چیزی که دلم می‌خواست بگم..."
                        },

                        color = SadTextDark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (moment.date.isNotBlank()) {

                        Text(
                            text = moment.date,
                            color = SadSoftText,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(
                    onClick = onEdit
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ویرایش",
                        tint = SadDeepPink
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = Color(0xFFB85C70)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = moment.text,
                color = SadTextDark,
                fontSize = 14.sp,
                lineHeight = 23.sp
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun SadMomentDialog(
    moment: SadMoment?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {

    var title by remember(moment) {
        mutableStateOf(moment?.title ?: "")
    }

    var text by remember(moment) {
        mutableStateOf(moment?.text ?: "")
    }

    var date by remember(moment) {
        mutableStateOf(
            moment?.date ?: currentPersianDateForSad()
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {

            Text(
                text = if (moment == null) {
                    "یک دل‌نوشته ❤️"
                } else {
                    "ویرایش دل‌نوشته ✨"
                },

                color = SadTextDark,
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("عنوان")
                    },

                    placeholder = {
                        Text("مثلاً دلم برات تنگ شده...")
                    },

                    singleLine = true
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),

                    label = {
                        Text("حرف دلم")
                    },

                    placeholder = {
                        Text("هر چیزی که دوست داری با هم به اشتراک بذار...")
                    }
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("تاریخ")
                    },

                    singleLine = true
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    if (text.isNotBlank()) {
                        onSave(
                            title.ifBlank {
                                "یک دل‌نوشته برای ما ❤️"
                            },
                            text,
                            date
                        )
                    }
                }
            ) {

                Text(
                    text = "ذخیره",
                    color = SadDeepPink,
                    fontWeight = FontWeight.Bold
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "لغو",
                    color = SadSoftText
                )
            }
        }
    )
}

private fun currentPersianDateForSad(): String {

    return try {

        val now = java.util.Calendar.getInstance()

        val gy = now.get(java.util.Calendar.YEAR)
        val gm = now.get(java.util.Calendar.MONTH) + 1
        val gd = now.get(java.util.Calendar.DAY_OF_MONTH)

        val (jy, jm, jd) = gregorianToJalaliSad(
            gy,
            gm,
            gd
        )

        "$jy/$jm/$jd"

    } catch (_: Exception) {

        ""
    }
}

private fun gregorianToJalaliSad(
    gy: Int,
    gm: Int,
    gd: Int
): Triple<Int, Int, Int> {

    val gDaysInMonth = intArrayOf(
        0,
        31,
        28,
        31,
        30,
        31,
        30,
        31,
        31,
        30,
        31,
        30,
        31
    )

    var gy2 = gy

    val gyAdj = if (gm > 2) {
        gy2 + 1
    } else {
        gy2
    }

    val gDayNo =
        365 * gy2 +
                (gyAdj + 3) / 4 -
                (gyAdj + 99) / 100 +
                (gyAdj + 399) / 400

    var dayNo = gDayNo

    for (i in 1 until gm) {
        dayNo += gDaysInMonth[i]
    }

    if (
        gm > 2 &&
        (
            gy % 4 == 0 &&
                    (gy % 100 != 0 || gy % 400 == 0)
            )
    ) {
        dayNo += 1
    }

    dayNo += gd

    var jy = 979
    var jDayNo = dayNo - 79

    val jNp = jDayNo / 12053
    jDayNo %= 12053

    jy += 33 * jNp

    var i = 0

    while (i < 33) {

        val leap =
            if (
                i % 4 == 3 &&
                (i == 3 || i % 33 != 0)
            ) {
                1
            } else {
                0
            }

        if (jDayNo < 365 + leap) {
            break
        }

        jDayNo -= 365 + leap
        i++
    }

    jy += i

    val jm: Int
    val jd: Int

    if (jDayNo < 186) {

        jm = 1 + jDayNo / 31
        jd = 1 + jDayNo % 31

    } else {

        jm = 7 + (jDayNo - 186) / 30
        jd = 1 + (jDayNo - 186) % 30
    }

    return Triple(
        jy,
        jm,
        jd
    )
}
