package com.royaram.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.SentimentSatisfiedAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoyaramApp()
        }
    }
}

data class HomeItem(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class Memory(
    val title: String,
    val date: String,
    val description: String,
    val imageUri: String? = null
)

@Composable
private fun RoyaramApp() {

    var showMemories by remember {
        mutableStateOf(false)
    }

    if (showMemories) {

        MemoriesScreen(
            onBack = {
                showMemories = false
            }
        )

    } else {

        RoyaramHome(
            onMemoriesClick = {
                showMemories = true
            }
        )
    }
}

@Composable
private fun RoyaramHome(
    onMemoriesClick: () -> Unit
) {

    val startDate = LocalDate.of(2026, 6, 10)
    val today = LocalDate.now()

    val relationshipDays = ChronoUnit.DAYS.between(
        startDate,
        today
    )

   val items = listOf(
    HomeItem(
        "خاطرات ما",
        "لحظه‌های قشنگمون",
        Icons.Rounded.PhotoLibrary
    ),
    HomeItem(
        "نامه‌های عاشقانه",
        "حرف‌هایی از قلبمون",
        Icons.Rounded.Mail
    ),
    HomeItem(
        "آهنگ ما",
        "صدای خاطره‌هامون",
        Icons.Rounded.MusicNote
    ),
    HomeItem(
        "وقتی دلمون گرفت",
        "اینجا همیشه کنار همیم",
        Icons.Rounded.SentimentSatisfiedAlt
    ),
    HomeItem(
        "چت دونفره",
        "حرف‌های من و تو 💬❤️",
        Icons.Rounded.Favorite
    )
) 
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFE8EF),
                        Color(0xFFFFF5F8),
                        Color.White
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    top = 28.dp,
                    end = 20.dp,
                    bottom = 16.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {

                    Text(
                        text = "رویارام ❤️",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF402A30)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "قصه‌ی من و تو، برای همیشه",
                        fontSize = 14.sp,
                        color = Color(0xFF795C64)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            Color.White.copy(alpha = 0.85f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE85D75),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(
                            id = R.drawable.couple_main
                        ),
                        contentDescription = "رامین و رویا",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(22.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFFE85D75),
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "رامین ❤️ رویا",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A3038)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "هر روز یک خاطره‌ی تازه",
                        fontSize = 14.sp,
                        color = Color(0xFF795C64)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$relationshipDays روز کنار هم ❤️",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE85D75)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "شروع قصه: ۲۰ خرداد ۱۴۰۵",
                        fontSize = 13.sp,
                        color = Color(0xFF795C64)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "دنیای دونفره‌ی ما",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF402A30)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(items) { item ->

                    (
                        item = item,
                        onClick = {
                            when (item.title) {

        "خاطرات ما" -> {
            onMemoriesClick()
        }

        "چت دونفره" -> {
            context.startActivity(
                Intent(
                    context,
                    ChatActivity::class.java
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeCard(
    item: HomeItem,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.94f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFFFFE1E9),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Color(0xFFE85D75),
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF402A30)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.subtitle,
                fontSize = 12.sp,
                color = Color(0xFF795C64)
            )
        }
    }
}

@Composable
private fun MemoriesScreen(
    onBack: () -> Unit
) {

    var showAddMemory by remember {
        mutableStateOf(false)
    }

    val memories = remember {

        mutableStateListOf(
            Memory(
                title = "شروع قصه‌ی ما ❤️",
                date = "۲۰ خرداد ۱۴۰۵",
                description = "روزی که قصه‌ی من و تو شروع شد؛ روزی که قرار بود فقط یک تاریخ باشد، اما تبدیل شد به یکی از قشنگ‌ترین روزهای زندگی‌مون."
            ),
            Memory(
                title = "یک خاطره‌ی دونفره ❤️",
                date = "خاطره‌ی ما",
                description = "کنار هم، بدون اینکه دنیا عجله‌ای برای رفتن داشته باشد. بعضی لحظه‌ها فقط باید زندگی شوند و در قلب بمانند."
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFE8EF),
                        Color(0xFFFFF5F8),
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
                        start = 16.dp,
                        top = 26.dp,
                        end = 16.dp,
                        bottom = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            onBack()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color(0xFFE85D75),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "خاطرات ما ❤️",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF402A30)
                    )

                    Text(
                        text = "لحظه‌هایی که نمی‌خوایم فراموش کنیم",
                        fontSize = 13.sp,
                        color = Color(0xFF795C64)
                    )
                }

                IconButton(
                    onClick = {
                        showAddMemory = true
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            Color.White,
                            CircleShape
                        )
                ) {

                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "افزودن خاطره",
                        tint = Color(0xFFE85D75),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 8.dp,
                    bottom = 30.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                items(memories) { memory ->

                    MemoryCard(
                        memory = memory
                    )
                }
            }
        }

        if (showAddMemory) {

            AddMemoryDialog(
                onDismiss = {
                    showAddMemory = false
                },
                onSave = { title, date, description, imageUri ->

                    memories.add(
                        0,
                        Memory(
                            title = title,
                            date = date,
                            description = description,
                            imageUri = imageUri
                        )
                    )

                    showAddMemory = false
                }
            )
        }
    }
}

@Composable
private fun AddMemoryDialog(
    onDismiss: () -> Unit,
    onSave: (
        String,
        String,
        String,
        String?
    ) -> Unit
) {

    val context = LocalContext.current

    var title by remember {
        mutableStateOf("")
    }

    var date by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var selectedImage by remember {
        mutableStateOf<String?>(null)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {

            try {

                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

            } catch (_: Exception) {
            }

            selectedImage = uri.toString()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.25f)
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                Text(
                    text = "خاطره‌ی جدید ❤️",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF402A30)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        imagePicker.launch(
                            arrayOf("image/*")
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE85D75)
                    )
                ) {

                    Icon(
                        imageVector = Icons.Rounded.PhotoLibrary,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (selectedImage == null) {
                            "انتخاب عکس از گالری"
                        } else {
                            "عکس انتخاب شد ❤️"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

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

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("تاریخ خاطره")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    label = {
                        Text("توضیح خاطره")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {

                        Text("انصراف")
                    }

                    Button(
                        onClick = {

                            if (
                                title.isNotBlank() &&
                                date.isNotBlank() &&
                                description.isNotBlank()
                            ) {

                                onSave(
                                    title,
                                    date,
                                    description,
                                    selectedImage
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE85D75)
                        )
                    ) {

                        Text("ذخیره ❤️")
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoryCard(
    memory: Memory
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            if (memory.imageUri != null) {

                AsyncImage(
                    model = memory.imageUri,
                    contentDescription = memory.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )

            } else {

                Image(
                    painter = painterResource(
                        id = R.drawable.couple_main
                    ),
                    contentDescription = memory.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {

                Text(
                    text = memory.title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF402A30)
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = memory.date,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE85D75)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = memory.description,
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFF6F5960)
                )
            }
        }
    }
}
