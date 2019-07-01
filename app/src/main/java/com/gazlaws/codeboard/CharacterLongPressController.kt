package com.gazlaws.codeboard

import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputConnection


class CharacterLongPressController(private val inputMethodService: InputMethodService) {
    private val uiHandler: Handler = Handler(Looper.getMainLooper())

    private val currentInputConnection: InputConnection?
        get() = inputMethodService.currentInputConnection

    var isLongPressSuccess: Boolean = false
        private set

    fun fire(primaryCode: Int) {
        isLongPressSuccess = false
        release()
        val character = primaryCode.toChar()
        if (character !in KEY_PAIR_MAP) {
            return
        }
        uiHandler.postDelayed({
            isLongPressSuccess = true
            val pairedCharacter = KEY_PAIR_MAP[character] ?: return@postDelayed
            currentInputConnection?.commitText(pairedCharacter, 1)
        }, DEFAULT_LONG_PRESS_DURATION_MILLIS)
    }

    fun release() = uiHandler.removeCallbacksAndMessages(null)

    companion object {
        private const val DEFAULT_LONG_PRESS_DURATION_MILLIS = 500L

        private val KEY_PAIR_MAP: Map<Char, String> = mapOf(
            '{' to "}",
            '}' to "{",
            '[' to "]",
            ']' to "[",
            '(' to ")",
            ')' to "(",
            '.' to ";",
            ';' to ".",
            '"' to "\'",
            '\'' to "\"",
            ':' to "/",
            '/' to ":"
        )
    }
}
