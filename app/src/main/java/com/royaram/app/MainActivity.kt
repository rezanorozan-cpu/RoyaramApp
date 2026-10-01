package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

private val PageBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFE3ED),
        Color(0xFFF6ECFF),
        Color(0xFFFFF8FB)
    )
)

private val RelationshipStartDate = Calendar.getInstance().apply {
    set(2026, Calendar.JUNE, 10, 0, 0, 0)
    set(Calendar.MILLISECOND, 0)
}.time

data class Memory(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val description: String = "",
    val imageUrl: String = ""
)

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
    var currentPage by remember { mutableStateOf("home") }

    when (currentPage) {
        "home" -> {
            HomeScreen(
                onMemoriesClick = {
                    currentPage = "memories"
                },
                onChatClick = onChatClick,
                onSpecialClick = {
                    currentPage = "special"
                },
                onToast = onToast,
                onHomeClick = {
                    currentPage = "home"
                }
            )
        }

        "memories" -> {
            MemoriesScreen(
                onBack = {
                    currentPage = "home"
                },
                onHomeClick = {
                    currentPage = "home"
                },
                onSpecialClick = {
                    currentPage = "special"
                },
                onToast = onToast
            )
        }

        "special" -> {
            SpecialDatesScreen(
                onBack = {
                    currentPage = "home"
                },
                onHomeClick = {
                    currentPage = "home"
                },
                onMemoriesClick = {
                    currentPage = "memories"
                }
            )
        }
    }
}

@Composable
fun HomeScreen(
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit,
    onSpecialClick: () -> Unit,
    onToast: (String) -> Unit,
    onHomeClick: () -> Unit
) {
    val daysTogether = remember {
        calculateDaysTogether()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 14.dp,
                bottom = 105.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                PremiumTopBar(
                    onSettingsClick = {
                        onToast("تنظیمات رویارام 💗")
                    }
                )
            }

            item {
                PremiumHero(
                    daysTogether = daysTogether
                )
            }

            item {
                RelationshipGlassCard(
                    daysTogether = daysTogether
                )
            }

            item {
                RoyaramFolderLayout(
                    onMemoriesClick = onMemoriesClick,
                    onChatClick = onChatClick,
                    onSpecialClick = onSpecialClick,
                    onToast = onToast
                )
            }

            item {
                DailyWordsCard()
            }

            item {
                CompactLoveFooter()
            }
        }

        PremiumBottomBar(
            selected = "home",
            onHomeClick = onHomeClick,
            onMemoriesClick = onMemoriesClick,
            onSpecialClick = onSpecialClick
        )
    }
}

@Composable
fun PremiumTopBar(
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "رویارام",
                fontSize = 25.sp,
                fontWeight = FontWeight.Black,
                color = DeepRose
            )

            Text(
                text = "قصه‌ی من و تو، برای همیشه",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = SoftText
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = Rose.copy(alpha = 0.16f)
                )
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.78f))
                .clickable {
                    onSettingsClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚙",
                fontSize = 20.sp,
                color = DeepRose
            )
        }
    }
}

