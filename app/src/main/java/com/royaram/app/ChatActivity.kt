package com.royaram.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val type: String = "text",
    val filePath: String = "",
    val mimeType: String = "",
    val fileName: String = ""
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


/* =========================================================
   MAIN CHAT SCREEN
   ========================================================= */

@Composable
fun ChatScreen() {

    val context = LocalContext.current

    val auth =
        remember {
            FirebaseAuth.getInstance()
        }

    val db =
        remember {
            FirebaseFirestore.getInstance()
        }

    val currentUser =
        auth.currentUser


    var messageText by remember {
        mutableStateOf("")
    }

    var showStickers by remember {
        mutableStateOf(false)
    }

    var showAttachmentMenu by remember {
        mutableStateOf(false)
    }

    var isSending by remember {
        mutableStateOf(false)
    }


    val messages =
        remember {
            mutableStateListOf<ChatMessage>()
        }


    val listState =
        rememberLazyListState()


    /* =====================================================
       IMAGE PICKER
       ===================================================== */

    val imageLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }


            val user =
                auth.currentUser

            if (user == null) {

                Toast.makeText(
                    context,
                    "ابتدا وارد حساب کاربری شوید.",
                    Toast.LENGTH_LONG
                ).show()

                return@rememberLauncherForActivityResult
            }


            Toast.makeText(
                context,
                "در حال آپلود عکس... ⏳",
                Toast.LENGTH_SHORT
            ).show()


            val timestamp =
                System.currentTimeMillis()


            val fileName =
                uri.lastPathSegment
                    ?: "photo_$timestamp"


            /*
             * مسیر اختصاصی عکس‌های چت
             */

            val filePath =
                "chat/ramin_roya/photo_$timestamp"


            /*
             * نوع فایل
             */

            val mimeType =
                context.contentResolver
                    .getType(uri)
                    ?: "image/jpeg"


            SupabaseStorage.uploadFile(
                context = context,
                fileUri = uri,
                filePath = filePath
            ) { success, message ->


                Handler(
                    Looper.getMainLooper()
                ).post {


                    if (!success) {

                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_LONG
                        ).show()

                        return@post
                    }


                    /*
                     * آپلود موفق شد.
                     *
                     * حالا خود عکس را به عنوان
                     * یک پیام Firestore ثبت می‌کنیم.
                     */

                    val imageMessage =
                        hashMapOf(
                            "text" to "",
                            "senderId" to user.uid,
                            "createdAt" to
                                FieldValue.serverTimestamp(),

                            "type" to "image",

                            "filePath" to filePath,

                            "mimeType" to mimeType,

                            "fileName" to fileName
                        )


                    db.collection("chatRooms")
                        .document("ramin_roya")
                        .collection("messages")
                        .add(imageMessage)
                        .addOnSuccessListener {

                            Toast.makeText(
                                context,
                                "عکس داخل گپ قرار گرفت ❤️📸",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { error ->

                            Toast.makeText(
                                context,
                                "عکس آپلود شد ولی پیام ثبت نشد:\n${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
        }


    /* =====================================================
       AUDIO PICKER
       ===================================================== */

    val audioLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                Toast.makeText(
                    context,
                    "موسیقی انتخاب شد 🎵",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    /* =====================================================
       FILE PICKER
       ===================================================== */

    val fileLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                Toast.makeText(
                    context,
                    "فایل انتخاب شد 📁",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    /* =====================================================
       FIRESTORE REAL-TIME LISTENER
       ===================================================== */

    DisposableEffect(currentUser?.uid) {

        val listener =

            if (currentUser != null) {

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
                                "خطای دریافت پیام:\n${error.message}",
                                Toast.LENGTH_LONG
                            ).show()

                            return@addSnapshotListener
                        }


                        messages.clear()


                        snapshot
                            ?.documents
                            ?.forEach { document ->


                                messages.add(

                                    ChatMessage(

                                        id =
                                            document.id,

                                        text =
                                            document.getString(
                                                "text"
                                            )
                                                ?: "",

                                        senderId =
                                            document.getString(
                                                "senderId"
                                            )
                                                ?: "",

                                        type =
                                            document.getString(
                                                "type"
                                            )
                                                ?: "text",

                                        filePath =
                                            document.getString(
                                                "filePath"
                                            )
                                                ?: "",

                                        mimeType =
                                            document.getString(
                                                "mimeType"
                                            )
                                                ?: "",

                                        fileName =
                                            document.getString(
                                                "fileName"
                                            )
                                                ?: ""
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


    /* =====================================================
       AUTO SCROLL
       ===================================================== */

    LaunchedEffect(messages.size) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }


    /* =====================================================
       SEND TEXT MESSAGE
       ===================================================== */

    fun sendMessage() {

        val user =
            auth.currentUser


        if (user == null) {

            Toast.makeText(
                context,
                "ابتدا وارد حساب کاربری شوید.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val cleanText =
            messageText.trim()


        if (
            cleanText.isEmpty() ||
            isSending
        ) {
            return
        }


        isSending = true


        val message =
            hashMapOf(

                "text" to cleanText,

                "senderId" to user.uid,

                "createdAt" to
                    FieldValue.serverTimestamp(),

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
                    "ارسال نشد:\n${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    /* =====================================================
       UI
       ===================================================== */

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFE8EF),
                            Color(0xFFFFF7FA),
                            Color.White
                        )
                    )
                )
    ) {


        ChatHeader()


        LazyColumn(

            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(
                        horizontal = 10.dp
                    ),

            state = listState,

            verticalArrangement =
                Arrangement.spacedBy(8.dp),

            contentPadding =
                androidx.compose.foundation.layout
                    .PaddingValues(
                        top = 12.dp,
                        bottom = 12.dp
                    )
        ) {


            items(
                items = messages,
                key = { it.id }
            ) { message ->


                ChatBubble(

                    message = message,

                    isMine =
                        message.senderId ==
                            currentUser?.uid
                )
            }
        }


        /* =================================================
           ATTACHMENT MENU
           ================================================= */

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


        /* =================================================
           STICKERS
           ================================================= */

        if (showStickers) {

            StickerPanel(

                onStickerSelected = { sticker ->

                    messageText += sticker

                    showStickers = false
                }
            )
        }


        /* =================================================
           INPUT
           ================================================= */

        MessageInput(

            text = messageText,

            onTextChange = {
                messageText = it
            },

            onSend = {
                sendMessage()
            },

            onSticker = {

                showStickers =
                    !showStickers

                showAttachmentMenu = false
            },

            onAttachment = {

                showAttachmentMenu =
                    !showAttachmentMenu

                showStickers = false
            },

            isSending = isSending
        )
    }
}


/* =========================================================
   CHAT HEADER
   ========================================================= */

@Composable
fun ChatHeader() {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFF6F91),
                            Color(0xFFFF4F79)
                        )
                    )
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        Box(

            modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(
                            alpha = 0.25f
                        )
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Rounded.Favorite,

                contentDescription =
                    null,

                tint = Color.White,

                modifier =
                    Modifier.size(27.dp)
            )
        }


        Spacer(
            modifier =
                Modifier.width(12.dp)
        )


        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(

                text =
                    "رامین و رویا ❤️",

                color =
                    Color.White,

                fontSize =
                    19.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )


            Text(

                text =
                    "گپ دونفره‌ی ما",

                color =
                    Color.White.copy(
                        alpha = 0.85f
                    ),

                fontSize =
                    13.sp
            )
        }


        Box(

            modifier =
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFF8EFFB2)
                    )
        )
    }
}


