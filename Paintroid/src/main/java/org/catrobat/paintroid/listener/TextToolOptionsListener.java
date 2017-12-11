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
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import org.catrobat.paintroid.R;

import java.util.Arrays;

public final class TextToolOptionsListener {
	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "TextToolDialog has not been initialized. Call init() first!";

	private static TextToolOptionsListener instance;
	private OnTextToolOptionsChangedListener onTextToolOptionsChangedListener;
	private Context context;
	private EditText textEditText;

	private TextToolOptionsListener(Context context, View textToolOptionsView) {
		this.context = context;
		initializeListeners(textToolOptionsView);
	}

	public static TextToolOptionsListener getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	public static void init(Context context, View textToolOptionsView) {
		instance = new TextToolOptionsListener(context, textToolOptionsView);
	}

	private void initializeListeners(View textToolOptionsView) {
		textEditText = (EditText) textToolOptionsView.findViewById(R.id.text_tool_dialog_input_text);
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
		textEditText.requestFocus();

		textEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					hideKeyboard();
				}
			}
		});

		Spinner fontSpinner = (Spinner) textToolOptionsView.findViewById(R.id.text_tool_dialog_spinner_font);
		ArrayAdapter<String> fontSpinnerAdapter = new ArrayAdapter<>(context,
				android.R.layout.simple_list_item_activated_1,
				Arrays.asList(context.getResources().getStringArray(R.array.text_tool_font_array)));
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
			}
		});

		ToggleButton underlinedToggleButton = (ToggleButton) textToolOptionsView.findViewById(R.id.text_tool_dialog_toggle_underlined);
		underlinedToggleButton.setPaintFlags(underlinedToggleButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		underlinedToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean underlined = ((Checkable) v).isChecked();
				onTextToolOptionsChangedListener.setUnderlined(underlined);
				hideKeyboard();
			}
		});

		ToggleButton italicToggleButton = (ToggleButton) textToolOptionsView.findViewById(R.id.text_tool_dialog_toggle_italic);
		italicToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean italic = ((Checkable) v).isChecked();
				onTextToolOptionsChangedListener.setItalic(italic);
				hideKeyboard();
			}
		});

		ToggleButton boldToggleButton = (ToggleButton) textToolOptionsView.findViewById(R.id.text_tool_dialog_toggle_bold);
		boldToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean bold = ((Checkable) v).isChecked();
				onTextToolOptionsChangedListener.setBold(bold);
				hideKeyboard();
			}
		});

		Spinner textSizeSpinner = (Spinner) textToolOptionsView.findViewById(R.id.text_tool_dialog_spinner_text_size);
		ArrayAdapter<String> textSizeArrayAdapter = new ArrayAdapter<>(context,
				android.R.layout.simple_list_item_activated_1,
				Arrays.asList(context.getResources().getStringArray(R.array.text_tool_size_array)));
		textSizeSpinner.setAdapter(textSizeArrayAdapter);

		textSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				int textSize = Integer.valueOf((String) parent.getItemAtPosition(position));
				onTextToolOptionsChangedListener.setTextSize(textSize);
				hideKeyboard();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(textEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void setOnTextToolOptionsChangedListener(OnTextToolOptionsChangedListener listener) {
		onTextToolOptionsChangedListener = listener;
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
