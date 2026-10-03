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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.LayoutDirection
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
        Color(0xFFFFE2EC),
        Color(0xFFF4ECFF),
        Color(0xFFFFF9FB)
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
                RoyaramApp(
                    onChatClick = {
                        startActivity(
                            Intent(this, ChatActivity::class.java)
                        )
                    },
                    onToast = {
                        Toast.makeText(
                            this,
                            it,
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
    var page by remember {
        mutableStateOf("home")
    }

    when (page) {

        "home" -> {
            HomeScreen(
                onMemoriesClick = {
                    page = "memories"
                },
                onChatClick = onChatClick,
                onSpecialClick = {
                    page = "special"
                },
                onToast = onToast,
                onHomeClick = {
                    page = "home"
                }
            )
        }

        "memories" -> {
            MemoriesScreen(
                onBack = {
                    page = "home"
                },
                onHomeClick = {
                    page = "home"
                },
                onSpecialClick = {
                    page = "special"
                }
            )
        }

        "special" -> {
            SpecialDatesScreen(
                onBack = {
                    page = "home"
                },
                onHomeClick = {
                    page = "home"
                },
                onMemoriesClick = {
                    page = "memories"
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
                start = 12.dp,
                end = 12.dp,
                top = 5.dp,
                bottom = 105.dp
            ),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {

            // هدر رویارام
            item {
                PremiumTopBar(
                    onSettingsClick = {
                        onToast("تنظیمات رویارام 💗")
                    }
                )
            }

            // عکس بوسه — کوتاه‌تر و جمع‌وجورتر
            item {
                PremiumHero(
                    daysTogether = daysTogether
                )
            }

            // تاریخ آشنایی + تعداد روزها
            item {
                RelationshipGlassCard(
                    daysTogether = daysTogether
                )
            }

            // عنوان بخش‌ها
            item {
                SectionTitle(
                    title = "دنیای دونفره‌ی ما",
                    subtitle = "هر گوشه، یک تکه از قصه‌ی رامین ❤️ رویا"
                )
            }

            // خاطرات
            item {
                PremiumMemoryFeatureCard(
                    onClick = onMemoriesClick
                )
            }

            // نامه‌ها + آهنگ
            item {
                PremiumSplitCards(
                    onLettersClick = {
                        onToast("نامه‌های عاشقانه 💌 به‌زودی")
                    },
                    onMusicClick = {
                        onToast("آهنگ ما 🎵 به‌زودی")
                    }
                )
            }

            // وقتی دلمون گرفت
            item {
                PremiumWideLoveCard(
                    onClick = {
                        onToast("وقتی دلمون گرفت 🫂 همیشه کنار همیم")
                    }
                )
            }

            // سه بخش مستقل: چت، رویدادها، پوشه‌ها
item {
    PremiumTripleCards(
        onChatClick = onChatClick,
        onEventsClick = onSpecialClick,
        onFoldersClick = {
            onToast("پوشه‌های ما 📁 به‌زودی")
        }
    )
}
            // بخش خصوصی
            item {
                PremiumPrivateCard(
                    onClick = {
                        onToast("بخش خصوصی 🔐 به‌زودی")
                    }
                )
            }

            // کلام روزانه
            item {
                DailyWordsCard()
            }

            // پایین صفحه
            item {
                CompactLoveFooter()
            }
        }

        // نوار پایین
        PremiumBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
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
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text(
                text = "رویارام",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = DeepRose
            )

            Text(
                text = "قصه‌ی من و تو، برای همیشه",
                fontSize = 11.sp,
                color = SoftText
            )
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(
                    8.dp,
                    CircleShape,
                    ambientColor = Rose.copy(alpha = 0.18f)
                )
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.82f))
                .clickable {
                    onSettingsClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚙️",
                fontSize = 19.sp
            )
        }
    }
}

@Composable
fun PremiumHero(
    daysTogether: Int
) {
    val transition = rememberInfiniteTransition(label = "heroHeart")

    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .clip(RoundedCornerShape(29.dp))
            .shadow(
                elevation = 17.dp,
                shape = RoundedCornerShape(29.dp),
                ambientColor = Rose.copy(alpha = 0.20f)
            )
    ) {

        // پس‌زمینه‌ی نرم برای بخش خالی کارت
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF4E1E7),
                            Color(0xFFFFE9EF),
                            Color(0xFFF8E9F0)
                        )
                    )
                )
        )

        // همان عکس بوسه‌ی اصلی رویارام
        AsyncImage(
            model = "android.resource://com.royaram.app/drawable/royaram_photo_1",
            contentDescription = "رامین و رویا",
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.66f)
                .align(Alignment.CenterStart)
                .clip(
                    RoundedCornerShape(
                        topStart = 29.dp,
                        bottomStart = 29.dp,
                        topEnd = 24.dp,
                        bottomEnd = 24.dp
                    )
                ),
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center
        )

        // محو شدن نرم عکس به سمت متن
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0xFFF6E4EA).copy(alpha = 0.18f),
                            Color(0xFFF6E4EA).copy(alpha = 0.88f),
                            Color(0xFFF8EAF0).copy(alpha = 0.98f)
                        )
                    )
                )
        )

        // سایه‌ی خیلی ظریف پایین برای عمق بیشتر
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0xFF3B202A).copy(alpha = 0.16f)
                        )
                    )
                )
        )

        // قلب متحرک
        Text(
            text = "♥",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 13.dp,
                    end = 16.dp
                )
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                },
            color = DeepRose.copy(alpha = 0.92f),
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )

        // متن اصلی سمت راست
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(
                    start = 8.dp,
                    end = 17.dp,
                    top = 20.dp,
                    bottom = 14.dp
                ),
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = "رویارام",
                color = DeepRose,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "قصه‌ی من و تو، برای همیشه",
                color = TextDark.copy(alpha = 0.82f),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Color.White.copy(alpha = 0.72f)
                    )
                    .padding(
                        horizontal = 11.dp,
                        vertical = 7.dp
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "رامین ❤️ رویا",
                        color = DeepRose,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "$daysTogether روز کنار هم",
                        color = TextDark,
                        fontSize = 8.sp,
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
            .height(126.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.82f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 16.dp,
                    vertical = 13.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // بخش تاریخ
            Column(
                modifier = Modifier.weight(0.95f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "تاریخ آشنایی ما",
                    color = SoftText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "۱۴۰۵/۰۳/۲۰",
                    color = DeepRose,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "از همون روز...",
                        color = SoftText,
                        fontSize = 8.sp
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = "♥",
                        color = Rose,
                        fontSize = 12.sp
                    )
                }
            }

            // جداکننده‌ی ظریف
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(67.dp)
                    .background(
                        SoftPink.copy(alpha = 0.75f)
                    )
            )

            // شمارش روزها
            Column(
                modifier = Modifier.weight(1.05f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(61.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SoftPink,
                                    PaleLavender
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = daysTogether.toString(),
                            color = DeepRose,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = "روز",
                            color = SoftText,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "کنار هم ❤️",
                    color = TextDark,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // متن عاشقانه
            Column(
                modifier = Modifier.weight(1.05f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "هر روز با تو",
                    color = DeepRose,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "یک صفحه‌ی تازه\nاز قصه‌ی ماست...",
                    color = TextDark.copy(alpha = 0.82f),
                    fontSize = 9.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "رامین ❤️ رویا",
                    color = SoftText,
                    fontSize = 8.sp
                )
            }
        }
    }
}
@Composable
fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Black,
            color = TextDark
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = subtitle,
            fontSize = 9.sp,
            color = SoftText
        )
    }
}
@Composable
fun PremiumMemoryFeatureCard(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .clip(RoundedCornerShape(30.dp))
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = Rose.copy(alpha = 0.18f)
            )
            .clickable { onClick() }
            .background(Color.White)
    ) {

        // عکس خاطرات
        AsyncImage(
            model = "android.resource://com.royaram.app/drawable/royaram_photo_2",
            contentDescription = "خاطرات ما",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(30.dp)),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )

        // لایه‌ی شیشه‌ای و گرادیان برای خوانایی متن
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0xFF24151C).copy(alpha = 0.80f)
                        )
                    )
                )
        )

        // هاله‌ی لطیف صورتی
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Rose.copy(alpha = 0.05f),
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                )
        )

        // نشان قلب
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .size(43.dp)
                .clip(CircleShape)
                .background(
                    Color.White.copy(alpha = 0.24f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♡",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Black
            )
        }

        // متن کارت
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    bottom = 16.dp
                )
        ) {

            Text(
                text = "خاطرات ما",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "لحظه‌هایی که دلمون نمی‌خواد هیچ‌وقت فراموش بشن",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(22.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Color.White.copy(alpha = 0.18f)
                        )
                        .padding(horizontal = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لحظه‌های قشنگمون",
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = "❤️",
                    fontSize = 11.sp
                )
            }
        }
    }
}
@Composable
fun PremiumSplitCards(
    firstTitle: String = "نامه‌های عاشقانه",
    firstSubtitle: String = "حرف‌هایی از ته دل",
    firstIcon: String = "💌",
    secondTitle: String = "آهنگ ما",
    secondSubtitle: String = "صدای خاطره‌های ما",
    secondIcon: String = "🎵",
    onLettersClick: () -> Unit,
    onMusicClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        PremiumSmallPhotoCard(
            modifier = Modifier.weight(1f),
            imageRes = "android.resource://com.royaram.app/drawable/royaram_photo_3",
            icon = firstIcon,
            title = firstTitle,
            subtitle = firstSubtitle,
            onClick = onLettersClick
        )

        PremiumSmallPhotoCard(
            modifier = Modifier.weight(1f),
            imageRes = "android.resource://com.royaram.app/drawable/royaram_photo_4",
            icon = secondIcon,
            title = secondTitle,
            subtitle = secondSubtitle,
            onClick = onMusicClick
        )
    }
}
@Composable
fun PremiumTripleCards(
    onChatClick: () -> Unit,
    onEventsClick: () -> Unit,
    onFoldersClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {

        PremiumCompactCard(
            modifier = Modifier.weight(1f),
            icon = "💬",
            title = "چت",
            subtitle = "حرف‌های ما",
            onClick = onChatClick
        )

        PremiumCompactCard(
            modifier = Modifier.weight(1f),
            icon = "✨",
            title = "رویدادها",
            subtitle = "تاریخ‌های خاص",
            onClick = onEventsClick
        )

        PremiumCompactCard(
            modifier = Modifier.weight(1f),
            icon = "📁",
            title = "پوشه‌ها",
            subtitle = "دنیای ما",
            onClick = onFoldersClick
        )
    }
}

