package com.gazlaws.codeboard.ime

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.gazlaws.codeboard.ime.CodeBoardIME.Companion.KEYCODE_SHIFT
import com.gazlaws.codeboard.sendKeyEventOnce

class MetaKeysPressHandler(private val inputMethodService: CodeBoardIME) {
    var isShiftOn: Boolean = false
        private set
    var isShiftLocked: Boolean = false
        private set
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
        if (primaryKey == KEYCODE_SHIFT) {
            onShiftKeyPressed()
            return true
        }
        lastShiftKeyPressed = 0 // reset when another key is pressed
        return false
    }

    private fun onShiftKeyPressed() {
        val doubleShiftDurationMillis = System.currentTimeMillis() - lastShiftKeyPressed
        isShiftLocked = doubleShiftDurationMillis <= DOUBLE_SHIFT_MAX_DURATION_MILLIS
        lastShiftKeyPressed = System.currentTimeMillis()

        val shiftKeyAction = if (isShiftOn) KeyEvent.ACTION_UP else KeyEvent.ACTION_DOWN
        currentInputConnection.sendKeyEventOnce(
            shiftKeyAction,
            KeyEvent.KEYCODE_SHIFT_LEFT,
            CodeBoardIME.MetaState.SHIFT_ON
        )

        isShiftOn = isShiftLocked || !isShiftOn
        inputMethodService.updateViewByShiftKey()
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

        inputMethodService.updateViewByShiftKey()
    }

    companion object {
        private const val DOUBLE_SHIFT_MAX_DURATION_MILLIS = 300L
    }
}
