package com.example.myapplication.ui // MainActivity와 동일한 패키지 그룹에 있도록 설정

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.utils.ThemeManager // ThemeManager 경로
import com.example.myapplication.utils.ThemeMode // ThemeMode 경로
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // 1. ThemeManager 인스턴스 생성
    private val themeManager = ThemeManager(application.applicationContext)

    // 2. 현재 테마 모드를 UI가 관찰할 수 있도록 StateFlow로 노출
    val themeMode: StateFlow<ThemeMode> = themeManager.themeMode.stateIn(
        scope = viewModelScope,
        // 앱이 활성화되어 있는 동안에만 상태를 공유 (메모리 효율)
        started = SharingStarted.WhileSubscribed(5000),
        // 초기값은 시스템 설정
        initialValue = ThemeMode.SYSTEM
    )

    // 3. UI에서 호출하여 테마 모드를 변경하는 함수
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeManager.setThemeMode(mode)
        }
    }
}
