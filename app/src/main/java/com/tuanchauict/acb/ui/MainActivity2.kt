package com.tuanchauict.acb.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.tuanchauict.acb.BooleanMap
import com.tuanchauict.acb.R
import com.tuanchauict.acb.isVisible
import com.tuanchauict.acb.toast

class MainActivity2 : AppCompatActivity() {

    private lateinit var step1: StepViewController
    private lateinit var step2: StepViewController

    private lateinit var demoTextBox: EditText

    private val inputMethodManager: InputMethodManager
        get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)

        step1 = StepViewController(
            1,
            findViewById(R.id.step_1),
            R.string.step_1_checked,
            R.string.step_1_unchecked
        ) { Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).let(::startActivity) }
        step2 = StepViewController(
            2,
            findViewById(R.id.step_2),
            R.string.step_2_checked,
            R.string.step_2_unchecked
        ) { inputMethodManager.showInputMethodPicker() }

        demoTextBox = findViewById(R.id.demo_text_box)
    }

    override fun onResume() {
        super.onResume()
        updateStepsStates()
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            return
        }
        updateStepsStates()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_setting) {
            Intent(this, SettingsActivity::class.java).let(::startActivity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateStepsStates() {
        step1.isChecked = isCodeBoardEnabled()
        step2.isEnabled = step1.isChecked
        step2.isChecked = isCodeBoardSelected()
    }

    private fun isCodeBoardEnabled(): Boolean {
        return inputMethodManager.enabledInputMethodList.any { it.packageName == packageName }
    }

    private fun isCodeBoardSelected(): Boolean {
        val defaultIME =
            Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        val defaultInputMethod = ComponentName.unflattenFromString(defaultIME)
        return defaultInputMethod?.packageName == packageName
    }
}

private class StepViewController(
    nth: Int,
    rootView: View,
    @StringRes checkedStringRes: Int,
    @StringRes uncheckStringRes: Int,
    onClickAction: () -> Unit
) {
    private val texts: BooleanMap<Int> = BooleanMap(checkedStringRes, uncheckStringRes)

    private val checkedView: View = rootView.findViewById(R.id.step_checked)
    private val numberView: TextView = rootView.findViewById<TextView>(R.id.step_number).apply {
        text = nth.toString()
    }
    private val titleView: TextView = rootView.findViewById<TextView>(R.id.step_title).apply {
        setOnClickListener {
            if (isSelected) {
                context.toast(R.string.step_enabled_toast)
            } else {
                onClickAction()
            }
        }
    }

    var isChecked: Boolean = false
        set(value) {
            field = value
            checkedView.isVisible = value
            numberView.isVisible = !value
            titleView.isSelected = value
            titleView.setText(texts[value])
        }

    var isEnabled: Boolean = false
        set(value) {
            field = value
            titleView.isEnabled = value
        }
}
