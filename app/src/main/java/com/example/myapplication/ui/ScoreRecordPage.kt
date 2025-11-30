package com.example.myapplication.ui

// Androidx Compose 관련 라이브러리들을 가져옵니다. UI 레이아웃, виджет, 상태 관리 등에 사용됩니다.
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
// ViewModel을 Compose에서 사용하기 위한 라이브러리를 가져옵니다.
import androidx.lifecycle.viewmodel.compose.viewModel
// ScoreRecord 데이터 모델을 가져옵니다.
import com.example.myapplication.data.ScoreRecord
// 숫자 형식을 지정하기 위한 Java의 DecimalFormat 클래스를 가져옵니다.
import java.text.DecimalFormat

/**
 * 모의고사 성적을 표시하고 관리하는 메인 화면 Composable.
 *
 * @param onBack 뒤로가기 내비게이션 콜백.
 * @param viewModel [ScoreRecordViewModel] 인스턴스. `viewModel()`을 통해 주입됩니다.
 */
// Material3의 실험적인 API를 사용하도록 설정합니다.
@OptIn(ExperimentalMaterial3Api::class)
// Composable 함수 `ScoreRecordPage`를 선언합니다.
@Composable
fun ScoreRecordPage(
    onBack: () -> Unit, // 뒤로가기 동작을 처리할 함수를 인자로 받습니다.
    viewModel: ScoreRecordViewModel = viewModel() // `viewModel()` 헬퍼를 통해 ScoreRecordViewModel의 인스턴스를 주입받습니다.
) {
    // ViewModel의 `records` Flow를 구독하여 상태 변화를 감지하고 UI를 다시 그립니다.
    val records by viewModel.records.collectAsState()
    // 성적 추가 다이얼로그의 표시 여부를 관리하는 상태 변수를 선언합니다.
    var showAddDialog by remember { mutableStateOf(false) }

    // 머티리얼 디자인의 기본 레이아웃 구조를 제공하는 `Scaffold`를 사용합니다.
    Scaffold(
        topBar = {
            // 상단 앱 바를 설정합니다.ㅋ
            TopAppBar(
                title = { Text("모의고사 성적") }, // 앱 바의 제목을 설정합니다.
                navigationIcon = {
                    // 뒤로가기 버튼 아이콘을 설정합니다.
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "뒤로") }
                }
            )
        },
        floatingActionButton = {
            // 플로팅 액션 버튼을 설정합니다.
            FloatingActionButton(
                onClick = { showAddDialog = true }, // 버튼 클릭 시 성적 추가 다이얼로그를 표시합니다.
                containerColor = MaterialTheme.colorScheme.primary, // 버튼의 배경색을 앱 테마의 기본 색상으로 지정합니다.
                contentColor = MaterialTheme.colorScheme.onPrimary // 버튼 내부 아이콘의 색상을 앱 테마의 기본 색상 위에 오는 색으로 지정합니다.
            ) {
                Icon(Icons.Default.Add, "추가") // 추가 아이콘을 표시합니다.
            }
        }
    ) { padding -> // `Scaffold`의 컨텐츠 영역으로, 상단 앱 바 등에 의해 내용이 가려지지 않도록 `padding` 값을 제공합니다.
        // UI 요소들을 수직으로 배치하기 위해 `Column`을 사용합니다.
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // 통계 정보를 담은 카드를 수평으로 배치하기 위해 `Row`를 사용합니다.
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // 성적 기록이 비어있으면 평균을 0.0으로, 그렇지 않으면 `totalGrade`의 평균을 계산합니다.
                val avg = if (records.isEmpty()) 0.0 else records.map { it.totalGrade }.average()
                // 숫자 형식을 소수점 한 자리까지 표현하도록 설정합니다.
                val df = DecimalFormat("#.#")
                // 전체 기록 횟수를 표시하는 `StatCard`를 그립니다.
                StatCard("전체 기록", "${records.size}회", Color(0xFF10B981), Modifier.weight(1f))
                // 평균 등급을 표시하는 `StatCard`를 그립니다.
                StatCard("평균 등급", df.format(avg), Color(0xFF8B5CF6), Modifier.weight(1f))
            }

            // UI 요소들 사이에 20.dp 만큼의 수직 공간을 추가합니다.
            Spacer(modifier = Modifier.height(20.dp))

            // 성적 기록이 없을 경우
            if (records.isEmpty()) {
                // 화면 중앙에 안내 문구를 표시합니다.
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("아직 기록된 성적이 없습니다.", color = Color.Gray)
                }
            } else { // 성적 기록이 있을 경우
                // 스크롤 가능한 리스트를 표시하기 위해 `LazyColumn`을 사용합니다.
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // `records` 리스트의 각 항목에 대해 `ScoreCard`를 표시합니다. `key`를 사용해 항목의 고유성을 보장합니다.
                    items(records, key = { it.id }) { record ->
                        // `ScoreCard`를 그리고, 삭제 버튼 클릭 시 ViewModel의 `deleteRecord` 함수를 호출합니다.
                        ScoreCard(record, onDelete = { viewModel.deleteRecord(record) })
                    }
                }
            }
        }

        // `showAddDialog`가 `true`일 경우, 성적 추가 다이얼로그를 표시합니다.
        if (showAddDialog) {
            AddScoreDialog(
                onDismiss = { showAddDialog = false }, // 다이얼로그가 닫힐 때 `showAddDialog`를 `false`로 설정합니다.
                onAdd = { newRecord ->
                    // '추가' 버튼 클릭 시 ViewModel의 `addRecord` 함수를 호출하여 새 성적을 추가합니다.
                    viewModel.addRecord(newRecord)
                    // 다이얼로그를 닫습니다.
                    showAddDialog = false
                }
            )
        }
    }
}

