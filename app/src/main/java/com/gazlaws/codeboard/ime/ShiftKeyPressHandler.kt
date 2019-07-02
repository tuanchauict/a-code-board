package com.gazlaws.codeboard.ime

import android.inputmethodservice.Keyboard
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.gazlaws.codeboard.BooleanMap
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

    private fun Keyboard.getKeyByKeyCode(keyCode: Int): Keyboard.Key? {
        // TODO: Optimise this with shiftKeyIndex for shift
        return keys.find { keyCode in it.codes }
    }

    private fun Keyboard.updateShiftState() {
        getKeyByKeyCode(Keycode.SHIFT)?.label = TEXT_SHIFT[isShiftOn]
        isShifted = isShiftOn
    }

    companion object {
        private const val DOUBLE_SHIFT_MAX_DURATION_MILLIS = 300L

        private val TEXT_SHIFT = BooleanMap("SHIFT", "Shift")
    }
}
