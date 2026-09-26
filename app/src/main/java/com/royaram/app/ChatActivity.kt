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
    val imageUrl: String? = null
)

class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FirebaseChatScreen(
                onBack = { finish() }
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

            if (uri == null) return@rememberLauncherForActivityResult

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
                storage.reference
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

        var listenerRegistration:
            com.google.firebase.firestore.ListenerRegistration? = null

        if (currentUser != null) {

            listenerRegistration =
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
                                "خطای دریافت پیام:\n${error.message}",
                                Toast.LENGTH_LONG
                            ).show()

                            return@addSnapshotListener
                        }

                        val newMessages =
                            snapshot?.documents?.mapNotNull { document ->

                                val senderId =
                                    document.getString("senderId")
                                        ?: return@mapNotNull null

                                val type =
                                    document.getString("type")
                                        ?: "text"

                                val text =
                                    document.getString("text")
                                        ?: ""

                                val imageUrl =
                                    document.getString("imageUrl")

                                val timestamp =
                                    document.getTimestamp("createdAt")

                                val time =
                                    if (timestamp != null) {
                                        SimpleDateFormat(
                                            "HH:mm",
                                            Locale.getDefault()
                                        ).format(
                                            timestamp.toDate()
                                        )
                                    } else {
                                        "..."
                                    }

                                ChatMessage(
                                    id = document.id,
                                    text = text,
                                    senderId = senderId,
                                    time = time,
                                    type = type,
                                    imageUrl = imageUrl
                                )
                            } ?: emptyList()

                        messages.clear()
                        messages.addAll(newMessages)
                    }
        }

        onDispose {
            listenerRegistration?.remove()
        }
    }

    LaunchedEffect(messages.size) {

        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF1F5),
                        Color(0xFFFFF8FA),
                        Color.White
                    )
                )
            )
            .imePadding()
    ) {

        ChatHeader(
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),

            state = listState,

            verticalArrangement =
                Arrangement.spacedBy(7.dp),

            contentPadding =
                PaddingValues(
                    top = 14.dp,
                    bottom = 14.dp
                )
        ) {

            items(
                items = messages,
                key = { it.id }
            ) { message ->

                ChatBubble(
                    message = message,
                    myUid = currentUser?.uid
                )
            }
        }

        if (showStickers) {

            StickerPanel(
                onStickerSelected = { sticker ->

                    messageText += sticker
                    showStickers = false
                }
            )
        }

        MessageInput(
            messageText = messageText,

            onMessageChange = {
                messageText = it
            },

            isSending = isSending,

            onAttachmentClick = {
                imagePicker.launch(
                    arrayOf("image/*")
                )
            },

            onStickerClick = {
                showStickers = !showStickers
            },

            onSend = {

                val text =
                    messageText.trim()

                if (text.isEmpty()) {

                    Toast.makeText(
                        context,
                        "اول یک پیام بنویس ❤️",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (currentUser == null) {

                    Toast.makeText(
                        context,
                        "حساب کاربری وارد نشده است.",
                        Toast.LENGTH_LONG
                    ).show()

                } else if (!isSending) {

                    isSending = true

                    val messageData =
                        hashMapOf(
                            "text" to text,
                            "senderId" to currentUser.uid,
                            "type" to "text",
                            "createdAt" to FieldValue.serverTimestamp()
                        )

                    firestore
                        .collection("chatRooms")
                        .document("ramin_roya")
                        .collection("messages")
                        .add(messageData)
                        .addOnSuccessListener {

                            messageText = ""
                            isSending = false
                            showStickers = false
                        }
                        .addOnFailureListener { error ->

                            isSending = false

                            Toast.makeText(
                                context,
                                "ارسال نشد:\n${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
        )
    }
}

@Composable
private fun ChatHeader(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 10.dp,
                vertical = 10.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBack
        ) {
           
