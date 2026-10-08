package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import java.util.Calendar
import kotlin.math.max

private val Pink = Color(0xFFE85D86)
private val LightPink = Color(0xFFFFE7EF)
private val DeepPink = Color(0xFFB83D63)
private val TextDark = Color(0xFF33252B)
private val SoftText = Color(0xFF82747A)
private val BackgroundPink = Color(0xFFFFF4F7)

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var showMemories by remember {
                mutableStateOf(false)
            }

            var showLetters by remember {
                mutableStateOf(false)
            }

            var showMusic by remember {
                mutableStateOf(false)
            }

            var showSadMoments by remember {
                mutableStateOf(false)
            }

            var showSpecialMoments by remember {
                mutableStateOf(false)
            }

            var showPrivate by remember {
                mutableStateOf(false)
            }

            when {

                showMemories -> {
                    MemoriesScreen(
                        onBack = {
                            showMemories = false
                        }
                    )
                }

                showLetters -> {
                    LettersScreen(
                        onBack = {
                            showLetters = false
                        }
                    )
                }

                showMusic -> {
                    MusicScreen(
                        onBack = {
                            showMusic = false
                        }
                    )
                }

                showSadMoments -> {
                    SadMomentsScreen(
                        onBack = {
                            showSadMoments = false
                        }
                    )
                }

                showSpecialMoments -> {
                    SpecialMomentsScreen(
                        onBack = {
                            showSpecialMoments = false
                        }
                    )
                }

                showPrivate -> {
                    PrivateScreen(
                        activity = this@MainActivity,
                        onBack = {
                            showPrivate = false
                        }
                    )
                }

                else -> {

                    RoyaramApp(
                        onChatClick = {

                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    ChatActivity::class.java
                                )
                            )
                        },

                        onMemoriesClick = {
                            showMemories = true
                        },

                        onLettersClick = {
                            showLetters = true
                        },

                        onMusicClick = {
                            showMusic = true
                        },

                        onSadClick = {
                            showSadMoments = true
                        },

                        onSpecialClick = {
                            showSpecialMoments = true
                        },

                        onPrivateClick = {
                            showPrivate = true
                        },

                        onSettingsClick = {

                            Toast.makeText(
                                this@MainActivity,
                                "تنظیمات رویارام 💗",
                                Toast.LENGTH_SHORT
                            ).show()
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
    onMemoriesClick: () -> Unit,
    onLettersClick: () -> Unit,
    onMusicClick: () -> Unit,
    onSadClick: () -> Unit,
    onSpecialClick: () -> Unit,
    onPrivateClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToast: (String) -> Unit
) {

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

    val daysTogether = remember {
        calculateDaysTogether()
    }

    LazyColumn(
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
            ),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 18.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {

                LuxuryBanner()

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "تنظیمات",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(26.dp)
                        .clickable {
                            onSettingsClick()
                        }
                )
            }
        }

        item {

            MainDateCard(
                daysTogether = daysTogether
            )
        }

        item {

            Text(
                text = "دنیای دونفره‌ی ما",
                color = TextDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    start = 4.dp,
                    top = 4.dp
                )
            )
        }

        item {

            HomeCardRow(
                first = {

                    ImageFolderCard(
                        title = "خاطرات ما",
                        subtitle = "لحظه‌های قشنگمون",
                        icon = "📸",
                        onClick = onMemoriesClick
                    )
                },

                second = {

                    ImageFolderCard(
                        title = "نامه‌های عاشقانه",
                        subtitle = "حرف‌های قلبمون",
                        icon = "💌",
                        onClick = onLettersClick
                    )
                },

                third = {

                    ImageFolderCard(
                        title = "آهنگ ما",
                        subtitle = "صدای قصه‌ی ما",
                        icon = "🎵",
                        onClick = onMusicClick
                    )
                }
            )
        }

        item {

            HomeCardRow(
                first = {

                    ImageFolderCard(
                        title = "وقتی دلمون گرفت",
                        subtitle = "اینجا همیشه کنار همیم",
                        icon = "🌷",
                        onClick = onSadClick
                    )
                },

                second = {

                    ImageFolderCard(
                        title = "لحظه‌های خاص",
                        subtitle = "تاریخ‌های مهم ما ✨",
                        icon = "✨",
                        onClick = onSpecialClick
                    )
                },

                third = {

                    ImageFolderCard(
                        title = "بخش خصوصی",
                        subtitle = "فقط برای من و تو 🔐",
                        icon = "🔐",
                        onClick = onPrivateClick
                    )
                }
            )
        }

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    ImageFolderCard(
                        title = "تنظیمات",
                        subtitle = "شخصی‌سازی رویارام",
                        icon = "⚙️",
                        onClick = onSettingsClick
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    LoveQuoteCard()
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    Spacer(
                        modifier = Modifier.height(1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LuxuryBanner() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(
                color = LightPink,
                shape = RoundedCornerShape(28.dp)
            )
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.couple_main
            ),
            contentDescription = "رامین و رویا",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {

            Text(
                text = "رویارام",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "قصه‌ی من و تو، برای همیشه",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MainDateCard(
    daysTogether: Int
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "رامین ❤️ رویا",
                color = DeepPink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "هر روز یک صفحه‌ی تازه از قصه‌ی ما",
                color = SoftText,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "$daysTogether",
                color = Pink,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "❤️ روز کنار هم",
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            BirthdayMiniRow()
        }
    }
}

@Composable
fun BirthdayMiniRow() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        BirthdayItem(
            icon = "🎂",
            title = "تولد رامین",
            date = "۲۰ شهریور"
        )

        BirthdayItem(
            icon = "🎂",
            title = "تولد رویا",
            date = "۱۵ آذر"
        )

        BirthdayItem(
            icon = "💗",
            title = "سالگرد",
            date = "۲۰ خرداد"
        )
    }
}

