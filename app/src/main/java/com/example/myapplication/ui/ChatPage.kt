package com.example.myapplication.ui

// 1. 중복되거나 사용하지 않는 import 문 정리
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.BuildConfig
import com.example.myapplication.data.Subject
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.myapplication.data.Message // Message 데이터 클래스 import
import androidx.lifecycle.viewmodel.compose.viewModel // viewModel() 함수 import


/**
 * 채팅 메시지를 표현하는 데이터 클래스.
 * 이 파일 내에서 유일하게 선언하여 관리합니다.
 */


/**
 * 채팅 UI를 구성하는 메인 컴포저블
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(
    subject: Subject,
    onBack: () -> Unit,
    viewModel: ChatViewModel = viewModel() // ViewModel 주입
) {
    // ViewModel로부터 메시지 목록을 구독합니다.
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // 화면이 처음 그려지거나 과목이 변경될 때 ViewModel의 setupModel을 호출합니다.
    LaunchedEffect(subject) {
        viewModel.setupModel(subject)
    }

    // 메시지 목록이 변경될 때마다 마지막 메시지로 스크롤합니다.
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            ChatTopBar(subject = subject, onBack = onBack)
        },
        bottomBar = {
            ChatInputBar(
                value = inputText,
                onValueChange = { inputText = it },
                onSendClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message, subject = subject)
            }
        }
    }
}

/**
 * 채팅방 상단 바 컴포저블
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(subject: Subject, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(subject.name, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "뒤로가기")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
        modifier = Modifier.background(brush = Brush.horizontalGradient(colors = subject.gradientBrush))
    )
}

/**
 * 메시지 말풍선 컴포저블
 */
@Composable
private fun MessageBubble(message: Message, subject: Subject) {
    val horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Bottom
    ) {
        val bubbleShape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (message.isFromUser) 16.dp else 0.dp,
            bottomEnd = if (message.isFromUser) 0.dp else 16.dp
        )

        val backgroundBrush = if (message.isFromUser) {
            Brush.horizontalGradient(colors = subject.gradientBrush)
        } else {
            SolidColor(MaterialTheme.colorScheme.surfaceVariant)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(bubbleShape)
                .background(backgroundBrush)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isFromUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 하단 메시지 입력창 컴포저블
 */
@Composable
private fun ChatInputBar(value: String, onValueChange: (String) -> Unit, onSendClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("메시지를 입력하세요...") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = onSendClick,
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "전송",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
