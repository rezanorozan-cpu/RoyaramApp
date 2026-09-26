package com.royaram.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

data class ChatMessage(
    val id: String,
    val text: String,
    val senderId: String,
    val time: String,
    val type: String = "text",
    val imageUrl: String? = null,
    val liked: Boolean = false
)

class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FirebaseChatScreen(
                onBack = {
                    finish()
                }
            )
        }
    }
}

@Composable
private fun FirebaseChatScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val storage = remember {
        FirebaseStorage.getInstance()
    }

    val currentUser = auth.currentUser

    val messages = remember {
        mutableStateListOf<ChatMessage>()
    }

    var messageText by remember {
        mutableStateOf("")
    }

    var isSending by remember {
        mutableStateOf(false)
    }

    var showStickers by remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()

    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            if (currentUser == null) {
                Toast.makeText(
                    context,
                    "حساب کاربری وارد نشده است.",
                    Toast.LENGTH_LONG
                ).show()

                return@rememberLauncherForActivityResult
            }

            if (isSending) {
                return@rememberLauncherForActivityResult
            }

            isSending = true

            Toast.makeText(
                context,
                "در حال ارسال عکس... 📷❤️",
                Toast.LENGTH_SHORT
            ).show()

            val fileName =
                "chat_${currentUser.uid}_${UUID.randomUUID()}.jpg"

            val imageRef =
                storage
                    .reference
                    .child("chatImages")
                    .child(fileName)

            imageRef
                .putFile(uri)
                .continueWithTask { task ->

                    if (!task.isSuccessful) {
                        throw task.exception
                            ?: Exception("آپلود عکس ناموفق بود")
                    }

                    imageRef.downloadUrl
                }
                .addOnSuccessListener { downloadUri ->

                    val messageData =
                        hashMapOf(
                            "text" to "",
                            "senderId" to currentUser.uid,
                            "type" to "image",
                            "imageUrl" to downloadUri.toString(),
                            "createdAt" to FieldValue.serverTimestamp()
                        )

                    firestore
                        .collection("chatRooms")
                        .document("ramin_roya")
                        .collection("messages")
                        .add(messageData)
                        .addOnSuccessListener {

                            isSending = false

                            Toast.makeText(
                                context,
                                "عکس ارسال شد ❤️",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { error ->

                            isSending = false

                            Toast.makeText(
                                context,
                                "ذخیره پیام عکس نشد:\n${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
                .addOnFailureListener { error ->

                    isSending = false

                    Toast.makeText(
                        context,
                        "آپلود عکس انجام نشد:\n${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

    DisposableEffect(currentUser?.uid) {

        if (currentUser == null) {

            Toast.makeText(
                context,
                "ابتدا وارد حساب کاربری شوید.",
                Toast.LENGTH_LONG
            ).show()

            onDispose { }

        } else {

            val listener =
                firestore
                    .collection("chatRooms")
                    .document("ramin_roya")
                    .collection("messages")
                    .orderBy(
                        "createdAt",
                        Query.Direction.ASCENDING
                    )
                    .addSnapshotListener { snapshot, error ->

                        if (error != null) {

                            Toast.makeText(
                                context,
                                "خطای دریافت پیام: ${error.message}",
                                Toast
