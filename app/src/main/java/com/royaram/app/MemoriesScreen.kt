package com.royaram.app

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val MemoryPink = Color(0xFFE85D86)
private val MemoryDeepPink = Color(0xFFB83D63)
private val MemoryBackground = Color(0xFFFFF5F8)
private val MemoryText = Color(0xFF33252B)
private val MemorySoftText = Color(0xFF82747A)

data class LuxuryMemory(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val imageUrl: String = "",
    val createdAt: Long = 0L
)

@Composable
fun MemoriesScreen(
    onBack: () -> Unit
) {
    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val storage = remember {
        FirebaseStorage.getInstance()
    }

    var memories by remember {
        mutableStateOf<List<LuxuryMemory>>(emptyList())
    }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var editingMemory by remember {
        mutableStateOf<LuxuryMemory?>(null)
    }

    var deletingMemory by remember {
        mutableStateOf<LuxuryMemory?>(null)
    }

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedImage = uri
        }
    }

    LaunchedEffect(Unit) {
        firestore.collection("memories")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val result = mutableListOf<LuxuryMemory>()

                for (document in snapshot.documents) {
                    try {
                        result.add(
                            LuxuryMemory(
                                id = document.id,
                                title = document.getString("title").orEmpty(),
                                description = document.getString("description").orEmpty(),
                                date = document.getString("date").orEmpty(),
                                imageUrl = document.getString("imageUrl").orEmpty(),
                                createdAt =
                                    document.getLong("createdAt") ?: 0L
                            )
                        )
                    } catch (_: Exception) {
                    }
                }

                memories = result
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFE5EE),
                        MemoryBackground,
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
                        start = 10.dp,
                        end = 16.dp,
                        top = 14.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = MemoryDeepPink
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "خاطرات ما",
                        color = MemoryText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "لحظه‌هایی که برای همیشه می‌مونن ❤️",
                        color = MemorySoftText,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MemoryPink,
                                    MemoryDeepPink
                                )
                            )
                        )
                        .clickable {
                            selectedImage = null
                            errorMessage = ""
                            editingMemory = null
                            showAddDialog = true
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "افزودن خاطره",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            if (memories.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = MemoryPink,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        Text(
                            text = "هنوز خاطره‌ای ثبت نشده",
                            color = MemoryText,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(7.dp)
                        )

                        Text(
                            text = "اولین لحظه قشنگتون رو به آلبوم اضافه کنید ❤️",
                            color = MemorySoftText,
                            fontSize = 13.sp
                        )

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        Button(
                            onClick = {
                                selectedImage = null
                                errorMessage = ""
                                editingMemory = null
                                showAddDialog = true
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.size(6.dp)
                            )

                            Text("افزودن اولین خاطره")
                        }
                    }
                }

            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 14.dp,
                        end = 14.dp,
                        top = 10.dp,
                        bottom = 24.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    items(
                        items = memories,
                        key = { it.id }
                    ) { memory ->

                        MemoryCard(
                            memory = memory,

                            onEdit = {
                                editingMemory = memory
                                selectedImage = null
                                errorMessage = ""
                            },

                            onDelete = {
                                deletingMemory = memory
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {

        MemoryEditorDialog(
            title = "",
            description = "",
            date = jalaliToday(),
            selectedImage = selectedImage,
            errorMessage = errorMessage,
            isEdit = false,

            onChooseImage = {
                imagePicker.launch(
                    arrayOf("image/*")
                )
            },

            onDismiss = {
                if (editingMemory == null) {
                    showAddDialog = false
                    selectedImage = null
                    errorMessage = ""
                }
            },

            onSave = { title, description, date, setSaving ->

                saveMemory(
                    firestore = firestore,
                    storage = storage,
                    title = title,
                    description = description,
                    date = date,
                    imageUri = selectedImage,
                    setSaving = setSaving,
                    onSuccess = {
                        showAddDialog = false
                        selectedImage = null
                        errorMessage = ""
                    },
                    onError = {
                        errorMessage = it
                    }
                )
            }
        )
    }

    editingMemory?.let { memory ->

        MemoryEditorDialog(
            title = memory.title,
            description = memory.description,
            date = memory.date,
            selectedImage = selectedImage,
            errorMessage = errorMessage,
            isEdit = true,

            onChooseImage = {
                imagePicker.launch(
                    arrayOf("image/*")
                )
            },

            onDismiss = {
                editingMemory = null
                selectedImage = null
                errorMessage = ""
            },

            onSave = { title, description, date, setSaving ->

                updateMemory(
                    firestore = firestore,
                    storage = storage,
                    memory = memory,
                    title = title,
                    description = description,
                    date = date,
                    imageUri = selectedImage,
                    setSaving = setSaving,
                    onSuccess = {
                        editingMemory = null
                        selectedImage = null
                        errorMessage = ""
                    },
                    onError = {
                        errorMessage = it
                    }
                )
            }
        )
    }

    deletingMemory?.let { memory ->

        AlertDialog(
            onDismissRequest = {
                deletingMemory = null
            },

            title = {
                Text(
                    text = "حذف خاطره؟",
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    text =
                        "مطمئنی می‌خوای «${memory.title}» رو حذف کنی؟"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        firestore
                            .collection("memories")
                            .document(memory.id)
                            .delete()
                            .addOnSuccessListener {
                                deletingMemory = null
                            }
                            .addOnFailureListener {
                                deletingMemory = null
                                errorMessage =
                                    it.message
                                        ?: "حذف خاطره انجام نشد"
                            }
                    }
                ) {

                    Text(
                        text = "حذف",
                        color = Color(0xFFD32F2F)
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        deletingMemory = null
                    }
                ) {
                    Text("لغو")
                }
            }
        )
    }

    if (errorMessage.isNotBlank() &&
        !showAddDialog &&
        editingMemory == null
    ) {

        ToastMessage(
            message = errorMessage
        )
    }
}

private fun saveMemory(
    firestore: FirebaseFirestore,
    storage: FirebaseStorage,
    title: String,
    description: String,
    date: String,
    imageUri: Uri?,
    setSaving: (Boolean) -> Unit,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {

    setSaving(true)

    fun writeToFirestore(
        imageUrl: String
    ) {

        val data = hashMapOf(
            "title" to title,
            "description" to description,
            "date" to date,
            "imageUrl" to imageUrl,
            "createdAt" to System.currentTimeMillis()
        )

        firestore
            .collection("memories")
            .add(data)
            .addOnSuccessListener {
                setSaving(false)
                onSuccess()
            }
            .addOnFailureListener {
                setSaving(false)
                onError(
                    it.message
                        ?: "ذخیره خاطره انجام نشد"
                )
            }
    }

    if (imageUri == null) {

        writeToFirestore("")

        return
    }

    val fileName =
        "memories/${UUID.randomUUID()}.jpg"

    val reference =
        storage.reference.child(fileName)

    reference
        .putFile(imageUri)
        .continueWithTask { task ->

            if (!task.isSuccessful) {
                throw task.exception
                    ?: Exception("آپلود عکس ناموفق بود")
            }

            reference.downloadUrl
        }
        .addOnSuccessListener { uri ->

            writeToFirestore(
                uri.toString()
            )
        }
        .addOnFailureListener {

            setSaving(false)

            onError(
                "آپلود عکس انجام نشد:\n" +
                    (it.message ?: "خطای نامشخص")
            )
        }
}

private fun updateMemory(
    firestore: FirebaseFirestore,
    storage: FirebaseStorage,
    memory: LuxuryMemory,
    title: String,
    description: String,
    date: String,
    imageUri: Uri?,
    setSaving: (Boolean) -> Unit,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {

    setSaving(true)

    fun updateFirestore(
        imageUrl: String
    ) {

        val data = hashMapOf<String, Any>(
            "title" to title,
            "description" to description,
            "date" to date,
            "imageUrl" to imageUrl
        )

        firestore
            .collection("memories")
            .document(memory.id)
            .update(data)
            .addOnSuccessListener {
                setSaving(false)
                onSuccess()
            }
            .addOnFailureListener {
                setSaving(false)
                onError(
                    it.message
                        ?: "ویرایش خاطره انجام نشد"
                )
            }
    }

    if (imageUri == null) {

        updateFirestore(
            memory.imageUrl
        )

        return
    }

    val fileName =
        "memories/${UUID.randomUUID()}.jpg"

    val reference =
        storage.reference.child(fileName)

    reference
        .putFile(imageUri)
        .continueWithTask { task ->

            if (!task.isSuccessful) {
                throw task.exception
                    ?: Exception("آپلود عکس ناموفق بود")
            }

            reference.downloadUrl
        }
        .addOnSuccessListener { uri ->

            updateFirestore(
                uri.toString()
            )
        }
        .addOnFailureListener {

            setSaving(false)

            onError(
                "تعویض عکس انجام نشد:\n" +
                    (it.message ?: "خطای نامشخص")
            )
        }
}

@Composable
private fun MemoryCard(
    memory: LuxuryMemory,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(24.dp)
            )
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.92f)
        ) {

            if (memory.imageUrl.isNotBlank()) {

                AsyncImage(
                    model = memory.imageUrl,
                    contentDescription = memory.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 24.dp,
                                topEnd = 24.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )

            } else {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFE5EE),
                                    Color(0xFFFFF7FA)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MemoryPink,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(5.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(
                                alpha = 0.9f
                            )
                        )
                        .clickable {
                            onEdit()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ویرایش",
                        tint = MemoryDeepPink,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(
                                alpha = 0.9f
                            )
                        )
                        .clickable {
                            onDelete()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 10.dp
            )
        ) {

            Text(
                text = memory.title,
                color = MemoryText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            if (memory.description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = memory.description,
                    color = MemorySoftText,
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MemoryPink,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(
                    modifier = Modifier.size(4.dp)
                )

                Text(
                    text = memory.date,
                    color = MemorySoftText,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun MemoryEditorDialog(
    title: String,
    description: String,
    date: String,
    selectedImage: Uri?,
    errorMessage: String,
    isEdit: Boolean,
    onChooseImage: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (
        String,
        String,
        String,
        (Boolean) -> Unit
    ) -> Unit
) {

    var titleText by remember(title) {
        mutableStateOf(title)
    }

    var descriptionText by remember(description) {
        mutableStateOf(description)
    }

    var dateText by remember(date) {
        mutableStateOf(
            if (date.isBlank()) {
                jalaliToday()
            } else {
                date
            }
        )
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        },

        title = {
            Text(
                text = if (isEdit) {
                    "ویرایش خاطره ❤️"
                } else {
                    "خاطره‌ی جدید ❤️"
                },
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .background(
                            Color(0xFFFFF0F4)
                        )
                        .clickable {
                            if (!isSaving) {
                                onChooseImage()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {

                    if (selectedImage != null) {

                        AsyncImage(
                            model = selectedImage,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = MemoryPink,
                                modifier = Modifier.size(42.dp)
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = if (isEdit) {
                                    "انتخاب عکس جدید"
                                } else {
                                    "انتخاب عکس"
                                },
                                color = MemoryDeepPink,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "اختیاری",
                                color = MemorySoftText,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                OutlinedTextField(
                    value = titleText,
                    onValueChange = {
                        titleText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("عنوان خاطره")
                    },
                    singleLine = true,
                    enabled = !isSaving
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = dateText,
                    onValueChange = {
                        dateText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("تاریخ شمسی")
                    },
                    singleLine = true,
                    enabled = !isSaving
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = {
                        descriptionText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("توضیح کوتاه")
                    },
                    maxLines = 3,
                    enabled = !isSaving
                )

                if (errorMessage.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = errorMessage,
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (isSaving) {

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "در حال ذخیره... ❤️",
                        color = MemoryDeepPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                enabled =
                    !isSaving &&
                        titleText.isNotBlank(),

                onClick = {

                    isSaving = true

                    onSave(
                        titleText.trim(),
                        descriptionText.trim(),
                        dateText.trim()
                    ) {
                        isSaving = it
                    }
                }
            ) {

                Text(
                    text = if (isSaving) {
                        "در حال ذخیره..."
                    } else {
                        if (isEdit) {
                            "ذخیره تغییرات"
                        } else {
                            "ذخیره خاطره"
                        }
                    },
                    color = MemoryDeepPink
                )
            }
        },

        dismissButton = {

            TextButton(
                enabled = !isSaving,
                onClick = onDismiss
            ) {

                Text("لغو")
            }
        }
    )
}

@Composable
private fun ToastMessage(
    message: String
) {
    LaunchedEffect(message) {
        // فقط برای جلوگیری از خطای state
    }
}

private fun jalaliToday(): String {

    val calendar =
        java.util.Calendar.getInstance()

    val gy =
        calendar.get(
            java.util.Calendar.YEAR
        )

    val gm =
        calendar.get(
            java.util.Calendar.MONTH
        ) + 1

    val gd =
        calendar.get(
            java.util.Calendar.DAY_OF_MONTH
        )

    val result =
        gregorianToJalali(
            gy,
            gm,
            gd
        )

    return String.format(
        Locale.US,
        "%04d/%02d/%02d",
        result[0],
        result[1],
        result[2]
    )
}

private fun gregorianToJalali(
    gy: Int,
    gm: Int,
    gd: Int
): IntArray {

    val gDaysInMonth =
        intArrayOf(
            31, 28, 31, 30, 31, 30,
            31, 31, 30, 31, 30, 31
        )

    val jDaysInMonth =
        intArrayOf(
            31, 31, 31, 31, 31, 31,
            30, 30, 30, 30, 30, 29
        )

    var gyTemp = gy
    var jy: Int

    if (gyTemp > 1600) {
        jy = 979
        gyTemp -= 1600
    } else {
        jy = 0
        gyTemp -= 621
    }

    val gy2 =
        if (gm > 2) {
            gyTemp + 1
        } else {
            gyTemp
        }

    var days =
        365 * gyTemp +
            (gy2 + 3) / 4 -
            (gy2 + 99) / 100 +
            (gy2 + 399) / 400 -
            80 +
            gd

    for (i in 0 until gm - 1) {
        days += gDaysInMonth[i]
    }

    jy += 33 * (days / 12053)
    days %= 12053

    jy += 4 * (days / 1461)
    days %= 1461

    if (days > 365) {
        jy += (days - 1) / 365
        days = (days - 1) % 365
    }

    val jm: Int
    val jd: Int

    if (days < 186) {
        jm = 1 + days / 31
        jd = 1 + days % 31
    } else {
        jm = 7 + (days - 186) / 30
        jd = 1 + (days - 186) % 30
    }

    return intArrayOf(
        jy,
        jm,
        jd
    )
}
