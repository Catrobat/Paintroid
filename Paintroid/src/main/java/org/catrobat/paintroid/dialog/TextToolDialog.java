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

package org.catrobat.paintroid.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.catrobat.paintroid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@SuppressLint("ValidFragment")
public final class TextToolDialog extends DialogFragment implements
		OnClickListener, DialogInterface.OnClickListener {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "TextToolDialog has not been initialized. Call init() first!";

	private static TextToolDialog instance;
	private OnTextToolDialogChangedListener mOnTextToolDialogChangedListener;
	private Context mContext;
	private EditText mTextEditText;
	private MaterialSpinner mFontSpinner;
	private boolean mFontSpinnerInitialized = false;
	private ToggleButton mUnderlinedToggleButton;
	private ToggleButton mItalicToggleButton;
	private ToggleButton mBoldToggleButton;
	private MaterialSpinner mTextSizeSpinner;
	private boolean mTextSizeSpinnerInitialized = false;
	private String mText;
	private int mFontIndex;
	private boolean mUnderlined;
	private boolean mItalic;
	private boolean mBold;
	private int mTextSizeIndex;

	public interface OnTextToolDialogChangedListener {
		void setText(String text);
		void setFont(String font);
		void setUnderlined(boolean underlined);
		void setItalic(boolean italic);
		void setBold(boolean bold);
		void setTextSize(int size);
	}

	@SuppressLint("ValidFragment")
	private TextToolDialog(Context context) {
		mContext = context;
		setDefaultDialogValues();
	}

	public static TextToolDialog getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}
	
	public static void init(Context context) {
		instance = new TextToolDialog(context);
	}

	public void setDefaultDialogValues() {
		mText = "";
		mFontIndex = 0;
		mUnderlined = false;
		mItalic = false;
		mBold = false;
		mTextSizeIndex = 0;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(mContext);
		builder.setTitle(R.string.text_tool_dialog_title);
		final View view = inflater.inflate(R.layout.dialog_text_tool, null);

		mTextEditText = (EditText) view.findViewById(R.id.text_tool_dialog_input_text);
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
				mOnTextToolDialogChangedListener.setText(text);
				mText = text;
			}
		});

		mFontSpinner = (MaterialSpinner) view.findViewById(R.id.text_tool_dialog_spinner_font);
		//ArrayAdapter<CharSequence> fontAdapter = ArrayAdapter.createFromResource(
		//		mContext, R.array.text_tool_font_array, android.R.layout.simple_spinner_item);
		//fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//mFontSpinner.setAdapter(fontAdapter);
		mFontSpinner.setItems(new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.text_tool_font_array))));

		//mFontSpinner.setBackgroundColor(Color.GRAY);
		mFontSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(MaterialSpinner view, int position, long id, Object font) {
				mOnTextToolDialogChangedListener.setFont(font.toString());
				mFontIndex = position;
				if (mFontSpinnerInitialized)
					hideKeyboard();
				else
					mFontSpinnerInitialized = true;
			}

		});


		mUnderlinedToggleButton = (ToggleButton) view.findViewById(R.id.text_tool_dialog_toggle_underlined);
		mUnderlinedToggleButton.setTextOn(Html.fromHtml(
				"<u>" + getResources().getString(R.string.text_tool_dialog_underline_shortcut) + "</u>"));
		mUnderlinedToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean underlined = mUnderlinedToggleButton.isChecked();
				mOnTextToolDialogChangedListener.setUnderlined(underlined);
				mUnderlined = underlined;
				hideKeyboard();
			}
		});

		mItalicToggleButton = (ToggleButton) view.findViewById(R.id.text_tool_dialog_toggle_italic);
		mItalicToggleButton.setTextOn(Html.fromHtml(
				"<i>" +  getResources().getString(R.string.text_tool_dialog_italic_shortcut) + "</i>"));
		mItalicToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean italic = mItalicToggleButton.isChecked();
				mOnTextToolDialogChangedListener.setItalic(italic);
				mItalic = italic;
				hideKeyboard();
			}
		});

		mBoldToggleButton = (ToggleButton) view.findViewById(R.id.text_tool_dialog_toggle_bold);
		mBoldToggleButton.setTextOn(Html.fromHtml(
				"<b>" +  getResources().getString(R.string.text_tool_dialog_bold_shortcut) + "</b>"));
		mBoldToggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean bold = mBoldToggleButton.isChecked();
				mOnTextToolDialogChangedListener.setBold(bold);
				mBold = bold;
				hideKeyboard();
			}
		});

		mTextSizeSpinner = (MaterialSpinner) view.findViewById(R.id.text_tool_dialog_spinner_text_size);
		//ArrayAdapter<CharSequence> textSizeAdapter = ArrayAdapter.createFromResource(
		//		mContext, R.array.text_tool_size_array, R.layout.text_tool_spinner_item);
		//textSizeAdapter.setDropDownViewResource(R.layout.text_tool_spinner_item); //android.R.layout.simple_spinner_dropdown_item);
		//mTextSizeSpinner.setAdapter(textSizeAdapter);
		mTextSizeSpinner.setItems(new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.text_tool_size_array))));

		//mTextSizeSpinner.setBackgroundColor(Color.GRAY);
		mTextSizeSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(MaterialSpinner view, int position, long id, Object size) {
				mOnTextToolDialogChangedListener.setTextSize(Integer.valueOf(size.toString()));
				mTextSizeIndex = position;
				if (mTextSizeSpinnerInitialized)
					hideKeyboard();
				else
					mTextSizeSpinnerInitialized = true;
		}

		});

		builder.setView(view);
		builder.setNeutralButton(R.string.done, this);

		Dialog textDialog = builder.create();
		WindowManager.LayoutParams window_params = textDialog.getWindow().getAttributes();
		textDialog.getWindow().setDimAmount(0.0f);
		textDialog.getWindow().setAttributes(window_params);

		return textDialog;
	}

	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mTextEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void setOnTextToolDialogChangedListener(OnTextToolDialogChangedListener listener) {
		mOnTextToolDialogChangedListener = listener;
	}

	@Override
	public void onStart() {
		super.onStart();

		mTextSizeSpinnerInitialized = false;
		mFontSpinnerInitialized = false;

		mTextEditText.setText(mText);
		mFontSpinner.setSelectedIndex(mFontIndex);
		mUnderlinedToggleButton.setChecked(mUnderlined);
		mItalicToggleButton.setChecked(mItalic);
		mBoldToggleButton.setChecked(mBold);
		mTextSizeSpinner.setSelectedIndex(mTextSizeIndex);

		getDialog().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
	}
}
