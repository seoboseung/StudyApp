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
 *
 * @param onBack 뒤로가기 내비게이션 콜백.
 * @param viewModel [ScoreRecordViewModel] 인스턴스. `viewModel()`을 통해 주입됩니다.
 */
@OptIn(ExperimentalMaterial3Api::class) // Material3의 실험적인 API 사용을 허용합니다.
@Composable
fun ScoreRecordPage(
    onBack: () -> Unit, // 뒤로가기 액션을 처리할 함수를 매개변수로 받습니다.
    viewModel: ScoreRecordViewModel = viewModel() // viewModel() 헬퍼를 통해 ScoreRecordViewModel 인스턴스를 가져옵니다.
) {
    // viewModel의 records 상태를 구독하여 UI에 반영합니다.
    val records by viewModel.records.collectAsState()

    // records가 변경될 때만 정렬을 다시 수행하도록 remember로 최적화합니다.
    val sortedRecords = remember(records) {
        // 월별 정렬 순서를 정의합니다.
        val monthOrder = mapOf("3월 모의고사" to 3, "6월 모의고사" to 6, "9월 모의고사" to 9, "수능" to 11)
        // records를 연도 내림차순, 그 다음 월 내림차순으로 정렬합니다.
        records.sortedWith(
            compareByDescending<ScoreRecord> { it.year }
                .thenByDescending { record -> monthOrder[record.month] ?: 0 }
        )
    }

    // 성적 추가 다이얼로그의 표시 여부를 관리하는 상태 변수입니다.
    val (showAddDialog, setShowAddDialog) = remember { mutableStateOf(false) }

    // 머티리얼 디자인의 기본 레이아웃 구조인 Scaffold를 사용합니다.
    Scaffold(
        topBar = { // 상단 앱 바를 정의합니다.
            TopAppBar(
                title = { Text("모의고사 성적") }, // 앱 바의 제목을 설정합니다.
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로") } } // 뒤로가기 아이콘 버튼을 설정합니다.
            )
        },
        floatingActionButton = { // 플로팅 액션 버튼을 정의합니다.
            FloatingActionButton(
                onClick = { setShowAddDialog(true) }, // 버튼 클릭 시 성적 추가 다이얼로그를 띄웁니다.
                containerColor = MaterialTheme.colorScheme.primary, // 버튼의 배경색을 테마의 primary 색상으로 지정합니다.
                contentColor = MaterialTheme.colorScheme.onPrimary // 버튼 내부 아이콘/텍스트 색상을 지정합니다.
            ) {
                Icon(Icons.Default.Add, "추가") // 추가 아이콘을 표시합니다.
            }
        }
    ) { padding -> // Scaffold의 내부 컨텐츠 영역입니다. 상단 바 등에 가려지지 않도록 패딩 값을 제공합니다.
        // UI 요소들을 수직으로 배치하는 Column을 사용합니다.
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // 통계 정보를 표시하는 섹션을 호출합니다.
            StatisticsSection(records)
            // 통계 섹션과 성적 목록 사이에 20.dp의 공간을 둡니다.
            Spacer(modifier = Modifier.height(20.dp))
            // 정렬된 성적 목록을 표시하는 섹션을 호출합니다.
            ScoreListSection(sortedRecords, onDelete = { viewModel.deleteRecord(it) })
        }

        // showAddDialog가 true이면 성적 추가 다이얼로그를 표시합니다.
        if (showAddDialog) {
            AddScoreDialog(
                existingRecords = records, // 중복 확인을 위해 기존 기록을 전달합니다.
                onDismiss = { setShowAddDialog(false) }, // 다이얼로그가 닫힐 때의 동작을 정의합니다.
                onAdd = { // 성적이 추가될 때의 동작을 정의합니다.
                    viewModel.addRecord(it) // ViewModel에 새 성적 기록을 추가합니다.
                    setShowAddDialog(false) // 다이얼로그를 닫습니다.
                }
            )
        }
    }
}

