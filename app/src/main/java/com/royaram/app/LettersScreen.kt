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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Send
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
import java.util.Calendar

private val LetterPink = Color(0xFFE85D86)
private val LetterDeepPink = Color(0xFFB83D63)
private val LetterSoftText = Color(0xFF82747A)
private val LetterDark = Color(0xFF33252B)

data class LoveLetter(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val date: String = "",
    val createdAt: Long = 0L
)

@Composable
fun LettersScreen(
    onBack: () -> Unit
) {
    val db = remember { FirebaseFirestore.getInstance() }

    var letters by remember {
        mutableStateOf(emptyList<LoveLetter>())
    }

    var showEditor by remember {
        mutableStateOf(false)
    }

    var editingLetter by remember {
        mutableStateOf<LoveLetter?>(null)
    }

    var deleteLetter by remember {
        mutableStateOf<LoveLetter?>(null)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        db.collection("letters")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    errorMessage = "دریافت نامه‌ها انجام نشد"
                    return@addSnapshotListener
                }

                letters = snapshot?.documents?.mapNotNull { doc ->

                    LoveLetter(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        text = doc.getString("text") ?: "",
                        date = doc.getString("date") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0L
                    )

                } ?: emptyList()
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFDDE8),
                        Color(0xFFFFF0F5),
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
                        horizontal = 10.dp,
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
                        tint = LetterDeepPink
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "نامه‌های عاشقانه 💌",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LetterDark
                    )

                    Text(
                        text = "حرف‌هایی که از قلبمون می‌نویسیم",
                        fontSize = 10.sp,
                        color = LetterSoftText
                    )
                }

                Icon(
                    imageVector = Icons.Default.Mail,
                    contentDescription = null,
                    tint = LetterPink,
                    modifier = Modifier.size(28.dp)
                )
            }

            if (letters.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "💌",
                        fontSize = 54.sp
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "هنوز نامه‌ای نوشته نشده",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = LetterDark
                    )

                    Text(
                        text = "اولین نامه‌ی عاشقانه‌تون رو بنویس ❤️",
                        fontSize = 11.sp,
                        color = LetterSoftText,
                        textAlign = TextAlign.Center
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),

                    verticalArrangement = Arrangement.spacedBy(10.dp),

                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                ) {

                    items(
                        letters,
                        key = { it.id }
                    ) { letter ->

                        LetterCard(
                            letter = letter,

                            onEdit = {
                                editingLetter = letter
                                showEditor = true
                            },

                            onDelete = {
                                deleteLetter = letter
                            }
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clickable {

                        editingLetter = null
                        showEditor = true
                    },

                shape = RoundedCornerShape(22.dp),

                colors = CardDefaults.cardColors(
                    containerColor = LetterDeepPink
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 13.dp),

                    horizontalArrangement = Arrangement.Center,

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Text(
                        text = "  نوشتن نامه‌ی جدید",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showEditor) {

        LetterEditorDialog(
            letter = editingLetter,

            onDismiss = {
                showEditor = false
                editingLetter = null
            },

            onSave = { title, text, date ->

                val data = hashMapOf(
                    "title" to title.trim(),
                    "text" to text.trim(),
                    "date" to date.trim(),
                    "createdAt" to (
                        editingLetter?.createdAt
                            ?: System.currentTimeMillis()
                        )
                )

                val task =
                    if (editingLetter == null) {

                        db.collection("letters")
                            .document()

                    } else {

                        db.collection("letters")
                            .document(editingLetter!!.id)
                    }

                task.set(data)

                    .addOnSuccessListener {

                        showEditor = false
                        editingLetter = null
                    }

                    .addOnFailureListener {

                        errorMessage =
                            "ذخیره نامه انجام نشد: ${
                                it.message ?: "خطای نامشخص"
                            }"
                    }
            }
        )
    }

    deleteLetter?.let { letter ->

        AlertDialog(

            onDismissRequest = {
                deleteLetter = null
            },

            title = {
                Text("حذف نامه؟")
            },

            text = {
                Text("این نامه برای همیشه حذف می‌شود.")
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        db.collection("letters")
                            .document(letter.id)
                            .delete()

                            .addOnFailureListener {
                                errorMessage =
                                    "حذف نامه انجام نشد"
                            }

                        deleteLetter = null
                    }
                ) {

                    Text(
                        "حذف",
                        color = Color(0xFFC62828)
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        deleteLetter = null
                    }
                ) {

                    Text("انصراف")
                }
            }
        )
    }

    errorMessage?.let { message ->

        AlertDialog(

            onDismissRequest = {
                errorMessage = null
            },

            title = {
                Text("رویـارام 💗")
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
                    Text("باشه")
                }
            }
        )
    }
}

