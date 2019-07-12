package com.tuanchauict.acb.ime

import android.content.Context
import android.inputmethodservice.KeyboardView
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import com.tuanchauict.acb.Preferences

class KeyboardActionListener(
    private val context: Context,
    private val keyboardView: KeyboardView,
    private val longPressKeyCodes: Set<Int>,
    private val preferences: Preferences,
    private val onKeyAction: (keyCode: Int) -> Unit,
    private val onKeyLongPressAction: (keyCode: Int) -> Unit
) : KeyboardView.OnKeyboardActionListener {
    private val uiHandler: Handler = Handler(Looper.getMainLooper())

    private var isLongPressSuccess: Boolean = false

    override fun swipeLeft() = Unit
    override fun swipeRight() = Unit
    override fun swipeUp() = Unit
    override fun swipeDown() = Unit

    override fun onPress(primaryCode: Int) {
        if (primaryCode in Keycode.NO_PREVIEW_KEY_CODES) {
            keyboardView.isPreviewEnabled = false
        } else {
            keyboardView.isPreviewEnabled = preferences.isPreviewEnabled
        }

        uiHandler.removeCallbacksAndMessages(null)
        isLongPressSuccess = false
        if (primaryCode in longPressKeyCodes) {
            uiHandler.postDelayed({
                isLongPressSuccess = true
                onKeyLongPress(primaryCode)
            }, DEFAULT_LONG_PRESS_DURATION_MILLIS)
        }
    }

    override fun onRelease(primaryCode: Int) = uiHandler.removeCallbacksAndMessages(null)


    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        if (isLongPressSuccess) {
            return
        }
        if (preferences.isVibrateOn) {
            vibrate(KEY_PRESS_VIBRATION_DURATION)
        }
        onKeyAction(primaryCode)
    }

    private fun onKeyLongPress(keyCode: Int) {
        onKeyLongPressAction(keyCode)
        vibrate(KEY_LONG_PRESS_VIBRATION_DURATION)
    }

    override fun onText(text: CharSequence?) = Unit

    @Suppress("DEPRECATION")
    private fun vibrate(durationMillis: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(durationMillis)
    }

    companion object {
        private const val DEFAULT_LONG_PRESS_DURATION_MILLIS = 300L
        private const val KEY_PRESS_VIBRATION_DURATION = 20L
        private const val KEY_LONG_PRESS_VIBRATION_DURATION = 50L
    }
}
