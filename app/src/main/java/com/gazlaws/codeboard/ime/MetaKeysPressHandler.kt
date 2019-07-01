package com.gazlaws.codeboard.ime

import android.inputmethodservice.InputMethodService
import android.view.inputmethod.InputConnection

class MetaKeysPressHandler(private val inputMethodService: InputMethodService) {

    private val currentInputConnection: InputConnection?
        get() = inputMethodService.currentInputConnection

    /**
     * Handles onKey event for meta keys like Shift, Control, etc.
     * Returns true if this method consumes the event.
     */
    fun onKey(primaryKey: Int): Boolean {

        return false
    }
}
