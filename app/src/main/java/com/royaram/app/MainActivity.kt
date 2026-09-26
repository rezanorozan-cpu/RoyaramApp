package com.royaram.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val id: String,
    val title: String,
    val date: String,
    val description: String,
    val createdBy: String,
    val createdAt: Long
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

    val context = LocalContext.current

    val startDate = java.time.LocalDate.of(
        2026,
        6,
        10
    )

    val today = java.time.LocalDate.now()

    val relationshipDays =
        java.time.temporal.ChronoUnit.DAYS.between(
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
                        "رویارام ❤️",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF402A30)
                    )

                    Spacer(
                        Modifier.height(4.dp)
                    )

                    Text(
                        "قصه‌ی من و تو، برای همیشه",
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
                        Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE85D75),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(
                Modifier.height(18.dp)
            )

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

                    androidx.compose.foundation.Image(
                        painter = painterResource(
                            R.drawable.couple_main
                        ),

                        contentDescription = "رامین و رویا",

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(
                                RoundedCornerShape(22.dp)
                            ),

                        contentScale = ContentScale.Crop
                    )

                    Spacer(
                        Modifier.height(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFFE85D75),
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(
                            Modifier.width(8.dp)
                        )

                        Text(
                            "رامین ❤️ رویا",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A3038)
                        )
                    }

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(
                        "هر روز یک خاطره‌ی تازه",
                        fontSize = 14.sp,
                        color = Color(0xFF795C64)
                    )

                    Spacer(
                        Modifier.height(10.dp)
                    )

                    Text(
                        "$relationshipDays روز کنار هم ❤️",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE85D75)
                    )

                    Spacer(
                        Modifier.height(4.dp)
                    )

                    Text(
                        "شروع قصه: ۲۰ خرداد ۱۴۰۵",
                        fontSize = 13.sp,
                        color = Color(0xFF795C64)
                    )

                    Spacer(
                        Modifier.height(12.dp)
                    )
                }
            }

            Spacer(
                Modifier.height(18.dp)
            )

            Text(
                "دنیای دونفره‌ی ما",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF402A30)
            )

            Spacer(
                Modifier.height(12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(items) { item ->

                    HomeCard(item) {

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
                                )
                            }
                        }
                    }
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
            containerColor = Color.White.copy(
                alpha = 0.94f
            )
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
                    item.icon,
                    contentDescription = null,
                    tint = Color(0xFFE85D75),
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(
                Modifier.height(12.dp)
            )

            Text(
                item.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF402A30)
            )

            Spacer(
                Modifier.height(4.dp)
            )

            Text(
                item.subtitle,
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

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val memories = remember {
        mutableStateListOf<Memory>()
    }

    var showAddMemory by remember {
        mutableStateOf(false)
    }

    var loading by remember {
        mutableStateOf(true)
    }

    /*
     * دریافت زنده خاطرات از Firestore
     */
    DisposableEffect(Unit) {

        val listener = firestore
            .collection("memories")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    loading = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val firebaseMemories =
                        snapshot.documents.mapNotNull { document ->

                            val title =
                                document.getString("title")
                                    ?: return@mapNotNull null

                            val date =
                                document.getString("date")
                                    ?: return@mapNotNull null

                            val description =
                                document.getString("description")
                                    ?: return@mapNotNull null

                            val createdBy =
                                document.getString("createdBy")
                                    ?: return@mapNotNull null

                            val createdAt =
                                document.getTimestamp("createdAt")
                                    ?.toDate()
                                    ?.time
                                    ?: 0L

                            Memory(
                                id = document.id,
                                title = title,
                                date = date,
                                description = description,
                                createdBy = createdBy,
                                createdAt = createdAt
                            )
                        }

                    memories.clear()
                    memories.addAll(
                        firebaseMemories
                    )
                }

                loading = false
            }

        onDispose {
            listener.remove()
        }
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
            Modifier.fillMaxSize()
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
                        Icons.Rounded.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color(0xFFE85D75),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(
                    Modifier.width(14.dp)
                )

                Column(
                    Modifier.weight(1f)
                ) {

                    Text(
                        "خاطرات ما ❤️",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF402A30)
                    )

                    Text(
                        "لحظه‌هایی که نمی‌خوایم فراموش کنیم",
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
                        Icons.Rounded.Add,
                        contentDescription = "افزودن خاطره",
                        tint = Color(0xFFE85D75),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            if (loading) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        "در حال دریافت خاطرات... ❤️",
                        fontSize = 15.sp,
                        color = Color(0xFF795C64)
                    )
                }

            } else if (memories.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFFE85D75),
                            modifier = Modifier.size(52.dp)
                        )

                        Spacer(
                            Modifier.height(12.dp)
                        )

                        Text(
                            "هنوز خاطره‌ای ثبت نشده ❤️",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF402A30)
                        )

                        Spacer(
                            Modifier.height(6.dp)
                        )

                        Text(
                            "اولین خاطره‌تون رو ثبت کنید",
                            fontSize = 13.sp,
                            color = Color(0xFF795C64)
                        )
                    }
                }

            } else {

                LazyColumn(
                    Modifier.fillMaxSize(),

                    contentPadding = PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        top = 8.dp,
                        bottom = 30.dp
                    ),

                    verticalArrangement = Arrangement.spacedBy(
                        18.dp
                    )
                ) {

                    items(
                        items = memories,
                        key = {
                            it.id
                        }
                    ) { memory ->

                        MemoryCard(
                            memory = memory
                        )
                    }
                }
            }
        }

        if (showAddMemory) {

            AddMemoryDialog(
                onDismiss = {
                    showAddMemory = false
                },

                onSave = {
                    title,
                    date,
                    description ->

                    val user = auth.currentUser

                    if (user == null) {
                        return@AddMemoryDialog
                    }

                    val memoryData =
                        hashMapOf(
                            "title" to title.trim(),
                            "date" to date.trim(),
                            "description" to description.trim(),
                            "createdBy" to user.uid,
                            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                        )

                    firestore
                        .collection("memories")
                        .add(memoryData)
                        .addOnSuccessListener {

                            showAddMemory = false
                        }
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
        String
    ) -> Unit
) {

    var title by remember {
        mutableStateOf("")
    }

    var date by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(
                    alpha = 0.25f
                )
            ),

        contentAlignment = Alignment.Center
    ) {

        Card(
            Modifier
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
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                Text(
                    "خاطره‌ی جدید ❤️",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF402A30)
                )

                Spacer(
                    Modifier.height(16.dp)
                )

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

                Spacer(
                    Modifier.height(10.dp)
                )

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

                Spacer(
                    Modifier.height(10.dp)
                )

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

                Spacer(
                    Modifier.height(16.dp)
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp
                    )
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
                                    description
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
        Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            Modifier.fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFFFE1E9),
                                Color(0xFFFFF5F8)
                            )
                        )
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE85D75),
                    modifier = Modifier.size(60.dp)
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {

                Text(
                    memory.title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF402A30)
                )

                Spacer(
                    Modifier.height(7.dp)
                )

                Text(
                    memory.date,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE85D75)
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                Text(
                    memory.description,
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFF6F5960)
                )
            }
        }
    }
}
