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
import com.google.firebase.auth.FirebaseAuthException


class LoginActivity : FragmentActivity() {

    private lateinit var auth: FirebaseAuth

    private val PREFS_NAME =
        "royaram_security"

    private val KEY_SECURE_LOGIN =
        "secure_login_enabled"


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        auth =
            FirebaseAuth.getInstance()

        setContentView(
            androidx.compose.ui.platform.ComposeView(this).apply {

                setContent {

                    RoyaramLoginScreen(

                        onLogin = {
                                email,
                                password,
                                onResult ->

                            loginUser(
                                email,
                                password,
                                onResult
                            )
                        },

                        onForgotPassword = {
                                email,
                                onResult ->

                            resetPassword(
                                email,
                                onResult
                            )
                        },

                        onBiometric = {
                            showBiometricPrompt()
                        },

                        biometricAvailable =
                            isSecureDeviceAuthenticationAvailable()
                    )
                }
            }
        )
    }


    /*
     * بررسی می‌کند آیا دستگاه یکی از روش‌های
     * احراز هویت امن را دارد:
     *
     * اثر انگشت
     * چهره
     * PIN
     * Pattern
     * Password گوشی
     */
    private fun isSecureDeviceAuthenticationAvailable(): Boolean {

        val manager =
            BiometricManager.from(this)

        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL

        return manager.canAuthenticate(
            authenticators
        ) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }


    /*
     * بررسی می‌کند ورود امن قبلاً فعال شده یا نه.
     */
    private fun isSecureLoginEnabled(): Boolean {

        return getSharedPreferences(
            PREFS_NAME,
            MODE_PRIVATE
        ).getBoolean(
            KEY_SECURE_LOGIN,
            false
        )
    }


    /*
     * ورود با ایمیل و رمز عبور
     */
    private fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {

        val cleanEmail =
            email.trim()

        if (cleanEmail.isBlank()) {

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
            cleanEmail,
            password
        )
            .addOnSuccessListener {

                /*
                 * بعد از اولین ورود موفق،
                 * ورود امن دستگاه فعال می‌شود.
                 *
                 * رمز عبور ذخیره نمی‌شود.
                 */
                getSharedPreferences(
                    PREFS_NAME,
                    MODE_PRIVATE
                )
                    .edit()
                    .putBoolean(
                        KEY_SECURE_LOGIN,
                        true
                    )
                    .apply()

                onResult(
                    true,
                    "ورود موفق بود ❤️"
                )

                openHome()
            }

            .addOnFailureListener { error ->

                val message =
                    when (
                        (error as? FirebaseAuthException)
                            ?.errorCode
                    ) {

                        "ERROR_INVALID_EMAIL" ->
                            "فرمت ایمیل درست نیست"

                        "ERROR_INVALID_CREDENTIAL" ->
                            "ایمیل یا رمز عبور صحیح نیست"

                        "ERROR_WRONG_PASSWORD" ->
                            "رمز عبور صحیح نیست"

                        "ERROR_USER_NOT_FOUND" ->
                            "حسابی با این ایمیل پیدا نشد"

                        "ERROR_USER_DISABLED" ->
                            "این حساب غیرفعال شده است"

                        "ERROR_TOO_MANY_REQUESTS" ->
                            "تلاش‌های زیادی انجام شده. کمی بعد دوباره امتحان کن"

                        "ERROR_NETWORK_REQUEST_FAILED" ->
                            "اتصال اینترنت برقرار نیست"

                        else ->
                            "ورود انجام نشد. دوباره امتحان کن"
                    }

                onResult(
                    false,
                    message
                )
            }
    }


    /*
     * بازیابی رمز عبور
     */
    private fun resetPassword(
        email: String,
        onResult: (Boolean, String) -> Unit
    ) {

        val cleanEmail =
            email.trim()

        if (cleanEmail.isBlank()) {

            onResult(
                false,
                "اول ایمیلت را وارد کن"
            )

            return
        }

        auth.sendPasswordResetEmail(
            cleanEmail
        )
            .addOnSuccessListener {

                onResult(
                    true,
                    "لینک بازیابی رمز ارسال شد ❤️\nایمیل اصلی و پوشه Spam را هم بررسی کن."
                )
            }

            .addOnFailureListener { error ->

                val message =
                    when (
                        (error as? FirebaseAuthException)
                            ?.errorCode
                    ) {

                        "ERROR_INVALID_EMAIL" ->
                            "فرمت ایمیل درست نیست"

                        "ERROR_USER_NOT_FOUND" ->
                            "حسابی با این ایمیل پیدا نشد"

                        "ERROR_NETWORK_REQUEST_FAILED" ->
                            "اتصال اینترنت برقرار نیست"

                        "ERROR_TOO_MANY_REQUESTS" ->
                            "درخواست‌های زیادی ارسال شده. کمی بعد دوباره امتحان کن"

                        else ->
                            "ارسال لینک بازیابی انجام نشد. دوباره امتحان کن"
                    }

                onResult(
                    false,
                    message
                )
            }
    }


    /*
     * باز کردن صفحه اصلی
     */
    private fun openHome() {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            )

        startActivity(intent)

        finish()
    }


    /*
     * ورود امن با اثر انگشت،
     * چهره یا قفل خود گوشی
     */
    private fun showBiometricPrompt() {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "اول با ایمیل و رمز وارد شو ❤️",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (
            !isSecureDeviceAuthenticationAvailable()
        ) {

            Toast.makeText(
                this,
                "قفل امن گوشی فعال نیست",
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

                        if (
                            auth.currentUser != null
                        ) {

                            Toast.makeText(
                                this@LoginActivity,
                                "خوش اومدی ❤️",
                                Toast.LENGTH_SHORT
                            ).show()

                            openHome()

                        } else {

                            Toast.makeText(
                                this@LoginActivity,
                                "نشست ورود پیدا نشد؛ با ایمیل و رمز وارد شو.",
                                Toast.LENGTH_LONG
                            ).show()
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
                            "تأیید انجام نشد، دوباره امتحان کن",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )


        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()

                .setTitle(
                    "ورود امن به رویارام"
                )

                .setSubtitle(
                    "اثر انگشت، چهره یا قفل گوشی"
                )

                .setDescription(
                    "برای ورود به فضای خصوصی رامین و رویا ❤️"
                )

                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )

                .build()


        biometricPrompt.authenticate(
            promptInfo
        )
    }
}


/*
 * صفحه ورود رویارام
 */
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
        modifier =
            Modifier.fillMaxSize()
    ) {

        Image(

            painter =
                painterResource(
                    id =
                        R.drawable.couple_main
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

                            colors =
                                listOf(

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

                text =
                    "رویارام",

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


                if (
                    biometricAvailable
                ) {

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
                                "🔐  ورود با اثر انگشت / چهره / رمز گوشی",

                            color =
                                Color(0xFFB23A62),

                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }


                if (
                    message.isNotBlank()
                ) {

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
