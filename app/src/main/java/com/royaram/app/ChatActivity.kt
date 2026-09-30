@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.royaram.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
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
        setContent { RoyaramChatScreen() }
    }
}

private const val CHAT_ROOM = "ramin_roya"

private val Pink = Color(0xFFE85D86)
private val DeepPink = Color(0xFFB83D63)
private val TextDark = Color(0xFF33252B)
private val SoftText = Color(0xFF82747A)
private val LightPink = Color(0xFFFFE7EF)
private val Lavender = Color(0xFFF4EAFF)

private enum class LocalMessageStatus { SENDING, SENT, FAILED }

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
    val localStatus: LocalMessageStatus? = null,
    val reaction: String = "",
    val replyToId: String = "",
    val replyToText: String = "",
    val edited: Boolean = false
)

@Composable
private fun RoyaramChatScreen() {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val currentUserId = auth.currentUser?.uid ?: ""

    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()

    var messageText by remember { mutableStateOf("") }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var selectedMessageIds by remember { mutableStateOf(setOf<String>()) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var replyMessage by remember { mutableStateOf<ChatMessage?>(null) }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {
        }

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

        firestore.collection("chatRooms")
            .document(CHAT_ROOM)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Toast.makeText(
                        context,
                        "خطا در دریافت پیام‌ها",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }

                val remote = snapshot.documents.mapNotNull { doc ->
                    val senderId = doc.getString("senderId")
                        ?: return@mapNotNull null

                    ChatMessage(
                        id = doc.id,
                        senderId = senderId,
                        type = doc.getString("type") ?: "TEXT",
                        text = doc.getString("text") ?: "",
                        filePath = doc.getString("filePath") ?: "",
                        fileName = doc.getString("fileName") ?: "",
                        mimeType = doc.getString("mimeType") ?: "",
                        fileSize = doc.getLong("fileSize") ?: 0L,
                        createdAt = doc.getTimestamp("createdAt"),
                        reaction = doc.getString("reaction") ?: "",
                        replyToId = doc.getString("replyToId") ?: "",
                        replyToText = doc.getString("replyToText") ?: "",
                        edited = doc.getBoolean("edited") ?: false,
                        localStatus = LocalMessageStatus.SENT
                    )
                }

                val pending = messages.filter {
                    it.localStatus == LocalMessageStatus.SENDING ||
                        it.localStatus == LocalMessageStatus.FAILED
                }

                val merged = (remote + pending)
                    .distinctBy { it.id }
                    .sortedBy { it.createdAt?.seconds ?: 0L }

                messages.clear()
                messages.addAll(merged)

                selectedMessageIds = selectedMessageIds
                    .filter { id -> merged.any { it.id == id } }
                    .toSet()
            }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty() && selectedMessageIds.isEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            if (selectedMessageIds.isNotEmpty()) {
                SelectionTopBar(
                    selectedCount = selectedMessageIds.size,
                    onClose = { selectedMessageIds = emptySet() },
                    onReply = {
                        if (selectedMessageIds.size == 1) {
                            messages.firstOrNull {
                                it.id == selectedMessageIds.first()
                            }?.let {
                                replyMessage = it
                                selectedMessageIds = emptySet()
                            }
                        }
                    },
                    onDelete = {
                        deleteSelectedMessages(
                            firestore,
                            auth,
                            messages,
                            selectedMessageIds,
                            context
                        )
                        selectedMessageIds = emptySet()
                    },
                    onMore = { showMoreMenu = true },
                    onReaction = { reaction ->
                        setReactionForSelectedMessages(
                            firestore,
                            selectedMessageIds,
                            reaction,
                            messages,
                            context
                        )
                    }
                )
            } else {
                NormalChatTopBar()
            }
        },
        bottomBar = {
            ChatInputBar(
                text = messageText,
                replyMessage = replyMessage,
                onTextChange = { messageText = it },
                onCancelReply = { replyMessage = null },
                onAttachClick = { showAttachmentMenu = true },
                onSendClick = {
                    val clean = messageText.trim()
                    if (clean.isNotEmpty()) {
                        sendTextMessage(
                            firestore,
                            auth,
                            clean,
                            messages,
                            replyMessage
                        )
                        messageText = ""
                        replyMessage = null
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFEAF2),
                            Color(0xFFF7EEFF),
                            Color(0xFFFFFBFD)
                        )
                    )
                )
        ) {
            if (messages.isEmpty()) {
                EmptyChat()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    item { Spacer(Modifier.height(8.dp)) }

                    items(messages, key = { it.id }) { message ->
                        val selected = selectedMessageIds.contains(message.id)

                        ChatMessageBubble(
                            message = message,
                            currentUserId = currentUserId,
                            isSelected = selected,
                            selectionMode = selectedMessageIds.isNotEmpty(),
                            onLongPress = {
                                selectedMessageIds =
                                    selectedMessageIds + message.id
                            },
                            onClick = {
                                if (selectedMessageIds.isNotEmpty()) {
                                    selectedMessageIds =
                                        if (selected) {
                                            selectedMessageIds - message.id
                                        } else {
                                            selectedMessageIds + message.id
                                        }
                                }
                            }
                        )
                    }

                    item { Spacer(Modifier.height(10.dp)) }
                }
            }
        }
    }

    if (showMoreMenu) {
        val selected = messages.filter {
            selectedMessageIds.contains(it.id)
        }

        AlertDialog(
            onDismissRequest = { showMoreMenu = false },
            title = {
                Text(
                    "عملیات پیام",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    if (selected.size == 1 && selected.first().type == "TEXT") {
                        TextButton(
                            onClick = {
                                copyText(context, selected.first().text)
                                showMoreMenu = false
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, null)
                            Spacer(Modifier.width(8.dp))
                            Text("کپی متن")
                        }

                        if (selected.first().senderId == currentUserId) {
                            TextButton(
                                onClick = {
                                    editingMessage = selected.first()
                                    showEditDialog = true
                                    showMoreMenu = false
                                }
                            ) {
                                Icon(Icons.Default.Edit, null)
                                Spacer(Modifier.width(8.dp))
                                Text("ویرایش پیام")
                            }
                        }

                        TextButton(
                            onClick = {
                                replyMessage = selected.first()
                                selectedMessageIds = emptySet()
                                showMoreMenu = false
                            }
                        ) {
                            Icon(Icons.Default.Reply, null)
                            Spacer(Modifier.width(8.dp))
                            Text("پاسخ به پیام")
                        }
                    }

                    TextButton(
                        onClick = {
                            deleteSelectedMessages(
                                firestore,
                                auth,
                                messages,
                                selectedMessageIds,
                                context
                            )
                            selectedMessageIds = emptySet()
                            showMoreMenu = false
                        }
                    ) {
                        Icon(Icons.Default.Delete, null)
                        Spacer(Modifier.width(8.dp))
                        Text("حذف پیام")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMoreMenu = false }) {
                    Text("بستن")
                }
            }
        )
    }

    if (showEditDialog && editingMessage != null) {
        EditMessageDialog(
            message = editingMessage!!,
            onDismiss = {
                showEditDialog = false
                editingMessage = null
            },
            onSave = { newText ->
                editMessage(                    firestore,
                    auth,
                    editingMessage!!,
                    newText,
                    messages,
                    context
                )
                showEditDialog = false
                editingMessage = null
                selectedMessageIds = emptySet()
            }
        )
    }

    if (showAttachmentMenu) {
        AttachmentDialog(
            onDismiss = { showAttachmentMenu = false },
            onSelect = {
                showAttachmentMenu = false
                filePicker.launch(arrayOf("*/*"))
            }
        )
    }
}

