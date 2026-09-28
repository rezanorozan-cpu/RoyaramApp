package com.royaram.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

import com.google.firebase.auth.FirebaseAuth


class LoginActivity : FragmentActivity() {

    private val PREFS_NAME = "royaram_security"
    private val KEY_SECURE_LOGIN = "secure_login_enabled"

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
                                email,
                                password,
                                onResult
                            )
                        },

                        onForgotPassword = { email, onResult ->

                            resetPassword(
                                email,
                                onResult
                            )
                        },

                        onBiometric = {
                            showBiometricPrompt()
                        },

                        biometricAvailable =
                            isBiometricAvailable()
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

            onResult(
                false,
                "ایمیل را وارد کن"
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

                onResult(
                    true,
                    "خوش اومدی ❤️"
                )

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

            onResult(
                false,
                "اول ایمیلت را وارد کن"
            )

            return
        }

        auth.sendPasswordResetEmail(
            email.trim()
        )
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
            Intent(
                this,
                MainActivity::class.java
            )

        startActivity(intent)

        finish()
    }


    private fun isBiometricAvailable(): Boolean {

        val manager =
            BiometricManager.from(this)

        return manager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }


    private fun showBiometricPrompt() {

        if (!isBiometricAvailable()) {

            Toast.makeText(
                this,
                "ورود بیومتریک در این دستگاه در دسترس نیست",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val executor =
            ContextCompat.getMainExecutor(this)

        val biometricPrompt =
            BiometricPrompt(
                this,
                executor,

                object :
                    BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {

                        super.onAuthenticationSucceeded(
                            result
                        )

                        Toast.makeText(
                            this@LoginActivity,
                            "ورود موفق بود ❤️",
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

                .setTitle(
                    "ورود به رویارام"
                )

                .setSubtitle(
                    "برای ورود هویتت را تأیید کن"
                )

                .setDescription(
                    "این بخش فقط برای رامین و رویاست ❤️"
                )

                .setNegativeButtonText(
                    "لغو"
                )

                .build()


        biometricPrompt.authenticate(
            promptInfo
        )
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

    var loading by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    var messageError by remember {
        mutableStateOf(false)
    }

    var showPassword by remember {
        mutableStateOf(false)
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {


        Image(
            painter =
                painterResource(
                    id = R.drawable.couple_main
                ),

            contentDescription =
                "رامین و رویا",

            modifier =
                Modifier.fillMaxSize(),

            contentScale =
                ContentScale.Crop
        )


        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(

                                Color(0xCC160A10),

                                Color(0x990F080D),

                                Color(0xE6000000)
                            )
                        )
                    )
        )


        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 22.dp,
                        vertical = 34.dp
                    ),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {


            Box(

                modifier =
                    Modifier
                        .size(78.dp)
                        .clip(
                            RoundedCornerShape(26.dp)
                        )
                        .background(
                            Color.White.copy(
                                alpha = 0.18f
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "❤️",
                    fontSize = 40.sp
                )
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            Text(

                text = "رویارام",

                color =
                    Color.White,

                fontSize =
                    32.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            Text(

                text =
                    "قصه‌ی من و تو، برای همیشه",

                color =
                    Color.White.copy(
                        alpha = 0.82f
                    ),

                fontSize =
                    14.sp
            )


            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )


            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(28.dp)
                        )
                        .background(
                            Color.White.copy(
                                alpha = 0.94f
                            )
                        )
                        .padding(22.dp)
            ) {


                Text(

                    text =
                        "خوش اومدی ❤️",

                    color =
                        Color(0xFF29202A),

                    fontSize =
                        23.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(

                    text =
                        "برای ورود به دنیای دوتایی‌مون وارد شو",

                    color =
                        Color(0xFF777177),

                    fontSize =
                        13.sp
                )


                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )


                OutlinedTextField(

                    value =
                        email,

                    onValueChange = {

                        email = it

                        message = ""
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !loading,

                    singleLine =
                        true,

                    label = {
                        Text("ایمیل")
                    },

                    placeholder = {
                        Text(
                            "ایمیل خودت را وارد کن"
                        )
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Email
                        ),

                    shape =
                        RoundedCornerShape(16.dp),

                    colors =
                        OutlinedTextFieldDefaults.colors(

                            focusedBorderColor =
                                Color(0xFFE85D86),

                            focusedLabelColor =
                                Color(0xFFE85D86),

                            cursorColor =
                                Color(0xFFE85D86)
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                OutlinedTextField(

                    value =
                        password,

                    onValueChange = {

                        password = it

                        message = ""
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !loading,

                    singleLine =
                        true,

                    label = {
                        Text("رمز عبور")
                    },

                    placeholder = {
                        Text("رمز عبور")
                    },

                    visualTransformation =

                        if (showPassword) {

                            androidx.compose.ui.text.input.VisualTransformation.None

                        } else {

                            PasswordVisualTransformation()
                        },


                    trailingIcon = {

                        IconButton(

                            onClick = {

                                showPassword =
                                    !showPassword
                            }
                        ) {

                            Icon(

                                imageVector =

                                    if (showPassword) {

                                        Icons.Filled.VisibilityOff

                                    } else {

                                        Icons.Filled.Visibility
                                    },

                                contentDescription =

                                    if (showPassword) {

                                        "مخفی کردن رمز"

                                    } else {

                                        "نمایش رمز"
                                    }
                            )
                        }
                    },


                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Password
                        ),

                    shape =
                        RoundedCornerShape(16.dp),

                    colors =
                        OutlinedTextFieldDefaults.colors(

                            focusedBorderColor =
                                Color(0xFFE85D86),

                            focusedLabelColor =
                                Color(0xFFE85D86),

                            cursorColor =
                                Color(0xFFE85D86)
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                TextButton(

                    onClick = {

                        onForgotPassword(
                            email
                        ) { success, result ->

                            message =
                                result

                            messageError =
                                !success
                        }
                    },

                    enabled =
                        !loading
                ) {

                    Text(

                        text =
                            "رمز عبورم را فراموش کردم",

                        color =
                            Color(0xFFE04F79)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Button(

                    onClick = {

                        loading = true

                        message = ""

                        onLogin(

                            email,

                            password

                        ) { success, result ->

                            loading =
                                false

                            message =
                                result

                            messageError =
                                !success
                        }
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                    enabled =
                        !loading,

                    shape =
                        RoundedCornerShape(17.dp),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                Color(0xFFE85D86),

                            contentColor =
                                Color.White
                        )
                ) {


                    if (loading) {

                        CircularProgressIndicator(

                            modifier =
                                Modifier.size(24.dp),

                            color =
                                Color.White,

                            strokeWidth =
                                2.5.dp
                        )

                    } else {

                        Text(

                            text =
                                "ورود به دنیای ما ❤️",

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }


                if (biometricAvailable) {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    TextButton(

                        onClick =
                            onBiometric,

                        enabled =
                            !loading,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(

                            text =
                                "🔐  ورود با اثر انگشت / چهره",

                            color =
                                Color(0xFFB23A62),

                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }


                if (message.isNotBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    Text(

                        text =
                            message,

                        modifier =
                            Modifier.fillMaxWidth(),

                        color =

                            if (messageError) {

                                Color(0xFFD32F2F)

                            } else {

                                Color(0xFF2E7D32)
                            },

                        fontSize =
                            13.sp
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            Text(

                text =
                    "🔒  فضای خصوصی رامین و رویا",

                color =
                    Color.White.copy(
                        alpha = 0.85f
                    ),

                fontSize =
                    12.sp
            )
        }
    }
}
