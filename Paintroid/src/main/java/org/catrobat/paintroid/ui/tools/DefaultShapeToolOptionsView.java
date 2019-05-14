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

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.drawable.DrawableShape;
import org.catrobat.paintroid.tools.drawable.DrawableStyle;
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter;
import org.catrobat.paintroid.tools.options.ShapeToolOptionsView;

import java.util.Locale;

public class DefaultShapeToolOptionsView implements ShapeToolOptionsView {
	private static final int MIN_STROKE_WIDTH = 1;

	private Callback callback;
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

	public DefaultShapeToolOptionsView(ViewGroup rootView) {
		LayoutInflater inflater = LayoutInflater.from(rootView.getContext());
		View shapeToolView = inflater.inflate(R.layout.dialog_pocketpaint_shapes, rootView);

		squareButton = shapeToolView.findViewById(R.id.pocketpaint_shapes_square_btn);
		circleButton = shapeToolView.findViewById(R.id.pocketpaint_shapes_circle_btn);
		heartButton = shapeToolView.findViewById(R.id.pocketpaint_shapes_heart_btn);
		starButton = shapeToolView.findViewById(R.id.pocketpaint_shapes_star_btn);
		fillButton = shapeToolView.findViewById(R.id.pocketpaint_shape_ibtn_fill);
		outlineButton = shapeToolView.findViewById(R.id.pocketpaint_shape_ibtn_outline);
		shapeToolDialogTitle = shapeToolView.findViewById(R.id.pocketpaint_shape_tool_dialog_title);
		shapeToolFillOutline = shapeToolView.findViewById(R.id.pocketpaint_shape_tool_fill_outline);

		outlineView = shapeToolView.findViewById(R.id.pocketpaint_outline_view_border);
		outlineTextView = shapeToolView.findViewById(R.id.pocketpaint_outline_view_text_view);

		outlineWidthSeekBar = shapeToolView.findViewById(R.id.pocketpaint_shape_stroke_width_seek_bar);
		outlineWidthEditText = shapeToolView.findViewById(R.id.pocketpaint_shape_outline_edit);
		outlineWidthEditText.setFilters(new InputFilter[]{new DefaultNumberRangeFilter(1, 100)});

		int startingOutlineWidth = 25;
		outlineWidthEditText.setText(String.valueOf(startingOutlineWidth));
		outlineWidthSeekBar.setProgress(startingOutlineWidth);

		initializeListeners();
		setShapeActivated(DrawableShape.RECTANGLE);
		setDrawTypeActivated(DrawableStyle.FILL);
	}

	private void initializeListeners() {
		squareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShapeClicked(DrawableShape.RECTANGLE);
			}
		});
		circleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShapeClicked(DrawableShape.OVAL);
			}
		});
		heartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShapeClicked(DrawableShape.HEART);
			}
		});
		starButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShapeClicked(DrawableShape.STAR);
			}
		});
		fillButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDrawTypeClicked(DrawableStyle.FILL);
			}
		});
		outlineButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDrawTypeClicked(DrawableStyle.STROKE);
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
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
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

	private void onShapeClicked(DrawableShape shape) {
		callback.setToolType(shape);
		setShapeActivated(shape);
	}

	private void onDrawTypeClicked(DrawableStyle drawType) {
		callback.setDrawType(drawType);
		setDrawTypeActivated(drawType);
	}

	private void onOutlineWidthChanged(int outlineWidth) {
		callback.setOutlineWidth(outlineWidth);
	}

	private void resetShapeActivated() {
		View[] buttons = {squareButton, circleButton, heartButton, starButton};
		for (View button : buttons) {
			button.setSelected(false);
		}
	}

	private void resetDrawTypeActivated() {
		fillButton.setSelected(false);
		outlineButton.setSelected(false);
	}

	@Override
	public void setShapeActivated(DrawableShape shape) {
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

	@Override
	public void setDrawTypeActivated(DrawableStyle drawType) {
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
			case STROKE:
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

	@Override
	public void setShapeOutlineWidth(int outlineWidth) {
		outlineWidthSeekBar.setProgress(outlineWidth);
		outlineWidthEditText.setText(String.format(Locale.getDefault(), "%d", (int) outlineWidth));
	}

	@Override
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
}