/**
 * 통계 정보를 보여주는 카드 Composable.
 *
 * @param title 통계 항목의 제목 (예: "전체 기록").
 * @param value 표시될 값.
 * @param color 카드의 배경색.
 * @param modifier Composable에 적용할 [Modifier].
 */
@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    // 컨텐츠를 담는 머티리얼 디자인의 `Card` 컴포넌트를 사용합니다.
    Card(
        modifier = modifier, // 전달받은 `modifier`를 적용합니다.
        colors = CardDefaults.cardColors(containerColor = color), // 카드의 배경색을 설정합니다.
        shape = RoundedCornerShape(12.dp) // 카드의 모서리를 둥글게 처리합니다.
    ) {
        // 텍스트들을 수직으로 배치하기 위해 `Column`을 사용합니다.
        Column(modifier = Modifier.padding(16.dp)) {
            // 통계 제목을 표시합니다.
            Text(title, color = Color.White.copy(0.9f), fontSize = 14.sp)
            // 통계 값을 굵은 글씨체로 표시합니다.
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * 개별 성적 기록을 보여주는 카드 Composable.
 *
 * @param record 표시할 [ScoreRecord] 데이터.
 * @param onDelete 삭제 버튼 클릭 시 호출될 콜백.
 */
@Composable
fun ScoreCard(record: ScoreRecord, onDelete: () -> Unit) {
    // 성적 정보를 표시하기 위해 `Card` 컴포넌트를 사용합니다.
    Card(
        modifier = Modifier.fillMaxWidth(), // 너비를 화면에 꽉 채웁니다.
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // 카드에 그림자 효과를 줍니다.
    ) {
        // 내용들을 수직으로 배치하기 위해 `Column`을 사용합니다.
        Column(modifier = Modifier.padding(16.dp)) {
            // 제목과 삭제 버튼을 수평으로 배치하기 위해 `Row`를 사용합니다.
            Row(
                modifier = Modifier.fillMaxWidth(), // 너비를 꽉 채웁니다.
                horizontalArrangement = Arrangement.SpaceBetween, // 요소들을 양쪽 끝으로 정렬합니다.
                verticalAlignment = Alignment.CenterVertically // 요소들을 수직 방향으로 중앙에 정렬합니다.
            ) {
                // 시험 제목을 굵은 글씨체로 표시합니다.
                Text(record.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                // 삭제 아이콘 버튼을 표시하고, 클릭 시 `onDelete` 콜백을 호출합니다.
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "삭제", tint = Color.Gray)
                }
            }
            // 12.dp 만큼의 수직 공간을 추가합니다.
            Spacer(modifier = Modifier.height(12.dp))
            // 과목별 점수를 수평으로 배치하기 위해 `Row`를 사용합니다.
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                // 각 과목 점수를 표시하는 `ScoreItem`을 호출합니다.
                ScoreItem("국어", record.korean)
                ScoreItem("수학", record.math)
                ScoreItem("영어", record.english)
                ScoreItem("탐구1", record.science1)
                ScoreItem("탐구2", record.science2)
            }
        }
    }
}

/**
 * 과목별 점수를 표시하는 작은 Composable.
 *
 * @param subject 과목 이름.
 * @param score 과목 점수.
 */