/* =========================================================
   CHAT BUBBLE
   ========================================================= */

@Composable
fun ChatBubble(
    message: ChatMessage,
    isMine: Boolean
) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            if (isMine) {
                Arrangement.End
            } else {
                Arrangement.Start
            }
    ) {


        when (message.type) {

            "image" -> {

                ImageMessageBubble(
                    message = message,
                    isMine = isMine
                )
            }


            else -> {

                TextMessageBubble(
                    message = message,
                    isMine = isMine
                )
            }
        }
    }
}


/* =========================================================
   TEXT MESSAGE
   ========================================================= */

@Composable
fun TextMessageBubble(
    message: ChatMessage,
    isMine: Boolean
) {

    Column(

        modifier =
            Modifier
                .widthInSafe(
                    min = 0.dp,
                    max = 310.dp
                )
                .clip(
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
                .background(
                    if (isMine) {
                        Color(0xFFFF5D83)
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

            text =
                message.text,

            color =
                if (isMine) {
                    Color.White
                } else {
                    Color(0xFF333333)
                },

            fontSize =
                15.sp
        )
    }
}


/* =========================================================
   IMAGE MESSAGE
   ========================================================= */

@Composable
fun ImageMessageBubble(
    message: ChatMessage,
    isMine: Boolean
) {

    Column(

        modifier =
            Modifier
                .widthInSafe(
                    min = 0.dp,
                    max = 280.dp
                )
                .clip(
                    RoundedCornerShape(
                        18.dp
                    )
                )
                .background(
                    if (isMine) {
                        Color(0xFFFF5D83)
                    } else {
                        Color.White
                    }
                )
                .padding(5.dp)
    ) {


        if (
            message.filePath.isNotBlank()
        ) {

            SecureChatImage(
                filePath =
                    message.filePath
            )

        } else {

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(
                            RoundedCornerShape(
                                15.dp
                            )
                        )
                        .background(
                            Color(0xFFF3E7EB)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        "عکس پیدا نشد 😔",

                    color =
                        Color.Gray,

                    fontSize =
                        13.sp
                )
            }
        }
    }
}


/* =========================================================
   SECURE IMAGE
   ========================================================= */

@Composable
fun SecureChatImage(
    filePath: String
) {

    val context =
        LocalContext.current


    var signedUrl by remember(
        filePath
    ) {
        mutableStateOf<String?>(
            null
        )
    }


    var loading by remember(
        filePath
    ) {
        mutableStateOf(true)
    }


    var failed by remember(
        filePath
    ) {
        mutableStateOf(false)
    }


    LaunchedEffect(filePath) {

        loading = true
        failed = false
        signedUrl = null


        SupabaseStorage.getSignedUrl(
            context = context,
            filePath = filePath
        ) { success, result ->


            Handler(
                Looper.getMainLooper()
            ).post {

                loading = false


                if (success) {

                    signedUrl = result

                } else {

                    failed = true
                }
            }
        }
    }


    when {

        loading -> {

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(
                            RoundedCornerShape(
                                15.dp
                            )
                        )
                        .background(
                            Color(0xFFF4E8EC)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "در حال دریافت عکس... 📸",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }


        failed -> {

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(
                            RoundedCornerShape(
                                15.dp
                            )
                        )
                        .background(
                            Color(0xFFF4E8EC)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "نمایش عکس انجام نشد 😔",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }


        signedUrl != null -> {

            AsyncImage(

                model =
                    signedUrl,

                contentDescription =
                    "عکس چت",

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(
                            RoundedCornerShape(
                                15.dp
                            )
                        ),

                contentScale =
                    ContentScale.Crop
            )
        }
    }
}


/* =========================================================
   ATTACHMENT MENU
   ========================================================= */

@Composable
fun AttachmentMenu(
    onClose: () -> Unit,
    onImage: () -> Unit,
    onMusic: () -> Unit,
    onVoice: () -> Unit,
    onFile: () -> Unit,
    onLocation: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                )
                .clip(
                    RoundedCornerShape(22.dp)
                )
                .background(Color.White)
                .padding(10.dp)
    ) {


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(

                text =
                    "ارسال برای عشقم ❤️",

                modifier =
                    Modifier.weight(1f),

                fontWeight =
                    FontWeight.Bold,

                fontSize =
                    15.sp
            )


            IconButton(
                onClick = onClose
            ) {

                Icon(
                    imageVector =
                        Icons.Rounded.Close,

                    contentDescription =
                        "بستن"
                )
            }
        }


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {


            AttachmentItem(
                icon =
                    Icons.Rounded.Image,

                title =
                    "عکس",

                onClick =
                    onImage
            )


            AttachmentItem(
                icon =
                    Icons.Rounded.MusicNote,

                title =
                    "موسیقی",

                onClick =
                    onMusic
            )


            AttachmentItem(
                icon =
                    Icons.Rounded.Mic,

                title =
                    "صدا",

                onClick =
                    onVoice
            )


            AttachmentItem(
                icon =
                    Icons.Rounded.Folder,

                title =
                    "فایل",

                onClick =
                    onFile
            )


            AttachmentItem(
                icon =
                    Icons.Rounded.LocationOn,

                title =
                    "موقعیت",

                onClick =
                    onLocation
            )
        }
    }
}


/* =========================================================
   ATTACHMENT ITEM
   ========================================================= */

@Composable
fun AttachmentItem(
    icon:
        androidx.compose.ui.graphics.vector.ImageVector,

    title: String,

    onClick: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .clip(
                    RoundedCornerShape(15.dp)
                )
                .clickable(
                    onClick = onClick
                )
                .padding(
                    7.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        Box(

            modifier =
                Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFFFFE8EF)
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    title,

                tint =
                    Color(0xFFFF4F79)
            )
        }


        Spacer(
            modifier =
                Modifier.height(4.dp)
        )


        Text(

            text =
                title,

            fontSize =
                11.sp,

            color =
                Color.DarkGray
        )
    }
}


/* =========================================================
   STICKER PANEL
   ========================================================= */

@Composable
fun StickerPanel(
    onStickerSelected:
        (String) -> Unit
) {

    val stickers =
        listOf(
            "❤️",
            "🥰",
            "😘",
            "😍",
            "💕",
            "💋",
            "🌹",
            "🫶",
            "💗",
            "✨",
            "🤍",
            "💞"
        )


    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Color.White
                )
                .padding(
                    10.dp
                ),

        horizontalArrangement =
            Arrangement.SpaceEvenly
    ) {

        stickers
            .take(6)
            .forEach { sticker ->

                Text(

                    text =
                        sticker,

                    fontSize =
                        26.sp,

                    modifier =
                        Modifier.clickable {

                            onStickerSelected(
                                sticker
                            )
                        }
                )
            }
    }


    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Color.White
                )
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 10.dp
                ),

        horizontalArrangement =
            Arrangement.SpaceEvenly
    ) {

        stickers
            .drop(6)
            .forEach { sticker ->

                Text(

                    text =
                        sticker,

                    fontSize =
                        26.sp,

                    modifier =
                        Modifier.clickable {

                            onStickerSelected(
                                sticker
                            )
                        }
                )
            }
    }
}


