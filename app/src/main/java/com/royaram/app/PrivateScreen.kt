package com.royaram.app

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

private val PrivateAuthenticators =
    BiometricManager.Authenticators.BIOMETRIC_WEAK or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL

private fun canUsePrivateLock(
    activity: FragmentActivity
): Boolean {
    return BiometricManager
        .from(activity)
        .canAuthenticate(PrivateAuthenticators) ==
            BiometricManager.BIOMETRIC_SUCCESS
}

private fun showPrivateBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("تأیید انجام نشد، دوباره امتحان کن ❤️")
            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)

                if (
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_CANCELED
                ) {
                    onError("ورود به بخش خصوصی لغو شد.")
                } else {
                    onError(errString.toString())
                }
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("بخش خصوصی رویارام 🔐")
        .setSubtitle("برای ورود، اثر انگشت یا قفل گوشی را تأیید کن")
        .setAllowedAuthenticators(PrivateAuthenticators)
        .build()

    biometricPrompt.authenticate(promptInfo)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateScreen(
    activity: FragmentActivity,
    onBack: () -> Unit
) {
    var unlocked by rememberSaveable {
        mutableStateOf(false)
    }

    var message by rememberSaveable {
        mutableStateOf("")
    }

    val canAuthenticate = canUsePrivateLock(activity)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("بخش خصوصی 🔐")
                },
                navigationIcon = {
                    Button(
                        onClick = onBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("بازگشت")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (unlocked) {

            PrivateUnlockedContent(
                paddingValues = paddingValues
            )

        } else {

            PrivateLockedContent(
                paddingValues = paddingValues,
                canAuthenticate = canAuthenticate,
                message = message,
                onUnlockClick = {
                    if (canAuthenticate) {

                        message = ""

                        showPrivateBiometricPrompt(
                            activity = activity,
                            onSuccess = {
                                unlocked = true
                                message = ""
                            },
                            onError = {
                                message = it
                            }
                        )

                    } else {
                        message =
                            "روی این گوشی اثر انگشت یا قفل امن فعال نیست."
                    }
                }
            )
        }
    }
}

@Composable
private fun PrivateLockedContent(
    paddingValues: PaddingValues,
    canAuthenticate: Boolean,
    message: String,
    onUnlockClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = androidx.compose.ui.graphics.Color(
                    0xFFFFE7EF
                )
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "قفل",
                    tint = androidx.compose.ui.graphics.Color(
                        0xFFB83D63
                    )
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = "اینجا فقط برای من و توئه ❤️",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "برای ورود به بخش خصوصی، هویتت رو تأیید کن.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Button(
                    onClick = onUnlockClick,
                    enabled = canAuthenticate,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.padding(4.dp)
                    )

                    Text("باز کردن بخش خصوصی 🔐")
                }

                if (message.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = message,
                        color = androidx.compose.ui.graphics.Color(
                            0xFFB83D63
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivateUnlockedContent(
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = androidx.compose.ui.graphics.Color(
                    0xFFFFE7EF
                )
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "خوش اومدی رامین و رویا ❤️",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "این بخش فقط برای شما دوتاست 🔐",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "به‌زودی خاطرات و چیزهای خصوصی اینجا قرار می‌گیرن.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
