package com.royaram.app

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
private val DeepPink = Color(0xFFB83D63)
private val LightPink = Color(0xFFFFE8F0)
private val Cream = Color(0xFFFFF9FB)
private val TextDark = Color(0xFF33252B)
private val SoftText = Color(0xFF8A747C)
private val Glass = Color.White.copy(alpha = 0.88f)

private const val START_YEAR = 2026
private const val START_MONTH = Calendar.JUNE
private const val START_DAY = 10

@Composable
fun RoyaramApp(
    onToast: (String) -> Unit = {}
) {
    val infinite = rememberInfiniteTransition(label = "heart")
    val heartScale by infinite.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    val daysTogether = remember { calculateDaysTogether() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFDCE8),
                        Color(0xFFFFEEF4),
                        Color(0xFFFFF8FA)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------------------------------------------------------
            // کادر بالایی طبیعت
            // ---------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {

                    // عکس طبیعت
                    Image(
                        painter = painterResource(id = R.drawable.couple_main),
                        contentDescription = "طبیعت رویارام",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.05f),
                                        Color.Black.copy(alpha = 0.48f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "رویارام",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "رامین ❤️ رویا",
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ---------------------------------------------------------
            // روزشمار آشنایی + تولد
            // ---------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Glass),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    CounterItem(
                        modifier = Modifier.weight(1f),
                        icon = "❤️",
                        title = "روزهای آشنایی",
                        value = "$daysTogether روز"
                    )

                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .width(1.dp)
                            .background(Color(0xFFE9CBD5))
                    )

                    CounterItem(
                        modifier = Modifier.weight(1f),
                        icon = "🎂",
                        title = "تا تولد رویا",
                        value = "به‌زودی"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ---------------------------------------------------------
            // دو ردیف سه‌تایی
            // ---------------------------------------------------------
            val folders = listOf(
                FolderItem("خاطرات ما", "لحظه‌های قشنگمون", Icons.Default.Photo),
                FolderItem("نامه‌های عاشقانه", "حرف‌های قلبمون", Icons.Default.Mail),
                FolderItem("آهنگ ما", "صدای خاطره‌هامون", Icons.Default.MusicNote),
                FolderItem("لحظه‌های خاص", "تاریخ‌های مهم ما", Icons.Default.Star),
                FolderItem("بخش خصوصی", "فقط برای من و تو", Icons.Default.Lock),
                FolderItem("تنظیمات", "شخصی‌سازی رویارام", Icons.Default.Settings)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
                userScrollEnabled = false
            ) {
                items(folders) { item ->
                    FolderCard(item = item)
                }
            }

            Spacer(modifier = Modifier.height(9.dp))

            // ---------------------------------------------------------
            // ردیف سوم: یک پوشه + کادر جملات عاشقانه
            // ---------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {

                FolderCard(
                    item = FolderItem(
                        title = "چت دونفره",
                        subtitle = "حرف‌های من و تو ❤️",
                        icon = Icons.Default.Favorite
                    ),
                    modifier = Modifier.weight(1f)
                )

                Card(
                    modifier = Modifier
                        .weight(2f)
                        .height(92.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 13.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Pink,
                            modifier = Modifier
                                .size(25.dp)
                                .scale(heartScale)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.»",
                            color = TextDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )

                        Text(
                            text = "❤️ برای رامین و رویا",
                            color = SoftText,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ساخته شده با ❤️ برای رامین و رویا",
                color = SoftText,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
private fun CounterItem(
    modifier: Modifier,
    icon: String,
    title: String,
    value: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$icon  $title",
            color = DeepPink,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = TextDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class FolderItem(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun FolderCard(
    item: FolderItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.88f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(39.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                LightPink,
                                Color(0xFFFFF4F7)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Pink,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.title,
                color = TextDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = item.subtitle,
                color = SoftText,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

private fun calculateDaysTogether(): Long {
    val start = Calendar.getInstance().apply {
        set(START_YEAR, START_MONTH, START_DAY, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val difference = today.timeInMillis - start.timeInMillis

    return if (difference > 0) {
        difference / (1000L * 60L * 60L * 24L)
    } else {
        0L
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                RoyaramApp(
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


