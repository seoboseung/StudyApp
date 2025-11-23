package com.example.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// 앱 컨텍스트를 통해 DataStore 인스턴스를 싱글톤으로 관리합니다.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scores")

class ScoreRepository(private val context: Context) {

    companion object {
        // 성적 목록을 JSON 문자열 형태로 저장할 키
        private val SCORE_RECORDS_KEY = stringPreferencesKey("score_records")
    }

    // 저장된 성적 목록을 Flow 형태로 가져오기
    val scoreRecords: Flow<List<ScoreRecord>> = context.dataStore.data.map { preferences ->
        // 키에 해당하는 값이 없으면 빈 목록("[]")을 기본값으로 사용
        val jsonString = preferences[SCORE_RECORDS_KEY] ?: "[]"
        // JSON 문자열을 List<ScoreRecord> 객체로 변환
        Json.decodeFromString<List<ScoreRecord>>(jsonString)
    }

    // 새로운 성적 목록을 DataStore에 저장하는 함수
    suspend fun saveScoreRecords(records: List<ScoreRecord>) {
        // List<ScoreRecord> 객체를 JSON 문자열로 변환
        val jsonString = Json.encodeToString(records)
        context.dataStore.edit { settings ->
            settings[SCORE_RECORDS_KEY] = jsonString
        }
    }
}
