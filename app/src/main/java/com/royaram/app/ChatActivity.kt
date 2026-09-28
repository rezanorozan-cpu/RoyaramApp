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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
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

        setContent {
            RoyaramChatScreen()
        }
    }
}

private const val CHAT_ROOM = "ramin_roya"

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
    val localStatus: LocalMessageStatus? = null,
    val reaction: String = "",
    val replyToId: String = "",
    val replyToText: String = "",
    val edited: Boolean = false
)

@Composable
private fun RoyaramChatScreen() {

    val context = LocalContext.current

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

    val currentUserId =
        auth.currentUser?.uid ?: ""

    val messages = remember {
        mutableStateListOf<ChatMessage>()
    }

    var messageText by remember {
        mutableStateOf("")
    }

    var showAttachmentMenu by remember {
        mutableStateOf(false)
    }

    var selectedMessageIds by remember {
        mutableStateOf(setOf<String>())
    }

    var showMoreMenu by remember {
        mutableStateOf(false)
    }

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var editingMessage by remember {
        mutableStateOf<ChatMessage?>(null)
    }

    var replyMessage by remember {
        mutableStateOf<ChatMessage?>(null)
    }

    val listState =
        rememberLazyListState()

    val filePicker =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            try {

                context.contentResolver
                    .takePersistableUriPermission(
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

    /*
     * دریافت پیام‌ها
     */
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
                                document.getTimestamp(
                                    "createdAt"
                                ),

                            reaction =
                                document.getString(
                                    "reaction"
                                ) ?: "",

                            replyToId =
                                document.getString(
                                    "replyToId"
                                ) ?: "",

                            replyToText =
                                document.getString(
                                    "replyToText"
                                ) ?: "",

                            edited =
                                document.getBoolean(
                                    "edited"
                                ) ?: false,

                            localStatus =
                                LocalMessageStatus.SENT
                        )
                    }

                val localPending =
                    messages.filter {
                        it.localStatus ==
                                LocalMessageStatus.SENDING ||
                                it.localStatus ==
                                LocalMessageStatus.FAILED
                    }

                val merged =
                    (remoteMessages + localPending)
                        .distinctBy {
                            it.id
                        }
                        .sortedBy {
                            it.createdAt?.seconds ?: 0L
                        }

                messages.clear()
                messages.addAll(merged)

                /*
                 * اگر پیامی که حذف شده بود
                 * در حالت انتخاب باقی مانده بود
                 * از انتخاب خارجش می‌کنیم.
                 */
                selectedMessageIds =
                    selectedMessageIds.filter { id ->
                        merged.any {
                            it.id == id
                        }
                    }.toSet()
            }
    }

    /*
     * رفتن خودکار به آخر چت
     */
    LaunchedEffect(messages.size) {

        if (
            messages.isNotEmpty() &&
            selectedMessageIds.isEmpty()
        ) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    Scaffold(

        topBar = {

            if (selectedMessageIds.isNotEmpty()) {

                SelectionTopBar(

                    selectedCount =
                        selectedMessageIds.size,

                    onClose = {
                        selectedMessageIds =
                            emptySet()
                    },

                    onReply = {

                        if (
                            selectedMessageIds.size == 1
                        ) {

                            val selected =
                                messages.firstOrNull {
                                    it.id ==
                                            selectedMessageIds.first()
                                }

                            if (selected != null) {

                                replyMessage =
                                    selected

                                selectedMessageIds =
                                    emptySet()
                            }
                        }
                    },

                    onDelete = {

                        deleteSelectedMessages(
                            firestore = firestore,
                            auth = auth,
                            messages = messages,
                            selectedIds =
                                selectedMessageIds,
                            context = context
                        )

                        selectedMessageIds =
                            emptySet()
                    },

                    onMore = {
                        showMoreMenu = true
                    },

                    onReaction = { reaction ->

                        setReactionForSelectedMessages(
                            firestore = firestore,
                            auth = auth,
                            selectedIds =
                                selectedMessageIds,
                            reaction = reaction,
                            messages = messages,
                            context = context
                        )
                    }
                )

            } else {

                NormalChatTopBar(
                    onMoreClick = {}
                )
            }
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

                    val text =
                        messageText.trim()

                    if (text.isNotEmpty()) {

                        sendTextMessage(
                            firestore = firestore,
                            auth = auth,
                            text = text,
                            messages = messages,
                            replyMessage = replyMessage
                        )

                        messageText = ""

                        replyMessage = null
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
                        key = {
                            it.id
                        }
                    ) { message ->

                        val isSelected =
                            selectedMessageIds.contains(
                                message.id
                            )

                        ChatMessageBubble(

                            message = message,

                            currentUserId =
                                currentUserId,

                            context = context,

                            isSelected =
                                isSelected,

                            selectionMode =
                                selectedMessageIds.isNotEmpty(),

                            onLongPress = {

                                selectedMessageIds =
                                    selectedMessageIds +
                                            message.id
                            },

                            onClick = {

                                if (
                                    selectedMessageIds
                                        .isNotEmpty()
                                ) {

                                    selectedMessageIds =
                                        if (
                                            isSelected
                                        ) {
                                            selectedMessageIds -
                                                    message.id
                                        } else {
                                            selectedMessageIds +
                                                    message.id
                                        }
                                }
                            }
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

    /*
     * منوی بیشتر
     */
    if (showMoreMenu) {

        val selectedMessages =
            messages.filter {
                selectedMessageIds.contains(
                    it.id
                )
            }

        AlertDialog(

            onDismissRequest = {
                showMoreMenu = false
            },

            title = {
                Text(
                    text = "عملیات پیام",
                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {

                Column {

                    if (
                        selectedMessages.size == 1 &&
                        selectedMessages.first().type ==
                        "TEXT"
                    ) {

                        TextButton(

                            onClick = {

                                val message =
                                    selectedMessages.first()

                                copyText(
                                    context,
                                    message.text
                                )

                                showMoreMenu = false
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.ContentCopy,
                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text("کپی متن")
                        }

                        TextButton(

                            onClick = {

                                editingMessage =
                                    selectedMessages.first()

                                showEditDialog =
                                    true

                                showMoreMenu =
                                    false
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Edit,
                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text("ویرایش پیام")
                        }
                    }

                    if (
                        selectedMessages.size == 1
                    ) {

                        TextButton(

                            onClick = {

                                replyMessage =
                                    selectedMessages.first()

                                selectedMessageIds =
                                    emptySet()

                                showMoreMenu =
                                    false
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Reply,
                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text("پاسخ به پیام")
                        }
                    }

                    TextButton(

                        onClick = {

                            deleteSelectedMessages(
                                firestore =
                                    firestore,

                                auth =
                                    auth,

                                messages =
                                    messages,

                                selectedIds =
                                    selectedMessageIds,

                                context =
                                    context
                            )

                            selectedMessageIds =
                                emptySet()

                            showMoreMenu =
                                false
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Delete,
                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text("حذف پیام")
                    }
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showMoreMenu = false
                    }
                ) {
                    Text("بستن")
                }
            }
        )
    }

    /*
     * ویرایش پیام
     */
    if (
        showEditDialog &&
        editingMessage != null
    ) {

        EditMessageDialog(

            message =
                editingMessage!!,

            onDismiss = {

                showEditDialog =
                    false

                editingMessage =
                    null
            },

            onSave = { newText ->

                editMessage(
                    firestore =
                        firestore,

                    auth =
                        auth,

                    message =
                        editingMessage!!,

                    newText =
                        newText,

                    messages =
                        messages,

                    context =
                        context
                )

                showEditDialog =
                    false

                editingMessage =
                    null

                selectedMessageIds =
                    emptySet()
            }
        )
    }

    /*
     * انتخاب فایل
     */
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

/*
 * نوار بالای حالت انتخاب
 */
@Composable
private fun SelectionTopBar(

    selectedCount: Int,

    onClose: () -> Unit,

    onReply: () -> Unit,

    onDelete: () -> Unit,

    onMore: () -> Unit,

    onReaction: (String) -> Unit
) {

    Column {

        Surface(
            modifier =
                Modifier.fillMaxWidth(),

            color =
                Color.White,

            shadowElevation =
                4.dp
        ) {

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 4.dp,
                            vertical = 6.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onClose
                ) {

                    Text(
                        text = "✕",
                        fontSize = 24.sp
                    )
                }

                Text(
                    text =
                        "$selectedCount انتخاب شد",
                    modifier =
                        Modifier.weight(1f),
                    fontSize = 18.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                if (selectedCount == 1) {

                    IconButton(
                        onClick = onReply
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Reply,
                            contentDescription =
                                "پاسخ"
                        )
                    }
                }

                IconButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Delete,
                        contentDescription =
                            "حذف"
                    )
                }

                IconButton(
                    onClick = onMore
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.MoreVert,
                        contentDescription =
                            "بیشتر"
                    )
                }
            }
        }

        /*
         * واکنش‌های سریع
         */
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            24.dp
                        )
                    ),

            color =
                Color.White,

            shadowElevation =
                3.dp
        ) {

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),

                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                ReactionButton(
                    emoji = "❤️",
                    onClick = {
                        onReaction("❤️")
                    }
                )

                ReactionButton(
                    emoji = "👍",
                    onClick = {
                        onReaction("👍")
                    }
                )

                ReactionButton(
                    emoji = "👎",
                    onClick = {
                        onReaction("👎")
                    }
                )

                ReactionButton(
                    emoji = "🔥",
                    onClick = {
                        onReaction("🔥")
                    }
                )

                ReactionButton(
                    emoji = "🥰",
                    onClick = {
                        onReaction("🥰")
                    }
                )

                ReactionButton(
                    emoji = "😂",
                    onClick = {
                        onReaction("😂")
                    }
                )
            }
        }
    }
}

