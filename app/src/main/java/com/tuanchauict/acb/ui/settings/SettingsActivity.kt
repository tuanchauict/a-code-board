package com.tuanchauict.acb.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tuanchauict.acb.Preferences
import com.tuanchauict.acb.R

class SettingsActivity : AppCompatActivity() {
    private val preferences: Preferences by lazy { Preferences(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        SettingSwitchItemController(findViewById(R.id.sound_on_keypress)) {
            preferences.isSoundOn = !preferences.isSoundOn
            update(R.string.setting_sound_on_keypress, 0, preferences.isSoundOn)
        }.apply {
            update(R.string.setting_sound_on_keypress, 0, preferences.isSoundOn)
        }

        SettingSwitchItemController(findViewById(R.id.vibrate_on_keypress)) {
            preferences.isVibrateOn = !preferences.isVibrateOn
            update(R.string.setting_vibrate_on_keypress, 0, preferences.isVibrateOn)
        }.apply {
            update(R.string.setting_vibrate_on_keypress, 0, preferences.isVibrateOn)
        }
    }
}
