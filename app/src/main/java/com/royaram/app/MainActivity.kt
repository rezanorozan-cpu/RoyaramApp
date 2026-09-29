package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import java.util.Date

// ============================================================
// رنگ‌های رویارام
// ============================================================

private val Pink = Color(0xFFE95782)
private val PinkDark = Color(0xFFAD315B)
private val PinkLight = Color(0xFFFFD6E3)
private val PinkVeryLight = Color(0xFFFFECF3)
private val Background = Color(0xFFFFE5EE)
private val WhiteGlass = Color(0xFFFFF8FA)
private val TextDark = Color(0xFF35232B)
private val SoftText = Color(0xFF866B76)
private val PurplePink = Color(0xFFE8D5E8)

// ============================================================
// تاریخ شروع رابطه
// ۲۰ خرداد ۱۴۰۵
// ============================================================

private val RelationshipStartDate = Date(
    126,
    5,
    10
)

// ============================================================
// مدل کارت
// ============================================================

data class HomeItem(
    val title: String,
    val subtitle: String,
    val iconType: String,
    val emoji: String,
    val color1: Color,
    val color2: Color
)

// ============================================================
// مدل خاطره
// ============================================================

data class Memory(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val description: String = "",
    val createdBy: String = "",
    val imageUrl: String = ""
)

// ============================================================
// MainActivity
// ============================================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Rtl
                ) {

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
}

// ============================================================
// ریشه برنامه
// ============================================================

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

// ============================================================
// صفحه اصلی
// ============================================================

@Composable
fun RoyaramHome(
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit,
    onToast: (String) -> Unit
) {

    val daysTogether = remember {

        (
            (
                System.currentTimeMillis()
                    - RelationshipStartDate.time
            ) /
                (1000L * 60L * 60L * 24L)
            )
            .toInt()
            .coerceAtLeast(0)
    }

    // --------------------------------------------------------
    // انیمیشن قلب
    // --------------------------------------------------------

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "mainHeart"
        )

    val heartScale by
        infiniteTransition.animateFloat(

            initialValue = 0.94f,

            targetValue = 1.06f,

            animationSpec =
                infiniteRepeatable(

                    animation =
                        tween(
                            durationMillis = 1100,
                            easing =
                                FastOutSlowInEasing
                        ),

                    repeatMode =
                        RepeatMode.Reverse
                ),

            label = "heartScale"
        )

    // --------------------------------------------------------
    // کارت‌ها
    // --------------------------------------------------------

    val homeItems = listOf(

        HomeItem(
            title = "خاطرات ما",
            subtitle = "لحظه‌های قشنگمون",
            iconType = "memory",
            emoji = "📸",
            color1 = Color(0xFFFFB8C9),
            color2 = Color(0xFFFFE8EF)
        ),

        HomeItem(
            title = "نامه‌های عاشقانه",
            subtitle = "حرف‌هایی از قلبمون",
            iconType = "letter",
            emoji = "💌",
            color1 = Color(0xFFFFB4CB),
            color2 = Color(0xFFFFE4EE)
        ),

        HomeItem(
            title = "آهنگ ما",
            subtitle = "صدای خاطره‌هامون",
            iconType = "music",
            emoji = "🎵",
            color1 = Color(0xFFE6C6EA),
            color2 = Color(0xFFFFE8F3)
        ),

        HomeItem(
            title = "وقتی دلمون گرفت",
            subtitle = "اینجا همیشه کنار همیم",
            iconType = "sad",
            emoji = "🧸",
            color1 = Color(0xFFFFC5D3),
            color2 = Color(0xFFFFE8EF)
        ),

        HomeItem(
            title = "چت دونفره",
            subtitle = "حرف‌های من و تو ❤️",
            iconType = "chat",
            emoji = "💬",
            color1 = Color(0xFFE4BDE8),
            color2 = Color(0xFFFFE4EF)
        ),

        HomeItem(
            title = "بخش خصوصی",
            subtitle = "فقط برای من و تو 🔐",
            iconType = "private",
            emoji = "🔐",
            color1 = Color(0xFFFFB9C9),
            color2 = Color(0xFFFFE9EF)
        ),

        HomeItem(
            title = "تنظیمات",
            subtitle = "شخصی‌سازی رویارام",
            iconType = "settings",
            emoji = "⚙️",
            color1 = Color(0xFFD9C9E2),
            color2 = Color(0xFFFFEAF1)
        )
    )

    LazyColumn(

        modifier =
            Modifier
                .fillMaxSize()
                .background(

                    Brush.verticalGradient(

                        colors =
                            listOf(
                                Color(0xFFFFC6D9),
                                Color(0xFFFFDCE8),
                                Color(0xFFFFEDF3),
                                Color(0xFFFFF8FA)
                            )
                    )
                )
                .statusBarsPadding(),

        contentPadding =
            PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 8.dp,
                bottom = 30.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        // ====================================================
        // عکس اصلی
        // ====================================================

        item {

            MainHero(

                heartScale = heartScale,

                onSettings = {

                    onToast(
                        "تنظیمات رویارام 💗"
                    )
                }
            )
        }

        // ====================================================
        // شمارنده
        // ====================================================

        item {

            MainCounter(

                daysTogether = daysTogether,

                heartScale = heartScale
            )
        }

        // ====================================================
        // عنوان بخش
        // ====================================================

        item {

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 4.dp,
                            start = 5.dp,
                            end = 5.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text =
                        "دنیای دونفره‌ی ما",

                    fontSize = 23.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color = TextDark
                )

                Spacer(
                    Modifier.width(8.dp)
                )

                Text(

                    text = "♥",

                    fontSize = 20.sp,

                    color = Pink,

                    modifier =
                        Modifier.graphicsLayer {

                            scaleX =
                                heartScale

                            scaleY =
                                heartScale
                        }
                )
            }
        }

        // ====================================================
        // گرید سه ستونه
        // ====================================================

        item {

            HomeCardsGrid(

                items = homeItems,

                onMemoriesClick =
                    onMemoriesClick,

                onChatClick =
                    onChatClick,

                onToast =
                    onToast
            )
        }

        // ====================================================
        // کارت عاشقانه پایین
        // ====================================================

        item {

            LoveFooter()
        }
    }
}

