package com.tuanchauict.codecube.ime

import android.annotation.SuppressLint
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.support.annotation.IntegerRes
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.tuanchauict.codecube.BooleanMap
import com.tuanchauict.codecube.Preferences
import com.tuanchauict.codecube.R
import com.tuanchauict.codecube.sendKeyEventDownUpWithActionBetween
import com.tuanchauict.codecube.sendKeyEventOnce

/**
 * Created by Ruby(aka gazlaws) on 13/02/2016.
 * Kotlinized by Tuan Chau (aka tuanchauict)
 */
class CodeCubeIME : InputMethodService() {
    var keyboardView: KeyboardView? = null
        private set
    private lateinit var sEditorInfo: EditorInfo
    private var isCtrlOn = false

    @IntegerRes
    private var currentKeyboardMode = R.integer.keyboard_normal
    private var switchedKeyboard = false

    private val shiftKeyPressHandler: ShiftKeyPressHandler = ShiftKeyPressHandler(this)

    private val preferences: Preferences by lazy { Preferences(applicationContext) }

    private val mapKeyCodeToOnKeyAction: Map<Int, () -> Unit?> = mapOf(
        Keycode.FUNCTION_SWITCH to {
            val newKeyboardMode = if (currentKeyboardMode == R.integer.keyboard_normal) {
                R.integer.keyboard_functions
            } else {
                R.integer.keyboard_normal
            }
            currentKeyboardMode = newKeyboardMode
            keyboardView?.keyboard = chooseKeyboard(newKeyboardMode)
            controlKeyUpdateView()
            shiftKeyPressHandler.updateViewByShiftKey()
        },
        Keycode.CONTROL to {
            val controlKeyAction = if (isCtrlOn) KeyEvent.ACTION_UP else KeyEvent.ACTION_DOWN
            currentInputConnection.sendKeyEventOnce(
                controlKeyAction,
                KeyEvent.KEYCODE_CTRL_LEFT,
                MetaState.CONTROL_ON
            )
            isCtrlOn = !isCtrlOn
            controlKeyUpdateView()
        },
        Keycode.FUNCTION_DPAD_LEFT to {
            handleArrow(KeyEvent.KEYCODE_DPAD_LEFT)
        },
        Keycode.FUNCTION_DPAD_RIGHT to {
            handleArrow(KeyEvent.KEYCODE_DPAD_RIGHT)
        }
    )

    private fun onKeyCtrl(code: Int) {
        val codeChar = code.toChar().toUpperCase()
        if (sEditorInfo.isDroidEdit() && codeChar in DROID_EDIT_PROBLEM_KEY_CODES) {
            val actionKey =
                if (codeChar == 'Z' && !shiftKeyPressHandler.isShiftOn) 'z' else codeChar
            val action = DROID_EDIT_PROBLEM_KEY_CODES[actionKey] ?: return
            currentInputConnection?.performContextMenuAction(action)
            return
        }
        val keyCode = CHAR_TO_KEYCODE_MAP[codeChar]
        if (keyCode == null) {
            currentInputConnection?.commitText("$codeChar", 1)
            shiftKeyPressHandler.releaseShiftKeyWhenNotLocked()
            return
        }

        val metaState = if (codeChar == 'Z' && shiftKeyPressHandler.isShiftOn) {
            MetaState.SHIFT_CONTROL_ON
        } else {
            MetaState.CONTROL_ON
        }

        currentInputConnection.sendKeyEventOnce(
            KeyEvent.ACTION_DOWN,
            keyCode,
            metaState,
            System.currentTimeMillis() + 1
        )
    }

