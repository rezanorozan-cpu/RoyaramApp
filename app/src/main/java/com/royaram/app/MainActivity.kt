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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
private val LightPink = Color(0xFFFFE5EE)
private val SoftPink = Color(0xFFFFF1F5)
private val Lavender = Color(0xFFF1E5FF)
private val TextDark = Color(0xFF33252B)
private val SoftText = Color(0xFF82747A)
private val Background = Color(0xFFFFF7F9)

private const val START_YEAR = 1405
private const val START_MONTH = 3
private const val START_DAY = 20

private const val RAMIN_BIRTH_MONTH = 6
private const val RAMIN_BIRTH_DAY = 20

private const val ROYA_BIRTH_MONTH = 9
private const val ROYA_BIRTH_DAY = 15

private const val PERIOD_START_YEAR = 1405
private const val PERIOD_START_MONTH = 7
private const val PERIOD_START_DAY = 3

private const val PERIOD_END_YEAR = 1405
private const val PERIOD_END_MONTH = 7
private const val PERIOD_END_DAY = 10


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val showMemories = remember {
                mutableStateOf(false)
            }

            if (showMemories.value) {

                MemoriesScreen(
                    onBack = {
                        showMemories.value = false
                    }
                )

            } else {

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
                        showMemories.value = true
                    },
                    onLettersClick = {
                        toast("نامه‌های عاشقانه 💌")
                    },
                    onMusicClick = {
                        toast("آهنگ ما 🎵")
                    },
                    onSadClick = {
                        toast("وقتی دلمون گرفت ❤️")
                    },
                    onSpecialClick = {
                        toast("لحظه‌های خاص ✨")
                    },
                    onPrivateClick = {
                        toast("بخش خصوصی 🔐")
                    },
                    onSettingsClick = {
                        toast("تنظیمات رویارام ⚙️")
                    }
                )
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
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


data class PersianDate(
    val year: Int,
    val month: Int,
    val day: Int
)


private fun mod(a: Int, b: Int): Int {
    val result = a % b
    return if (result >= 0) result else result + b
}


private fun persianToJulianDay(
    year: Int,
    month: Int,
    day: Int
): Long {

    val epBase =
        year - if (year >= 0) 474 else 473

    val epYear =
        474 + mod(epBase, 2820)

    val monthDays =
        if (month <= 7) {
            (month - 1) * 31
        } else {
            (month - 1) * 30 + 6
        }

    return (
        day +
            monthDays +
            ((epYear * 682 - 110) / 2816) +
            (epYear - 1) * 365 +
            (epBase / 2820) * 1029983 +
            1948320
        ).toLong()
}


private fun gregorianToPersian(
    gy: Int,
    gm: Int,
    gd: Int
): PersianDate {

    val gDays = intArrayOf(
        31, 28, 31, 30, 31, 30,
        31, 31, 30, 31, 30, 31
    )

    val gyTemp = gy - 1600
    val gmTemp = gm - 1
    val gdTemp = gd - 1

    var gDayNo =
        365 * gyTemp +
            (gyTemp + 3) / 4 -
            (gyTemp + 99) / 100 +
            (gyTemp + 399) / 400

    var i = 0

    while (i < gmTemp) {
        gDayNo += gDays[i]
        i++
    }

    if (
        gmTemp > 1 &&
        gy % 4 == 0 &&
        (gy % 100 != 0 || gy % 400 == 0)
    ) {
        gDayNo++
    }

    gDayNo += gdTemp

    var jDayNo = gDayNo - 79

    val jNp = jDayNo / 12053

    var jDay = jDayNo % 12053

    var jy =
        979 +
            33 * jNp +
            4 * (jDay / 1461)

    jDay %= 1461

    if (jDay >= 366) {
        jy += (jDay - 1) / 365
        jDay = (jDay - 1) % 365
    }

    val jm: Int
    val jd: Int

    if (jDay < 186) {
        jm = 1 + jDay / 31
        jd = 1 + jDay % 31
    } else {
        jm = 7 + (jDay - 186) / 30
        jd = 1 + (jDay - 186) % 30
    }

    return PersianDate(
        jy,
        jm,
        jd
    )
}


