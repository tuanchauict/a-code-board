package com.tuanchauict.acb.ime

import android.annotation.SuppressLint
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IntegerRes
import com.tuanchauict.acb.Preferences
import com.tuanchauict.acb.R
import com.tuanchauict.acb.sendKeyEventDownUpWithActionBetween

/**
 * An input method service which handles keyboard layout UI and key press event effect.
 */
class CodeBoardInputMethodService : InputMethodService() {
    var keyboardView: KeyboardView? = null
        private set

    private val shiftKeyPressHandler: ShiftKeyPressHandler = ShiftKeyPressHandler(this)
    private val functionKeysPressHandler: FunctionKeysPressHandler = FunctionKeysPressHandler(this)

    private val preferences: Preferences by lazy { Preferences(applicationContext) }

    private val mapKeyCodeToOnKeyAction: Map<Int, () -> Any?> = mapOf(
        Keycode.FUNCTION_ENTER_FUNCTION_MODE to {
            keyboardView?.keyboard = chooseKeyboard(R.integer.keyboard_functions)
            shiftKeyPressHandler.updateViewByShiftKey()
        },
        Keycode.FUNCTION_EXIT_FUNCTION_MODE to {
            keyboardView?.keyboard = chooseKeyboard(R.integer.keyboard_normal)
            shiftKeyPressHandler.updateViewByShiftKey()
        },
        Keycode.TAB to {
            if (preferences.tabMode == Preferences.TabMode.TAB) {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_TAB)
            } else {
                val text = preferences.tabMode.text
                currentInputConnection?.commitText(text, text.length)
            }
        }
    )

    @SuppressLint("InflateParams")
    override fun onCreateInputView(): View? {
        val keyboardView =
            layoutInflater.inflate(R.layout.keyboard, null) as? KeyboardView ?: return null
        this.keyboardView = keyboardView

        shiftKeyPressHandler.reset()

        val keyboard = chooseKeyboard(R.integer.keyboard_normal)
        keyboardView.keyboard = keyboard

        val keyboardActionListener = KeyboardActionListener(
            this,
            keyboardView,
            Keycode.LONG_PRESS_KEY_CODES,
            preferences,
            ::onKey,
            ::onKeyLongPress
        )
        keyboardView.setOnKeyboardActionListener(keyboardActionListener)

        return keyboardView
    }

    override fun onStartInputView(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInputView(attribute, restarting)
        setInputView(onCreateInputView())
    }

    private fun onKey(keyCode: Int) {
        if (shiftKeyPressHandler.onKey(keyCode)) {
            return
        }
        if (functionKeysPressHandler.onKey(keyCode)) {
            return
        }
        mapKeyCodeToOnKeyAction[keyCode]?.also { action ->
            action.invoke()
            return
        }
        Keycode.KEY_TO_SIMPLE_DOWN_UP_KEY_EVENT_MAP[keyCode]?.also {
            val metaState = SHIFT_OR_NONE_MAP[shiftKeyPressHandler.isShifted]
            currentInputConnection.sendKeyEventDownUpWithActionBetween(it, metaState)
            return
        }
        shiftKeyPressHandler.getKeyStringWithShiftState(keyCode)?.also {
            currentInputConnection?.commitText(it, 1)
            shiftKeyPressHandler.releaseShiftKeyWhenNotLocked()
            return
        }

        currentInputConnection?.commitText("${keyCode.toChar()}", 1)
    }

    private fun onKeyLongPress(keyCode: Int) {
        if (functionKeysPressHandler.onKeyLongPress(keyCode)) {
            return
        }
        val reversedShiftedStateChar =
            shiftKeyPressHandler.getKeyStringWithShiftState(keyCode, true)
        when {
            keyCode == Keycode.SPACE -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
            keyCode in Keycode.LONG_KEY_TO_KEY_EVENT_MAP ->
                Keycode.LONG_KEY_TO_KEY_EVENT_MAP[keyCode]?.let(::sendDownUpKeyEvents)
            keyCode == Keycode.SYMBOL_COMMA -> currentInputConnection?.commitText(".", 1)
            reversedShiftedStateChar != null ->
                currentInputConnection?.commitText(reversedShiftedStateChar, 1)
        }
    }

    private fun chooseKeyboard(@IntegerRes keyboardMode: Int): Keyboard =
        Keyboard(this, R.xml.code_1, keyboardMode)
}
