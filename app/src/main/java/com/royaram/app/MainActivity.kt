package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Pink = Color(0xFFE85D86)
private val DeepPink = Color(0xFFB83260)
private val LightPink = Color(0xFFFFE7EF)
private val VeryLightPink = Color(0xFFFFF4F8)
private val SoftPink = Color(0xFFFFD1DE)
private val TextDark = Color(0xFF34252B)
private val SoftText = Color(0xFF8A737C)
private val White = Color.White

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
            MaterialTheme {

                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.ui.unit.LayoutDirectionAmbient provides LayoutDirection.Rtl
                ) {
                    RoyaramApp(
                        onChatClick = {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    ChatActivity::class.java
                                )
                            )
                        },
                        onToast = { message ->
                            Toast.makeText(
                                this@MainActivity,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RoyaramApp(
    onChatClick: () -> Unit,
    onToast: (String) -> Unit
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
            onChatClick = onChatClick,
            onToast = onToast
        )
    }
}

@Composable
fun RoyaramHome(
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit,
    onToast: (String) -> Unit
) {

    val items = listOf(

        HomeItem(
            title = "خاطرات ما",
            subtitle = "لحظه‌های قشنگمون",
            emoji = "📸"
        ),

        HomeItem(
            title = "نامه‌های عاشقانه",
            subtitle = "حرف‌هایی از قلبمون",
            emoji = "💌"
        ),

        HomeItem(
            title = "آهنگ ما",
            subtitle = "صدای خاطره‌هامون",
            emoji = "🎵"
        ),

        HomeItem(
            title = "وقتی دلمون گرفت",
            subtitle = "اینجا همیشه کنار همیم",
            emoji = "🧸"
        ),

        HomeItem(
            title = "چت دونفره",
            subtitle = "حرف‌های من و تو ❤️",
            emoji = "💬"
        ),

        HomeItem(
            title = "بخش خصوصی",
            subtitle = "فقط برای من و تو 🔐",
            emoji = "🔐"
        ),

        HomeItem(
            title = "لحظه‌های ویژه",
            subtitle = "تاریخ‌های مهممون",
            emoji = "🎁"
        ),

        HomeItem(
            title = "تنظیمات",
            subtitle = "شخصی‌سازی رویارام",
            emoji = "⚙️"
        )
    )

    val daysTogether = remember {
        calculateDaysTogether()
    }

    val infiniteTransition = rememberInfiniteTransition(
        label = "heart"
    )

    val heartScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 12.dp,
            bottom = 30.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {

            TopHeader(
                onSettingsClick = {
                    onToast("تنظیمات رویارام ❤️")
                }
            )
        }

        item {

            HeroPhotoCard(
                daysTogether = daysTogether,
                heartScale = heartScale
            )
        }

        item {

            RelationshipCounterCard(
                daysTogether = daysTogether,
                heartScale = heartScale
            )
        }

        item {

            Text(
                text = "دنیای دونفره‌ی ما ❤️",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 2.dp,
                        end = 8.dp
                    ),
                textAlign = TextAlign.Right,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        items(
            items.chunked(2)
        ) { rowItems ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                rowItems.forEach { item ->

                    HomeCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = {

                            when (item.title) {

                                "خاطرات ما" -> {
                                    onMemoriesClick()
                                }

                                "چت دونفره" -> {
                                    onChatClick()
                                }

                                "نامه‌های عاشقانه" -> {
                                    onToast(
                                        "نامه‌های عاشقانه به‌زودی ❤️"
                                    )
                                }

                                "آهنگ ما" -> {
                                    onToast(
                                        "آهنگ ما به‌زودی 🎵"
                                    )
                                }

                                "وقتی دلمون گرفت" -> {
                                    onToast(
                                        "هر وقت دلت گرفت، اینجا کنارته ❤️"
                                    )
                                }

                                "بخش خصوصی" -> {
                                    onToast(
                                        "بخش خصوصی 🔐"
                                    )
                                }

                                "لحظه‌های ویژه" -> {
                                    onToast(
                                        "تاریخ‌های مهممون 🎁"
                                    )
                                }

                                "تنظیمات" -> {
                                    onToast(
                                        "تنظیمات رویارام ⚙️"
                                    )
                                }
                            }
                        }
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {

            TodayMessageCard()
        }

        item {

            Text(
                text = "« بعضی آدم‌ها فقط وارد زندگی‌مون نمی‌شن،\nبلکه تبدیل به خودِ زندگی می‌شن. »",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 6.dp,
                        bottom = 10.dp
                    ),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                lineHeight = 25.sp,
                color = SoftText
            )
        }
    }
}

@Composable
fun TopHeader(
    onSettingsClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 4.dp,
                bottom = 2.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(
                    elevation = 7.dp,
                    shape = RoundedCornerShape(50)
                )
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFFFD8E5)
                        )
                    )
                )
                .clickable {
                    onSettingsClick()
                },
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "⚙️",
                fontSize = 29.sp
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "♡ رویارام",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Text(
                text = "قصه‌ی من و تو، برای همیشه",
                fontSize = 13.sp,
                color = SoftText
            )
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
        label = "photoScale"
    )

    val photoOffset by animateFloatAsState(
        targetValue = if (animationStarted) -4f else 0f,
        animationSpec = tween(
            durationMillis = 2600,
            easing = FastOutSlowInEasing
        ),
        label = "photoOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(285.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(34.dp)
            )
            .clip(
                RoundedCornerShape(34.dp)
            )
    ) {

        AsyncImage(
            model = com.royaram.app.R.drawable.couple_main,
            contentDescription = "رامین و رویا",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = photoScale
                    scaleY = photoScale
                    translationY = photoOffset
                },
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x99000000)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = 22.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "رامین  ❤️  رویا",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "هر روز یک صفحه‌ی تازه از قصه‌ی ما",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.92f)
            )
        }

        if (showHearts) {

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        scaleX = heartScale
                        scaleY = heartScale
                        alpha = 0.95f
                    }
            ) {

                Text(
                    text = "❤️   ❤️",
                    fontSize = 29.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
                .clip(
                    RoundedCornerShape(50)
                )
                .background(
                    Color.White.copy(alpha = 0.9f)
                )
                .padding(
                    horizontal = 15.dp,
                    vertical = 9.dp
                )
        ) {

            Text(
                text = "❤️ $daysTogether روز",
                color = DeepPink,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun RelationshipCounterCard(
    daysTogether: Long,
    heartScale: Float
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 9.dp,
                shape = RoundedCornerShape(32.dp)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "تاریخ آشنایی",
                    fontSize = 12.sp,
                    color = SoftText
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "۱۴۰۵/۰۳/۲۰",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepPink
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = "♡",
                    color = Pink,
                    fontSize = 22.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(70.dp)
                    )
                    .clip(
                        RoundedCornerShape(70.dp)
                    )
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFFFDCE7)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "❤️",
                        fontSize = 30.sp,
                        modifier = Modifier.graphicsLayer {
                            scaleX = heartScale
                            scaleY = heartScale
                        }
                    )

                    Text(
                        text = daysTogether.toString(),
                        fontSize = 31.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepPink
                    )

                    Text(
                        text = "روز کنار هم",
                        fontSize = 11.sp,
                        color = SoftText
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "هر روز",
                    fontSize = 12.sp,
                    color = SoftText
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "عاشق‌تر شدن",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepPink
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = "♡",
                    color = Pink,
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
fun HomeCard(
    item: HomeItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .aspectRatio(0.92f)
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(27.dp)
            )
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(27.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.90f)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF8FA),
                            Color(0xFFFFEAF1)
                        )
                    )
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = item.emoji,
                fontSize = 37.sp
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = item.subtitle,
                    fontSize = 10.sp,
                    color = SoftText,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(
                        RoundedCornerShape(50)
                    )
                    .background(
                        Color(0xFFFFDCE7)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "‹",
                    fontSize = 21.sp,
                    color = DeepPink,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TodayMessageCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.90f)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "💗",
                fontSize = 29.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "پیام امروز برای تو",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = DeepPink
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "اگر امروز دنیا کمی سخت بود،\nیادت باشه یک نفر هست که دلش می‌خواد لبخندت رو ببینه.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 23.sp,
                color = TextDark
            )
        }
    }
}