private fun todayPersianDate(): PersianDate {

    val calendar = Calendar.getInstance()

    return gregorianToPersian(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}


private fun daysBetweenPersian(
    first: PersianDate,
    second: PersianDate
): Long {

    return persianToJulianDay(
        second.year,
        second.month,
        second.day
    ) -
        persianToJulianDay(
            first.year,
            first.month,
            first.day
        )
}


private fun nextBirthday(
    month: Int,
    day: Int,
    today: PersianDate
): PersianDate {

    val current =
        PersianDate(
            today.year,
            month,
            day
        )

    return if (
        daysBetweenPersian(
            today,
            current
        ) < 0
    ) {
        PersianDate(
            today.year + 1,
            month,
            day
        )
    } else {
        current
    }
}


private fun nextMonthlyAnniversary(
    today: PersianDate
): PersianDate {

    return if (today.day < START_DAY) {
        PersianDate(
            today.year,
            today.month,
            START_DAY
        )
    } else if (today.month == 12) {
        PersianDate(
            today.year + 1,
            1,
            START_DAY
        )
    } else {
        PersianDate(
            today.year,
            today.month + 1,
            START_DAY
        )
    }
}


private fun nextYearlyAnniversary(
    today: PersianDate
): PersianDate {

    val current =
        PersianDate(
            today.year,
            START_MONTH,
            START_DAY
        )

    return if (
        daysBetweenPersian(
            today,
            current
        ) < 0
    ) {
        PersianDate(
            today.year + 1,
            START_MONTH,
            START_DAY
        )
    } else {
        current
    }
}


private fun persianDigits(
    value: String
): String {

    return value
        .replace("0", "۰")
        .replace("1", "۱")
        .replace("2", "۲")
        .replace("3", "۳")
        .replace("4", "۴")
        .replace("5", "۵")
        .replace("6", "۶")
        .replace("7", "۷")
        .replace("8", "۸")
        .replace("9", "۹")
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

    val today =
        remember {
            todayPersianDate()
        }

    val start =
        PersianDate(
            START_YEAR,
            START_MONTH,
            START_DAY
        )

    val daysTogether =
        max(
            0L,
            daysBetweenPersian(
                start,
                today
            )
        )

    val monthDate =
        nextMonthlyAnniversary(today)

    val yearDate =
        nextYearlyAnniversary(today)

    val monthDays =
        max(
            0L,
            daysBetweenPersian(
                today,
                monthDate
            )
        )

    val yearDays =
        max(
            0L,
            daysBetweenPersian(
                today,
                yearDate
            )
        )

    val raminBirthday =
        nextBirthday(
            RAMIN_BIRTH_MONTH,
            RAMIN_BIRTH_DAY,
            today
        )

    val royaBirthday =
        nextBirthday(
            ROYA_BIRTH_MONTH,
            ROYA_BIRTH_DAY,
            today
        )

    val raminDays =
        max(
            0L,
            daysBetweenPersian(
                today,
                raminBirthday
            )
        )

    val royaDays =
        max(
            0L,
            daysBetweenPersian(
                today,
                royaBirthday
            )
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFDDE8),
                        Color(0xFFFFEEF4),
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
            modifier = Modifier.height(10.dp)
        )

        LuxuryBanner(
            onSettingsClick = onSettingsClick
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        MainDateCard(
            daysTogether = daysTogether,
            monthDays = monthDays,
            yearDays = yearDays,
            monthDate = monthDate,
            yearDate = yearDate
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        BirthdayMiniRow(
            raminDays = raminDays,
            royaDays = royaDays
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        HomeCardRow(
            left = Triple(
                "خاطرات ما",
                "لحظه‌های قشنگمون",
                Icons.Default.PhotoLibrary
            ),
            center = Triple(
                "نامه‌های عاشقانه",
                "حرف‌هایی از قلبمون",
                Icons.Default.Mail
            ),
            right = Triple(
                "آهنگ ما",
                "صدای قصه‌ی ما",
                Icons.Default.MusicNote
            ),
            leftClick = onMemoriesClick,
            centerClick = onLettersClick,
            rightClick = onMusicClick
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        HomeCardRow(
            left = Triple(
                "وقتی دلمون گرفت",
                "اینجا همیشه کنار همیم",
                Icons.Default.Favorite
            ),
            center = Triple(
                "چت دونفره",
                "حرف‌های من و تو ❤️",
                Icons.Default.Chat
            ),
            right = Triple(
                "بخش خصوصی",
                "فقط برای من و تو 🔐",
                Icons.Default.Lock
            ),
            leftClick = onSadClick,
            centerClick = onChatClick,
            rightClick = onPrivateClick
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {

            ImageFolderCard(
                title = "تنظیمات",
                subtitle = "شخصی‌سازی رویارام",
                icon = Icons.Default.Settings,
                modifier = Modifier.weight(1f),
                onClick = onSettingsClick,
                gradient = listOf(
                    Color(0xFFEADFFF),
                    Color(0xFFFFEFF5)
                )
            )

            LoveQuoteCard(
                modifier = Modifier.weight(2f),
                onClick = onSpecialClick
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        PeriodPrivateCard()

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "ساخته شده با ❤️ برای رامین و رویا",
            fontSize = 10.sp,
            color = SoftText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )
    }
}


@Composable
private fun LuxuryBanner(
    onSettingsClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp)
            .clip(
                RoundedCornerShape(28.dp)
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
                            Color.Black.copy(alpha = 0.08f),
                            Color.Black.copy(alpha = 0.02f),
                            Color(0xFF7A304D).copy(alpha = 0.35f)
                        )
                    )
                )
        )

        Card(
            modifier = Modifier
                .padding(12.dp)
                .size(44.dp)
                .clickable {
                    onSettingsClick()
                },
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(
                    alpha = 0.86f
                )
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "تنظیمات",
                    tint = DeepPink,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = 18.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "رویارام ♡",
                color = Color.White,
                fontSize = 29.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "قصه‌ی من و تو، برای همیشه",
                color = Color.White.copy(
                    alpha = 0.96f
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "♡  ─────────  ❤️  ─────────  ♡",
                color = Color.White.copy(
                    alpha = 0.9f
                ),
                fontSize = 9.sp
            )
        }
    }
}


