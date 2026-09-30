package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.CompositionLocalProvider
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.Date

// ============================================================
// ROYARAM COLORS
// ============================================================

private val Rose = Color(0xFFE85D86)
private val DeepRose = Color(0xFFB83260)
private val Pink = Color(0xFFFF7FA5)

private val SoftPink = Color(0xFFFFDCE7)
private val PalePink = Color(0xFFFFF3F7)

private val Lavender = Color(0xFFB9A7E8)
private val PaleLavender = Color(0xFFF0EBFF)

private val Cream = Color(0xFFFFFBFC)

private val TextDark = Color(0xFF30252A)
private val SoftText = Color(0xFF8A737C)

private val MainBackground =
    Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFE8F0),
            Color(0xFFF7F0FF),
            Color(0xFFFFFAFC)
        )
    )

// ============================================================
// DATA
// ============================================================

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

// ============================================================
// MAIN ACTIVITY
// ============================================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            CompositionLocalProvider(
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

                            } catch (_: Exception) {

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

// ============================================================
// ROOT
// ============================================================

@Composable
fun RoyaramApp(
    onChatClick: () -> Unit
) {

    var showMemories by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {

        if (showMemories) {

            MemoriesScreen(
                onBack = {
                    showMemories = false
                }
            )

        } else {

            HomeScreen(
                onMemoriesClick = {
                    showMemories = true
                },
                onChatClick = onChatClick
            )
        }
    }
}

// ============================================================
// HOME SCREEN
// ============================================================

@Composable
fun HomeScreen(
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit
) {

    val daysTogether =
        remember {
            calculateDaysTogether()
        }

    var showContent by remember {
        mutableStateOf(false)
    }

    var heartPulse by remember {
        mutableStateOf(false)
    }

    var selectedTab by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {

        showContent = true

        while (true) {

            heartPulse = !heartPulse

            kotlinx.coroutines.delay(1200)
        }
    }

    val heartScale by animateFloatAsState(

        targetValue =
            if (heartPulse) {
                1.08f
            } else {
                0.96f
            },

        animationSpec =
            tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),

        label = "heart_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {

        LazyColumn(

            modifier = Modifier.fillMaxSize(),

            contentPadding =
                PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 14.dp,
                    bottom = 135.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            item {

                AnimatedVisibility(

                    visible = showContent,

                    enter =
                        fadeIn(
                            tween(450)
                        ) +
                                slideInVertically(
                                    initialOffsetY = {
                                        -25
                                    },
                                    animationSpec =
                                        tween(450)
                                )
                ) {

                    PremiumHeader()
                }
            }

            item {

                AnimatedVisibility(

                    visible = showContent,

                    enter =
                        fadeIn(
                            tween(650)
                        ) +
                                slideInVertically(
                                    initialOffsetY = {
                                        45
                                    },
                                    animationSpec =
                                        tween(650)
                                )
                ) {

                    HeroCoupleCard(
                        daysTogether = daysTogether,
                        heartScale = heartScale
                    )
                }
            }

            item {

                AnimatedVisibility(

                    visible = showContent,

                    enter =
                        fadeIn(
                            tween(800)
                        ) +
                                slideInVertically(
                                    initialOffsetY = {
                                        50
                                    },
                                    animationSpec =
                                        tween(800)
                                )
                ) {

                    RelationshipCard(
                        daysTogether = daysTogether
                    )
                }
            }

            item {

                AnimatedVisibility(

                    visible = showContent,

                    enter =
                        fadeIn(
                            tween(950)
                        ) +
                                slideInVertically(
                                    initialOffsetY = {
                                        55
                                    },
                                    animationSpec =
                                        tween(950)
                                )
                ) {

                    TodayMessageCard()
                }
            }

            item {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text = "دنیای دونفره‌مون",
                            fontSize = 22.sp,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color = TextDark
                        )

                        Text(
                            text =
                                "همه‌چیز برای من و تو، همین‌جاست ❤️",
                            fontSize = 11.sp,
                            color = SoftText,
                            modifier =
                                Modifier.padding(
                                    top = 3.dp
                                )
                        )
                    }

                    Text(
                        text = "✦",
                        fontSize = 25.sp,
                        color = Rose
                    )
                }
            }

            item {

                FeatureGrid(
                    onMemoriesClick =
                        onMemoriesClick,

                    onChatClick =
                        onChatClick
                )
            }

            item {

                RomanticFooter()
            }
        }

        PremiumBottomBar(

            selectedTab = selectedTab,

            onHomeClick = {
                selectedTab = 0
            },

            onMemoriesClick = {

                selectedTab = 1

                onMemoriesClick()
            },

            onChatClick = {

                selectedTab = 2

                onChatClick()
            },

            onMoreClick = {

                selectedTab = 3
            },

            modifier =
                Modifier
                    .align(
                        Alignment.BottomCenter
                    )
                    .navigationBarsPadding()
        )
    }
}

