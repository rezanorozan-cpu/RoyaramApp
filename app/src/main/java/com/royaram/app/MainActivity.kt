package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.AlertDialog
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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

    val homeItems = listOf(

        HomeItem(
            title = "خاطرات ما",
            subtitle = "لحظه‌های قشنگمون",
            icon = Icons.Rounded.PhotoLibrary
        ),

        HomeItem(
            title = "نامه‌های عاشقانه",
            subtitle = "حرف‌هایی از قلبمون",
            icon = Icons.Rounded.Mail
        ),

        HomeItem(
            title = "آهنگ ما",
            subtitle = "صدای خاطره‌هامون",
            icon = Icons.Rounded.MusicNote
        ),

        HomeItem(
            title = "وقتی دلمون گرفت",
            subtitle = "اینجا همیشه کنار همیم",
            icon = Icons.Rounded.SentimentSatisfiedAlt
        ),

        HomeItem(
            title = "چت دونفره",
            subtitle = "حرف‌های من و تو 💬❤️",
            icon = Icons.Rounded.Favorite
        ),

        HomeItem(
            title = "لحظه‌های خاص",
            subtitle = "تاریخ‌های مهم ما ✨",
            icon = Icons.Rounded.FavoriteBorder
        ),

        HomeItem(
            title = "بخش خصوصی",
            subtitle = "فقط برای من و تو 🔐",
            icon = Icons.Rounded.Favorite
        ),

        HomeItem(
            title = "تنظیمات",
            subtitle = "شخصی‌سازی رویارام ⚙️",
            icon = Icons.Rounded.FavoriteBorder
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFE3EB),
                        Color(0xFFFFF4F7),
                        Color.White
                    )
                )
            )
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 28.dp,
                bottom = 32.dp
            ),

            verticalArrangement = Arrangement.spacedBy(
                16.dp
            )
        ) {

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            text = "رویارام ❤️",
                            fontSize = 31.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF402A30)
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "قصه‌ی من و تو، برای همیشه",
                            fontSize = 14.sp,
                            color = Color(0xFF795C64)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                Color.White.copy(alpha = 0.9f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE85D75),
                            modifier = Modifier.size(29.dp)
                        )
                    }
                }
            }

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(30.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 7.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Image(
                            painter = painterResource(
                                R.drawable.couple_main
                            ),

                            contentDescription = "رامین و رویا",

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(245.dp)
                                .clip(
                                    RoundedCornerShape(24.dp)
                                ),

                            contentScale = ContentScale.Crop
                        )

                        Spacer(
                            modifier = Modifier.height(15.dp)
                        )

                        Text(
                            text = "رامین ❤️ رویا",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF402A30)
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text = "هر روز، یک صفحه‌ی تازه از قصه‌ی ما",
                            fontSize = 13.sp,
                            color = Color(0xFF795C64)
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(
                                    RoundedCornerShape(20.dp)
                                )
                                .background(
                                    Color(0xFFFFE8EE)
                                )
                                .padding(
                                    vertical = 14.dp
                                ),

                            contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = relationshipDays.toString(),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE85D75)
                                )

                                Text(
                                    text = "روز کنار هم ❤️",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF795C64)
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "شروع قصه: ۲۰ خرداد ۱۴۰۵",
                            fontSize = 12.sp,
                            color = Color(0xFF8B7078)
                        )
                    }
                }
            }

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(22.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF0F4)
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {

                        Text(
                            text = "جمله‌ی امروز برای ما 💌",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF402A30)
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.» ❤️",
                            fontSize = 14.sp,
                            lineHeight = 23.sp,
                            color = Color(0xFF795C64)
                        )
                    }
                }
            }

            item {

                Text(
                    text = "دنیای دونفره‌ی ما",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF402A30)
                )
            }

            item {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(620.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),

                    userScrollEnabled = false
                ) {

                    items(homeItems) { item ->

                        HomeCard(
                            item = item
                        ) {

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

                                else -> {

                                    Toast.makeText(
                                        context,
                                        "این بخش به‌زودی به رویارام اضافه می‌شود ❤️",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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

            verticalArrangement =
                Arrangement.Center
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

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = item.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF402A30)
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

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

    val context = LocalContext.current

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

                    Toast.makeText(
                        context,
                        "خطا در دریافت خاطرات",
                        Toast.LENGTH_SHORT
                    ).show()

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
                                    ?: ""

                            val createdBy =
                                document.getString("createdBy")
                                    ?: ""

                            val createdAt =
                                document
                                    .getTimestamp("createdAt")
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

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            onBack()
                        },

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Rounded.ArrowBack,

                        contentDescription =
                            "بازگشت",

                        tint =
                            Color(0xFFE85D75),

                        modifier =
                            Modifier.size(26.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

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
                        contentDescription =
                            "افزودن خاطره",

                        tint =
                            Color(0xFFE85D75),

                        modifier =
                            Modifier.size(30.dp)
                    )
                }
            }

            if (loading) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "در حال دریافت خاطرات... ❤️",
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
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector =
                                Icons.Rounded.FavoriteBorder,

                            contentDescription = null,

                            tint =
                                Color(0xFFE85D75),

                            modifier =
                                Modifier.size(64.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        Text(
                            text = "هنوز خاطره‌ای ثبت نکردیم ❤️",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF402A30)
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "اولین خاطره‌مون رو همین حالا ثبت کنیم",
                            fontSize = 13.sp,
                            color = Color(0xFF795C64)
                        )

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        Button(
                            onClick = {
                                showAddMemory = true
                            },

                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    Color(0xFFE85D75)
                            ),

                            shape =
                                RoundedCornerShape(18.dp)
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Rounded.Add,

                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(7.dp)
                            )

                            Text(
                                text = "افزودن اولین خاطره"
                            )
                        }
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),

                    contentPadding =
                        PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 30.dp
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    items(
                        items = memories,
                        key = { memory ->
                            memory.id
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
                }
            )
        }
    }
}

@Composable
private fun MemoryCard(
    memory: Memory
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0xFFFFE3EB),
                            CircleShape
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Rounded.Favorite,

                        contentDescription = null,

                        tint =
                            Color(0xFFE85D75),

                        modifier =
                            Modifier.size(25.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = memory.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF402A30)
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = memory.date,
                        fontSize = 12.sp,
                        color = Color(0xFFE85D75)
                    )
                }
            }

            if (memory.description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(13.dp)
                )

                Text(
                    text = memory.description,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF795C64)
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
    text = "ثبت شده توسط ما ❤️",
    fontSize = 12.sp,
    color = Color(0xFF9A7A83)
)
