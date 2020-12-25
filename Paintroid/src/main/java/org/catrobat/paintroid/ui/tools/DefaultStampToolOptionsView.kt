package org.catrobat.paintroid.ui.tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.options.StampToolOptionsView

class DefaultStampToolOptionsView(rootView: ViewGroup) : StampToolOptionsView {
    private val pasteChip: Chip
    private val copyChip: Chip
    private val cutChip: Chip
    private var callback: StampToolOptionsView.Callback? = null

    private fun initializeListeners() {
        copyChip.setOnClickListener {
            callback?.copyClicked()
        }

        cutChip.setOnClickListener {
            callback?.cutClicked()
        }

        pasteChip.setOnClickListener {
            callback?.pasteClicked()
        }
    }

    override fun setCallback(callback: StampToolOptionsView.Callback?) {
        this.callback = callback
    }

    override fun enablePaste(enable: Boolean) {
        pasteChip.isEnabled = enable
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val stampToolOptionsView: View = inflater.inflate(R.layout.dialog_pocketpaint_stamp_tool, rootView)
        copyChip = stampToolOptionsView.findViewById(R.id.action_copy)
        pasteChip = stampToolOptionsView.findViewById(R.id.action_paste)
        cutChip = stampToolOptionsView.findViewById(R.id.action_cut)
        enablePaste(false)
        initializeListeners()
    }
}