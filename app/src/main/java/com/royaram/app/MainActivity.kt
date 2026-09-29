package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import java.util.Date

private val Pink = Color(0xFFF05A87)
private val PinkDark = Color(0xFFB52E5D)
private val PinkSoft = Color(0xFFFFD8E5)
private val Background = Color(0xFFFFE7F0)
private val TextDark = Color(0xFF3B2530)
private val SoftText = Color(0xFF8A6977)
private val Glass = Color(0xFFFFF3F7)
private val Lavender = Color(0xFFEAD9EC)

private val RelationshipStartDate = Date(126, 5, 10)

data class HomeItem(
    val title: String,
    val subtitle: String,
    val iconType: String,
    val color: Color,
    val emoji: String
)

data class Memory(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val description: String = "",
    val createdBy: String = "",
    val imageUrl: String = ""
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                RoyaramApp(
                    onChatClick = {
                        startActivity(
                            Intent(
                                this,
                                ChatActivity::class.java
                            )
                        )
                    },
                    onToast = { message ->
                        Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
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

    val daysTogether = remember {
        (
            (System.currentTimeMillis() - RelationshipStartDate.time) /
                (1000L * 60L * 60L * 24L)
            )
            .toInt()
            .coerceAtLeast(0)
    }

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "homeHeart"
        )

    val heartScale by
        infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.06f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            1200,
                            easing = FastOutSlowInEasing
                        ),
                    repeatMode = RepeatMode.Reverse
                ),
            label = "heartScale"
        )

    val homeItems = listOf(

        HomeItem(
            "خاطرات ما",
            "لحظه‌های قشنگمون",
            "memory",
            Pink,
            "📸"
        ),

        HomeItem(
            "نامه‌های عاشقانه",
            "حرف‌هایی از قلبمون",
            "letter",
            Color(0xFFE8759B),
            "💌"
        ),

        HomeItem(
            "آهنگ ما",
            "صدای خاطره‌هامون",
            "music",
            Color(0xFFD65A86),
            "🎵"
        ),

        HomeItem(
            "وقتی دلمون گرفت",
            "اینجا همیشه کنار همیم",
            "sad",
            Color(0xFFE58AA7),
            "🧸"
        ),

        HomeItem(
            "چت دونفره",
            "حرف‌های من و تو ❤️",
            "chat",
            Color(0xFFDB4F7C),
            "💬"
        ),

        HomeItem(
            "بخش خصوصی",
            "فقط برای من و تو 🔐",
            "private",
            Color(0xFFB55C79),
            "🔐"
        ),

        HomeItem(
            "تنظیمات",
            "شخصی‌سازی رویارام",
            "settings",
            Lavender,
            "⚙️"
        )
    )

    LazyColumn(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFC8DB),
                            Color(0xFFFFDCE8),
                            Background,
                            Color(0xFFFFF7FA)
                        )
                    )
                )
                .statusBarsPadding(),

        contentPadding =
            PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 8.dp,
                bottom = 24.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)

    ) {

        item {

            HomeHeader(
                heartScale = heartScale,
                onSettings = {
                    onToast(
                        "تنظیمات رویارام 💗"
                    )
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

            LoveCounterCard(
                daysTogether = daysTogether,
                heartScale = heartScale
            )
        }

        item {
            SectionTitle()
        }

        item {

            HomeGrid(
                items = homeItems,
                onMemoriesClick = onMemoriesClick,
                onChatClick = onChatClick,
                onToast = onToast
            )
        }

        item {
            FooterCard()
        }
    }
}

@Composable
private fun HomeHeader(
    heartScale: Float,
    onSettings: () -> Unit
) {

    Box(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(165.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                )
                .background(
                    Color(0xFFFFB8D0)
                )
    ) {

        Image(

            painter =
                painterResource(
                    R.drawable.couple_main
                ),

            contentDescription = null,

            modifier =
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 0.92f
                    },

            contentScale =
                ContentScale.Crop
        )

        Box(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(

                        Brush.horizontalGradient(

                            listOf(
                                Color(0x66000000),
                                Color(0x33000000),
                                Color(0x995A203E)
                            )
                        )
                    )
        )

        IconButton(

            onClick = onSettings,

            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.88f
                        )
                    )
        ) {

            Icon(

                imageVector =
                    Icons.Default.Settings,

                contentDescription =
                    "تنظیمات",

                tint =
                    PinkDark,

                modifier =
                    Modifier.size(25.dp)
            )
        }

        Column(

            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(
                        end = 22.dp,
                        top = 20.dp
                    ),

            horizontalAlignment =
                Alignment.End
        ) {

            Text(

                text = "رویارام ♡",

                color = Color.White,

                fontSize = 34.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Text(

                text =
                    "قصه‌ی من و تو، برای همیشه",

                color =
                    Color.White.copy(
                        alpha = 0.92f
                    ),

                fontSize = 13.sp
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    Modifier
                        .width(80.dp)
                        .height(1.dp)
                        .background(
                            Color.White.copy(
                                alpha = 0.65f
                            )
                        )
                )

                Text(

                    text = " ♥ ",

                    color = Color.White,

                    fontSize = 15.sp,

                    modifier =
                        Modifier.graphicsLayer {

                            scaleX =
                                heartScale

                            scaleY =
                                heartScale
                        }
                )

                Box(
                    Modifier
                        .width(80.dp)
                        .height(1.dp)
                        .background(
                            Color.White.copy(
                                alpha = 0.65f
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun HeroPhotoCard(
    daysTogether: Int,
    heartScale: Float
) {

    var started by remember {
        mutableStateOf(false)
    }

    var showHearts by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        delay(250)

        started = true

        delay(1500)

        showHearts = true
    }

    val scale by
        animateFloatAsState(

            targetValue =
                if (started) {
                    1.055f
                } else {
                    1f
                },

            animationSpec =
                tween(
                    3000,
                    easing = FastOutSlowInEasing
                ),

            label = "heroZoom"
        )

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(240.dp)
                .shadow(
                    12.dp,
                    RoundedCornerShape(32.dp)
                ),

        shape =
            RoundedCornerShape(32.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = Color.Black
            )
    ) {

        Box(
            Modifier.fillMaxSize()
        ) {

            Image(

                painter =
                    painterResource(
                        R.drawable.couple_main
                    ),

                contentDescription =
                    "رامین و رویا",

                modifier =
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {

                            scaleX =
                                scale

                            scaleY =
                                scale
                        },

                contentScale =
                    ContentScale.Crop
            )

            Box(

                Modifier
                    .fillMaxSize()
                    .background(

                        Brush.verticalGradient(

                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color(0xCC1B1017)
                            )
                        )
                    )
            )

            if (showHearts) {

                Text(

                    text = "♥  ♥",

                    color = Color.White,

                    fontSize = 30.sp,

                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .graphicsLayer {

                                scaleX =
                                    heartScale

                                scaleY =
                                    heartScale
                            }
                )
            }

            Column(

                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .padding(18.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(

                    text =
                        "رامین  ❤️  رویا",

                    color = Color.White,

                    fontSize = 25.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(

                    text =
                        "هر روز یک صفحه‌ی تازه از قصه‌ی ما",

                    color =
                        Color.White.copy(
                            alpha = 0.9f
                        ),

                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun LoveCounterCard(
    daysTogether: Int,
    heartScale: Float
) {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(
                    10.dp,
                    RoundedCornerShape(34.dp)
                ),

        shape =
            RoundedCornerShape(34.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White.copy(
                        alpha = 0.90f
                    )
            )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            CounterSide(

                title =
                    "تاریخ آشنایی",

                value =
                    "۱۴۰۵/۰۳/۲۰",

                icon =
                    "♡",

                modifier =
                    Modifier.weight(1f)
            )

            Box(

                modifier =
                    Modifier
                        .weight(1.12f)
                        .size(118.dp)
                        .clip(CircleShape)
                        .background(

                            Brush.radialGradient(

                                listOf(
                                    Color(0xFFFFC5D9),
                                    Color(0xFFFFEAF2)
                                )
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(

                        text = "♥",

                        color = Pink,

                        fontSize = 24.sp,

                        modifier =
                            Modifier.graphicsLayer {

                                scaleX =
                                    heartScale

                                scaleY =
                                    heartScale
                            }
                    )

                    Text(

                        text =
                            daysTogether.toString(),

                        color =
                            PinkDark,

                        fontSize = 34.sp,

                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(

                        text =
                            "روز کنار هم",

                        color =
                            SoftText,

                        fontSize = 10.sp
                    )
                }
            }

            CounterSide(

                title =
                    "هر روز",

                value =
                    "عاشق‌تر شدن",

                icon =
                    "♡",

                modifier =
                    Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CounterSide(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier
) {

    Column(

        modifier =
            modifier.padding(
                horizontal = 4.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            color = SoftText,
            fontSize = 11.sp
        )

        Spacer(
            Modifier.height(5.dp)
        )

        Text(

            text = value,

            color = PinkDark,

            fontSize = 13.sp,

            fontWeight =
                FontWeight.Bold,

            textAlign =
                TextAlign.Center
        )

        Spacer(
            Modifier.height(4.dp)
        )

        Text(

            text = icon,

            color = Pink,

            fontSize = 20.sp
        )
    }
}

@Composable
private fun SectionTitle() {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 6.dp,
                    vertical = 2.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(

            text =
                "دنیای دونفره‌ی ما",

            color =
                TextDark,

            fontSize = 24.sp,

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            Modifier.width(8.dp)
        )

        Text(
            text = "♥",
            color = Pink,
            fontSize = 20.sp
        )
    }
}

@Composable
private fun HomeGrid(
    items: List<HomeItem>,
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit,
    onToast: (String) -> Unit
) {

    var visibleCount by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {

        for (i in items.indices) {

            delay(90)

            visibleCount = i + 1
        }
    }

    LazyVerticalGrid(

        columns =
            GridCells.Fixed(3),

        modifier =
            Modifier
                .fillMaxWidth()
                .height(430.dp),

        verticalArrangement =
            Arrangement.spacedBy(10.dp),

        horizontalArrangement =
            Arrangement.spacedBy(10.dp),

        userScrollEnabled = false

    ) {

        gridItems(items) { item ->

            val index =
                items.indexOf(item)

            if (index < visibleCount) {

                RomanticHomeCard(

                    item = item,

                    onClick = {

                        when (item.iconType) {

                            "memory" -> {
                                onMemoriesClick()
                            }

                            "chat" -> {
                                onChatClick()
                            }

                            "letter" -> {

                                onToast(
                                    "نامه‌های عاشقانه به‌زودی فعال می‌شود 💌"
                                )
                            }

                            "music" -> {

                                onToast(
                                    "بخش آهنگ ما به‌زودی فعال می‌شود 🎵"
                                )
                            }

                            "sad" -> {

                                onToast(
                                    "اینجا همیشه کنار همیم ❤️"
                                )
                            }

                            "private" -> {

                                onToast(
                                    "این بخش مخصوص رامین و رویاست 🔐"
                                )
                            }

                            "settings" -> {

                                onToast(
                                    "تنظیمات رویارام به‌زودی آماده می‌شود ⚙️"
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RomanticHomeCard(
    item: HomeItem,
    onClick: () -> Unit
) {

    var pressed by remember {
        mutableStateOf(false)
    }

    val scale by
        animateFloatAsState(

            targetValue =
                if (pressed) {
                    0.94f
                } else {
                    1f
                },

            animationSpec =
                tween(130),

            label = "cardPress"
        )

    LaunchedEffect(pressed) {

        if (pressed) {

            delay(140)

            pressed = false
        }
    }

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(137.dp)
                .graphicsLayer {

                    scaleX =
                        scale

                    scaleY =
                        scale
                }
                .clickable {

                    pressed = true

                    onClick()
                }
                .animateContentSize(),

        shape =
            RoundedCornerShape(25.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = Glass
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
    ) {

        Box(
            Modifier.fillMaxSize()
        ) {

            Box(

                Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .clip(

                        RoundedCornerShape(
                            topStart = 25.dp,
                            topEnd = 25.dp
                        )
                    )
                    .background(

                        Brush.linearGradient(

                            listOf(
                                item.color.copy(
                                    alpha = 0.50f
                                ),
                                Color.White.copy(
                                    alpha = 0.78f
                                ),
                                Lavender.copy(
                                    alpha = 0.45f
                                )
                            )
                        )
                    )
            )

            Text(

                text = item.emoji,

                fontSize = 31.sp,

                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 15.dp)
            )

            Column(

                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .fillMaxWidth()
                        .padding(
                            start = 5.dp,
                            end = 5.dp,
                            bottom = 8.dp
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(

                    text = item.title,

                    color = TextDark,

                    fontSize = 12.sp,

                    fontWeight =
                        FontWeight.Bold,

                    textAlign =
                        TextAlign.Center,

                    maxLines = 2
                )

                Text(

                    text = item.subtitle,

                    color = SoftText,

                    fontSize = 8.sp,

                    textAlign =
                        TextAlign.Center,

                    maxLines = 1
                )

                Spacer(
                    Modifier.height(3.dp)
                )

                Box(

                    Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(PinkSoft),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text = "›",

                        color = PinkDark,

                        fontSize = 15.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FooterCard() {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .shadow(
                    10.dp,
                    RoundedCornerShape(30.dp)
                ),

        shape =
            RoundedCornerShape(30.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFFFDDE9)
            )
    ) {

        Box(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .background(

                        Brush.horizontalGradient(

                            listOf(
                                Color(0xAA5B304F),
                                Color(0xAA9D5674),
                                Color(0xAA5B304F)
                            )
                        )
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    "♡",
                    color = Color.White,
                    fontSize = 30.sp
                )

                Text(

                    text = "دوستت دارم",

                    color = Color.White,

                    fontSize = 22.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(

                    text =
                        "نه فقط امروز، بلکه تا همیشه...",

                    color =
                        Color.White.copy(
                            alpha = 0.92f
                        ),

                    fontSize = 13.sp
                )

                Spacer(
                    Modifier.height(5.dp)
                )

                Text(
                    "♥",
                    color = Color(0xFFFFAFC8),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun MemoriesScreen(
    onBack: () -> Unit
) {

    val db =
        FirebaseFirestore.getInstance()

    var memories by remember {
        mutableStateOf(
            emptyList<Memory>()
        )
    }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        db.collection("memories")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {

                    memories =
                        snapshot.documents.map { document ->

                            Memory(

                                id =
                                    document.id,

                                title =
                                    document.getString(
                                        "title"
                                    ) ?: "",

                                date =
                                    document.getString(
                                        "date"
                                    ) ?: "",

                                description =
                                    document.getString(
                                        "description"
                                    ) ?: "",

                                createdBy =
                                    document.getString(
                                        "createdBy"
                                    ) ?: "",

                                imageUrl =
                                    document.getString(
                                        "imageUrl"
                                    ) ?: ""
                            )
                        }
                }
            }
    }

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(

                    Brush.verticalGradient(

                        listOf(
                            Color(0xFFFFDCE8),
                            Background,
                            Color.White
                        )
                    )
                )
                .statusBarsPadding()
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 15.dp,
                        vertical = 10.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            TextButton(
                onClick = onBack
            ) {

                Text(
                    "← بازگشت",
                    color = PinkDark
                )
            }

            Text(

                text =
                    "خاطرات ما ❤️",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold,

                color = TextDark
            )

            IconButton(

                onClick = {
                    showAddDialog = true
                }

            ) {

                Icon(

                    imageVector =
                        Icons.Default.Favorite,

                    contentDescription =
                        "افزودن خاطره",

                    tint = Pink
                )
            }
        }

        if (memories.isEmpty()) {

            Box(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(30.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        "🌷",
                        fontSize = 55.sp
                    )

                    Spacer(
                        Modifier.height(12.dp)
                    )

                    Text(

                        text =
                            "هنوز خاطره‌ای ثبت نشده",

                        fontSize = 19.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color = TextDark
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(

                        text =
                            "اولین خاطره‌ی قشنگمون رو ثبت کنیم؟ ❤️",

                        fontSize = 13.sp,

                        color = SoftText,

                        textAlign =
                            TextAlign.Center
                    )

                    Spacer(
                        Modifier.height(18.dp)
                    )

                    Button(

                        onClick = {
                            showAddDialog = true
                        },

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Pink
                            )
                    ) {

                        Text(
                            "افزودن اولین خاطره"
                        )
                    }
                }
            }

        } else {

            LazyColumn(

                modifier =
                    Modifier.fillMaxSize(),

                contentPadding =
                    PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 5.dp,
                        bottom = 30.dp
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                items(memories) { memory ->

                    MemoryCard(memory)
                }
            }
        }
    }

    if (showAddDialog) {

        AddMemoryDialog(

            onDismiss = {
                showAddDialog = false
            },

            onSaved = {
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MemoryCard(
    memory: Memory
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(26.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
    ) {

        Column(
            Modifier.padding(18.dp)
        ) {

            if (memory.imageUrl.isNotBlank()) {

                AsyncImage(

                    model =
                        memory.imageUrl,

                    contentDescription =
                        memory.title,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .clip(
                                RoundedCornerShape(20.dp)
                            ),

                    contentScale =
                        ContentScale.Crop
                )

                Spacer(
                    Modifier.height(14.dp)
                )
            }

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(

                    Modifier
                        .size(45.dp)
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                        .background(PinkSoft),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        "❤️",
                        fontSize = 22.sp
                    )
                }

                Spacer(
                    Modifier.width(12.dp)
                )

                Column(
                    Modifier.weight(1f)
                ) {

                    Text(

                        text =
                            memory.title.ifBlank {
                                "یک خاطره‌ی قشنگ"
                            },

                        fontSize = 18.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color = TextDark
                    )

                    if (memory.date.isNotBlank()) {

                        Text(

                            memory.date,

                            fontSize = 11.sp,

                            color = Pink
                        )
                    }
                }
            }

            if (memory.description.isNotBlank()) {

                Spacer(
                    Modifier.height(12.dp)
                )

                Text(

                    text =
                        memory.description,

                    fontSize = 13.sp,

                    lineHeight = 22.sp,

                    color = SoftText
                )
            }
        }
    }
}

@Composable
private fun AddMemoryDialog(
    onDismiss: () -> Unit,
    onSaved: () -> Unit
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

    val auth =
        FirebaseAuth.getInstance()

    val db =
        FirebaseFirestore.getInstance()

    AlertDialog(

        onDismissRequest = {

            if (!saving) {
                onDismiss()
            }
        },

        title = {

            Text(

                "خاطره‌ی جدید ❤️",

                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            Column {

                OutlinedTextField(

                    value = title,

                    onValueChange = {
                        title = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("عنوان خاطره")
                    }
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                OutlinedTextField(

                    value = date,

                    onValueChange = {
                        date = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {
                        Text("تاریخ")
                    },

                    placeholder = {
                        Text(
                            "مثلاً ۱۴۰۵/۰۳/۲۰"
                        )
                    }
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                OutlinedTextField(

                    value = description,

                    onValueChange = {
                        description = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    minLines = 4,

                    label = {
                        Text("توضیح خاطره")
                    }
                )
            }
        },

        confirmButton = {

            Button(

                onClick = {

                    if (
                        title.isBlank() ||
                        description.isBlank()
                    ) {
                        return@Button
                    }

                    val user =
                        auth.currentUser
                            ?: return@Button

                    saving = true

                    val data =
                        hashMapOf(

                            "title" to
                                title.trim(),

                            "date" to
                                date.trim(),

                            "description" to
                                description.trim(),

                            "createdBy" to
                                user.uid,

                            "createdAt" to
                                FieldValue.serverTimestamp()
                        )

                    db.collection("memories")
                        .add(data)
                        .addOnSuccessListener {

                            saving = false

                            onSaved()
                        }
                        .addOnFailureListener {

                            saving = false
                        }
                },

                enabled = !saving,

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Pink
                    )
            ) {

                Text(

                    if (saving) {
                        "در حال ذخیره..."
                    } else {
                        "ذخیره ❤️"
                    }
                )
            }
        },

        dismissButton = {

            TextButton(

                onClick = onDismiss,

                enabled = !saving
            ) {

                Text(
                    "لغو",
                    color = SoftText
                )
            }
        }
    )
}
