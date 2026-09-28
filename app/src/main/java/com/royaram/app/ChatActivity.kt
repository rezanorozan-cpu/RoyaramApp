package com.royaram.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoyaramChatScreen()
        }
    }
}

private const val CHAT_ROOM = "ramin_roya"

private enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    FILE
}

private enum class LocalMessageStatus {
    SENDING,
    SENT,
    FAILED
}

private data class ChatMessage(
    val id: String,
    val senderId: String,
    val type: String = "TEXT",
    val text: String = "",
    val filePath: String = "",
    val fileName: String = "",
    val mimeType: String = "",
    val fileSize: Long = 0L,
    val createdAt: Timestamp? = null,
    val localUri: String = "",
    val localStatus: LocalMessageStatus? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoyaramChatScreen() {

    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

    val currentUserId = auth.currentUser?.uid ?: ""

    val messages = remember {
        mutableStateListOf<ChatMessage>()
    }

    var messageText by remember {
        mutableStateOf("")
    }

    var showAttachmentMenu by remember {
        mutableStateOf(false)
    }

    var selectedFileUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var isSending by remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()

    val filePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }

            selectedFileUri = uri

            uploadFileToChat(
                context = context,
                firestore = firestore,
                auth = auth,
                uri = uri,
                messages = messages
            )
        }

    LaunchedEffect(Unit) {

        if (currentUserId.isBlank()) {
            Toast.makeText(
                context,
                "لطفاً ابتدا وارد حساب شوید",
                Toast.LENGTH_LONG
            ).show()

            return@LaunchedEffect
        }

        firestore
            .collection("chatRooms")
            .document(CHAT_ROOM)
            .collection("messages")
            .orderBy(
                "createdAt",
                Query.Direction.ASCENDING
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Toast.makeText(
                        context,
                        "خطا در دریافت پیام‌ها",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    return@addSnapshotListener
                }

                val remoteMessages =
                    snapshot.documents.mapNotNull { document ->

                        val senderId =
                            document.getString("senderId")
                                ?: return@mapNotNull null

                        ChatMessage(
                            id = document.id,
                            senderId = senderId,
                            type =
                                document.getString("type")
                                    ?: "TEXT",
                            text =
                                document.getString("text")
                                    ?: "",
                            filePath =
                                document.getString("filePath")
                                    ?: "",
                            fileName =
                                document.getString("fileName")
                                    ?: "",
                            mimeType =
                                document.getString("mimeType")
                                    ?: "",
                            fileSize =
                                document.getLong("fileSize")
                                    ?: 0L,
                            createdAt =
                                document.getTimestamp("createdAt"),
                            localStatus =
                                LocalMessageStatus.SENT
                        )
                    }

                messages.removeAll {
                    it.localStatus != null &&
                        it.localStatus != LocalMessageStatus.SENDING &&
                        it.localStatus != LocalMessageStatus.FAILED
                }

                messages.addAll(remoteMessages)

                val unique =
                    messages
                        .distinctBy { it.id }
                        .sortedBy {
                            it.createdAt?.seconds ?: 0L
                        }

                messages.clear()
                messages.addAll(unique)
            }
    }

    LaunchedEffect(messages.size) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    Scaffold(

        topBar = {

            SmallTopAppBar(

                title = {

                    Column {

                        Text(
                            text = "رامین ❤️ رویا",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "گفت‌وگوی دونفره",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = {}
                    ) {

                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "بیشتر"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults
                        .smallTopAppBarColors(
                            containerColor =
                                Color(0xFFFFF4F7)
                        )
            )
        },

        bottomBar = {

            ChatInputBar(

                text = messageText,

                onTextChange = {
                    messageText = it
                },

                onAttachClick = {
                    showAttachmentMenu = true
                },

                onSendClick = {

                    if (
                        messageText.trim().isNotEmpty()
                    ) {

                        sendTextMessage(
                            firestore = firestore,
                            auth = auth,
                            text = messageText.trim(),
                            messages = messages
                        )

                        messageText = ""
                    }
                }
            )
        }

    ) { paddingValues ->

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFF7F9),
                                Color(0xFFFFEEF3),
                                Color.White
                            )
                        )
                    )
        ) {

            if (messages.isEmpty()) {

                EmptyChat()

            } else {

                LazyColumn(

                    state = listState,

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = 10.dp
                            ),

                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    item {

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )
                    }

                    items(
                        items = messages,
                        key = { it.id }
                    ) { message ->

                        ChatMessageBubble(
                            message = message,
                            currentUserId = currentUserId,
                            context = context
                        )
                    }

                    item {

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAttachmentMenu) {

        AttachmentDialog(

            onDismiss = {
                showAttachmentMenu = false
            },

            onSelect = {

                showAttachmentMenu = false

                filePicker.launch(
                    arrayOf("*/*")
                )
            }
        )
    }
}

