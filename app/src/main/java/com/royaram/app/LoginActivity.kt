package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : FragmentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            LoginScreen(
                onLogin = { email, password ->
                    login(email, password)
                },
                onBiometricLogin = {
                    showBiometricPrompt()
                }
            )
        }
    }

    private fun login(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(
                this,
                "ایمیل و رمز عبور را وارد کن ❤️",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                Toast.makeText(
                    this,
                    "ورود موفق بود ❤️",
                    Toast.LENGTH_SHORT
                ).show()

                openHome()

            } else {

                Toast.makeText(
                    this,
                    "ایمیل یا رمز عبور اشتباه است",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showBiometricPrompt() {

        val biometricManager = BiometricManager.from(this)

        val canAuthenticate =
            biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {

            Toast.makeText(
                this,
                "اثر انگشت روی این گوشی آماده نیست 🔐",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val executor = androidx.core.content.ContextCompat.getMainExecutor(this)

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
                        Toast.makeText(
                            this@LoginActivity,
                            "ابتدا یک‌بار با ایمیل و رمز وارد شو ❤️",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                    Toast.makeText(
                        this@LoginActivity,
                        "ورود با اثر انگشت لغو شد",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("ورود به رویارام ❤️")
            .setSubtitle("اثر انگشت خودت را تأیید کن")
            .setNegativeButtonText("استفاده از رمز عبور")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun openHome() {
        startActivity(
            Intent(this, MainActivity::class.java)
        )
        finish()
    }
}

@Composable
private fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onBiometricLogin: () -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(
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
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "رویارام ❤️",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF402A30)
        )

        Spacer(
            Modifier.height(8.dp)
        )

        Text(
            text = "ورود به دنیای دونفره‌ی ما",
            fontSize = 15.sp,
            color = Color(0xFF795C64)
        )

        Spacer(
            Modifier.height(30.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("ایمیل")
            },
            singleLine = true
        )

        Spacer(
            Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("رمز عبور")
            },
            visualTransformation =
                PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(
            Modifier.height(20.dp)
        )

        Button(
            onClick = {
                onLogin(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE85D75)
            )
        ) {
            Text(
                "ورود با رمز عبور ❤️",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        OutlinedButton(
            onClick = {
                onBiometricLogin()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                "ورود با اثر انگشت 🔐",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
