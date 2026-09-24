package com.royaram.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChatMessage(
    val text: String,
    val isMine: Boolean,
    val time: String
)

class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChatScreen(
                onBack = { finish() }
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

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                "سلام عشق من ❤️",
                false,
                "18:20"
            ),
            ChatMessage(
                "سلام رویای من ❤️ دلم برات تنگ شده",
                true,
                "18:21"
            ),
            ChatMessage(
                "منم دلم برات تنگ شده 🥹❤️",
                false,
                "18:21"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5F8))
            .imePadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFE8EF))
                .padding(
                    horizontal = 10.dp,
                    vertical = 12.dp
                ),
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
                    .size(46.dp)
                    .background(
                        Color.White,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE85D75),
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column {
                Text(
                    text = "رامین ❤️ رویا",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF402A30)
                )

                Text(
                    text = "گفت‌وگوی دونفره ما",
                    fontSize = 12.sp,
                    color = Color(0xFF795C64)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = false
        ) {

            items(messages) { message ->

                ChatBubble(message)
            }
        }

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

            OutlinedTextField(
                value = messageText,
                onValueChange = {
                    messageText = it
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("پیامت رو بنویس ❤️")
                },
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            IconButton(
                onClick = {

                    if (messageText.isNotBlank()) {

                        messages.add(
                            ChatMessage(
                                text = messageText.trim(),
                                isMine = true,
                                time = "الان"
                            )
                        )

                        messageText = ""
                    }
                },
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
}

@Composable
private fun ChatBubble(
    message: ChatMessage
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (message.isMine)
                Arrangement.End
            else
                Arrangement.Start
    ) {

        Column(
            horizontalAlignment =
                if (message.isMine)
                    Alignment.End
                else
                    Alignment.Start
        ) {

            Box(
                modifier = Modifier
                    .background(
                        if (message.isMine)
                            Color(0xFFE85D75)
                        else
                            Color.White,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    )
            ) {

                Text(
                    text = message.text,
                    fontSize = 15.sp,
                    color =
                        if (message.isMine)
                            Color.White
                        else
                            Color(0xFF402A30)
                )
            }

            Text(
                text = message.time,
                fontSize = 10.sp,
                color = Color(0xFF92777F),
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 2.dp
                )
            )
        }
    }
}