@Composable
private fun EmptyChat() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "❤️",
                fontSize = 54.sp
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text = "اینجا جای حرف‌های من و توئه",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text = "اولین پیام رو بفرست 🌹",
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onAttachClick: () -> Unit,
    onSendClick: () -> Unit
) {

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 8.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onAttachClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.AttachFile,
                    contentDescription = "پیوست"
                )
            }

            androidx.compose.material3.OutlinedTextField(

                value = text,

                onValueChange = onTextChange,

                modifier =
                    Modifier
                        .weight(1f),

                placeholder = {
                    Text("پیامت رو بنویس...")
                },

                maxLines = 4,

                shape =
                    RoundedCornerShape(24.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )

            IconButton(
                onClick = onSendClick
            ) {

                Box(
                    modifier =
                        Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Color(0xFFE91E63)
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Send,
                        contentDescription = "ارسال",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    currentUserId: String,
    context: Context
) {

    val isMine =
        message.senderId == currentUserId

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (isMine)
                Arrangement.End
            else
                Arrangement.Start
    ) {

        Column(
            horizontalAlignment =
                if (isMine)
                    Alignment.End
                else
                    Alignment.Start
        ) {

            Surface(
                shape =
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart =
                            if (isMine) 18.dp else 4.dp,
                        bottomEnd =
                            if (isMine) 4.dp else 18.dp
                    ),
                color =
                    if (isMine)
                        Color(0xFFFFD9E5)
                    else
                        Color.White,
                shadowElevation = 2.dp
            ) {

                Column(
                    modifier =
                        Modifier.padding(10.dp)
                ) {

                    when (message.type.uppercase()) {

                        "TEXT" -> {

                            Text(
                                text = message.text,
                                fontSize = 16.sp
                            )
                        }

                        "IMAGE" -> {

                            ImageMessageContent(
                                message = message,
                                context = context
                            )
                        }

                        "VIDEO" -> {

                            FileMessageContent(
                                message = message,
                                icon =
                                    Icons.Default.Videocam
                            )
                        }

                        "AUDIO" -> {

                            FileMessageContent(
                                message = message,
                                icon =
                                    Icons.Default.Mic
                            )
                        }

                        else -> {

                            FileMessageContent(
                                message = message,
                                icon =
                                    Icons.Default.InsertDriveFile
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                formatMessageTime(
                                    message.createdAt
                                ),
                            fontSize = 10.sp,
                            color = Color.Gray
                        )

                        if (isMine) {

                            Spacer(
                                modifier =
                                    Modifier.width(4.dp)
                            )

                            when (
                                message.localStatus
                            ) {

                                LocalMessageStatus.SENDING -> {

                                    CircularProgressIndicator(
                                        modifier =
                                            Modifier.size(11.dp),
                                        strokeWidth = 1.5.dp
                                    )
                                }

                                LocalMessageStatus.FAILED -> {

                                    Icon(
                                        imageVector =
                                            Icons.Default.ErrorOutline,
                                        contentDescription =
                                            "خطا",
                                        modifier =
                                            Modifier.size(14.dp),
                                        tint =
                                            Color.Red
                                    )
                                }

                                else -> {

                                    Text(
                                        text = "✓✓",
                                        fontSize = 10.sp,
                                        color =
                                            Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (
                message.localStatus ==
                LocalMessageStatus.FAILED
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "ارسال نشد",
                        color = Color.Red,
                        fontSize = 11.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.width(4.dp)
                    )

                    Text(
                        text = "تلاش دوباره",
                        color =
                            Color(0xFFE91E63),
                        fontSize = 11.sp,
                        fontWeight =
                            FontWeight.Bold,
                        modifier =
                            Modifier.clickable {
                                retryMessage(
                                    message,
                                    context
                                )
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageMessageContent(
    message: ChatMessage,
    context: Context
) {

    if (message.localUri.isNotBlank()) {

        AsyncImage(
            model = message.localUri.toUri(),
            contentDescription = message.fileName,
            modifier =
                Modifier
                    .size(
                        width = 230.dp,
                        height = 230.dp
                    )
                    .clip(
                        RoundedCornerShape(14.dp)
                    ),
            contentScale = ContentScale.Crop
        )

    } else {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    Icons.Default.Image,
                contentDescription = null,
                modifier =
                    Modifier.size(60.dp),
                tint =
                    Color(0xFFE91E63)
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    message.fileName.ifBlank {
                        "عکس"
                    },
                maxLines = 1,
                overflow =
                    TextOverflow.Ellipsis,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun FileMessageContent(
    message: ChatMessage,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {

    Row(
        modifier =
            Modifier.width(240.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Color(0xFFFFE4EC)
                    ),
            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint =
                    Color(0xFFE91E63)
            )
        }

        Spacer(
            modifier =
                Modifier.width(10.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    message.fileName.ifBlank {
                        "فایل"
                    },
                fontWeight =
                    FontWeight.Bold,
                maxLines = 1,
                overflow =
                    TextOverflow.Ellipsis
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    formatFileSize(
                        message.fileSize
                    ),
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        IconButton(
            onClick = {}
        ) {

            Icon(
                imageVector =
                    Icons.Default.Download,
                contentDescription =
                    "دانلود"
            )
        }
    }
}

@Composable
private fun AttachmentDialog(
    onDismiss: () -> Unit,
    onSelect: () -> Unit
) {

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "ارسال فایل",
                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            Column {

                AttachmentItem(
                    icon = Icons.Default.Image,
                    title = "عکس یا تصویر",
                    onClick = onSelect
                )

                AttachmentItem(
                    icon = Icons.Default.Videocam,
                    title = "ویدیو",
                    onClick = onSelect
                )

                AttachmentItem(
                    icon = Icons.Default.Mic,
                    title = "فایل صوتی",
                    onClick = onSelect
                )

                AttachmentItem(
                    icon = Icons.Default.Description,
                    title = "سند و فایل",
                    onClick = onSelect
                )
            }
        },

        confirmButton = {},

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("بستن")
            }
        }
    )
}

@Composable
private fun AttachmentItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                )
                .padding(vertical = 12.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint =
                Color(0xFFE91E63)
        )

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Text(
            text = title,
            fontSize = 16.sp
        )
    }
}

private fun sendTextMessage(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    text: String,
    messages: MutableList<ChatMessage>
) {

    val user =
        auth.currentUser
            ?: return

    val messageId =
        UUID.randomUUID().toString()

    val localMessage =
        ChatMessage(
            id = messageId,
            senderId = user.uid,
            type = "TEXT",
            text = text,
            createdAt = Timestamp.now(),
            localStatus =
                LocalMessageStatus.SENDING
        )

    messages.add(localMessage)

    val data =
        hashMapOf<String, Any>(
            "senderId" to user.uid,
            "type" to "TEXT",
            "text" to text,
            "createdAt" to Timestamp.now()
        )

    firestore
        .collection("chatRooms")
        .document(CHAT_ROOM)
        .collection("messages")
        .document(messageId)
        .set(data)
        .addOnSuccessListener {

            val index =
                messages.indexOfFirst {
                    it.id == messageId
                }

            if (index >= 0) {

                messages[index] =
                    localMessage.copy(
                        localStatus =
                            LocalMessageStatus.SENT
                    )
            }
        }
        .addOnFailureListener {

            val index =
                messages.indexOfFirst {
                    it.id == messageId
                }

            if (index >= 0) {

                messages[index] =
                    localMessage.copy(
                        localStatus =
                            LocalMessageStatus.FAILED
                    )
            }
        }
}

private fun uploadFileToChat(
    context: Context,
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    uri: Uri,
    messages: MutableList<ChatMessage>
) {

    val user =
        auth.currentUser
            ?: return

    val fileName =
        getFileName(
            context,
            uri
        )

    val mimeType =
        context.contentResolver.getType(uri)
            ?: "application/octet-stream"

    val fileSize =
        getFileSize(
            context,
            uri
        )

    val messageId =
        UUID.randomUUID().toString()

    val type =
        when {

            mimeType.startsWith("image/") ->
                "IMAGE"

            mimeType.startsWith("video/") ->
                "VIDEO"

            mimeType.startsWith("audio/") ->
                "AUDIO"

            else ->
                "FILE"
        }

    val filePath =
        "chat/ramin_roya/$messageId-$fileName"

    val localMessage =
        ChatMessage(
            id = messageId,
            senderId = user.uid,
            type = type,
            filePath = filePath,
            fileName = fileName,
            mimeType = mimeType,
            fileSize = fileSize,
            localUri = uri.toString(),
            createdAt = Timestamp.now(),
            localStatus =
                LocalMessageStatus.SENDING
        )

    messages.add(localMessage)

    Toast.makeText(
        context,
        "در حال ارسال فایل... ⏳",
        Toast.LENGTH_SHORT
    ).show()

    SupabaseStorage.uploadFile(
        context = context,
        fileUri = uri,
        filePath = filePath
    ) { success, result ->

        android.os.Handler(
            android.os.Looper.getMainLooper()
        ).post {

            if (success) {

                val data =
                    hashMapOf<String, Any>(
                        "senderId" to user.uid,
                        "type" to type,
                        "text" to "",
                        "filePath" to filePath,
                        "fileName" to fileName,
                        "mimeType" to mimeType,
                        "fileSize" to fileSize,
                        "createdAt" to Timestamp.now()
                    )

                firestore
                    .collection("chatRooms")
                    .document(CHAT_ROOM)
                    .collection("messages")
                    .document(messageId)
                    .set(data)
                    .addOnSuccessListener {

                        val index =
                            messages.indexOfFirst {
                                it.id == messageId
                            }

                        if (index >= 0) {

                            messages[index] =
                                localMessage.copy(
                                    localStatus =
                                        LocalMessageStatus.SENT
                                )
                        }

                        Toast.makeText(
                            context,
                            "فایل ارسال شد ❤️",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {

                        val index =
                            messages.indexOfFirst {
                                it.id == messageId
                            }

                        if (index >= 0) {

                            messages[index] =
                                localMessage.copy(
                                    localStatus =
                                        LocalMessageStatus.FAILED
                                )
                        }

                        Toast.makeText(
                            context,
                            "فایل آپلود شد ولی پیام ثبت نشد",
                            Toast.LENGTH_LONG
                        ).show()
                    }

            } else {

                val index =
                    messages.indexOfFirst {
                        it.id == messageId
                    }

                if (index >= 0) {

                    messages[index] =
                        localMessage.copy(
                            localStatus =
                                LocalMessageStatus.FAILED
                        )
                }

                Toast.makeText(
                    context,
                    result,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

private fun retryMessage(
    message: ChatMessage,
    context: Context
) {

    if (message.localUri.isBlank()) {

        Toast.makeText(
            context,
            "فایل اصلی برای تلاش دوباره پیدا نشد",
            Toast.LENGTH_LONG
        ).show()

        return
    }

    Toast.makeText(
        context,
        "تلاش دوباره برای ارسال...",
        Toast.LENGTH_SHORT
    ).show()
}

private fun getFileName(
    context: Context,
    uri: Uri
): String {

    var result: String? = null

    context.contentResolver
        .query(
            uri,
            arrayOf(
                OpenableColumns.DISPLAY_NAME
            ),
            null,
            null,
            null
        )
        ?.use { cursor ->

            if (cursor.moveToFirst()) {

                val index =
                    cursor.getColumnIndex(
                        OpenableColumns.DISPLAY_NAME
                    )

                if (index >= 0) {

                    result =
                        cursor.getString(index)
                }
            }
        }

    return result
        ?: uri.lastPathSegment
        ?: "royaram_file"
}

private fun getFileSize(
    context: Context,
    uri: Uri
): Long {

    return try {

        context.contentResolver
            .query(
                uri,
                arrayOf(
                    OpenableColumns.SIZE
                ),
                null,
                null,
                null
            )
            ?.use { cursor ->

                if (cursor.moveToFirst()) {

                    val index =
                        cursor.getColumnIndex(
                            OpenableColumns.SIZE
                        )

                    if (index >= 0) {

                        return cursor.getLong(index)
                    }
                }
            }

        0L

    } catch (_: Exception) {

        0L
    }
}

private fun formatFileSize(
    bytes: Long
): String {

    if (bytes <= 0) {
        return "اندازه نامشخص"
    }

    return when {

        bytes < 1024 ->
            "$bytes B"

        bytes < 1024 * 1024 ->
            String.format(
                Locale.US,
                "%.1f KB",
                bytes / 1024.0
            )

        bytes < 1024 * 1024 * 1024 ->
            String.format(
                Locale.US,
                "%.1f MB",
                bytes / (1024.0 * 1024.0)
            )

        else ->
            String.format(
                Locale.US,
                "%.1f GB",
                bytes /
                    (1024.0 * 1024.0 * 1024.0)
            )
    }
}

private fun formatMessageTime(
    timestamp: Timestamp?
): String {

    if (timestamp == null) {
        return ""
    }

    return try {

        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(
            Date(
                timestamp.seconds * 1000
            )
        )

    } catch (_: Exception) {

        ""
    }
}
