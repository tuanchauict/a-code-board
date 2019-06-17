package com.gazlaws.codeboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar


/**
 * Created by Ruby on 02/06/2016.
 */
class MainActivity : AppCompatActivity() {
    internal var radioGroupColour: RadioGroup
    internal var radioGroupLayout: RadioGroup
    internal var seekBar: SeekBar


    internal val RADIO_INDEX_COLOUR = "RADIO_INDEX_COLOUR"
    internal val RADIO_INDEX_LAYOUT = "RADIO_INDEX_LAYOUT"


    internal var radioGroupOnCheckedChangeListenerColour: RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonColour =
                radioGroupColour.findViewById<View>(checkedId) as RadioButton
            val checkedIndexColour = radioGroupColour.indexOfChild(checkedRadioButtonColour)
            SavePreferences(RADIO_INDEX_COLOUR, checkedIndexColour)
        }

    internal var radioGroupOnCheckedChangeListenerLayout: RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonLayout =
                radioGroupLayout.findViewById<View>(checkedId) as RadioButton
            val checkedIndexLayout = radioGroupLayout.indexOfChild(checkedRadioButtonLayout)
            SavePreferences(RADIO_INDEX_LAYOUT, checkedIndexLayout)
        }


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //  Declare a new thread to do a preference check
        val t = Thread(Runnable {
            //  Initialize SharedPreferences
            val getPrefs = PreferenceManager
                .getDefaultSharedPreferences(baseContext)

            //  Create a new boolean and preference and set it to true
            val isFirstStart = getPrefs.getBoolean("firstStart1", true)

            //  If the activity has never started before...
            if (isFirstStart) {


                val change = findViewById<View>(R.id.change_button) as Button
                change.visibility = View.GONE

                //  Launch app intro
                val i = Intent(this@MainActivity, IntroActivity::class.java)
                startActivity(i)

                //  Make a new preferences editor
                val e = getPrefs.edit()

                //  Edit preference to make it false because we don't want this to run again
                e.putBoolean("firstStart1", false)

                //  Apply changes
                e.apply()

            } else {
                //Dev
                //SharedPreferences.Editor e = getPrefs.edit();
                //
                //e.putBoolean("firstStart1", true);
                //REMOVE BEFORE PUBLISHING ^
                //
                //e.apply();
            }
        })

        // Start the thread
        t.start()

        //debug only
        //        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        seekBar = findViewById<View>(R.id.size_seekbar) as SeekBar
        // perform seek bar change listener event used for getting the progress value
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            internal var progressChangedValue = seekBar.progress

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //                Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,
                //                        Toast.LENGTH_SHORT).show();
                SavePreferences("SIZE", progressChangedValue)


            }
        })

        radioGroupColour = findViewById<View>(R.id.radiogroupcolour) as RadioGroup
        radioGroupColour.setOnCheckedChangeListener(radioGroupOnCheckedChangeListenerColour)

        radioGroupLayout = findViewById<View>(R.id.radiogrouplayout) as RadioGroup
        radioGroupLayout.setOnCheckedChangeListener(radioGroupOnCheckedChangeListenerLayout)


        LoadPreferences()

    }


    private fun SavePreferences(key: String, value: Int) {
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
            SavePreferences("PREVIEW", 1)
        } else
            SavePreferences("PREVIEW", 0)
        closeKeyboard(v)

    }

    fun soundToggle(v: View) {
        val preview = findViewById<View>(R.id.check_sound) as CheckBox
        if (preview.isChecked) {
            SavePreferences("SOUND", 1)
        } else
            SavePreferences("SOUND", 0)
        closeKeyboard(v)
    }

    fun vibratorToggle(v: View) {
        val preview = findViewById<View>(R.id.check_vibrator) as CheckBox
        if (preview.isChecked) {
            SavePreferences("VIBRATE", 1)
        } else
            SavePreferences("VIBRATE", 0)
        closeKeyboard(v)
    }

    fun arrowToggle(v: View) {
        val preview = findViewById<View>(R.id.check_no_arrow) as CheckBox
        if (preview.isChecked) {
            SavePreferences("ARROW_ROW", 0)
        } else
            SavePreferences("ARROW_ROW", 1)
        closeKeyboard(v)
    }


    fun closeKeyboard(v: View) {

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(v.windowToken, 0)

    }

    private fun LoadPreferences() {
        val sharedPreferences = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE)

        val savedRadioColour = sharedPreferences.getInt(RADIO_INDEX_COLOUR, 0)
        val savedCheckedRadioButtonColour =
            radioGroupColour.getChildAt(savedRadioColour) as RadioButton
        savedCheckedRadioButtonColour.isChecked = true

        val savedRadioLayout = sharedPreferences.getInt(RADIO_INDEX_LAYOUT, 0)
        val savedCheckedRadioButtonLayout =
            radioGroupLayout.getChildAt(savedRadioLayout) as RadioButton
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

        if (setPreview == 1)
            preview.isChecked = true
        else
            preview.isChecked = false

        if (setSound == 1)
            sound.isChecked = true
        else
            sound.isChecked = false

        if (setVibrator == 1)
            vibrate.isChecked = true
        else
            vibrate.isChecked = false

        if (setArrow == 1)
            noarrow.isChecked = false
        else
            noarrow.isChecked = true

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
