/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.listener;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.tools.FontArrayAdapter;

import java.util.Arrays;
import java.util.List;

public final class TextToolOptionsListener {
	private OnTextToolOptionsChangedListener onTextToolOptionsChangedListener;
	private Context context;
	private final EditText textEditText;
	private final Spinner fontSpinner;
	private final ToggleButton underlinedToggleButton;
	private final ToggleButton italicToggleButton;
	private final ToggleButton boldToggleButton;
	private final List<String> fonts;

	public TextToolOptionsListener(Context context, View textToolOptionsView) {
		this.context = context;

		textEditText = textToolOptionsView.findViewById(R.id.pocketpaint_text_tool_dialog_input_text);
		fontSpinner = textToolOptionsView.findViewById(R.id.pocketpaint_text_tool_dialog_spinner_font);
		underlinedToggleButton = textToolOptionsView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined);
		italicToggleButton = textToolOptionsView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic);
		boldToggleButton = textToolOptionsView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold);

		fonts = Arrays.asList(context.getResources().getStringArray(R.array.pocketpaint_main_text_tool_fonts));
		initializeListeners();

		textEditText.requestFocus();
	}

	private void initializeListeners() {
		textEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = textEditText.getText().toString();
				onTextToolOptionsChangedListener.setText(text);
			}
		});

		textEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					hideKeyboard();
				}
			}
		});

		FontArrayAdapter fontSpinnerAdapter = new FontArrayAdapter(context,
				android.R.layout.simple_list_item_activated_1, fonts);
		fontSpinner.setAdapter(fontSpinnerAdapter);
		fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String fontString = (String) parent.getItemAtPosition(position);
				onTextToolOptionsChangedListener.setFont(fontString);
				hideKeyboard();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				hideKeyboard();
			}
		});

		underlinedToggleButton.setPaintFlags(underlinedToggleButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		underlinedToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean underlined = ((Checkable) v).isChecked();
				onTextToolOptionsChangedListener.setUnderlined(underlined);
				hideKeyboard();
			}
		});

		italicToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean italic = ((Checkable) v).isChecked();
				onTextToolOptionsChangedListener.setItalic(italic);
				hideKeyboard();
			}
		});

		boldToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean bold = ((Checkable) v).isChecked();
				onTextToolOptionsChangedListener.setBold(bold);
				hideKeyboard();
			}
		});
	}

	public void setState(boolean bold, boolean italic, boolean underlined, String text, int textSize, String font) {
		boldToggleButton.setChecked(bold);
		italicToggleButton.setChecked(italic);
		underlinedToggleButton.setChecked(underlined);
		textEditText.setText(text);
		fontSpinner.setSelection(fonts.indexOf(font));
	}

	public void setOnTextToolOptionsChangedListener(OnTextToolOptionsChangedListener listener) {
		onTextToolOptionsChangedListener = listener;
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(textEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public interface OnTextToolOptionsChangedListener {
		void setText(String text);

		void setFont(String font);

		void setUnderlined(boolean underlined);

		void setItalic(boolean italic);

		void setBold(boolean bold);

		void setTextSize(int size);
	}
}
