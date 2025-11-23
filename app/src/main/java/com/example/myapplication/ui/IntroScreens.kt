// 1. "uiimport"를 "ui"로 수정합니다.
package com.example.myapplication.ui

// 2. 필요없는 import 문들을 정리합니다.
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import kotlinx.coroutines.delay

private enum class IntroStep {
    SPLASH,
    LOGIN
}

@Composable
fun IntroScreens(onNavigateToMain: () -> Unit) {
    var currentStep by remember { mutableStateOf(IntroStep.SPLASH) }

    LaunchedEffect(Unit) {
        delay(2500)
        currentStep = IntroStep.LOGIN
    }

    when (currentStep) {
        IntroStep.SPLASH -> SplashScreen()
        IntroStep.LOGIN -> KakaoLoginPage(onLogin = onNavigateToMain)
    }
}

/**
 * 스플래시 화면 UI
 */
@Composable
private fun SplashScreen() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Restart), label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Restart), label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF10B981), Color(0xFF06B6D4), Color(0xFF0891B2))
                )
            )
            .alpha(animateFloatAsState(if (isVisible) 1f else 0f, tween(500), label = "").value),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                Box(modifier = Modifier.matchParentSize().scale(pulseScale).alpha(pulseAlpha).background(Color.White.copy(alpha = 0.3f), CircleShape))
                Box(modifier = Modifier.clip(RoundedCornerShape(32.dp)).background(Color.White).padding(24.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "앱 로고", tint = Color(0xFF10B981), modifier = Modifier.size(64.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("수능 AI 공부방", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("당신의 수능 파트너", color = Color.White.copy(0.8f), fontSize = 16.sp)
        }
    }
}

/**
 * 카카오 로그인 페이지 UI
 */
@Composable
private fun KakaoLoginPage(onLogin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFBBF24), Color(0xFFF8B133), Color(0xFFF59E0B))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Logo",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("수능 AI 공부방", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("AI와 함께하는 수능 학습", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
            }

            // Login Card
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("로그인", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("카카오톡으로 간편하게 시작하세요", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                    Spacer(modifier = Modifier.height(32.dp))

                    // Kakao Login Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEE500))
                            .clickable(onClick = onLogin),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(id = R.drawable.kakao), contentDescription = "카카오 로고", modifier = Modifier.size(20.dp), tint = Color.Unspecified)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("카카오톡으로 시작하기", color = Color(0xFF191919), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    // Info Text
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "로그인하시면 이용약관 및 개인정보처리방침에\n동의하는 것으로 간주됩니다.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }

            // Features
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureItem(emoji = "📚", text = "5개 과목", modifier = Modifier.weight(1f))
                FeatureItem(emoji = "🤖", text = "AI 튜터", modifier = Modifier.weight(1f))
                FeatureItem(emoji = "📊", text = "성적 관리", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FeatureItem(emoji: String, text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, color = Color.White, fontSize = 12.sp)
        }
    }
}
