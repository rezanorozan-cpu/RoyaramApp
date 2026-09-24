package com.royaram.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.SentimentSatisfiedAlt
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoyaramHome()
        }
    }
}

data class HomeItem(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun RoyaramHome() {

    val startDate = remember {
        LocalDate.of(2026, 6, 10)
    }

    val today = LocalDate.now()

    val relationshipDays = ChronoUnit.DAYS.between(
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
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFE8EF),
                        Color(0xFFFFF5F8),
                        Color.White
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    top = 28.dp,
                    end = 20.dp,
                    bottom = 16.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {

                    Text(
                        text = "رویارام ❤️",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF402A30)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "قصه‌ی من و تو، برای همیشه",
                        fontSize = 14.sp,
                        color = Color(0xFF795C64)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            Color.White.copy(alpha = 0.85f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE85D75),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(
                            id = com.royaram.app.R.drawable.couple_main
                        ),
                        contentDescription = "رامین و رویا",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(22.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFFE85D75),
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "رامین ❤️ رویا",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A3038)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "هر روز یک خاطره‌ی تازه",
                        fontSize = 14.sp,
                        color = Color(0xFF795C64)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$relationshipDays روز کنار هم ❤️",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE85D75)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "شروع قصه: ۲۰ خرداد ۱۴۰۵",
                        fontSize = 13.sp,
                        color = Color(0xFF795C64)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "دنیای دونفره‌ی ما",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF402A30)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(items) { item ->
                    HomeCard(item)
                }
            }
        }
    }
}

@Composable
private fun HomeCard(item: HomeItem) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.94f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFFFFE1E9),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Color(0xFFE85D75),
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF402A30)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.subtitle,
                fontSize = 12.sp,
                color = Color(0xFF795C64)
            )
        }
    }
}
