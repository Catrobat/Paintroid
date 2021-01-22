/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.tools

import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.google.android.material.textfield.TextInputEditText
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.options.SprayToolOptionsView

class DefaultSprayToolOptionsView(rootView: ViewGroup) : SprayToolOptionsView {

    private var callback: SprayToolOptionsView.Callback? = null
    private val radiusText: TextInputEditText
    private val radiusSeekBar: SeekBar

    companion object {
        private const val MIN_RADIUS = 1
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val sprayToolOptionsView: View = inflater.inflate(R.layout.dialog_pocketpaint_spray_tool, rootView)
        radiusText = sprayToolOptionsView.findViewById(R.id.pocketpaint_radius_text)
        radiusSeekBar = sprayToolOptionsView.findViewById(R.id.pocketpaint_spray_radius_seek_bar)
        initializeListeners()
    }

    private fun initializeListeners() {
        radiusSeekBar.progress = MIN_RADIUS
        radiusText.setText(radiusSeekBar.progress.toString())
        radiusText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().toInt() > 100) {
                    radiusText.setText(s.toString().substring(0,2))
                    return
                }

                if(radiusSeekBar.progress.toString() != s.toString()) {
                    radiusSeekBar.progress = s.toString().toInt()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        radiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                if (progress < MIN_RADIUS) {
                    radiusText.setText(MIN_RADIUS.toString())
                } else {
                    radiusText.setText(progress.toString())
                }

                callback?.radiusChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun setCallback(callback: SprayToolOptionsView.Callback?) {
        this.callback = callback
    }

    override fun setRadius(radius: Int) {
        radiusSeekBar.progress = radius
    }

    override fun setCurrentPaint(paint: Paint) {
        setRadius(paint.strokeWidth.toInt())
    }
}