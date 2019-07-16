package com.tuanchauict.acb.ui.settings

import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.StringRes
import com.tuanchauict.acb.R
import com.tuanchauict.acb.isVisible

class SettingSwitchItemController(
    rootView: View,
    onCheckedChangeAction: (Boolean) -> Unit
) {
    private val titleTextView: TextView = rootView.findViewById(R.id.item_title)
    private val subtitleTextView: TextView = rootView.findViewById(R.id.item_subtitle)
    private val switchView: Switch = rootView.findViewById(R.id.item_switch)

    init {
        switchView.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChangeAction(isChecked)
        }
    }

    fun update(@StringRes titleRes: Int, @StringRes subtitleRes: Int, isChecked: Boolean) {
        titleTextView.setText(titleRes)
        subtitleTextView.isVisible = subtitleRes != 0
        subtitleTextView.setText(subtitleRes)
        switchView.isChecked = isChecked
    }
}