@Composable
private fun MainDateCard(
    daysTogether: Long,
    monthDays: Long,
    yearDays: Long,
    monthDate: PersianDate,
    yearDate: PersianDate
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFFE1EA),
                            Color(0xFFFFF0F5),
                            Color(0xFFF6E9FF)
                        )
                    ),
                    RoundedCornerShape(28.dp)
                )
                .padding(
                    horizontal = 7.dp,
                    vertical = 10.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                DateSide(
                    icon = "📅",
                    title = "تاریخ آشنایی ما",
                    value = "۱۴۰۵/۰۳/۲۰",
                    modifier = Modifier.weight(1f)
                )

                MainCounter(
                    daysTogether = daysTogether,
                    modifier = Modifier.weight(1.1f)
                )

                DateSide(
                    icon = "💕",
                    title = "هر روز",
                    value = "بهانه‌ای برای عاشق‌تر شدن",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
private fun DateSide(
    icon: String,
    title: String,
    value: String,
    modifier: Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = icon,
            fontSize = 19.sp
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = DeepPink,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = value,
            fontSize = 8.sp,
            color = SoftText,
            textAlign = TextAlign.Center,
            lineHeight = 11.sp
        )
    }
}


@Composable
private fun MainCounter(
    daysTogether: Long,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .padding(
                horizontal = 3.dp
            )
            .height(96.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.82f),
                        Color(0xFFFFE5EE)
                    )
                ),
                RoundedCornerShape(50)
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "❤️",
                fontSize = 20.sp
            )

            Text(
                text = persianDigits(
                    daysTogether.toString()
                ),
                fontSize = 31.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DeepPink
            )

            Text(
                text = "روز کنار هم",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}


