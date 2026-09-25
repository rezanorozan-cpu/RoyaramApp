package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : FragmentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            showBiometricPrompt()
        } else {
            showLoginScreen()
        }
    }

    private fun showLoginScreen() {
        setContent {
            LoginScreen(
                onLogin = { email, password ->
                    login(email, password)
                },
                onForgotPassword = { email ->
                    resetPassword(email)
                },
                onBiometricLogin = {
                    showBiometricPrompt()
                }
            )
        }
    }

    private fun login(
        email: String,
        password: String
    ) {

        if (email.isBlank()) {
            showLoginScreenWithError("ایمیل رو وارد کن ❤️")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            showLoginScreenWithError("فرمت ایمیل درست نیست")
            return
        }

        if (password.isBlank()) {
            showLoginScreenWithError("رمز عبور رو وارد کن 🔐")
            return
        }

        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                openHome()
            } else {
                showLoginScreenWithError(
                    "ایمیل یا رمز عبور اشتباه است"
                )
            }
        }
    }

    private fun resetPassword(email: String) {

        if (email.isBlank()) {
            showLoginScreenWithError(
                "اول ایمیلت رو وارد کن 📧"
            )
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            showLoginScreenWithError(
                "فرمت ایمیل درست نیست"
            )
            return
        }

        auth.sendPasswordResetEmail(
            email.trim()
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                showLoginScreenWithError(
                    "لینک تغییر رمز به ایمیلت ارسال شد 📧❤️"
                )

            } else {

                showLoginScreenWithError(
                    "ارسال لینک بازیابی انجام نشد"
                )
            }
        }
    }

    private fun showLoginScreenWithError(message: String) {

        setContent {
            LoginScreen(
                initialError = message,
                onLogin = { email, password ->
                    login(email, password)
                },
                onForgotPassword = { email ->
                    resetPassword(email)
                },
                onBiometricLogin = {
                    showBiometricPrompt()
                }
            )
        }
    }

    private fun showBiometricPrompt() {

        val biometricManager = BiometricManager.from(this)

        val result = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        )

        if (result != BiometricManager.BIOMETRIC_SUCCESS) {

            if (auth.currentUser == null) {
                showLoginScreenWithError(
                    "بیومتریک روی این گوشی آماده نیست"
                )
            } else {
                Toast.makeText(
                    this,
                    "بیومتریک در دسترس نیست 🔐",
                    Toast.LENGTH_SHORT
                ).show()
            }

            return
        }

        val executor =
            ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    if (auth.currentUser != null) {
                        openHome()
                    } else {
                        showLoginScreenWithError(
                            "ابتدا با ایمیل و رمز وارد شو ❤️"
                        )
                    }
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(
                        errorCode,
                        errString
                    )

                    if (auth.currentUser == null) {
                        showLoginScreenWithError(
                            "ورود با اثر انگشت لغو شد"
                        )
                    }
                }
            }
        )

        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("ورود به رویارام ❤️")
                .setSubtitle(
                    "برای ورود، هویت خودت را تأیید کن"
                )
                .setNegativeButtonText("استفاده از رمز عبور")
                .build()

        biometricPrompt.authenticate(promptInfo)
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
}

@Composable
private fun LoginScreen(
    initialError: String? = null,
    onLogin: (String, String) -> Unit,
    onForgotPassword: (String) -> Unit,
    onBiometricLogin: () -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf(initialError)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFDCE6),
                        Color(0xFFFFEEF3),
                        Color.White
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .imePadding()
                .navigationBarsPadding()
                .padding(
                    horizontal = 22.dp,
                    vertical = 30.dp
                ),

            horizontalAlignment =
                Alignment.CenterHoriz