/** 통계 정보를 표시하는 섹션 */
@Composable
fun StatisticsSection(records: List<ScoreRecord>) {
    // 통계 카드들을 수평으로 배치하기 위해 Row를 사용합니다.
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // 성적 기록이 비어있으면 0.0, 아니면 평균 등급을 계산합니다.
        val avg = if (records.isEmpty()) 0.0 else records.map { it.totalGrade }.average()
        // 소수점 한 자리까지 숫자를 포맷하기 위한 DecimalFormat을 기억합니다.
        val df = remember { DecimalFormat("#.#") }

        // "전체 기록" 통계 카드를 표시합니다.
        StatCard("전체 기록", "${records.size}회", Color(0xFF10B981), Modifier.weight(1f))
        // "평균 등급" 통계 카드를 표시합니다.
        StatCard("평균 등급", df.format(avg), Color(0xFF8B5CF6), Modifier.weight(1f))
    }
}

/** 성적 목록을 표시하는 섹션 */
@Composable
fun ScoreListSection(records: List<ScoreRecord>, onDelete: (ScoreRecord) -> Unit) {
    // 성적 기록이 비어있는 경우
    if (records.isEmpty()) {
        // 화면 중앙에 안내 문구를 표시합니다.
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("아직 기록된 성적이 없습니다.", color = Color.Gray)
        }
    } else { // 성적 기록이 있는 경우
        // 스크롤 가능한 리스트를 효율적으로 표시하기 위해 LazyColumn을 사용합니다.
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // records 리스트의 각 항목에 대해 ScoreCard를 표시합니다.
            items(records, key = { it.id }) { record ->
                ScoreCard(record, onDelete = { onDelete(record) })
            }
        }
    }
}

