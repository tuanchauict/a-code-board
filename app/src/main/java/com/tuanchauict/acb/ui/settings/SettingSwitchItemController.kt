package com.tuanchauict.acb.ui.settings

import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.StringRes
import com.tuanchauict.acb.R
import com.tuanchauict.acb.isVisible

class SettingSwitchItemController(
    rootView: View,
    onItemClickListener: SettingSwitchItemController.() -> Unit
) {
    private val titleTextView: TextView = rootView.findViewById(R.id.item_title)
    private val subtitleTextView: TextView = rootView.findViewById(R.id.item_subtitle)
    private val switchView: Switch = rootView.findViewById(R.id.item_switch)

    init {
        rootView.setOnClickListener { onItemClickListener() }
    }

    fun update(@StringRes titleRes: Int, @StringRes subtitleRes: Int, isChecked: Boolean) {
        titleTextView.setText(titleRes)
        if (subtitleRes != 0) {
            subtitleTextView.setText(subtitleRes)
        }
        subtitleTextView.isVisible = subtitleRes != 0
        switchView.isChecked = isChecked
    }
}
