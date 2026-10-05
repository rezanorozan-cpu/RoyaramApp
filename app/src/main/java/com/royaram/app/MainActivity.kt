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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.max

private val Pink = Color(0xFFE85D86)
private val DeepPink = Color(0xFFB83D63)
private val LightPink = Color(0xFFFFE7EF)
private val SoftPink = Color(0xFFFFF1F5)
private val TextDark = Color(0xFF33252B)
private val SoftText = Color(0xFF82747A)
private val Background = Color(0xFFFFF7F9)

/*
 * =========================================================
 * تاریخ‌های اصلی رویارام - شمسی
 * =========================================================
 */

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

            RoyaramApp(
                onChatClick = {
                    startActivity(
                        Intent(
                            this,
                            ChatActivity::class.java
                        )
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


/*
 * =========================================================
 * مدل پوشه‌ها
 * =========================================================
 */

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


/*
 * =========================================================
 * مدل تاریخ شمسی
 * =========================================================
 */

data class PersianDate(
    val year: Int,
    val month: Int,
    val day: Int
)


/*
 * =========================================================
 * تبدیل تاریخ شمسی به روز مطلق
 *
 * برای محاسبات روزشمار استفاده می‌شود.
 * =========================================================
 */

private fun persianToJulianDay(
    year: Int,
    month: Int,
    day: Int
): Long {

    val epBase =
        year - if (year >= 0) 474 else 473

    val epYear =
        474 + mod(
            epBase,
            2820
        )

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


private fun mod(
    a: Int,
    b: Int
): Int {

    val result = a % b

    return if (result >= 0) {
        result
    } else {
        result + b
    }
}


/*
 * =========================================================
 * امروز به تاریخ شمسی
 * =========================================================
 */

private fun todayPersianDate(): PersianDate {

    val calendar =
        Calendar.getInstance()

    val gy =
        calendar.get(Calendar.YEAR)

    val gm =
        calendar.get(Calendar.MONTH) + 1

    val gd =
        calendar.get(Calendar.DAY_OF_MONTH)

    return gregorianToPersian(
        gy,
        gm,
        gd
    )
}


/*
 * =========================================================
 * تبدیل میلادی به شمسی
 * =========================================================
 */

private fun gregorianToPersian(
    gy: Int,
    gm: Int,
    gd: Int
): PersianDate {

    val gDaysInMonth =
        intArrayOf(
            31, 28, 31, 30, 31, 30,
            31, 31, 30, 31, 30, 31
        )

    var gyTemp = gy - 1600
    var gmTemp = gm - 1
    val gdTemp = gd - 1

    var gDayNo =
        365 * gyTemp +
                (gyTemp + 3) / 4 -
                (gyTemp + 99) / 100 +
                (gyTemp + 399) / 400

    var i = 0

    while (i < gmTemp) {
        gDayNo +=
            gDaysInMonth[i]
        i++
    }

    if (
        gmTemp > 1 &&
        (
            gy % 4 == 0 &&
                    (
                        gy % 100 != 0 ||
                                gy % 400 == 0
                        )
            )
    ) {
        gDayNo++
    }

    gDayNo += gdTemp

    var jDayNo =
        gDayNo - 79

    val jNp =
        jDayNo / 12053

    var jDay =
        jDayNo % 12053

    var jy =
        979 + 33 * jNp + 4 * (jDay / 1461)

    jDay %= 1461

    if (jDay >= 366) {

        jy +=
            (jDay - 1) / 365

        jDay =
            (jDay - 1) % 365
    }

    val jm: Int
    val jd: Int

    if (jDay < 186) {

        jm =
            1 + jDay / 31

        jd =
            1 + jDay % 31

    } else {

        jm =
            7 + (jDay - 186) / 30

        jd =
            1 + (jDay - 186) % 30
    }

    return PersianDate(
        jy,
        jm,
        jd
    )
}


/*
 * =========================================================
 * تعداد روزهای بین دو تاریخ شمسی
 * =========================================================
 */

private fun daysBetweenPersian(
    first: PersianDate,
    second: PersianDate
): Long {

    return (
        persianToJulianDay(
            second.year,
            second.month,
            second.day
        ) -
                persianToJulianDay(
                    first.year,
                    first.month,
                    first.day
                )
        )
}


/*
 * =========================================================
 * تاریخ تولد بعدی
 * =========================================================
 */

private fun nextBirthday(
    birthMonth: Int,
    birthDay: Int,
    today: PersianDate
): PersianDate {

    val thisYearBirthday =
        PersianDate(
            today.year,
            birthMonth,
            birthDay
        )

    val birthdayPassed =
        daysBetweenPersian(
            today,
            thisYearBirthday
        ) < 0

    return if (birthdayPassed) {

        PersianDate(
            today.year + 1,
            birthMonth,
            birthDay
        )

    } else {

        thisYearBirthday
    }
}


/*
 * =========================================================
 * ماهگرد بعدی
 * روز ثابت = ۲۰ هر ماه
 * =========================================================
 */

private fun nextMonthlyAnniversary(
    today: PersianDate
): PersianDate {

    return if (today.day < START_DAY) {

        PersianDate(
            today.year,
            today.month,
            START_DAY
        )

    } else {

        if (today.month == 12) {

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
}


/*
 * =========================================================
 * سالگرد بعدی
 * =========================================================
 */

private fun nextYearlyAnniversary(
    today: PersianDate
): PersianDate {

    val anniversary =
        PersianDate(
            today.year,
            START_MONTH,
            START_DAY
        )

    return if (
        daysBetweenPersian(
            today,
            anniversary
        ) < 0
    ) {

        PersianDate(
            today.year + 1,
            START_MONTH,
            START_DAY
        )

    } else {

        anniversary
    }
}


/*
 * =========================================================
 * صفحه اصلی
 * =========================================================
 */

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

    /*
     * روزهای گذشته از شروع آشنایی
     */

    val startDate =
        PersianDate(
            START_YEAR,
            START_MONTH,
            START_DAY
        )

    val daysTogether =
        max(
            0L,
            daysBetweenPersian(
                startDate,
                today
            )
        )


    /*
     * تولد رامین
     */

    val raminBirthday =
        nextBirthday(
            RAMIN_BIRTH_MONTH,
            RAMIN_BIRTH_DAY,
            today
        )

    val daysToRaminBirthday =
        max(
            0L,
            daysBetweenPersian(
                today,
                raminBirthday
            )
        )


    /*
     * تولد رویا
     */

    val royaBirthday =
        nextBirthday(
            ROYA_BIRTH_MONTH,
            ROYA_BIRTH_DAY,
            today
        )

    val daysToRoyaBirthday =
        max(
            0L,
            daysBetweenPersian(
                today,
                royaBirthday
            )
        )


    /*
     * ماهگرد
     */

    val nextMonthDay =
        nextMonthlyAnniversary(
            today
        )

    val daysToMonthAnniversary =
        max(
            0L,
            daysBetweenPersian(
                today,
                nextMonthDay
            )
        )


    /*
     * سالگرد
     */

    val nextYearAnniversary =
        nextYearlyAnniversary(
            today
        )

    val daysToYearAnniversary =
        max(
            0L,
            daysBetweenPersian(
                today,
                nextYearAnniversary
            )
        )


    /*
     * وضعیت پریودی ثبت‌شده
     */

    val periodStart =
        PersianDate(
            PERIOD_START_YEAR,
            PERIOD_START_MONTH,
            PERIOD_START_DAY
        )

    val periodEnd =
        PersianDate(
            PERIOD_END_YEAR,
            PERIOD_END_MONTH,
            PERIOD_END_DAY
        )

    val periodStatus =
        when {

            daysBetweenPersian(
                periodStart,
                today
            ) < 0 -> {
                "شروع: ۱۴۰۵/۰۷/۰۳"
            }

            daysBetweenPersian(
                today,
                periodEnd
            ) >= 0 -> {
                "پایان: ۱۴۰۵/۰۷/۱۰"
            }

            else -> {
                "در بازه ثبت‌شده"
            }
        }


    val folders =
        listOf(

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
        modifier =
            Modifier
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
            modifier =
                Modifier.height(12.dp)
        )


        /*
         * =================================================
         * بنر اصلی
         * =================================================
         */

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(
                        RoundedCornerShape(24.dp)
                    )
        ) {

            androidx.compose.foundation.Image(
                painter =
                    painterResource(
                        id =
                            R.drawable.couple_main
                    ),

                contentDescription =
                    "رامین و رویا",

                contentScale =
                    ContentScale.Fit,

                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Color.White
                        )
            )

            Box(
                modifier =
                    Modifier
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

            Column(
                modifier =
                    Modifier
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
                    text =
                        "رامین ❤️ رویا",

                    color =
                        Color.White,

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "قصه‌ی من و تو",

                    color =
                        Color.White.copy(
                            alpha = 0.94f
                        ),

                    fontSize =
                        10.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        /*
         * =================================================
         * کادر اصلی روزشمار
         * =================================================
         */

        Card(
            modifier =
                Modifier
                    .fillMaxWidth(),

            shape =
                RoundedCornerShape(22.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White.copy(
                            alpha = 0.96f
                        )
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 10.dp,
                            horizontal = 8.dp
                        )
            ) {

                Text(
                    text =
                        "دنیای تاریخ‌های ما",

                    modifier =
                        Modifier.fillMaxWidth(),

                    textAlign =
                        TextAlign.Center,

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        DeepPink
                )

                Spacer(
                    modifier =
                        Modifier.height(7.dp)
                )


                /*
                 * ردیف اول
                 */

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceEvenly
                ) {

                    CounterItem(
                        icon = "❤️",
                        value =
                            daysTogether.toString(),
                        label =
                            "روز کنار هم",
                        modifier =
                            Modifier.weight(1f)
                    )

                    CounterDivider()

                    CounterItem(
                        icon = "🌙",
                        value =
                            daysToMonthAnniversary.toString(),
                        label =
                            "روز تا ماهگرد",
                        modifier =
                            Modifier.weight(1f)
                    )

                    CounterDivider()

                    CounterItem(
                        icon = "💕",
                        value =
                            daysToYearAnniversary.toString(),
                        label =
                            "روز تا سالگرد",
                        modifier =
                            Modifier.weight(1f)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                /*
                 * ردیف تولدها
                 */

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceEvenly
                ) {

                    CounterItem(
                        icon = "🎂",
                        value =
                            daysToRaminBirthday.toString(),
                        label =
                            "تا تولد رامین",
                        modifier =
                            Modifier.weight(1f)
                    )

                    CounterDivider()

                    CounterItem(
                        icon = "🎂",
                        value =
                            daysToRoyaBirthday.toString(),
                        label =
                            "تا تولد رویا",
                        modifier =
                            Modifier.weight(1f)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                /*
                 * پریودی
                 */

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFFFF0F4),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(
                                vertical = 7.dp,
                                horizontal = 10.dp
                            )
                ) {

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "🩷",

                            fontSize =
                                14.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.size(5.dp)
                        )

                        Text(
                            text =
                                "بازه ثبت‌شده پریودی: " +
                                        "۱۴۰۵/۰۷/۰۳ تا ۱۴۰۵/۰۷/۱۰",

                            fontSize =
                                9.sp,

                            color =
                                SoftText,

                            textAlign =
                                TextAlign.Center
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        periodStatus,

                    modifier =
                        Modifier.fillMaxWidth(),

                    textAlign =
                        TextAlign.Center,

                    fontSize =
                        8.sp,

                    color =
                        SoftText
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        /*
         * =================================================
         * دو ردیف سه‌تایی پوشه‌ها
         * =================================================
         */

        LazyVerticalGrid(
            columns =
                GridCells.Fixed(3),

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(268.dp),

            contentPadding =
                PaddingValues(0.dp),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp),

            verticalArrangement =
                Arrangement.spacedBy(8.dp),

            userScrollEnabled =
                false
        ) {

            items(
                folders
            ) { folder ->

                FolderCard(
                    folder =
                        folder,

                    onClick = {

                        when (
                            folder.type
                        ) {

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

                            else ->
                                Unit
                        }
                    }
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        /*
         * =================================================
         * ردیف سوم
         * =================================================
         */

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            FolderCard(
                folder =
                    FolderItem(
                        "بخش خصوصی",
                        "فقط من و تو",
                        FolderType.PRIVATE
                    ),

                modifier =
                    Modifier.weight(1f),

                onClick =
                    onPrivateClick
            )

            LoveQuoteCard(
                modifier =
                    Modifier.weight(1f)
            )
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        /*
         * =================================================
         * تنظیمات
         * =================================================
         */

        Card(
            modifier =
                Modifier
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
                modifier =
                    Modifier
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

                    tint =
                        DeepPink,

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

                    fontSize =
                        12.sp,

                    color =
                        SoftText
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

            fontSize =
                10.sp,

            color =
                SoftText,

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


/*
 * =========================================================
 * آیتم روزشمار
 * =========================================================
 */

@Composable
fun CounterItem(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier =
            modifier,

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text =
                icon,

            fontSize =
                15.sp
        )

        Text(
            text =
                value,

            fontSize =
                18.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                DeepPink
        )

        Text(
            text =
                label,

            fontSize =
                8.sp,

            color =
                SoftText,

            textAlign =
                TextAlign.Center
        )
    }
}


/*
 * خط جداکننده
 */

@Composable
fun CounterDivider() {

    Box(
        modifier =
            Modifier
                .size(
                    width = 1.dp,
                    height = 36.dp
                )
                .background(
                    LightPink
                )
    )
}


/*
 * =========================================================
 * کارت پوشه
 * =========================================================
 */

@Composable
fun FolderCard(
    folder: FolderItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val icon: ImageVector =
        when (folder.type) {

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
        modifier =
            modifier
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
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(7.dp),

            verticalArrangement =
                Arrangement.Center,

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(
                modifier =
                    Modifier
                        .size(43.dp)
                        .background(
                            SoftPink,
                            RoundedCornerShape(14.dp)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        icon,

                    contentDescription =
                        folder.title,

                    tint =
                        DeepPink,

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

                fontSize =
                    11.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextDark,

                textAlign =
                    TextAlign.Center,

                maxLines =
                    1
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text =
                    folder.subtitle,

                fontSize =
                    8.sp,

                color =
                    SoftText,

                textAlign =
                    TextAlign.Center,

                maxLines =
                    1
            )
        }
    }
}


/*
 * =========================================================
 * کادر جملات عاشقانه
 * =========================================================
 */

@Composable
fun LoveQuoteCard(
    modifier: Modifier = Modifier
) {

    Card(
        modifier =
            modifier
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
            modifier =
                Modifier
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

                contentDescription =
                    null,

                tint =
                    Pink,

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

                fontSize =
                    12.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    DeepPink,

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

                fontSize =
                    8.sp,

                color =
                    TextDark,

                lineHeight =
                    12.sp,

                textAlign =
                    TextAlign.Center
            )
        }
    }
}