// ============================================================
// هدر اصلی
// فقط یک عکس در صفحه
// ============================================================

@Composable
private fun MainHero(
    heartScale: Float,
    onSettings: () -> Unit
) {

    var started by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        delay(250)

        started = true
    }

    val photoScale by
        animateFloatAsState(

            targetValue =
                if (started) {
                    1.045f
                } else {
                    1f
                },

            animationSpec =
                tween(
                    durationMillis = 3200,
                    easing =
                        FastOutSlowInEasing
                ),

            label = "mainPhotoZoom"
        )

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(315.dp)
                .shadow(
                    elevation = 14.dp,
                    shape =
                        RoundedCornerShape(32.dp)
                ),

        shape =
            RoundedCornerShape(32.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.Black
            )
    ) {

        Box(
            Modifier.fillMaxSize()
        ) {

            // ------------------------------------------------
            // عکس
            // ------------------------------------------------

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
                                photoScale

                            scaleY =
                                photoScale
                        },

                contentScale =
                    ContentScale.Crop
            )

            // ------------------------------------------------
            // لایه رنگی روی عکس
            // ------------------------------------------------

            Box(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(

                            Brush.verticalGradient(

                                colors =
                                    listOf(

                                        Color(
                                            0x22000000
                                        ),

                                        Color(
                                            0x22000000
                                        ),

                                        Color(
                                            0xAA3B1730
                                        )
                                    )
                            )
                        )
            )

            // ------------------------------------------------
            // تنظیمات
            // ------------------------------------------------

            IconButton(

                onClick = onSettings,

                modifier =
                    Modifier
                        .align(
                            Alignment.TopStart
                        )
                        .padding(14.dp)
                        .size(58.dp)
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
                        Modifier.size(28.dp)
                )
            }

            // ------------------------------------------------
            // عنوان
            // ------------------------------------------------

            Column(

                modifier =
                    Modifier
                        .align(
                            Alignment.TopEnd
                        )
                        .padding(
                            top = 35.dp,
                            end = 20.dp
                        ),

                horizontalAlignment =
                    Alignment.End
            ) {

                Text(

                    text =
                        "رویارام ♡",

                    color =
                        Color.White,

                    fontSize = 36.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(

                    text =
                        "قصه‌ی من و تو، برای همیشه",

                    color =
                        Color.White.copy(
                            alpha = 0.94f
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
                            .width(62.dp)
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

                        fontSize = 16.sp,

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
                            .width(62.dp)
                            .height(1.dp)
                            .background(
                                Color.White.copy(
                                    alpha = 0.65f
                                )
                            )
                    )
                }
            }

            // ------------------------------------------------
            // نام زوج
            // ------------------------------------------------

            Column(

                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .padding(
                            bottom = 25.dp
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(

                    text =
                        "رامین  ❤️  رویا",

                    color =
                        Color.White,

                    fontSize = 26.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    Modifier.height(5.dp)
                )

                Text(

                    text =
                        "هر روز یک صفحه‌ی تازه از قصه‌ی ما",

                    color =
                        Color.White.copy(
                            alpha = 0.92f
                        ),

                    fontSize = 11.sp
                )
            }
        }
    }
}