@@Composable
fun PremiumHero(
    daysTogether: Int
) {
    val transition = rememberInfiniteTransition(
        label = "heroPulse"
    )

    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.018f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp)
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = Rose.copy(alpha = 0.18f)
            ),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.78f)
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // طبیعت پشت عکس
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFB9D9D0),
                                Color(0xFFDFE8D9),
                                Color(0xFFFFE1D8)
                            )
                        )
                    )
            )

            // عکس اصلی کوچک‌تر و بدون کشیدگی
            AsyncImage(
                model = R.drawable.royaram_photo_1,
                contentDescription = "رامین و رویا",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp)
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(
                        RoundedCornerShape(
                            topStart = 30.dp,
                            topEnd = 30.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            // لایه طبیعی برای ترکیب عکس با پس‌زمینه
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(205.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.05f),
                                Color.Black.copy(alpha = 0.60f)
                            )
                        )
                    )
            )

            // آیکون کوچک گوشه بالا
            Surface(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.86f),
                shadowElevation = 5.dp
            ) {
                Text(
                    text = "❤️",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 14.sp
                )
            }

            // متن پایین
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(
                        horizontal = 18.dp,
                        vertical = 13.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "رامین ❤️ رویا",
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "هر روز یک صفحه‌ی تازه از قصه‌ی ما",
                    color = Color.White.copy(alpha = 0.94f),
                    fontSize = 9.sp
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White.copy(alpha = 0.23f)
                ) {
                    Text(
                        text = "❤️ $daysTogether روز کنار هم",
                        modifier = Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 5.dp
                        ),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
@Composable
fun RelationshipGlassCard(
    daysTogether: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(25.dp),
                ambientColor = Rose.copy(alpha = 0.10f)
            ),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.80f)
        )
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
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Rose,
                                Lavender
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♥",
                    color = Color.White,
                    fontSize = 25.sp
                )
            }

            Spacer(
                modifier = Modifier.width(13.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "از ۲۰ خرداد ۱۴۰۵",
                    fontSize = 10.sp,
                    color = SoftText,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "داستان ما از همین‌جا شروع شد ✨",
                    fontSize = 13.sp,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = daysTogether.toString(),
                    fontSize = 23.sp,
                    color = DeepRose,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "روز",
                    fontSize = 9.sp,
                    color = SoftText
                )
            }
        }
    }
}

@Composable
fun RoyaramFolderLayout(
    onMemoriesClick: () -> Unit,
    onChatClick: () -> Unit,
    onSpecialClick: () -> Unit,
    onToast: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        SectionTitle(
            title = "دنیای دونفره‌ی ما",
            subtitle = "همه‌ی لحظه‌های رامین ❤️ رویا"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {

            RoyaramFolderCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_2,
                title = "خاطرات ما",
                subtitle = "لحظه‌های قشنگمون",
                iconText = "📸",
                onClick = onMemoriesClick
            )

            RoyaramFolderCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_3,
                title = "نامه‌ها",
                subtitle = "حرف‌های قلبمون",
                iconText = "💌",
                onClick = {
                    onToast("نامه‌های عاشقانه به‌زودی 💌")
                }
            )

            RoyaramFolderCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_4,
                title = "آهنگ ما",
                subtitle = "صدای عشق ما",
                iconText = "🎵",
                onClick = {
                    onToast("آهنگ ما به‌زودی 🎵")
                }
            )
        }

        Spacer(
            modifier = Modifier.height(9.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {

            RoyaramFolderCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_5,
                title = "کنار هم",
                subtitle = "وقتی دلمون گرفت",
                iconText = "🤍",
                onClick = {
                    onToast("اینجا همیشه کنار همیم 🤍")
                }
            )

            RoyaramFolderCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_6,
                title = "چت دونفره",
                subtitle = "حرف‌های من و تو",
                iconText = "💬",
                onClick = onChatClick
            )

            RoyaramFolderCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_7,
                title = "لحظه‌های خاص",
                subtitle = "تاریخ‌های مهم ما",
                iconText = "✨",
                onClick = onSpecialClick
            )
        }

        Spacer(
            modifier = Modifier.height(9.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {

            RoyaramMiniCard(
                modifier = Modifier.weight(1f),
                iconText = "🔐",
                title = "بخش خصوصی",
                subtitle = "فقط من و تو",
                onClick = {
                    onToast("بخش خصوصی رویارام 🔐")
                }
            )

            RoyaramMiniCard(
                modifier = Modifier.weight(1f),
                iconText = "📁",
                title = "گنجینه",
                subtitle = "چیزهای خاص ما",
                onClick = {
                    onToast("گنجینه‌ی رویارام به‌زودی ✨")
                }
            )

            RoyaramMiniCard(
                modifier = Modifier.weight(1f),
                iconText = "⚙",
                title = "تنظیمات",
                subtitle = "شخصی‌سازی",
                onClick = {
                    onToast("تنظیمات رویارام 💗")
                }
            )
        }
    }
}

