package com.gazlaws.codeboard.ime

import android.inputmethodservice.Keyboard
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.gazlaws.codeboard.BooleanMap
import com.gazlaws.codeboard.R
import com.gazlaws.codeboard.sendKeyEventOnce

class ShiftKeyPressHandler(private val inputMethodService: CodeBoardIME) {
    var isShiftOn: Boolean = false
        private set
    private var isShiftLocked: Boolean = false
    private var lastShiftKeyPressed: Long = 0L

    private val currentInputConnection: InputConnection?
        get() = inputMethodService.currentInputConnection

    fun reset() {
        isShiftOn = false
        isShiftLocked = false
    }

    /**
     * Handles onKey event for meta keys like Shift, Control, etc.
     * Returns true if this method consumes the event.
     */
    fun onKey(primaryKey: Int): Boolean {
        if (primaryKey == Keycode.SHIFT) {
            onShiftKeyPressed()
            lastShiftKeyPressed = System.currentTimeMillis()
            return true
        }
        lastShiftKeyPressed = 0 // reset when another key is pressed
        return false
    }

    private fun onShiftKeyPressed() {
        val doubleShiftDurationMillis = System.currentTimeMillis() - lastShiftKeyPressed
        isShiftLocked = doubleShiftDurationMillis <= DOUBLE_SHIFT_MAX_DURATION_MILLIS

        val shiftKeyAction = if (isShiftOn) KeyEvent.ACTION_UP else KeyEvent.ACTION_DOWN
        currentInputConnection.sendKeyEventOnce(
            shiftKeyAction,
            KeyEvent.KEYCODE_SHIFT_LEFT,
            CodeBoardIME.MetaState.SHIFT_ON
        )

        isShiftOn = isShiftLocked || !isShiftOn
        updateViewByShiftKey()
    }

    fun releaseShiftKeyWhenNotLocked() {
        if (isShiftLocked) {
            return
        }
        isShiftOn = false
        currentInputConnection.sendKeyEventOnce(
            KeyEvent.ACTION_UP,
            KeyEvent.KEYCODE_SHIFT_LEFT,
            CodeBoardIME.MetaState.SHIFT_ON
        )

        updateViewByShiftKey()
    }

    fun updateViewByShiftKey() {
        val nonNullKeyboardView = inputMethodService.keyboardView ?: return
        nonNullKeyboardView.keyboard?.updateShiftState()
        nonNullKeyboardView.invalidateAllKeys()
    }

    fun getKeyStringWithShiftState(keyCode: Int): String? {
        val keyChar = keyCode.toChar()
        if (keyChar !in CHARACTER_WITH_SHIFT_MAP) {
            return null
        }
        return if (isShiftOn) CHARACTER_WITH_SHIFT_MAP[keyChar] else keyChar.toString()
    }

    private fun Keyboard.getKeyByKeyCode(keyCode: Int): Keyboard.Key? {
        // TODO: Optimise this with shiftKeyIndex for shift
        return keys.find { keyCode in it.codes }
    }

    private fun Keyboard.updateShiftState() {
        getKeyByKeyCode(Keycode.SHIFT)?.label = TEXT_SHIFT[isShiftOn]
        isShifted = isShiftOn
        val characterToResMap = CHARACTER_TO_RES_MAP[isShifted]
        keys.forEach { key ->
            val keyChar = key.codes.first().toChar()
            val iconRes = characterToResMap[keyChar]
            if (iconRes != null) {
                key.icon = ContextCompat.getDrawable(inputMethodService, iconRes)
                return@forEach
            }

            if (keyChar in CHARACTER_WITH_SHIFT_MAP) {
                key.label = if (isShifted) CHARACTER_WITH_SHIFT_MAP[keyChar] else keyChar.toString()
            }
        }
    }

