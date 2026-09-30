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

private val PageBackground =
    Brush.verticalGradient(
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

    var showMemories by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
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

                onChatClick = onChatClick,

                onToast = onToast
            )
        }
    }
}

// ============================================================
// HOME
// ============================================================

@Composable
fun HomeScreen(
    onMemoriesClick: () -> Unit,
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
            start = 15.dp,
            end = 15.dp,
            top = 10.dp,
            bottom = 30.dp
        ),

        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // --------------------------------------------------------
        // TOP HEADER
        // --------------------------------------------------------

        item {

            PremiumTopBar(
                onSettingsClick = {
                    onToast("تنظیمات رویارام 💗")
                }
            )
        }

        // --------------------------------------------------------
        // HERO PHOTO
        // --------------------------------------------------------

        item {

            PremiumHero(
                daysTogether = daysTogether
            )
        }

        // --------------------------------------------------------
        // RELATIONSHIP CARD
        // --------------------------------------------------------

        item {

            RelationshipGlassCard(
                daysTogether = daysTogether
            )
        }

        // --------------------------------------------------------
        // SMALL LOVE MESSAGE
        // --------------------------------------------------------

        item {

            LoveQuoteCard()
        }

        // --------------------------------------------------------
        // SECTION TITLE
        // --------------------------------------------------------

        item {

            SectionTitle()
        }

        // --------------------------------------------------------
        // FEATURE GRID
        // --------------------------------------------------------

        item {

            FeatureGrid(

                onMemoriesClick = onMemoriesClick,

                onChatClick = onChatClick,

                onToast = onToast
            )
        }

        // --------------------------------------------------------
        // FINAL LOVE CARD
        // --------------------------------------------------------

        item {

            FinalLoveCard()
        }

        // --------------------------------------------------------
        // FOOTER
        // --------------------------------------------------------

        item {

            PremiumFooter()
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
                vertical = 5.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "رویـارام",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DeepRose
            )

            Text(
                text = "قصه‌ی من و تو، برای همیشه",
                fontSize = 12.sp,
                color = SoftText,
                modifier = Modifier.padding(top = 1.dp)
            )
        }

        Box(

            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = Rose.copy(alpha = 0.20f),
                    spotColor = Rose.copy(alpha = 0.25f)
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
                fontSize = 27.sp
            )
        }
    }
}

// ============================================================
// HERO
// ============================================================

