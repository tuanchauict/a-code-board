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
        Keycode.FUNCTION_MOVE_TO_LAST to ::moveToLastCharacter
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

    private fun selectAll() {
        inputConnection.sendKeyEventDownUpWithActionBetween(
            KeyEvent.KEYCODE_A,
            MetaState.CONTROL_ON
        )
    }

    private fun moveToFirstCharacter() {
        inputConnection.sendKeyEventDownUpWithActionBetween(
            KeyEvent.KEYCODE_MOVE_HOME,
            MetaState.CONTROL_ON
        )
    }

    private fun moveToLastCharacter() {
        inputConnection.sendKeyEventDownUpWithActionBetween(
            KeyEvent.KEYCODE_MOVE_END,
            MetaState.CONTROL_ON
        )
    }
}
