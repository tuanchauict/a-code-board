package com.tuanchauict.acb.ime

import android.annotation.SuppressLint
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import androidx.annotation.IntegerRes
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.tuanchauict.acb.Preferences
import com.tuanchauict.acb.R

/**
 * Created by Ruby(aka gazlaws) on 13/02/2016.
 * Kotlinized by Tuan Chau (aka tuanchauict)
 */
class CodeboardIME : InputMethodService() {
    var keyboardView: KeyboardView? = null
        private set

    @IntegerRes
    private var currentKeyboardMode = R.integer.keyboard_normal
    private var switchedKeyboard = false

    private val shiftKeyPressHandler: ShiftKeyPressHandler = ShiftKeyPressHandler(this)

    private val preferences: Preferences by lazy { Preferences(applicationContext) }

    private val mapKeyCodeToOnKeyAction: Map<Int, () -> Any?> = mapOf(
        Keycode.FUNCTION_SWITCH to {
            val newKeyboardMode = if (currentKeyboardMode == R.integer.keyboard_normal) {
                R.integer.keyboard_functions
            } else {
                R.integer.keyboard_normal
            }
            currentKeyboardMode = newKeyboardMode
            keyboardView?.keyboard = chooseKeyboard(newKeyboardMode)
            shiftKeyPressHandler.updateViewByShiftKey()
        },
        Keycode.FUNCTION_MOVE_TO_FIRST to {
            // TODO: This works wrongly when shift key is on
            currentInputConnection?.setSelection(0, 0)
        },
        Keycode.FUNCTION_MOVE_TO_LAST to {
            // TODO: This works wrongly when shift key is on
            currentInputConnection?.performContextMenuAction(android.R.id.selectAll)
            currentInputConnection?.getSelectedText(0)?.also {
                currentInputConnection?.setSelection(it.length, it.length)
            }
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

        currentKeyboardMode = R.integer.keyboard_normal

        val keyboard = chooseKeyboard(currentKeyboardMode)
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
        Keycode.FUNCTION_KEY_TO_MENU_ACTION_MAP[keyCode]?.also {
            currentInputConnection?.performContextMenuAction(it)
            return
        }
        Keycode.KEY_TO_SIMPLE_DOWN_UP_KEY_EVENT_MAP[keyCode]?.also {
            sendDownUpKeyEvents(it)
            return
        }
        mapKeyCodeToOnKeyAction[keyCode]?.also { action ->
            action.invoke()
            return
        }
        shiftKeyPressHandler.getKeyStringWithShiftState(keyCode)?.also {
            currentInputConnection?.commitText(it, 1)
            shiftKeyPressHandler.releaseShiftKeyWhenNotLocked()
            return
        }

        if (!switchedKeyboard) {
            currentInputConnection?.commitText("${keyCode.toChar()}", 1)
        }
        switchedKeyboard = false
    }

    private fun onKeyLongPress(keyCode: Int) {
        val reversedShiftedStateChar =
            shiftKeyPressHandler.getKeyStringWithShiftState(keyCode, true)
        when {
            keyCode == Keycode.SPACE -> {
                switchedKeyboard = true
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
            keyCode in Keycode.LONG_KEY_TO_KEY_EVENT_MAP ->
                Keycode.LONG_KEY_TO_KEY_EVENT_MAP[keyCode]?.let(::sendDownUpKeyEvents)
            keyCode in Keycode.LONG_KEY_TO_MENU_ACTION_MAP ->
                Keycode.LONG_KEY_TO_MENU_ACTION_MAP[keyCode]
                    ?.let { currentInputConnection?.performContextMenuAction(it) }
            keyCode == Keycode.SYMBOL_COMMA -> currentInputConnection?.commitText(".", 1)
            reversedShiftedStateChar != null ->
                currentInputConnection?.commitText(reversedShiftedStateChar, 1)
        }
    }

    private fun chooseKeyboard(@IntegerRes keyboardMode: Int): Keyboard =
        Keyboard(this, R.xml.code_1, keyboardMode)

    enum class MetaState(val value: Int) {
        SHIFT_ON(KeyEvent.META_CTRL_ON)
    }
}