@Composable
private fun ReactionButton(
    emoji: String,
    onClick: () -> Unit
) {

    Text(
        text = emoji,
        fontSize = 25.sp,
        modifier =
            Modifier
                .clip(CircleShape)
                .clickable(
                    onClick = onClick
                )
                .padding(5.dp)
    )
}

/*
 * نوار عادی بالای چت
 */
@Composable
private fun NormalChatTopBar(
    onMoreClick: () -> Unit
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),

        color =
            Color.White,

        shadowElevation =
            2.dp
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = "رامین ❤️ رویا",
                    fontWeight =
                        FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text =
                        "گفت‌وگوی دونفره",
                    fontSize = 12.sp,
                    color =
                        Color.Gray
                )
            }

            IconButton(
                onClick = onMoreClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MoreVert,
                    contentDescription =
                        "بیشتر"
                )
            }
        }
    }
}

@Composable
private fun EmptyChat() {

    Box(
        modifier =
            Modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
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
                text =
                    "اینجا جای حرف‌های من و توئه",
                fontSize = 18.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "اولین پیام رو بفرست 🌹",
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

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(
                    Color.White
                )
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
                contentDescription =
                    "پیوست"
            )
        }

        OutlinedTextField(

            value = text,

            onValueChange =
                onTextChange,

            modifier =
                Modifier.weight(1f),

            placeholder = {
                Text(
                    text =
                        "پیامت رو بنویس..."
                )
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
                    contentDescription =
                        "ارسال",
                    tint =
                        Color.White
                )
            }
        }
    }
}

/*
 * حباب پیام
 */
@OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
private fun ChatMessageBubble(

    message: ChatMessage,

    currentUserId: String,

    context: Context,

    isSelected: Boolean,

    selectionMode: Boolean,

    onLongPress: () -> Unit,

    onClick: () -> Unit
) {

    val isMine =
        message.senderId ==
                currentUserId

    val bubbleColor =
        when {

            isSelected ->
                Color(0xFFFFB6CB)

            isMine ->
                Color(0xFFFFD9E5)

            else ->
                Color.White
        }

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

            Box(

                modifier =
                    Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp
