package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.Date

// ============================================================
// ROYARAM — PREMIUM COLORS
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

private val PageBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFE3ED),
        Color(0xFFF6ECFF),
        Color(0xFFFFF8FB)
    )
)

// ============================================================
// DATA
// ============================================================

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

// ============================================================
// ROOT
// ============================================================

@Composable
fun RoyaramApp(
    onChatClick: () -> Unit,
    onToast: (String) -> Unit
) {

    var currentPage by remember {
        mutableStateOf("home")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {

        when (currentPage) {

            "memories" -> {

                MemoriesScreen(
                    onBack = {
                        currentPage = "home"
                    }
                )
            }

            "special" -> {

                SpecialDatesScreen(
                    onBack = {
                        currentPage = "home"
                    }
                )
            }

            else -> {

                HomeScreen(

                    onMemoriesClick = {
                        currentPage = "memories"
                    },

                    onSpecialClick = {
                        currentPage = "special"
                    },

                    onChatClick = onChatClick,

                    onToast = onToast
                )
            }
        }
    }
}

// ============================================================
// HOME
// ============================================================

@Composable
fun HomeScreen(
    onMemoriesClick: () -> Unit,
    onSpecialClick: () -> Unit,
    onChatClick: () -> Unit,
    onToast: (String) -> Unit
) {

    val daysTogether = remember {
        calculateDaysTogether()
    }

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),

        contentPadding = PaddingValues(
            start = 11.dp,
            end = 11.dp,
            top = 5.dp,
            bottom = 14.dp
        ),

        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // --------------------------------------------------------
        // TOP
        // --------------------------------------------------------

        item {

            PremiumTopBar(
                onSettingsClick = {
                    onToast("تنظیمات رویارام 💗")
                }
            )
        }

        // --------------------------------------------------------
        // HERO
        // --------------------------------------------------------

        item {

            PremiumHero(
                daysTogether = daysTogether
            )
        }

        // --------------------------------------------------------
        // RELATIONSHIP
        // --------------------------------------------------------

        item {

            RelationshipGlassCard(
                daysTogether = daysTogether
            )
        }

        // --------------------------------------------------------
        // SECTION TITLE
        // --------------------------------------------------------

        item {

            SectionTitle()
        }

        // --------------------------------------------------------
        // ALL FEATURES
        // --------------------------------------------------------

        item {

            FeatureGrid(

                onMemoriesClick = onMemoriesClick,

                onSpecialClick = onSpecialClick,

                onChatClick = onChatClick,

                onToast = onToast
            )
        }

        // --------------------------------------------------------
        // SMALL FOOTER
        // --------------------------------------------------------

        item {

            CompactLoveFooter()
        }
    }
}

// ============================================================
// TOP BAR
// ============================================================

@Composable
fun PremiumTopBar(
    onSettingsClick: () -> Unit
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 3.dp,
                vertical = 3.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "رویـارام",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DeepRose
            )

            Text(
                text = "قصه‌ی من و تو، برای همیشه",
                fontSize = 10.sp,
                color = SoftText
            )
        }

        Box(

            modifier = Modifier
                .size(43.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = Rose.copy(alpha = 0.20f),
                    spotColor = Rose.copy(alpha = 0.22f)
                )
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
                    onSettingsClick()
                },

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "♡",
                color = Color.White,
                fontSize = 25.sp
            )
        }
    }
}

// ============================================================
// HERO
// ============================================================

