package com.gazlaws.codeboard

import android.view.KeyEvent
import android.view.inputmethod.InputConnection

fun InputConnection?.sendKeyEventOnce(
    action: Int,
    code: Int,
    metaState: CodeBoardIME.MetaState,
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
    metaState: CodeBoardIME.MetaState,
    action: () -> Unit = {}
) {
    sendKeyEventOnce(KeyEvent.ACTION_DOWN, code, metaState)
    action()
    sendKeyEventOnce(KeyEvent.ACTION_UP, code, metaState)
}
