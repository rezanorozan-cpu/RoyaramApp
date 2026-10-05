package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import kotlin.math.max

private val Pink = Color(0xFFE85D86)
private val DeepPink = Color(0xFFB83D63)
private val LightPink = Color(0xFFFFE7EF)
private val SoftPink = Color(0xFFFFF1F5)
private val TextDark = Color(0xFF33252B)
private val SoftText = Color(0xFF82747A)
private val Background = Color(0xFFFFF7F9)

private val RelationshipStartDate =
    Calendar.getInstance().apply {
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
                        "وقتی دلمون گرفت ❤️",
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
                        "تنظیمات رویارام ⚙️",
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

data class FolderItem(
    val title: String,
    val subtitle: String,
    val type: FolderType
)

enum class FolderType {
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

    val today = remember {
        Calendar.getInstance()
    }

    val daysTogether = remember {

        val difference =
            today.timeInMillis -
                    RelationshipStartDate.timeInMillis

        max(
            0,
            (difference /
                    (1000L * 60L * 60L * 24L)).toInt()
        )
    }

    /*
     * دقیقاً ۶ پوشه:
     * ردیف اول = ۳
     * ردیف دوم = ۳
     *
     * ردیف سوم جداگانه:
     * یک پوشه + جملات عاشقانه
     */
    val folders = listOf(

        FolderItem(
            "خاطرات ما",
            "لحظه‌های قشنگمون",
            FolderType.MEMORIES
        ),

        FolderItem(
            "نامه‌های عاشقانه",
            "حرف‌هایی از قلبمون",
            FolderType.LETTERS
        ),

        FolderItem(
            "آهنگ ما",
            "صدای قصه‌ی ما",
            FolderType.MUSIC
        ),

        FolderItem(
            "وقتی دلمون گرفت",
            "همیشه کنار هم",
            FolderType.SAD
        ),

        FolderItem(
            "چت دونفره",
            "حرف‌های من و تو",
            FolderType.CHAT
        ),

        FolderItem(
            "لحظه‌های خاص",
            "تاریخ‌های مهم ما",
            FolderType.SPECIAL
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFE8EF),
                        Background,
                        Color.White
                    )
                )
            )
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 12.dp
            )
    ) {

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        /*
         * =========================================
         * بنر اصلی لاکچری
         * =========================================
         */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
                .clip(
                    RoundedCornerShape(24.dp)
                )
        ) {

            /*
             * تصویر اصلی
             *
             * Fit = هیچ قسمتی از عکس بریده نمی‌شود
             */
            Image(
                painter = painterResource(
                    id = R.drawable.couple_main
                ),

                contentDescription = "رامین و رویا",

                contentScale = ContentScale.Fit,

                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.White
                    )
            )

            /*
             * لایه ظریف روی عکس
             */
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(
                                    alpha = 0.12f
                                )
                            )
                        )
                    )
            )

            /*
             * نوشته روی بنر
             */
            Column(
                modifier = Modifier
                    .align(
                        Alignment.BottomCenter
                    )
                    .padding(
                        bottom = 8.dp
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "رامین ❤️ رویا",

                    color = Color.White,

                    fontSize = 17.sp,

                    fontWeight =
                        FontWeight.Bold,

                    textAlign =
                        TextAlign.Center
                )

                Text(
                    text = "قصه‌ی من و تو",

                    color =
                        Color.White.copy(
                            alpha = 0.94f
                        ),

                    fontSize = 10.sp,

                    textAlign =
                        TextAlign.Center
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        /*
         * =========================================
         * روزشمار
         * =========================================
         */

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp),

            shape =
                RoundedCornerShape(22.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White.copy(
                            alpha = 0.95f
                        )
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 12.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "❤️",
                        fontSize = 16.sp
                    )

                    Text(
                        text = "$daysTogether",

                        fontSize = 21.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color = DeepPink
                    )

                    Text(
                        text = "روز آشنایی",

                        fontSize = 10.sp,

                        color = SoftText
                    )
                }

                Box(
                    modifier = Modifier
                        .size(
                            width = 1.dp,
                            height = 48.dp
                        )
                        .background(
                            LightPink
                        )
                )

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🎂",
                        fontSize = 16.sp
                    )

                    Text(
                        text = "به‌زودی",

                        fontSize = 15.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color = DeepPink
                    )

                    Text(
                        text = "تا تولد",

                        fontSize = 10.sp,

                        color = SoftText
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        /*
         * =========================================
         * دو ردیف سه‌تایی
         * =========================================
         */

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),

            modifier = Modifier
                .fillMaxWidth()
                .height(268.dp),

            contentPadding =
                PaddingValues(0.dp),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp),

            verticalArrangement =
                Arrangement.spacedBy(8.dp),

            userScrollEnabled = false
        ) {

            items(folders) { folder ->

                FolderCard(
                    folder = folder,

                    onClick = {

                        when (folder.type) {

                            FolderType.MEMORIES ->
                                onMemoriesClick()

                            FolderType.LETTERS ->
                                onLettersClick()

                            FolderType.MUSIC ->
                                onMusicClick()

                            FolderType.SAD ->
                                onSadClick()

                            FolderType.CHAT ->
                                onChatClick()

                            FolderType.SPECIAL ->
                                onSpecialClick()

                            else -> Unit
                        }
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================================
         * ردیف سوم
         * یک پوشه + جملات عاشقانه
         * =========================================
         */

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            FolderCard(
                folder = FolderItem(
                    "بخش خصوصی",
                    "فقط من و تو",
                    FolderType.PRIVATE
                ),

                modifier =
                    Modifier.weight(1f),

                onClick = {
                    onPrivateClick()
                }
            )

            LoveQuoteCard(
                modifier =
                    Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        /*
         * تنظیمات
         */

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSettingsClick()
                },

            shape =
                RoundedCornerShape(18.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White.copy(
                            alpha = 0.78f
                        )
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(11.dp),

                verticalAlignment =
                    Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Settings,

                    contentDescription =
                        "تنظیمات",

                    tint = DeepPink,

                    modifier =
                        Modifier.size(18.dp)
                )

                Spacer(
                    modifier =
                        Modifier.size(6.dp)
                )

                Text(
                    text =
                        "تنظیمات رویارام",

                    fontSize = 12.sp,

                    color = SoftText
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Text(
            text =
                "ساخته شده با ❤️ برای رامین و رویا",

            fontSize = 10.sp,

            color = SoftText,

            textAlign =
                TextAlign.Center,

            modifier =
                Modifier.fillMaxWidth()
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )
    }
}

@Composable
fun FolderCard(
    folder: FolderItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val icon = when (folder.type) {

        FolderType.MEMORIES ->
            Icons.Default.PhotoLibrary

        FolderType.LETTERS ->
            Icons.Default.Mail

        FolderType.MUSIC ->
            Icons.Default.MusicNote

        FolderType.SAD ->
            Icons.Default.Favorite

        FolderType.CHAT ->
            Icons.Default.Chat

        FolderType.SPECIAL ->
            Icons.Default.Star

        FolderType.PRIVATE ->
            Icons.Default.Lock

        FolderType.SETTINGS ->
            Icons.Default.Settings
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(
                        alpha = 0.95f
                    )
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp),

            verticalArrangement =
                Arrangement.Center,

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(43.dp)
                    .background(
                        SoftPink,
                        RoundedCornerShape(14.dp)
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector = icon,

                    contentDescription =
                        folder.title,

                    tint = DeepPink,

                    modifier =
                        Modifier.size(22.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    folder.title,

                fontSize = 11.sp,

                fontWeight =
                    FontWeight.Bold,

                color = TextDark,

                textAlign =
                    TextAlign.Center,

                maxLines = 1
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text =
                    folder.subtitle,

                fontSize = 8.sp,

                color = SoftText,

                textAlign =
                    TextAlign.Center,

                maxLines = 1
            )
        }
    }
}

@Composable
fun LoveQuoteCard(
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp),

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFFFEAF1)
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(9.dp),

            verticalArrangement =
                Arrangement.Center,

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    Icons.Default.Favorite,

                contentDescription = null,

                tint = Pink,

                modifier =
                    Modifier.size(23.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    "حرف‌های عاشقانه",

                fontSize = 12.sp,

                fontWeight =
                    FontWeight.Bold,

                color = DeepPink,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "«کنار تو،\nحتی روزهای معمولی قشنگن.» ❤️",

                fontSize = 8.sp,

                color = TextDark,

                lineHeight = 12.sp,

                textAlign =
                    TextAlign.Center
            )
        }
    }
}


