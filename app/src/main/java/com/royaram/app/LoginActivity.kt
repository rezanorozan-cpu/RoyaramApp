package com.royaram.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    private val prefsName =
        "royaram_security"

    private val secureLoginKey =
        "secure_login_enabled"

    private var biometricStarted = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        auth =
            FirebaseAuth.getInstance()

        setContent {
            LoginScreen()
        }

        /*
         * اگر قبلاً وارد حساب شده‌ای،
         * بعد از نمایش کوتاه صفحه،
         * احراز هویت امن را خودکار باز می‌کنیم.
         */
        Handler(
            Looper.getMainLooper()
        ).postDelayed({

            tryAutoSecureLogin()

        }, 350)
    }

    private fun tryAutoSecureLogin() {

        if (biometricStarted) {
            return
        }

        val currentUser =
            auth.currentUser

        val secureLoginEnabled =
            getSharedPreferences(
                prefsName,
                Context.MODE_PRIVATE
            )
                .getBoolean(
                    secureLoginKey,
                    false
                )

        /*
         * اگر قبلاً ورود امن فعال نشده،
         * صفحه معمولی ورود نمایش داده می‌شود.
         */
        if (
            currentUser == null ||
            !secureLoginEnabled
        ) {
            return
        }

        if (
            !isSecureAuthenticationAvailable()
        ) {
            return
        }

        biometricStarted = true

        showBiometricPrompt()
    }

    private fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (email.isBlank()) {
            onResult(
                false,
                "ایمیل را وارد کن"
            )
            return
        }

        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {
            onResult(
                false,
                "فرمت ایمیل درست نیست"
            )
            return
        }

        if (password.isBlank()) {
            onResult(
                false,
                "رمز عبور را وارد کن"
            )
            return
        }

        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        )
            .addOnSuccessListener {

                getSharedPreferences(
                    prefsName,
                    Context.MODE_PRIVATE
                )
                    .edit()
                    .putBoolean(
                        secureLoginKey,
                        true
                    )
                    .apply()

                onResult(
                    true,
                    "ورود موفق بود ❤️"
                )
            }
            .addOnFailureListener { error ->

                val firebaseError =
                    error as? FirebaseAuthException

                val code =
                    firebaseError?.errorCode
                        ?: "UNKNOWN_ERROR"

                val message =
                    error.message
                        ?: "پیام خطا موجود نیست"

                onResult(
                    false,
                    "کد خطای Firebase:\n" +
                            "$code\n\n$message"
                )
            }
    }

    private fun resetPassword(
        email: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (email.isBlank()) {
            onResult(
                false,
                "اول ایمیلت را وارد کن"
            )
            return
        }

        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {
            onResult(
                false,
                "فرمت ایمیل درست نیست"
            )
            return
        }

        auth.sendPasswordResetEmail(
            email.trim()
        )
            .addOnSuccessListener {

                onResult(
                    true,
                    "لینک بازیابی رمز ارسال شد.\nایمیل و پوشه Spam را بررسی کن."
                )
            }
            .addOnFailureListener { error ->

                val firebaseError =
                    error as? FirebaseAuthException

                val code =
                    firebaseError?.errorCode
                        ?: "UNKNOWN_ERROR"

                val message =
                    error.message
                        ?: "پیام خطا موجود نیست"

                onResult(
                    false,
                    "کد خطای بازیابی Firebase:\n" +
                            "$code\n\n$message"
                )
            }
    }

    private fun isSecureAuthenticationAvailable():
            Boolean {

        val biometricManager =
            BiometricManager.from(this)

        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL

        return biometricManager.canAuthenticate(
            authenticators
        ) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun showBiometricPrompt() {

        if (auth.currentUser == null) {

            biometricStarted = false

            Toast.makeText(
                this,
                "اول یک بار با ایمیل و رمز وارد شو",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (
            !isSecureAuthenticationAvailable()
        ) {

            biometricStarted = false

            Toast.makeText(
                this,
                "اثر انگشت یا قفل امن گوشی در دسترس نیست",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val executor =
            androidx.core.content
                .ContextCompat
                .getMainExecutor(this)

        val biometricPrompt =
            BiometricPrompt(
                this,
                executor,
                object :
                    BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result:
                        BiometricPrompt.AuthenticationResult
                    ) {

                        super
                            .onAuthenticationSucceeded(
                                result
                            )

                        biometricStarted = false

                        openHome()
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {

                        super
                            .onAuthenticationError(
                                errorCode,
                                errString
                            )

                        biometricStarted = false

                        /*
                         * اگر کاربر لغو کرد،
                         * صفحه ورود باقی می‌ماند.
                         */
                        if (
                            errorCode !=
                            BiometricPrompt
                                .ERROR_CANCELED
                        ) {

                            Toast.makeText(
                                this@LoginActivity,
                                errString.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onAuthenticationFailed() {

                        super
                            .onAuthenticationFailed()

                        /*
                         * اینجا برنامه بسته نمی‌شود.
                         * کاربر می‌تواند دوباره امتحان کند.
                         */
                    }
                }
            )

        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(
                    "ورود امن به رویارام"
                )
                .setSubtitle(
                    "اثر انگشت یا قفل امن گوشی"
                )
                .setAllowedAuthenticators(
                    BiometricManager
                        .Authenticators
                        .BIOMETRIC_STRONG or
                            BiometricManager
                                .Authenticators
                                .DEVICE_CREDENTIAL
                )
                .build()

        biometricPrompt.authenticate(
            promptInfo
        )
    }

    private fun openHome() {

        startActivity(
            Intent(
                this,
                MainActivity::class.java
            )
        )

        finish()
    }

    @Composable
    private fun LoginScreen() {

        var email by remember {
            mutableStateOf("")
        }

        var password by remember {
            mutableStateOf("")
        }

        var passwordVisible by remember {
            mutableStateOf(false)
        }

        var message by remember {
            mutableStateOf("")
        }

        var isLoading by remember {
            mutableStateOf(false)
        }

        val backgroundBrush =
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFE8EF),
                    Color(0xFFFFF5F8),
                    Color.White
                )
            )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    backgroundBrush
                )
                .padding(24.dp),
            contentAlignment =
                Alignment.Center
        ) {

            Column(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text = "❤️",
                    fontSize = 58.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text = "رویارام",
                    style =
                        MaterialTheme
                            .typography
                            .headlineLarge
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text =
                        "قصه‌ی من و تو، برای همیشه",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )

                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        message = ""
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("ایمیل")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector =
                                Icons.Default.Email,
                            contentDescription =
                                null
                        )
                    },
                    shape =
                        RoundedCornerShape(18.dp)
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        message = ""
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("رمز عبور")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector =
                                Icons.Default.Lock,
                            contentDescription =
                                null
                        )
                    },
                    trailingIcon = {

                        IconButton(
                            onClick = {
                                passwordVisible =
                                    !passwordVisible
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (
                                        passwordVisible
                                    )
                                        Icons.Default
                                            .VisibilityOff
                                    else
                                        Icons.Default
                                            .Visibility,
                                contentDescription =
                                    null
                            )
                        }
                    },
                    visualTransformation =
                        if (
                            passwordVisible
                        )
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                    shape =
                        RoundedCornerShape(18.dp)
                )

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                Button(
                    onClick = {

                        isLoading = true
                        message = ""

                        loginUser(
                            email,
                            password
                        ) { success, result ->

                            isLoading = false
                            message = result

                            if (success) {
                                openHome()
                            }
                        }
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !isLoading,
                    shape =
                        RoundedCornerShape(18.dp)
                ) {

                    Text(
                        text =
                            if (isLoading)
                                "در حال ورود..."
                            else
                                "ورود به رویارام ❤️"
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                OutlinedButton(
                    onClick = {
                        showBiometricPrompt()
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(18.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Fingerprint,
                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.padding(
                                horizontal = 4.dp
                            )
                    )

                    Text(
                        text =
                            "ورود با اثر انگشت / رمز گوشی"
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                TextButton(
                    onClick = {

                        resetPassword(
                            email
                        ) { success, result ->

                            message = result

                            Toast.makeText(
                                this@LoginActivity,
                                result,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                ) {

                    Text(
                        text =
                            "رمز عبور را فراموش کرده‌ام"
                    )
                }

                if (
                    message.isNotBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    Color.White.copy(
                                        alpha = 0.8f
                                    ),
                                    RoundedCornerShape(
                                        16.dp
                                    )
                                )
                                .padding(14.dp)
                    ) {

                        Text(
                            text = message,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }
                }
            }
        }
    }
}