@Composable
private fun NormalChatTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFFDCE9),
                        Color(0xFFF1E5FF),
                        Color(0xFFFFF5F8)
                    )
                )
            )
            .padding(horizontal = 14.dp, vertical = 11.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Pink, DeepPink)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("❤️", fontSize = 24.sp)
            }

            Spacer(Modifier.width(11.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    "رامین ❤️ رویا",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "فضای خصوصیِ دونفره‌مون ✨",
                    fontSize = 12.sp,
                    color = SoftText
                )
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.62f)
            ) {
                Text(
                    "خصوصی 🔐",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 7.dp
                    ),
                    fontSize = 11.sp,
                    color = DeepPink,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SelectionTopBar(
    selectedCount: Int,
    onClose: () -> Unit,
    onReply: () -> Unit,
    onDelete: () -> Unit,
    onMore: () -> Unit,
    onReaction: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFFDCE9),
                        Color(0xFFF2E8FF)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Text("✕", fontSize = 22.sp, color = TextDark)
            }

            Column(Modifier.weight(1f)) {
                Text(
                    "$selectedCount پیام",
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    "عملیات روی پیام انتخاب‌شده",
                    fontSize = 10.sp,
                    color = SoftText
                )
            }

            if (selectedCount == 1) {
                IconButton(onClick = onReply) {
                    Icon(Icons.Default.Reply, "پاسخ", tint = DeepPink)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "حذف", tint = DeepPink)
            }

            IconButton(onClick = onMore) {
                Icon(Icons.Default.MoreVert, "بیشتر", tint = TextDark)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("❤️", "🥰", "🔥", "😂", "👍", "✨").forEach { emoji ->
                Text(
                    emoji,
                    fontSize = 23.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .combinedClickable(
                            onClick = { onReaction(emoji) },
                            onLongClick = {}
                        )
                        .padding(5.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyChat() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Pink, DeepPink)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("❤️", fontSize = 42.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "اینجا جای حرف‌های من و توئه",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "اولین پیام رو بفرست 🌹",
                fontSize = 13.sp,
                color = SoftText
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    replyMessage: ChatMessage?,
    onTextChange: (String) -> Unit,
    onCancelReply: () -> Unit,
    onAttachClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            if (replyMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(LightPink)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .width(3.dp)
                            .height(34.dp)
                            .background(Pink)
                    )

                    Spacer(Modifier.width(8.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            "پاسخ به پیام",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepPink
                        )
                        Text(
                            replyMessage.text.ifBlank {
                                replyMessage.fileName.ifBlank { "فایل" }
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            color = TextDark
                        )
                    }

                    IconButton(onClick = onCancelReply) {
                        Text("✕", color = SoftText)
                    }
                }

                Spacer(Modifier.height(7.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(LightPink)
                        .combinedClickable(
                            onClick = onAttachClick,
                            onLongClick = {}
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AttachFile,
                        "پیوست",
                        tint = DeepPink
                    )
                }

                Spacer(Modifier.width(7.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "پیامت رو بنویس...",
                            color = SoftText
                        )
                    },
                    maxLines = 4,
                    shape = RoundedCornerShape(22.dp)
                )

                Spacer(Modifier.width(7.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Pink, DeepPink)
                            )
                        )
                        .combinedClickable(
                            onClick = onSendClick,
                            onLongClick = {}
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Send,
                        "ارسال",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    currentUserId: String,
    isSelected: Boolean,
    selectionMode: Boolean,
    onLongPress: () -> Unit,
    onClick: () -> Unit
) {
    val isMine = message.senderId == currentUserId

    val bubbleBrush = when {
        isSelected -> Brush.linearGradient(
            listOf(Color(0xFFFFB8CC), Color(0xFFFFD9E5))
        )
        isMine -> Brush.linearGradient(
            listOf(Color(0xFFFFD7E4), Color(0xFFFFEAF1))
        )
        else -> Brush.linearGradient(
            listOf(Color.White, Color(0xFFFFF9FC))
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment =
                if (isMine) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .widthInSafe(min = 0.dp, max = 300.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isMine) 20.dp else 5.dp,
                            bottomEnd = if (isMine) 5.dp else 20.dp
                        )
                    )
                    .background(bubbleBrush)
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongPress
                    )
                    .padding(10.dp)
            ) {
                Column {
                    if (message.replyToText.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Color.White.copy(alpha = 0.58f)
                                )
                                .padding(7.dp)
                        ) {
                            Box(
                                Modifier
                                    .width(3.dp)
                                    .height(34.dp)
                                    .background(Pink)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                message.replyToText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 11.sp,
                                color = SoftText
                            )
                        }
                        Spacer(Modifier.height(7.dp))
                    }

                    when (message.type.uppercase()) {
                        "TEXT" -> {
                            Text(
                                message.text,
                                fontSize = 16.sp,
                                color = TextDark
                            )

                            if (message.edited) {
                                Text(
                                    "ویرایش شد",
                                    fontSize = 9.sp,
                                    color = SoftText
                                )
                            }
                        }

                        "IMAGE" -> ImageMessageContent(message)

                        "VIDEO" -> FileMessageContent(
                            message,
                            Icons.Default.Videocam
                        )                        "AUDIO" -> FileMessageContent(
                            message,
                            Icons.Default.Mic
                        )

                        else -> FileMessageContent(
                            message,
                            Icons.Default.InsertDriveFile
                        )
                    }

                    if (message.reaction.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                message.reaction,
                                modifier = Modifier.padding(
                                    horizontal = 7.dp,
                                    vertical = 2.dp
                                ),
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            formatMessageTime(message.createdAt),
                            fontSize = 9.sp,
                            color = SoftText
                        )

                        if (isMine) {
                            Spacer(Modifier.width(4.dp))

                            when (message.localStatus) {
                                LocalMessageStatus.SENDING -> {
                                    CircularProgressIndicator(
                                        Modifier.size(11.dp),
                                        strokeWidth = 1.5.dp
                                    )
                                }

                                LocalMessageStatus.FAILED -> {
                                    Icon(
                                        Icons.Default.ErrorOutline,
                                        "خطا",
                                        Modifier.size(14.dp),
                                        tint = Color.Red
                                    )
                                }

                                else -> {
                                    Text(
                                        "✓✓",
                                        fontSize = 9.sp,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (message.localStatus == LocalMessageStatus.FAILED) {
                Text(
                    "ارسال نشد",
                    color = Color.Red,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

private fun Modifier.widthInSafe(
    min: androidx.compose.ui.unit.Dp,
    max: androidx.compose.ui.unit.Dp
): Modifier = this.then(
    Modifier
        .then(
            androidx.compose.foundation.layout.widthIn(
                min = min,
                max = max
            )
        )
)

@Composable
private fun ImageMessageContent(message: ChatMessage) {
    if (message.localUri.isNotBlank()) {
        AsyncImage(
            model = message.localUri.toUri(),
            contentDescription = message.fileName,
            modifier = Modifier
                .size(width = 240.dp, height = 240.dp)
                .clip(RoundedCornerShape(15.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Image,
                null,
                Modifier.size(58.dp),
                tint = Pink
            )
            Text(
                message.fileName.ifBlank { "عکس" },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                color = TextDark
            )
        }
    }
}

@Composable
private fun FileMessageContent(
    message: ChatMessage,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.width(245.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(LightPink),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = DeepPink)
        }

        Spacer(Modifier.width(10.dp))

        Column(Modifier.weight(1f)) {
            Text(
                message.fileName.ifBlank { "فایل" },
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = TextDark
            )
            Text(
                formatFileSize(message.fileSize),
                fontSize = 10.sp,
                color = SoftText
            )
        }

        Icon(
            Icons.Default.Download,
            "دانلود",
            tint = DeepPink
        )
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
                "ارسال فایل",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                AttachmentItem(Icons.Default.Image, "عکس یا تصویر", onSelect)
                AttachmentItem(Icons.Default.Videocam, "ویدیو", onSelect)
                AttachmentItem(Icons.Default.Mic, "فایل صوتی", onSelect)
                AttachmentItem(Icons.Default.Description, "سند و فایل", onSelect)
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("بستن") }
        }
    )
}

@Composable
private fun AttachmentItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = {}
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = DeepPink)
        Spacer(Modifier.width(12.dp))
        Text(title, fontSize = 16.sp, color = TextDark)
    }
}

@Composable
private fun EditMessageDialog(
    message: ChatMessage,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(message.text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "ویرایش پیام",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                label = { Text("متن پیام") },
                shape = RoundedCornerShape(18.dp)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    text.trim()
                        .takeIf { it.isNotEmpty() }
                        ?.let(onSave)
                }
            ) {
                Text("ذخیره")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}

private fun sendTextMessage(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    text: String,
    messages: MutableList<ChatMessage>,
    replyMessage: ChatMessage?
) {
    val user = auth.currentUser ?: return
    val messageId = UUID.randomUUID().toString()
    val createdAt = Timestamp.now()

    val local = ChatMessage(
        id = messageId,
        senderId = user.uid,
        type = "TEXT",
        text = text,
        createdAt = createdAt,
        replyToId = replyMessage?.id ?: "",
        replyToText = replyMessage?.text ?: "",
        localStatus = LocalMessageStatus.SENDING
    )

    messages.add(local)

    val data = hashMapOf<String, Any>(
        "senderId" to user.uid,
        "type" to "TEXT",
        "text" to text,
        "createdAt" to createdAt,
        "replyToId" to (replyMessage?.id ?: ""),
        "replyToText" to (replyMessage?.text ?: "")
    )

    firestore.collection("chatRooms")
        .document(CHAT_ROOM)
        .collection("messages")
        .document(messageId)
        .set(data)
        .addOnSuccessListener {
            val index = messages.indexOfFirst { it.id == messageId }
            if (index >= 0) {
                messages[index] = local.copy(
                    localStatus = LocalMessageStatus.SENT
                )
            }
        }
        .addOnFailureListener {
            val index = messages.indexOfFirst { it.id == messageId }
            if (index >= 0) {
                messages[index] = local.copy(
                    localStatus = LocalMessageStatus.FAILED
                )
            }
        }
}

private fun editMessage(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    message: ChatMessage,
    newText: String,
    messages: MutableList<ChatMessage>,
    context: Context
) {
    val user = auth.currentUser ?: return

    if (message.senderId != user.uid) {
        Toast.makeText(
            context,
            "فقط پیام خودت را می‌توانی ویرایش کنی",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    firestore.collection("chatRooms")
        .document(CHAT_ROOM)
        .collection("messages")
        .document(message.id)
        .update(
            mapOf(
                "text" to newText,
                "edited" to true,
                "editedAt" to Timestamp.now()
            )
        )
        .addOnSuccessListener {
            val index = messages.indexOfFirst { it.id == message.id }
            if (index >= 0) {
                messages[index] = message.copy(
                    text = newText,
                    edited = true
                )
            }
            Toast.makeText(
                context,
                "پیام ویرایش شد ✏️",
                Toast.LENGTH_SHORT
            ).show()
        }
        .addOnFailureListener {
            Toast.makeText(
                context,
                "ویرایش پیام ناموفق بود",
                Toast.LENGTH_SHORT
            ).show()
        }
}

private fun deleteSelectedMessages(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    messages: MutableList<ChatMessage>,
    selectedIds: Set<String>,
    context: Context
) {
    val user = auth.currentUser ?: return

    val mine = messages.filter {
        selectedIds.contains(it.id) &&
            it.senderId == user.uid
    }

    if (mine.isEmpty()) {
        Toast.makeText(
            context,
            "فقط پیام‌های خودت را می‌توانی حذف کنی",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val batch = firestore.batch()

    mine.forEach { message ->
        batch.delete(
            firestore.collection("chatRooms")
                .document(CHAT_ROOM)
                .collection("messages")
                .document(message.id)
        )
    }

    batch.commit()
        .addOnSuccessListener {
            messages.removeAll { current ->
                mine.any { it.id == current.id }
            }
            Toast.makeText(
                context,
                "پیام حذف شد 🗑️",
                Toast.LENGTH_SHORT
            ).show()
        }
        .addOnFailureListener {
            Toast.makeText(
                context,
                "حذف پیام ناموفق بود",
                Toast.LENGTH_SHORT
            ).show()
        }
}

private fun setReactionForSelectedMessages(
    firestore: FirebaseFirestore,
    selectedIds: Set<String>,
    reaction: String,
    messages: MutableList<ChatMessage>,
    context: Context
) {
    if (selectedIds.isEmpty()) return

    val batch = firestore.batch()

    selectedIds.forEach { id ->
        batch.update(
            firestore.collection("chatRooms")
                .document(CHAT_ROOM)
                .collection("messages")
                .document(id),
            "reaction",
            reaction
        )    }

    batch.commit()
        .addOnSuccessListener {
            selectedIds.forEach { id ->
                val index = messages.indexOfFirst { it.id == id }
                if (index >= 0) {
                    messages[index] =
                        messages[index].copy(reaction = reaction)
                }
            }

            Toast.makeText(
                context,
                "واکنش اضافه شد $reaction",
                Toast.LENGTH_SHORT
            ).show()
        }
        .addOnFailureListener {
            Toast.makeText(
                context,
                "ثبت واکنش ناموفق بود",
                Toast.LENGTH_SHORT
            ).show()
        }
}

private fun copyText(context: Context, text: String) {
    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE)
            as ClipboardManager

    clipboard.setPrimaryClip(
        ClipData.newPlainText("Royaram", text)
    )

    Toast.makeText(
        context,
        "متن کپی شد 📋",
        Toast.LENGTH_SHORT
    ).show()
}

private fun uploadFileToChat(
    context: Context,
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    uri: Uri,
    messages: MutableList<ChatMessage>
) {
    val user = auth.currentUser ?: return

    val fileName = getFileName(context, uri)
    val mimeType =
        context.contentResolver.getType(uri)
            ?: "application/octet-stream"
    val fileSize = getFileSize(context, uri)
    val messageId = UUID.randomUUID().toString()

    val type = when {
        mimeType.startsWith("image/") -> "IMAGE"
        mimeType.startsWith("video/") -> "VIDEO"
        mimeType.startsWith("audio/") -> "AUDIO"
        else -> "FILE"
    }

    val filePath = "chat/ramin_roya/$messageId-$fileName"
    val createdAt = Timestamp.now()

    val local = ChatMessage(
        id = messageId,
        senderId = user.uid,
        type = type,
        filePath = filePath,
        fileName = fileName,
        mimeType = mimeType,
        fileSize = fileSize,
        localUri = uri.toString(),
        createdAt = createdAt,
        localStatus = LocalMessageStatus.SENDING
    )

    messages.add(local)

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
        Handler(Looper.getMainLooper()).post {
            if (!success) {
                val index = messages.indexOfFirst { it.id == messageId }
                if (index >= 0) {
                    messages[index] =
                        local.copy(
                            localStatus =
                                LocalMessageStatus.FAILED
                        )
                }

                Toast.makeText(
                    context,
                    "ارسال فایل ناموفق بود:\n$result",
                    Toast.LENGTH_LONG
                ).show()
                return@post
            }

            val data = hashMapOf<String, Any>(
                "senderId" to user.uid,
                "type" to type,
                "text" to "",
                "filePath" to filePath,
                "fileName" to fileName,
                "mimeType" to mimeType,
                "fileSize" to fileSize,
                "createdAt" to createdAt
            )

            firestore.collection("chatRooms")
                .document(CHAT_ROOM)
                .collection("messages")
                .document(messageId)
                .set(data)
                .addOnSuccessListener {
                    val index =
                        messages.indexOfFirst { it.id == messageId }

                    if (index >= 0) {
                        messages[index] =
                            local.copy(
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
                        messages.indexOfFirst { it.id == messageId }

                    if (index >= 0) {
                        messages[index] =
                            local.copy(
                                localStatus =
                                    LocalMessageStatus.FAILED
                            )
                    }

                    Toast.makeText(
                        context,
                        "آپلود شد ولی ثبت پیام ناموفق بود",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}

private fun getFileName(
    context: Context,
    uri: Uri
): String {
    var result: String? = null

    try {
        context.contentResolver.query(
            uri,
            arrayOf(
                android.provider.OpenableColumns.DISPLAY_NAME
            ),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(
                    android.provider.OpenableColumns.DISPLAY_NAME
                )
                if (index >= 0) {
                    result = cursor.getString(index)
                }
            }
        }
    } catch (_: Exception) {
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
        context.contentResolver.query(
            uri,
            arrayOf(android.provider.OpenableColumns.SIZE),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(
                    android.provider.OpenableColumns.SIZE
                )
                if (index >= 0) return cursor.getLong(index)
            }
        }
        0L
    } catch (_: Exception) {
        0L
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "اندازه نامشخص"

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
                bytes / (1024.0 * 1024.0 * 1024.0)
            )
    }
}

private fun formatMessageTime(
    timestamp: Timestamp?
): String {
    if (timestamp == null) return ""

    return try {
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(
            Date(timestamp.seconds * 1000)
        )
    } catch (_: Exception) {
        ""
    }
}
