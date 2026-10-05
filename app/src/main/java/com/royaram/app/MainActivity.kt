package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

private val Pink = Color(0xFFE85D86)
private val LightPink = Color(0xFFFFE7EF)
private val DeepPink = Color(0xFFB83D63)
private val TextDark = Color(0xFF33252B)
private val SoftText = Color(0xFF82747A)
private val BackgroundPink = Color(0xFFFFF4F7)

private val RelationshipStartDate: Calendar = Calendar.getInstance().apply {
    set(2026, Calendar.JUNE, 10, 0, 0, 0)
    set(Calendar.MILLISECOND, 0)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoyaramApp(
                onChatClick = {
                    startActivity(
                        Intent(this, ChatActivity::class.java)
                    )
                },
                onMemoriesClick = {
                    Toast.makeText(
                        this,
                        "خاطرات ما 💕",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onLettersClick = {
                    Toast.makeText(
                        this,
                        "نامه‌های عاشقانه 💌",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onMusicClick = {
                    Toast.makeText(
                        this,
                        "آهنگ ما 🎵",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onSadClick = {
                    Toast.makeText(
                        this,
                        "وقتی دلمون گرفت، اینجا کنار همیم ❤️",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onSpecialClick = {
                    Toast.makeText(
                        this,
                        "لحظه‌های خاص ✨",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onPrivateClick = {
                    Toast.makeText(
                        this,
                        "بخش خصوصی 🔐",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onSettingsClick = {
                    Toast.makeText(
                        this,
                        "تنظیمات رویارام 💗",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}

@Composable
fun RoyaramApp(
    onChatClick: () -> Unit,
    onMemoriesClick: () -> Unit,
    onLettersClick: () -> Unit,
    onMusicClick: () -> Unit,
    onSadClick: () -> Unit,
    onSpecialClick: () -> Unit,
    onPrivateClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    MaterialTheme {
        HomeScreen(
            onChatClick = onChatClick,
            onMemoriesClick = onMemoriesClick,
            onLettersClick = onLettersClick,
            onMusicClick = onMusicClick,
            onSadClick = onSadClick,
            onSpecialClick = onSpecialClick,
            onPrivateClick = onPrivateClick,
            onSettingsClick = onSettingsClick
        )
    }
}

data class HomeCard(
    val title: String,
    val subtitle: String,
    val type: CardType
)

enum class CardType {
    MEMORIES,
    LETTERS,
    MUSIC,
    SAD,
    CHAT,
    SPECIAL,
    PRIVATE,
    SETTINGS
}

@Composable
fun HomeScreen(
    onChatClick: () -> Unit,
    onMemoriesClick: () -> Unit,
    onLettersClick: () -> Unit,
    onMusicClick: () -> Unit,
    onSadClick: () -> Unit,
    onSpecialClick: () -> Unit,
    onPrivateClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val today = Calendar.getInstance()

    val daysTogether = remember(today.timeInMillis) {
        val difference =
            today.timeInMillis - RelationshipStartDate.timeInMillis

        (difference / (1000L * 60L * 60L * 24L)).toInt()
            .coerceAtLeast(0)
    }

    val pulse = rememberInfiniteTransition(
        label = "heartPulse"
    )

    val heartScale by pulse.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    val cards = listOf(
        HomeCard(
            "خاطرات ما",
            "لحظه‌های قشنگمون",
            CardType.MEMORIES
        ),
        HomeCard(
            "نامه‌های عاشقانه",
            "حرف‌هایی از ته قلبمون",
            CardType.LETTERS
        ),
        HomeCard(
            "آهنگ ما",
            "صدای قصه‌ی عشق ما",
            CardType.MUSIC
        ),
        HomeCard(
            "وقتی دلمون گرفت",
            "اینجا همیشه کنار همیم",
            CardType.SAD
        ),
        HomeCard(
            "چت دونفره",
            "حرف‌های من و تو ❤️",
            CardType.CHAT
        ),
        HomeCard(
            "لحظه‌های خاص",
            "تاریخ‌های مهم ما ✨",
            CardType.SPECIAL
        ),
        HomeCard(
            "بخش خصوصی",
            "فقط برای من و تو 🔐",
            CardType.PRIVATE
        ),
        HomeCard(
            "تنظیمات",
            "شخصی‌سازی رویارام",
            CardType.SETTINGS
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFE4ED),
                        BackgroundPink,
                        Color.White
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "تنظیمات",
                tint = DeepPink,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onSettingsClick()
                    }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "رویارام",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = DeepPink,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "قصه‌ی من و تو، برای همیشه",
            fontSize = 15.sp,
            color = SoftText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.88f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.couple_main
                    ),
                    contentDescription = "رامین و رویا",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(205.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Pink,
                    modifier = Modifier
                        .size(38.dp)
                        .scale(heartScale)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "رامین ❤️ رویا",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "هر روز یک صفحه‌ی تازه از قصه‌ی ما",
                    fontSize = 14.sp,
                    color = SoftText
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$daysTogether",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepPink
                )

                Text(
                    text = "❤️ روز کنار هم",
                    fontSize = 14.sp,
                    color = SoftText
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "دنیای دونفره‌ی ما",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(620.dp),
            contentPadding = PaddingValues(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {

            items(cards) { card ->

                CoupleCard(
                    card = card,
                    onClick = {

                        when (card.type) {

                            CardType.MEMORIES ->
                                onMemoriesClick()

                            CardType.LETTERS ->
                                onLettersClick()

                            CardType.MUSIC ->
                                onMusicClick()

                            CardType.SAD ->
                                onSadClick()

                            CardType.CHAT ->
                                onChatClick()

                            CardType.SPECIAL ->
                                onSpecialClick()

                            CardType.PRIVATE ->
                                onPrivateClick()

                            CardType.SETTINGS ->
                                onSettingsClick()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.85f)
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.» ❤️",
                    fontSize = 15.sp,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ساخته شده با ❤️ برای رامین و رویا",
            fontSize = 12.sp,
            color = SoftText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CoupleCard(
    card: HomeCard,
    onClick: () -> Unit
) {

    val icon = when (card.type) {

        CardType.MEMORIES ->
            Icons.Default.PhotoLibrary

        CardType.LETTERS ->
            Icons.Default.Mail

        CardType.MUSIC ->
            Icons.Default.MusicNote

        CardType.SAD ->
            Icons.Default.Favorite

        CardType.CHAT ->
            Icons.Default.Chat

        CardType.SPECIAL ->
            Icons.Default.Star

        CardType.PRIVATE ->
            Icons.Default.Lock

        CardType.SETTINGS ->
            Icons.Default.Settings
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.88f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        color = LightPink,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = card.title,
                    tint = DeepPink,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = card.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = card.subtitle,
                fontSize = 12.sp,
                color = SoftText,
                textAlign = TextAlign.Center
            )
        }
    }
}