@Composable
fun ScoreItem(subject: String, score: Int) {
    // 과목 이름과 점수를 수직으로, 그리고 가운데 정렬하여 배치하기 위해 `Column`을 사용합니다.
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 과목 이름을 회색으로 표시합니다.
        Text(subject, fontSize = 12.sp, color = Color.Gray)
        // 4.dp 만큼의 수직 공간을 추가합니다.
        Spacer(modifier = Modifier.height(4.dp))
        // 점수를 굵은 글씨체로 표시합니다.
        Text("$score", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

/**
 * 새로운 성적 기록을 추가하기 위한 다이얼로그 Composable.
 *
 * @param onDismiss 다이얼로그가 닫힐 때 호출될 콜백.
 * @param onAdd '추가' 버튼 클릭 시 호출될 콜백. 새로 생성된 [ScoreRecord]를 전달합니다.
 */
@OptIn(ExperimentalMaterial3Api::class) // Material3의 실험적인 API를 사용하도록 설정합니다.
@Composable
fun AddScoreDialog(onDismiss: () -> Unit, onAdd: (ScoreRecord) -> Unit) {
    // 각 입력 필드의 상태를 기억하고 관리하는 변수들을 선언합니다.
    var year by remember { mutableStateOf("2025") }
    var month by remember { mutableStateOf("6") }
    var korean by remember { mutableStateOf("") }
    var math by remember { mutableStateOf("") }
    var english by remember { mutableStateOf("") }
    var science1 by remember { mutableStateOf("") }
    var science2 by remember { mutableStateOf("") }

    // 모든 입력 필드가 비어있지 않은지 확인하는 변수입니다. '추가' 버튼의 활성화 상태를 제어합니다.
    val isFormValid = year.isNotBlank() && month.isNotBlank() && korean.isNotBlank() &&
            math.isNotBlank() && english.isNotBlank() && science1.isNotBlank() && science2.isNotBlank()

    // 사용자에게 정보를 요청하는 `AlertDialog`를 표시합니다.
    AlertDialog(
        onDismissRequest = onDismiss, // 다이얼로그 바깥을 클릭하거나 뒤로가기 버튼을 눌렀을 때 `onDismiss`를 호출합니다.
        title = { Text("성적 추가") }, // 다이얼로그의 제목을 설정합니다.
        text = { // 다이얼로그의 본문 내용을 설정합니다.
            // 스크롤이 가능한 입력 필드 목록을 만들기 위해 `LazyColumn`을 사용합니다.
            LazyColumn {
                item {
                    // 연도와 월 입력 필드를 수평으로 배치합니다.
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("연도") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = month, onValueChange = { month = it }, label = { Text("월") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                    // 8.dp 만큼의 수직 공간을 추가합니다.
                    Spacer(Modifier.height(8.dp))
                    // 각 과목의 등급을 입력받는 `OutlinedTextField`를 만듭니다. 키보드 타입을 숫자로 설정합니다.
                    OutlinedTextField(value = korean, onValueChange = { korean = it }, label = { Text("국어 등급") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = math, onValueChange = { math = it }, label = { Text("수학 등급") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = english, onValueChange = { english = it }, label = { Text("영어 등급") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = science1, onValueChange = { science1 = it }, label = { Text("탐구1 등급") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = science2, onValueChange = { science2 = it }, label = { Text("탐구2 등급") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            }
        },
        confirmButton = { // '확인' 역할을 하는 버튼을 설정합니다.
            Button(
                onClick = { // 버튼 클릭 시 실행될 로직입니다.
                    // 입력된 각 등급을 정수로 변환하여 합산합니다. 변환 실패 시 0을 사용합니다.
                    val total = (korean.toIntOrNull() ?: 0) + (math.toIntOrNull() ?: 0) + (english.toIntOrNull() ?: 0) + (science1.toIntOrNull() ?: 0) + (science2.toIntOrNull() ?: 0)
                    // 총점이 0보다 크면 5.0으로 나누어 평균을 계산합니다.
                    val avg = if (total > 0) total / 5.0 else 0.0
                    // `onAdd` 콜백 함수를 호출하여, 입력된 정보로 생성된 새로운 `ScoreRecord` 객체를 전달합니다.
                    onAdd(ScoreRecord(
                        id = System.currentTimeMillis().toString(), // 현재 시간을 고유 ID로 사용합니다.
                        title = "${year}년 ${month}월 모의고사", // 시험 제목을 형식에 맞게 생성합니다.
                        year = year.toIntOrNull() ?: 0, // 연도를 정수로 변환합니다.
                        month = month, // 월을 저장합니다.
                        korean = korean.toIntOrNull() ?: 0, // 국어 등급을 정수로 변환합니다.
                        math = math.toIntOrNull() ?: 0, // 수학 등급을 정수로 변환합니다.
                        english = english.toIntOrNull() ?: 0, // 영어 등급을 정수로 변환합니다.
                        science1 = science1.toIntOrNull() ?: 0, // 탐구1 등급을 정수로 변환합니다.
                        science2 = science2.toIntOrNull() ?: 0, // 탐구2 등급을 정수로 변환합니다.
                        totalGrade = avg // 계산된 평균 등급을 저장합니다.
                    ))
                },
                enabled = isFormValid // `isFormValid` 값에 따라 버튼의 활성화 여부를 결정합니다.
            ) {
                Text("추가") // 버튼에 표시될 텍스트입니다.
            }
        },
        dismissButton = { // '취소' 역할을 하는 버튼을 설정합니다.
            TextButton(onClick = onDismiss) { // 버튼 클릭 시 `onDismiss` 콜백을 호출하여 다이얼로그를 닫습니다.
                Text("취소") // 버튼에 표시될 텍스트입니다.
            }
        }
    )
}
