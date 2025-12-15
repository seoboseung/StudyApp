package com.example.myapplication.ui
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.utils.ThemeManager
import com.example.myapplication.utils.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val themeManager = ThemeManager(application.applicationContext)


    val themeMode: StateFlow<ThemeMode> = themeManager.themeMode.stateIn(
        scope = viewModelScope,

        started = SharingStarted.WhileSubscribed(5000),

        initialValue = ThemeMode.SYSTEM
    )


    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeManager.setThemeMode(mode)
        }
    }
}