    companion object {
        private const val DOUBLE_SHIFT_MAX_DURATION_MILLIS = 300L

        private val TEXT_SHIFT = BooleanMap("SHIFT", "Shift")
        private val CHARACTER_WITH_SHIFT_MAP: Map<Char, String> = mapOf(
            '+' to ".",
            '-' to "_",
            '*' to "^",
            '/' to "\\",
            '"' to "'",
            ':' to ";",
            '{' to "}",
            '[' to "]",
            '(' to ")",
            '!' to "#",

            '1' to "%",
            '2' to "<",
            '3' to ">",
            '4' to "?",
            '5' to "&",
            '6' to "|",
            '7' to "`",
            '8' to "~",
            '9' to "@",
            '0' to "$",

            'a' to "A",
            'b' to "B",
            'c' to "C",
            'd' to "D",
            'e' to "E",
            'f' to "F",
            'g' to "G",
            'h' to "H",
            'i' to "I",
            'j' to "J",
            'k' to "K",
            'l' to "L",
            'm' to "M",
            'n' to "N",
            'o' to "O",
            'p' to "P",
            'q' to "Q",
            'r' to "R",
            's' to "S",
            't' to "T",
            'u' to "U",
            'v' to "V",
            'w' to "W",
            'x' to "X",
            'y' to "Y",
            'z' to "Z"
        )

        private val CHARACTER_TO_RES_WITHOUT_SHIFT_MAP: Map<Char, Int> = mapOf(
            '+' to R.drawable.keyboard_s_plus,
            '-' to R.drawable.keyboard_s_minus,
            '*' to R.drawable.keyboard_s_multiply,
            '/' to R.drawable.keyboard_s_divide,
            '"' to R.drawable.keyboard_s_quote,
            ':' to R.drawable.keyboard_s_colon,
            '{' to R.drawable.keyboard_s_angle_bracket,
            '[' to R.drawable.keyboard_s_square_bracket,
            '(' to R.drawable.keyboard_s_bracket,

            '1' to R.drawable.keyboard_1,
            '2' to R.drawable.keyboard_2,
            '3' to R.drawable.keyboard_3,
            '4' to R.drawable.keyboard_4,
            '5' to R.drawable.keyboard_5,
            '6' to R.drawable.keyboard_6,
            '7' to R.drawable.keyboard_7,
            '8' to R.drawable.keyboard_8,
            '9' to R.drawable.keyboard_9,
            '0' to R.drawable.keyboard_0
        )

        private val CHARACTER_TO_RES_WITH_SHIFT_MAP: Map<Char, Int> = mapOf(
            '+' to R.drawable.keyboard_s_plus_shift,
            '-' to R.drawable.keyboard_s_minus_shift,
            '*' to R.drawable.keyboard_s_multiply_shift,
            '/' to R.drawable.keyboard_s_divide_shift,
            '"' to R.drawable.keyboard_s_quote_shift,
            ':' to R.drawable.keyboard_s_colon_shift,
            '{' to R.drawable.keyboard_s_angle_bracket_shift,
            '[' to R.drawable.keyboard_s_square_bracket_shift,
            '(' to R.drawable.keyboard_s_bracket_shift,

            '1' to R.drawable.keyboard_1_shift,
            '2' to R.drawable.keyboard_2_shift,
            '3' to R.drawable.keyboard_3_shift,
            '4' to R.drawable.keyboard_4_shift,
            '5' to R.drawable.keyboard_5_shift,
            '6' to R.drawable.keyboard_6_shift,
            '7' to R.drawable.keyboard_7_shift,
            '8' to R.drawable.keyboard_8_shift,
            '9' to R.drawable.keyboard_9_shift,
            '0' to R.drawable.keyboard_0_shift
        )

        private val CHARACTER_TO_RES_MAP: BooleanMap<Map<Char, Int>> =
            BooleanMap(CHARACTER_TO_RES_WITH_SHIFT_MAP, CHARACTER_TO_RES_WITHOUT_SHIFT_MAP)
    }
}
