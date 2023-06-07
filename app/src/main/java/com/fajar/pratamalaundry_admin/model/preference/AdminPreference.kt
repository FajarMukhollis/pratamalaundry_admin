package com.fajar.pratamalaundry_admin.model.preference

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.*
import com.fajar.pratamalaundry_admin.model.admin.AdminModel

class AdminPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun gerAdmin(): Flow<AdminModel> {
        return dataStore.data.map { preferences ->
            AdminModel(
                preferences[ID_KEY] ?: 0,
                preferences[USERNAME_KEY] ?: "",
                preferences[STATE_TOKEN] ?: "",
                preferences[STATE_KEY] ?: false,
            )
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[STATE_TOKEN] ?: ""
        }
    }

    fun getIsLogin(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[STATE_KEY] ?: false
        }
    }

    suspend fun saveUser(user: AdminModel) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.localId
            preferences[USERNAME_KEY] = user.username
            preferences[STATE_TOKEN] = user.token
            preferences[STATE_KEY] = user.isLogin
        }
    }
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[ID_KEY]
            preferences[USERNAME_KEY] = ""
            preferences[STATE_KEY] = false
            preferences[STATE_TOKEN] = ""
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: AdminPreference? = null
        private val ID_KEY = intPreferencesKey("id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val STATE_TOKEN = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): AdminPreference{
            return INSTANCE ?: synchronized(this) {
                val instance = AdminPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}