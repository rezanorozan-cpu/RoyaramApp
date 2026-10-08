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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

private val SpecialPink = Color(0xFFE85D86)
private val SpecialLightPink = Color(0xFFFFE7EF)
private val SpecialDeepPink = Color(0xFFB83D63)
private val SpecialTextDark = Color(0xFF33252B)
private val SpecialSoftText = Color(0xFF82747A)

data class SpecialMoment(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val description: String = "",
    val createdAt: Long = 0L
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SpecialMomentsScreen(
    onBack: () -> Unit
) {
    val db = remember {
        FirebaseFirestore.getInstance()
    }

    var moments by remember {
        mutableStateOf<List<SpecialMoment>>(emptyList())
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var editingMoment by remember {
        mutableStateOf<SpecialMoment?>(null)
    }

    LaunchedEffect(Unit) {

        db.collection("specialMoments")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                moments = snapshot.documents.mapNotNull { document ->

                    SpecialMoment(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        date = document.getString("date") ?: "",
                        description = document.getString("description") ?: "",
                        createdAt = document.getLong("createdAt") ?: 0L
                    )
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
                            text = "لحظه‌های خاص ✨",
                            color = SpecialTextDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "تاریخ‌هایی که برای ما مهم‌اند ❤️",
                            color = SpecialSoftText,
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
                            tint = SpecialDeepPink
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = {

                    editingMoment = null
                    showDialog = true
                },

                containerColor = SpecialPink,
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
                            .background(SpecialLightPink),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = SpecialPink,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    Text(
                        text = "هنوز لحظه‌ی خاصی ثبت نشده",
                        color = SpecialTextDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "تاریخ‌هایی که برای رامین و رویا مهم هستند را اینجا نگه داریم ❤️",
                        color = SpecialSoftText,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    TextButton(
                        onClick = {

                            editingMoment = null
                            showDialog = true
                        }
                    ) {

                        Text(
                            text = "ثبت اولین تاریخ خاص ✨",
                            color = SpecialDeepPink,
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

                        SpecialMomentCard(
                            moment = moment,

                            onEdit = {

                                editingMoment = moment
                                showDialog = true
                            },

                            onDelete = {

                                db.collection("specialMoments")
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

        SpecialMomentDialog(
            moment = editingMoment,

            onDismiss = {

                showDialog = false
                editingMoment = null
            },

            onSave = { title, date, description ->

                val collection = db.collection("specialMoments")

                if (editingMoment == null) {

                    val data = hashMapOf<String, Any>(
                        "title" to title,
                        "date" to date,
                        "description" to description,
                        "createdAt" to System.currentTimeMillis()
                    )

                    collection.add(data)

                } else {

                    val data = hashMapOf<String, Any>(
                        "title" to title,
                        "date" to date,
                        "description" to description
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

@Composable
private fun SpecialMomentCard(
    moment: SpecialMoment,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },

        shape = RoundedCornerShape(26.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
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
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SpecialLightPink),

                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = SpecialPink,
                        modifier = Modifier.size(25.dp)
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
                            "یک لحظه‌ی خاص ❤️"
                        },

                        color = SpecialTextDark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = moment.date,
                        color = SpecialDeepPink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onEdit
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ویرایش",
                        tint = SpecialDeepPink
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

            if (moment.description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = moment.description,
                    color = SpecialTextDark,
                    fontSize = 14.sp,
                    lineHeight = 23.sp
                )
            }
        }
    }
}

@Composable
private fun SpecialMomentDialog(
    moment: SpecialMoment?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {

    var title by remember(moment) {
        mutableStateOf(
            moment?.title ?: ""
        )
    }

    var date by remember(moment) {
        mutableStateOf(
            moment?.date ?: currentPersianDateForSpecial()
        )
    }

    var description by remember(moment) {
        mutableStateOf(
            moment?.description ?: ""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {

            Text(
                text = if (moment == null) {
                    "ثبت لحظه‌ی خاص ✨"
                } else {
                    "ویرایش لحظه‌ی خاص"
                },

                color = SpecialTextDark,
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
                        Text("مثلاً اولین آشنایی ❤️")
                    },

                    singleLine = true
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

                    placeholder = {
                        Text("مثلاً 1405/03/20")
                    },

                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),

                    label = {
                        Text("توضیحات")
                    },

                    placeholder = {
                        Text("چرا این روز برای ما خاصه؟")
                    }
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    if (title.isNotBlank() && date.isNotBlank()) {

                        onSave(
                            title,
                            date,
                            description
                        )
                    }
                }
            ) {

                Text(
                    text = "ذخیره",
                    color = SpecialDeepPink,
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
                    color = SpecialSoftText
                )
            }
        }
    )
}

private fun currentPersianDateForSpecial(): String {

    return try {

        val now = java.util.Calendar.getInstance()

        val gy = now.get(java.util.Calendar.YEAR)
        val gm = now.get(java.util.Calendar.MONTH) + 1
        val gd = now.get(java.util.Calendar.DAY_OF_MONTH)

        val (jy, jm, jd) = gregorianToJalaliSpecial(
            gy,
            gm,
            gd
        )

        "$jy/$jm/$jd"

    } catch (_: Exception) {

        ""
    }
}

private fun gregorianToJalaliSpecial(
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

    val gy2 = if (gm > 2) {
        gy + 1
    } else {
        gy
    }

    var dayNo =
        365 * gy +
                (gy2 + 3) / 4 -
                (gy2 + 99) / 100 +
                (gy2 + 399) / 400

    for (i in 1 until gm) {
        dayNo += gDaysInMonth[i]
    }

    if (
        gm > 2 &&
        gy % 4 == 0 &&
        (gy % 100 != 0 || gy % 400 == 0)
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
