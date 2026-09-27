package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Pink = Color(0xFFE85D86)
private val LightPink = Color(0xFFFFE7EF)
private val DeepPink = Color(0xFFB83D63)
private val TextDark = Color(0xFF33252B)
private val SoftText = Color(0xFF82747A)
private val BackgroundPink = Color(0xFFFFF4F7)

private val RelationshipStartDate = Date(
    126,
    5,
    10
)

data class HomeItem(
    val title: String,
    val subtitle: String,
    val iconType: String,
    val color: Color
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

    val daysTogether =
        remember {

            val now = System.currentTimeMillis()

            val start =
                RelationshipStartDate.time

            ((now - start) /
                    (1000L * 60L * 60L * 24L))
                .toInt()
                .coerceAtLeast(0)
        }

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "heart"
        )

    val heartScale by
        infiniteTransition.animateFloat(
            initialValue = 0.96f,
            targetValue = 1.05f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(1300),
                    repeatMode =
                        RepeatMode.Reverse
                ),
            label = "heartScale"
        )

    val items =
        listOf(

            HomeItem(
                "خاطرات ما",
                "لحظه‌های قشنگمون",
                "memory",
                Pink
            ),

            HomeItem(
                "نامه‌های عاشقانه",
                "حرف‌هایی از قلبمون",
                "letter",
                Color(0xFFE8759B)
            ),

            HomeItem(
                "آهنگ ما",
                "صدای خاطره‌هامون",
                "music",
                Color(0xFFD9547F)
            ),

            HomeItem(
                "وقتی دلمون گرفت",
                "اینجا همیشه کنار همیم",
                "sad",
                Color(0xFFE38AA4)
            ),

            HomeItem(
                "چت دونفره",
                "حرف‌های من و تو ❤️",
                "chat",
                Color(0xFFE34F78)
            ),

            HomeItem(
                "لحظه‌های خاص",
                "تاریخ‌های مهم ما ✨",
                "special",
                Color(0xFFC86A8B)
            ),

            HomeItem(
                "بخش خصوصی",
                "فقط برای من و تو 🔐",
                "private",
                Color(0xFF9E6277)
            ),

            HomeItem(
                "تنظیمات",
                "شخصی‌سازی رویارام",
                "settings",
                Color(0xFFAD788B)
            )
        )

    LazyColumn(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFE4ED),
                            BackgroundPink,
                            Color.White
                        )
                    )
                )
                .statusBarsPadding(),

        contentPadding =
            PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 28.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        item {

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 4.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier =
                            Modifier
                                .size(62.dp)
                                .clip(
                                    RoundedCornerShape(22.dp)
                                )
                                .background(
                                    Color.White.copy(
                                        alpha = 0.85f
                                    )
                                ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "❤️",
                            fontSize = 31.sp,
                            modifier =
                                Modifier.graphicsLayer {
                                    scaleX =
                                        heartScale
                                    scaleY =
                                        heartScale
                                }
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(12.dp)
                    )

                    Column {

                        Text(
                            text = "رویارام",
                            fontSize = 27.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color = TextDark
                        )

                        Text(
                            text =
                                "قصه‌ی من و تو، برای همیشه",
                            fontSize = 12.sp,
                            color = SoftText
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onToast(
                            "تنظیمات رویارام 💗"
                        )
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Settings,
                        contentDescription =
                            "تنظیمات",
                        tint = DeepPink
                    )
                }
            }
        }

        item {

            HeroPhotoCard(
                daysTogether = daysTogether
            )
        }

        item {

            TodayMessageCard()
        }

        item {

            Text(
                text = "دنیای دونفره‌ی ما",
                modifier =
                    Modifier.padding(
                        horizontal = 4.dp
                    ),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        items(
            items.chunked(2)
        ) { rowItems ->

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                rowItems.forEach { item ->

                    HomeCard(
                        item = item,
                        modifier =
                            Modifier.weight(1f),

                        onClick = {

                            when (item.iconType) {

                                "memory" ->
                                    onMemoriesClick()

                                "chat" ->
                                    onChatClick()

                                "letter" ->
                                    onToast(
                                        "نامه‌های عاشقانه به‌زودی فعال می‌شود 💌"
                                    )

                                "music" ->
                                    onToast(
                                        "بخش آهنگ ما به‌زودی فعال می‌شود 🎵"
                                    )

                                "sad" ->
                                    onToast(
                                        "اینجا همیشه کنار همیم ❤️"
                                    )

                                "special" ->
                                    onToast(
                                        "لحظه‌های خاص به‌زودی آماده می‌شود ✨"
                                    )

                                "private" ->
                                    onToast(
                                        "این بخش مخصوص رامین و رویاست 🔐"
                                    )

                                "settings" ->
                                    onToast(
                                        "تنظیمات رویارام به‌زودی آماده می‌شود ⚙️"
                                    )
                            }
                        }
                    )
                }

                if (rowItems.size == 1) {

                    Spacer(
                        modifier =
                            Modifier.weight(1f)
                    )
                }
            }
        }

        item {

            FooterCard()
        }
    }
}

