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
                top = 6.dp,
                bottom = 112.dp
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp)
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

            // چت + رویدادهای خاص
            item {
                PremiumSplitCards(
                    firstTitle = "چت دونفره",
                    firstSubtitle = "حرف‌های من و تو ❤️",
                    firstIcon = "💬",
                    secondTitle = "رویدادهای خاص",
                    secondSubtitle = "تاریخ‌های مهم ما ✨",
                    secondIcon = "✨",
                    onLettersClick = onChatClick,
                    onMusicClick = onSpecialClick
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
    val transition = rememberInfiniteTransition(
        label = "heart"
    )

    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .clip(RoundedCornerShape(29.dp))
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(29.dp),
                ambientColor = Rose.copy(alpha = 0.18f)
            )
    ) {

        // زمینه‌ی لطیف پشت عکس
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE8D5DC),
                            Color(0xFFF4DCE2),
                            Color(0xFFFFE9D7)
                        )
                    )
                )
        )

        // عکس اصلی بوسه
        AsyncImage(
            model = "android.resource://com.royaram.app/drawable/royaram_photo_1",
            contentDescription = "رامین و رویا",
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.72f)
                .align(Alignment.CenterStart)
                .clip(
                    RoundedCornerShape(
                        topStart = 29.dp,
                        bottomStart = 29.dp,
                        topEnd = 20.dp,
                        bottomEnd = 20.dp
                    )
                ),
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterStart
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
                            Color(0xFF4A2633).copy(alpha = 0.18f),
                            Color(0xFF321A24).copy(alpha = 0.62f)
                        )
                    )
                )
        )

        // سایه‌ی پایین برای خوانایی متن
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0xFF321A24).copy(alpha = 0.56f)
                        )
                    )
                )
        )

        // قلب بالای کارت
        Text(
            text = "♥",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 14.dp,
                    end = 15.dp
                )
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                },
            color = Color.White,
            fontSize = 25.sp
        )

        // نوشته‌ی روی عکس
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    start = 16.dp,
                    end = 18.dp,
                    bottom = 15.dp
                ),
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = "رامین ❤️ رویا",
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "هر روز یک صفحه‌ی تازه از قصه‌ی ما",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 9.sp
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "$daysTogether روز کنار هم ❤️",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
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
            .height(116.dp),
        shape = RoundedCornerShape(27.dp),
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
                    horizontal = 16.dp,
                    vertical = 13.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // سمت چپ: تاریخ شروع
            Column(
                modifier = Modifier.width(94.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "تاریخ آشنایی ما",
                    color = DeepRose,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = "۱۴۰۵/۰۳/۲۰",
                    color = TextDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "۲۰ خرداد ۱۴۰۵",
                    color = SoftText,
                    fontSize = 8.sp
                )
            }

            // جداکننده
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(65.dp)
                    .background(
                        Rose.copy(alpha = 0.18f)
                    )
            )

            Spacer(
                modifier = Modifier.width(13.dp)
            )

            // مرکز: تعداد روزها
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = daysTogether.toString(),
                    color = DeepRose,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "روز کنار هم ❤️",
                    color = SoftText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            // سمت راست: جمله عاشقانه
            Column(
                modifier = Modifier.width(92.dp),
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "از اون روز...",
                    color = Lavender,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "هر روزمون\nیک خاطره‌ست",
                    color = TextDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "و قصه‌مون ادامه داره ✨",
                    color = SoftText,
                    fontSize = 7.sp,
                    textAlign = TextAlign.End
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
            .height(190.dp)
            .clip(RoundedCornerShape(30.dp))
            .shadow(
                15.dp,
                RoundedCornerShape(30.dp),
                ambientColor = Rose.copy(alpha = 0.17f)
            )
            .clickable {
                onClick()
            }
    ) {

        AsyncImage(
            model = "android.resource://com.royaram.app/drawable/royaram_photo_2",
            contentDescription = "خاطرات ما",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0xFF29171E).copy(alpha = 0.76f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(15.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♡",
                color = Color.White,
                fontSize = 22.sp
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 19.dp,
                    end = 19.dp,
                    bottom = 18.dp
                )
        ) {

            Text(
                text = "خاطرات ما",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "لحظه‌هایی که دلمون نمی‌خواد هیچ‌وقت فراموش بشن",
                color = Color.White.copy(alpha = 0.90f),
                fontSize = 10.sp
            )
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
            .height(145.dp)
            .clip(RoundedCornerShape(27.dp))
            .shadow(
                10.dp,
                RoundedCornerShape(27.dp),
                ambientColor = Rose.copy(alpha = 0.12f)
            )
            .clickable {
                onClick()
            }
    ) {

        AsyncImage(
            model = imageRes,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0xFF26161D).copy(alpha = 0.78f)
                        )
                    )
                )
        )

        Text(
            text = icon,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(13.dp),
            fontSize = 21.sp
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
        ) {

            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.88f),
                fontSize = 8.sp,
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
            .height(105.dp)
            .clip(RoundedCornerShape(27.dp))
            .clickable {
                onClick()
            }
    ) {

        AsyncImage(
            model = "android.resource://com.royaram.app/drawable/royaram_photo_5",
            contentDescription = "کنار هم",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF331B25).copy(alpha = 0.80f),
                            Color(0xFF331B25).copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(18.dp)
        ) {

            Text(
                text = "وقتی دلمون گرفت 🫂",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "اینجا همیشه جای امن ماست",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun PremiumPrivateCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF392630)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 19.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔐",
                    fontSize = 23.sp
                )
            }

            Spacer(
                modifier = Modifier.width(13.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "بخش خصوصی",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "فقط برای من و تو",
                    color = Color.White.copy(alpha = 0.68f),
                    fontSize = 9.sp
                )
            }

            Text(
                text = "›",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 27.sp
            )
        }
    }
}

@Composable
fun DailyWordsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.76f)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(19.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "امروز برای تو...",
                color = DeepRose,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.» ❤️",
                color = TextDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CompactLoveFooter() {
    Text(
        text = "ساخته شده با ❤️ برای رامین و رویا",
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 4.dp,
                bottom = 4.dp
            ),
        textAlign = TextAlign.Center,
        color = SoftText,
        fontSize = 9.sp
    )
}

@Composable
fun PremiumBottomBar(
    modifier: Modifier = Modifier,
    selected: String,
    onHomeClick: () -> Unit,
    onMemoriesClick: () -> Unit,
    onSpecialClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                start = 15.dp,
                end = 15.dp,
                bottom = 9.dp
            )
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.91f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 9.dp
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 7.dp
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly
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
                    icon = "✨",
                    title = "خاص",
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
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 20.dp,
                vertical = 6.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = icon,
            color = if (selected) DeepRose else SoftText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = title,
            color = if (selected) DeepRose else SoftText,
            fontSize = 8.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
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



