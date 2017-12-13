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

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;

public class ShapeToolOptionsListener {
	private GeometricFillTool.BaseShape shape;
	private OnShapeToolOptionsChangedListener onShapeToolOptionsChangedListener;
	private ImageButton squareButton;
	private ImageButton circleButton;
	private ImageButton heartButton;
	private ImageButton starButton;
	private TextView shapeToolDialogTitle;

	public ShapeToolOptionsListener(View shapeToolOptionsView) {
		shape = GeometricFillTool.BaseShape.RECTANGLE;
		squareButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_square_btn);
		circleButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_circle_btn);
		heartButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_heart_btn);
		starButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_star_btn);
		shapeToolDialogTitle = (TextView) shapeToolOptionsView.findViewById(R.id.shape_tool_dialog_title);

		initializeListeners();
		setShapeActivated(shape);
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
	}

	private void onShapeClicked(GeometricFillTool.BaseShape shape) {
		if (this.shape == shape) {
			return;
		}
		onShapeToolOptionsChangedListener.setToolType(shape);
		setShapeActivated(shape);
		this.shape = shape;
	}

	private void resetShapeActivated() {
		View[] buttons = {squareButton, circleButton, heartButton, starButton};
		for (View button : buttons) {
			button.setSelected(false);
		}
	}

	private void setShapeActivated(GeometricFillTool.BaseShape shape) {
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

	public void setOnShapeToolOptionsChangedListener(OnShapeToolOptionsChangedListener listener) {
		onShapeToolOptionsChangedListener = listener;
	}

	public interface OnShapeToolOptionsChangedListener {
		void setToolType(GeometricFillTool.BaseShape shape);
	}
}