@Composable
fun PremiumCompactCard(
    modifier: Modifier,
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(86.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.82f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 7.dp,
                    vertical = 10.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                SoftPink,
                                PaleLavender
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 16.sp
                )
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = title,
                color = TextDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                color = SoftText,
                fontSize = 7.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
fun PremiumSmallPhotoCard(
    modifier: Modifier,
    imageRes: String,
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(132.dp)
            .clip(RoundedCornerShape(27.dp))
            .shadow(
                elevation = 11.dp,
                shape = RoundedCornerShape(27.dp),
                ambientColor = Rose.copy(alpha = 0.14f)
            )
            .clickable { onClick() }
            .background(Color.White)
    ) {

        AsyncImage(
            model = imageRes,
            contentDescription = title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(27.dp)),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )

        // گرادیان تیره‌ی پایین برای خوانایی
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0xFF24151C).copy(alpha = 0.86f)
                        )
                    )
                )
        )

        // هاله‌ی خیلی ظریف
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.03f),
                            Color.Transparent,
                            Rose.copy(alpha = 0.04f)
                        )
                    )
                )
        )

        // آیکون
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(11.dp)
                .size(35.dp)
                .clip(CircleShape)
                .background(
                    Color.White.copy(alpha = 0.22f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 17.sp
            )
        }

        // متن
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 13.dp,
                    end = 13.dp,
                    bottom = 12.dp
                )
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.90f),
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
fun PremiumWideLoveCard(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(RoundedCornerShape(27.dp))
            .shadow(
                elevation = 11.dp,
                shape = RoundedCornerShape(27.dp),
                ambientColor = Rose.copy(alpha = 0.14f)
            )
            .clickable { onClick() }
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFFE5ED),
                        Color(0xFFF4ECFF),
                        Color(0xFFFFF8FA)
                    )
                )
            )
    ) {

        // هاله‌ی نرم
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.72f),
                            Color.Transparent
                        )
                    )
                )
        )

        // قلب بزرگ
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 17.dp)
                .size(55.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            SoftPink,
                            PaleLavender
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♥",
                color = DeepRose,
                fontSize = 25.sp,
                fontWeight = FontWeight.Black
            )
        }

        // متن
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(
                    start = 17.dp,
                    end = 82.dp
                )
        ) {
            Text(
                text = "وقتی دلمون گرفت",
                color = DeepRose,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "اینجا همیشه کنار همیم",
                color = TextDark.copy(alpha = 0.78f),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "برای روزهای سخت، یک جای امن داریم ❤️",
                color = SoftText,
                fontSize = 7.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // علامت کوچک گوشه
        Text(
            text = "♡",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 14.dp,
                    top = 9.dp
                ),
            color = Rose.copy(alpha = 0.55f),
            fontSize = 16.sp
        )
    }
}
@Composable
fun PremiumPrivateCard(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clip(RoundedCornerShape(26.dp))
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = Lavender.copy(alpha = 0.16f)
            )
            .clickable { onClick() }
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFF1EAFF),
                        Color(0xFFFFEAF1),
                        Color(0xFFFFF8FB)
                    )
                )
            )
    ) {

        // هاله‌ی شیشه‌ای
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.72f),
                            Color.Transparent
                        )
                    )
                )
        )

        // آیکون قفل
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(49.dp)
                .clip(CircleShape)
                .background(
                    Color.White.copy(alpha = 0.72f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔐",
                fontSize = 22.sp
            )
        }

        // متن
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(
                    start = 17.dp,
                    end = 78.dp
                )
        ) {
            Text(
                text = "بخش خصوصی",
                color = DeepRose,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "فقط برای من و تو",
                color = TextDark.copy(alpha = 0.80f),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "دنیای کوچیک و خصوصی رامین ❤️ رویا",
                color = SoftText,
                fontSize = 7.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // نشان امنیت
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 13.dp,
                    top = 8.dp
                )
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Color.White.copy(alpha = 0.62f)
                )
                .padding(
                    horizontal = 7.dp,
                    vertical = 3.dp
                )
        ) {
            Text(
                text = "خصوصی",
                color = Lavender,
                fontSize = 6.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
@Composable
fun DailyWordsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.78f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 17.dp,
                    vertical = 11.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // آیکن قلب
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                SoftPink,
                                PaleLavender
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♥",
                    color = DeepRose,
                    fontSize = 20.sp
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "کلام امروز",
                    color = DeepRose,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.» ❤️",
                    color = TextDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "✦",
                color = Lavender,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CompactLoveFooter() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.68f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 14.dp,
                    vertical = 8.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "دوستت دارم...",
                color = DeepRose,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "نه فقط امروز، بلکه تا همیشه... ❤️",
                color = TextDark,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "رامین ❤️ رویا",
                color = SoftText,
                fontSize = 7.sp
            )
        }
    }
}