// ============================================================
// HEADER
// ============================================================

@Composable
fun PremiumHeader() {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 2.dp,
                    vertical = 5.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text = "رویـارام",
                fontSize = 30.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                color = DeepRose
            )

            Text(
                text =
                    "قصه‌ی من و تو، برای همیشه",
                fontSize = 12.sp,
                color = SoftText,
                modifier =
                    Modifier.padding(
                        top = 2.dp
                    )
            )
        }

        Box(

            modifier =
                Modifier
                    .size(49.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor =
                            Rose.copy(
                                alpha = 0.18f
                            ),
                        spotColor =
                            Rose.copy(
                                alpha = 0.22f
                            )
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors =
                                listOf(
                                    Rose,
                                    DeepRose
                                )
                        )
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "♥",
                color = Color.White,
                fontSize = 25.sp
            )
        }
    }
}

// ============================================================
// HERO
// ============================================================

@Composable
fun HeroCoupleCard(
    daysTogether: Long,
    heartScale: Float
) {

    var imageStarted by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        kotlinx.coroutines.delay(350)

        imageStarted = true
    }

    val photoScale by animateFloatAsState(

        targetValue =
            if (imageStarted) {
                1.045f
            } else {
                1f
            },

        animationSpec =
            tween(
                durationMillis = 3000,
                easing = FastOutSlowInEasing
            ),

        label = "photo_scale"
    )

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(365.dp)
                .shadow(
                    elevation = 22.dp,
                    shape =
                        RoundedCornerShape(32.dp),
                    ambientColor =
                        Rose.copy(
                            alpha = 0.20f
                        ),
                    spotColor =
                        Rose.copy(
                            alpha = 0.20f
                        )
                ),

        shape =
            RoundedCornerShape(32.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = Color.White
            )
    ) {

        Box(
            modifier =
                Modifier.fillMaxSize()
        ) {

            Box(
    modifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
            scaleX = photoScale
            scaleY = photoScale
        }
        .background(
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFDCE9),
                    Color(0xFFF4E8FF),
                    Color(0xFFFFF5F8)
                )
            )
        ),
    contentAlignment = Alignment.Center
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "رامین ❤️ رویا",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "قصه‌ی من و تو، برای همیشه",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.92f)
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "♥",
            fontSize = 72.sp,
            color = Rose
        )
    }
}

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Black.copy(
                                            alpha = 0.04f
                                        ),
                                        Color.Transparent,
                                        Color.Black.copy(
                                            alpha = 0.72f
                                        )
                                    )
                            )
                        )
            )

            Box(
                modifier =
                    Modifier
                        .align(
                            Alignment.TopEnd
                        )
                        .padding(15.dp)
                        .clip(
                            RoundedCornerShape(
                                50.dp
                            )
                        )
                        .background(
                            Color.White.copy(
                                alpha = 0.90f
                            )
                        )
                        .padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        )
            ) {

                Text(
                    text =
                        "❤️ $daysTogether روز",
                    fontSize = 12.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = DeepRose
                )
            }

            Box(

                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {

                            scaleX =
                                heartScale

                            scaleY =
                                heartScale
                        }
            ) {

                Text(
                    text = "♥",
                    color =
                        Color.White.copy(
                            alpha = 0.92f
                        ),
                    fontSize = 44.sp
                )
            }

            Column(

                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .fillMaxWidth()
                        .padding(21.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        "رامین  ❤️  رویا",
                    fontSize = 27.sp,
                    fontWeight =
                        FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        "هر روز، یک صفحه‌ی تازه از قصه‌ی ما",
                    fontSize = 12.sp,
                    color =
                        Color.White.copy(
                            alpha = 0.92f
                        ),
                    textAlign =
                        TextAlign.Center
                )
            }
        }
    }
}

