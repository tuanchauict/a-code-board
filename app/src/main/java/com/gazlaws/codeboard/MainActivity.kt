package com.gazlaws.codeboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
    private lateinit var radioGroupColour: RadioGroup
    private lateinit var radioGroupLayout: RadioGroup
    private lateinit var seekBar: SeekBar

    private var radioGroupOnCheckedChangeListenerColour: RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { _, checkedId ->
            val selectedIndex = radioGroupColour.findViewById<RadioButton>(checkedId)
            preferences.selectedKeyboardColorIndex = radioGroupColour.indexOfChild(selectedIndex)
        }

    private var radioGroupOnCheckedChangeListenerLayout: RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { _, checkedId ->
            val selectedIndex = radioGroupLayout.findViewById<RadioButton>(checkedId)
            preferences.selectedKeyboardLayoutIndex = radioGroupLayout.indexOfChild(selectedIndex)
        }

    private val preferences: Preferences by lazy { Preferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onFirstOpenApp()

        seekBar = findViewById<View>(R.id.size_seekbar) as SeekBar
        // perform seek bar change listener event used for getting the progress value
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChangedValue = seekBar.progress

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //                Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,
                //                        Toast.LENGTH_SHORT).show();
                savePreferences("SIZE", progressChangedValue)
            }
        })

        radioGroupColour = findViewById<View>(R.id.radiogroupcolour) as RadioGroup
        radioGroupColour.setOnCheckedChangeListener(radioGroupOnCheckedChangeListenerColour)

        radioGroupLayout = findViewById<View>(R.id.radiogrouplayout) as RadioGroup
        radioGroupLayout.setOnCheckedChangeListener(radioGroupOnCheckedChangeListenerLayout)

        loadPreferences()
    }

    private fun onFirstOpenApp() {
        if (!preferences.isFirstTimeAppOpen) {
            return
        }
        preferences.isFirstTimeAppOpen = false

        findViewById<View>(R.id.change_button).visibility = View.GONE

        //  Launch app intro
        Intent(this, IntroActivity::class.java).also(::startActivity)
    }

    private fun savePreferences(key: String, value: Int) {
        val sharedPreferences = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun changeButton(v: View) {

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showInputMethodPicker()

        //        Button enable = (Button) findViewById(R.id.enable_button);
        //        enable.setText("Change Keyboard");
        //
        //        String id = Settings.Secure.getString(
        //                getContentResolver(),
        //                Settings.Secure.DEFAULT_INPUT_METHOD
        //        );
        //
        //        if(!(id.equals("com.gazlaws.codeboard/.CodeBoardIME"))){
        //            InputMethodManager imm = (InputMethodManager)
        //                    getSystemService(Context.INPUT_METHOD_SERVICE);
        //            imm.showInputMethodPicker();
        //        }

    }

    fun previewToggle(v: View) {
        val preview = findViewById<View>(R.id.check_preview) as CheckBox
        if (preview.isChecked) {
            savePreferences("PREVIEW", 1)
        } else
            savePreferences("PREVIEW", 0)
        closeKeyboard(v)

    }

    fun soundToggle(v: View) {
        val preview = findViewById<View>(R.id.check_sound) as CheckBox
        if (preview.isChecked) {
            savePreferences("SOUND", 1)
        } else
            savePreferences("SOUND", 0)
        closeKeyboard(v)
    }

    fun vibratorToggle(v: View) {
        val preview = findViewById<View>(R.id.check_vibrator) as CheckBox
        if (preview.isChecked) {
            savePreferences("VIBRATE", 1)
        } else
            savePreferences("VIBRATE", 0)
        closeKeyboard(v)
    }

    fun arrowToggle(v: View) {
        val preview = findViewById<View>(R.id.check_no_arrow) as CheckBox
        if (preview.isChecked) {
            savePreferences("ARROW_ROW", 0)
        } else
            savePreferences("ARROW_ROW", 1)
        closeKeyboard(v)
    }

    fun closeKeyboard(v: View) {

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun loadPreferences() {
        val sharedPreferences = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE)

        val savedCheckedRadioButtonColour =
            radioGroupColour.getChildAt(preferences.selectedKeyboardColorIndex) as RadioButton
        savedCheckedRadioButtonColour.isChecked = true

        val savedCheckedRadioButtonLayout =
            radioGroupLayout.getChildAt(preferences.selectedKeyboardLayoutIndex) as RadioButton
        savedCheckedRadioButtonLayout.isChecked = true

        val setPreview = sharedPreferences.getInt("PREVIEW", 0)
        val setSound = sharedPreferences.getInt("SOUND", 1)
        val setVibrator = sharedPreferences.getInt("VIBRATE", 1)
        val setSize = sharedPreferences.getInt("SIZE", 2)

        val setArrow = sharedPreferences.getInt("ARROW_ROW", 1)
        val preview = findViewById<View>(R.id.check_preview) as CheckBox

        val sound = findViewById<View>(R.id.check_sound) as CheckBox
        val vibrate = findViewById<View>(R.id.check_vibrator) as CheckBox
        val noarrow = findViewById<View>(R.id.check_no_arrow) as CheckBox
        val size = findViewById<View>(R.id.size_seekbar) as SeekBar

        preview.isChecked = setPreview == 1

        sound.isChecked = setSound == 1

        vibrate.isChecked = setVibrator == 1

        noarrow.isChecked = setArrow != 1

        size.progress = setSize
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
}