@Composable
fun PremiumBottomBar(
    modifier: Modifier = Modifier,
    selected: String,
    onHomeClick: () -> Unit,
    onMemoriesClick: () -> Unit,
    onSpecialClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.90f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 8.dp, vertical = 7.dp),
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
                title = "رویدادها",
                selected = selected == "special",
                onClick = onSpecialClick
            )
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
    val backgroundColor =
        if (selected) {
            Brush.linearGradient(
                colors = listOf(
                    SoftPink,
                    PaleLavender
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Transparent
                )
            )
        }

    Column(
        modifier = Modifier
            .width(82.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .background(backgroundColor)
            .padding(
                horizontal = 7.dp,
                vertical = 5.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(if (selected) 34.dp else 30.dp)
                .clip(CircleShape)
                .background(
                    if (selected) {
                        Color.White.copy(alpha = 0.72f)
                    } else {
                        Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                color = if (selected) DeepRose else SoftText,
                fontSize = if (selected) 20.sp else 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = title,
            color = if (selected) DeepRose else SoftText,
            fontSize = 8.sp,
            fontWeight = if (selected) {
                FontWeight.Black
            } else {
                FontWeight.SemiBold
            },
            maxLines = 1
        )
    }
}
@Composable
fun MemoriesScreen(
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onSpecialClick: () -> Unit
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

        val listener = firestore
            .collection("memories")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    loading = false
                    return@addSnapshotListener
                }

                memories = snapshot?.documents?.map { doc ->

                    Memory(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        date = doc.getString("date") ?: "",
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )

                } ?: emptyList()

                loading = false
            }

        onDispose {
            listener.remove()
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
                top = 15.dp,
                bottom = 115.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    EmptyMemories()
                }

            } else {

                items(
                    items = memories,
                    key = { it.id }
                ) { memory ->

                    MemoryCard(
                        memory = memory
                    )
                }
            }
        }

        PremiumBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.8f))
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "خاطرات ما",
                color = TextDark,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = "تکه‌هایی از زندگیِ دونفره‌مون",
                color = SoftText,
                fontSize = 9.sp
            )
        }

        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(Rose)
                .clickable {
                    onAdd()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun MemoriesIntro() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.68f)
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Text(
                text = "هر خاطره، یک لبخند دوباره ❤️",
                color = DeepRose,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "عکس‌ها و لحظه‌های قشنگتون رو اینجا نگه دارید.",
                color = SoftText,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun EmptyMemories() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(29.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.72f)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "📷",
                fontSize = 43.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "هنوز خاطره‌ای ثبت نشده",
                color = TextDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "اولین خاطره‌ی قشنگتون رو ثبت کنید ❤️",
                color = SoftText,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun MemoryCard(
    memory: Memory
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.82f)
        )
    ) {

        Column {

            if (memory.imageUrl.isNotBlank()) {

                AsyncImage(
                    model = memory.imageUrl,
                    contentDescription = memory.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
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
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = memory.title,
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                if (memory.date.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = memory.date,
                        color = DeepRose,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (memory.description.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Text(
                        text = memory.description,
                        color = SoftText,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "خاطره‌ی جدید ❤️",
                fontWeight = FontWeight.Black
            )
        },
        text = {

            Column {

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = {
                        Text("عنوان خاطره")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                    },
                    label = {
                        Text("تاریخ")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("توضیح")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = {
                        imageUrl = it
                    },
                    label = {
                        Text("لینک عکس")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {

            Button(
                onClick = {

                    val user = FirebaseAuth
                        .getInstance()
                        .currentUser

                    if (user != null) {

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

                        onSaved()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Rose
                )
            ) {
                Text("ثبت خاطره")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("انصراف")
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
                top = 15.dp,
                bottom = 115.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Box(
                        modifier = Modifier
                            .size(43.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                            .clickable {
                                onBack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "→",
                            color = DeepRose,
                            fontSize = 20.sp
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "لحظه‌های خاص",
                            color = TextDark,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = "تاریخ‌هایی که برای ما مهم‌اند",
                            color = SoftText,
                            fontSize = 9.sp
                        )
                    }

                    Text(
                        text = "✨",
                        fontSize = 25.sp
                    )
                }
            }

            item {
                DateInfoCard(
                    icon = "❤️",
                    title = "شروع قصه‌ی ما",
                    date = "۲۰ خرداد ۱۴۰۵",
                    subtitle = "روزی که داستان ما شروع شد"
                )
            }

            item {
                DateInfoCard(
                    icon = "🌙",
                    title = "ماهگرد ما",
                    date = "هر ماه",
                    subtitle = "یک ماه دیگر کنار هم"
                )
            }

            item {
                DateInfoCard(
                    icon = "🎂",
                    title = "تولدها",
                    date = "به‌زودی",
                    subtitle = "تاریخ تولد رامین و رویا"
                )
            }

            item {
                DateInfoCard(
                    icon = "💍",
                    title = "سالگرد ما",
                    date = "۲۰ خرداد هر سال",
                    subtitle = "یک سال دیگر از قصه‌مون"
                )
            }
        }

        PremiumBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
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
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(27.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.80f)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
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
                modifier = Modifier.width(13.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = subtitle,
                    color = SoftText,
                    fontSize = 9.sp
                )
            }

            Text(
                text = date,
                color = DeepRose,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun calculateDaysTogether(): Int {
    val today = Calendar.getInstance()

    val start = Calendar.getInstance().apply {
        time = RelationshipStartDate
    }

    val difference =
        today.timeInMillis - start.timeInMillis

    return (difference /
            (1000L * 60L * 60L * 24L))
        .toInt()
        .coerceAtLeast(0)
}



