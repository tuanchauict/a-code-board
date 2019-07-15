package com.tuanchauict.acb.ime

import android.inputmethodservice.Keyboard
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.tuanchauict.acb.BooleanMap
import com.tuanchauict.acb.MetaState
import com.tuanchauict.acb.R
import com.tuanchauict.acb.sendKeyEventOnce

/**
 * A handler which handlers shift key press and shift key states including caps lock states.
 */
class ShiftKeyPressHandler(private val inputMethodService: CodeBoardInputMethodService) {
    private val isShifted: Boolean
        get() = isShiftOn xor isCapOn
    private var isShiftOn: Boolean = false
    private var isCapOn: Boolean = false
    private var lastShiftKeyPressed: Long = 0L

    private val currentInputConnection: InputConnection?
        get() = inputMethodService.currentInputConnection

    fun reset() {
        isShiftOn = false
        isCapOn = false
    }

    /**
     * Handles onKey event for shift key.
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
        isShiftOn = !isShiftOn
        if (doubleShiftDurationMillis <= DOUBLE_SHIFT_MAX_DURATION_MILLIS) {
            isCapOn = !isCapOn
        }

        val shiftKeyAction = if (!isShifted) KeyEvent.ACTION_UP else KeyEvent.ACTION_DOWN
        currentInputConnection.sendKeyEventOnce(
            shiftKeyAction,
            KeyEvent.KEYCODE_SHIFT_LEFT,
            MetaState.SHIFT_ON
        )

        updateViewByShiftKey()
    }

    fun releaseShiftKeyWhenNotLocked() {
        if (isCapOn) {
            return
        }
        isShiftOn = false
        currentInputConnection.sendKeyEventOnce(
            KeyEvent.ACTION_UP,
            KeyEvent.KEYCODE_SHIFT_LEFT,
            MetaState.SHIFT_ON
        )

        updateViewByShiftKey()
    }

    fun updateViewByShiftKey() {
        val nonNullKeyboardView = inputMethodService.keyboardView ?: return
        nonNullKeyboardView.keyboard?.updateShiftState()
        nonNullKeyboardView.invalidateAllKeys()
    }

    fun getKeyStringWithShiftState(keyCode: Int, isWithReversedState: Boolean = false): String? {
        val keyChar = keyCode.toChar()
        if (keyChar !in CHARACTER_WITH_SHIFT_MAP) {
            return null
        }
        val isWithShiftKey = if (isWithReversedState) !isShifted else isShifted
        return if (isWithShiftKey) CHARACTER_WITH_SHIFT_MAP[keyChar] else keyChar.toString()
    }

    private fun Keyboard.updateShiftState() {
        isShifted = this@ShiftKeyPressHandler.isShifted
        val characterToResMap = CHARACTER_TO_RES_MAP[isShifted]
        keys.forEach { key ->
            if (key.isShiftKey()) {
                SHIFT_CAP_ICON_MAP[isCapOn][isShiftOn].let { key.setIconRes(it) }
                return@forEach
            }
            val keyChar = key.code.toChar()
            characterToResMap[key.code]?.also {
                key.setIconRes(it)
                return@forEach
            }

            if (keyChar in CHARACTER_WITH_SHIFT_MAP) {
                key.label = if (isShifted) CHARACTER_WITH_SHIFT_MAP[keyChar] else keyChar.toString()
            }
        }
    }

    private val Keyboard.Key.code: Int get() = codes.first()

    private fun Keyboard.Key.isShiftKey(): Boolean = code == Keycode.SHIFT

    private fun Keyboard.Key.setIconRes(@DrawableRes iconRes: Int) {
        icon = ContextCompat.getDrawable(inputMethodService, iconRes)
    }

    companion object {
        private const val DOUBLE_SHIFT_MAX_DURATION_MILLIS = 200L
        private val SHIFT_ICON_RES =
            BooleanMap(R.drawable.keyboard_f_shift_on, R.drawable.keyboard_f_shift_off)
        private val CAP_ICON_RES =
            BooleanMap(R.drawable.keyboard_f_cap_off, R.drawable.keyboard_f_cap_on)
        private val SHIFT_CAP_ICON_MAP = BooleanMap(CAP_ICON_RES, SHIFT_ICON_RES)
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

        private val CHARACTER_TO_RES_WITHOUT_SHIFT_MAP: Map<Int, Int> = mapOf(
            Keycode.SYMBOL_PLUS to R.drawable.keyboard_s_plus,
            Keycode.SYMBOL_MINUS to R.drawable.keyboard_s_minus,
            Keycode.SYMBOL_MULTIPLY to R.drawable.keyboard_s_multiply,
            Keycode.SYMBOL_DIVIDE to R.drawable.keyboard_s_divide,
            Keycode.SYMBOL_QUOTE to R.drawable.keyboard_s_quote,
            Keycode.SYMBOL_COLON to R.drawable.keyboard_s_colon,
            Keycode.SYMBOL_ANGLE_BRACKET to R.drawable.keyboard_s_angle_bracket,
            Keycode.SYMBOL_SQUARE_BRACKET to R.drawable.keyboard_s_square_bracket,
            Keycode.SYMBOL_BRACKET to R.drawable.keyboard_s_bracket,

            Keycode.DIGIT_1 to R.drawable.keyboard_1,
            Keycode.DIGIT_2 to R.drawable.keyboard_2,
            Keycode.DIGIT_3 to R.drawable.keyboard_3,
            Keycode.DIGIT_4 to R.drawable.keyboard_4,
            Keycode.DIGIT_5 to R.drawable.keyboard_5,
            Keycode.DIGIT_6 to R.drawable.keyboard_6,
            Keycode.DIGIT_7 to R.drawable.keyboard_7,
            Keycode.DIGIT_8 to R.drawable.keyboard_8,
            Keycode.DIGIT_9 to R.drawable.keyboard_9,
            Keycode.DIGIT_0 to R.drawable.keyboard_0,

            Keycode.FUNCTION_DPAD_LEFT to R.drawable.keyboard_f_left,
            Keycode.FUNCTION_DPAD_RIGHT to R.drawable.keyboard_f_right,
            Keycode.FUNCTION_DPAD_UP to R.drawable.keyboard_f_up,
            Keycode.FUNCTION_DPAD_DOWN to R.drawable.keyboard_f_down,

            Keycode.FUNCTION_MOVE_TO_FIRST to R.drawable.keyboard_f_move_first,
            Keycode.FUNCTION_MOVE_TO_LAST to R.drawable.keyboard_f_move_last,
            Keycode.FUNCTION_MOVE_END to R.drawable.keyboard_f_move_end,
            Keycode.FUNCTION_MOVE_HOME to R.drawable.keyboard_f_move_home
        )

        private val CHARACTER_TO_RES_WITH_SHIFT_MAP: Map<Int, Int> = mapOf(
            Keycode.SYMBOL_PLUS to R.drawable.keyboard_s_plus_shift,
            Keycode.SYMBOL_MINUS to R.drawable.keyboard_s_minus_shift,
            Keycode.SYMBOL_MULTIPLY to R.drawable.keyboard_s_multiply_shift,
            Keycode.SYMBOL_DIVIDE to R.drawable.keyboard_s_divide_shift,
            Keycode.SYMBOL_QUOTE to R.drawable.keyboard_s_quote_shift,
            Keycode.SYMBOL_COLON to R.drawable.keyboard_s_colon_shift,
            Keycode.SYMBOL_ANGLE_BRACKET to R.drawable.keyboard_s_angle_bracket_shift,
            Keycode.SYMBOL_SQUARE_BRACKET to R.drawable.keyboard_s_square_bracket_shift,
            Keycode.SYMBOL_BRACKET to R.drawable.keyboard_s_bracket_shift,

            Keycode.DIGIT_1 to R.drawable.keyboard_1_shift,
            Keycode.DIGIT_2 to R.drawable.keyboard_2_shift,
            Keycode.DIGIT_3 to R.drawable.keyboard_3_shift,
            Keycode.DIGIT_4 to R.drawable.keyboard_4_shift,
            Keycode.DIGIT_5 to R.drawable.keyboard_5_shift,
            Keycode.DIGIT_6 to R.drawable.keyboard_6_shift,
            Keycode.DIGIT_7 to R.drawable.keyboard_7_shift,
            Keycode.DIGIT_8 to R.drawable.keyboard_8_shift,
            Keycode.DIGIT_9 to R.drawable.keyboard_9_shift,
            Keycode.DIGIT_0 to R.drawable.keyboard_0_shift,

            Keycode.FUNCTION_DPAD_LEFT to R.drawable.keyboard_f_left_shift,
            Keycode.FUNCTION_DPAD_RIGHT to R.drawable.keyboard_f_right_shift,
            Keycode.FUNCTION_DPAD_UP to R.drawable.keyboard_f_up_shift,
            Keycode.FUNCTION_DPAD_DOWN to R.drawable.keyboard_f_down_shift,

            Keycode.FUNCTION_MOVE_TO_FIRST to R.drawable.keyboard_f_move_first_shift,
            Keycode.FUNCTION_MOVE_TO_LAST to R.drawable.keyboard_f_move_last_shift,
            Keycode.FUNCTION_MOVE_HOME to R.drawable.keyboard_f_move_home_shift,
            Keycode.FUNCTION_MOVE_END to R.drawable.keyboard_f_move_end_shift
        )

        private val CHARACTER_TO_RES_MAP: BooleanMap<Map<Int, Int>> =
            BooleanMap(CHARACTER_TO_RES_WITH_SHIFT_MAP, CHARACTER_TO_RES_WITHOUT_SHIFT_MAP)
    }
}