/* =========================================================
   MESSAGE INPUT
   ========================================================= */

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

        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Color.White
                )
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),

        verticalAlignment =
            Alignment.Bottom
    ) {


        IconButton(
            onClick = onAttachment
        ) {

            Icon(

                imageVector =
                    Icons.Rounded.AttachFile,

                contentDescription =
                    "پیوست",

                tint =
                    Color(0xFFFF4F79)
            )
        }


        IconButton(
            onClick = onSticker
        ) {

            Icon(

                imageVector =
                    Icons.Rounded.EmojiEmotions,

                contentDescription =
                    "استیکر",

                tint =
                    Color(0xFFFF4F79)
            )
        }


        TextField(

            value =
                text,

            onValueChange =
                onTextChange,

            modifier =
                Modifier.weight(1f),

            placeholder = {

                Text(
                    text =
                        "حرفی برای عشقم بنویس..."
                )
            },

            maxLines =
                4,

            shape =
                RoundedCornerShape(
                    22.dp
                ),

            colors =
                TextFieldDefaults.colors(

                    focusedContainerColor =
                        Color(0xFFFFF3F6),

                    unfocusedContainerColor =
                        Color(0xFFFFF3F6),

                    focusedIndicatorColor =
                        Color.Transparent,

                    unfocusedIndicatorColor =
                        Color.Transparent
                )
        )


        Spacer(
            modifier =
                Modifier.width(4.dp)
        )


        IconButton(

            onClick =
                onSend,

            enabled =
                text.trim().isNotEmpty() &&
                    !isSending
        ) {

            Icon(

                imageVector =
                    Icons.Rounded.Send,

                contentDescription =
                    "ارسال",

                tint =
                    if (
                        text.trim().isNotEmpty() &&
                        !isSending
                    ) {
                        Color(0xFFFF4F79)
                    } else {
                        Color.LightGray
                    }
            )
        }
    }
}


/* =========================================================
   SAFE WIDTH HELPER
   ========================================================= */

private fun Modifier.widthInSafe(
    min: androidx.compose.ui.unit.Dp,
    max: androidx.compose.ui.unit.Dp
): Modifier {

    return this.then(
        Modifier.width(
            max
        )
    )
}
