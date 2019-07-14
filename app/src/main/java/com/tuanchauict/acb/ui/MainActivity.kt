package com.tuanchauict.acb.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import com.tuanchauict.acb.Preferences
import com.tuanchauict.acb.R

/**
 * Created by Ruby on 02/06/2016.
 * Kotlinized by Tuan Chau
 *
 * TODO: Update this class doc
 */
class MainActivity : AppCompatActivity() {
    private val preferences: Preferences by lazy {
        Preferences(
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(this, MainActivity2::class.java).let(::startActivity)
        setContentView(R.layout.activity_main)

        onFirstOpenApp()

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

        findViewById<View>(R.id.change_keyboard).apply {
            setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
        }

        findViewById<View>(R.id.open_github).apply {
            setOnClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL)).let(::startActivity)
            }
        }

        findViewById<View>(R.id.open_play_store).apply {
            setOnClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL)).let(::startActivity)
            }
        }

        findViewById<View>(R.id.open_tutorial).apply {
            setOnClickListener {
                Intent(this@MainActivity, IntroActivity::class.java).let(::startActivity)
            }
        }
    }

    private fun onFirstOpenApp() {
        if (!preferences.isFirstTimeAppOpen) {
            return
        }
        preferences.isFirstTimeAppOpen = false

        findViewById<View>(R.id.change_keyboard).visibility = View.GONE

        // Launch app intro
        Intent(this, IntroActivity::class.java).also(::startActivity)
    }

    private fun closeKeyboard(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    companion object {
        private const val GITHUB_URL = "https://github.com/tuanchauict/codeboard"
        private const val PLAY_STORE_URL = "market://details?id=com.gazlaws.codeboard"
    }
}
