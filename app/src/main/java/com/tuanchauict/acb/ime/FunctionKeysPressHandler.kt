package com.tuanchauict.acb.ime

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.tuanchauict.acb.MetaState
import com.tuanchauict.acb.sendKeyEventDownUpWithActionBetween

class FunctionKeysPressHandler(
    private val inputMethodService: InputMethodService
) {
    private val inputConnection: InputConnection? get() = inputMethodService.currentInputConnection

    private val functionKeyToActionMap: Map<Int, () -> Unit> = mapOf(
        Keycode.FUNCTION_SELECT_ALL to ::selectAll,
        Keycode.FUNCTION_MOVE_TO_FIRST to ::moveToFirstCharacter,
        Keycode.FUNCTION_MOVE_TO_LAST to ::moveToLastCharacter,
        Keycode.FUNCTION_CUT to ::cut,
        Keycode.FUNCTION_COPY to ::copy,
        Keycode.FUNCTION_PASTE to ::paste,
        Keycode.FUNCTION_UNDO to ::undo,
        Keycode.FUNCTION_REDO to ::redo
    )

    private val longPressKeyToActionMap: Map<Int, () -> Unit> = mapOf(
        Keycode.LETTER_X to ::cut,
        Keycode.LETTER_C to ::copy,
        Keycode.LETTER_V to ::paste,
        Keycode.LETTER_Z to ::undo
    )

    fun onKey(keyCode: Int): Boolean {
        val action = functionKeyToActionMap[keyCode]
        return if (action != null) {
            action.invoke()
            true
        } else {
            false
        }
    }

    fun onKeyLongPress(keyCode: Int): Boolean {
        val action = longPressKeyToActionMap[keyCode]
        return if (action != null) {
            action.invoke()
            true
        } else {
            false
        }
    }

    private fun selectAll() = inputConnection.sendKeyEventDownUpWithActionBetween(
        KeyEvent.KEYCODE_A,
        MetaState.CONTROL_ON
    )

    private fun moveToFirstCharacter() = inputConnection.sendKeyEventDownUpWithActionBetween(
        KeyEvent.KEYCODE_MOVE_HOME,
        MetaState.CONTROL_ON
    )

    private fun moveToLastCharacter() = inputConnection.sendKeyEventDownUpWithActionBetween(
        KeyEvent.KEYCODE_MOVE_END,
        MetaState.CONTROL_ON
    )

    private fun cut() = inputConnection.sendKeyEventDownUpWithActionBetween(
        KeyEvent.KEYCODE_X,
        MetaState.CONTROL_ON
    )

    private fun copy() = inputConnection.sendKeyEventDownUpWithActionBetween(
        KeyEvent.KEYCODE_C,
        MetaState.CONTROL_ON
    )

    private fun paste() = inputConnection.sendKeyEventDownUpWithActionBetween(
        KeyEvent.KEYCODE_V,
        MetaState.CONTROL_ON
    )

    private fun undo() = inputConnection.sendKeyEventDownUpWithActionBetween(
        KeyEvent.KEYCODE_Z,
        MetaState.CONTROL_ON
    )

    private fun redo() = inputConnection.sendKeyEventDownUpWithActionBetween(
        KeyEvent.KEYCODE_Z,
        MetaState.CONTROL_SHIFT_ON
    )
}
