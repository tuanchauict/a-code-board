package com.tuanchauict.acb.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager

import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.tuanchauict.acb.R

/**
 * Created by Ruby on 05/12/2016.
 */
class IntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(CodeboardIntroFragment.newInstance(R.layout.codeboard_intro1))
        addSlide(CodeboardIntroFragment.newInstance(R.layout.codeboard_intro2))
        @Suppress("DEPRECATION")
        addSlide(
            AppIntroFragment.newInstance(
                "All the shortcuts!", "Click 'ctrl' for select all, cut, copy, paste, or undo." +
                        "\nCtrl+Shift+Z for redo" + "\n Long press Space to change keyboard \n More symbols",
                R.drawable.intro3, Color.parseColor("#3F51B5")
            )
        )

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // OPTIONAL METHODS
        // Override bar/separator color.
        //        setBarColor(Color.parseColor("#3F51B5"));
        //        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false)
        // setProgressButtonEnabled(false);

        //         Turn vibration on and set intensity.
        //         NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //        setVibrate(true);
        //        setVibrateIntensity(30);
        //        setFadeAnimation();
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Do something when users tap on Skip button.
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Do something when users tap on Done button.
        finish()
    }

    fun enableButtonIntro(v: View) {
        val intent = Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS)
        startActivity(intent)
    }

    fun changeButtonIntro(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showInputMethodPicker()
    }
}