// ============================================================
// RELATIONSHIP CARD
// ============================================================

@Composable
fun RelationshipCard(
    daysTogether: Long
) {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape =
                        RoundedCornerShape(27.dp),
                    ambientColor =
                        Lavender.copy(
                            alpha = 0.13f
                        ),
                    spotColor =
                        Lavender.copy(
                            alpha = 0.13f
                        )
                ),

        shape =
            RoundedCornerShape(27.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(
                        alpha = 0.86f
                    )
            )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(19.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(

                modifier =
                    Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors =
                                    listOf(
                                        PalePink,
                                        PaleLavender
                                    )
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "💕",
                    fontSize = 29.sp
                )
            }

            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        "داستان قشنگمون",
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = TextDark
                )

                Text(
                    text =
                        "شروع قصه: ۲۰ خرداد ۱۴۰۵",
                    fontSize = 11.sp,
                    color = SoftText,
                    modifier =
                        Modifier.padding(
                            top = 4.dp
                        )
                )
            }

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        daysTogether.toString(),
                    fontSize = 27.sp,
                    fontWeight =
                        FontWeight.ExtraBold,
                    color = DeepRose
                )

                Text(
                    text =
                        "روز کنار هم",
                    fontSize = 10.sp,
                    color = SoftText
                )
            }
        }
    }
}

// ============================================================
// TODAY MESSAGE
// ============================================================

