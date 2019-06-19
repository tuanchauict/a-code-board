package com.gazlaws.codeboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.support.annotation.IntRange
import android.support.annotation.IntegerRes
import android.support.annotation.LayoutRes
import android.support.annotation.XmlRes
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_CTRL_LEFT
import android.view.KeyEvent.KEYCODE_SHIFT_LEFT
import android.view.KeyEvent.META_CTRL_ON
import android.view.KeyEvent.META_SHIFT_ON
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager

/**
 * Created by Ruby(aka gazlaws) on 13/02/2016.
 * Kotlinized by Tuan Chau (aka tuanchauict)
 */
class CodeBoardIME : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    private var keyboardView: KeyboardView? = null
    private lateinit var sEditorInfo: EditorInfo
    private var isShiftLocked = false
    private var isShiftOn = false
    private var isCtrlOn = false

    @IntegerRes
    private var currentKeyboardMode = R.integer.keyboard_normal
    private var switchedKeyboard = false

    private val uiHandler = Handler(Looper.getMainLooper())

    private val preferences: Preferences by lazy { Preferences(applicationContext) }

    private val mapKeyCodeToOnKeyAction: Map<Int, () -> Unit?> = mapOf(
        KEYCODE_ESCAPE to {
            currentInputConnection?.sendKeyEventOnce(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_ESCAPE,
                MetaState.CONTROL_ON
            )
        },
        KEYCODE_INPUT_METHOD_PICKER to {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        },
        KEYCODE_SYM_MODE to {
            val newKeyboardMode = if (currentKeyboardMode == R.integer.keyboard_normal) {
                R.integer.keyboard_sym
            } else {
                R.integer.keyboard_normal
            }
            keyboardView?.keyboard = chooseKeyboard(newKeyboardMode)
            controlKeyUpdateView()
            shiftKeyUpdateView()
            currentKeyboardMode = newKeyboardMode
        },
        KEYCODE_CONTROL to {
            val controlKeyAction = if (isCtrlOn) KeyEvent.ACTION_UP else KeyEvent.ACTION_DOWN
            currentInputConnection?.sendKeyEventOnce(
                controlKeyAction,
                KEYCODE_CTRL_LEFT,
                MetaState.CONTROL_ON
            )
            isCtrlOn = !isCtrlOn
            controlKeyUpdateView()
        },
        KEYCODE_SHIFT to {
            // Shift - runs after long press, so shiftlock may have just been activated
            val shiftKeyAction = if (isShiftOn) KeyEvent.ACTION_UP else KeyEvent.ACTION_DOWN
            currentInputConnection?.sendKeyEventOnce(
                shiftKeyAction,
                KEYCODE_SHIFT_LEFT,
                MetaState.SHIFT_ON
            )

            isShiftOn = if (isShiftLocked) true else !isShiftOn
            shiftKeyUpdateView()
        },
        KEYCODE_DPAD_LEFT to {
            handleArrow(KeyEvent.KEYCODE_DPAD_LEFT)
        },
        KEYCODE_DPAD_RIGHT to {
            handleArrow(KeyEvent.KEYCODE_DPAD_RIGHT)
        }
    )

    private fun onKeyCtrl(code: Int) {
        val codeChar = code.toChar().toUpperCase()
        if (sEditorInfo.isDroidEdit() && codeChar in DROID_EDIT_PROBLEM_KEY_CODES) {
            val actionKey = if (codeChar == 'Z' && !isShiftOn) 'z' else codeChar
            val action = DROID_EDIT_PROBLEM_KEY_CODES[actionKey] ?: return
            currentInputConnection?.performContextMenuAction(action)
            if (codeChar == 'Z') {
                isShiftLocked = false
                releaseShiftKeyWhenNotLocked()
            }
            return
        }
        val keyCode = CHAR_TO_KEYCODE_MAP[codeChar]
        if (keyCode == null) {
            currentInputConnection?.commitText("$codeChar", 1)
            releaseShiftKeyWhenNotLocked()
            return
        }

        val metaState = if (codeChar == 'Z' && isShiftOn) {
            MetaState.SHIFT_CONTROL_ON
        } else {
            MetaState.CONTROL_ON
        }

        currentInputConnection?.sendKeyEventOnce(
            KeyEvent.ACTION_DOWN,
            keyCode,
            metaState,
            System.currentTimeMillis() + 1
        )
        if (codeChar == 'Z' && isShiftOn) {
            isShiftLocked = false
            releaseShiftKeyWhenNotLocked()
        }
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray) {
        KEYCODE_TO_MENU_ACTION_MAP[primaryCode]?.also {
            currentInputConnection?.performContextMenuAction(it)
            return
        }
        KEYCODE_TO_SIMPLE_DOWN_UP_KEY_EVENT_MAP[primaryCode]?.also {
            sendDownUpKeyEvents(it)
            return
        }
        mapKeyCodeToOnKeyAction[primaryCode]?.also { action ->
            action.invoke()
            return
        }

        val code = primaryCode.toChar()
        when {
            isCtrlOn -> {
                onKeyCtrl(primaryCode)
                releaseShiftKeyWhenNotLocked()
                isCtrlOn = false
                controlKeyUpdateView()
            }
            isShiftOn && code.isLetter() -> {
                currentInputConnection?.commitText("${code.toUpperCase()}", 1)
                releaseShiftKeyWhenNotLocked()
            }
            else -> {
                if (!switchedKeyboard) {
                    currentInputConnection?.commitText("$code", 1)
                }
                switchedKeyboard = false
            }
        }
    }

    private fun releaseShiftKeyWhenNotLocked() {
        if (isShiftLocked) {
            return
        }
        isShiftOn = false
        currentInputConnection?.sendKeyEventOnce(
            KeyEvent.ACTION_UP,
            KEYCODE_SHIFT_LEFT,
            MetaState.SHIFT_ON
        )

        shiftKeyUpdateView()
    }

    override fun onPress(primaryCode: Int) {
        if (preferences.isSoundOn) {
            val keypressSoundPlayer = MediaPlayer.create(this, R.raw.keypress_sound)
            keypressSoundPlayer.start()
            keypressSoundPlayer.setOnCompletionListener { mp -> mp.release() }
        }
        if (preferences.isVibrateOn) {
            vibrate(20)
        }

        uiHandler.removeCallbacksAndMessages(null)
        uiHandler.postDelayed({
            try {
                onKeyLongPress(primaryCode)
            } catch (e: Exception) {
                Log.e("CodeBoardIME", "uiHandler.run: ${e.message}", e)
            }
        }, ViewConfiguration.getLongPressTimeout().toLong())
    }

    override fun onRelease(primaryCode: Int) {
        uiHandler.removeCallbacksAndMessages(null)
    }

    private fun onKeyLongPress(keyCode: Int) {
        if (keyCode == KEYCODE_SHIFT) {
            isShiftLocked = !isShiftLocked
        }

        if (keyCode == KEYCODE_SPACE) {
            switchedKeyboard = true
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }

        vibrate(50L)
    }

    override fun onText(text: CharSequence) {
        val inputConnection = currentInputConnection ?: return
        inputConnection.commitText(text, 1)

        val numberOfRepeats = if ("for" in text) 7 else 3
        repeat(numberOfRepeats) {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
        }
    }

    override fun swipeDown() {
        keyboardView?.closing()
    }

    override fun swipeLeft() = Unit

    override fun swipeRight() = Unit

    override fun swipeUp() = Unit

    private fun chooseKeyboard(@IntegerRes keyboardMode: Int): Keyboard {
        @IntRange(from = 0L, to = 1L)
        val topRowIndex = if (preferences.isDpadOn) 1 else 0
        val sizeIndex = preferences.keyboardSize
        @XmlRes
        val keyboardXmlRes = if (preferences.selectedKeyboardLayoutIndex == 0) {
            QWERTY_KEYBOARDS[sizeIndex][topRowIndex]
        } else {
            AZERTY_KEYBOARDS[sizeIndex][topRowIndex]
        }
        return Keyboard(this, keyboardXmlRes, keyboardMode)
    }

    override fun onCreateInputView(): View? {
        @LayoutRes
        val keyboardLayoutRes = KEYBOARD_LAYOUT_RESES[preferences.selectedKeyboardColorIndex]
        val keyboardView =
            layoutInflater.inflate(keyboardLayoutRes, null) as? KeyboardView ?: return null
        this.keyboardView = keyboardView

        keyboardView.isPreviewEnabled = preferences.isPreviewEnabled

        isShiftOn = false
        isCtrlOn = false

        currentKeyboardMode = R.integer.keyboard_normal
        //reset to normal

        val keyboard = chooseKeyboard(currentKeyboardMode)
        keyboardView.keyboard = keyboard
        keyboardView.setOnKeyboardActionListener(this)

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

    private fun shiftKeyUpdateView() {
        val nonNullKeyboardView = keyboardView ?: return
        val nonNullKeyboard = nonNullKeyboardView.keyboard ?: return

        val index = nonNullKeyboard.keys.indexOfFirst { it.label in TEXT_SHIFT.values }
        nonNullKeyboard.keys.getOrNull(index)?.label = TEXT_SHIFT[isShiftOn]
        nonNullKeyboard.isShifted = isShiftOn
        nonNullKeyboardView.invalidateAllKeys()
    }

    private fun handleArrow(keyCode: Int) {
        val inputConnection = currentInputConnection ?: return
        when {
            isCtrlOn && isShiftOn -> {
                inputConnection.sendKeyEventDownUpWithActionBetween(
                    KEYCODE_CTRL_LEFT,
                    MetaState.SHIFT_CONTROL_ON
                ) { moveSelection(keyCode) }
            }
            isShiftOn -> moveSelection(keyCode)
            isCtrlOn -> inputConnection.sendKeyEventOnce(
                KeyEvent.ACTION_DOWN,
                keyCode,
                MetaState.CONTROL_ON
            )
            else -> sendDownUpKeyEvents(keyCode)
        }
    }

    private fun moveSelection(keyCode: Int) = currentInputConnection?.sendKeyEventDownUpWithActionBetween(
            KEYCODE_SHIFT_LEFT,
            MetaState.SHIFT_CONTROL_ON
        ) {
            val metaState = if (isCtrlOn) MetaState.SHIFT_CONTROL_ON else MetaState.SHIFT_ON
            currentInputConnection?.sendKeyEventOnce(KeyEvent.ACTION_DOWN, keyCode, metaState)
        }

    @Suppress("DEPRECATION")
    private fun vibrate(durationMillis: Long) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(durationMillis)
    }

    private fun InputConnection.sendKeyEventOnce(
        action: Int,
        code: Int,
        metaState: MetaState,
        sendingTimeMillis: Long = System.currentTimeMillis()
    ) {
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

    private fun InputConnection.sendKeyEventDownUpWithActionBetween(
        code: Int,
        metaState: MetaState,
        action: () -> Unit = {}
    ) {
        sendKeyEventOnce(KeyEvent.ACTION_DOWN, code, metaState)
        action()
        sendKeyEventOnce(KeyEvent.ACTION_UP, code, metaState)
    }

    private fun EditorInfo.isDroidEdit(): Boolean = imeOptions == DROID_EDIT_IME_OPTIONS

    private enum class MetaState(val value: Int) {
        SHIFT_ON(META_CTRL_ON),
        CONTROL_ON(META_CTRL_ON),
        SHIFT_CONTROL_ON(META_SHIFT_ON or META_CTRL_ON)
    }

    companion object {
        private const val DROID_EDIT_IME_OPTIONS = 1342177286

        private const val KEYCODE_SELECT_ALL = 53737
        private const val KEYCODE_CUT = 53738
        private const val KEYCODE_COPY = 53739
        private const val KEYCODE_PASTE = 53740
        private const val KEYCODE_UNDO = 53741
        private const val KEYCODE_REDO = 53742

        private const val KEYCODE_DELETE = Keyboard.KEYCODE_DELETE
        private const val KEYCODE_DONE = Keyboard.KEYCODE_DONE
        private const val KEYCODE_ESCAPE = 27
        private const val KEYCODE_SYM_MODE = -15
        private const val KEYCODE_CONTROL = 17
        private const val KEYCODE_SHIFT = 16
        private const val KEYCODE_TAB = 9
        private const val KEYCODE_INPUT_METHOD_PICKER = -13

        private const val KEYCODE_DPAD_LEFT = 5000
        private const val KEYCODE_DPAD_DOWN = 5001
        private const val KEYCODE_DPAD_UP = 5002
        private const val KEYCODE_DPAD_RIGHT = 5003

        private const val KEYCODE_SPACE = 32

        private val KEYCODE_TO_MENU_ACTION_MAP = mapOf(
            KEYCODE_SELECT_ALL to android.R.id.selectAll,
            KEYCODE_CUT to android.R.id.cut,
            KEYCODE_COPY to android.R.id.copy,
            KEYCODE_PASTE to android.R.id.paste,
            KEYCODE_UNDO to android.R.id.undo,
            KEYCODE_REDO to android.R.id.redo
        )

        private val KEYCODE_TO_SIMPLE_DOWN_UP_KEY_EVENT_MAP = mapOf(
            KEYCODE_DELETE to KeyEvent.KEYCODE_DEL,
            KEYCODE_DONE to KeyEvent.KEYCODE_ENTER,
            KEYCODE_TAB to KeyEvent.KEYCODE_TAB,
            KEYCODE_DPAD_DOWN to KeyEvent.KEYCODE_DPAD_DOWN,
            KEYCODE_DPAD_UP to KeyEvent.KEYCODE_DPAD_UP
        )

        @LayoutRes
        private val KEYBOARD_LAYOUT_RESES: Array<Int> = arrayOf(
            R.layout.keyboard,
            R.layout.keyboard1,
            R.layout.keyboard2,
            R.layout.keyboard3,
            R.layout.keyboard4,
            R.layout.keyboard5
        )

        @XmlRes
        private val QWERTY_KEYBOARDS = arrayOf(
            arrayOf(R.xml.qwerty0e, R.xml.qwerty0r),
            arrayOf(R.xml.qwerty1e, R.xml.qwerty1r),
            arrayOf(R.xml.qwerty2e, R.xml.qwerty2r),
            arrayOf(R.xml.qwerty3e, R.xml.qwerty3r)
        )

        @XmlRes
        private val AZERTY_KEYBOARDS = arrayOf(
            arrayOf(R.xml.azerty0e, R.xml.azerty0r),
            arrayOf(R.xml.azerty1e, R.xml.azerty1r),
            arrayOf(R.xml.azerty2e, R.xml.azerty2r),
            arrayOf(R.xml.azerty3e, R.xml.azerty3r)
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

        private val TEXT_SHIFT = BooleanMap("SHFT", "Shft")
        private val TEXT_CONTROL = BooleanMap("CTRL", "Ctrl")
    }
}