@Composable
private fun BirthdayItem(
    icon: String,
    title: String,
    date: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = icon,
            fontSize = 20.sp
        )

        Text(
            text = title,
            fontSize = 11.sp,
            color = SoftText
        )

        Text(
            text = date,
            fontSize = 11.sp,
            color = TextDark,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HomeCardRow(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    third: @Composable () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Box(
            modifier = Modifier.weight(1f)
        ) {
            first()
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            second()
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            third()
        }
    }
}

@Composable
fun ImageFolderCard(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.94f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = icon,
                fontSize = 30.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = title,
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = subtitle,
                color = SoftText,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoveQuoteCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightPink
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Pink,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.» ❤️",
                color = TextDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun calculateDaysTogether(): Int {

    val start = Calendar.getInstance().apply {
        clear()

        set(
            Calendar.YEAR,
            Calendar.JUNE,
            10
        )
    }

    val today = Calendar.getInstance()

    val difference =
        today.timeInMillis - start.timeInMillis

    val days =
        difference / (1000L * 60L * 60L * 24L)

    return max(
        0,
        days.toInt()
    )
}package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
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
import androidx.fragment.app.FragmentActivity
import java.util.Calendar
import kotlin.math.max

private val Pink = Color(0xFFE85D86)
private val LightPink = Color(0xFFFFE7EF)
private val DeepPink = Color(0xFFB83D63)
private val TextDark = Color(0xFF33252B)
private val SoftText = Color(0xFF82747A)
private val BackgroundPink = Color(0xFFFFF4F7)

private const val START_YEAR = 1405
private const val START_MONTH = 3
private const val START_DAY = 20

private const val RAMIN_BIRTH_MONTH = 6
private const val RAMIN_BIRTH_DAY = 20

private const val ROYA_BIRTH_MONTH = 9
private const val ROYA_BIRTH_DAY = 15

private const val PERIOD_START_MONTH = 7
private const val PERIOD_START_DAY = 3
private const val PERIOD_END_MONTH = 7
private const val PERIOD_END_DAY = 10

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var showMemories by remember {
                mutableStateOf(false)
            }

            var showLetters by remember {
                mutableStateOf(false)
            }

            var showMusic by remember {
                mutableStateOf(false)
            }

            var showSadMoments by remember {
                mutableStateOf(false)
            }

            var showSpecialMoments by remember {
                mutableStateOf(false)
            }

            var showPrivate by remember {
                mutableStateOf(false)
            }

            when {

                showMemories -> {
                    MemoriesScreen(
                        onBack = {
                            showMemories = false
                        }
                    )
                }

                showLetters -> {
                    LettersScreen(
                        onBack = {
                            showLetters = false
                        }
                    )
                }

                showMusic -> {
                    MusicScreen(
                        onBack = {
                            showMusic = false
                        }
                    )
                }

                showSadMoments -> {
                    SadMomentsScreen(
                        onBack = {
                            showSadMoments = false
                        }
                    )
                }

                showSpecialMoments -> {
                    SpecialMomentsScreen(
                        onBack = {
                            showSpecialMoments = false
                        }
                    )
                }

                showPrivate -> {
                    PrivateScreen(
                        activity = this@MainActivity,
                        onBack = {
                            showPrivate = false
                        }
                    )
                }

                else -> {
                    RoyaramApp(
                        onChatClick = {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    ChatActivity::class.java
                                )
                            )
                        },

                        onMemoriesClick = {
                            showMemories = true
                        },

                        onLettersClick = {
                            showLetters = true
                        },

                        onMusicClick = {
                            showMusic = true
                        },

                        onSadClick = {
                            showSadMoments = true
                        },

                        onSpecialClick = {
                            showSpecialMoments = true
                        },

                        onPrivateClick = {
                            showPrivate = true
                        },

                        onSettingsClick = {
                            Toast.makeText(
                                this@MainActivity,
                                "تنظیمات رویارام 💗",
                                Toast.LENGTH_SHORT
                            ).show()
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
    onMemoriesClick: () -> Unit,
    onLettersClick: () -> Unit,
    onMusicClick: () -> Unit,
    onSadClick: () -> Unit,
    onSpecialClick: () -> Unit,
    onPrivateClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToast: (String) -> Unit
) {
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

    val daysTogether = remember {
        calculateDaysTogether()
    }

    LazyColumn(
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
            ),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 18.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {

                LuxuryBanner()

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "تنظیمات",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(26.dp)
                        .clickable {
                            onSettingsClick()
                        }
                )
            }
        }

        item {

            MainDateCard(
                daysTogether = daysTogether
            )
        }

        item {

            Text(
                text = "دنیای دونفره‌ی ما",
                color = TextDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    start = 4.dp,
                    top = 4.dp
                )
            )
        }

        item {

            HomeCardRow(
                first = {
                    ImageFolderCard(
                        title = "خاطرات ما",
                        subtitle = "لحظه‌های قشنگمون",
                        icon = "📸",
                        onClick = onMemoriesClick
                    )
                },
                second = {
                    ImageFolderCard(
                        title = "نامه‌های عاشقانه",
                        subtitle = "حرف‌های قلبمون",
                        icon = "💌",
                        onClick = onLettersClick
                    )
                },
                third = {
                    ImageFolderCard(
                        title = "آهنگ ما",
                        subtitle = "صدای قصه‌ی ما",
                        icon = "🎵",
                        onClick = onMusicClick
                    )
                }
            )
        }

        item {

            HomeCardRow(
                first = {
                    ImageFolderCard(
                        title = "وقتی دلمون گرفت",
                        subtitle = "اینجا همیشه کنار همیم",
                        icon = "🌷",
                        onClick = onSadClick
                    )
                },
                second = {
                    ImageFolderCard(
                        title = "لحظه‌های خاص",
                        subtitle = "تاریخ‌های مهم ما ✨",
                        icon = "✨",
                        onClick = onSpecialClick
                    )
                },
                third = {
                    ImageFolderCard(
                        title = "بخش خصوصی",
                        subtitle = "فقط برای من و تو 🔐",
                        icon = "🔐",
                        onClick = onPrivateClick
                    )
                }
            )
        }

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    ImageFolderCard(
                        title = "تنظیمات",
                        subtitle = "شخصی‌سازی رویارام",
                        icon = "⚙️",
                        onClick = onSettingsClick
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    LoveQuoteCard()
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(
                        modifier = Modifier.height(1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LuxuryBanner() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(
                color = LightPink,
                shape = RoundedCornerShape(28.dp)
            )
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.couple_main
            ),
            contentDescription = "رامین و رویا",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.05f)
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {

            Text(
                text = "رویارام",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "قصه‌ی من و تو، برای همیشه",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MainDateCard(
    daysTogether: Int
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.92f
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "رامین ❤️ رویا",
                color = DeepPink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "هر روز یک صفحه‌ی تازه از قصه‌ی ما",
                color = SoftText,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "$daysTogether",
                color = Pink,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "❤️ روز کنار هم",
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            BirthdayMiniRow()
        }
    }
}

@Composable
fun BirthdayMiniRow() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🎂",
                fontSize = 20.sp
            )

            Text(
                text = "تولد رامین",
                fontSize = 11.sp,
                color = SoftText
            )

            Text(
                text = "۲۰ شهریور",
                fontSize = 11.sp,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🎂",
                fontSize = 20.sp
            )

            Text(
                text = "تولد رویا",
                fontSize = 11.sp,
                color = SoftText
            )

            Text(
                text = "۱۵ آذر",
                fontSize = 11.sp,
                color = TextDark,
                fontWeight = FontWeight.Bold
@Composable
fun MainDateCard(
    daysTogether: Int
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.92f
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "رامین ❤️ رویا",
                color = DeepPink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "هر روز یک صفحه‌ی تازه از قصه‌ی ما",
                color = SoftText,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "$daysTogether",
                color = Pink,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "❤️ روز کنار هم",
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

@Composable
fun BirthdayMiniRow() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎂",
                fontSize = 20.sp
            )

            Text(
                text = "تولد رامین",
                fontSize = 11.sp,
                color = SoftText
            )

            Text(
                text = "۲۰ شهریور",
                fontSize = 11.sp,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎂",
                fontSize = 20.sp
            )

            Text(
                text = "تولد رویا",
                fontSize = 11.sp,
                color = SoftText
            )

            Text(
                text = "۱۵ آذر",
                fontSize = 11.sp,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💗",
                fontSize = 20.sp
            )

            Text(
                text = "سالگرد",
                fontSize = 11.sp,
                color = SoftText
            )

            Text(
                text = "۲۰ خرداد",
                fontSize = 11.sp,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HomeCardRow(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    third: @Composable () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Box(
            modifier = Modifier.weight(1f)
        ) {
            first()
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            second()
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            third()
        }
    }
}

@Composable
fun ImageFolderCard(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.94f
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = icon,
                fontSize = 30.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = title,
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = subtitle,
                color = SoftText,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoveQuoteCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightPink
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Pink,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.» ❤️",
                color = TextDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PeriodPrivateCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.94f
            )
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Text(
                text = "🌷 یادمون باشه کنار هم باشیم",
                color = DeepPink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "در روزهای سخت، حتی یک پیام کوچیک هم می‌تونه حال همدیگه رو بهتر کنه ❤️",
                color = SoftText,
                fontSize = 12.sp
            )
        }
    }
}

