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

import android.app.Fragment;
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
import org.catrobat.paintroid.ui.tools.FontArrayAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class TextToolOptionsListener extends Fragment {
	private OnTextToolOptionsChangedListener onTextToolOptionsChangedListener;
	private Context context;
	private final EditText textEditText;
	private final Spinner fontSpinner;
	private final ToggleButton underlinedToggleButton;
	private final ToggleButton italicToggleButton;
	private final ToggleButton boldToggleButton;
	private final Spinner textSizeSpinner;
	private final List<String> sizes;
	private final List<String> fonts;
	private final NumberFormat localeNumberFormat;

	public TextToolOptionsListener(Context context, View textToolOptionsView) {
		this.context = context;

		textEditText = (EditText) textToolOptionsView.findViewById(R.id.text_tool_dialog_input_text);
		fontSpinner = (Spinner) textToolOptionsView.findViewById(R.id.text_tool_dialog_spinner_font);
		underlinedToggleButton = (ToggleButton) textToolOptionsView.findViewById(R.id.text_tool_dialog_toggle_underlined);
		italicToggleButton = (ToggleButton) textToolOptionsView.findViewById(R.id.text_tool_dialog_toggle_italic);
		boldToggleButton = (ToggleButton) textToolOptionsView.findViewById(R.id.text_tool_dialog_toggle_bold);
		textSizeSpinner = (Spinner) textToolOptionsView.findViewById(R.id.text_tool_dialog_spinner_text_size);

		fonts = Arrays.asList(context.getResources().getStringArray(R.array.text_tool_font_array));
		sizes = new ArrayList<>();
		localeNumberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

		initializeListeners();
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

		textEditText.requestFocus();
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

		int[] intSizes = context.getResources().getIntArray(R.array.text_tool_size_array);
		for (int value : intSizes) {
			sizes.add(localeNumberFormat.format(value));
		}
		ArrayAdapter<String> textSizeArrayAdapter = new ArrayAdapter<>(context,
				android.R.layout.simple_list_item_activated_1, sizes);
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

	public void setState(boolean bold, boolean italic, boolean underlined, String text, int textSize, String font) {
		boldToggleButton.setChecked(bold);
		italicToggleButton.setChecked(italic);
		underlinedToggleButton.setChecked(underlined);
		textEditText.setText(text);
		textSizeSpinner.setSelection(sizes.indexOf(localeNumberFormat.format(textSize)));
		fontSpinner.setSelection(fonts.indexOf(font));
	}

	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(textEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
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
