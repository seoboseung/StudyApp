package com.example.myapplication.ui

// ═══════════════════════════════════════════════════════════════════════════
// MainPage.kt - 수능 AI 공부방 메인 화면
// ═══════════════════════════════════════════════════════════════════════════
// 이 파일은 메인 화면과 관련된 모든 UI 컴포넌트를 포함합니다.
// ═══════════════════════════════════════════════════════════════════════════

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.data.Subject
import com.example.myapplication.data.subjectList
import com.example.myapplication.utils.ThemeMode

// ═══════════════════════════════════════════════════════════════════════════
// MainPage: 메인 화면 전체
// ═══════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    var showSettings by remember { mutableStateOf(false) }
    val themeMode by mainViewModel.themeMode.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "수능 AI 공부방",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "설정",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // 환영 배너
            item { WelcomeBanner() }

            // 성적 배너
            item {
                ScoreBanner(onClick = {
                    navController.navigate("scoreRecord")
                })
            }

            // 과목 카드들
            items(subjectList) { subject ->
                SubjectCardItem(
                    subject = subject,
                    onClick = {
                        navController.navigate("chat/${subject.id}")
                    }
                )
            }

            // 하단 힌트
            item { FooterHint() }
        }

        // 설정 창
        if (showSettings) {
            ModalBottomSheet(onDismissRequest = { showSettings = false }) {
                SettingsSheetContent(
                    currentThemeMode = themeMode,
                    onThemeChange = { mode -> mainViewModel.setThemeMode(mode) },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 설정 창 컴포넌트
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun SettingsSheetContent(
    currentThemeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .navigationBarsPadding()
    ) {
        Text("설정", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        // 다크모드 설정
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("다크모드", fontWeight = FontWeight.Medium)
                Text(
                    "어두운 테마 사용",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = currentThemeMode == ThemeMode.DARK,
                onCheckedChange = { isChecked ->
                    onThemeChange(if (isChecked) ThemeMode.DARK else ThemeMode.LIGHT)
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 로그아웃 버튼
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ExitToApp, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("로그아웃")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 환영 배너
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun WelcomeBanner() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF10B981),  // 초록
                            Color(0xFF3B82F6)   // 파랑
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // 왼쪽: 환영 메시지
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = "✨ 오늘도 화이팅!",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "어떤 과목 공부할까요?",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 오른쪽: '총 5과목' 뱃지
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("총 5과목", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 성적 배너
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun ScoreBanner(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8B5CF6)  // 보라색
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 차트 아이콘
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 중앙: 텍스트
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "모의고사 성적",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "3월, 6월, 9월 성적 관리",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }

            // 오른쪽: 화살표 아이콘
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 과목 카드
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun SubjectCardItem(
    subject: Subject,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 왼쪽: 과목 이미지
            AsyncImage(
                model = subject.imageUrl,
                contentDescription = subject.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 2. 중앙: 텍스트 정보
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subject.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "AI 선생님 >",
                    fontSize = 14.sp,
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.Medium
                )
            }

            // 3. 오른쪽: 과목 아이콘
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(subject.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = subject.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 하단 힌트
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun FooterHint() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "궁금한 과목을 선택해보세요",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}