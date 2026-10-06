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
    val createdAt: Long = System.currentTimeMillis()
)

@Composable
fun MemoriesScreen(
    onBack: () -> Unit
) {
    val firestore = remember { FirebaseFirestore.getInstance() }
    val storage = remember { FirebaseStorage.getInstance() }

    var memories by remember { mutableStateOf<List<LuxuryMemory>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }

    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
) { uri ->
    selectedImage = uri
}

    LaunchedEffect(Unit) {
        firestore.collection("memories")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    memories = snapshot.documents.mapNotNull { document ->
                        document.toObject(LuxuryMemory::class.java)?.copy(
                            id = document.id
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

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "هنوز خاطره‌ای ثبت نشده",
                            color = MemoryText,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = "اولین لحظه قشنگتون رو به آلبوم اضافه کنید ❤️",
                            color = MemorySoftText,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                selectedImage = null
                                showAddDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.size(6.dp))

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
                            memory = memory
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {

        AddMemoryDialog(
            selectedImage = selectedImage,
            onChooseImage = {
    imagePicker.launch(
        PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.ImageOnly
        )
    )
},
            onDismiss = {
                showAddDialog = false
                selectedImage = null
            },
            onSave = { title, description, date ->

                val imageUri = selectedImage

                if (imageUri == null) {
                    return@AddMemoryDialog
                }

                val fileName = "memories/${UUID.randomUUID()}.jpg"

                storage.reference
                    .child(fileName)
                    .putFile(imageUri)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            throw task.exception
                                ?: Exception("خطا در آپلود عکس")
                        }

                        storage.reference
                            .child(fileName)
                            .downloadUrl
                    }
                    .addOnSuccessListener { downloadUri ->

                        val memory = LuxuryMemory(
                            title = title,
                            description = description,
                            date = date,
                            imageUrl = downloadUri.toString(),
                            createdAt = System.currentTimeMillis()
                        )

                        firestore.collection("memories")
                            .add(memory)

                        showAddDialog = false
                        selectedImage = null
                    }
            }
        )
    }
}

@Composable
private fun MemoryCard(
    memory: LuxuryMemory
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.92f)
        ) {

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

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(9.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MemoryPink,
                    modifier = Modifier.size(17.dp)
                )
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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = memory.description,
                    color = MemorySoftText,
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MemoryPink,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.size(4.dp))

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
private fun AddMemoryDialog(
    selectedImage: Uri?,
    onChooseImage: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val today = remember {
        SimpleDateFormat(
            "yyyy/MM/dd",
            Locale.getDefault()
        ).format(Date())
    }

    var date by remember { mutableStateOf(today) }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "خاطره‌ی جدید ❤️",
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFF0F4))
                        .clickable {
                            onChooseImage()
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
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = MemoryPink,
                                modifier = Modifier.size(42.dp)
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "انتخاب عکس",
                                color = MemoryDeepPink,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("عنوان خاطره")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("توضیح کوتاه")
                    },
                    maxLines = 3
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {
                    if (
                        selectedImage != null &&
                        title.isNotBlank()
                    ) {
                        onSave(
                            title.trim(),
                            description.trim(),
                            date.trim()
                        )
                    }
                }
            ) {
                Text(
                    text = "ذخیره خاطره",
                    color = MemoryDeepPink
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("لغو")
            }
        }
    )
}
