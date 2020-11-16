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

package org.catrobat.paintroid.ui.tools;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.options.TextToolOptionsView;

import java.util.Arrays;
import java.util.List;

public class DefaultTextToolOptionsView implements TextToolOptionsView {
	private final Context context;
	private Callback callback;
	private final EditText textEditText;
	private final EditText fontSizeText;
	private final Spinner fontSpinner;
	private final ToggleButton underlinedToggleButton;
	private final ToggleButton italicToggleButton;
	private final ToggleButton boldToggleButton;
	private final List<String> fonts;
	private static final String DEFAULT_TEXTSIZE = "20";
	private static final String MAX_TEXTSIZE = "300";
	private static final int MIN_FONT_SIZE = 1;
	private static final int MAX_FONT_SIZE = 300;

	public DefaultTextToolOptionsView(ViewGroup rootView) {
		context = rootView.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		View textToolView = inflater.inflate(R.layout.dialog_pocketpaint_text_tool, rootView);

		textEditText = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_input_text);
		fontSpinner = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_spinner_font);
		underlinedToggleButton = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined);
		italicToggleButton = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic);
		boldToggleButton = textToolView.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold);
		fontSizeText = textToolView.findViewById(R.id.pocketpaint_font_size_text);
		fontSizeText.setText(DEFAULT_TEXTSIZE);

		underlinedToggleButton.setPaintFlags(underlinedToggleButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
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
			public void afterTextChanged(Editable editable) {
				notifyTextChanged(editable.toString());
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
				notifyFontChanged(fontString);
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
				notifyUnderlinedChanged(underlined);
				hideKeyboard();
			}
		});

		italicToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean italic = ((Checkable) v).isChecked();
				notifyItalicChanged(italic);
				hideKeyboard();
			}
		});

		boldToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean bold = ((Checkable) v).isChecked();
				notifyBoldChanged(bold);
				hideKeyboard();
			}
		});

		fontSizeText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				String sizeText = fontSizeText.getText().toString();
				int sizeTextInt;
				try {
					sizeTextInt = Integer.parseInt(sizeText);
				} catch (NumberFormatException exp) {
					sizeTextInt = MIN_FONT_SIZE;
				}
				if (sizeTextInt > MAX_FONT_SIZE) {
					sizeTextInt = MAX_FONT_SIZE;
					fontSizeText.setText(MAX_TEXTSIZE);
					fontSizeText.setSelection(MAX_TEXTSIZE.length());
				}
				notifyTextSizeChanged(sizeTextInt);
			}
		});
	}

	private void notifyFontChanged(String fontString) {
		if (callback != null) {
			callback.setFont(fontString);
		}
	}

	private void notifyUnderlinedChanged(boolean underlined) {
		if (callback != null) {
			callback.setUnderlined(underlined);
		}
	}

	private void notifyItalicChanged(boolean italic) {
		if (callback != null) {
			callback.setItalic(italic);
		}
	}

	private void notifyBoldChanged(boolean bold) {
		if (callback != null) {
			callback.setBold(bold);
		}
	}

	private void notifyTextSizeChanged(int textSize) {
		if (callback != null) {
			callback.setTextSize(textSize);
		}
	}

	private void notifyTextChanged(String text) {
		if (callback != null) {
			callback.setText(text);
		}
	}

	@Override
	public void setState(boolean bold, boolean italic, boolean underlined, String text, int textSize, String font) {
		boldToggleButton.setChecked(bold);
		italicToggleButton.setChecked(italic);
		underlinedToggleButton.setChecked(underlined);
		textEditText.setText(text);
		fontSpinner.setSelection(fonts.indexOf(font));
		fontSizeText.setText(DEFAULT_TEXTSIZE);
	}

	@Override
	public void setCallback(Callback listener) {
		callback = listener;
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(textEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