@Composable
fun MemoriesScreen(
    onBack: () -> Unit
) {

    var memories by remember {
        mutableStateOf<List<Memory>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    val db = remember {
        FirebaseFirestore.getInstance()
    }

    LaunchedEffect(Unit) {

        db.collection("memories")
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
                        title = document.getString("title")
                            ?: "خاطره‌ی ما",
                        date = document.getString("date")
                            ?: "",
                        description = document.getString("description")
                            ?: "",
                        imageUrl = document.getString("imageUrl")
                            ?: ""
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
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp,
                        vertical = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(
                            RoundedCornerShape(50)
                        )
                        .background(
                            Color.White.copy(alpha = 0.9f)
                        )
                        .clickable {
                            onBack()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "→",
                        fontSize = 25.sp,
                        color = DeepPink
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "خاطرات ما 📸",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        text = "لحظه‌هایی که هیچ‌وقت فراموش نمی‌کنیم",
                        fontSize = 11.sp,
                        color = SoftText
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(
                            RoundedCornerShape(50)
                        )
                        .background(
                            DeepPink
                        )
                        .clickable {
                            showAddDialog = true
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "+",
                        fontSize = 29.sp,
                        color = Color.White
                    )
                }
            }

            if (loading) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = DeepPink
                    )
                }

            } else if (memories.isEmpty()) {

                EmptyMemories()

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        bottom = 30.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
fun EmptyMemories() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "📸",
                fontSize = 70.sp
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "هنوز خاطره‌ای ثبت نشده",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = "اولین خاطره‌ی قشنگتون رو اضافه کنید ❤️",
                fontSize = 13.sp,
                color = SoftText,
                textAlign = TextAlign.Center
            )
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
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.94f)
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            if (memory.imageUrl.isNotBlank()) {

                AsyncImage(
                    model = memory.imageUrl,
                    contentDescription = memory.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 28.dp
                            )
                        ),
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
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepPink
                )

                if (memory.date.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "📅 ${memory.date}",
                        fontSize = 11.sp,
                        color = SoftText
                    )
                }

                if (memory.description.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = memory.description,
                        fontSize = 14.sp,
                        lineHeight = 23.sp,
                        color = TextDark
                    )
                }

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                Text(
                    text = "رامین ❤️ رویا",
                    fontSize = 11.sp,
                    color = Pink,
                    fontWeight = FontWeight.Bold
                )
            }
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
                text = "افزودن خاطره ❤️",
                fontWeight = FontWeight.Bold,
                color = DeepPink
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        max = 430.dp
                    )
                    .verticalScroll(
                        rememberScrollState()
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("عنوان خاطره")
                    },
                    placeholder = {
                        Text("مثلاً اولین قرارمون")
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
                        Text("مثلاً ۱۴۰۵/۰۳/۲۰")
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
                        .heightIn(
                            min = 110.dp
                        ),
                    label = {
                        Text("توضیح خاطره")
                    },
                    placeholder = {
                        Text(
                            "اینجا از این خاطره‌ی قشنگ برامون بنویس..."
                        )
                    },
                    minLines = 4,
                    maxLines = 6
                )
            }
        },

        confirmButton = {

            TextButton(
                enabled = !saving && title.isNotBlank(),
                onClick = {

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
                    text = "لغو"
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
        difference /
                (1000L * 60L * 60L * 24L)

    return if (days < 0) {
        0L
    } else {
        days + 1
    }
}
