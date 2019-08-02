package com.tuanchauict.acb.ui.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.tuanchauict.acb.Preferences
import com.tuanchauict.acb.R

class SettingsActivity : AppCompatActivity() {
    private val preferences: Preferences by lazy { Preferences(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initLongPressMovingCursor()
        initTabSize()
        initAutoClosePair()

        initSoundOnKeypress()
        initVibrationOnKeypress()
        initPopupOnKeypress()
        initLongPressDelay()
    }

    private fun initLongPressMovingCursor() {
        SettingSwitchItemController(
            findViewById(R.id.setting_long_press_moving_cursor),
            R.string.setting_long_press_moving_cursor_title,
            R.string.setting_long_press_moving_cursor_subtitle,
            preferences.isLongPressMovingCursor
        ) {
            preferences.isLongPressMovingCursor = !preferences.isLongPressMovingCursor
            setChecked(preferences.isLongPressMovingCursor)
        }
    }

    private fun initTabSize() {
        val progressInfo = SeekbarDialog.ProgressInfo(0, 3, null) {
            @StringRes val textRes = when (it) {
                0 -> R.string.setting_tab_size_tab
                1 -> R.string.setting_tab_size_2_spaces
                3 -> R.string.setting_tab_size_8_spaces
                else -> R.string.setting_tab_size_4_spaces
            }
            getString(textRes)
        }

        SettingSimpleClickItem(
            findViewById(R.id.setting_tab_size),
            R.string.setting_tab_size_title,
            progressInfo.toText(preferences.tabMode.value)
        ) {
            SeekbarDialog(
                this@SettingsActivity,
                R.string.setting_tab_size_title,
                preferences.tabMode.value,
                progressInfo
            ) {
                preferences.tabMode = Preferences.TabMode.fromValue(it)
                setSubtitle(progressInfo.toText(it))
            }
        }
    }

    private fun initAutoClosePair() {
        SettingSwitchItemController(
            findViewById(R.id.setting_auto_close_pair),
            R.string.setting_auto_close_pair_title,
            R.string.setting_auto_close_pair_subtitle,
            preferences.isAutoClosePair
        ) {
            preferences.isAutoClosePair = !preferences.isAutoClosePair
            setChecked(preferences.isAutoClosePair)
        }
    }

    private fun initSoundOnKeypress() {
        val progressInfo = SeekbarDialog.ProgressInfo(0, 100, 30) { "$it %" }
        val volumeOnKeypress = SettingSimpleClickItem(
            findViewById(R.id.volume_on_keypress),
            R.string.setting_volume_on_keypress,
            progressInfo.toText(preferences.soundVolume)
        ) {
            SeekbarDialog(
                this@SettingsActivity,
                R.string.setting_volume_on_keypress,
                preferences.soundVolume,
                progressInfo
            ) {
                preferences.soundVolume = it
                setSubtitle(progressInfo.toText(it))
            }
        }
        volumeOnKeypress.setEnabled(preferences.isSoundOn)

        SettingSwitchItemController(
            findViewById(R.id.setting_sound_on_keypress),
            R.string.sound_on_keypress,
            isChecked = preferences.isSoundOn
        ) {
            preferences.isSoundOn = !preferences.isSoundOn
            setChecked(preferences.isSoundOn)
            volumeOnKeypress.setEnabled(preferences.isSoundOn)
        }
    }

    private fun initVibrationOnKeypress() {
        val progressInfo = SeekbarDialog.ProgressInfo(10, 100, 20) { "$it ms" }

        val vibrationStrength = SettingSimpleClickItem(
            findViewById(R.id.vibrate_strength_on_keypress),
            R.string.setting_vibrate_strength_on_keypress,
            progressInfo.toText(preferences.vibrationStrength)
        ) {
            SeekbarDialog(
                this@SettingsActivity,
                R.string.setting_vibrate_strength_on_keypress,
                preferences.vibrationStrength,
                progressInfo
            ) {
                preferences.vibrationStrength = it
                setSubtitle(progressInfo.toText(it))
            }
        }
        vibrationStrength.setEnabled(preferences.isVibrateOn)

        SettingSwitchItemController(
            findViewById(R.id.setting_vibrate_on_keypress),
            R.string.setting_vibrate_on_keypress,
            isChecked = preferences.isVibrateOn
        ) {
            preferences.isVibrateOn = !preferences.isVibrateOn
            setChecked(preferences.isVibrateOn)
            vibrationStrength.setEnabled(preferences.isVibrateOn)
        }
    }

    private fun initPopupOnKeypress() {
        SettingSwitchItemController(
            findViewById(R.id.setting_popup_on_keypress),
            R.string.setting_popup_on_keypress,
            isChecked = preferences.isPreviewEnabled
        ) {
            preferences.isPreviewEnabled = !preferences.isPreviewEnabled
            setChecked(preferences.isPreviewEnabled)
        }
    }

    private fun initLongPressDelay() {
        val progressInfo = SeekbarDialog.ProgressInfo(150, 1000, 300) { "$it ms" }

        SettingSimpleClickItem(
            findViewById(R.id.key_long_press_delay),
            R.string.setting_long_press_delay,
            progressInfo.toText(preferences.longPressDelay)
        ) {
            SeekbarDialog(
                this@SettingsActivity,
                R.string.setting_long_press_delay,
                preferences.longPressDelay,
                progressInfo
            ) {
                preferences.longPressDelay = it
                setSubtitle(progressInfo.toText(it))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
