package com.example.myapplication.ui

// Androidx Compose UI 및 레이아웃 관련 라이브러리 임포트
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
// 머티리얼 디자인 아이콘 임포트 (추가, 뒤로가기, 삭제)
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
// 머티리얼3 디자인 컴포넌트 임포트
import androidx.compose.material3.*
// Compose 런타임 관련 라이브러리 임포트 (상태 관리 등)
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// ViewModel을 Compose에서 사용하기 위한 라이브러리 임포트
import androidx.lifecycle.viewmodel.compose.viewModel
// ScoreRecord 데이터 클래스 임포트
import com.example.myapplication.data.ScoreRecord
// 숫자 포맷팅을 위한 DecimalFormat 임포트
import java.text.DecimalFormat
// 현재 날짜를 가져오기 위한 Calendar 임포트
import java.util.Calendar

/**
 * 모의고사 성적을 표시하고 관리하는 메인 화면 Composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreRecordPage(
    onBack: () -> Unit,
    viewModel: ScoreRecordViewModel = viewModel()
) {
    val records by viewModel.records.collectAsState()

    val sortedRecords = remember(records) {
        val monthOrder = mapOf("3월 모의고사" to 3, "6월 모의고사" to 6, "9월 모의고사" to 9, "수능" to 11)
        records.sortedWith(
            compareByDescending<ScoreRecord> { it.year }
                .thenByDescending { record -> monthOrder[record.month] ?: 0 }
        )
    }

    val (showAddDialog, setShowAddDialog) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("모의고사 성적") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { setShowAddDialog(true) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, "추가")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            StatisticsSection(records)
            Spacer(modifier = Modifier.height(20.dp))
            ScoreListSection(sortedRecords, onDelete = { viewModel.deleteRecord(it) })
        }

        if (showAddDialog) {
            AddScoreDialog(
                existingRecords = records,
                onDismiss = { setShowAddDialog(false) },
                onAdd = {
                    viewModel.addRecord(it)
                    setShowAddDialog(false)
                }
            )
        }
    }
}

/** 통계 정보를 표시하는 섹션 */
@Composable
fun StatisticsSection(records: List<ScoreRecord>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val avg = if (records.isEmpty()) 0.0 else records.map { it.totalGrade }.average()
        val df = remember { DecimalFormat("#.#") }

        StatCard("전체 기록", "${records.size}회", Color(0xFF10B981), Modifier.weight(1f))
        StatCard("평균 등급", df.format(avg), Color(0xFF8B5CF6), Modifier.weight(1f))
    }
}

/** 성적 목록을 표시하는 섹션 */
@Composable
fun ScoreListSection(records: List<ScoreRecord>, onDelete: (ScoreRecord) -> Unit) {
    if (records.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("아직 기록된 성적이 없습니다.", color = Color.Gray)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(records, key = { it.id }) { record ->
                ScoreCard(record, onDelete = { onDelete(record) })
            }
        }
    }
}

/** 통계 정보를 보여주는 카드 */
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

/** 개별 성적 기록을 보여주는 카드 */
@Composable
fun ScoreCard(record: ScoreRecord, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(record.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "삭제", tint = Color.Gray)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                ScoreItem("국어", record.korean)
                ScoreItem("수학", record.math)
                ScoreItem("영어", record.english)
                ScoreItem("탐구1", record.science1)
                ScoreItem("탐구2", record.science2)
            }
        }
    }
}

/** 과목별 점수를 표시하는 작은 컴포넌트 */
@Composable
fun ScoreItem(subject: String, score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(subject, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text("$score", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

// 성적 추가 폼의 상태를 관리하는 데이터 클래스
data class AddScoreFormData(
    val year: String,
    val month: String,
    val korean: String = "",
    val math: String = "",
    val english: String = "",
    val science1: String = "",
    val science2: String = ""
) {
    fun isValid() = korean.isNotBlank() && math.isNotBlank() && english.isNotBlank() &&
            science1.isNotBlank() && science2.isNotBlank()

    fun toScoreRecord(): ScoreRecord {
        val total = (korean.toIntOrNull() ?: 0) + (math.toIntOrNull() ?: 0) +
                (english.toIntOrNull() ?: 0) + (science1.toIntOrNull() ?: 0) +
                (science2.toIntOrNull() ?: 0)
        val avg = if (total > 0) total / 5.0 else 0.0

        return ScoreRecord(
            id = System.currentTimeMillis().toString(),
            title = "${year}년 $month",
            year = year.toIntOrNull() ?: 0,
            month = month,
            korean = korean.toIntOrNull() ?: 0,
            math = math.toIntOrNull() ?: 0,
            english = english.toIntOrNull() ?: 0,
            science1 = science1.toIntOrNull() ?: 0,
            science2 = science2.toIntOrNull() ?: 0,
            totalGrade = avg
        )
    }
}

/** 새로운 성적 기록을 추가하기 위한 다이얼로그 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScoreDialog(
    existingRecords: List<ScoreRecord>,
    onDismiss: () -> Unit,
    onAdd: (ScoreRecord) -> Unit
) {
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR).toString() }
    val years = remember { (currentYear.toInt() downTo 1900).map { it.toString() } }
    val examTypes = remember { listOf("3월 모의고사", "6월 모의고사", "9월 모의고사", "수능") }
    val grades = remember { (1..9).map { it.toString() } }

    val (formData, setFormData) = remember { mutableStateOf(AddScoreFormData(year = currentYear, month = "6월 모의고사")) }
    val (errorMessage, setErrorMessage) = remember { mutableStateOf<String?>(null) }

    val updateFormData = { newFormData: AddScoreFormData ->
        setFormData(newFormData)
        setErrorMessage(null)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("성적 추가") },
        text = {
            LazyColumn {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DropdownSelector("연도", years, formData.year, { updateFormData(formData.copy(year = it)) }, Modifier.weight(1f))
                        DropdownSelector("구분", examTypes, formData.month, { updateFormData(formData.copy(month = it)) }, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                    DropdownSelector("국어 등급", grades, formData.korean, { updateFormData(formData.copy(korean = it)) }, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    DropdownSelector("수학 등급", grades, formData.math, { updateFormData(formData.copy(math = it)) }, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    DropdownSelector("영어 등급", grades, formData.english, { updateFormData(formData.copy(english = it)) }, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    DropdownSelector("탐구1 등급", grades, formData.science1, { updateFormData(formData.copy(science1 = it)) }, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    DropdownSelector("탐구2 등급", grades, formData.science2, { updateFormData(formData.copy(science2 = it)) }, Modifier.fillMaxWidth())

                    errorMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val isDuplicate = existingRecords.any { it.year.toString() == formData.year && it.month == formData.month }
                    if (isDuplicate) {
                        setErrorMessage("${formData.year}년 ${formData.month} 성적은 이미 등록되어 있습니다.")
                    } else {
                        onAdd(formData.toScoreRecord())
                    }
                },
                enabled = formData.isValid()
            ) {
                Text("추가")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
    )
}

/** 재사용 가능한 드롭다운 메뉴 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { setExpanded(it) }, modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = selectedOption.ifEmpty { " " },
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { setExpanded(false) }) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        setExpanded(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
