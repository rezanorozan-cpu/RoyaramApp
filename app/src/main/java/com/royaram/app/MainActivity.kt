package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.Date

private val Pink = Color(0xFFE85D86)
private val DeepPink = Color(0xFFB83260)
private val LightPink = Color(0xFFFFE7EF)
private val VeryLightPink = Color(0xFFFFF4F8)
private val SoftPink = Color(0xFFFFD1DE)
private val TextDark = Color(0xFF34252B)
private val SoftText = Color(0xFF8A737C)

private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFE7EF),
        Color(0xFFFFF2F7),
        Color(0xFFFFF8FA)
    )
)

data class HomeItem(
    val title: String,
    val subtitle: String,
    val emoji: String
)

data class Memory(
    val id: String,
    val title: String,
    val date: String,
    val description: String,
    val imageUrl: String
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Rtl
            ) {
                MaterialTheme {
                    RoyaramApp(
                        onChatClick = {
                            try {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        ChatActivity::class.java
                                    )
                                )
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "صفحه چت هنوز آماده نیست ❤️",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RoyaramApp(
    onChatClick: () -> Unit
) {
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
            },
            onChatClick = onChatClick
        )
    }
}

@Composable
fun RoyaramHome(
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit
) {
    val daysTogether = remember {
        calculateDaysTogether()
    }

    var heartPulse by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        while (true) {
            heartPulse = !heartPulse
            kotlinx.coroutines.delay(900)
        }
    }

    val heartScale by animateFloatAsState(
        targetValue = if (heartPulse) 1.08f else 0.94f,
        animationSpec = tween(
            durationMillis = 900,
            easing = FastOutSlowInEasing
        ),
        label = "heart_scale"
    )

    val items = remember {
        listOf(
            HomeItem(
                "خاطرات ما",
                "لحظه‌هایی که موندگار شدن",
                "📸"
            ),
            HomeItem(
                "نامه‌های عاشقانه",
                "حرف‌هایی از قلبمون",
                "💌"
            ),
            HomeItem(
                "آهنگ ما",
                "صدای خاطره‌هامون",
                "🎵"
            ),
            HomeItem(
                "وقتی دلمون گرفت",
                "اینجا همیشه کنار همیم",
                "🧸"
            ),
            HomeItem(
                "چت دونفره",
                "حرف‌های من و تو",
                "💬"
            ),
            HomeItem(
                "بخش خصوصی",
                "فقط برای خودمون",
                "🔐"
            ),
            HomeItem(
                "لحظه‌های ویژه",
                "تولدها و سالگردهامون",
                "🎁"
            ),
            HomeItem(
                "تنظیمات",
                "شخصی‌سازی رویارام",
                "⚙️"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(
                top = 18.dp,
                bottom = 30.dp
            )
    ) {
        TopHeader()

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        HeroPhotoCard(
            daysTogether = daysTogether,
            heartScale = heartScale
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        RelationshipCounterCard(
            daysTogether = daysTogether
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "دنیای دوتایی ما",
            color = TextDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Right
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (row in items.chunked(2)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { item ->

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            HomeCard(
                                item = item,
                                onClick = {
                                    when (item.title) {
                                        "خاطرات ما" -> {
                                            onMemoriesClick()
                                        }

                                        "چت دونفره" -> {
                                            onChatClick()
                                        }

                                        else -> {
                                        }
                                    }
                                }
                            )
                        }
                    }

                    if (row.size == 1) {
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        TodayMessageCard()

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Text(
            text = "رامین ❤️ رویا",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = DeepPink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "با عشق ساخته شده برای ما",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = SoftText,
            fontSize = 12.sp
        )
    }
}

@Composable
fun TopHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "♡ رویارام",
                color = DeepPink,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "جایی برای من و تو ❤️",
                color = SoftText,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        }

        Card(
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.75f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚙️",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun HeroPhotoCard(
    daysTogether: Long,
    heartScale: Float
) {
    var animationStarted by remember {
        mutableStateOf(false)
    }

    var showHearts by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(400)
        animationStarted = true

        kotlinx.coroutines.delay(1800)
        showHearts = true
    }

    val photoScale by animateFloatAsState(
        targetValue = if (animationStarted) 1.055f else 1f,
        animationSpec = tween(
            durationMillis = 2600,
            easing = FastOutSlowInEasing
        ),
        label = "photo_scale"
    )

    val photoOffset by animateFloatAsState(
        targetValue = if (animationStarted) -4f else 0f,
        animationSpec = tween(
            durationMillis = 2600,
            easing = FastOutSlowInEasing
        ),
        label = "photo_offset"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(285.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            AsyncImage(
                model = R.drawable.couple_main,
                contentDescription = "رامین و رویا",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = photoScale
                        scaleY = photoScale
                        translationY = photoOffset
                    }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.64f)
                            )
                        )
                    )
            )

            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp),
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.88f)
                )
            ) {
                Text(
                    text = "❤️ $daysTogether روز",
                    color = DeepPink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = 13.dp,
                        vertical = 8.dp
                    )
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        horizontal = 18.dp,
                        vertical = 18.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "رامین ❤️ رویا",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "هر لحظه کنار هم، یک خاطره قشنگ‌تر",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 12.sp
                )
            }

            if (showHearts) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            alpha = 0.95f
                            scaleX = heartScale
                            scaleY = heartScale
                        }
                ) {
                    Text(
                        text = "❤️   ❤️",
                        color = Color.White,
                        fontSize = 28.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RelationshipCounterCard(
    daysTogether: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.84f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "داستان ما",
                color = DeepPink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "شروع قشنگمون • ۱۴۰۵/۰۳/۲۰",
                color = SoftText,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = daysTogether.toString(),
                color = DeepPink,
                fontSize = 43.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "روز کنار هم",
                color = TextDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = "و هنوز کلی خاطره برای ساختن داریم ❤️",
                color = SoftText,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HomeCard(
    item: HomeItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.78f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.emoji,
                fontSize = 36.sp
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Text(
                text = item.title,
                color = TextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = item.subtitle,
                color = SoftText,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "‹",
                color = Pink,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TodayMessageCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.84f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "حرف امروز ❤️",
                color = DeepPink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "بعضی آدم‌ها فقط وارد زندگی‌مون نمی‌شن...\nمی‌شن قشنگ‌ترین قسمت زندگی‌مون.",
                color = TextDark,
                fontSize = 14.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MemoriesScreen(
    onBack: () -> Unit
) {
    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    var memories by remember {
        mutableStateOf<List<Memory>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        firestore.collection("memories")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    loading = false
                    return@addSnapshotListener
                }

                memories = snapshot?.documents?.map { document ->
                    Memory(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        date = document.getString("date") ?: "",
                        description = document.getString("description") ?: "",
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                } ?: emptyList()

                loading = false
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            onBack()
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.82f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "→",
                            fontSize = 25.sp,
                            color = DeepPink
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "خاطرات ما 📸",
                        color = DeepPink,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "لحظه‌هایی که موندگار شدن",
                        color = SoftText,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (loading) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Pink
                    )
                }

            } else if (memories.isEmpty()) {

                EmptyMemories(
                    onAdd = {
                        showAddDialog = true
                    }
                )

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        bottom = 100.dp
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

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(18.dp)
                .clickable {
                    showAddDialog = true
                }
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = DeepPink
            )
        ) {
            Text(
                text = "＋ افزودن خاطره",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = 22.dp,
                    vertical = 13.dp
                )
            )
        }
    }

    if (showAddDialog) {
        AddMemoryDialog(
            onDismiss = {
                showAddDialog = false
            }
        )
    }
}

