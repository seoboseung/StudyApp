package com.example.myapp.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.data.Subject
import com.example.myapplication.data.subjectList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    onSubjectClick: (Subject) -> Unit
) {
    Scaffold(
        topBar = {
            // 상단바: 수능 AI 공부방 + 설정 아이콘
            CenterAlignedTopAppBar(
                title = { Text("수능 AI 공부방", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
                actions = {
                    IconButton(onClick = { /* 설정 이동 */ }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "설정", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5) // 전체 배경 회색빛
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // 아이템 간 간격
        ) {
            // 1. 상단 응원 배너 (초록-파랑 그라데이션)
            item {
                TopWelcomeBanner()
            }

            // 2. 모의고사 성적 배너 (보라색)
            item {
                ScoreRecordBanner()
            }

            // 3. 과목 리스트
            items(subjectList) { subject ->
                SubjectCardItem(subject = subject, onClick = { onSubjectClick(subject) })
            }

            // 4. 하단 푸터
            item {
                FooterHint()
                Spacer(modifier = Modifier.height(20.dp)) // 바닥 여백
            }
        }
    }
}

// --- 컴포넌트들 (MainPage.kt 파일 아래에 같이 두거나 별도 파일로 분리해도 됩니다) ---

@Composable
fun TopWelcomeBanner() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(100.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF10B981), Color(0xFF3B82F6)) // Green to Blue
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text("✨ 오늘도 화이팅!", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("어떤 과목 공부할까요?", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // 오른쪽 '총 5과목' 뱃지
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("총 5과목", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ScoreRecordBanner() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8B5CF6)), // 보라색
        modifier = Modifier.fillMaxWidth().clickable { /* 성적 페이지 이동 */ }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("모의고사 성적", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("3월, 6월, 9월 성적 관리", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun SubjectCardItem(subject: Subject, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 왼쪽 이미지
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

            // 2. 가운데 텍스트
            Column(modifier = Modifier.weight(1f)) {
                Text(subject.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(subject.description, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("AI 선생님 >", fontSize = 14.sp, color = Color(0xFF059669), fontWeight = FontWeight.Medium) // 초록색 텍스트
            }

            // 3. 오른쪽 아이콘
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(subject.color, CircleShape), // 과목별 색상 적용
                contentAlignment = Alignment.Center
            ) {
                Icon(subject.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun FooterHint() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("궁금한 과목을 선택해보세요", color = Color.Gray, fontSize = 14.sp)
    }
}