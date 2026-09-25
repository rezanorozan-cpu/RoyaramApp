package com.royaram.app

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

data class ChatMessage(
    val id: String,
    val text: String,
    val senderId: String,
    val time: String,
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

            if (uri != null) {

                Toast.makeText(
                    context,
                    "انتخاب عکس انجام شد ❤️ ارسال عکس را در مرحله بعد وصل می‌کنیم.",
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

            val listener = firestore
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
                            Toast.LENGTH_LONG
                        ).show()

                        return@addSnapshotListener
                    }

                    if (snapshot != null) {

                        val newMessages =
                            snapshot.documents.mapNotNull { document ->

                                val text =
                                    document.getString("text")
                                        ?: return@mapNotNull null

                                val senderId =
                                    document.getString("senderId")
                                        ?: return@mapNotNull null

                                val createdAt =
                                    document.getTimestamp("createdAt")

                                val time =
                                    if (createdAt != null) {

                                        SimpleDateFormat(
                                            "HH:mm",
                                            Locale.getDefault()
                                        ).format(
                                            createdAt.toDate()
                                        )

                                    } else {
                                        "..."
                                    }

                                ChatMessage(
                                    id = document.id,
                                    text = text,
                                    senderId = senderId,
                                    time = time
                                )
                            }

                        messages.clear()
                        messages.addAll(newMessages)
                    }
                }

            onDispose {
                listener.remove()
            }
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
                key = {
                    it.id
                }
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

                    messageText =
                        messageText + sticker

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

                showStickers =
                    !showStickers
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
                            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
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
            .background(
                Color.White.copy(alpha = 0.96f)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 10.dp
            )
            .navigationBarsPadding(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBack
        ) {

            Icon(
                Icons.Rounded.ArrowBack,
                contentDescription = "بازگشت",
                tint = Color(0xFFE85D75)
            )
        }

        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    Color(0xFFFFE1E9)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                Icons.Rounded.Favorite,
                contentDescription = null,
                tint = Color(0xFFE85D75),
                modifier = Modifier.size(27.dp)
            )
        }

        Spacer(
            Modifier.width(10.dp)
        )

        Column(
            Modifier.weight(1f)
        ) {

            Text(
                "رامین ❤️ رویا",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF402A30)
            )

            Text(
                "گفت‌وگوی دونفره ❤️",
                fontSize = 12.sp,
                color = Color(0xFF92777F)
            )
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    myUid: String?
) {

    val isMine =
        message.senderId == myUid

    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement =
            if (isMine) {
                Arrangement.End
            } else {
                Arrangement.Start
            }
    ) {

        Column(
            horizontalAlignment =
                if (isMine) {
                    Alignment.End
                } else {
                    Alignment.Start
                }
        ) {

            Box(
                modifier = Modifier
                    .background(
                        if (isMine) {
                            Color(0xFFE85D75)
                        } else {
                            Color.White
                        },

                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,

                            bottomStart =
                                if (isMine) {
                                    20.dp
                                } else {
                                    5.dp
                                },

                            bottomEnd =
                                if (isMine) {
                                    5.dp
                                } else {
                                    20.dp
                                }
                        )
                    )
                    .padding(
                        horizontal = 15.dp,
                        vertical = 10.dp
                    )
            ) {

                Text(
                    text = message.text,
                    fontSize = 15.sp,

                    color =
                        if (isMine) {
                            Color.White
                        } else {
                            Color(0xFF402A30)
                        }
                )
            }

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = message.time,
                    fontSize = 10.sp,
                    color = Color(0xFF92777F),

                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 3.dp
                    )
                )

                Icon(
                    imageVector =
                        if (message.liked) {
                            Icons.Rounded.Favorite
                        } else {
                            Icons.Rounded.FavoriteBorder
                        },

                    contentDescription =
                        "پسندیدن",

                    tint =
                        Color(0xFFE85D75),

                    modifier = Modifier
                        .size(15.dp)
                        .clickable { }
                )
            }
        }
    }
}

@Composable
private fun StickerPanel(
    onStickerSelected: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            )
    ) {

        Text(
            "استیکرهای ما ❤️",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF402A30)
        )

        Spacer(
            Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceAround
        ) {

            StickerItem("❤️", onStickerSelected)
            StickerItem("🥰", onStickerSelected)
            StickerItem("😘", onStickerSelected)
            StickerItem("😍", onStickerSelected)
            StickerItem("🥹", onStickerSelected)
            StickerItem("💋", onStickerSelected)
        }

        Spacer(
            Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceAround
        ) {

            StickerItem("💕", onStickerSelected)
            StickerItem("💖", onStickerSelected)
            StickerItem("🫶", onStickerSelected)
            StickerItem("🌹", onStickerSelected)
            StickerItem("💍", onStickerSelected)
            StickerItem("❤️‍🔥", onStickerSelected)
        }
    }
}

@Composable
private fun StickerItem(
    sticker: String,
    onStickerSelected: (String) -> Unit
) {

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                Color(0xFFFFF1F5)
            )
            .clickable {
                onStickerSelected(sticker)
            },

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            sticker,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun MessageInput(
    messageText: String,
    onMessageChange: (String) -> Unit,
    isSending: Boolean,
    onAttachmentClick: () -> Unit,
    onStickerClick: () -> Unit,
    onSend: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onAttachmentClick,
                modifier = Modifier.size(45.dp)
            ) {

                Icon(
                    Icons.Rounded.AttachFile,
                    contentDescription = "پیوست",
                    tint = Color(0xFFE85D75),
                    modifier = Modifier.size(25.dp)
                )
            }

            IconButton(
                onClick = onStickerClick,
                modifier = Modifier.size(45.dp)
            ) {

                Icon(
                    Icons.Rounded.EmojiEmotions,
                    contentDescription = "استیکر",
                    tint = Color(0xFFE85D75),
                    modifier = Modifier.size(25.dp)
                )
            }

            OutlinedTextField(
                value = messageText,

                onValueChange =
                    onMessageChange,

                modifier =
                    Modifier.weight(1f),

                placeholder = {
                    Text(
                        "پیامت رو بنویس ❤️"
                    )
                },

                shape =
                    RoundedCornerShape(24.dp),

                singleLine = true,

                enabled = !isSending
            )

            Spacer(
                Modifier.width(6.dp)
            )

            IconButton(
                onClick = onSend,

                enabled =
                    messageText.isNotBlank() &&
                    !isSending,

                modifier = Modifier
                    .size(52.dp)
                    .background(
                        if (
                            messageText.isNotBlank() &&
                            !isSending
                        ) {
                            Color(0xFFE85D75)
                        } else {
                            Color(0xFFE8C5CD)
                        },

                        CircleShape
                    )
            ) {

                Icon(
                    Icons.Rounded.Send,
                    contentDescription = "ارسال",
                    tint = Color.White,
                    modifier = Modifier.size(23.dp)
                )
            }
        }
    }
}