@@Composable
fun PremiumHero(
    daysTogether: Long
) {

    val transition = rememberInfiniteTransition(
        label = "royaram_hero"
    )

    val imageScale by transition.animateFloat(
        initialValue = 1.00f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7000,
                easing = FastOutSlowInEasing
            )
        ),
        label = "hero_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(228.dp)
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Rose.copy(alpha = 0.20f),
                spotColor = Rose.copy(alpha = 0.22f)
            ),

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            /*
             * عکس اصلی بوسه
             *
             * Alignment.CenterStart باعث می‌شود
             * کادر بیشتر سمت چپ عکس را نگه دارد.
             */
            Image(
                painter = painterResource(
                    id = R.drawable.royaram_photo_1
                ),

                contentDescription = "رامین و رویا",

                contentScale = ContentScale.Crop,

                alignment = Alignment.CenterStart,

                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = imageScale
                        scaleY = imageScale
                    }
            )

            /*
             * لایه‌ی بسیار ظریف برای خوانایی متن
             */
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.05f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.48f)
                            )
                        )
                    )
            )

            /*
             * برچسب کوچک بالا
             */
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(
                        RoundedCornerShape(50.dp)
                    )
                    .background(
                        Color.White.copy(alpha = 0.20f)
                    )
                    .padding(
                        horizontal = 11.dp,
                        vertical = 6.dp
                    )
            ) {

                Text(
                    text = "برای همیشه ♡",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            /*
             * اطلاعات پایین عکس
             */
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 18.dp,
                        end = 18.dp,
                        bottom = 13.dp
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "رامین ❤️ رویا",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "قصه‌ی من و تو، برای همیشه",
                    fontSize = 9.sp,
                    color = Color.White.copy(
                        alpha = 0.95f
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Row(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(50.dp)
                        )
                        .background(
                            Color.White.copy(alpha = 0.20f)
                        )
                        .padding(
                            horizontal = 13.dp,
                            vertical = 5.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "❤️",
                        fontSize = 11.sp
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = "$daysTogether روز کنار هم",
                        fontSize = 9.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

    v

// ============================================================
// RELATIONSHIP GLASS CARD
// ============================================================

@Composable
fun RelationshipGlassCard(
    daysTogether: Long
) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Lavender.copy(alpha = 0.13f)
            ),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.78f
            )
        )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 11.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "تاریخ آشنایی",
                    fontSize = 8.sp,
                    color = SoftText
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "۱۴۰۵/۰۳/۲۰",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepRose
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(38.dp)
                    .background(
                        Rose.copy(alpha = 0.18f)
                    )
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "$daysTogether",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Rose
                )

                Text(
                    text = "روز کنار هم",
                    fontSize = 8.sp,
                    color = SoftText
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(38.dp)
                    .background(
                        Rose.copy(alpha = 0.18f)
                    )
            )

            Column(
                modifier = Modifier.weight(1.2f),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "هر روز ❤️",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepRose
                )

                Text(
                    text = "بهانه‌ای برای عاشق‌تر شدن",
                    fontSize = 7.sp,
                    color = SoftText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ============================================================
// SECTION TITLE
// ============================================================

@Composable
fun SectionTitle() {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 3.dp,
                vertical = 1.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "دنیای دونفره‌مون",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )

            Text(
                text = "همه‌چیز برای من و تو، همین‌جاست ❤️",
                fontSize = 8.sp,
                color = SoftText
            )
        }

        Text(
            text = "✦",
            fontSize = 20.sp,
            color = Rose
        )
    }
}

// ============================================================
// FEATURE GRID
// ============================================================

