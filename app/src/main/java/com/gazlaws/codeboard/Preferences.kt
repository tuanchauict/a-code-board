package com.gazlaws.codeboard

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.IntRange

class Preferences(context: Context) {
    private val sharePreferences: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var selectedKeyboardColorIndex: Int
        @IntRange(from = 0, to = 5L)
        get() = sharePreferences.getInt(KEY_SELECTED_COLOR_INDEX, 0)
        set(@IntRange(from = 0, to = 5L) value) =
            sharePreferences.edit().putInt(KEY_SELECTED_COLOR_INDEX, value).apply()

    var isPreviewEnabled: Boolean
        get() = sharePreferences.getBoolean(KEY_IS_PREVIEW_ENABLED, false)
        set(value) = sharePreferences.edit().putBoolean(KEY_IS_PREVIEW_ENABLED, value).apply()

    var isSoundOn: Boolean
        get() = sharePreferences.getBoolean(KEY_IS_SOUND_ON, true)
        set(value) = sharePreferences.edit().putBoolean(KEY_IS_SOUND_ON, value).apply()

    var isVibrateOn: Boolean
        get() = sharePreferences.getBoolean(KEY_IS_VIBRATE_ON, true)
        set(value) = sharePreferences.edit().putBoolean(KEY_IS_VIBRATE_ON, value).apply()

    var selectedKeyboardLayoutIndex: Int
        @IntRange(from = 0L, to = 1L)
        get() = sharePreferences.getInt(KEY_SELECTED_KEYBOARD_LAYOUT_INDEX, 0)
        set(@IntRange(from = 0L, to = 1L)value) =
            sharePreferences.edit().putInt(KEY_SELECTED_KEYBOARD_LAYOUT_INDEX, value).apply()

    var keyboardSize: Int
        @IntRange(from = 0L, to = 3L)
        get() = sharePreferences.getInt(KEY_KEYBOARD_SIZE, 2)
        set(@IntRange(from = 0L, to = 3L)value) =
            sharePreferences.edit().putInt(KEY_KEYBOARD_SIZE, value).apply()

    var isDpadOn: Boolean
        get() = sharePreferences.getBoolean(KEY_IS_DPAD_ON, true)
        set(value) = sharePreferences.edit().putBoolean(KEY_IS_DPAD_ON, value).apply()

    private companion object {
        const val FILE_NAME = "CodeBoard"

        const val KEY_SELECTED_COLOR_INDEX = "KEY_SELECTED_COLOR_INDEX"
        const val KEY_IS_PREVIEW_ENABLED = "IS_PREVIEW_ENABLED"
        const val KEY_IS_SOUND_ON = "IS_SOUND_ON"
        const val KEY_IS_VIBRATE_ON = "IS_VIBRATE_ON"

        const val KEY_SELECTED_KEYBOARD_LAYOUT_INDEX = "KEYBOARD_LAYOUT_INDEX"
        const val KEY_KEYBOARD_SIZE = "KEYBOARD_SIZE"
        const val KEY_IS_DPAD_ON = "IS_DPAD_ON"
    }
}
