package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.subjectList
import com.example.myapplication.utils.ThemeMode

/**
 * 메인 페이지
 * - 과목 목록 표시
 * - 환영 배너 및 성적 배너
 * - 설정 기능 (다크모드, 로그아웃)
 */
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
                        "수능 AI 공부방",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Outlined.Settings, "설정")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            // 환영 배너
            item { 
                WelcomeBanner() 
            }
            
            // 성적 배너
            item {
                ScoreBanner(
                    onClick = { navController.navigate("scoreRecord") }
                )
            }
            
            // 과목 목록
            items(subjectList) { subject ->
                SubjectCard(
                    subject = subject,
                    onClick = {
                        navController.navigate("chat/${subject.id}")
                    }
                )
            }
        }

        // 설정 바텀시트
        if (showSettings) {
            ModalBottomSheet(
                onDismissRequest = { showSettings = false }
            ) {
                SettingsSheetContent(
                    currentThemeMode = themeMode,
                    onThemeChange = { mode -> 
                        mainViewModel.setThemeMode(mode) 
                    },
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

/**
 * 설정 바텀시트 내용
 */
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
        Text(
            "설정",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(20.dp))

        // 다크모드 설정
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "다크모드",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "어두운 테마 사용",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = currentThemeMode == ThemeMode.DARK,
                onCheckedChange = { isChecked ->
                    onThemeChange(
                        if (isChecked) ThemeMode.DARK 
                        else ThemeMode.LIGHT
                    )
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
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("로그아웃")
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}