@Composable
fun FeatureGrid(
    onMemoriesClick: () -> Unit,
    onSpecialClick: () -> Unit,
    onChatClick: () -> Unit,
    onToast: (String) -> Unit
) {

    Column(
        verticalArrangement =
            Arrangement.spacedBy(7.dp)
    ) {

        // --------------------------------------------------------
        // PHOTO ROW 1
        // --------------------------------------------------------

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(7.dp)
        ) {

            PhotoFeatureCard(

                modifier =
                    Modifier.weight(1f),

                image =
                    R.drawable.royaram_photo_2,

                title =
                    "خاطرات ما",

                subtitle =
                    "لحظه‌های قشنگمون",

                emoji =
                    "📸",

                onClick =
                    onMemoriesClick
            )

            PhotoFeatureCard(

                modifier =
                    Modifier.weight(1f),

                image =
                    R.drawable.royaram_photo_3,

                title =
                    "نامه‌های عاشقانه",

                subtitle =
                    "حرف‌هایی از قلبمون",

                emoji =
                    "💌",

                onClick = {

                    onToast(
                        "نامه‌های عاشقانه به‌زودی آماده می‌شه 💌"
                    )
                }
            )

            PhotoFeatureCard(

                modifier =
                    Modifier.weight(1f),

                image =
                    R.drawable.royaram_photo_4,

                title =
                    "آهنگ ما",

                subtitle =
                    "صدای خاطره‌هامون",

                emoji =
                    "🎵",

                onClick = {

                    onToast(
                        "آهنگ ما به‌زودی اضافه می‌شه 🎵"
                    )
                }
            )
        }

        // --------------------------------------------------------
        // PHOTO ROW 2
        // --------------------------------------------------------

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(7.dp)
        ) {

            PhotoFeatureCard(

                modifier =
                    Modifier.weight(1f),

                image =
                    R.drawable.royaram_photo_5,

                title =
                    "وقتی دلمون گرفت",

                subtitle =
                    "اینجا امنِ ماست",

                emoji =
                    "🤍",

                onClick = {

                    onToast(
                        "اینجا همیشه جای امنِ من و توئه 🤍"
                    )
                }
            )

            PhotoFeatureCard(

                modifier =
                    Modifier.weight(1f),

                image =
                    R.drawable.royaram_photo_6,

                title =
                    "چت دونفره",

                subtitle =
                    "حرف‌های من و تو",

                emoji =
                    "💬",

                onClick =
                    onChatClick
            )

            PhotoFeatureCard(

                modifier =
                    Modifier.weight(1f),

                image =
                    R.drawable.royaram_photo_7,

                title =
                    "بخش خصوصی",

                subtitle =
                    "فقط برای ما 🔐",

                emoji =
                    "🔐",

                onClick = {

                    onToast(
                        "بخش خصوصی رویارام به‌زودی فعال می‌شه 🔐"
                    )
                }
            )
        }

        // --------------------------------------------------------
        // SMALL FUNCTION ROW
        // --------------------------------------------------------

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(7.dp)
        ) {

            CompactFeatureCard(

                modifier =
                    Modifier.weight(1f),

                icon =
                    "✨",

                title =
                    "لحظه‌های خاص",

                subtitle =
                    "تاریخ‌های مهم",

                onClick =
                    onSpecialClick
            )

            CompactFeatureCard(

                modifier =
                    Modifier.weight(1f),

                icon =
                    "⚙️",

                title =
                    "تنظیمات",

                subtitle =
                    "شخصی‌سازی",

                onClick = {

                    onToast(
                        "تنظیمات رویارام 💗"
                    )
                }
            )

            CompactFeatureCard(

                modifier =
                    Modifier.weight(1f),

                icon =
                    "❤️",

                title =
                    "برای همیشه",

                subtitle =
                    "رامین ♡ رویا",

                onClick = {

                    onToast(
                        "قصه‌ی ما ادامه دارد... ❤️"
                    )
                }
            )
        }
    }
}

// ============================================================
// PHOTO FEATURE CARD
// ============================================================

@Composable
fun PhotoFeatureCard(
    modifier: Modifier,
    image: Int,
    title: String,
    subtitle: String,
    emoji: String,
    onClick: () -> Unit
) {

    Card(

        modifier = modifier
            .aspectRatio(0.96f)
            .clickable {
                onClick()
            }
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor =
                    Rose.copy(alpha = 0.10f)
            ),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(

                painter = painterResource(
                    id = image
                ),

                contentDescription = title,

                contentScale =
                    ContentScale.Crop,

                modifier =
                    Modifier.fillMaxSize()
            )

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(
                                    alpha = 0.03f
                                ),
                                Color.Black.copy(
                                    alpha = 0.80f
                                )
                            )
                        )
                    )
            )

            Box(

                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.82f
                        )
                    )
                    .padding(5.dp)
            ) {

                Text(
                    text = emoji,
                    fontSize = 12.sp
                )
            }

            Column(

                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        horizontal = 5.dp,
                        vertical = 7.dp
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Text(
                    text = subtitle,
                    fontSize = 7.sp,
                    color =
                        Color.White.copy(
                            alpha = 0.92f
                        ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

// ============================================================
// COMPACT FEATURE CARD
// ============================================================

@Composable
fun CompactFeatureCard(
    modifier: Modifier,
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Card(

        modifier = modifier
            .height(73.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(17.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                Color.White.copy(
                    alpha = 0.80f
                )
        )
    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = icon,
                fontSize = 17.sp
            )

            Text(
                text = title,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = subtitle,
                fontSize = 6.sp,
                color = SoftText,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// ============================================================
// COMPACT LOVE FOOTER
// ============================================================

@Composable
fun CompactLoveFooter() {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(18.dp)
            )
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Rose.copy(alpha = 0.90f),
                        DeepRose.copy(alpha = 0.90f),
                        Lavender.copy(alpha = 0.82f)
                    )
                )
            )
            .padding(
                horizontal = 13.dp,
                vertical = 9.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = "❤️",
            fontSize = 17.sp
        )

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text =
                    "دوستت دارم، نه فقط امروز؛ تا همیشه...",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )

            Text(
                text =
                    "رامین ♡ رویا  •  یک دنیای کوچک برای دو نفر",
                fontSize = 6.sp,
                color =
                    Color.White.copy(
                        alpha = 0.88f
                    ),
                maxLines = 1
            )
        }

        Text(
            text = "♡",
            fontSize = 22.sp,
            color = Color.White
        )
    }
}

