package com.tuanchauict.acb.ui

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.tuanchauict.acb.BooleanMap
import com.tuanchauict.acb.R
import com.tuanchauict.acb.isVisible

class MainActivity2 : AppCompatActivity() {

    private lateinit var step1: StepViewController
    private lateinit var step2: StepViewController
    private lateinit var step3: StepViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)

        step1 = StepViewController(
            1,
            findViewById(R.id.step_1),
            R.string.step_1_checked,
            R.string.step_1_unchecked
        ) {
            println("Con heo: 1")
        }
        step2 = StepViewController(
            2,
            findViewById(R.id.step_2),
            R.string.step_2_checked,
            R.string.step_2_unchecked
        ) {
            println("Con heo: 2")
        }
        step3 = StepViewController(
            3,
            findViewById(R.id.step_3),
            R.string.step_3_checked,
            R.string.step_3_unchecked
        ) {
            println("Con heo: 3")
        }

        step1.update(true)
        step2.update(false)
        step3.update(false)
    }
}

private class StepViewController(
    nth: Int, rootView: View,
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
            if (!isSelected) {
                onClickAction()
            }
        }
    }

    fun update(isChecked: Boolean) {
        checkedView.isVisible = isChecked
        numberView.isVisible = !isChecked
        titleView.isSelected = isChecked
        titleView.setText(texts[isChecked])
    }
}