@Composable
private fun HeroPhotoCard(
    daysTogether: Int
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(30.dp),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 7.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor = Color.White
            )
    ) {

        Column {

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(275.dp)
            ) {

                Image(

                    painter =
                        painterResource(
                            id = R.drawable.couple_main
                        ),

                    contentDescription =
                        "رامین و رویا",

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(
                                RoundedCornerShape(
                                    bottomStart = 30.dp,
                                    bottomEnd = 30.dp
                                )
                            ),

                    contentScale =
                        ContentScale.Crop
                )

                Box(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            Color(0xAA000000)
                                        )
                                )
                            )
                )

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
                            "رامین ❤️ رویا",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "هر روز یک صفحه‌ی تازه از قصه‌ی ما",
                        color =
                            Color.White.copy(
                                alpha = 0.88f
                            ),
                        fontSize = 12.sp
                    )
                }
            }

            Column(
                modifier =
                    Modifier.padding(18.dp)
            ) {

                Box(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(22.dp)
                            )
                            .background(
                                LightPink
                            )
                            .padding(
                                vertical = 14.dp
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                daysTogether.toString(),
                            fontSize = 35.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color = Pink
                        )

                        Text(
                            text =
                                "❤️ روز کنار هم",
                            fontSize = 13.sp,
                            color = DeepPink
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text =
                        "شروع قصه: ۲۰ خرداد ۱۴۰۵",
                    modifier =
                        Modifier.fillMaxWidth(),
                    textAlign =
                        TextAlign.Center,
                    fontSize = 11.sp,
                    color = SoftText
                )
            }
        }
    }
}

@Composable
private fun TodayMessageCard() {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(25.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFFFEDF3)
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "💌",
                    fontSize = 25.sp
                )

                Spacer(
                    modifier =
                        Modifier.width(9.dp)
                )

                Text(
                    text =
                        "جمله‌ی امروز برای ما",
                    fontSize = 17.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.» ❤️",
                fontSize = 14.sp,
                lineHeight = 24.sp,
                color = SoftText
            )
        }
    }
}

@Composable
private fun HomeCard(
    item: HomeItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(

        modifier =
            modifier
                .height(178.dp)
                .clickable {
                    onClick()
                }
                .animateContentSize(),

        shape =
            RoundedCornerShape(28.dp),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor = Color.White
            )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(18.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Box(

                modifier =
                    Modifier
                        .size(62.dp)
                        .clip(
                            RoundedCornerShape(22.dp)
                        )
                        .background(
                            LightPink
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                HomeIcon(
                    type = item.iconType,
                    color = item.color
                )
            }

            Spacer(
                modifier =
                    Modifier.height(13.dp)
            )

            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.Bold,
                color = TextDark,
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text = item.subtitle,
                fontSize = 11.sp,
                color = SoftText,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}

@Composable
private fun HomeIcon(
    type: String,
    color: Color
) {

    val icon = when (type) {

        "memory" ->
            Icons.Default.PhotoLibrary

        "letter" ->
            Icons.Default.Mail

        "music" ->
            Icons.Default.MusicNote

        "sad" ->
            Icons.Default.SentimentSatisfiedAlt

        "chat" ->
            Icons.Default.Chat

        "special" ->
            Icons.Default.Star

        "private" ->
            Icons.Default.Lock

        "settings" ->
            Icons.Default.Settings

        else ->
            Icons.Default.Favorite
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier =
            Modifier.size(30.dp)
    )
}

@Composable
private fun FooterCard() {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),

        shape =
            RoundedCornerShape(25.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFFFEAF1)
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "∞",
                fontSize = 28.sp,
                color = Pink,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    "قصه‌ی من و تو، برای همیشه",
                fontSize = 14.sp,
                fontWeight =
                    FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "ساخته شده با ❤️ برای رامین و رویا",
                fontSize = 11.sp,
                color = SoftText
            )
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
                        colors =
                            listOf(
                                Color(0xFFFFE5EE),
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
                        horizontal = 16.dp,
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
                    text = "← بازگشت",
                    color = DeepPink
                )
            }

            Text(
                text = "خاطرات ما ❤️",
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
                        text = "🌷",
                        fontSize = 55.sp
                    )

                    Spacer(
                        modifier =
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
                        modifier =
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
                        modifier =
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
                            text = "افزودن اولین خاطره"
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
                    modifier =
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
                                LightPink
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
                    modifier =
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
                        color = TextDark
                    )

                    if (memory.date.isNotBlank()) {

                        Text(
                            text =
                                memory.date,
                            fontSize = 11.sp,
                            color = Pink
                        )
                    }
                }
            }

            if (memory.description.isNotBlank()) {

                Spacer(
                    modifier =
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
                text = "خاطره‌ی جدید ❤️",
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
                    modifier =
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
                        Text("مثلاً ۱۴۰۵/۰۳/۲۰")
                    }
                )

                Spacer(
                    modifier =
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
                            "title" to title.trim(),
                            "date" to date.trim(),
                            "description" to description.trim(),
                            "createdBy" to user.uid,
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
                    text =
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
                    text = "لغو",
                    color = SoftText
                )
            }
        }
    )
}
