package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ScoreRecord
import com.example.myapplication.data.ScoreRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScoreRecordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScoreRepository(application.applicationContext)

    // Repository로부터 성적 목록을 StateFlow 형태로 UI에 노출
    val records: StateFlow<List<ScoreRecord>> = repository.scoreRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList() // 초기값은 빈 리스트
    )

    // 성적을 추가하는 함수
    fun addRecord(newRecord: ScoreRecord) {
        viewModelScope.launch {
            // 현재 목록에 새 기록을 추가하고 저장
            val updatedList = records.value + newRecord
            repository.saveScoreRecords(updatedList)
        }
    }

    // 성적을 삭제하는 함수
    fun deleteRecord(record: ScoreRecord) {
        viewModelScope.launch {
            // 현재 목록에서 해당 기록을 제거하고 저장
            val updatedList = records.value.toMutableList().apply { remove(record) }
            repository.saveScoreRecords(updatedList)
        }
    }
}