// ============================================================
// شمارنده
// ============================================================

@Composable
private fun MainCounter(
    daysTogether: Int,
    heartScale: Float
) {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 9.dp,
                    shape =
                        RoundedCornerShape(34.dp)
                ),

        shape =
            RoundedCornerShape(34.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(
                        alpha = 0.94f
                    )
            )
    ) {

        Box(
            Modifier.fillMaxWidth()
        ) {

            // گل تزئینی گوشه راست

            Text(

                text = "🌸",

                fontSize = 29.sp,

                modifier =
                    Modifier
                        .align(
                            Alignment.TopStart
                        )
                        .offset(
                            x = (-5).dp,
                            y = 4.dp
                        )
            )

            // گل تزئینی گوشه چپ

            Text(

                text = "🌸",

                fontSize = 29.sp,

                modifier =
                    Modifier
                        .align(
                            Alignment.BottomEnd
                        )
                        .offset(
                            x = 4.dp,
                            y = (-2).dp
                        )
            )

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 13.dp,
                            vertical = 16.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                // ------------------------------------------------
                // تاریخ
                // ------------------------------------------------

                Column(

                    modifier =
                        Modifier.weight(1f),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(

                        text =
                            "هر روز",

                        fontSize = 11.sp,

                        color = SoftText
                    )

                    Spacer(
                        Modifier.height(3.dp)
                    )

                    Text(

                        text =
                            "عاشق‌تر شدن",

                        fontSize = 14.sp,

                        color = PinkDark,

                        fontWeight =
                            FontWeight.Bold,

                        textAlign =
                            TextAlign.Center
                    )

                    Text(

                        text = "♡",

                        color = Pink,

                        fontSize = 22.sp
                    )
                }

                // ------------------------------------------------
                // عدد مرکزی
                // ------------------------------------------------

                Box(

                    modifier =
                        Modifier
                            .size(135.dp)
                            .clip(CircleShape)
                            .background(

                                Brush.radialGradient(

                                    colors =
                                        listOf(
                                            Color(0xFFFFBBD0),
                                            Color(0xFFFFE9F1)
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

                            fontSize = 25.sp,

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

                            fontSize = 38.sp,

                            fontWeight =
                                FontWeight.ExtraBold,

                            color =
                                PinkDark
                        )

                        Text(

                            text =
                                "روز کنار هم",

                            fontSize = 10.sp,

                            color =
                                SoftText
                        )
                    }
                }

                // ------------------------------------------------
                // تاریخ آشنایی
                // ------------------------------------------------

                Column(

                    modifier =
                        Modifier.weight(1f),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(

                        text =
                            "تاریخ آشنایی",

                        fontSize = 11.sp,

                        color =
                            SoftText
                    )

                    Spacer(
                        Modifier.height(5.dp)
                    )

                    Text(

                        text =
                            "۱۴۰۵/۰۳/۲۰",

                        fontSize = 13.sp,

                        color =
                            PinkDark,

                        fontWeight =
                            FontWeight.Bold
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
}

// ============================================================
// گرید کارت‌ها
// ============================================================

@Composable
private fun HomeCardsGrid(
    items: List<HomeItem>,
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit,
    onToast: (String) -> Unit
) {

    var visibleCount by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {

        for (index in items.indices) {

            delay(100)

            visibleCount = index + 1
        }
    }

    LazyVerticalGrid(

        columns =
            GridCells.Fixed(3),

        modifier =
            Modifier
                .fillMaxWidth()
                .height(430.dp),

        horizontalArrangement =
            Arrangement.spacedBy(9.dp),

        verticalArrangement =
            Arrangement.spacedBy(11.dp),

        userScrollEnabled = false,

        contentPadding =
            PaddingValues(
                top = 2.dp,
                bottom = 2.dp
            )
    ) {

        gridItems(items) { item ->

            val index =
                items.indexOf(item)

            if (index < visibleCount) {

                RomanticCard(

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

// ============================================================
// کارت سه ستونه
// ============================================================

@Composable
private fun RomanticCard(
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

            delay(130)

            pressed = false
        }
    }

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(132.dp)
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
                .shadow(
                    elevation = 7.dp,
                    shape =
                        RoundedCornerShape(27.dp)
                ),

        shape =
            RoundedCornerShape(27.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    WhiteGlass
            )
    ) {

        Column(

            modifier =
                Modifier.fillMaxSize()
        ) {

            // ------------------------------------------------
            // قسمت تصویری کارت
            // ------------------------------------------------

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .background(

                            Brush.linearGradient(

                                colors =
                                    listOf(
                                        item.color1,
                                        item.color2,
                                        PurplePink.copy(
                                            alpha = 0.35f
                                        )
                                    )
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                // نور گرد

                Box(

                    modifier =
                        Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(
                                Color.White.copy(
                                    alpha = 0.24f
                                )
                            )
                )

                Text(

                    text =
                        item.emoji,

                    fontSize = 32.sp,

                    modifier =
                        Modifier.graphicsLayer {

                            scaleX = 1.0f
                            scaleY = 1.0f
                        }
                )
            }

            // ------------------------------------------------
            // متن کارت
            // ------------------------------------------------

            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(
                            start = 3.dp,
                            end = 3.dp,
                            top = 5.dp,
                            bottom = 4.dp
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(

                    text =
                        item.title,

                    fontSize = 11.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextDark,

                    textAlign =
                        TextAlign.Center,

                    maxLines = 1
                )

                Text(

                    text =
                        item.subtitle,

                    fontSize = 7.sp,

                    color =
                        SoftText,

                    textAlign =
                        TextAlign.Center,

                    maxLines = 1
                )

                Spacer(
                    Modifier.height(2.dp)
                )

                Box(

                    modifier =
                        Modifier
                            .size(21.dp)
                            .clip(CircleShape)
                            .background(
                                PinkLight
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text = "‹",

                        color =
                            PinkDark,

                        fontSize = 15.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ============================================================
// فوتر بزرگ
// ============================================================

@Composable
private fun LoveFooter() {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(155.dp)
                .navigationBarsPadding()
                .shadow(
                    elevation = 10.dp,
                    shape =
                        RoundedCornerShape(30.dp)
                ),

        shape =
            RoundedCornerShape(30.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF9A5975)
            )
    ) {

        Box(

            modifier =
                Modifier.fillMaxSize()
        ) {

            // نورهای تزئینی

            Box(

                modifier =
                    Modifier
                        .size(170.dp)
                        .offset(
                            x = (-40).dp,
                            y = 55.dp
                        )
                        .clip(CircleShape)
                        .background(
                            Color(0x66FFD6E5)
                        )
            )

            Box(

                modifier =
                    Modifier
                        .size(150.dp)
                        .offset(
                            x = 260.dp,
                            y = (-55).dp
                        )
                        .clip(CircleShape)
                        .background(
                            Color(0x55FFD6E5)
                        )
            )

            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(20.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(

                    text = "♡",

                    color =
                        Color.White,

                    fontSize = 31.sp
                )

                Text(

                    text =
                        "دوستت دارم",

                    color =
                        Color.White,

                    fontSize = 23.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    Modifier.height(3.dp)
                )

                Text(

                    text =
                        "نه فقط امروز، بلکه تا همیشه...",

                    color =
                        Color.White.copy(
                            alpha = 0.94f
                        ),

                    fontSize = 13.sp
                )

                Spacer(
                    Modifier.height(4.dp)
                )

                Text(
                    text = "♥",
                    color =
                        Color(0xFFFFB5CC),
                    fontSize = 17.sp
                )
            }
        }
    }
}

// ============================================================
// صفحه خاطرات
// ============================================================

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

                        colors =
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
                        horizontal = 14.dp,
                        vertical = 8.dp
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

                    text =
                        "→ بازگشت",

                    color =
                        PinkDark
                )
            }

            Text(

                text =
                    "خاطرات ما ❤️",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark
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

                    tint =
                        Pink
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
                        text = "🌷",
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

                        color =
                            TextDark
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(

                        text =
                            "اولین خاطره‌ی قشنگمون رو ثبت کنیم؟ ❤️",

                        fontSize = 13.sp,

                        color =
                            SoftText,

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
                                containerColor =
                                    Pink
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

                    MemoryCard(
                        memory = memory
                    )
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

// ============================================================
// کارت خاطره
// ============================================================

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
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
    ) {

        Column(
            modifier =
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

                    modifier =
                        Modifier
                            .size(45.dp)
                            .clip(
                                RoundedCornerShape(15.dp)
                            )
                            .background(
                                PinkLight
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "❤️",
                        fontSize = 22.sp
                    )
                }

                Spacer(
                    Modifier.width(12.dp)
                )

                Column(
                    modifier =
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

                        color =
                            TextDark
                    )

                    if (memory.date.isNotBlank()) {

                        Text(

                            text =
                                memory.date,

                            fontSize = 11.sp,

                            color =
                                Pink
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

                    color =
                        SoftText
                )
            }
        }
    }
}

// ============================================================
// دیالوگ افزودن خاطره
// ============================================================

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

                text =
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
                        Text(
                            "عنوان خاطره"
                        )
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
                        Text(
                            "تاریخ"
                        )
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

                OutlinedTextField
