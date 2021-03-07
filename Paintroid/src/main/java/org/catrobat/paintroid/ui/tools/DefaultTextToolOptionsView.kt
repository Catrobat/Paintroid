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

import android.content.Context
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Checkable
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.options.TextToolOptionsView
import java.util.*

class DefaultTextToolOptionsView(rootView: ViewGroup) : TextToolOptionsView {
    private val context: Context = rootView.context
    private var callback: TextToolOptionsView.Callback? = null
    private val textEditText: EditText
    private val fontSizeText: EditText
    private val fontList: RecyclerView
    private val underlinedToggleButton: MaterialButton
    private val italicToggleButton: MaterialButton
    private val boldToggleButton: MaterialButton
    private val fonts: List<String>

    private fun initializeListeners() {
        textEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                notifyTextChanged(editable.toString())
            }
        })
        textEditText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
            }
        }
        fontList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fontList.adapter = FontListAdapter(context, fonts) { font ->
            notifyFontChanged(font)
            hideKeyboard()
        }

        underlinedToggleButton.setOnClickListener { v ->
            val underlined = (v as Checkable).isChecked
            notifyUnderlinedChanged(underlined)
            hideKeyboard()
        }
        italicToggleButton.setOnClickListener { v ->
            val italic = (v as Checkable).isChecked
            notifyItalicChanged(italic)
            hideKeyboard()
        }
        boldToggleButton.setOnClickListener { v ->
            val bold = (v as Checkable).isChecked
            notifyBoldChanged(bold)
            hideKeyboard()
        }
        fontSizeText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val sizeText = fontSizeText.text.toString()
                var sizeTextInt: Int
                sizeTextInt = try {
                    sizeText.toInt()
                } catch (exp: NumberFormatException) {
                    MIN_FONT_SIZE
                }
                if (sizeTextInt > MAX_FONT_SIZE) {
                    sizeTextInt = MAX_FONT_SIZE
                    fontSizeText.setText(MAX_TEXTSIZE)
                    fontSizeText.setSelection(MAX_TEXTSIZE.length)
                }
                notifyTextSizeChanged(sizeTextInt)
            }
        })
    }

    private fun notifyFontChanged(fontString: String) {
        if (callback != null) {
            callback!!.setFont(fontString)
        }
    }

    private fun notifyUnderlinedChanged(underlined: Boolean) {
        if (callback != null) {
            callback!!.setUnderlined(underlined)
        }
    }

    private fun notifyItalicChanged(italic: Boolean) {
        if (callback != null) {
            callback!!.setItalic(italic)
        }
    }

    private fun notifyBoldChanged(bold: Boolean) {
        if (callback != null) {
            callback!!.setBold(bold)
        }
    }

    private fun notifyTextSizeChanged(textSize: Int) {
        if (callback != null) {
            callback!!.setTextSize(textSize)
        }
    }

    private fun notifyTextChanged(text: String) {
        if (callback != null) {
            callback!!.setText(text)
        }
    }

    override fun setState(bold: Boolean, italic: Boolean, underlined: Boolean, text: String, textSize: Int, font: String) {
        boldToggleButton.isChecked = bold
        italicToggleButton.isChecked = italic
        underlinedToggleButton.isChecked = underlined
        textEditText.setText(text)
        (fontList.adapter as FontListAdapter).setSelectedIndex(fonts.indexOf(font));
        fontSizeText.setText(DEFAULT_TEXTSIZE)
    }

    override fun setCallback(listener: TextToolOptionsView.Callback) {
        callback = listener
    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(textEditText.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    companion object {
        private const val DEFAULT_TEXTSIZE = "20"
        private const val MAX_TEXTSIZE = "300"
        private const val MIN_FONT_SIZE = 1
        private const val MAX_FONT_SIZE = 300
    }

    init {
        val inflater = LayoutInflater.from(context)
        val textToolView = inflater.inflate(R.layout.dialog_pocketpaint_text_tool, rootView)
        textEditText = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_input_text)
        fontList = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_list_font)
        underlinedToggleButton = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined)
        italicToggleButton = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic)
        boldToggleButton = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold)
        fontSizeText = textToolView.findViewById(R.id.pocketpaint_font_size_text)
        fontSizeText.setText(DEFAULT_TEXTSIZE)
        underlinedToggleButton.paintFlags = underlinedToggleButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        fonts = listOf(*context.resources.getStringArray(R.array.pocketpaint_main_text_tool_fonts))
        initializeListeners()
        textEditText.requestFocus()
    }
}