@Composable
private fun LetterCard(
    letter: LoveLetter,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.84f
            )
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFD5E2),
                                    Color(0xFFF0DFFF)
                                )
                            )
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "💌",
                        fontSize = 23.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                ) {

                    Text(
                        text =
                            letter.title.ifBlank {
                                "نامه‌ی بدون عنوان"
                            },

                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = LetterDark
                    )

                    Text(
                        text = letter.date,
                        fontSize = 9.sp,
                        color = LetterDeepPink
                    )
                }

                IconButton(
                    onClick = onEdit
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ویرایش",
                        tint = LetterPink
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = Color(0xFFC05A70)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = letter.text,
                fontSize = 12.sp,
                lineHeight = 21.sp,
                color = LetterDark
            )
        }
    }
}

@Composable
private fun LetterEditorDialog(
    letter: LoveLetter?,
    onDismiss: () -> Unit,
    onSave: (
        String,
        String,
        String
    ) -> Unit
) {

    var title by remember(letter?.id) {
        mutableStateOf(
            letter?.title ?: ""
        )
    }

    var text by remember(letter?.id) {
        mutableStateOf(
            letter?.text ?: ""
        )
    }

    var date by remember(letter?.id) {
        mutableStateOf(
            letter?.date
                ?: jalaliTodayForLetter()
        )
    }

    val canSave =
        title.isNotBlank() &&
        text.isNotBlank() &&
        date.isNotBlank()

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {

            Text(
                text =
                    if (letter == null)
                        "نامه‌ی جدید 💌"
                    else
                        "ویرایش نامه 💌",

                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("عنوان نامه")
                    },

                    singleLine = true
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("تاریخ شمسی")
                    },

                    placeholder = {
                        Text("۱۴۰۵/۰۷/۱۵")
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
                        Text("متن نامه")
                    },

                    maxLines = 7
                )
            }
        },

        confirmButton = {

            TextButton(
                enabled = canSave,

                onClick = {
                    onSave(
                        title,
                        text,
                        date
                    )
                }
            ) {

                Text(
                    text = "ذخیره",

                    color =
                        if (canSave)
                            LetterDeepPink
                        else
                            LetterSoftText
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("انصراف")
            }
        }
    )
}

private fun jalaliTodayForLetter(): String {

    val c = Calendar.getInstance()

    val p =
        gregorianToPersianForLetter(
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH) + 1,
            c.get(Calendar.DAY_OF_MONTH)
        )

    return "${p.first.toString().padStart(4, '0')}/" +
            "${p.second.toString().padStart(2, '0')}/" +
            "${p.third.toString().padStart(2, '0')}"
}

private fun gregorianToPersianForLetter(
    gy: Int,
    gm: Int,
    gd: Int
): Triple<Int, Int, Int> {

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

        jy +=
            (jDay - 1) / 365

        jDay =
            (jDay - 1) % 365
    }

    val jm: Int
    val jd: Int

    if (jDay < 186) {

        jm =
            1 + jDay / 31

        jd =
            1 + jDay % 31

    } else {

        jm =
            7 + (jDay - 186) / 30

        jd =
            1 + (jDay - 186) % 30
    }

    return Triple(
        jy,
        jm,
        jd
    )
}
