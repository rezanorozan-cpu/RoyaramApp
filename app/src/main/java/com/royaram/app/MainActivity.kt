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
import androidx.compose.ui.background
import androidx.compose.ui.clip
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
                today.year
