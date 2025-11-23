package com.example.myapplication.ui // 패키지 경로를 수정했습니다.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.myapp.ui.SubjectCardItem
import com.example.myapplication.data.Subject
import com.example.myapplication.data.subjectList
import com.example.myapplication.utils.ThemeMode

// 컴포저블 함수 시그니처를 NavController와 ViewModel을 받도록 변경했습니다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel() // ViewModel 주입
) {
    var showSettings by remember { mutableStateOf(false) }

    // 다크모드 상태를 ViewModel에서 가져옵니다.
    val themeMode by mainViewModel.themeMode.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("수능 AI 공부방", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Outlined.Settings, "설정")
                    }
                }
            )
        }
        // 이제 Scaffold의 배경색은 테마에 의해 자동으로 관리됩니다.
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp) // 콘텐츠 패딩 추가
        ) {
            item { WelcomeBanner() }
            item {
                // ScoreBanner 클릭 시 navController를 사용해 직접 이동합니다.
                ScoreBanner(onClick = { navController.navigate("scoreRecord") })
            }
            items(subjectList) { subject ->
                SubjectCardItem(
                    subject = subject,
                    onClick = {
                        navController.navigate("chat/${subject.id}")
                    }
                )
            }
        }

        if (showSettings) {
            ModalBottomSheet(onDismissRequest = { showSettings = false }) {
                SettingsSheetContent(
                    currentThemeMode = themeMode,
                    onThemeChange = { mode -> mainViewModel.setThemeMode(mode) },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("main") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

// 설정 시트 내용을 별도 컴포저블로 분리하여 가독성을 높였습니다.
@Composable
fun SettingsSheetContent(
    currentThemeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onLogout: () -> Unit
) {
    Column(modifier = Modifier
        .padding(20.dp)
        .navigationBarsPadding()) {
        Text("설정", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("다크모드", fontWeight = FontWeight.Medium)
                Text("어두운 테마 사용", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // 스위치의 checked 상태를 ViewModel의 상태와 동기화합니다.
            Switch(
                checked = currentThemeMode == ThemeMode.DARK,
                onCheckedChange = { isChecked ->
                    onThemeChange(if (isChecked) ThemeMode.DARK else ThemeMode.LIGHT)
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
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

@Composable
fun WelcomeBanner() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1690788210614-9052cffd8a14?q=80&w=1080",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xE610B981), Color(0xE606B6D4)))
                    )
            )
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterStart),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("오늘도 화이팅!", color = Color.White, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("어떤 과목 공부할까요?", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScoreBanner(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(0.2f), RoundedCornerShape(10.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.BarChart, null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("모의고사 성적", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("3월, 6월, 9월 성적 관리", color = Color.White.copy(0.8f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SubjectCard(subject: Subject, onClick: (Subject) -> Unit) {
    Card(
        onClick = { onClick(subject) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = subject.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(subject.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subject.description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Brush.linearGradient(subject.gradientBrush), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(subject.icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}
