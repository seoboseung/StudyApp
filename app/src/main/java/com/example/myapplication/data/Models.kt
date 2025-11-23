package com.example.myapplication.data
import kotlinx.serialization.Serializable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*

// 과목 데이터 모델
data class Subject(
    val id: String,
    val name: String,
    val description: String,
    val color: Color, // 아이콘 배경용 단색
    val gradientBrush: List<Color>, // 그라데이션용 색상 리스트
    val icon: ImageVector,
    val imageUrl: String
)

// 성적 데이터 모델
@Serializable
data class ScoreRecord(
    val id: String,val title: String, // title이 있어야 합니다.
    val year: Int,
    val month: String, // month는 String 타입이어야 합니다.
    val korean: Int,
    val math: Int,
    val english: Int,
    val science1: Int,
    val science2: Int,
    val totalGrade: Double
)

// 샘플 과목 데이터 (React 코드 기반 변환)
val subjectList = listOf(
    Subject("korean", "국어", "문학·문법·독서", Color(0xFFF43F5E), listOf(Color(0xFFF43F5E), Color(0xFFDB2777)), Icons.Default.MenuBook, "https://images.unsplash.com/photo-1712527320820-bec5fd19d69b?q=80&w=200"),
    Subject("math", "수학", "미적분·확통·기하", Color(0xFF3B82F6), listOf(Color(0xFF3B82F6), Color(0xFF4F46E5)), Icons.Default.Calculate, "https://images.unsplash.com/photo-1758685848895-e724272475d2?q=80&w=200"),
    Subject("english", "영어", "독해·문법·어휘", Color(0xFF10B981), listOf(Color(0xFF10B981), Color(0xFF0D9488)), Icons.Default.Public, "https://images.unsplash.com/photo-1539632346654-dd4c3cffad8c?q=80&w=200"),
    Subject("biology", "생명과학", "세포·유전·생태", Color(0xFF22C55E), listOf(Color(0xFF22C55E), Color(0xFF65A30D)), Icons.Default.Spa, "https://images.unsplash.com/photo-1636386689060-37d233b5d345?q=80&w=200"),
    Subject("earth", "지구과학", "지질·천문·대기", Color(0xFFF59E0B), listOf(Color(0xFFF59E0B), Color(0xFFEA580C)), Icons.Default.Terrain, "https://images.unsplash.com/photo-1533921482637-8e125577dde6?q=80&w=200")
)