/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Checkable
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.FontType
import org.catrobat.paintroid.tools.options.TextToolOptionsView

private const val DEFAULT_TEXTSIZE = "20"
private const val MAX_TEXTSIZE = "300"
private const val MIN_FONT_SIZE = 1
private const val MAX_FONT_SIZE = 300

class DefaultTextToolOptionsView(rootView: ViewGroup) : TextToolOptionsView {
    private val context: Context = rootView.context
    private var callback: TextToolOptionsView.Callback? = null
    private val textEditText: EditText
    private val fontSizeText: EditText
    private val fontList: RecyclerView
    private val underlinedToggleButton: MaterialButton
    private val italicToggleButton: MaterialButton
    private val boldToggleButton: MaterialButton
    private val fontTypes: List<FontType>
    private val topLayout: View
    private val bottomLayout: View

    init {
        val inflater = LayoutInflater.from(context)
        val textToolView = inflater.inflate(R.layout.dialog_pocketpaint_text_tool, rootView)
        topLayout = textToolView.findViewById(R.id.pocketpaint_text_top_layout)
        bottomLayout = textToolView.findViewById(R.id.pocketpaint_text_bottom_layout)
        textEditText = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_input_text)
        fontList = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_list_font)
        underlinedToggleButton =
            textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined)
        italicToggleButton =
            textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic)
        boldToggleButton = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold)
        fontSizeText = textToolView.findViewById(R.id.pocketpaint_font_size_text)
        fontSizeText.setText(DEFAULT_TEXTSIZE)
        underlinedToggleButton.paintFlags =
            underlinedToggleButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        @Suppress("SpreadOperator")
        fontTypes = FontType.values().toList()
        initializeListeners()
        textEditText.requestFocus()
    }

    private fun initializeListeners() {
        textEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(editable: Editable) {
                notifyTextChanged(editable.toString())
            }
        })
        textEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
            }
        }
        fontList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fontList.adapter = FontListAdapter(context, fontTypes) { font ->
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
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) = Unit

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) = Unit

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

    private fun notifyFontChanged(fontType: FontType) {
        callback?.setFont(fontType)
    }

    private fun notifyUnderlinedChanged(underlined: Boolean) {
        callback?.setUnderlined(underlined)
    }

    private fun notifyItalicChanged(italic: Boolean) {
        callback?.setItalic(italic)
    }

    private fun notifyBoldChanged(bold: Boolean) {
        callback?.setBold(bold)
    }

    private fun notifyTextSizeChanged(textSize: Int) {
        callback?.setTextSize(textSize)
    }

    private fun notifyTextChanged(text: String) {
        callback?.setText(text)
    }

    override fun setState(
        bold: Boolean,
        italic: Boolean,
        underlined: Boolean,
        text: String,
        textSize: Int,
        fontType: FontType
    ) {
        boldToggleButton.isChecked = bold
        italicToggleButton.isChecked = italic
        underlinedToggleButton.isChecked = underlined
        textEditText.setText(text)
        (fontList.adapter as FontListAdapter).setSelectedIndex(fontTypes.indexOf(fontType))
        fontSizeText.setText(DEFAULT_TEXTSIZE)
    }

    override fun setCallback(listener: TextToolOptionsView.Callback) {
        callback = listener
    }

    override fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(textEditText.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInputFromWindow(textEditText.windowToken, InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun getTopLayout(): View = topLayout

    override fun getBottomLayout(): View = bottomLayout
}
