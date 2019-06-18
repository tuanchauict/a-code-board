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
import java.util.Timer
import java.util.TimerTask

/**
 * Created by Ruby(aka gazlaws) on 13/02/2016.
 * Kotlinized by Tuan Chau (aka tuanchauict)
 */
class CodeBoardIME : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    private var keyboardView: KeyboardView? = null
    private var keyboard: Keyboard? = null
    private lateinit var sEditorInfo: EditorInfo
    private var shiftLock = false
    private var isShiftOn = false
    private var isCtrlOn = false

    @IntegerRes
    private var mKeyboardState = R.integer.keyboard_normal
    private var timerLongPress: Timer? = null
    private var switchedKeyboard = false

    private val uiHandler = Handler(Looper.getMainLooper())

    private val preferences: Preferences = Preferences(applicationContext)

    private fun onKeyCtrl(code: Int, ic: InputConnection?) {
        val codeChar = code.toChar().toUpperCase()
        if (sEditorInfo.isDroidEdit() && codeChar in DROID_EDIT_PROBLEM_KEY_CODES) {
            val actionKey = if (codeChar == 'Z' && !isShiftOn) 'z' else codeChar
            val action = DROID_EDIT_PROBLEM_KEY_CODES[actionKey] ?: return
            currentInputConnection.performContextMenuAction(action)
            if (codeChar == 'Z') {
                isShiftOn = false
                ic?.sendKeyEventOnce(
                    KeyEvent.ACTION_UP,
                    KEYCODE_SHIFT_LEFT,
                    META_SHIFT_ON,
                    System.currentTimeMillis()
                )

                shiftLock = false
                shiftKeyUpdateView()
            }
            return
        }
        val keyCode = CHAR_TO_KEYCODE_MAP[codeChar]
        if (keyCode == null) {
            ic?.commitText("$codeChar", 1)
            if (!shiftLock) {
                isShiftOn = false
                ic?.sendKeyEventOnce(
                    KeyEvent.ACTION_UP,
                    KEYCODE_SHIFT_LEFT,
                    META_SHIFT_ON
                )

                //Log.e("CodeboardIME", "Unshifted b/c no lock");
            }
            shiftKeyUpdateView()
            return
        }

        val metaState = if (codeChar == 'Z' && isShiftOn) {
            META_CTRL_ON or META_SHIFT_ON
        } else {
            META_CTRL_ON
        }

        ic?.sendKeyEventOnce(
            KeyEvent.ACTION_DOWN,
            keyCode,
            metaState,
            System.currentTimeMillis() + 1
        )
        if (codeChar == 'Z' && isShiftOn) {
            isShiftOn = false
            ic?.sendKeyEventOnce(
                KeyEvent.ACTION_UP,
                KEYCODE_SHIFT_LEFT,
                META_SHIFT_ON
            )

            shiftLock = false
            shiftKeyUpdateView()
        }
    }

    override fun onKey(primaryCode: Int, KeyCodes: IntArray) {
        val ic = currentInputConnection
        keyboard = keyboardView!!.keyboard

        when (primaryCode) {
            53737 -> currentInputConnection.performContextMenuAction(android.R.id.selectAll)
            53738 -> currentInputConnection.performContextMenuAction(android.R.id.cut)
            53739 -> currentInputConnection.performContextMenuAction(android.R.id.copy)
            53740 -> currentInputConnection.performContextMenuAction(android.R.id.paste)
            53741 -> currentInputConnection.performContextMenuAction(android.R.id.undo)
            53742 -> currentInputConnection.performContextMenuAction(android.R.id.redo)

            Keyboard.KEYCODE_DELETE -> sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL)
            Keyboard.KEYCODE_DONE -> sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)

            27 -> {
                // Escape
                ic.sendKeyEventOnce(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_ESCAPE,
                    META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
                )
            }

            -13 -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
            -15 -> if (keyboardView != null) {
                if (mKeyboardState == R.integer.keyboard_normal) {
                    //change to symbol keyboard
                    val symbolKeyboard =
                        chooseKeyboard(R.integer.keyboard_sym)

                    keyboardView!!.keyboard = symbolKeyboard

                    mKeyboardState = R.integer.keyboard_sym
                } else if (mKeyboardState == R.integer.keyboard_sym) {
                    //change to normal keyboard
                    val normalKeyboard =
                        chooseKeyboard(R.integer.keyboard_normal)

                    keyboardView!!.keyboard = normalKeyboard
                    mKeyboardState = R.integer.keyboard_normal
                }
                controlKeyUpdateView()
                shiftKeyUpdateView()

            }

            17 -> {
                // Ctrl key
                if (isCtrlOn)
                    ic.sendKeyEventOnce(
                        KeyEvent.ACTION_UP,
                        KEYCODE_CTRL_LEFT,
                        META_CTRL_ON
                    )
                else
                    ic.sendKeyEventOnce(
                        KeyEvent.ACTION_DOWN,
                        KEYCODE_CTRL_LEFT,
                        META_CTRL_ON
                    )

                isCtrlOn = !isCtrlOn
                controlKeyUpdateView()
            }

            16 -> {
                // Log.e("CodeBoardIME", "onKey" + Boolean.toString(shiftLock));
                //Shift - runs after long press, so shiftlock may have just been activated
                if (isShiftOn)
                    ic.sendKeyEventOnce(
                        KeyEvent.ACTION_UP,
                        KEYCODE_SHIFT_LEFT,
                        META_SHIFT_ON
                    )
                else {
                    ic.sendKeyEventOnce(
                        KeyEvent.ACTION_DOWN,
                        KEYCODE_SHIFT_LEFT,
                        META_SHIFT_ON
                    )
                }

                if (shiftLock) {
                    isShiftOn = true
                    shiftKeyUpdateView()
                } else {
                    isShiftOn = !isShiftOn
                    shiftKeyUpdateView()
                }
            }

            9 ->
                //tab
                // ic.commitText("\u0009", 1);
                sendDownUpKeyEvents(KeyEvent.KEYCODE_TAB)

            5000 -> handleArrow(KeyEvent.KEYCODE_DPAD_LEFT)
            5001 -> sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_DOWN)
            5002 -> sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_UP)
            5003 -> handleArrow(KeyEvent.KEYCODE_DPAD_RIGHT)

            else -> {
                val code = primaryCode.toChar()
                when {
                    isCtrlOn -> {
                        onKeyCtrl(primaryCode, ic)
                        if (!shiftLock) {
                            isShiftOn = false
                            ic.sendKeyEventOnce(
                                KeyEvent.ACTION_UP,
                                KEYCODE_SHIFT_LEFT,
                                META_SHIFT_ON
                            )

                            shiftKeyUpdateView()
                        }
                        isCtrlOn = false
                        controlKeyUpdateView()
                    }
                    code.isLetter() && isShiftOn -> {
                        ic.commitText("${code.toUpperCase()}", 1)
                        if (!shiftLock) {

                            isShiftOn = false
                            ic.sendKeyEventOnce(
                                KeyEvent.ACTION_UP,
                                KEYCODE_SHIFT_LEFT,
                                META_SHIFT_ON
                            )

                            //Log.e("CodeboardIME", "Unshifted b/c no lock");
                        }

                        shiftKeyUpdateView()
                    }
                    else -> {
                        if (!switchedKeyboard) {
                            ic.commitText("$code", 1)
                        }
                        switchedKeyboard = false
                    }
                }
            }
        }

    }

    override fun onPress(primaryCode: Int) {
        if (preferences.isSoundOn) {
            val keypressSoundPlayer = MediaPlayer.create(this, R.raw.keypress_sound)
            keypressSoundPlayer.start()
            keypressSoundPlayer.setOnCompletionListener { mp -> mp.release() }
        }
        if (preferences.isVibrateOn) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            @Suppress("DEPRECATION")
            vibrator.vibrate(20)
        }

        timerLongPress?.cancel()

        timerLongPress = Timer().apply {
            schedule(
                LongKeyPressTimerTask(primaryCode),
                ViewConfiguration.getLongPressTimeout().toLong()
            )
        }
    }

    override fun onRelease(primaryCode: Int) {
        timerLongPress?.cancel()
        timerLongPress = null
    }

    @Suppress("DEPRECATION")
    fun onKeyLongPress(keyCode: Int) {
        // Process long-click here
        if (keyCode == 16) {
            shiftLock = !shiftLock
            //Log.e("CodeBoardIME", "long press" + Boolean.toString(shiftLock));
            //and onKey will now happen
        }

        if (keyCode == 32) {
            switchedKeyboard = true
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(50)
    }

    override fun onText(text: CharSequence) {
        val ic = currentInputConnection
        if (text.toString().contains("for")) {
            ic.commitText(text, 1)
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)

        } else {
            ic.commitText(text, 1)
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
        }
    }

    override fun swipeDown() {

        keyboardView!!.closing()
    }

    override fun swipeLeft() {

    }

    override fun swipeRight() {}

    override fun swipeUp() {

    }

    private fun chooseKeyboard(@IntegerRes mode: Int): Keyboard {
        @IntRange(from = 0L, to = 1L)
        val topRowIndex = if (preferences.isDpadOn) 1 else 0
        val sizeIndex = preferences.keyboardSize
        @XmlRes
        val keyboardXmlRes = if (preferences.selectedKeyboardLayoutIndex == 0) {
            QWERTY_KEYBOARDS[sizeIndex][topRowIndex]
        } else {
            AZERTY_KEYBOARDS[sizeIndex][topRowIndex]
        }
        return Keyboard(this, keyboardXmlRes, mode)
    }

    override fun onCreateInputView(): View? {
        @LayoutRes
        val keyboardLayoutRes =
            KEYBOARD_LAYOUT_RESES.getOrElse(preferences.selectedKeyboardColorIndex) { R.layout.keyboard }
        val keyboardView =
            layoutInflater.inflate(keyboardLayoutRes, null) as? KeyboardView ?: return null
        this.keyboardView = keyboardView

        keyboardView.isPreviewEnabled = preferences.isPreviewEnabled

        isShiftOn = false
        isCtrlOn = false

        mKeyboardState = R.integer.keyboard_normal
        //reset to normal

        val keyboard = chooseKeyboard(mKeyboardState)
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

        val index = nonNullKeyboard.keys.indexOfFirst { it.label == "Ctrl" || it.label == "CTRL" }
        nonNullKeyboard.keys.getOrNull(index)?.label = if (isCtrlOn) "CTRL" else "Ctrl"
        nonNullKeyboardView.invalidateKey(index)
    }

    private fun shiftKeyUpdateView() {

        keyboard = keyboardView!!.keyboard
        val keys = keyboard!!.keys
        for (i in keys.indices) {
            if (isShiftOn) {
                if (keys[i].label != null && keys[i].label == "Shft") {
                    keys[i].label = "SHFT"
                    break
                }
            } else {
                if (keys[i].label != null && keys[i].label == "SHFT") {
                    keys[i].label = "Shft"
                    break
                }
            }
        }
        keyboard!!.isShifted = isShiftOn
        keyboardView!!.invalidateAllKeys()
    }

    private fun handleArrow(keyCode: Int) {
        val ic = currentInputConnection ?: return
        when {
            isCtrlOn && isShiftOn -> {
                ic.sendKeyEventOnce(
                    KeyEvent.ACTION_DOWN,
                    KEYCODE_CTRL_LEFT,
                    META_SHIFT_ON or META_CTRL_ON
                )
                moveSelection(keyCode)
                ic.sendKeyEventOnce(
                    KeyEvent.ACTION_UP,
                    KEYCODE_CTRL_LEFT,
                    META_SHIFT_ON or META_CTRL_ON
                )

            }
            isShiftOn -> moveSelection(keyCode)
            isCtrlOn -> ic.sendKeyEventOnce(KeyEvent.ACTION_DOWN, keyCode, META_CTRL_ON)
            else -> sendDownUpKeyEvents(keyCode)
        }
    }

    private fun moveSelection(keyCode: Int) {
        //        inputMethodService.sendDownKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, 0);
        //        inputMethodService.sendDownAndUpKeyEvent(dpad_keyCode, 0);
        //        inputMethodService.sendUpKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, 0);
        val ic = currentInputConnection ?: return
        ic.sendKeyEventOnce(
            KeyEvent.ACTION_DOWN,
            KEYCODE_SHIFT_LEFT,
            META_SHIFT_ON or META_CTRL_ON
        )

        val metaState = if (isCtrlOn) META_SHIFT_ON or META_CTRL_ON else META_SHIFT_ON
        ic.sendKeyEventOnce(
            KeyEvent.ACTION_DOWN,
            keyCode,
            metaState
        )

        ic.sendKeyEventOnce(
            KeyEvent.ACTION_UP,
            KEYCODE_SHIFT_LEFT,
            META_SHIFT_ON or META_CTRL_ON
        )
    }

    private fun InputConnection.sendKeyEventOnce(
        action: Int,
        code: Int,
        metaState: Int,
        sendingTimeMillis: Long = System.currentTimeMillis()
    ) {
        val keyEvent = KeyEvent(
            sendingTimeMillis,
            sendingTimeMillis,
            action,
            code,
            0,
            metaState
        )
        sendKeyEvent(keyEvent)
    }

    private fun EditorInfo.isDroidEdit(): Boolean = imeOptions == DROID_EDIT_IME_OPTIONS

    private inner class LongKeyPressTimerTask(private val primaryCode: Int) : TimerTask() {
        override fun run() {
            uiHandler.post {
                try {
                    onKeyLongPress(primaryCode)
                } catch (e: Exception) {
                    Log.e("CodeBoardIME", "uiHandler.run: " + e.message, e)
                }
            }
        }
    }

    companion object {
        private const val DROID_EDIT_IME_OPTIONS = 1342177286

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
    }
}
