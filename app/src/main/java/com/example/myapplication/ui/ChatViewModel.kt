package com.example.myapplication.ui

import android.util.Log // Log import 추가
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.BuildConfig
import com.example.myapplication.data.Message
import com.example.myapplication.data.Subject
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private var generativeModel: GenerativeModel? = null

    fun setupModel(subject: Subject) {
        _messages.value = listOf(Message("안녕하세요! ${subject.name} 과목에 대해 무엇이든 물어보세요.", false))

        val prompt = """
                당신은 "${subject.name}" 과목을 가르치는 친절하고 유능한 수능 전문 AI 튜터입니다.
                모든 답변은 다음 규칙을 반드시 지켜주세요:
                1. 항상 학생의 눈높이에 맞춰 쉽고 명확하게 설명합니다.
                2. 존댓말을 사용하며, 학생을 격려하는 따뜻한 말투를 유지합니다.
                3. 질문과 관련 없는 내용은 답변하지 않습니다.
                4. 모든 답변은 한국어로만 제공합니다.
                5. 답변은 핵심만 간결하게 요약하여 2~3문장 이내로 끝냅니다.
            """.trimIndent()

        generativeModel = GenerativeModel(
            // ▼▼▼ 모델 이름을 "gemini-2.5-flash"로 수정 ▼▼▼
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = content { text(prompt) }
        )
    }

    fun sendMessage(userMessageText: String) {
        _messages.value += Message(userMessageText, true)

        viewModelScope.launch {
            try {
                val response = generativeModel?.generateContent(userMessageText)
                response?.text?.let { aiResponse ->
                    _messages.value += Message(aiResponse, false)
                }
            } catch (e: Exception) {
                // 오류 발생 시, 좀 더 자세한 원인을 파악하기 위해 로그를 추가합니다.
                Log.e("ChatViewModel", "API Error: ", e)
                _messages.value += Message("죄송해요, 답변을 생성하는 중 오류가 발생했어요. 다시 시도해주세요.", false)
            }
        }
    }
}
