package com.tuanchauict.codecube

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.tuanchauict.codecube.ime.CodeCubeIME

fun InputConnection?.sendKeyEventOnce(
    action: Int,
    code: Int,
    metaState: CodeCubeIME.MetaState,
    sendingTimeMillis: Long = System.currentTimeMillis()
) {
    if (this == null) {
        return
    }
    val keyEvent = KeyEvent(
        sendingTimeMillis,
        sendingTimeMillis,
        action,
        code,
        0,
        metaState.value
    )
    sendKeyEvent(keyEvent)
}

fun InputConnection?.sendKeyEventDownUpWithActionBetween(
    code: Int,
    metaState: CodeCubeIME.MetaState,
    action: () -> Unit = {}
) {
    sendKeyEventOnce(KeyEvent.ACTION_DOWN, code, metaState)
    action()
    sendKeyEventOnce(KeyEvent.ACTION_UP, code, metaState)
}
