package com.royaram.app

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
private val PrivatePink = Color(0xFFE85D86)
private val PrivateLightPink = Color(0xFFFFE7EF)
private val PrivateDeepPink = Color(0xFFB83D63)
private val PrivateTextDark = Color(0xFF33252B)
private val PrivateSoftText = Color(0xFF82747A)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PrivateScreen(
    activity: ComponentActivity,
    onBack: () -> Unit
)
    var isUnlocked by remember {
        mutableStateOf(false)
    }

    var biometricAvailable by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {

        val manager = BiometricManager.from(activity)

        val result = manager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        biometricAvailable =
            result == BiometricManager.BIOMETRIC_SUCCESS
    }

    Scaffold(
        containerColor = Color.Transparent,

        topBar = {

            TopAppBar(
                title = {

                    Column {

                        Text(
                            text = "بخش خصوصی 🔐",
                            color = PrivateTextDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "فقط برای رامین و رویا ❤️",
                            color = PrivateSoftText,
                            fontSize = 12.sp
                        )
                    }
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = PrivateDeepPink
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFE4ED),
                            Color(0xFFFFF4F7),
                            Color.White
                        )
                    )
                )
                .padding(paddingValues),

            contentAlignment = Alignment.Center
        ) {

            if (!isUnlocked) {

                LockedPrivateContent(
                    biometricAvailable = biometricAvailable,
                    onUnlock = {

                        showBiometricPrompt(
                            activity = activity,

                            onSuccess = {
                                isUnlocked = true
                            }
                        )
                    }
                )

            } else {

                UnlockedPrivateContent()
            }
        }
    }
}

@Composable
private fun LockedPrivateContent(
    biometricAvailable: Boolean,
    onUnlock: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(34.dp))
                .background(PrivateLightPink),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = PrivatePink,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "این قسمت فقط برای شما دوتاست ❤️",
            color = PrivateTextDark,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "برای ورود، قفل گوشی را تأیید کن",
            color = PrivateSoftText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Button(
            onClick = onUnlock,
            enabled = biometricAvailable,

            colors = ButtonDefaults.buttonColors(
                containerColor = PrivatePink,
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray
            ),

            shape = RoundedCornerShape(18.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text(
                text = if (biometricAvailable) {
                    "باز کردن بخش خصوصی"
                } else {
                    "قفل گوشی فعال نیست"
                },

                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun UnlockedPrivateContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(PrivateLightPink),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.LockOpen,
                contentDescription = null,
                tint = PrivatePink,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Text(
            text = "خوش اومدی رامین و رویا ❤️",
            color = PrivateTextDark,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "این فضای خصوصی مخصوص قصه‌ی خودتونه.",
            color = PrivateSoftText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "🔐 فضای خصوصی ما\n\nبه‌زودی عکس‌ها، نوشته‌ها و رازهای کوچیک اینجا قرار می‌گیرن ❤️",
            color = PrivateTextDark,
            fontSize = 15.sp,
            lineHeight = 25.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit
) {

    val executor = androidx.core.content.ContextCompat.getMainExecutor(
        activity
    )

    val prompt = BiometricPrompt(
        activity,
        executor,

        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)

                onSuccess()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("ورود به بخش خصوصی ❤️")
        .setSubtitle("برای رامین و رویا")
        .setDescription("با اثر انگشت، تشخیص چهره یا قفل گوشی وارد شوید.")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()

    prompt.authenticate(promptInfo)
}