private fun calculateDaysTogether(): Int {

    val calendar = Calendar.getInstance()

    val start = Calendar.getInstance().apply {
        clear()
        set(
            calendar.get(Calendar.YEAR),
            Calendar.JUNE,
            10
        )
    }

    val today = Calendar.getInstance()

    val difference =
        today.timeInMillis - start.timeInMillis

    val days =
        difference / (1000L * 60L * 60L * 24L)

    return max(
        0,
        days.toInt()
    )
}

@Composable
fun HomeCardRow(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    third: @Composable () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Box(
            modifier = Modifier.weight(1f)
        ) {
            first()
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            second()
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            third()
        }
    }
}

@Composable
fun ImageFolderCard(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.94f
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = icon,
                fontSize = 30.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = title,
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = subtitle,
                color = SoftText,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoveQuoteCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightPink
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Pink,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.» ❤️",
                color = TextDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PeriodPrivateCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.94f
            )
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Text(
                text = "🌷 یادمون باشه کنار هم باشیم",
                color = DeepPink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "در روزهای سخت، حتی یک پیام کوچیک هم می‌تونه حال همدیگه رو بهتر کنه ❤️",
                color = SoftText,
                fontSize = 12.sp
            )
        }
    }
}

private fun calculateDaysTogether(): Int {

    val calendar = Calendar.getInstance()

    val start = Calendar.getInstance().apply {
        clear()
        set(
            calendar.get(Calendar.YEAR),
            Calendar.JUNE,
            10
        )
    }

    val today = Calendar.getInstance()

    val difference =
        today.timeInMillis - start.timeInMillis

    val days =
        difference / (1000L * 60L * 60L * 24L)

    return max(
        0,
        days.toInt()
    )
}
