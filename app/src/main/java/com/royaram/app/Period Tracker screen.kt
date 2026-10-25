package com.royaram.app

import android.icu.util.PersianCalendar
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val PeriodPink = Color(0xFFE85D86)
private val PeriodDeepPink = Color(0xFFB83D63)
private val PeriodLightPink = Color(0xFFFFE5EE)
private val PeriodBackground = Color(0xFFFFF7F9)
private val PeriodText = Color(0xFF33252B)
private val PeriodMuted = Color(0xFF82747A)

private data class PeriodRecord(
    val id: String,
    val startMillis: Long,
    val endMillis: Long,
    val note: String = ""
)

@Composable
fun PeriodTrackerScreen(
    onBack: () -> Unit
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }

    var records by remember { mutableStateOf<List<PeriodRecord>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    var startMillis by remember { mutableStateOf<Long?>(null) }
    var endMillis by remember { mutableStateOf<Long?>(null) }
    var note by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }

    var pickerTarget by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<PeriodRecord?>(null) }

    DisposableEffect(Unit) {
        val user = auth.currentUser

        if (user == null) {
            loading = false
            error = "برای استفاده از این بخش ابتدا وارد حساب کاربری شو."
            onDispose { }
        } else {
            val registration: ListenerRegistration =
                db.collection("periodCycles")
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            loading = false
                            error = "دریافت اطلاعات ناموفق بود: ${exception.localizedMessage ?: "خطای نامشخص"}"
                        } else {
                            records = snapshot?.documents
                                ?.mapNotNull { doc ->
                                    val start = doc.getLong("startMillis")
                                    val end = doc.getLong("endMillis")

                                    if (start != null && end != null) {
                                        PeriodRecord(
                                            id = doc.id,
                                            startMillis = start,
                                            endMillis = end,
                                            note = doc.getString("note") ?: ""
                                        )
                                    } else {
                                        null
                                    }
                                }
                                ?.sortedByDescending { it.startMillis }
                                ?: emptyList()

                            loading = false
                            error = null
                        }
                    }

            onDispose { registration.remove() }
        }
    }

    val chronological = records.sortedBy { it.startMillis }

    val cycleLengths = chronological
        .zipWithNext { a, b ->
            ((b.startMillis - a.startMillis) / 86_400_000.0).roundToInt()
        }
        .filter { it in 20..45 }

    val averageCycle = if (cycleLengths.isEmpty()) {
        28
    } else {
        cycleLengths.average().roundToInt().coerceIn(20, 45)
    }

    val latest = chronological.lastOrNull()
    val predictedNext = latest?.let {
        it.startMillis + averageCycle * 86_400_000L
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeriodBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "بازگشت",
                    tint = PeriodDeepPink
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "تقویم سلامت ما 🌸",
                    color = PeriodDeepPink,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "پیگیری چرخه قاعدگی",
                    color = PeriodMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = PeriodPink
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = PeriodLightPink
                    ),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "خلاصه چرخه 🌷",
                            color = PeriodDeepPink,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SummaryBox(
                                title = "میانگین چرخه",
                                value = "$averageCycle روز",
                                modifier = Modifier.weight(1f)
                            )

                            SummaryBox(
                                title = "مدت آخرین دوره",
                                value = latest?.let {
                                    val days = (
                                        (it.endMillis - it.startMillis) /
                                            86_400_000L
                                        ).toInt() + 1
                                    "$days روز"
                                } ?: "ثبت نشده",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Text(
                            text = if (predictedNext != null) {
                                "شروع احتمالی دوره بعد: ${formatJalali(predictedNext)}"
                            } else {
                                "برای تخمین دوره بعد، تاریخ شروع اولین دوره را ثبت کن."
                            },
                            color = PeriodText,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            "این پیش‌بینی تقریبی است و برای جلوگیری از بارداری قابل اتکا نیست.",
                            color = PeriodMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            if (editingId == null) "ثبت دوره جدید 💗"
                            else "ویرایش دوره 💗",
                            color = PeriodDeepPink,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        DateSelectButton(
                            title = "تاریخ شروع دوره",
                            date = startMillis?.let { formatJalali(it) }
                                ?: "انتخاب تاریخ شمسی",
                            onClick = { pickerTarget = "start" }
                        )

                        DateSelectButton(
                            title = "تاریخ پایان دوره",
                            date = endMillis?.let { formatJalali(it) }
                                ?: "انتخاب تاریخ شمسی",
                            onClick = { pickerTarget = "end" }
                        )

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("یادداشت (اختیاری)") },
                            placeholder = { Text("مثلاً علائم یا توضیحات") },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = false,
                            minLines = 2
                        )

                        Button(
                            onClick = {
                                val start = startMillis
                                val end = endMillis
                                val user = auth.currentUser

                                when {
                                    user == null -> {
                                        error = "ابتدا وارد حساب کاربری شو."
                                    }

                                    start == null || end == null -> {
                                        error = "لطفاً تاریخ شروع و پایان را انتخاب کن."
                                    }

                                    end < start -> {
                                        error = "تاریخ پایان نمی‌تواند قبل از شروع باشد."
                                    }

                                    ((end - start) / 86_400_000L) > 20 -> {
                                        error = "فاصله شروع تا پایان بیشتر از حد مجاز است؛ تاریخ‌ها را بررسی کن."
                                    }

                                    else -> {
                                        saving = true
                                        error = null

                                        val data = hashMapOf(
                                            "startMillis" to start,
                                            "endMillis" to end,
                                            "note" to note.trim(),
                                            "updatedBy" to user.uid,
                                            "updatedAt" to System.currentTimeMillis()
                                        )

                                        val collection =
                                            db.collection("periodCycles")

                                        val task = if (editingId == null) {
                                            data["createdBy"] = user.uid
                                            data["createdAt"] = System.currentTimeMillis()
                                            collection.add(data)
                                        } else {
                                            collection.document(editingId!!).set(
                                                data,
                                                com.google.firebase.firestore.SetOptions.merge()
                                            )
                                        }

                                        task.addOnSuccessListener {
                                            saving = false
                                            message = if (editingId == null) {
                                                "دوره با موفقیت ذخیره شد 🌸"
                                            } else {
                                                "تغییرات ذخیره شد 🌸"
                                            }
                                            startMillis = null
                                            endMillis = null
                                            note = ""
                                            editingId = null
                                        }.addOnFailureListener { e ->
                                            saving = false
                                            error = "ذخیره ناموفق بود: ${e.localizedMessage ?: "خطای نامشخص"}"
                                        }
                                    }
                                }
                            },
                            enabled = !saving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PeriodPink
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (saving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Save,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (editingId == null) "ذخیره دوره"
                                    else "ذخیره تغییرات"
                                )
                            }
                        }

                        if (editingId != null) {
                            TextButton(
                                onClick = {
                                    editingId = null
                                    startMillis = null
                                    endMillis = null
                                    note = ""
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("لغو ویرایش", color = PeriodMuted)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "سوابق ثبت‌شده",
                    color = PeriodDeepPink,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PeriodPink)
                    }
                }
            } else if (records.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "هنوز دوره‌ای ثبت نشده است.\nاولین رکورد را از بخش بالا اضافه کن.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            color = PeriodMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(records, key = { it.id }) { record ->
                    PeriodRecordCard(
                        record = record,
                        onEdit = {
                            editingId = record.id
                            startMillis = record.startMillis
                            endMillis = record.endMillis
                            note = record.note
                            error = null
                            message = null
                        },
                        onDelete = { deleteTarget = record }
                    )
                }
            }

            if (error != null) {
                item {
                    Text(
                        error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (message != null) {
                item {
                    Text(
                        message!!,
                        color = PeriodDeepPink,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (pickerTarget != null) {
        JalaliDatePickerDialog(
            initialMillis = if (pickerTarget == "start") {
                startMillis ?: System.currentTimeMillis()
            } else {
                endMillis ?: startMillis ?: System.currentTimeMillis()
            },
            onDismiss = { pickerTarget = null },
            onSelect = { selected ->
                if (pickerTarget == "start") {
                    startMillis = selected
                    if (endMillis != null && endMillis!! < selected) {
                        endMillis = null
                    }
                } else {
                    endMillis = selected
                }
                pickerTarget = null
            }
        )
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("حذف رکورد") },
            text = { Text("از حذف این دوره مطمئنی؟ این کار قابل بازگشت نیست.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val record = deleteTarget!!
                        deleteTarget = null

                        db.collection("periodCycles")
                            .document(record.id)
                            .delete()
                            .addOnSuccessListener {
                                message = "رکورد حذف شد."
                            }
                            .addOnFailureListener { e ->
                                error = "حذف ناموفق بود: ${e.localizedMessage ?: "خطای نامشخص"}"
                            }
                    }
                ) {
                    Text("حذف", color = PeriodDeepPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("انصراف")
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun SummaryBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                title,
                color = PeriodMuted,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                value,
                color = PeriodDeepPink,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun DateSelectButton(
    title: String,
    date: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = PeriodBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = PeriodPink
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = PeriodMuted,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    date,
                    color = PeriodText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PeriodRecordCard(
    record: PeriodRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = PeriodPink
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "دوره ثبت‌شده",
                    modifier = Modifier.weight(1f),
                    color = PeriodDeepPink,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "ویرایش",
                        tint = PeriodPink
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = PeriodDeepPink
                    )
                }
            }

            Text(
                "شروع: ${formatJalali(record.startMillis)}",
                color = PeriodText
            )
            Text(
                "پایان: ${formatJalali(record.endMillis)}",
                color = PeriodText
            )

            if (record.note.isNotBlank()) {
                Text(
                    "یادداشت: ${record.note}",
                    color = PeriodMuted
                )
            }
        }
    }
}

@Composable
private fun JalaliDatePickerDialog(
    initialMillis: Long,
    onDismiss: () -> Unit,
    onSelect: (Long) -> Unit
) {
    val calendar = remember(initialMillis) {
        PersianCalendar().apply {
            timeInMillis = initialMillis
            firstDayOfWeek = Calendar.SATURDAY
        }
    }

    var displayedYear by remember {
        mutableIntStateOf(calendar.get(Calendar.YEAR))
    }
    var displayedMonth by remember {
        mutableIntStateOf(calendar.get(Calendar.MONTH))
    }

    val monthCalendar = remember(displayedYear, displayedMonth) {
        PersianCalendar().apply {
            clear()
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            firstDayOfWeek = Calendar.SATURDAY
        }
    }

    val firstDayOffset =
        (monthCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.SATURDAY + 7) % 7

    val daysInMonth =
        monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val weekDays = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "انتخاب تاریخ شمسی",
                color = PeriodDeepPink,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            val temp = PersianCalendar().apply {
                                clear()
                                set(displayedYear, displayedMonth, 1, 12, 0)
                                add(Calendar.MONTH, -1)
                            }
                            displayedYear = temp.get(Calendar.YEAR)
                            displayedMonth = temp.get(Calendar.MONTH)
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "ماه قبل",
                            tint = PeriodPink
                        )
                    }

                    Text(
                        "${monthNames[displayedMonth]} $displayedYear",
                        color = PeriodText,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            val temp = PersianCalendar().apply {
                                clear()
                                set(displayedYear, displayedMonth, 1, 12, 0)
                                add(Calendar.MONTH, 1)
                            }
                            displayedYear = temp.get(Calendar.YEAR)
                            displayedMonth = temp.get(Calendar.MONTH)
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "ماه بعد",
                            tint = PeriodPink
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    weekDays.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = PeriodDeepPink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val cellCount = ((firstDayOffset + daysInMonth + 6) / 7) * 7

                for (row in 0 until cellCount / 7) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val index = row * 7 + col
                            val day = index - firstDayOffset + 1

                            if (day in 1..daysInMonth) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                        .background(
                                            PeriodLightPink,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            val chosen = PersianCalendar().apply {
                                                clear()
                                                set(
                                                    displayedYear,
                                                    displayedMonth,
                                                    day,
                                                    12,
                                                    0,
                                                    0
                                                )
                                                set(
                                                    Calendar.MILLISECOND,
                                                    0
                                                )
                                            }
                                            onSelect(chosen.timeInMillis)
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        day.toString(),
                                        color = PeriodDeepPink
                                    )
                                }
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                        .height(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("بستن", color = PeriodDeepPink)
            }
        },
        containerColor = Color.White
    )
}

private fun formatJalali(millis: Long): String {
    val calendar = PersianCalendar().apply {
        timeInMillis = millis
    }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return String.format(
        Locale.US,
        "%04d/%02d/%02d",
        year,
        month,
        day
    )
}