@Composable
fun SectionTitle(
    title: String = "دنیای دونفره‌مون",
    subtitle: String = "همه‌ی لحظه‌های رامین ❤️ رویا"
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = TextDark
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = subtitle,
            fontSize = 9.sp,
            color = SoftText
        )
    }
}

@Composable
fun RoyaramFolderCard(
    modifier: Modifier = Modifier,
    image: Int,
    title: String,
    subtitle: String,
    iconText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(0.82f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(23.dp),
                ambientColor = Rose.copy(alpha = 0.10f)
            )
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.82f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 23.dp,
                            topEnd = 23.dp
                        )
                    )
            ) {

                AsyncImage(
    model = image,
    contentDescription = title,
    modifier = Modifier
        .fillMaxSize()
        .padding(5.dp)
        .clip(RoundedCornerShape(18.dp)),
    contentScale = ContentScale.Fit
)
                Surface(
                    modifier = Modifier
                        .padding(7.dp)
                        .align(Alignment.TopEnd),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.84f)
                ) {
                    Text(
                        text = iconText,
                        modifier = Modifier.padding(6.dp),
                        fontSize = 13.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 7.dp
                    )
            ) {

                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subtitle,
                    fontSize = 8.sp,
                    color = SoftText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RoyaramMiniCard(
    modifier: Modifier = Modifier,
    iconText: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(83.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.78f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                SoftPink,
                                PaleLavender
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconText,
                    fontSize = 17.sp
                )
            }

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subtitle,
                    fontSize = 7.sp,
                    color = SoftText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun DailyWordsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Rose.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.80f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Rose,
                                Lavender
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✦",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "کلام امروز ❤️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepRose
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.»",
                    fontSize = 9.sp,
                    color = TextDark,
                    lineHeight = 15.sp
                )
            }

            Text(
                text = "♡",
                fontSize = 25.sp,
                color = Rose
            )
        }
    }
}

@Composable
fun CompactLoveFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 3.dp,
                bottom = 4.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "ساخته شده با ❤️ برای رامین و رویا",
            fontSize = 9.sp,
            color = SoftText,
            fontWeight = FontWeight.Medium
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = "رویـارام • فقط برای ما",
            fontSize = 7.sp,
            color = Rose
        )
    }
}

@Composable
fun PremiumBottomBar(
    selected: String,
    onHomeClick: () -> Unit,
    onMemoriesClick: () -> Unit,
    onSpecialClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = 18.dp,
                vertical = 10.dp
            )
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Rose.copy(alpha = 0.18f)
                ),
            shape = RoundedCornerShape(28.dp),
            color = Color.White.copy(alpha = 0.90f)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 7.dp
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                BottomBarItem(
                    icon = "⌂",
                    title = "خانه",
                    selected = selected == "home",
                    onClick = onHomeClick
                )

                BottomBarItem(
                    icon = "♡",
                    title = "خاطرات",
                    selected = selected == "memories",
                    onClick = onMemoriesClick
                )

                BottomBarItem(
                    icon = "✦",
                    title = "لحظه‌های خاص",
                    selected = selected == "special",
                    onClick = onSpecialClick
                )
            }
        }
    }
}

@Composable
fun BottomBarItem(
    icon: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(18.dp),
        color = if (selected) {
            SoftPink.copy(alpha = 0.72f)
        } else {
            Color.Transparent
        }
    ) {

        Column(
            modifier = Modifier
                .padding(
                    horizontal = 18.dp,
                    vertical = 5.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = icon,
                fontSize = 19.sp,
                color = if (selected) {
                    DeepRose
                } else {
                    SoftText
                }
            )

            Text(
                text = title,
                fontSize = 8.sp,
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                },
                color = if (selected) {
                    DeepRose
                } else {
                    SoftText
                }
            )
        }
    }
}

