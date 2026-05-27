package com.agenda.data.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("agenda_settings", Context.MODE_PRIVATE)

    val isDarkMode = MutableStateFlow<Boolean?>(
        if (prefs.contains("dark_mode")) prefs.getBoolean("dark_mode", false) else null
    )
    val defaultContractValue = MutableStateFlow<Int>(
        prefs.getInt("default_contract", 2000)
    )

    fun setDarkMode(isDark: Boolean) {
        prefs.edit { putBoolean("dark_mode", isDark) }
        isDarkMode.value = isDark
    }

    fun setDefaultContractValue(value: Int) {
        prefs.edit { putInt("default_contract", value) }
        defaultContractValue.value = value
    }
}