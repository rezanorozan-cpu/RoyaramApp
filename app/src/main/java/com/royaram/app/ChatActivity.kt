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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val type: String = "text"
)

class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ChatScreen()
                }
            }
        }
    }
}

@Composable
fun ChatScreen() {

    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    val currentUser = auth.currentUser

    var messageText by remember { mutableStateOf("") }
    var showStickers by remember { mutableStateOf(false) }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf<ChatMessage>()
    }

    val listState = rememberLazyListState()

    /*
     * انتخاب عکس
     */
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {
            Toast.makeText(
                context,
                "عکس انتخاب شد ❤️",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /*
     * انتخاب موسیقی
     */
    val audioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {
            Toast.makeText(
                context,
                "موسیقی انتخاب شد 🎵",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /*
     * انتخاب فایل
     */
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {
            Toast.makeText(
                context,
                "فایل انتخاب شد 📁",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /*
     * دریافت لحظه‌ای پیام‌ها
     */
    DisposableEffect(currentUser?.uid) {

        val listener = if (currentUser != null) {

            db.collection("chatRooms")
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

                    messages.clear()

                    snapshot?.documents?.forEach { document ->

                        messages.add(
                            ChatMessage(
                                id = document.id,
                                text = document.getString("text") ?: "",
                                senderId = document.getString("senderId") ?: "",
                                type = document.getString("type") ?: "text"
                            )
                        )
                    }
                }

        } else {
            null
        }

        onDispose {
            listener?.remove()
        }
    }

    /*
     * اسکرول خودکار
     */
    LaunchedEffect(messages.size) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    /*
     * ارسال پیام
     */
    fun sendMessage() {

        val user = auth.currentUser

        if (user == null) {

            Toast.makeText(
                context,
                "ابتدا وارد حساب کاربری شوید.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val cleanText = messageText.trim()

        if (cleanText.isEmpty() || isSending) {
            return
        }

        isSending = true

        val message = hashMapOf(
            "text" to cleanText,
            "senderId" to user.uid,
            "createdAt" to FieldValue.serverTimestamp(),
            "type" to "text"
        )

        db.collection("chatRooms")
            .document("ramin_roya")
            .collection("messages")
            .add(message)
            .addOnSuccessListener {

                messageText = ""
                isSending = false
            }
            .addOnFailureListener { error ->

                isSending = false

                Toast.makeText(
                    context,
                    "ارسال نشد: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF1F6),
                        Color.White
                    )
                )
            )
    ) {

        ChatHeader()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(
                items = messages,
                key = { it.id }
            ) { message ->

                ChatBubble(
                    message = message,
                    isMine = message.senderId == currentUser?.uid
                )
            }
        }

        /*
         * منوی حرفه‌ای گیره
         */
        if (showAttachmentMenu) {

            AttachmentMenu(
                onClose = {
                    showAttachmentMenu = false
                },
                onImage = {

                    showAttachmentMenu = false

                    imageLauncher.launch(
                        arrayOf("image/*")
                    )
                },
                onMusic = {

                    showAttachmentMenu = false

                    audioLauncher.launch(
                        arrayOf("audio/*")
                    )
                },
                onVoice = {

                    showAttachmentMenu = false

                    Toast.makeText(
                        context,
                        "ضبط صدا را در مرحله بعد اضافه می‌کنیم 🎙️",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onFile = {

                    showAttachmentMenu = false

                    fileLauncher.launch(
                        arrayOf(
                            "application/pdf",
                            "text/*",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        )
                    )
                },
                onLocation = {

                    showAttachmentMenu = false

                    Toast.makeText(
                        context,
                        "ارسال موقعیت را در مرحله بعد وصل می‌کنیم 📍",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        /*
         * استیکرها
         */
        if (showStickers) {

            StickerPanel(
                onStickerSelected = { sticker ->

                    messageText += sticker
                    showStickers = false
                }
            )
        }

        MessageInput(
            text = messageText,
            onTextChange = {
                messageText = it
            },
            onSend = {
                sendMessage()
            },
            onSticker = {
                showStickers = !showStickers
                showAttachmentMenu = false
            },
            onAttachment = {
                showAttachmentMenu = !showAttachmentMenu
                showStickers = false
            },
            isSending = isSending
        )
    }
}

/*
 * هدر
 */
@Composable
fun ChatHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 18.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD7E5)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(27.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column {

            Text(
                text = "رامین و رویا ❤️",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Text(
                text = "گفتگوی خصوصی ما",
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

/*
 * حباب پیام
 */
@Composable
fun ChatBubble(
    message: ChatMessage,
    isMine: Boolean
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isMine) 18.dp else 4.dp,
                        bottomEnd = if (isMine) 4.dp else 18.dp
                    )
                )
                .background(
                    if (isMine) {
                        Color(0xFFFFD1E0)
                    } else {
                        Color.White
                    }
                )
                .padding(
                    horizontal = 15.dp,
                    vertical = 11.dp
                )
        ) {

            Text(
                text = message.text,
                fontSize = 15.sp,
                color = Color(0xFF333333),
                textAlign = if (isMine) {
                    TextAlign.End
                } else {
                    TextAlign.Start
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/*
 * منوی حرفه‌ای گیره
 */
@Composable
fun AttachmentMenu(
    onClose: () -> Unit,
    onImage: () -> Unit,
    onMusic: () -> Unit,
    onVoice: () -> Unit,
    onFile: () -> Unit,
    onLocation: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "افزودن به گفت‌وگو ❤️",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )

                    Text(
                        text = "چیزی برای رویا بفرست",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }

                IconButton(
                    onClick = onClose
                ) {

                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "بستن",
                        tint = Color(0xFF888888)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                AttachmentItem(
                    icon = Icons.Rounded.Image,
                    title = "عکس",
                    subtitle = "گالری",
                    onClick = onImage
                )

                AttachmentItem(
                    icon = Icons.Rounded.MusicNote,
                    title = "موسیقی",
                    subtitle = "آهنگ",
                    onClick = onMusic
                )

                AttachmentItem(
                    icon = Icons.Rounded.Mic,
                    title = "صدا",
                    subtitle = "ویس",
                    onClick = onVoice
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                AttachmentItem(
                    icon = Icons.Rounded.Folder,
                    title = "فایل",
                    subtitle = "PDF و...",
                    onClick = onFile
                )

                AttachmentItem(
                    icon = Icons.Rounded.LocationOn,
                    title = "موقعیت",
                    subtitle = "لوکیشن",
                    onClick = onLocation
                )

                Spacer(
                    modifier = Modifier.width(82.dp)
                )
            }
        }
    }
}

/*
 * آیتم منوی گیره
 */
@Composable
fun AttachmentItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .width(82.dp)
            .clickable {
                onClick()
            }
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE3EC)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )

        Text(
            text = subtitle,
            fontSize = 10.sp,
            color = Color(0xFF999999)
        )
    }
}

/*
 * استیکرها
 */
@Composable
fun StickerPanel(
    onStickerSelected: (String) -> Unit
) {

    val stickers = listOf(
        "❤️",
        "😍",
        "🥰",
        "😘",
        "💕",
        "💋",
        "🌹",
        "🫶",
        "💖",
        "💗",
        "💞",
        "✨"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = "استیکرهای عاشقانه ❤️",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                stickers.take(6).forEach { sticker ->

                    Text(
                        text = sticker,
                        fontSize = 28.sp,
                        modifier = Modifier
                            .clickable {
                                onStickerSelected(sticker)
                            }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                stickers.drop(6).forEach { sticker ->

                    Text(
                        text = sticker,
                        fontSize = 28.sp,
                        modifier = Modifier
                            .clickable {
                                onStickerSelected(sticker)
                            }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

/*
 * نوار پیام
 */
@Composable
fun MessageInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onSticker: () -> Unit,
    onAttachment: () -> Unit,
    isSending: Boolean
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.Bottom
    ) {

        IconButton(
            onClick = onAttachment
        ) {

            Icon(
                imageVector = Icons.Rounded.AttachFile,
                contentDescription = "پیوست",
                tint = Color(0xFFE91E63)
            )
        }

        IconButton(
            onClick = onSticker
        ) {

            Icon(
                imageVector = Icons.Rounded.EmojiEmotions,
                contentDescription = "استیکر",
                tint = Color(0xFFE91E63)
            )
        }

        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "یه چیزی برای عشقت بنویس ❤️",
                    fontSize = 13.sp
                )
            },
            singleLine = false,
            maxLines = 4,
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFF4F7),
                unfocusedContainerColor = Color(0xFFFFF4F7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        IconButton(
            onClick = onSend,
            enabled = text.trim().isNotEmpty() && !isSending,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    if (text.trim().isNotEmpty() && !isSending) {
                        Color(0xFFE91E63)
                    } else {
                        Color(0xFFFFB6CB)
                    }
                )
        ) {

            Icon(
                imageVector = Icons.Rounded.Send,
                contentDescription = "ارسال",
                tint = Color.White
            )
        }
    }
}
