package com.igaming.localarcade.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.scoreDataStore by preferencesDataStore(name = "local_arcade_scores")

class ScoreRepository(private val context: Context) {
    fun bestScore(key: String): Flow<Int> {
        val prefKey = intPreferencesKey(key)
        return context.scoreDataStore.data.map { preferences -> preferences[prefKey] ?: 0 }
    }

    suspend fun submitScore(key: String, score: Int) {
        val prefKey = intPreferencesKey(key)
        context.scoreDataStore.edit { preferences ->
            val current = preferences[prefKey] ?: 0
            if (score > current) {
                preferences[prefKey] = score
            }
        }
    }
}