@Composable
fun TodayMessageCard() {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(27.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.Transparent
            )
    ) {

        Box(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors =
                                listOf(
                                    DeepRose,
                                    Rose,
                                    Lavender
                                )
                        ),
                        RoundedCornerShape(27.dp)
                    )
                    .padding(22.dp)
        ) {

            Column {

                Text(
                    text =
                        "پیام امروز برای تو 💌",
                    color =
                        Color.White.copy(
                            alpha = 0.88f
                        ),
                    fontSize = 12.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(9.dp)
                )

                Text(
                    text =
                        "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.»",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    lineHeight = 27.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text = "برای تو ❤️",
                    color =
                        Color.White.copy(
                            alpha = 0.82f
                        ),
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ============================================================
// FEATURE GRID
// ============================================================

@Composable
fun FeatureGrid(
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit
) {

    val items =
        listOf(

            HomeItem(
                "خاطرات ما",
                "عکس‌ها و لحظه‌های ما",
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
                "حرف‌های من و تو ❤️",
                "💬"
            ),

            HomeItem(
                "بخش خصوصی",
                "فقط برای خودمون",
                "🔐"
            ),

            HomeItem(
                "لحظه‌های ویژه",
                "تولد، سالگرد و قرارها",
                "🎁"
            ),

            HomeItem(
                "تنظیمات",
                "شخصی‌سازی رویارام",
                "⚙️"
            )
        )

    Column(
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        for (rowIndex in 0 until 4) {

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                val firstIndex =
                    rowIndex * 2

                val secondIndex =
                    firstIndex + 1

                FeatureCard(

                    item =
                        items[firstIndex],

                    modifier =
                        Modifier.weight(1f),

                    onClick = {

                        when (
                            items[firstIndex].title
                        ) {

                            "خاطرات ما" ->
                                onMemoriesClick()

                            "چت دونفره" ->
                                onChatClick()
                        }
                    }
                )

                FeatureCard(

                    item =
                        items[secondIndex],

                    modifier =
                        Modifier.weight(1f),

                    onClick = {

                        when (
                            items[secondIndex].title
                        ) {

                            "خاطرات ما" ->
                                onMemoriesClick()

                            "چت دونفره" ->
                                onChatClick()
                        }
                    }
                )
            }
        }
    }
}

// ============================================================
// FEATURE CARD
// ============================================================

@Composable
fun FeatureCard(
    item: HomeItem,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Card(

        modifier =
            modifier
                .aspectRatio(0.96f)
                .shadow(
                    elevation = 8.dp,
                    shape =
                        RoundedCornerShape(25.dp),
                    ambientColor =
                        Rose.copy(
                            alpha = 0.09f
                        ),
                    spotColor =
                        Rose.copy(
                            alpha = 0.09f
                        )
                )
                .clickable {
                    onClick()
                },

        shape =
            RoundedCornerShape(25.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(
                        alpha = 0.90f
                    )
            )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(14.dp),

            verticalArrangement =
                Arrangement.Center,

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(

                modifier =
                    Modifier
                        .size(57.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors =
                                    listOf(
                                        PalePink,
                                        PaleLavender
                                    )
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        item.emoji,
                    fontSize = 27.sp
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    item.title,
                fontSize = 14.sp,
                fontWeight =
                    FontWeight.Bold,
                color = TextDark,
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    item.subtitle,
                fontSize = 10.sp,
                color = SoftText,
                textAlign =
                    TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}

// ============================================================
// BOTTOM NAVIGATION
// ============================================================

@Composable
fun PremiumBottomBar(
    selectedTab: Int,
    onHomeClick: () -> Unit,
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(

        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    end = 14.dp,
                    bottom = 8.dp
                )
                .shadow(
                    elevation = 20.dp,
                    shape =
                        RoundedCornerShape(31.dp),
                    ambientColor =
                        Rose.copy(
                            alpha = 0.18f
                        ),
                    spotColor =
                        Rose.copy(
                            alpha = 0.20f
                        )
                ),

        shape =
            RoundedCornerShape(31.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(
                        alpha = 0.96f
                    )
            )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 7.dp,
                        vertical = 8.dp
                    ),

            horizontalArrangement =
                Arrangement.SpaceEvenly,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            BottomBarItem(
                selected =
                    selectedTab == 0,
                icon = "⌂",
                title = "خانه",
                onClick =
                    onHomeClick
            )

            BottomBarItem(
                selected =
                    selectedTab == 1,
                icon = "♡",
                title = "خاطرات",
                onClick =
                    onMemoriesClick
            )

            BottomBarCenterButton(
                selected =
                    selectedTab == 2,
                onClick =
                    onChatClick
            )

            BottomBarItem(
                selected =
                    selectedTab == 3,
                icon = "✦",
                title = "بیشتر",
                onClick =
                    onMoreClick
            )
        }
    }
}

// ============================================================
// BOTTOM ITEM
// ============================================================

@Composable
fun BottomBarItem(
    selected: Boolean,
    icon: String,
    title: String,
    onClick: () -> Unit
) {

    val scale by animateFloatAsState(

        targetValue =
            if (selected) {
                1.05f
            } else {
                1f
            },

        animationSpec =
            tween(250),

        label =
            "bottom_item_scale"
    )

    Column(

        modifier =
            Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 13.dp,
                    vertical = 7.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Box(

            modifier =
                Modifier
                    .size(
                        if (selected) {
                            39.dp
                        } else {
                            33.dp
                        }
                    )
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            SoftPink
                        } else {
                            Color.Transparent
                        }
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(

                text = icon,

                fontSize =
                    if (selected) {
                        23.sp
                    } else {
                        20.sp
                    },

                color =
                    if (selected) {
                        DeepRose
                    } else {
                        SoftText
                    }
            )
        }

        Spacer(
            modifier =
                Modifier.height(2.dp)
        )

        Text(

            text = title,

            fontSize = 10.sp,

            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },

            color =
                if (selected) {
                    DeepRose
                } else {
                    SoftText
                }
        )
    }
}

// ============================================================
// CENTER CHAT
// ============================================================

