package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.ScoreRecord
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreRecordPage(
    onBack: () -> Unit,
    viewModel: ScoreRecordViewModel = viewModel() // ViewModel 주입
) {
    // ViewModel로부터 성적 목록 상태를 가져옵니다.
    val records by viewModel.records.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("모의고사 성적") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "뒤로") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, "추가")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // 통계 카드
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val avg = if (records.isEmpty()) 0.0 else records.map { it.totalGrade }.average()
                val df = DecimalFormat("#.#")
                StatCard("전체 기록", "${records.size}회", Color(0xFF10B981), Modifier.weight(1f))
                StatCard("평균 등급", df.format(avg), Color(0xFF8B5CF6), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (records.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("아직 기록된 성적이 없습니다.", color = Color.Gray)
                }
            } else {
                // ViewModel의 records 상태를 기반으로 리스트를 그립니다.
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(records, key = { it.id }) { record ->
                        // 삭제 버튼 클릭 시 ViewModel의 deleteRecord 함수 호출
                        ScoreCard(record, onDelete = { viewModel.deleteRecord(record) })
                    }
                }
            }
        }

        if (showAddDialog) {
            AddScoreDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { newRecord ->
                    // 추가 버튼 클릭 시 ViewModel의 addRecord 함수 호출
                    viewModel.addRecord(newRecord)
                    showAddDialog = false
                }
            )
        }
    }
}

// 통계 정보를 보여주는 카드
@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.White.copy(0.9f), fontSize = 14.sp)
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// 개별 성적 기록을 보여주는 카드
@Composable
fun ScoreCard(record: ScoreRecord, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 'examName' 대신 'title'을 사용합니다.
                Text(record.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "삭제", tint = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                ScoreItem("국어", record.korean)
                ScoreItem("수학", record.math)
                ScoreItem("영어", record.english)
                // 'research1', 'research2' 대신 'science1', 'science2'를 사용합니다.
                ScoreItem("탐구1", record.science1)
                ScoreItem("탐구2", record.science2)
            }
        }
    }
}

// 과목별 점수를 표시하는 작은 컴포넌트
@Composable
fun ScoreItem(subject: String, score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(subject, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text("$score", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

// 성적 추가를 위한 다이얼로그
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScoreDialog(onDismiss: () -> Unit, onAdd: (ScoreRecord) -> Unit) {
    var year by remember { mutableStateOf("2025") }
    var month by remember { mutableStateOf("6") }
    var korean by remember { mutableStateOf("") }
    var math by remember { mutableStateOf("") }
    var english by remember { mutableStateOf("") }
    var science1 by remember { mutableStateOf("") } // 'research1' -> 'science1'
    var science2 by remember { mutableStateOf("") } // 'research2' -> 'science2'

    val isFormValid = year.isNotBlank() && month.isNotBlank() && korean.isNotBlank() &&
            math.isNotBlank() && english.isNotBlank() && science1.isNotBlank() && science2.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("성적 추가") },
        text = {
            LazyColumn {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("연도") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = month, onValueChange = { month = it }, label = { Text("월") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = korean, onValueChange = { korean = it }, label = { Text("국어 등급") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = math, onValueChange = { math = it }, label = { Text("수학 등급") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = english, onValueChange = { english = it }, label = { Text("영어 등급") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    // 라벨과 변수명을 '탐구'로 통일합니다.
                    OutlinedTextField(value = science1, onValueChange = { science1 = it }, label = { Text("탐구1 등급") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = science2, onValueChange = { science2 = it }, label = { Text("탐구2 등급") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val total = (korean.toIntOrNull() ?: 0) + (math.toIntOrNull() ?: 0) + (english.toIntOrNull() ?: 0) + (science1.toIntOrNull() ?: 0) + (science2.toIntOrNull() ?: 0)
                    val avg = if (total > 0) total / 5.0 else 0.0
                    // ScoreRecord 생성 시 'Models.kt'의 정의에 맞춥니다.
                    onAdd(ScoreRecord(
                        id = System.currentTimeMillis().toString(),
                        title = "${year}년 ${month}월 모의고사",
                        year = year.toIntOrNull() ?: 0,
                        month = month,
                        korean = korean.toIntOrNull() ?: 0,
                        math = math.toIntOrNull() ?: 0,
                        english = english.toIntOrNull() ?: 0,
                        science1 = science1.toIntOrNull() ?: 0,
                        science2 = science2.toIntOrNull() ?: 0,
                        totalGrade = avg
                    ))
                },
                enabled = isFormValid
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