// ============================================================
// SPECIAL DATES SCREEN
// ============================================================

@Composable
fun SpecialDatesScreen(
    onBack: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(12.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // HEADER

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 9.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(

                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(
                                alpha = 0.82f
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
                        fontSize = 21.sp,
                        color = DeepRose
                    )
                }

                Spacer(
                    modifier = Modifier.width(9.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "لحظه‌های خاص ✨",
                        fontSize = 20.sp,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = TextDark
                    )

                    Text(
                        text =
                            "تاریخ‌های مهم دنیای ما",
                        fontSize = 8.sp,
                        color = SoftText
                    )
                }

                Text(
                    text = "♡",
                    fontSize = 27.sp,
                    color = Rose
                )
            }

            // BIRTHDAYS

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                DateInfoCard(

                    modifier =
                        Modifier.weight(1f),

                    icon = "👑",

                    name = "رامین",

                    label = "تولد من",

                    date = "۱۳۷۳/۰۶/۲۰"
                )

                DateInfoCard(

                    modifier =
                        Modifier.weight(1f),

                    icon = "🌷",

                    name = "رویا",

                    label = "تولد عشق من",

                    date = "۱۳۸۱/۰۹/۱۵"
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // ANNIVERSARY

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(22.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White.copy(
                                alpha = 0.82f
                            )
                    )
            ) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(

                        modifier = Modifier
                            .size(55.dp)
                            .clip(
                                RoundedCornerShape(
                                    18.dp
                                )
                            )
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Rose,
                                        Lavender
                                    )
                                )
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "❤️",
                            fontSize = 25.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "سالگرد آشنایی ما",
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color = TextDark
                        )

                        Text(
                            text =
                                "۱۴۰۵/۰۳/۲۰",
                            fontSize = 17.sp,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color = DeepRose,
                            modifier =
                                Modifier.padding(
                                    top = 2.dp
                                )
                        )

                        Text(
                            text =
                                "از این روز، قصه‌ی ما شروع شد ❤️",
                            fontSize = 7.sp,
                            color = SoftText
                        )
                    }

                    Text(
                        text = "∞",
                        fontSize = 29.sp,
                        color = Rose
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // MONTHLY

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(22.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White.copy(
                                alpha = 0.74f
                            )
                    )
            ) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(11.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "🌙",
                        fontSize = 27.sp
                    )

                    Spacer(
                        modifier = Modifier.width(9.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "قرار ماهانه‌ی ما",
                            fontSize = 11.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color = TextDark
                        )

                        Text(
                            text =
                                "هر ماه یک خاطره‌ی تازه ❤️",
                            fontSize = 7.sp,
                            color = SoftText
                        )
                    }

                    Text(
                        text = "۲۰",
                        fontSize = 23.sp,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color = Rose
                    )
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            // BOTTOM LOVE

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(18.dp)
                    )
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Rose,
                                DeepRose
                            )
                        )
                    )
                    .padding(11.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "✨",
                    fontSize = 18.sp
                )

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text =
                        "مهم‌ترین تاریخ، همین امروزِ کنار هم بودنه.",
                    fontSize = 9.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = Color.White,
                    modifier =
                        Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "❤️",
                    fontSize = 17.sp
                )
            }
        }
    }
}

