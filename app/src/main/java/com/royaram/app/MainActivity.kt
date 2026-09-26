package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.SentimentSatisfiedAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoyaramApp()
        }
    }
}

data class HomeItem(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class Memory(
    val id: String,
    val title: String,
    val date: String,
    val description: String,
    val createdBy: String,
    val createdAt: Long
)

@Composable
private fun RoyaramApp() {

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
            }
        )
    }
}

@Composable
private fun RoyaramHome(
    onMemoriesClick: () -> Unit
) {

    val context = LocalContext.current

    val startDate = java.time.LocalDate.of(
        2026,
        6,
        10
    )

    val today = java.time.LocalDate.now()

    val relationshipDays =
        java.time.temporal.ChronoUnit.DAYS.between(
            startDate,
            today
        )

    val items = listOf(

        HomeItem(
            "خاطرات ما",
            "لحظه‌های قشنگمون",
            Icons.Rounded.PhotoLibrary
        ),

        HomeItem(
            "نامه‌های عاشقانه",
            "حرف‌هایی از قلبمون",
            Icons.Rounded.Mail
        ),

        HomeItem(
            "آهنگ ما",
            "صدای خاطره‌هامون",
            Icons.Rounded.MusicNote
        ),

        HomeItem(
            "وقتی دلمون گرفت",
            "اینجا همیشه کنار همیم",
            Icons.Rounded.SentimentSatisfiedAlt
        ),

        HomeItem(
            "چت دونفره",
            "حرف‌های من و تو 💬❤️",
            Icons.Rounded.Favorite
        ),

        HomeItem(
            "لحظه‌های خاص",
            "تاریخ‌های مهم ما ✨",
            Icons.Rounded.FavoriteBorder
        ),

        HomeItem(
            "بخش خصوصی",
            "فقط برای من و تو 🔐",
            Icons.Rounded.Favorite
        ),

        HomeItem(
            "تنظیمات",
            "شخصی‌سازی رویارام ⚙️",
            Icons.Rounded.FavoriteBorder
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFE3EB),
                        Color(0xFFFFF4F7),
                        Color.White
                    )
                )
            )
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 28.dp,
                bottom = 32.dp
            ),

            verticalArrangement = Arrangement.spacedBy(
                16.dp
            )
        ) {

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            "رویارام ❤️",
                            fontSize = 31.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF402A30)
                        )

                        Spacer(
                            Modifier.height(4.dp)
                        )

                        Text(
                            "قصه‌ی من و تو، برای همیشه",
                            fontSize = 14.sp,
                            color = Color(0xFF795C64)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                Color.White.copy(
                                    alpha = 0.9f
                                ),
                                CircleShape
                            ),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE85D75),
                            modifier = Modifier.size(29.dp)
                        )
                    }
                }
            }

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(30.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 7.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        androidx.compose.foundation.Image(
                            painter = painterResource(
                                R.drawable.couple_main
                            ),

                            contentDescription = "رامین و رویا",

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(245.dp)
                                .clip(
                                    RoundedCornerShape(24.dp)
                                ),

                            contentScale = ContentScale.Crop
                        )

                        Spacer(
                            Modifier.height(15.dp)
                        )

                        Text(
                            "رامین ❤️ رویا",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF402A30)
                        )

                        Spacer(
                            Modifier.height(5.dp)
                        )

                        Text(
                            "هر روز، یک صفحه‌ی تازه از قصه‌ی ما",
                            fontSize = 13.sp,
                            color = Color(0xFF795C64)
                        )

                        Spacer(
                            Modifier.height(12.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(
                                    RoundedCornerShape(20.dp)
                                )
                                .background(
                                    Color(0xFFFFE8EE)
                                )
                                .padding(
                                    vertical = 14.dp
                                ),

                            contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(
                                    "$relationshipDays",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE85D75)
                                )

                                Text(
                                    "روز کنار هم ❤️",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF795C64)
                                )
                            }
                        }

                        Spacer(
                            Modifier.height(8.dp)
                        )

                        Text(
                            "شروع قصه: ۲۰ خرداد ۱۴۰۵",
                            fontSize = 12.sp,
                            color = Color(0xFF8B7078)
                        )

                        Spacer(
                            Modifier.height(4.dp)
                        )
                    }
                }
            }

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(22.dp),

                    colors = CardDefaults.cardColors(
                        containerColor =
                            Color(0xFFFFF0F4)
                    ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {

                        Text(
                            "جمله‌ی امروز برای ما 💌",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF402A30)
                        )

                        Spacer(
                            Modifier.height(8.dp)
                        )

                        Text(
                            "«کنار تو، حتی روزهای معمولی هم قشنگ می‌شن.» ❤️",
                            fontSize = 14.sp,
                            lineHeight = 23.sp,
                            color = Color(0xFF795C64)
                        )
                    }
                }
            }

            item {

                Text(
                    "دنیای دونفره‌ی ما",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF402A30)
                )
            }

            item {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(620.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),

                    userScrollEnabled = false
                ) {

                    items(items) { item ->

                        HomeCard(item) {

                            when (item.title) {

                                "خاطرات ما" -> {

                                    onMemoriesClick()
                                }

                                "چت دونفره" -> {

                                    context.startActivity(
                                        Intent(
                                            context,
                                            ChatActivity::class.java
                                        )
                                    )
                                }

                               
