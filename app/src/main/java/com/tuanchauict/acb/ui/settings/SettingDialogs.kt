package com.tuanchauict.acb.ui.settings

import android.app.Activity
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.tuanchauict.acb.R

class SeekbarDialog(
    activity: Activity,
    @StringRes titleRes: Int,
    initValue: Int,
    private val progressInfo: ProgressInfo,
    private val onSelect: (Int) -> Unit
) {

    init {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_setting_slider, null)
        val seekBar = view.findViewById<SeekBar>(R.id.progress_seekbar).apply {
            max = progressInfo.max - progressInfo.min
        }
        val progressText = view.findViewById<TextView>(R.id.progress_value)
        progressText.text = progressInfo.toText(initValue)
        seekBar.progress = initValue

        seekBar.setOnSeekBarChangeListener(OnSeekBarChangeListener {
            progressText.text = progressInfo.toText(it + progressInfo.min)
        })

        AlertDialog.Builder(activity)
            .setTitle(titleRes)
            .setView(view)
            .setPositiveButton(R.string.common_ok) { _, _ ->
                onSelect(seekBar.progress + progressInfo.min)
            }
            .setNegativeButton(R.string.common_cancel, null)
            .setNeutralButton(R.string.common_default) { _, _ ->
                onSelect(progressInfo.default)
            }
            .show()
    }

    class ProgressInfo(val min: Int, val max: Int, val default: Int, val toText: (Int) -> String)

    private class OnSeekBarChangeListener(private val onChanged: (Int) -> Unit) :
        SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) =
            onChanged(progress)

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
    }
}
