package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.subjectList
import com.example.myapplication.ui.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.ThemeMode

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // 스플래시 스크린 적용
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by mainViewModel.themeMode.collectAsState()

            val useDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = useDarkTheme) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {

                    // 1. 로그인 화면 경로
                    composable("login") {
                        IntroScreens(
                            onNavigateToMain = {
                                // 로그인 성공 시 메인 화면으로 이동하고,
                                // 로그인 화면은 스택에서 제거합니다.
                                navController.navigate("main") {
                                    popUpTo("login") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    // 2. 메인 화면 경로
                    composable("main") {
                        MainPage(
                            navController = navController,
                            mainViewModel = mainViewModel
                        )
                    }

                    // 3. 채팅방 경로
                    composable("chat/{subjectId}") { backStackEntry ->
                        val subjectId = backStackEntry.arguments?.getString("subjectId")
                        val subject = subjectList.find { it.id == subjectId }

                        if (subject != null) {
                            ChatPage(
                                subject = subject,
                                onBack = { navController.popBackStack() }
                            )
                        } else {
                            navController.popBackStack()
                        }
                    }

                    // 4. 성적 기록 경로
                    composable("scoreRecord") {
                        ScoreRecordPage(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