@Composable
fun MemoriesScreen(
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onSpecialClick: () -> Unit,
    onToast: (String) -> Unit
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

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    DisposableEffect(Unit) {

        val registration = firestore
            .collection("memories")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, error ->

                loading = false

                if (error != null) {
                    onToast("خطا در دریافت خاطرات")
                    return@addSnapshotListener
                }

                memories = snapshot?.documents?.map { document ->

                    Memory(
                        id = document.id,
                        title = document.getString("title").orEmpty(),
                        date = document.getString("date").orEmpty(),
                        description = document
                            .getString("description")
                            .orEmpty(),
                        imageUrl = document
                            .getString("imageUrl")
                            .orEmpty()
                    )

                } ?: emptyList()
            }

        onDispose {
            registration.remove()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 14.dp,
                bottom = 105.dp
            ),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {

            item {
                MemoriesHeader(
                    onBack = onBack,
                    onAdd = {
                        showAddDialog = true
                    }
                )
            }

            item {
                MemoriesIntro()
            }

            if (loading) {

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Rose
                        )
                    }
                }

            } else if (memories.isEmpty()) {

                item {
                    EmptyMemories(
                        onAdd = {
                            showAddDialog = true
                        }
                    )
                }

            } else {

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

        PremiumBottomBar(
            selected = "memories",
            onHomeClick = onHomeClick,
            onMemoriesClick = {},
            onSpecialClick = onSpecialClick
        )
    }

    if (showAddDialog) {
        AddMemoryDialog(
            onDismiss = {
                showAddDialog = false
            },
            onSaved = {
                showAddDialog = false
                onToast("خاطره با عشق ذخیره شد ❤️")
            }
        )
    }
}

@Composable
fun MemoriesHeader(
    onBack: () -> Unit,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.82f))
                .clickable {
                    onBack()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "→",
                fontSize = 20.sp,
                color = DeepRose
            )
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "خاطرات ما",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = DeepRose
            )

            Text(
                text = "لحظه‌هایی که هیچ‌وقت فراموش نمی‌شن ❤️",
                fontSize = 9.sp,
                color = SoftText
            )
        }

        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Rose,
                            Lavender
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

@Composable
fun MemoriesIntro() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.76f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SoftPink),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📸",
                    fontSize = 22.sp
                )
            }

            Spacer(
                modifier = Modifier.width(11.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "دفتر خاطرات رامین و رویا",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "هر خاطره یک تکه از داستان ماست.",
                    fontSize = 9.sp,
                    color = SoftText
                )
            }
        }
    }
}

