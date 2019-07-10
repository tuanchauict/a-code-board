package com.tuanchauict.acb.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CodeboardIntroFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutIdRes = arguments?.getInt(ARG_LAYOUT_RES_ID) ?: return null
        return inflater.inflate(layoutIdRes, container, false)
    }

    companion object {
        private const val ARG_LAYOUT_RES_ID = "layoutResId"

        fun newInstance(layoutResId: Int): CodeboardIntroFragment {
            val args = Bundle().apply {
                putInt(ARG_LAYOUT_RES_ID, layoutResId)
            }

            return CodeboardIntroFragment().apply {
                arguments = args
            }
        }
    }
}
