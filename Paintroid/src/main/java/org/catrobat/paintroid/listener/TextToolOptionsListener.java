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
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.catrobat.paintroid.R;

import java.util.ArrayList;
import java.util.Arrays;

public final class TextToolOptionsListener {
	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "TextToolDialog has not been initialized. Call init() first!";

	private static TextToolOptionsListener instance;
	private OnTextToolOptionsChangedListener mOnTextToolOptionsChangedListener;
	private Context mContext;
	private EditText mTextEditText;
	private MaterialSpinner mFontSpinner;
	private ToggleButton mUnderlinedToggleButton;
	private ToggleButton mItalicToggleButton;
	private ToggleButton mBoldToggleButton;
	private MaterialSpinner mTextSizeSpinner;

	public interface OnTextToolOptionsChangedListener {
		void setText(String text);
		void setFont(String font);
		void setUnderlined(boolean underlined);
		void setItalic(boolean italic);
		void setBold(boolean bold);
		void setTextSize(int size);
	}

	public TextToolOptionsListener(Context context, View textToolOptionsView) {
		mContext = context;
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
		mTextEditText = (EditText) textToolOptionsView.findViewById(R.id.text_tool_dialog_input_text);
		mTextEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = mTextEditText.getText().toString();
				mOnTextToolOptionsChangedListener.setText(text);
			}
		});
		mTextEditText.requestFocus();

		mFontSpinner = (MaterialSpinner) textToolOptionsView.findViewById(R.id.text_tool_dialog_spinner_font);
		mFontSpinner.setItems(new ArrayList<String>(Arrays.asList(mContext.getResources().getStringArray(R.array.text_tool_font_array))));

		mFontSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(MaterialSpinner view, int position, long id, Object font) {
				mOnTextToolOptionsChangedListener.setFont(font.toString());
				hideKeyboard();
			}

		});

		mUnderlinedToggleButton = (ToggleButton) textToolOptionsView.findViewById(R.id.text_tool_dialog_toggle_underlined);
		mUnderlinedToggleButton.setTextOn(Html.fromHtml(
				"<u>" + mContext.getResources().getString(R.string.text_tool_dialog_underline_shortcut) + "</u>"));
		mUnderlinedToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean underlined = mUnderlinedToggleButton.isChecked();
				mOnTextToolOptionsChangedListener.setUnderlined(underlined);
				hideKeyboard();
			}
		});

		mItalicToggleButton = (ToggleButton) textToolOptionsView.findViewById(R.id.text_tool_dialog_toggle_italic);
		mItalicToggleButton.setTextOn(Html.fromHtml(
				"<i>" +  mContext.getResources().getString(R.string.text_tool_dialog_italic_shortcut) + "</i>"));
		mItalicToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean italic = mItalicToggleButton.isChecked();
				mOnTextToolOptionsChangedListener.setItalic(italic);
				hideKeyboard();
			}
		});

		mBoldToggleButton = (ToggleButton) textToolOptionsView.findViewById(R.id.text_tool_dialog_toggle_bold);
		mBoldToggleButton.setTextOn(Html.fromHtml(
				"<b>" +  mContext.getResources().getString(R.string.text_tool_dialog_bold_shortcut) + "</b>"));
		mBoldToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean bold = mBoldToggleButton.isChecked();
				mOnTextToolOptionsChangedListener.setBold(bold);
				hideKeyboard();
			}
		});

		mTextSizeSpinner = (MaterialSpinner) textToolOptionsView.findViewById(R.id.text_tool_dialog_spinner_text_size);
		mTextSizeSpinner.setItems(new ArrayList<String>(Arrays.asList(mContext.getResources().getStringArray(R.array.text_tool_size_array))));

		mTextSizeSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(MaterialSpinner view, int position, long id, Object size) {
				mOnTextToolOptionsChangedListener.setTextSize(Integer.valueOf(size.toString()));
				hideKeyboard();
			}
		});
	}

	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mTextEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void setOnTextToolOptionsChangedListener(OnTextToolOptionsChangedListener listener) {
		mOnTextToolOptionsChangedListener = listener;
	}

}