/** 통계 정보를 보여주는 카드 */
@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    // Card 컴포넌트를 사용하여 통계 정보를 감쌉니다.
    Card(
        modifier = modifier, // 외부에서 전달된 Modifier를 적용합니다.
        colors = CardDefaults.cardColors(containerColor = color), // 카드의 배경색을 설정합니다.
        shape = RoundedCornerShape(12.dp) // 카드의 모서리를 둥글게 만듭니다.
    ) {
        // 카드 내부의 텍스트들을 수직으로 배치합니다.
        Column(modifier = Modifier.padding(16.dp)) {
            // 통계 제목 텍스트를 표시합니다.
            Text(title, color = Color.White.copy(0.9f), fontSize = 14.sp)
            // 통계 값 텍스트를 굵게 표시합니다.
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** 개별 성적 기록을 보여주는 카드 */
@Composable
fun ScoreCard(record: ScoreRecord, onDelete: () -> Unit) {
    // Card 컴포넌트를 사용하여 개별 성적 기록을 감쌉니다.
    Card(
        modifier = Modifier.fillMaxWidth(), // 카드의 너비를 화면에 꽉 채웁니다.
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // 카드에 약간의 그림자 효과를 줍니다.
    ) {
        // 카드 내부 요소들을 수직으로, 12.dp 간격으로 배치합니다.
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // 시험 제목과 삭제 버튼을 수평으로 배치합니다.
            Row(
                modifier = Modifier.fillMaxWidth(), // Row의 너비를 꽉 채웁니다.
                horizontalArrangement = Arrangement.SpaceBetween, // 자식 요소들을 양쪽 끝에 배치합니다.
                verticalAlignment = Alignment.CenterVertically // 자식 요소들을 수직 중앙에 정렬합니다.
            ) {
                // 시험 제목을 굵게 표시합니다.
                Text(record.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                // 삭제 아이콘 버튼을 표시합니다.
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "삭제", tint = Color.Gray)
                }
            }
            // 과목별 점수들을 수평으로, 균등한 간격으로 배치합니다.
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
    // 과목명과 점수를 수직 중앙 정렬로 배치합니다.
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 과목명을 회색으로 표시합니다.
        Text(subject, fontSize = 12.sp, color = Color.Gray)
        // 과목명과 점수 사이에 4.dp의 공간을 둡니다.
        Spacer(modifier = Modifier.height(4.dp))
        // 점수를 굵게 표시합니다.
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
    // 모든 등급이 입력되었는지 확인하는 함수입니다.
    fun isValid() = korean.isNotBlank() && math.isNotBlank() && english.isNotBlank() &&
            science1.isNotBlank() && science2.isNotBlank()

    // 폼 데이터를 ScoreRecord 객체로 변환하는 함수입니다.
    fun toScoreRecord(): ScoreRecord {
        // 모든 과목의 등급을 합산합니다. 숫자가 아닌 경우 0으로 처리합니다.
        val total = (korean.toIntOrNull() ?: 0) + (math.toIntOrNull() ?: 0) +
                (english.toIntOrNull() ?: 0) + (science1.toIntOrNull() ?: 0) +
                (science2.toIntOrNull() ?: 0)
        // 5과목의 평균 등급을 계산합니다.
        val avg = if (total > 0) total / 5.0 else 0.0

        // ScoreRecord 객체를 생성하여 반환합니다.
        return ScoreRecord(
            id = System.currentTimeMillis().toString(), // 현재 시간을 고유 ID로 사용합니다.
            title = "${year}년 $month", // 제목을 생성합니다.
            year = year.toIntOrNull() ?: 0, // 연도를 정수로 변환합니다.
            month = month, // 월(구분)을 저장합니다.
            korean = korean.toIntOrNull() ?: 0, // 국어 등급을 저장합니다.
            math = math.toIntOrNull() ?: 0, // 수학 등급을 저장합니다.
            english = english.toIntOrNull() ?: 0, // 영어 등급을 저장합니다.
            science1 = science1.toIntOrNull() ?: 0, // 탐구1 등급을 저장합니다.
            science2 = science2.toIntOrNull() ?: 0, // 탐구2 등급을 저장합니다.
            totalGrade = avg // 평균 등급을 저장합니다.
        )
    }
}

/** 새로운 성적 기록을 추가하기 위한 다이얼로그 */
@OptIn(ExperimentalMaterial3Api::class) // Material3의 실험적인 API 사용을 허용합니다.
@Composable
fun AddScoreDialog(
    existingRecords: List<ScoreRecord>, // 중복 체크를 위한 기존 성적 목록
    onDismiss: () -> Unit, // 다이얼로그 닫기 콜백
    onAdd: (ScoreRecord) -> Unit // 성적 추가 콜백
) {
    // 현재 연도를 기억합니다.
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR).toString() }
    // 연도 목록 (현재 ~ 1900)을 기억합니다.
    val years = remember { (currentYear.toInt() downTo 1900).map { it.toString() } }
    // 시험 구분 목록을 기억합니다.
    val examTypes = remember { listOf("3월 모의고사", "6월 모의고사", "9월 모의고사", "수능") }
    // 등급 목록 (1 ~ 9)을 기억합니다.
    val grades = remember { (1..9).map { it.toString() } }

    // 폼 데이터 상태를 관리합니다. 초기값으로 현재 연도와 "6월 모의고사"를 설정합니다.
    val (formData, setFormData) = remember { mutableStateOf(AddScoreFormData(year = currentYear, month = "6월 모의고사")) }
    // 에러 메시지 상태를 관리합니다.
    val (errorMessage, setErrorMessage) = remember { mutableStateOf<String?>(null) }

    // formData를 업데이트하고 에러 메시지를 초기화하는 함수입니다.
    val updateFormData = { newFormData: AddScoreFormData ->
        setFormData(newFormData)
        setErrorMessage(null) // 입력값이 변경되면 에러 메시지를 초기화합니다.
    }

    // AlertDialog를 사용하여 성적 추가 UI를 구성합니다.
    AlertDialog(
        onDismissRequest = onDismiss, // 다이얼로그 바깥 영역 클릭 시 닫기 콜백을 호출합니다.
        title = { Text("성적 추가") }, // 다이얼로그 제목입니다.
        text = { // 다이얼로그 본문 내용입니다.
            LazyColumn { // 스크롤 가능한 컨텐츠를 위해 LazyColumn을 사용합니다.
                item {
                    // 연도와 시험 구분을 선택하는 드롭다운을 수평으로 배치합니다.
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DropdownSelector("연도", years, formData.year, { updateFormData(formData.copy(year = it)) }, Modifier.weight(1f))
                        DropdownSelector("구분", examTypes, formData.month, { updateFormData(formData.copy(month = it)) }, Modifier.weight(1f))
                    }
                    // 각 UI 요소 사이에 8.dp 간격을 줍니다.
                    Spacer(Modifier.height(8.dp))
                    // 각 과목별 등급을 선택하는 드롭다운 메뉴들입니다.
                    DropdownSelector("국어 등급", grades, formData.korean, { updateFormData(formData.copy(korean = it)) }, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    DropdownSelector("수학 등급", grades, formData.math, { updateFormData(formData.copy(math = it)) }, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    DropdownSelector("영어 등급", grades, formData.english, { updateFormData(formData.copy(english = it)) }, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    DropdownSelector("탐구1 등급", grades, formData.science1, { updateFormData(formData.copy(science1 = it)) }, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    DropdownSelector("탐구2 등급", grades, formData.science2, { updateFormData(formData.copy(science2 = it)) }, Modifier.fillMaxWidth())

                    // 에러 메시지가 있을 경우 표시합니다.
                    errorMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = { // 확인 버튼을 정의합니다.
            Button(
                onClick = {
                    // 선택한 연도와 월(구분)이 이미 존재하는지 확인합니다.
                    val isDuplicate = existingRecords.any { it.year.toString() == formData.year && it.month == formData.month }
                    // 중복된 경우 에러 메시지를 설정합니다.
                    if (isDuplicate) {
                        setErrorMessage("${formData.year}년 ${formData.month} 성적은 이미 등록되어 있습니다.")
                    } else { // 중복되지 않은 경우
                        // onAdd 콜백을 호출하여 성적을 추가합니다.
                        onAdd(formData.toScoreRecord())
                    }
                },
                enabled = formData.isValid() // 폼이 유효할 때만 버튼을 활성화합니다.
            ) {
                Text("추가")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } } // 취소 버튼을 정의합니다.
    )
}

/** 재사용 가능한 드롭다운 메뉴 */
@OptIn(ExperimentalMaterial3Api::class) // Material3의 실험적인 API 사용을 허용합니다.
@Composable
fun DropdownSelector(
    label: String, // 드롭다운의 라벨
    options: List<String>, // 선택할 수 있는 옵션 목록
    selectedOption: String, // 현재 선택된 옵션
    onOptionSelected: (String) -> Unit, // 옵션 선택 시 호출될 콜백
    modifier: Modifier = Modifier
) {
    // 드롭다운 메뉴의 확장(열림/닫힘) 상태를 관리합니다.
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    // 드롭다운 메뉴의 전체적인 레이아웃을 잡는 Box입니다.
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { setExpanded(it) }, modifier = modifier) {
        // 현재 선택된 값을 보여주는 텍스트 필드입니다.
        OutlinedTextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable), // 이 필드가 드롭다운 메뉴의 기준점(앵커)임을 알립니다.
            readOnly = true, // 사용자가 직접 텍스트를 입력할 수 없도록 읽기 전용으로 설정합니다.
            value = selectedOption.ifEmpty { " " }, // 선택된 값이 없으면 라벨이 UI 상에서 겹치지 않도록 공백을 표시합니다.
            onValueChange = {}, // 읽기 전용이므로 비워둡니다.
            label = { Text(label) }, // 텍스트 필드의 라벨입니다.
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, // 열림/닫힘 상태에 따라 아이콘 모양이 바뀌는 후행 아이콘입니다.
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        // 실제 옵션들이 표시되는 드롭다운 메뉴입니다.
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { setExpanded(false) }) {
            // 옵션 목록의 각 항목에 대해 메뉴 아이템을 생성합니다.
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) }, // 메뉴 아이템에 표시될 텍스트입니다.
                    onClick = { // 메뉴 아이템을 클릭했을 때의 동작입니다.
                        onOptionSelected(selectionOption) // 선택된 옵션을 콜백으로 전달합니다.
                        setExpanded(false) // 메뉴를 닫습니다.
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding // 기본 컨텐츠 패딩을 사용합니다.
                )
            }
        }
    }
}
