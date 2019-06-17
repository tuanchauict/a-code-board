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
    private var isVibratorOn: Boolean = false
    private var isSoundOn: Boolean = false
    private var shiftLock = false
    private var isShiftOn = false
    private var isCtrlOn = false
    private var mKeyboardState = R.integer.keyboard_normal
    // TODO: Change this to enum
    private var mLayout: Int = 0
    // TODO: Change this to boolean
    private var mToprow: Int = 0
    // TODO: Change this to enum
    private var mSize: Int = 0
    private var timerLongPress: Timer? = null
    private var switchedKeyboard = false

    private fun onKeyCtrl(code: Int, ic: InputConnection?) {
        var codeChar = code.toChar()
        val now2 = System.currentTimeMillis()
        when (codeChar) {
            'a', 'A' -> if (sEditorInfo.imeOptions == 1342177286)
            //fix for DroidEdit
            {
                currentInputConnection.performContextMenuAction(android.R.id.selectAll)
            } else
                ic!!.sendKeyEvent(
                    KeyEvent(
                        now2 + 1,
                        now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_A,
                        0,
                        META_CTRL_ON
                    )
                )
            'c', 'C' -> if (sEditorInfo.imeOptions == 1342177286)
            //fix for DroidEdit
            {
                currentInputConnection.performContextMenuAction(android.R.id.copy)
            } else
                ic!!.sendKeyEvent(
                    KeyEvent(
                        now2 + 1,
                        now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_C,
                        0,
                        META_CTRL_ON
                    )
                )
            'v', 'V' -> if (sEditorInfo.imeOptions == 1342177286)
            //fix for DroidEdit
            {
                currentInputConnection.performContextMenuAction(android.R.id.paste)
            } else
                ic!!.sendKeyEvent(
                    KeyEvent(
                        now2 + 1,
                        now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_V,
                        0,
                        META_CTRL_ON
                    )
                )
            'x', 'X' -> if (sEditorInfo.imeOptions == 1342177286)
            //fix for DroidEdit
            {
                currentInputConnection.performContextMenuAction(android.R.id.cut)
            } else
                ic!!.sendKeyEvent(
                    KeyEvent(
                        now2 + 1,
                        now2 + 1,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_X,
                        0,
                        META_CTRL_ON
                    )
                )
            'z', 'Z' -> if (isShiftOn) {
                if (ic != null) {
                    if (sEditorInfo.imeOptions == 1342177286)
                    //fix for DroidEdit
                    {
                        currentInputConnection.performContextMenuAction(android.R.id.redo)
                    } else
                        ic.sendKeyEvent(
                            KeyEvent(
                                now2 + 1,
                                now2 + 1,
                                KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_Z,
                                0,
                                META_CTRL_ON or META_SHIFT_ON
                            )
                        )

                    val nowS = System.currentTimeMillis()
                    isShiftOn = false
                    ic.sendKeyEvent(
                        KeyEvent(
                            nowS,
                            nowS,
                            KeyEvent.ACTION_UP,
                            KEYCODE_SHIFT_LEFT,
                            0,
                            META_SHIFT_ON
                        )
                    )

                    shiftLock = false
                    shiftKeyUpdateView()
                }
            } else {
                //Log.e("isCtrlOn", "z");
                if (sEditorInfo.imeOptions == 1342177286)
                //fix for DroidEdit
                {
                    currentInputConnection.performContextMenuAction(android.R.id.undo)
                } else
                    ic!!.sendKeyEvent(
                        KeyEvent(
                            now2 + 1,
                            now2 + 1,
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_Z,
                            0,
                            META_CTRL_ON
                        )
                    )

            }

            'b' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_B, 0, META_CTRL_ON
                )
            )

            'd' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_D, 0, META_CTRL_ON
                )
            )

            'e' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_E, 0, META_CTRL_ON
                )
            )
            'f' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_F, 0, META_CTRL_ON
                )
            )
            'g' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_G, 0, META_CTRL_ON
                )
            )
            'h' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_H, 0, META_CTRL_ON
                )
            )
            'i' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_I, 0, META_CTRL_ON
                )
            )
            'j' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_J, 0, META_CTRL_ON
                )
            )

            'k' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_K, 0, META_CTRL_ON
                )
            )
            'l' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_L, 0, META_CTRL_ON
                )
            )
            'm' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_M, 0, META_CTRL_ON
                )
            )
            'n' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_N, 0, META_CTRL_ON
                )
            )

            'o' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_O, 0, META_CTRL_ON
                )
            )
            'p' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_P, 0, META_CTRL_ON
                )
            )


            'q' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_P, 0, META_CTRL_ON
                )
            )
            'r' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_R, 0, META_CTRL_ON
                )
            )

            's' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_S, 0, META_CTRL_ON
                )
            )

            't' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_T, 0, META_CTRL_ON
                )
            )

            'u' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_U, 0, META_CTRL_ON
                )
            )

            'w' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_W, 0, META_CTRL_ON
                )
            )


            'y' -> ic!!.sendKeyEvent(
                KeyEvent(
                    now2 + 1, now2 + 1,
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_Y, 0, META_CTRL_ON
                )
            )

            else -> if (Character.isLetter(codeChar) && isShiftOn) {
                codeChar = Character.toUpperCase(codeChar)
                ic!!.commitText(codeChar.toString(), 1)
                if (!shiftLock) {
                    val nowS = System.currentTimeMillis()
                    isShiftOn = false
                    ic.sendKeyEvent(
                        KeyEvent(
                            nowS,
                            nowS,
                            KeyEvent.ACTION_UP,
                            KEYCODE_SHIFT_LEFT,
                            0,
                            META_SHIFT_ON
                        )
                    )

                    //Log.e("CodeboardIME", "Unshifted b/c no lock");
                }
                shiftKeyUpdateView()
            }
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
                //Escape
                val now = System.currentTimeMillis()
                ic.sendKeyEvent(
                    KeyEvent(
                        now,
                        now,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ESCAPE,
                        0,
                        META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
                    )
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
                        chooseKeyboard(mLayout, mToprow, mSize, R.integer.keyboard_sym)

                    keyboardView!!.keyboard = symbolKeyboard

                    mKeyboardState = R.integer.keyboard_sym
                } else if (mKeyboardState == R.integer.keyboard_sym) {
                    //change to normal keyboard
                    val normalKeyboard =
                        chooseKeyboard(mLayout, mToprow, mSize, R.integer.keyboard_normal)

                    keyboardView!!.keyboard = normalKeyboard
                    mKeyboardState = R.integer.keyboard_normal
                }
                controlKeyUpdateView()
                shiftKeyUpdateView()

            }

            17 -> {
                //              isCtrlOn key
                val nowCtrl = System.currentTimeMillis()
                if (isCtrlOn)
                    ic.sendKeyEvent(
                        KeyEvent(
                            nowCtrl,
                            nowCtrl,
                            KeyEvent.ACTION_UP,
                            KEYCODE_CTRL_LEFT,
                            0,
                            META_CTRL_ON
                        )
                    )
                else
                    ic.sendKeyEvent(
                        KeyEvent(
                            nowCtrl,
                            nowCtrl,
                            KeyEvent.ACTION_DOWN,
                            KEYCODE_CTRL_LEFT,
                            0,
                            META_CTRL_ON
                        )
                    )

                isCtrlOn = !isCtrlOn
                controlKeyUpdateView()
            }

            16 -> {
                // Log.e("CodeBoardIME", "onKey" + Boolean.toString(shiftLock));
                //Shift - runs after long press, so shiftlock may have just been activated
                val nowShift = System.currentTimeMillis()
                if (isShiftOn)
                    ic.sendKeyEvent(
                        KeyEvent(
                            nowShift,
                            nowShift,
                            KeyEvent.ACTION_UP,
                            KEYCODE_SHIFT_LEFT,
                            0,
                            META_SHIFT_ON
                        )
                    )
                else
                    ic.sendKeyEvent(
                        KeyEvent(
                            nowShift,
                            nowShift,
                            KeyEvent.ACTION_DOWN,
                            KEYCODE_SHIFT_LEFT,
                            0,
                            META_SHIFT_ON
                        )
                    )

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
                var code = primaryCode.toChar()
                if (isCtrlOn) {
                    onKeyCtrl(code.toInt(), ic)
                    if (!shiftLock) {
                        val nowS = System.currentTimeMillis()
                        isShiftOn = false
                        ic.sendKeyEvent(
                            KeyEvent(
                                nowS,
                                nowS,
                                KeyEvent.ACTION_UP,
                                KEYCODE_SHIFT_LEFT,
                                0,
                                META_SHIFT_ON
                            )
                        )

                        shiftKeyUpdateView()
                    }
                    isCtrlOn = false
                    controlKeyUpdateView()
                } else if (Character.isLetter(code) && isShiftOn) {
                    code = Character.toUpperCase(code)
                    ic.commitText(code.toString(), 1)
                    if (!shiftLock) {

                        val nowS = System.currentTimeMillis()
                        isShiftOn = false
                        ic.sendKeyEvent(
                            KeyEvent(
                                nowS,
                                nowS,
                                KeyEvent.ACTION_UP,
                                KEYCODE_SHIFT_LEFT,
                                0,
                                META_SHIFT_ON
                            )
                        )

                        //Log.e("CodeboardIME", "Unshifted b/c no lock");
                    }

                    shiftKeyUpdateView()
                } else {
                    if (!switchedKeyboard) {
                        ic.commitText(code.toString(), 1)
                    }
                    switchedKeyboard = false
                }
            }
        }

    }

    override fun onPress(primaryCode: Int) {

        if (isSoundOn) {
            val keypressSoundPlayer = MediaPlayer.create(this, R.raw.keypress_sound)
            keypressSoundPlayer.start()
            keypressSoundPlayer.setOnCompletionListener { mp -> mp.release() }
        }
        if (isVibratorOn) {

            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            @Suppress("DEPRECATION")
            vibrator.vibrate(20)
        }
        if (timerLongPress != null)
            timerLongPress!!.cancel()

        timerLongPress = Timer()

        timerLongPress!!.schedule(object : TimerTask() {

            override fun run() {

                try {

                    val uiHandler = Handler(Looper.getMainLooper())

                    val runnable = Runnable {
                        try {

                            this@CodeBoardIME.onKeyLongPress(primaryCode)

                        } catch (e: Exception) {
                            Log.e(
                                CodeBoardIME::class.java.simpleName,
                                "uiHandler.run: " + e.message,
                                e
                            )
                        }
                    }

                    uiHandler.post(runnable)

                } catch (e: Exception) {
                    Log.e(CodeBoardIME::class.java.simpleName, "Timer.run: " + e.message, e)
                }

            }

        }, ViewConfiguration.getLongPressTimeout().toLong())

    }

    override fun onRelease(primaryCode: Int) {
        if (timerLongPress != null)
            timerLongPress!!.cancel()

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

    private fun chooseKeyboard(
        @IntRange(from = 0L, to = 1L) layout: Int,
        @IntRange(from = 0L, to = 1L) toprow: Int,
        @IntRange(from = 0L, to = 3L) size: Int,
        mode: Int
    ): Keyboard {
        @XmlRes
        val keyboardXmlRes = if (layout == 0) {
            QWERTY_KEYBOARDS[size][toprow]
        } else {
            AZERTY_KEYBOARDS[size][toprow]
        }
        return Keyboard(this, keyboardXmlRes, mode)
    }

    override fun onCreateInputView(): View? {
        val preferences = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE)
        val radioColorIndex = preferences.getInt(KEY_RADIO_INDEX_COLOUR, 0)
        @LayoutRes
        val keyboardLayoutRes =
            KEYBOARD_LAYOUT_RESES.getOrElse(radioColorIndex) { R.layout.keyboard }
        val keyboardView =
            layoutInflater.inflate(keyboardLayoutRes, null) as? KeyboardView ?: return null
        this.keyboardView = keyboardView

        // TODO: Change this preference to Boolean
        keyboardView.isPreviewEnabled = preferences.getInt(KEY_PREVIEW, 0) == 1

        // TODO: Change this preference to Boolean
        isSoundOn = preferences.getInt(KEY_SOUND, 1) == 1

        // TODO: Change this preference to Boolean
        isVibratorOn = preferences.getInt(KEY_VIBRATE, 1) == 1

        isShiftOn = false
        isCtrlOn = false

        mLayout = preferences.getInt(KEY_RADIO_INDEX_LAYOUT, 0)
        mSize = preferences.getInt(KEY_SIZE, 2)
        mToprow = preferences.getInt(KEY_ARROW_ROW, 1)
        mKeyboardState = R.integer.keyboard_normal
        //reset to normal

        val keyboard = chooseKeyboard(mLayout, mToprow, mSize, mKeyboardState)
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
        keyboard = keyboardView!!.keyboard
        var i = 0
        val keys = keyboard!!.keys
        while (i < keys.size) {
            if (isCtrlOn) {
                if (keys[i].label != null && keys[i].label == "Ctrl") {
                    keys[i].label = "CTRL"
                    break
                }
            } else {
                if (keys[i].label != null && keys[i].label == "CTRL") {
                    keys[i].label = "Ctrl"
                    break
                }
            }
            i++
        }
        keyboardView!!.invalidateKey(i)
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
        val ic = currentInputConnection
        val now2 = System.currentTimeMillis()
        if (isCtrlOn && isShiftOn) {
            ic.sendKeyEvent(
                KeyEvent(
                    now2,
                    now2,
                    KeyEvent.ACTION_DOWN,
                    KEYCODE_CTRL_LEFT,
                    0,
                    META_SHIFT_ON or META_CTRL_ON
                )
            )
            moveSelection(keyCode)
            ic.sendKeyEvent(
                KeyEvent(
                    now2,
                    now2,
                    KeyEvent.ACTION_UP,
                    KEYCODE_CTRL_LEFT,
                    0,
                    META_SHIFT_ON or META_CTRL_ON
                )
            )

        } else if (isShiftOn)
            moveSelection(keyCode)
        else if (isCtrlOn)
            ic.sendKeyEvent(KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, keyCode, 0, META_CTRL_ON))
        else {
            sendDownUpKeyEvents(keyCode)
        }
    }

    private fun moveSelection(keyCode: Int) {
        //        inputMethodService.sendDownKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, 0);
        //        inputMethodService.sendDownAndUpKeyEvent(dpad_keyCode, 0);
        //        inputMethodService.sendUpKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, 0);
        val ic = currentInputConnection
        val now2 = System.currentTimeMillis()
        ic.sendKeyEvent(
            KeyEvent(
                now2,
                now2,
                KeyEvent.ACTION_DOWN,
                KEYCODE_SHIFT_LEFT,
                0,
                META_SHIFT_ON or META_CTRL_ON
            )
        )
        if (isCtrlOn)
            ic.sendKeyEvent(
                KeyEvent(
                    now2,
                    now2,
                    KeyEvent.ACTION_DOWN,
                    keyCode,
                    0,
                    META_SHIFT_ON or META_CTRL_ON
                )
            )
        else
            ic.sendKeyEvent(KeyEvent(now2, now2, KeyEvent.ACTION_DOWN, keyCode, 0, META_SHIFT_ON))
        ic.sendKeyEvent(
            KeyEvent(
                now2,
                now2,
                KeyEvent.ACTION_UP,
                KEYCODE_SHIFT_LEFT,
                0,
                META_SHIFT_ON or META_CTRL_ON
            )
        )
    }

    companion object {
        private const val SHARED_PREF_FILE = "MY_SHARED_PREF"
        private const val KEY_RADIO_INDEX_COLOUR = "RADIO_INDEX_COLOUR"
        private const val KEY_PREVIEW = "PREVIEW"
        private const val KEY_SOUND = "SOUND"
        private const val KEY_VIBRATE = "VIBRATE"
        private const val KEY_RADIO_INDEX_LAYOUT = "RADIO_INDEX_LAYOUT"
        private const val KEY_SIZE = "SIZE"
        private const val KEY_ARROW_ROW = "ARROW_ROW"

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

    }
}