@Composable
fun PremiumHero(
    daysTogether: Long
) {

    val transition =
        rememberInfiniteTransition(
            label = "hero_transition"
        )

    val imageScale by transition.animateFloat(

        initialValue = 1.00f,

        targetValue = 1.045f,

        animationSpec =
            infiniteRepeatable(
                animation = tween(
                    durationMillis = 6500,
                    easing = FastOutSlowInEasing
                )
            ),

        label = "hero_scale"
    )

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Rose.copy(alpha = 0.20f),
                spotColor = Rose.copy(alpha = 0.22f)
            ),

        shape = RoundedCornerShape(32.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // ----------------------------------------------------
            // REAL COUPLE PHOTO
            // ----------------------------------------------------

            Image(

                painter = painterResource(
                    id = R.drawable.royaram_photo_1
                ),

                contentDescription = "رامین و رویا",

                contentScale = ContentScale.Crop,

                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = imageScale
                        scaleY = imageScale
                    }
            )

            // ----------------------------------------------------
            // DARK ROMANTIC GRADIENT
            // ----------------------------------------------------

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.08f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.74f)
                            )
                        )
                    )
            )

            // ----------------------------------------------------
            // TOP GLASS BADGE
            // ----------------------------------------------------

            Box(

                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
                    .clip(
                        RoundedCornerShape(50.dp)
                    )
                    .background(
                        Color.White.copy(alpha = 0.20f)
                    )
                    .padding(
                        horizontal = 13.dp,
                        vertical = 8.dp
                    )
            ) {

                Text(
                    text = "برای همیشه ♡",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ----------------------------------------------------
            // HERO TEXT
            // ----------------------------------------------------

            Column(

                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        bottom = 22.dp
                    ),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "رامین ❤️ رویا",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "هر روز یک صفحه‌ی تازه از قصه‌ی ما",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.94f),
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(13.dp)
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
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "❤️",
                        fontSize = 14.sp
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "$daysTogether روز کنار هم",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

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
                elevation = 14.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Lavender.copy(alpha = 0.16f)
            ),

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.72f
            )
        )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 17.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // DATE

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "تاریخ آشنایی ما",
                    fontSize = 10.sp,
                    color = SoftText
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "۱۴۰۵/۰۳/۲۰",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepRose
                )
            }

            // CENTER DAYS

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "$daysTogether",
                    fontSize = 31.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Rose
                )

                Text(
                    text = "روز کنار هم",
                    fontSize = 10.sp,
                    color = SoftText
                )
            }

            // LOVE TEXT

            Column(
                modifier = Modifier.weight(1.15f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "✦",
                    fontSize = 20.sp,
                    color = Lavender
                )

                Text(
                    text = "هر روز بهانه‌ای",
                    fontSize = 10.sp,
                    color = SoftText,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "برای عاشق‌تر شدن...",
                    fontSize = 10.sp,
                    color = DeepRose,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ============================================================
// LOVE QUOTE
// ============================================================

@Composable
fun LoveQuoteCard() {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(23.dp)
            )
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.78f),
                        PaleLavender.copy(alpha = 0.70f),
                        SoftPink.copy(alpha = 0.68f)
                    )
                )
            )
            .padding(
                horizontal = 17.dp,
                vertical = 14.dp
            )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "❝",
                fontSize = 30.sp,
                color = Rose
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "کنار تو، حتی روزهای معمولی هم قشنگ می‌شن. ❤️",
                fontSize = 12.sp,
                color = TextDark,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "❞",
                fontSize = 30.sp,
                color = Rose
            )
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
                vertical = 2.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "دنیای دونفره‌مون",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )

            Text(
                text = "همه‌چیز برای من و تو، همین‌جاست ❤️",
                fontSize = 10.sp,
                color = SoftText,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Text(
            text = "✦",
            fontSize = 24.sp,
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
    onChatClick: () -> Unit,
    onToast: (String) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {

            PhotoFeatureCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_2,
                title = "خاطرات ما",
                subtitle = "لحظه‌های قشنگمون",
                emoji = "📸",
                onClick = onMemoriesClick
            )

            PhotoFeatureCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_3,
                title = "نامه‌های عاشقانه",
                subtitle = "حرف‌هایی از قلبمون",
                emoji = "💌",
                onClick = {
                    onToast("نامه‌های عاشقانه به‌زودی آماده می‌شه 💌")
                }
            )

            PhotoFeatureCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_4,
                title = "آهنگ ما",
                subtitle = "صدای خاطره‌هامون",
                emoji = "🎵",
                onClick = {
                    onToast("آهنگ ما به‌زودی اضافه می‌شه 🎵")
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {

            PhotoFeatureCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_5,
                title = "وقتی دلمون گرفت",
                subtitle = "اینجا همیشه کنار همیم",
                emoji = "🤍",
                onClick = {
                    onToast("اینجا همیشه جای امنِ من و توئه 🤍")
                }
            )

            PhotoFeatureCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_6,
                title = "چت دونفره",
                subtitle = "حرف‌های من و تو ❤️",
                emoji = "💬",
                onClick = onChatClick
            )

            PhotoFeatureCard(
                modifier = Modifier.weight(1f),
                image = R.drawable.royaram_photo_7,
                title = "بخش خصوصی",
                subtitle = "فقط برای من و تو 🔐",
                emoji = "🔐",
                onClick = {
                    onToast("بخش خصوصی رویارام به‌زودی فعال می‌شه 🔐")
                }
            )
        }

        // SETTINGS / SPECIAL WIDE CARD

        Card(

            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onToast("شخصی‌سازی رویارام 💗")
                },

            shape = RoundedCornerShape(24.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(
                    alpha = 0.76f
                )
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
                        .size(54.dp)
                        .clip(
                            RoundedCornerShape(18.dp)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Rose,
                                    Lavender
                                )
                            )
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "⚙️",
                        fontSize = 23.sp
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "لحظه‌های خاص",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )

                    Text(
                        text = "تاریخ‌های مهم ما ✨",
                        fontSize = 10.sp,
                        color = SoftText,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }

                Text(
                    text = "♡",
                    fontSize = 27.sp,
                    color = Rose
                )
            }
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
            .aspectRatio(0.78f)
            .clickable {
                onClick()
            }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Rose.copy(alpha = 0.10f)
            ),

        shape = RoundedCornerShape(22.dp),

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

                contentScale = ContentScale.Crop,

                modifier = Modifier.fillMaxSize()
            )

            // IMAGE OVERLAY

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.08f),
                                Color.Black.copy(alpha = 0.80f)
                            )
                        )
                    )
            )

            // EMOJI

            Box(

                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(alpha = 0.78f)
                    )
                    .padding(7.dp)
            ) {

                Text(
                    text = emoji,
                    fontSize = 14.sp
                )
            }

            // TEXT

            Column(

                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(9.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = subtitle,
                    fontSize = 8.sp,
                    color = Color.White.copy(alpha = 0.90f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ============================================================
// FINAL LOVE CARD
// ============================================================

@Composable
fun FinalLoveCard() {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = Rose.copy(alpha = 0.18f)
            ),

        shape = RoundedCornerShape(30.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(

                painter = painterResource(
                    id = R.drawable.royaram_photo_1
                ),

                contentDescription = "رویـارام",

                contentScale = ContentScale.Crop,

                modifier = Modifier.fillMaxSize()
            )

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.08f),
                                Color.Black.copy(alpha = 0.72f)
                            )
                        )
                    )
            )

            Column(

                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(20.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "دوستت دارم",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "نه فقط امروز، بلکه تا همیشه... ❤️",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                Text(
                    text = "رامین ♡ رویا",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ============================================================
// FOOTER
// ============================================================

@Composable
fun PremiumFooter() {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 2.dp,
                bottom = 8.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "رامین ❤️ رویا",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = DeepRose
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = "یک دنیای کوچک، فقط برای دو نفر",
            fontSize = 10.sp,
            color = SoftText
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "♡  رویارام  ♡",
            fontSize = 10.sp,
            color = Rose.copy(alpha = 0.75f)
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

                if (
                    error == null &&
                    snapshot != null
                ) {

                    memories =
                        snapshot.documents.map { document ->

                            Memory(

                                id = document.id,

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
                            horizontal = 17.dp,
                            vertical = 12.dp
                        ),

                        verticalArrangement =
                            Arrangement.spacedBy(14.dp)
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
                horizontal = 17.dp,
                vertical = 14.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(

            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Color.White.copy(alpha = 0.85f)
                )
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
            modifier = Modifier.width(11.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "خاطرات ما 📸",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )

            Text(
                text = "لحظه‌هایی که نباید فراموش شوند",
                fontSize = 10.sp,
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
                modifier = Modifier.height(13.dp)
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

                modifier = Modifier.clickable {
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

// ============================================================
// MEMORY CARD
// ============================================================

@Composable
fun MemoryCard(
    memory: Memory
) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(25.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.90f
            )
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            if (memory.imageUrl.isNotBlank()) {

                coil.compose.AsyncImage(

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
                text = "ثبت یک خاطره ❤️",
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(

                modifier = Modifier
                    .height(300.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
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
        today.time - startDate.time

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
