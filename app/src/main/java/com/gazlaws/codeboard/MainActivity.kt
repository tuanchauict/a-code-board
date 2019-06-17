package com.gazlaws.codeboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar

/**
 * Created by Ruby on 02/06/2016.
 */
class MainActivity : AppCompatActivity() {
    private val preferences: Preferences by lazy { Preferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onFirstOpenApp()

        findViewById<SeekBar>(R.id.size_seekbar).apply {
            progress = preferences.keyboardSize
            setOnSeekBarChangeListener(OnSeekBarChangeListener())
        }

        findViewById<RadioGroup>(R.id.radiogroupcolour).apply {
            setSelectedChild(preferences.selectedKeyboardColorIndex)
            setOnCheckedChangeListener { _, checkedId ->
                preferences.selectedKeyboardColorIndex = getSelectedItemIndexById(checkedId)
            }
        }

        findViewById<RadioGroup>(R.id.radiogrouplayout).apply {
            setSelectedChild(preferences.selectedKeyboardLayoutIndex)
            setOnCheckedChangeListener { _, checkedId ->
                preferences.selectedKeyboardLayoutIndex = getSelectedItemIndexById(checkedId)
            }
        }

        findViewById<CheckBox>(R.id.check_preview).apply {
            isChecked = preferences.isPreviewEnabled
            setOnClickListener {
                preferences.isPreviewEnabled = isChecked
                closeKeyboard(it)
            }
        }

        findViewById<CheckBox>(R.id.check_sound).apply {
            isChecked = preferences.isSoundOn
            setOnClickListener {
                preferences.isSoundOn = isChecked
                closeKeyboard(it)
            }
        }

        findViewById<CheckBox>(R.id.check_vibrator).apply {
            isChecked = preferences.isVibrateOn
            setOnClickListener {
                preferences.isVibrateOn = isChecked
                closeKeyboard(it)
            }
        }

        findViewById<CheckBox>(R.id.check_no_arrow).apply {
            isChecked = preferences.isDpadOn
            setOnClickListener {
                preferences.isDpadOn = isChecked
                closeKeyboard(it)
            }
        }

        findViewById<View>(R.id.change_keyboard).apply {
            setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
        }
    }

    private fun onFirstOpenApp() {
        if (!preferences.isFirstTimeAppOpen) {
            return
        }
        preferences.isFirstTimeAppOpen = false

        findViewById<View>(R.id.change_keyboard).visibility = View.GONE

        //  Launch app intro
        Intent(this, IntroActivity::class.java).also(::startActivity)
    }

    fun closeKeyboard(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun openPlay(v: View) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("market://details?id=com.gazlaws.codeboard")
        startActivity(i)
    }

    fun openTutorial(v: View) {
        val i = Intent(this@MainActivity, IntroActivity::class.java)
        startActivity(i)
    }

    fun openGithub(v: View) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gazlaws-dev/codeboard"))

        startActivity(i)
    }

    private fun RadioGroup.getSelectedItemIndexById(@IdRes selectedChildId: Int): Int {
        val selectedChildView = findViewById<View>(selectedChildId)
        return indexOfChild(selectedChildView)
    }

    private fun RadioGroup.setSelectedChild(childIndex: Int) {
        (getChildAt(childIndex) as? RadioButton)?.isChecked = true
    }

    private inner class OnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

        override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            preferences.keyboardSize = seekBar.progress
        }
    }
}
