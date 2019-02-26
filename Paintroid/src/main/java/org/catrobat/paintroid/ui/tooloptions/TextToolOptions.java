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

package org.catrobat.paintroid.ui.tooloptions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.options.TextToolOptionsContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TextToolOptions implements TextToolOptionsContract {
	private Callback callback;
	private Activity activity;
	private final EditText textEditText;
	private final Spinner fontSpinner;
	private final ToggleButton underlinedToggleButton;
	private final ToggleButton italicToggleButton;
	private final ToggleButton boldToggleButton;
	private final Spinner textSizeSpinner;
	private final List<String> fonts;

	public TextToolOptions(Activity activity, ViewGroup rootView) {
		this.activity = activity;

		LayoutInflater inflater = LayoutInflater.from(activity);
		inflater.inflate(R.layout.dialog_pocketpaint_text_tool, rootView);

		textEditText = activity.findViewById(R.id.pocketpaint_text_tool_dialog_input_text);
		fontSpinner = activity.findViewById(R.id.pocketpaint_text_tool_dialog_spinner_font);
		underlinedToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined);
		italicToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic);
		boldToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold);
		textSizeSpinner = activity.findViewById(R.id.pocketpaint_text_tool_dialog_spinner_text_size);

		underlinedToggleButton.setPaintFlags(underlinedToggleButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		fonts = Arrays.asList(activity.getResources().getStringArray(R.array.pocketpaint_main_text_tool_fonts));

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
			public void afterTextChanged(Editable editable) {
				callback.setText(editable.toString());
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

		FontArrayAdapter fontSpinnerAdapter = new FontArrayAdapter(activity,
				android.R.layout.simple_list_item_activated_1, fonts);
		fontSpinner.setAdapter(fontSpinnerAdapter);
		fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String fontString = (String) parent.getItemAtPosition(position);
				callback.setFont(fontString);
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
				callback.setUnderlined(underlined);
				hideKeyboard();
			}
		});

		italicToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean italic = ((Checkable) v).isChecked();
				callback.setItalic(italic);
				hideKeyboard();
			}
		});

		boldToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean bold = ((Checkable) v).isChecked();
				callback.setBold(bold);
				hideKeyboard();
			}
		});

		final int[] intSizes = activity.getResources().getIntArray(R.array.pocketpaint_text_tool_size_array);
		ArrayList<String> stringSizes = new ArrayList<String>();
		String pixelString = activity.getString(R.string.pixel);
		for (int size : intSizes) {
			stringSizes.add(String.format(Locale.getDefault(), "%d", size) + pixelString);
		}

		ArrayAdapter<String> textSizeArrayAdapter = new ArrayAdapter<>(activity,
				android.R.layout.simple_list_item_activated_1, stringSizes);
		textSizeSpinner.setAdapter(textSizeArrayAdapter);

		textSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				int textSize = intSizes[position];
				callback.setTextSize(textSize);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	@Override
	public void setState(boolean bold, boolean italic, boolean underlined, String text, int textSize, String font) {
		boldToggleButton.setChecked(bold);
		italicToggleButton.setChecked(italic);
		underlinedToggleButton.setChecked(underlined);
		textEditText.setText(text);
		fontSpinner.setSelection(fonts.indexOf(font));
	}

	@Override
	public void setCallback(Callback listener) {
		callback = listener;
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(textEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
