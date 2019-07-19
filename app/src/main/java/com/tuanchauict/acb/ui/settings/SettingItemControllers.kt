package com.tuanchauict.acb.ui.settings

import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.StringRes
import com.tuanchauict.acb.R
import com.tuanchauict.acb.isVisible

class SettingSwitchItemController(
    rootView: View,
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int = 0,
    isChecked: Boolean = false,
    onItemClickAction: SettingSwitchItemController.() -> Unit
) {
    private val titleTextView: TextView = rootView.findViewById(R.id.item_title)
    private val subtitleTextView: TextView = rootView.findViewById(R.id.item_subtitle)
    private val switchView: Switch = rootView.findViewById(R.id.item_switch)

    init {
        rootView.setOnClickListener { onItemClickAction() }
        titleTextView.setText(titleRes)
        if (subtitleRes != 0) {
            subtitleTextView.setText(subtitleRes)
        }
        subtitleTextView.isVisible = subtitleRes != 0
        switchView.isChecked = isChecked
        switchView.isClickable = false
    }

    fun setChecked(isChecked: Boolean) {
        switchView.isChecked = isChecked
    }
}

class SettingSimpleClickItem(
    private val rootView: View,
    @StringRes titleRes: Int,
    subtitle: String = "",
    onItemClickAction: SettingSimpleClickItem.() -> Unit
) {
    private val titleTextView: TextView = rootView.findViewById(R.id.item_title)
    private val subtitleTextView: TextView = rootView.findViewById(R.id.item_subtitle)

    init {
        rootView.setOnClickListener { onItemClickAction() }
        titleTextView.setText(titleRes)
        setSubtitle(subtitle)
    }

    fun setEnabled(isEnabled: Boolean) {
        rootView.isEnabled = isEnabled
    }

    fun setSubtitle(subtitle: String) {
        subtitleTextView.text = subtitle
        subtitleTextView.isVisible = subtitle.isNotEmpty()
    }
}