@Composable
fun EmptyMemories(
    onAdd: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "📸",
                fontSize = 58.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "هنوز خاطره‌ای ثبت نکردیم",
                color = TextDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "اولین خاطره قشنگمون رو ثبت کنیم ❤️",
                color = SoftText,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Card(
                modifier = Modifier.clickable {
                    onAdd()
                },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DeepPink
                )
            ) {
                Text(
                    text = "ثبت اولین خاطره",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    )
                )
            }
        }
    }
}

@Composable
fun MemoryCard(
    memory: Memory
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.86f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            if (memory.imageUrl.isNotBlank()) {

                AsyncImage(
                    model = memory.imageUrl,
                    contentDescription = memory.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = 160.dp,
                            max = 260.dp
                        )
                        .clip(
                            RoundedCornerShape(18.dp)
                        )
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            Text(
                text = memory.title,
                color = TextDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (memory.date.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "📅 ${memory.date}",
                    color = Pink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (memory.description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = memory.description,
                    color = SoftText,
                    fontSize = 13.sp,
                    lineHeight = 21.sp
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "رامین ❤️ رویا",
                color = DeepPink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left
            )
        }
    }
}

@Composable
fun AddMemoryDialog(
    onDismiss: () -> Unit
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

    var saving by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = {
            if (!saving) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = "ثبت خاطره جدید ❤️",
                color = DeepPink,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                )
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = {
                        Text("عنوان خاطره")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                    },
                    label = {
                        Text("تاریخ")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("توضیح خاطره")
                    },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !saving,
                onClick = {

                    if (title.trim().isEmpty()) {
                        return@TextButton
                    }

                    saving = true

                    val memory = hashMapOf<String, Any>(
                        "title" to title.trim(),
                        "date" to date.trim(),
                        "description" to description.trim(),
                        "createdBy" to "رامین و رویا",
                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("memories")
                        .add(memory)
                        .addOnSuccessListener {
                            saving = false
                            onDismiss()
                        }
                        .addOnFailureListener {
                            saving = false
                        }
                }
            ) {
                Text(
                    text = if (saving) {
                        "در حال ذخیره..."
                    } else {
                        "ذخیره ❤️"
                    },
                    color = DeepPink
                )
            }
        },
        dismissButton = {
            TextButton(
                enabled = !saving,
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    text = "لغو",
                    color = SoftText
                )
            }
        }
    )
}

fun calculateDaysTogether(): Long {

    val calendar = Calendar.getInstance()

    calendar.set(
        2026,
        Calendar.JUNE,
        10,
        0,
        0,
        0
    )

    calendar.set(
        Calendar.MILLISECOND,
        0
    )

    val startDate = calendar.time
    val today = Date()

    val difference =
        today.time - startDate.time

    val days =
        difference / (
            1000L *
                    60L *
                    60L *
                    24L
            )

    return if (days < 0) {
        0L
    } else {
        days + 1
    }
}