@Composable
fun BottomBarCenterButton(
    selected: Boolean,
    onClick: () -> Unit
) {

    val scale by animateFloatAsState(

        targetValue =
            if (selected) {
                1.08f
            } else {
                1f
            },

        animationSpec =
            tween(250),

        label =
            "center_button_scale"
    )

    Box(

        modifier =
            Modifier
                .size(62.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor =
                        Rose.copy(
                            alpha = 0.25f
                        ),
                    spotColor =
                        Rose.copy(
                            alpha = 0.28f
                        )
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors =
                            listOf(
                                Rose,
                                DeepRose
                            )
                    )
                )
                .clickable {
                    onClick()
                },

        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "💬",
                fontSize = 25.sp
            )

            Text(
                text = "چت",
                fontSize = 9.sp,
                fontWeight =
                    FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ============================================================
// FOOTER
// ============================================================

@Composable
fun RomanticFooter() {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 10.dp,
                    bottom = 10.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text =
                "رامین ❤️ رویا",
            fontSize = 18.sp,
            fontWeight =
                FontWeight.Bold,
            color = DeepRose
        )

        Spacer(
            modifier =
                Modifier.height(5.dp)
        )

        Text(
            text =
                "یک دنیای کوچک، فقط برای دو نفر",
            fontSize = 12.sp,
            color = SoftText
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "♡  رویارام  ♡",
            fontSize = 11.sp,
            color =
                Rose.copy(
                    alpha = 0.75f
                )
        )
    }
}

// ============================================================
// MEMORIES SCREEN
// ============================================================

@Composable
fun MemoriesScreen(
    onBack: () -> Unit
) {

    val firestore =
        remember {
            FirebaseFirestore.getInstance()
        }

    var memories by remember {
        mutableStateOf<List<Memory>>(
            emptyList()
        )
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        firestore
            .collection("memories")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener {
                    snapshot,
                    error ->

                loading = false

                if (
                    error == null &&
                    snapshot != null
                ) {

                    memories =
                        snapshot.documents.map {
                                document ->

                            Memory(

                                id =
                                    document.id,

                                title =
                                    document
                                        .getString(
                                            "title"
                                        )
                                        ?: "خاطره ما",

                                date =
                                    document
                                        .getString(
                                            "date"
                                        )
                                        ?: "",

                                description =
                                    document
                                        .getString(
                                            "description"
                                        )
                                        ?: "",

                                imageUrl =
                                    document
                                        .getString(
                                            "imageUrl"
                                        )
                                        ?: ""
                            )
                        }
                }
            }
    }

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize()
        ) {

            MemoriesHeader(

                onBack =
                    onBack,

                onAdd = {
                    showAddDialog = true
                }
            )

            when {

                loading -> {

                    Box(
                        modifier =
                            Modifier.fillMaxSize(),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator(
                            color = Rose
                        )
                    }
                }

                memories.isEmpty() -> {

                    EmptyMemories(
                        onAdd = {
                            showAddDialog = true
                        }
                    )
                }

                else -> {

                    LazyColumn(

                        modifier =
                            Modifier.fillMaxSize(),

                        contentPadding =
                            PaddingValues(
                                horizontal = 18.dp,
                                vertical = 16.dp
                            ),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                14.dp
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
        }

        if (showAddDialog) {

            AddMemoryDialog(

                onDismiss = {
                    showAddDialog = false
                }
            )
        }
    }
}

// ============================================================
// MEMORIES HEADER
// ============================================================

@Composable
fun MemoriesHeader(
    onBack: () -> Unit,
    onAdd: () -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(

            modifier =
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.88f
                        )
                    )
                    .clickable {
                        onBack()
                    },

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "→",
                fontSize = 23.sp,
                color = DeepRose
            )
        }

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    "خاطرات ما 📸",
                fontSize = 23.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                color = TextDark
            )

            Text(
                text =
                    "لحظه‌هایی که نباید فراموش شوند",
                fontSize = 11.sp,
                color = SoftText
            )
        }

        Box(

            modifier =
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors =
                                listOf(
                                    Rose,
                                    DeepRose
                                )
                        )
                    )
                    .clickable {
                        onAdd()
                    },

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "+",
                color = Color.White,
                fontSize = 25.sp
            )
        }
    }
}

// ============================================================
// EMPTY MEMORIES
// ============================================================