// ============================================================
// DATE INFO CARD
// ============================================================

@Composable
fun DateInfoCard(
    modifier: Modifier,
    icon: String,
    name: String,
    label: String,
    date: String
) {

    Card(

        modifier = modifier,

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(
                        alpha = 0.82f
                    )
            )
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(

                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        SoftPink
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = icon,
                    fontSize = 23.sp
                )
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                color = TextDark
            )

            Text(
                text = label,
                fontSize = 7.sp,
                color = SoftText
            )

            Text(
                text = date,
                fontSize = 10.sp,
                fontWeight =
                    FontWeight.Bold,
                color = DeepRose,
                modifier =
                    Modifier.padding(
                        top = 3.dp
                    )
            )
        }
    }
}

// ============================================================
// MEMORIES SCREEN
// ============================================================

@Composable
fun MemoriesScreen(
    onBack: () -> Unit
) {

    val firestore = remember {
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
                                    document.getString(
                                        "title"
                                    ) ?: "خاطره ما",

                                date =
                                    document.getString(
                                        "date"
                                    ) ?: "",

                                description =
                                    document.getString(
                                        "description"
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

    Box(

        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
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
                                horizontal = 15.dp,
                                vertical = 9.dp
                            ),

                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
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

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 15.dp,
                vertical = 11.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(

            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    Color.White.copy(
                        alpha = 0.85f
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
                fontSize = 21.sp,
                color = DeepRose
            )
        }

        Spacer(
            modifier = Modifier.width(9.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "خاطرات ما 📸",
                fontSize = 20.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                color = TextDark
            )

            Text(
                text =
                    "لحظه‌هایی که نباید فراموش شوند",
                fontSize = 8.sp,
                color = SoftText
            )
        }

        Box(

            modifier = Modifier
                .size(40.dp)
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

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "+",
                color = Color.White,
                fontSize = 23.sp
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

        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),

        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "💗",
                fontSize = 50.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text =
                    "هنوز خاطره‌ای ثبت نشده",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    "اولین لحظه قشنگتون رو اینجا ثبت کنید",
                fontSize = 11.sp,
                color = SoftText,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Card(

                modifier = Modifier.clickable {
                    onAdd()
                },

                shape =
                    RoundedCornerShape(17.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor = Rose
                    )
            ) {

                Text(
                    text =
                        "ثبت اولین خاطره ❤️",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier =
                        Modifier.padding(
                            horizontal = 19.dp,
                            vertical = 11.dp
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
            RoundedCornerShape(22.dp),

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

                coil.compose.AsyncImage(

                    model =
                        memory.imageUrl,

                    contentDescription =
                        memory.title,

                    contentScale =
                        ContentScale.Crop,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 22.dp,
                                    topEnd = 22.dp
                                )
                            )
                )
            }

            Column(

                modifier =
                    Modifier.padding(15.dp)
            ) {

                Text(
                    text = memory.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                if (
                    memory.date.isNotBlank()
                ) {

                    Text(
                        text =
                            "📅 ${memory.date}",
                        fontSize = 10.sp,
                        color = Rose,
                        modifier =
                            Modifier.padding(
                                top = 4.dp
                            )
                    )
                }

                if (
                    memory.description.isNotBlank()
                ) {

                    Text(
                        text =
                            memory.description,
                        fontSize = 12.sp,
                        color = SoftText,
                        lineHeight = 20.sp,
                        modifier =
                            Modifier.padding(
                                top = 7.dp
                            )
                    )
                }
            }
        }
    }
}

// ============================================================
// ADD MEMORY DIALOG
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

                modifier = Modifier
                    .height(285.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(9.dp)
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

                    value =
                        description,

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
                        .collection(
                            "memories"
                        )
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

// ============================================================
// DAYS TOGETHER
// ۱۴۰۵/۰۳/۲۰ = 2026/06/10
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