    private fun onKey(keyCode: Int) {
        if (shiftKeyPressHandler.onKey(keyCode)) {
            return
        }
        Keycode.FUNCTION_KEY_TO_MENU_ACTION_MAP[keyCode]?.also {
            currentInputConnection?.performContextMenuAction(it)
            return
        }
        KEYCODE_TO_SIMPLE_DOWN_UP_KEY_EVENT_MAP[keyCode]?.also {
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

        val code = keyCode.toChar()
        when {
            isCtrlOn -> {
                onKeyCtrl(keyCode)
                shiftKeyPressHandler.releaseShiftKeyWhenNotLocked()
                isCtrlOn = false
                controlKeyUpdateView()
            }
            else -> {
                if (!switchedKeyboard) {
                    currentInputConnection?.commitText("$code", 1)
                }
                switchedKeyboard = false
            }
        }
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

    @SuppressLint("InflateParams")
    override fun onCreateInputView(): View? {
        val keyboardView =
            layoutInflater.inflate(R.layout.keyboard, null) as? KeyboardView ?: return null
        this.keyboardView = keyboardView

        shiftKeyPressHandler.reset()

        isCtrlOn = false

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
        sEditorInfo = attribute
    }

    private fun controlKeyUpdateView() {
        val nonNullKeyboardView = keyboardView ?: return
        val nonNullKeyboard = nonNullKeyboardView.keyboard ?: return

        val index = nonNullKeyboard.keys.indexOfFirst { it.label in TEXT_CONTROL.values }
        nonNullKeyboard.keys.getOrNull(index)?.label = TEXT_CONTROL[isCtrlOn]
        nonNullKeyboardView.invalidateKey(index)
    }

    private fun handleArrow(keyCode: Int) =
        when {
            isCtrlOn && shiftKeyPressHandler.isShiftOn -> currentInputConnection
                ?.sendKeyEventDownUpWithActionBetween(
                    KeyEvent.KEYCODE_CTRL_LEFT,
                    MetaState.SHIFT_CONTROL_ON
                ) { moveSelection(keyCode) }
            shiftKeyPressHandler.isShiftOn -> moveSelection(keyCode)
            isCtrlOn -> currentInputConnection
                ?.sendKeyEventOnce(
                    KeyEvent.ACTION_DOWN,
                    keyCode,
                    MetaState.CONTROL_ON
                )
            else -> sendDownUpKeyEvents(keyCode)
        }

    private fun moveSelection(keyCode: Int): Unit? =
        currentInputConnection?.sendKeyEventDownUpWithActionBetween(
            KeyEvent.KEYCODE_SHIFT_LEFT,
            MetaState.SHIFT_CONTROL_ON
        ) {
            val metaState = if (isCtrlOn) MetaState.SHIFT_CONTROL_ON else MetaState.SHIFT_ON
            currentInputConnection.sendKeyEventOnce(KeyEvent.ACTION_DOWN, keyCode, metaState)
        }

    private fun EditorInfo.isDroidEdit(): Boolean = imeOptions == DROID_EDIT_IME_OPTIONS

    enum class MetaState(val value: Int) {
        SHIFT_ON(KeyEvent.META_CTRL_ON),
        CONTROL_ON(KeyEvent.META_CTRL_ON),
        SHIFT_CONTROL_ON(KeyEvent.META_SHIFT_ON or KeyEvent.META_CTRL_ON)
    }

    companion object {
        private const val DROID_EDIT_IME_OPTIONS = 1342177286

        val KEYCODE_TO_SIMPLE_DOWN_UP_KEY_EVENT_MAP = mapOf(
            Keycode.DELETE to KeyEvent.KEYCODE_DEL,
            Keycode.DONE to KeyEvent.KEYCODE_ENTER,
            Keycode.TAB to KeyEvent.KEYCODE_TAB,
            Keycode.FUNCTION_DPAD_DOWN to KeyEvent.KEYCODE_DPAD_DOWN,
            Keycode.FUNCTION_DPAD_UP to KeyEvent.KEYCODE_DPAD_UP,
            Keycode.FUNCTION_ESC to KeyEvent.KEYCODE_ESCAPE
        )

        private val DROID_EDIT_PROBLEM_KEY_CODES = mapOf(
            'A' to android.R.id.selectAll,
            'C' to android.R.id.copy,
            'V' to android.R.id.paste,
            'X' to android.R.id.cut,
            'z' to android.R.id.undo,
            'Z' to android.R.id.redo
        )

        private val CHAR_TO_KEYCODE_MAP = mapOf(
            'A' to KeyEvent.KEYCODE_A,
            'B' to KeyEvent.KEYCODE_B,
            'C' to KeyEvent.KEYCODE_C,
            'D' to KeyEvent.KEYCODE_D,
            'E' to KeyEvent.KEYCODE_E,
            'F' to KeyEvent.KEYCODE_F,
            'G' to KeyEvent.KEYCODE_G,
            'H' to KeyEvent.KEYCODE_H,
            'I' to KeyEvent.KEYCODE_I,
            'J' to KeyEvent.KEYCODE_J,
            'K' to KeyEvent.KEYCODE_K,
            'L' to KeyEvent.KEYCODE_L,
            'M' to KeyEvent.KEYCODE_M,
            'N' to KeyEvent.KEYCODE_N,
            'O' to KeyEvent.KEYCODE_O,
            'P' to KeyEvent.KEYCODE_P,
            'Q' to KeyEvent.KEYCODE_Q,
            'R' to KeyEvent.KEYCODE_R,
            'S' to KeyEvent.KEYCODE_S,
            'T' to KeyEvent.KEYCODE_T,
            'U' to KeyEvent.KEYCODE_U,
            'V' to KeyEvent.KEYCODE_V,
            'W' to KeyEvent.KEYCODE_W,
            'X' to KeyEvent.KEYCODE_X,
            'Y' to KeyEvent.KEYCODE_Y,
            'Z' to KeyEvent.KEYCODE_Z
        )

        private val TEXT_CONTROL = BooleanMap("CTRL", "Ctrl")
    }
}