@Composable
fun EmptyMemories(
    onAdd: () -> Unit
) {

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
                text = "💗",
                fontSize = 55.sp
            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Text(
                text =
                    "هنوز خاطره‌ای ثبت نشده",
                fontSize = 18.sp,
                fontWeight =
                    FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Text(
                text =
                    "اولین لحظه قشنگتون رو اینجا ثبت کنید",
                fontSize = 13.sp,
                color = SoftText,
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Card(

                modifier =
                    Modifier.clickable {
                        onAdd()
                    },

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor = Rose
                    )
            ) {

                Text(
                    text =
                        "ثبت اولین خاطره ❤️",
                    color = Color.White,
                    fontWeight =
                        FontWeight.Bold,
                    modifier =
                        Modifier.padding(
                            horizontal = 22.dp,
                            vertical = 13.dp
                        )
                )
            }
        }
    }
}

// ============================================================
// MEMORY CARD
// ============================================================

@Composable
fun MemoryCard(
    memory: Memory
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(25.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(
                        alpha = 0.90f
                    )
            )
    ) {

        Column(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            if (
                memory.imageUrl.isNotBlank()
            ) {

                AsyncImage(

                    model =
                        memory.imageUrl,

                    contentDescription =
                        memory.title,

                    contentScale =
                        ContentScale.Crop,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 25.dp,
                                    topEnd = 25.dp
                                )
                            )
                )
            }

            Column(
                modifier =
                    Modifier.padding(17.dp)
            ) {

                Text(
                    text =
                        memory.title,
                    fontSize = 18.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = TextDark
                )

                if (
                    memory.date.isNotBlank()
                ) {

                    Text(
                        text =
                            "📅 ${memory.date}",
                        fontSize = 11.sp,
                        color = Rose,
                        modifier =
                            Modifier.padding(
                                top = 5.dp
                            )
                    )
                }

                if (
                    memory.description.isNotBlank()
                ) {

                    Text(
                        text =
                            memory.description,
                        fontSize = 13.sp,
                        color = SoftText,
                        lineHeight = 22.sp,
                        modifier =
                            Modifier.padding(
                                top = 9.dp
                            )
                    )
                }
            }
        }
    }
}

// ============================================================
// ADD MEMORY
// ============================================================

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
                text =
                    "ثبت یک خاطره ❤️",
                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            Column(

                modifier =
                    Modifier
                        .heightIn(
                            max = 430.dp
                        )
                        .verticalScroll(
                            rememberScrollState()
                        ),

                verticalArrangement =
                    Arrangement.spacedBy(
                        10.dp
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

                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(

                    value = date,

                    onValueChange = {
                        date = it
                    },

                    label = {
                        Text("تاریخ")
                    },

                    placeholder = {
                        Text(
                            "مثلاً ۱۴۰۵/۰۳/۲۰"
                        )
                    },

                    singleLine = true,

                    modifier =
                        Modifier.fillMaxWidth()
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

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            TextButton(

                enabled =
                    !saving &&
                            title.isNotBlank(),

                onClick = {

                    val user =
                        FirebaseAuth
                            .getInstance()
                            .currentUser

                    if (user == null) {

                        return@TextButton
                    }

                    saving = true

                    val memory =
                        hashMapOf<String, Any>(

                            "title" to
                                    title.trim(),

                            "date" to
                                    date.trim(),

                            "description" to
                                    description.trim(),

                            "createdBy" to
                                    user.uid,

                            "createdAt" to
                                    FieldValue
                                        .serverTimestamp()
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
                    text =
                        if (saving) {
                            "در حال ذخیره..."
                        } else {
                            "ذخیره ❤️"
                        },

                    color = DeepRose
                )
            }
        },

        dismissButton = {

            TextButton(

                enabled =
                    !saving,

                onClick = {
                    onDismiss()
                }
            ) {

                Text("لغو")
            }
        }
    )
}

// ============================================================
// DAYS TOGETHER
// ============================================================

fun calculateDaysTogether(): Long {

    val calendar =
        Calendar.getInstance()

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

    val startDate =
        calendar.time

    val today =
        Date()

    val difference =
        today.time -
                startDate.time

    val days =
        difference /
                (
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