@Composable
fun EmptyMemories(
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.80f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "♡",
                fontSize = 50.sp,
                color = Rose
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "هنوز خاطره‌ای ثبت نشده",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "اولین لحظه‌ی قشنگتون رو اینجا ثبت کنید ❤️",
                fontSize = 9.sp,
                color = SoftText,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Rose
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "ثبت اولین خاطره",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
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
                shape = RoundedCornerShape(25.dp),
                ambientColor = Rose.copy(alpha = 0.09f)
            ),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.84f)
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
                        .height(210.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 25.dp,
                                topEnd = 25.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = memory.title.ifBlank {
                            "یک خاطره‌ی قشنگ ❤️"
                        },
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )

                    if (memory.date.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PalePink
                        ) {
                            Text(
                                text = memory.date,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 5.dp
                                ),
                                fontSize = 8.sp,
                                color = DeepRose,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (memory.description.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = memory.description,
                        fontSize = 10.sp,
                        color = SoftText,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemoryDialog(
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

    var imageUrl by remember {
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
        containerColor = Cream,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "ثبت یک خاطره ❤️",
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                color = DeepRose
            )
        },
        text = {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = "عنوان خاطره"
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = "تاریخ"
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = "توضیح خاطره"
                        )
                    },
                    minLines = 3,
                    maxLines = 4,
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = {
                        imageUrl = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = "لینک عکس، در صورت وجود"
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp)
                )
            }
        },
        confirmButton = {

            TextButton(
                enabled = !saving,
                onClick = {

                    if (title.isBlank()) {
                        return@TextButton
                    }

                    val user = FirebaseAuth
                        .getInstance()
                        .currentUser

                    if (user == null) {
                        return@TextButton
                    }

                    saving = true

                    val data = hashMapOf(
                        "title" to title.trim(),
                        "date" to date.trim(),
                        "description" to description.trim(),
                        "imageUrl" to imageUrl.trim(),
                        "createdBy" to user.uid,
                        "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("memories")
                        .add(data)
                        .addOnSuccessListener {
                            saving = false
                            onSaved()
                        }
                        .addOnFailureListener {
                            saving = false
                        }
                }
            ) {
                if (saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Rose,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "ذخیره ❤️",
                        color = DeepRose,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {

            TextButton(
                enabled = !saving,
                onClick = onDismiss
            ) {
                Text(
                    text = "انصراف",
                    color = SoftText
                )
            }
        }
    )
}

@Composable
fun SpecialDatesScreen(
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onMemoriesClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 14.dp,
                bottom = 105.dp
            ),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(43.dp)
                            .clip(CircleShape)
                            .background(
                                Color.White.copy(alpha = 0.82f)
                            )
                            .clickable {
                                onBack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "→",
                            fontSize = 20.sp,
                            color = DeepRose
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Column {
                        Text(
                            text = "لحظه‌های خاص",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = DeepRose
                        )

                        Text(
                            text = "تاریخ‌هایی که برای ما معنی دارن ✨",
                            fontSize = 9.sp,
                            color = SoftText
                        )
                    }
                }
            }

            item {
                DateInfoCard(
                    icon = "❤️",
                    title = "شروع داستان ما",
                    date = "۲۰ خرداد ۱۴۰۵",
                    description = "روزی که قصه‌ی رامین و رویا شروع شد."
                )
            }

            item {
                DateInfoCard(
                    icon = "💗",
                    title = "هر ماه با هم",
                    date = "ماهگرد ما",
                    description = "یک ماه دیگر از با هم بودنمان گذشت."
                )
            }

            item {
                DateInfoCard(
                    icon = "🎂",
                    title = "تولدها",
                    date = "به‌زودی ثبت می‌شود",
                    description = "تولد رامین و رویا را اینجا ذخیره می‌کنیم."
                )
            }

            item {
                DateInfoCard(
                    icon = "💍",
                    title = "سالگرد",
                    date = "هر سال",
                    description = "سال‌های بیشتری از داستانمان را کنار هم جشن می‌گیریم."
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(27.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DeepRose
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(21.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "«با تو، تاریخ‌ها فقط عدد نیستن؛",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "هر کدومشون یک خاطره‌ان.» ❤️",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        PremiumBottomBar(
            selected = "special",
            onHomeClick = onHomeClick,
            onMemoriesClick = onMemoriesClick,
            onSpecialClick = {}
        )
    }
}

@Composable
fun DateInfoCard(
    icon: String,
    title: String,
    date: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.82f)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                SoftPink,
                                PaleLavender
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 22.sp
                )
            }

            Spacer(
                modifier = Modifier.width(11.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = date,
                    fontSize = 10.sp,
                    color = DeepRose,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = description,
                    fontSize = 8.sp,
                    color = SoftText,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

fun calculateDaysTogether(): Int {
    val now = Calendar.getInstance().time

    val difference =
        now.time - RelationshipStartDate.time

    val days =
        difference / (1000L * 60L * 60L * 24L)

    return days
        .coerceAtLeast(0L)
        .toInt()
}
