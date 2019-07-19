package com.tuanchauict.acb

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context) {
    private val sharePreferences: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var isFirstTimeAppOpen: Boolean
        get() = sharePreferences.getBoolean(KEY_IS_FIRST_TIME_APP_OPEN, true)
        set(value) = sharePreferences.edit(KEY_IS_FIRST_TIME_APP_OPEN, value)

    var isPreviewEnabled: Boolean
        get() = sharePreferences.getBoolean(KEY_IS_PREVIEW_ENABLED, false)
        set(value) = sharePreferences.edit(KEY_IS_PREVIEW_ENABLED, value)

    var isSoundOn: Boolean
        get() = sharePreferences.getBoolean(KEY_IS_SOUND_ON, true)
        set(value) = sharePreferences.edit(KEY_IS_SOUND_ON, value)

    var soundVolume: Int
        get() = sharePreferences.getInt(KEY_SOUND_VOLUME, 50)
        set(value) = sharePreferences.edit(KEY_SOUND_VOLUME, value)

    var isVibrateOn: Boolean
        get() = sharePreferences.getBoolean(KEY_IS_VIBRATE_ON, true)
        set(value) = sharePreferences.edit(KEY_IS_VIBRATE_ON, value)

    var vibrationStrength: Int
        get() = sharePreferences.getInt(KEY_VIBRATION_STRENGTH, 20)
        set(value) = sharePreferences.edit(KEY_VIBRATION_STRENGTH, value)

    var tabMode: TabMode
        get() = when (sharePreferences.getInt(KEY_TAB_MODE, 2)) {
            0 -> TabMode.TAB
            1 -> TabMode.SPACE_2
            2 -> TabMode.SPACE_4
            3 -> TabMode.SPACE_8
            else -> TabMode.SPACE_4
        }
        set(tabMode) = sharePreferences.edit(KEY_TAB_MODE, tabMode.value)

    private fun SharedPreferences.edit(key: String, value: Boolean) {
        edit().putBoolean(key, value).apply()
    }

    private fun SharedPreferences.edit(key: String, value: Int) {
        edit().putInt(key, value).apply()
    }

    enum class TabMode(val value: Int, val text: String) {
        TAB(0, "\t"),
        SPACE_2(1, "  "),
        SPACE_4(2, "    "),
        SPACE_8(3, "        ")
    }

    private companion object {
        const val FILE_NAME = "CodeBoard"

        const val KEY_IS_FIRST_TIME_APP_OPEN = "IS_FIRST_TIME_APP_OPEN"

        const val KEY_IS_PREVIEW_ENABLED = "IS_PREVIEW_ENABLED"
        const val KEY_IS_SOUND_ON = "IS_SOUND_ON"
        const val KEY_SOUND_VOLUME = "SOUND_VOLUME"
        const val KEY_IS_VIBRATE_ON = "IS_VIBRATE_ON"
        const val KEY_VIBRATION_STRENGTH = "VIBRATION_STRENGTH"

        const val KEY_TAB_MODE = "tab_mode"
    }
}
