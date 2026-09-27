package com.royaram.app

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : FragmentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContentView(
            androidx.compose.ui.platform.ComposeView(this).apply {
                setContent {
                    RoyaramLoginScreen(
                        onLogin = { email, password, onResult ->
                            loginUser(
                                email = email,
                                password = password,
                                onResult = onResult
                            )
                        },
                        onForgotPassword = { email, onResult ->
                            resetPassword(
                                email = email,
                                onResult = onResult
                            )
                        },
                        onBiometric = {
                            showBiometricPrompt()
                        },
                        biometricAvailable = isBiometricAvailable()
                    )
                }
            }
        )
    }

    private fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (email.isBlank()) {
            onResult(false, "ایمیل را وارد کن")
            return
        }

        if (password.isBlank()) {
            onResult(false, "رمز عبور را وارد کن")
            return
        }

        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        )
            .addOnSuccessListener {
                onResult(true, "ورود موفق بود ❤️")
                openHome()
            }
            .addOnFailureListener {
                onResult(
                    false,
                    "ایمیل یا رمز عبور اشتباه است"
                )
            }
    }

    private fun resetPassword(
        email: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (email.isBlank()) {
            onResult(false, "اول ایمیلت را وارد کن")
            return
        }

        auth.sendPasswordResetEmail(email.trim())
            .addOnSuccessListener {
                onResult(
                    true,
                    "لینک بازیابی رمز عبور ارسال شد ❤️"
                )
            }
            .addOnFailureListener {
                onResult(
                    false,
                    "ارسال لینک بازیابی انجام نشد"
                )
            }
    }

    private fun openHome() {

        val intent =
            android.content.Intent(
                this,
                MainActivity::class.java
            )

        startActivity(intent)
        finish()
    }

    private fun isBiometricAvailable(): Boolean {

        val biometricManager =
            BiometricManager.from(this)

        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun showBiometricPrompt() {

        if (!isBiometricAvailable()) {
            Toast.makeText(
                this,
                "ورود با اثر انگشت یا تشخیص چهره در این دستگاه در دسترس نیست",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val executor =
            androidx.core.content.ContextCompat.getMainExecutor(this)

        val biometricPrompt =
            BiometricPrompt(
                this,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)

                        Toast.makeText(
                            this@LoginActivity,
                            "ورود با موفقیت انجام شد ❤️",
                            Toast.LENGTH_SHORT
                        ).show()

                        openHome()
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(
                            errorCode,
                            errString
                        )

                        Toast.makeText(
                            this@LoginActivity,
                            errString.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()

                        Toast.makeText(
                            this@LoginActivity,
                            "تشخیص انجام نشد، دوباره امتحان کن",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("ورود به رویارام")
                .setSubtitle("برای ورود، هویت خودت را تأیید کن")
                .setDescription("این بخش مخصوص رامین و رویاست ❤️")
                .setNegativeButtonText("لغو")
                .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

@Composable
private fun RoyaramLoginScreen(
    onLogin: (
        String,
        String,
        (Boolean, String) -> Unit
    ) -> Unit,
    onForgotPassword: (
        String,
        (Boolean, String) -> Unit
    ) -> Unit,
    onBiometric: () -> Unit,
    biometricAvailable: Boolean
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    var messageIsError by remember {
        mutableStateOf(false)
    }

    val backgroundBrush =
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFE4EC),
                Color(0xFFFFF5F8),
                Color.White
            )
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "❤️",
                fontSize = 58.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "رویارام",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "قصه‌ی من و تو، برای همیشه",
                fontSize = 16.sp,
                color = Color(0xFF777777)
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    message = ""
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("ایمیل")
                },
                singleLine = true,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    message = ""
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("رمز عبور")
                },
                singleLine = true,
                visualTransformation =
                    PasswordVisualTransformation(),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Button(
                onClick = {

                    isLoading = true
                    message = ""

                    onLogin(
                        email,
                        password
                    ) { success, result ->

                        isLoading = false
                        message = result
                        messageIsError = !success
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp)
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text = "ورود به رویارام",
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = {

                    onForgotPassword(
                        email
                    ) { success, result ->

                        message = result
                        messageIsError = !success
                    }
                },
                enabled = !isLoading
            ) {
                Text("رمز عبورم را فراموش کردم")
            }

            if (biometricAvailable) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                TextButton(
                    onClick = onBiometric,
                    enabled = !isLoading
                ) {
                    Text(
                        text = "🔐 ورود با اثر انگشت / چهره"
                    )
                }
            }

            if (message.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = message,
                    color =
                        if (messageIsError) {
                            Color(0xFFD32F2F)
                        } else {
                            Color(0xFF2E7D32)
                        },
                    fontSize = 14.sp
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "فقط برای رامین و رویا ❤️",
                fontSize = 13.sp,
                color = Color(0xFF999999)
            )
        }
    }
}
