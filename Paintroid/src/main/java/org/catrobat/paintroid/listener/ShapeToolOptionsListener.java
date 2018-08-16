/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.listener;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;
import org.catrobat.paintroid.ui.tools.NumberRangeFilter;

import java.util.Locale;

public class ShapeToolOptionsListener {
	private static final int MIN_STROKE_WIDTH = 1;

	private OnShapeToolOptionsChangedListener onShapeToolOptionsChangedListener;
	private ImageButton squareButton;
	private ImageButton circleButton;
	private ImageButton heartButton;
	private ImageButton starButton;
	private ImageButton fillButton;
	private ImageButton outlineButton;
	private View outlineView;
	private TextView outlineTextView;
	private SeekBar outlineWidthSeekBar;
	private EditText outlineWidthEditText;
	private TextView shapeToolDialogTitle;
	private TextView shapeToolFillOutline;

	public ShapeToolOptionsListener(View shapeToolOptionsView) {
		squareButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.pocketpaint_shapes_square_btn);
		circleButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.pocketpaint_shapes_circle_btn);
		heartButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.pocketpaint_shapes_heart_btn);
		starButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.pocketpaint_shapes_star_btn);
		fillButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.pocketpaint_shape_ibtn_fill);
		outlineButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.pocketpaint_shape_ibtn_outline);
		shapeToolDialogTitle = (TextView) shapeToolOptionsView.findViewById(R.id.pocketpaint_shape_tool_dialog_title);
		shapeToolFillOutline = (TextView) shapeToolOptionsView.findViewById(R.id.pocketpaint_shape_tool_fill_outline);

		outlineView = shapeToolOptionsView.findViewById(R.id.pocketpaint_outline_view_border);
		outlineTextView = (TextView) shapeToolOptionsView.findViewById(R.id.pocketpaint_outline_view_text_view);

		outlineWidthSeekBar = (SeekBar) shapeToolOptionsView.findViewById(R.id.pocketpaint_shape_stroke_width_seek_bar);
		outlineWidthEditText = (EditText) shapeToolOptionsView.findViewById(R.id.pocketpaint_shape_outline_edit);
		outlineWidthEditText.setFilters(new InputFilter[]{new NumberRangeFilter(1, 100)});

		int startingOutlineWidth = 25;
		outlineWidthEditText.setText(String.format(Locale.getDefault(), "%d", (int) startingOutlineWidth));
		outlineWidthSeekBar.setProgress(startingOutlineWidth);
		initializeListeners();
		setShapeActivated(GeometricFillTool.BaseShape.RECTANGLE);
		setDrawTypeActivated(GeometricFillTool.ShapeDrawType.FILL);
	}

	private void initializeListeners() {
		squareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShapeClicked(GeometricFillTool.BaseShape.RECTANGLE);
			}
		});
		circleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShapeClicked(GeometricFillTool.BaseShape.OVAL);
			}
		});
		heartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShapeClicked(GeometricFillTool.BaseShape.HEART);
			}
		});
		starButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShapeClicked(GeometricFillTool.BaseShape.STAR);
			}
		});
		fillButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDrawTypeClicked(GeometricFillTool.ShapeDrawType.FILL);
			}
		});
		outlineButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDrawTypeClicked(GeometricFillTool.ShapeDrawType.OUTLINE);
			}
		});
		outlineWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress < MIN_STROKE_WIDTH) {
					progress = MIN_STROKE_WIDTH;
				}
				if (fromUser) {
					seekBar.setProgress(progress);
				}
				outlineWidthEditText.setText(String.valueOf(progress));
				onOutlineWidthChanged(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});
		outlineWidthEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				String sizeText = outlineWidthEditText.getText().toString();
				int sizeTextInt;
				try {
					sizeTextInt = Integer.parseInt(sizeText);
				} catch (NumberFormatException exp) {
					sizeTextInt = MIN_STROKE_WIDTH;
				}
				outlineWidthSeekBar.setProgress(sizeTextInt);
			}
		});
	}

	private void onShapeClicked(GeometricFillTool.BaseShape shape) {
		onShapeToolOptionsChangedListener.setToolType(shape);
		setShapeActivated(shape);
	}

	private void onDrawTypeClicked(GeometricFillTool.ShapeDrawType drawType) {
		onShapeToolOptionsChangedListener.setDrawType(drawType);
		setDrawTypeActivated(drawType);
	}

	private void onOutlineWidthChanged(int outlineWidth) {
		onShapeToolOptionsChangedListener.setOutlineWidth(outlineWidth);
	}

	private void resetShapeActivated() {
		View[] buttons = {squareButton, circleButton, heartButton, starButton};
		for (View button : buttons) {
			button.setSelected(false);
		}
	}

	private void resetDrawTypeActivated() {
		View[] buttons = {fillButton, outlineButton};
		for (View button : buttons) {
			button.setSelected(false);
		}
	}

	public void setShapeActivated(GeometricFillTool.BaseShape shape) {
		resetShapeActivated();
		switch (shape) {
			case RECTANGLE:
				squareButton.setSelected(true);
				shapeToolDialogTitle.setText(R.string.shape_tool_dialog_rect_title);
				break;
			case OVAL:
				circleButton.setSelected(true);
				shapeToolDialogTitle.setText(R.string.shape_tool_dialog_ellipse_title);
				break;
			case HEART:
				heartButton.setSelected(true);
				shapeToolDialogTitle.setText(R.string.shape_tool_dialog_heart_title);
				break;
			case STAR:
				starButton.setSelected(true);
				shapeToolDialogTitle.setText(R.string.shape_tool_dialog_star_title);
				break;
			default:
				squareButton.setSelected(true);
				break;
		}
	}

	public void setDrawTypeActivated(GeometricFillTool.ShapeDrawType drawType) {
		resetDrawTypeActivated();
		switch (drawType) {
			case FILL:
				fillButton.setSelected(true);
				shapeToolFillOutline.setText(R.string.shape_tool_dialog_fill_title);
				squareButton.setImageResource(R.drawable.ic_pocketpaint_rectangle);
				circleButton.setImageResource(R.drawable.ic_pocketpaint_circle);
				heartButton.setImageResource(R.drawable.ic_pocketpaint_heart);
				starButton.setImageResource(R.drawable.ic_pocketpaint_star);
				outlineWidthSeekBar.setVisibility(View.GONE);
				outlineWidthEditText.setVisibility(View.GONE);
				outlineView.setVisibility(View.GONE);
				outlineTextView.setVisibility(View.GONE);
				break;
			case OUTLINE:
				outlineButton.setSelected(true);
				shapeToolFillOutline.setText(R.string.shape_tool_dialog_outline_title);
				squareButton.setImageResource(R.drawable.ic_pocketpaint_rectangle_out);
				circleButton.setImageResource(R.drawable.ic_pocketpaint_circle_out);
				heartButton.setImageResource(R.drawable.ic_pocketpaint_heart_out);
				starButton.setImageResource(R.drawable.ic_pocketpaint_star_out);
				outlineWidthSeekBar.setVisibility(View.VISIBLE);
				outlineWidthEditText.setVisibility(View.VISIBLE);
				outlineView.setVisibility(View.VISIBLE);
				outlineTextView.setVisibility(View.VISIBLE);
				break;
			default:
				fillButton.setSelected(true);
				break;
		}
	}

	public void setShapeOutlineWidth(int outlineWidth) {
		outlineWidthSeekBar.setProgress(outlineWidth);
		outlineWidthEditText.setText(String.format(Locale.getDefault(), "%d", (int) outlineWidth));
	}

	public void setOnShapeToolOptionsChangedListener(OnShapeToolOptionsChangedListener listener) {
		onShapeToolOptionsChangedListener = listener;
	}

	public interface OnShapeToolOptionsChangedListener {
		void setToolType(GeometricFillTool.BaseShape shape);
		void setDrawType(GeometricFillTool.ShapeDrawType drawType);
		void setOutlineWidth(int outlineWidth);
	}
}
