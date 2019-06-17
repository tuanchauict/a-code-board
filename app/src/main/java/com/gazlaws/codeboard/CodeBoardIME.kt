package com.gazlaws.codeboard

import android.content.Context
import android.content.SharedPreferences
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.media.MediaPlayer // for keypress sound
import java.util.Timer
import java.util.TimerTask


import android.view.KeyEvent.KEYCODE_CTRL_LEFT
import android.view.KeyEvent.KEYCODE_SHIFT_LEFT
import android.view.KeyEvent.META_CTRL_ON
import android.view.KeyEvent.META_SHIFT_ON


/*Created by Ruby(aka gazlaws) on 13/02/2016.
 */


class CodeBoardIME : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    private var kv: KeyboardView? = null
    private var keyboard: Keyboard? = null
    internal var sEditorInfo: EditorInfo
    private var vibratorOn: Boolean = false
    private var soundOn: Boolean = false
    private var shiftLock = false
    private var shift = false
    private var ctrl = false
    private var mKeyboardState = R.integer.keyboard_normal
    private var mLayout: Int = 0
    private var mToprow: Int = 0
    private var mSize: Int = 0
    private var timerLongPress: Timer? = null
    private var switchedKeyboard = false


    fun onKeyCtrl(code: Int, ic: InputConnection?) {
        var code = code
        val now2 = System.currentTimeMillis()
        when (code) {
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
            'z', 'Z' -> if (shift) {
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
                    shift = false
                    ic.sendKeyEvent(
                        KeyEvent(
                            nowS,
                            nowS,
                            KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_SHIFT_LEFT,
                            0,
                            META_SHIFT_ON
                        )
                    )

                    shiftLock = false
                    shiftKeyUpdateView()
                }
            } else {
                //Log.e("ctrl", "z");
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

            else -> if (Character.isLetter(code) && shift) {
                code = Character.toUpperCase(code)
                ic!!.commitText(code.toString(), 1)
                if (!shiftLock) {
                    val nowS = System.currentTimeMillis()
                    shift = false
                    ic.sendKeyEvent(
                        KeyEvent(
                            nowS,
                            nowS,
                            KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_SHIFT_LEFT,
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
        keyboard = kv!!.keyboard

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
                        KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
                    )
                )
            }

            -13 -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.showInputMethodPicker()
            }
            -15 -> if (kv != null) {
                if (mKeyboardState == R.integer.keyboard_normal) {
                    //change to symbol keyboard
                    val symbolKeyboard = chooseKB(mLayout, mToprow, mSize, R.integer.keyboard_sym)

                    kv!!.keyboard = symbolKeyboard

                    mKeyboardState = R.integer.keyboard_sym
                } else if (mKeyboardState == R.integer.keyboard_sym) {
                    //change to normal keyboard
                    val normalKeyboard =
                        chooseKB(mLayout, mToprow, mSize, R.integer.keyboard_normal)

                    kv!!.keyboard = normalKeyboard
                    mKeyboardState = R.integer.keyboard_normal
                }
                controlKeyUpdateView()
                shiftKeyUpdateView()

            }

            17 -> {
                //              ctrl key
                val nowCtrl = System.currentTimeMillis()
                if (ctrl)
                    ic.sendKeyEvent(
                        KeyEvent(
                            nowCtrl,
                            nowCtrl,
                            KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_CTRL_LEFT,
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
                            KeyEvent.KEYCODE_CTRL_LEFT,
                            0,
                            META_CTRL_ON
                        )
                    )

                ctrl = !ctrl
                controlKeyUpdateView()
            }

            16 -> {
                // Log.e("CodeBoardIME", "onKey" + Boolean.toString(shiftLock));
                //Shift - runs after long press, so shiftlock may have just been activated
                val nowShift = System.currentTimeMillis()
                if (shift)
                    ic.sendKeyEvent(
                        KeyEvent(
                            nowShift,
                            nowShift,
                            KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_SHIFT_LEFT,
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
                            KeyEvent.KEYCODE_SHIFT_LEFT,
                            0,
                            META_SHIFT_ON
                        )
                    )

                if (shiftLock) {
                    shift = true
                    shiftKeyUpdateView()
                } else {
                    shift = !shift
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
                if (ctrl) {
                    onKeyCtrl(code.toInt(), ic)
                    if (!shiftLock) {
                        val nowS = System.currentTimeMillis()
                        shift = false
                        ic.sendKeyEvent(
                            KeyEvent(
                                nowS,
                                nowS,
                                KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_SHIFT_LEFT,
                                0,
                                META_SHIFT_ON
                            )
                        )

                        shiftKeyUpdateView()
                    }
                    ctrl = false
                    controlKeyUpdateView()
                } else if (Character.isLetter(code) && shift) {
                    code = Character.toUpperCase(code)
                    ic.commitText(code.toString(), 1)
                    if (!shiftLock) {

                        val nowS = System.currentTimeMillis()
                        shift = false
                        ic.sendKeyEvent(
                            KeyEvent(
                                nowS,
                                nowS,
                                KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_SHIFT_LEFT,
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

        if (soundOn) {
            val keypressSoundPlayer = MediaPlayer.create(this, R.raw.keypress_sound)
            keypressSoundPlayer.start()
            keypressSoundPlayer.setOnCompletionListener { mp -> mp.release() }
        }
        if (vibratorOn) {

            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator?.vibrate(20)
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
                                CodeBoardIME::class.java!!.getSimpleName(),
                                "uiHandler.run: " + e.message,
                                e
                            )
                        }
                    }

                    uiHandler.post(runnable)

                } catch (e: Exception) {
                    Log.e(CodeBoardIME::class.java!!.getSimpleName(), "Timer.run: " + e.message, e)
                }

            }

        }, ViewConfiguration.getLongPressTimeout().toLong())

    }

    override fun onRelease(primaryCode: Int) {
        if (timerLongPress != null)
            timerLongPress!!.cancel()

    }

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
            imm?.showInputMethodPicker()
        }

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(50)
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

        kv!!.closing()
    }

    override fun swipeLeft() {

    }

    override fun swipeRight() {}

    override fun swipeUp() {

    }

    fun chooseKB(layout: Int, toprow: Int, size: Int, mode: Int): Keyboard {
        val keyboard: Keyboard
        if (layout == 0) {

            if (toprow == 1) {

                if (size == 0) {
                    keyboard = Keyboard(this, R.xml.qwerty0r, mode)
                } else if (size == 1) {
                    keyboard = Keyboard(this, R.xml.qwerty1r, mode)
                } else if (size == 2) {
                    keyboard = Keyboard(this, R.xml.qwerty2r, mode)
                } else
                    keyboard = Keyboard(this, R.xml.qwerty3r, mode)
            } else {

                if (size == 0) {
                    keyboard = Keyboard(this, R.xml.qwerty0e, mode)
                } else if (size == 1) {
                    keyboard = Keyboard(this, R.xml.qwerty1e, mode)
                } else if (size == 2) {
                    keyboard = Keyboard(this, R.xml.qwerty2e, mode)
                } else
                    keyboard = Keyboard(this, R.xml.qwerty3e, mode)
            }
        } else {
            if (toprow == 1) {
                if (size == 0) {
                    keyboard = Keyboard(this, R.xml.azerty0r, mode)
                } else if (size == 1) {
                    keyboard = Keyboard(this, R.xml.azerty1r, mode)
                } else if (size == 2) {
                    keyboard = Keyboard(this, R.xml.azerty2r, mode)
                } else
                    keyboard = Keyboard(this, R.xml.azerty3r, mode)
            } else {
                if (size == 0) {
                    keyboard = Keyboard(this, R.xml.azerty0e, mode)
                } else if (size == 1) {
                    keyboard = Keyboard(this, R.xml.azerty1e, mode)
                } else if (size == 2) {
                    keyboard = Keyboard(this, R.xml.azerty2e, mode)
                } else
                    keyboard = Keyboard(this, R.xml.azerty3e, mode)
            }
        }
        return keyboard
    }

    override fun onCreateInputView(): View {

        val pre = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE)

        when (pre.getInt("RADIO_INDEX_COLOUR", 0)) {
            0 ->
                //kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
                kv = layoutInflater.inflate(R.layout.keyboard, null) as KeyboardView
            1 -> kv = layoutInflater.inflate(R.layout.keyboard1, null) as KeyboardView
            2 -> kv = layoutInflater.inflate(R.layout.keyboard2, null) as KeyboardView
            3 -> kv = layoutInflater.inflate(R.layout.keyboard3, null) as KeyboardView
            4 -> kv = layoutInflater.inflate(R.layout.keyboard4, null) as KeyboardView
            5 -> kv = layoutInflater.inflate(R.layout.keyboard5, null) as KeyboardView

            else -> kv = layoutInflater.inflate(R.layout.keyboard, null) as KeyboardView
        }

        if (pre.getInt("PREVIEW", 0) == 1) {
            kv!!.isPreviewEnabled = true
        } else
            kv!!.isPreviewEnabled = false

        if (pre.getInt("SOUND", 1) == 1) {
            soundOn = true
        } else
            soundOn = false

        if (pre.getInt("VIBRATE", 1) == 1) {
            vibratorOn = true
        } else
            vibratorOn = false

        shift = false
        ctrl = false

        mLayout = pre.getInt("RADIO_INDEX_LAYOUT", 0)
        mSize = pre.getInt("SIZE", 2)
        mToprow = pre.getInt("ARROW_ROW", 1)
        mKeyboardState = R.integer.keyboard_normal
        //reset to normal

        val keyboard = chooseKB(mLayout, mToprow, mSize, mKeyboardState)
        kv!!.keyboard = keyboard
        kv!!.setOnKeyboardActionListener(this)


        return kv
    }

    override fun onStartInputView(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInputView(attribute, restarting)
        setInputView(onCreateInputView())
        sEditorInfo = attribute

    }

    fun controlKeyUpdateView() {
        keyboard = kv!!.keyboard
        var i: Int
        val keys = keyboard!!.keys
        i = 0
        while (i < keys.size) {
            if (ctrl) {
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
        kv!!.invalidateKey(i)
    }

    fun shiftKeyUpdateView() {

        keyboard = kv!!.keyboard
        val keys = keyboard!!.keys
        for (i in keys.indices) {
            if (shift) {
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
        keyboard!!.isShifted = shift
        kv!!.invalidateAllKeys()
    }

    fun handleArrow(keyCode: Int) {
        val ic = currentInputConnection
        val now2 = System.currentTimeMillis()
        if (ctrl && shift) {
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

        } else if (shift)
            moveSelection(keyCode)
        else if (ctrl)
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
        if (ctrl)
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
}
