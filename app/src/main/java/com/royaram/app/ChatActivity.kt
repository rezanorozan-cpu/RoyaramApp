package com.royaram.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChatMessage(
    val text: String,
    val isMine: Boolean,
    val time: String,
    val liked: Boolean = false
)

class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChatScreen(
                onBack = {
                    finish()
                }
            )
        }
    }
}

@Composable
private fun ChatScreen(
    onBack: () -> Unit
) {

    var messageText by remember {
        mutableStateOf("")
    }

    var isTyping by remember {
        mutableStateOf(false)
    }

    val messages = remember {

        mutableStateListOf(

            ChatMessage(
                text = "سلام عشق من ❤️",
                isMine = false,
                time = "18:20"
            ),

            ChatMessage(
                text = "سلام رویای من ❤️ دلم برات تنگ شده",
                isMine = true,
                time = "18:21"
            ),

            ChatMessage(
                text = "منم دلم برات تنگ شده 🥹❤️",
                isMine = false,
                time = "18:21"
            )
        )
    }

    val listState = rememberLazyListState()

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
                .padding(
                    horizontal = 12.dp
                ),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(7.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 14.dp,
                bottom = 14.dp
            )
        ) {

            items(messages) { message ->

                ChatBubble(
                    message = message
                )
            }

            if (isTyping) {

                item {

                    TypingBubble()
                }
            }
        }

        MessageInput(
            messageText = messageText,
            onMessageChange = {

                messageText = it
                isTyping = it.isNotBlank()
            },
            onAttachmentClick = {

            },
            onSend = {

                if (messageText.isNotBlank()) {

                    messages.add(
                        ChatMessage(
                            text = messageText.trim(),
                            isMine = true,
                            time = "الان"
                        )
                    )

                    messageText = ""
                    isTyping = false
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
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBack
        ) {

            Icon(
                imageVector = Icons.Rounded.ArrowBack,
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
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = Color(0xFFE85D75),
                modifier = Modifier.size(27.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "رامین ❤️ رویا",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF402A30)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            Color(0xFF4CAF50),
                            CircleShape
                        )
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = "آنلاین",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (message.isMine) {
                Arrangement.End
            } else {
                Arrangement.Start
            }
    ) {

        Column(
            horizontalAlignment =
                if (message.isMine) {
                    Alignment.End
                } else {
                    Alignment.Start
                }
        ) {

            Box(
                modifier = Modifier
                    .background(
                        if (message.isMine) {
                            Color(0xFFE85D75)
                        } else {
                            Color.White
                        },
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart =
                                if (message.isMine) {
                                    20.dp
                                } else {
                                    5.dp
                                },
                            bottomEnd =
                                if (message.isMine) {
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
                        if (message.isMine) {
                            Color.White
                        } else {
                            Color(0xFF402A30)
                        }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
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
                    contentDescription = null,
                    tint = Color(0xFFE85D75),
                    modifier = Modifier
                        .size(15.dp)
                        .clickable {
                        }
                )
            }
        }
    }
}

@Composable
private fun TypingBubble() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {

        Box(
            modifier = Modifier
                .background(
                    Color.White,
                    RoundedCornerShape(20.dp)
                )
                .padding(
                    horizontal = 15.dp,
                    vertical = 10.dp
                )
        ) {

            Text(
                text = "در حال تایپ... ✍️",
                fontSize = 13.sp,
                color = Color(0xFF92777F)
            )
        }
    }
}

@Composable
private fun MessageInput(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onAttachmentClick: () -> Unit,
    onSend: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onAttachmentClick,
            modifier = Modifier.size(45.dp)
        ) {

            Icon(
                imageVector = Icons.Rounded.AttachFile,
                contentDescription = "پیوست",
                tint = Color(0xFFE85D75)
            )
        }

        OutlinedTextField(
            value = messageText,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "پیامت رو بنویس ❤️"
                )
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.width(6.dp)
        )

        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(52.dp)
                .background(
                    Color(0xFFE85D75),
                    CircleShape
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
