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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

// ------------------------------------------------------------
// COLORS
// ------------------------------------------------------------

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

private val MainBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFE9F1),
        Color(0xFFF8F0FF),
        Color(0xFFFFF9FB)
    )
)

// ------------------------------------------------------------
// DATA
// ------------------------------------------------------------

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

// ------------------------------------------------------------
// ACTIVITY
// ------------------------------------------------------------

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

// ------------------------------------------------------------
// ROOT
// ------------------------------------------------------------

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

// ------------------------------------------------------------
// HOME
// ------------------------------------------------------------

@Composable
fun HomeScreen(
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit
) {

    val daysTogether = remember {
        calculateDaysTogether()
    }

    var showContent by remember {
        mutableStateOf(false)
    }

    var heartPulse by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        showContent = true

        while (true) {

            heartPulse = !heartPulse

            kotlinx.coroutines.delay(1000)
        }
    }

    val heartScale by animateFloatAsState(

        targetValue =
            if (heartPulse) {
                1.08f
            } else {
                0.94f
            },

        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),

        label = "heart_pulse"
    )

    LazyColumn(

        modifier = Modifier
            .fillMaxSize(),

        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 18.dp,
            bottom = 36.dp
        ),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(500)
                ) +
                        slideInVertically(
                            initialOffsetY = { -30 },
                            animationSpec = tween(500)
                        )
            ) {

                PremiumHeader()
            }
        }

        item {

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(700)
                ) +
                        slideInVertically(
                            initialOffsetY = { 50 },
                            animationSpec = tween(700)
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
                enter = fadeIn(
                    animationSpec = tween(850)
                ) +
                        slideInVertically(
                            initialOffsetY = { 60 },
                            animationSpec = tween(850)
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
                enter = fadeIn(
                    animationSpec = tween(1000)
                ) +
                        slideInVertically(
                            initialOffsetY = { 70 },
                            animationSpec = tween(1000)
                        )
            ) {

                TodayMessageCard()
            }
        }

        item {

            Text(
                text = "دنیای دونفره‌مون",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(
                    top = 4.dp,
                    bottom = 2.dp
                )
            )
        }

        item {

            FeatureGrid(
                onMemoriesClick = onMemoriesClick,
                onChatClick = onChatClick
            )
        }

        item {

            RomanticFooter()
        }
    }
}

// ------------------------------------------------------------
// HEADER
// ------------------------------------------------------------

@Composable
fun PremiumHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 2.dp,
                vertical = 4.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "رویـارام",
                fontSize = 29.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DeepRose
            )

            Text(
                text = "جایی برای من و تو ❤️",
                fontSize = 13.sp,
                color = SoftText,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = Rose.copy(alpha = 0.18f),
                    spotColor = Rose.copy(alpha = 0.18f)
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Rose,
                            DeepRose
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "♡",
                color = Color.White,
                fontSize = 27.sp
            )
        }
    }
}

// ------------------------------------------------------------
// HERO PHOTO
// ------------------------------------------------------------

@Composable
fun HeroCoupleCard(
    daysTogether: Long,
    heartScale: Float
) {

    var imageStarted by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        kotlinx.coroutines.delay(400)

        imageStarted = true
    }

    val photoScale by animateFloatAsState(

        targetValue =
            if (imageStarted) {
                1.055f
            } else {
                1f
            },

        animationSpec = tween(
            durationMillis = 2600,
            easing = FastOutSlowInEasing
        ),

        label = "photo_scale"
    )

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = Rose.copy(alpha = 0.20f),
                spotColor = Rose.copy(alpha = 0.20f)
            ),

        shape = RoundedCornerShape(30.dp),

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
                    }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.03f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.65f)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .clip(
                        RoundedCornerShape(50.dp)
                    )
                    .background(
                        Color.White.copy(alpha = 0.88f)
                    )
                    .padding(
                        horizontal = 13.dp,
                        vertical = 8.dp
                    )
            ) {

                Text(
                    text = "❤️ $daysTogether روز",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepRose
                )
            }

            Column(

                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "رامین  ❤️  رویا",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "هر لحظه کنار هم، یک خاطره قشنگ‌تر",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.92f)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        scaleX = heartScale
                        scaleY = heartScale
                    }
            ) {

                Text(
                    text = "♥",
                    color = Color.White.copy(alpha = 0.90f),
                    fontSize = 42.sp
                )
            }
        }
    }
}

// ------------------------------------------------------------
// RELATIONSHIP COUNTER
// ------------------------------------------------------------

@Composable
fun RelationshipCard(
    daysTogether: Long
) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = Lavender.copy(alpha = 0.15f),
                spotColor = Lavender.copy(alpha = 0.15f)
            ),

        shape = RoundedCornerShape(26.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.82f)
        )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(PalePink),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "💕",
                    fontSize = 30.sp
                )
            }

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "داستان قشنگمون",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Text(
                    text = "از ۲۰ خرداد ۱۴۰۵",
                    fontSize = 12.sp,
                    color = SoftText,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = daysTogether.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepRose
                )

                Text(
                    text = "روز",
                    fontSize = 11.sp,
                    color = SoftText
                )
            }
        }
    }
}

// ------------------------------------------------------------
// TODAY MESSAGE
// ------------------------------------------------------------

@Composable
fun TodayMessageCard() {

    Card(

        modifier = Modifier
            .fillMaxWidth(),

        shape = RoundedCornerShape(26.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            DeepRose,
                            Rose,
                            Lavender
                        )
                    ),
                    RoundedCornerShape(26.dp)
                )
                .padding(22.dp)
        ) {

            Column {

                Text(
                    text = "پیام امروز برای تو 💌",
                    color = Color.White.copy(alpha = 0.90f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                Text(
                    text = "«بعضی آدم‌ها فقط وارد زندگی‌مان نمی‌شوند؛ خانه‌ی قلبمان می‌شوند.»",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 27.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "برای تو ❤️",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ------------------------------------------------------------
// FEATURE GRID
// ------------------------------------------------------------

@Composable
fun FeatureGrid(
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit
) {

    val items = listOf(

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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        for (rowIndex in 0 until 4) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                val firstIndex = rowIndex * 2
                val secondIndex = firstIndex + 1

                FeatureCard(
                    item = items[firstIndex],
                    modifier = Modifier.weight(1f),
                    onClick = {

                        if (items[firstIndex].title == "خاطرات ما") {
                            onMemoriesClick()
                        }

                        if (items[firstIndex].title == "چت دونفره") {
                            onChatClick()
                        }
                    }
                )

                FeatureCard(
                    item = items[secondIndex],
                    modifier = Modifier.weight(1f),
                    onClick = {

                        if (items[secondIndex].title == "خاطرات ما") {
                            onMemoriesClick()
                        }

                        if (items[secondIndex].title == "چت دونفره") {
                            onChatClick()
                        }
                    }
                )
            }
        }
    }
}

// ------------------------------------------------------------
// FEATURE CARD
// ------------------------------------------------------------

@Composable
fun FeatureCard(
    item: HomeItem,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Card(

        modifier = modifier
            .aspectRatio(0.95f)
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Rose.copy(alpha = 0.10f),
                spotColor = Rose.copy(alpha = 0.10f)
            )
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.86f)
        )
    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                PalePink,
                                PaleLavender
                            )
                        )
                    ),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = item.emoji,
                    fontSize = 28.sp
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = item.subtitle,
                fontSize = 10.sp,
                color = SoftText,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}

// ------------------------------------------------------------
// FOOTER
// ------------------------------------------------------------

@Composable
fun RomanticFooter() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 10.dp,
                bottom = 10.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "رامین ❤️ رویا",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DeepRose
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "یک دنیای کوچک، فقط برای دو نفر",
            fontSize = 12.sp,
            color = SoftText
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "♡  رویارام  ♡",
            fontSize = 11.sp,
            color = Rose.copy(alpha = 0.75f)
        )
    }
}

// ------------------------------------------------------------
// MEMORIES SCREEN
// ------------------------------------------------------------

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

        firestore
            .collection("memories")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, error ->

                loading = false

                if (error == null && snapshot != null) {

                    memories = snapshot.documents.map { document ->

                        Memory(

                            id = document.id,

                            title =
                                document.getString("title")
                                    ?: "خاطره ما",

                            date =
                                document.getString("date")
                                    ?: "",

                            description =
                                document.getString("description")
                                    ?: "",

                            imageUrl =
                                document.getString("imageUrl")
                                    ?: ""
                        )
                    }
                }
            }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            MemoriesHeader(
                onBack = onBack,
                onAdd = {
                    showAddDialog = true
                }
            )

            when {

                loading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
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

                        modifier = Modifier.fillMaxSize(),

                        contentPadding = PaddingValues(
                            horizontal = 18.dp,
                            vertical = 16.dp
                        ),

                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        items(
                            memories,
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

// ------------------------------------------------------------
// MEMORIES HEADER
// ------------------------------------------------------------

@Composable
fun MemoriesHeader(
    onBack: () -> Unit,
    onAdd: () -> Unit
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
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.85f))
                .clickable {
                    onBack()
                },

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "→",
                fontSize = 23.sp,
                color = DeepRose
            )
        }

        Spacer(
            modifier = Modifier.size(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "خاطرات ما 📸",
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )

            Text(
                text = "لحظه‌هایی که نباید فراموش شوند",
                fontSize = 11.sp,
                color = SoftText
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Rose,
                            DeepRose
                        )
                    )
                )
                .clickable {
                    onAdd()
                },

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "+",
                color = Color.White,
                fontSize = 25.sp
            )
        }
    }
}

// ------------------------------------------------------------
// EMPTY MEMORIES
// ------------------------------------------------------------

@Composable
fun EmptyMemories(
    onAdd: () -> Unit
) {

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
                text = "💗",
                fontSize = 55.sp
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "هنوز خاطره‌ای ثبت نشده",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = "اولین لحظه قشنگتون رو اینجا ثبت کنید",
                fontSize = 13.sp,
                color = SoftText,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Card(

                modifier = Modifier
                    .clickable {
                        onAdd()
                    },

                shape = RoundedCornerShape(18.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Rose
                )
            ) {

                Text(
                    text = "ثبت اولین خاطره ❤️",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = 22.dp,
                        vertical = 13.dp
                    )
                )
            }
        }
    }
}

// ------------------------------------------------------------
// MEMORY CARD
// ------------------------------------------------------------

@Composable
fun MemoryCard(
    memory: Memory
) {

    Card(

        modifier = Modifier
            .fillMaxWidth(),

        shape = RoundedCornerShape(25.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            if (memory.imageUrl.isNotBlank()) {

                AsyncImage(

                    model = memory.imageUrl,

                    contentDescription = memory.title,

                    contentScale = ContentScale.Crop,

                    modifier = Modifier
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
                modifier = Modifier.padding(17.dp)
            ) {

                Text(
                    text = memory.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                if (memory.date.isNotBlank()) {

                    Text(
                        text = "📅 ${memory.date}",
                        fontSize = 11.sp,
                        color = Rose,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }

                if (memory.description.isNotBlank()) {

                    Text(
                        text = memory.description,
                        fontSize = 13.sp,
                        color = SoftText,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(top = 9.dp)
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------
// ADD MEMORY DIALOG
// ------------------------------------------------------------

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
                text = "ثبت یک خاطره ❤️",
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .heightIn(max = 430.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),

                verticalArrangement = Arrangement.spacedBy(10.dp)
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

                OutlinedTextField(

                    value = date,

                    onValueChange = {
                        date = it
                    },

                    label = {
                        Text("تاریخ")
                    },

                    placeholder = {
                        Text("مثلاً ۱۴۰۵/۰۳/۲۰")
                    },

                    singleLine = true,

                    modifier = Modifier.fillMaxWidth()
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

                enabled =
                    !saving &&
                            title.isNotBlank(),

                onClick = {

                    saving = true

                    val memory =
                        hashMapOf<String, Any>(

                            "title" to title.trim(),

                            "date" to date.trim(),

                            "description" to description.trim(),

                            "createdBy" to "رامین و رویا",

                            "createdAt" to
                                    FieldValue.serverTimestamp()
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

                enabled = !saving,

                onClick = {
                    onDismiss()
                }
            ) {

                Text("لغو")
            }
        }
    )
}

// ------------------------------------------------------------
// DAYS COUNTER
// ------------------------------------------------------------

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