@Composable
private fun BirthdayMiniRow(
    raminDays: Long,
    royaDays: Long
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        BirthdayMiniCard(
            name = "رامین",
            date = "۲۰ شهریور",
            days = raminDays,
            modifier = Modifier.weight(1f)
        )

        BirthdayMiniCard(
            name = "رویا",
            date = "۱۵ آذر",
            days = royaDays,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun BirthdayMiniCard(
    name: String,
    date: String,
    days: Long,
    modifier: Modifier
) {

    Card(
        modifier = modifier.height(57.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.72f
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "🎂",
                fontSize = 17.sp
            )

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "تولد $name",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepPink
                )

                Text(
                    text = "$date • ${persianDigits(days.toString())} روز",
                    fontSize = 7.sp,
                    color = SoftText
                )
            }

            Text(
                text = "✨",
                fontSize = 13.sp
            )
        }
    }
}


@Composable
private fun HomeCardRow(
    left: Triple<String, String, ImageVector>,
    center: Triple<String, String, ImageVector>,
    right: Triple<String, String, ImageVector>,
    leftClick: () -> Unit,
    centerClick: () -> Unit,
    rightClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        ImageFolderCard(
            title = left.first,
            subtitle = left.second,
            icon = left.third,
            modifier = Modifier.weight(1f),
            onClick = leftClick,
            gradient = listOf(
                Color(0xFFFFDCE8),
                Color(0xFFFFEFF5)
            )
        )

        ImageFolderCard(
            title = center.first,
            subtitle = center.second,
            icon = center.third,
            modifier = Modifier.weight(1f),
            onClick = centerClick,
            gradient = listOf(
                Color(0xFFFFE0EA),
                Color(0xFFF5E6FF)
            )
        )

        ImageFolderCard(
            title = right.first,
            subtitle = right.second,
            icon = right.third,
            modifier = Modifier.weight(1f),
            onClick = rightClick,
            gradient = listOf(
                Color(0xFFF0E1FF),
                Color(0xFFFFE7EF)
            )
        )
    }
}


@Composable
private fun ImageFolderCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier,
    onClick: () -> Unit,
    gradient: List<Color>
) {

    Card(
        modifier = modifier
            .height(126.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        gradient
                    ),
                    RoundedCornerShape(23.dp)
                )
        ) {

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .size(62.dp)
                    .background(
                        Color.White.copy(
                            alpha = 0.68f
                        ),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = DeepPink,
                    modifier = Modifier.size(31.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(
                            alpha = 0.77f
                        )
                    )
                    .padding(
                        horizontal = 5.dp,
                        vertical = 9.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = subtitle,
                    fontSize = 7.sp,
                    color = SoftText,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}


@Composable
private fun LoveQuoteCard(
    modifier: Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(126.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF9E6A7E),
                            Color(0xFFE7A9BC)
                        )
                    ),
                    RoundedCornerShape(23.dp)
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
                    text = "♡",
                    color = Color.White,
                    fontSize = 25.sp
                )

                Text(
                    text = "دوستت دارم",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "نه فقط امروز،",
                    color = Color.White,
                    fontSize = 9.sp
                )

                Text(
                    text = "بلکه تا همیشه...",
                    color = Color.White,
                    fontSize = 9.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "❤️",
                    fontSize = 12.sp
                )
            }
        }
    }
}


@Composable
private fun PeriodPrivateCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(
                alpha = 0.68f
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 9.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(
                        LightPink,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "🩷",
                    fontSize = 16.sp
                )
            }

            Spacer(
                modifier = Modifier.width(9.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "مراقبت و حال خوب",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepPink
                )

                Text(
                    text = "بخش خصوصی تاریخ‌های ثبت‌شده",
                    fontSize = 7.sp,
                    color = SoftText
                )
            }

            Text(
                text = "🔐",
                fontSize = 13.sp
            )
        }
    }
}
