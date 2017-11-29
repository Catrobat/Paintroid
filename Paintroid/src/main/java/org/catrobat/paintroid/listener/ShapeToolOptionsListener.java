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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;

public class ShapeToolOptionsListener {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "ShapeToolDialog has not been initialized. Call init() first!";

	private static ShapeToolOptionsListener instance;
	private static GeometricFillTool.BaseShape shape;
	private OnShapeToolOptionsChangedListener onShapeToolOptionsChangedListener;
	private ImageButton squareButton;
	private ImageButton circleButton;
	private ImageButton heartButton;
	private ImageButton starButton;

	public ShapeToolOptionsListener(View shapeToolOptionsView) {
		if (shape == null) {
			shape = GeometricFillTool.BaseShape.RECTANGLE;
		}
		initializeListeners(shapeToolOptionsView);
	}

	public static ShapeToolOptionsListener getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	public static void init(View shapeToolOptionsView) {
		instance = new ShapeToolOptionsListener(shapeToolOptionsView);
	}

	private void initializeListeners(final View shapeToolOptionsView) {
		setShapeActivated(shapeToolOptionsView, shape);
		squareButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_square_btn);
		squareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shape = GeometricFillTool.BaseShape.RECTANGLE;
				onShapeToolOptionsChangedListener.setToolType(shape);
				setShapeActivated(shapeToolOptionsView, shape);
			}
		});

		circleButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_circle_btn);
		circleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shape = GeometricFillTool.BaseShape.OVAL;
				onShapeToolOptionsChangedListener.setToolType(shape);
				setShapeActivated(shapeToolOptionsView, shape);
			}
		});

		heartButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_heart_btn);
		heartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shape = GeometricFillTool.BaseShape.HEART;
				onShapeToolOptionsChangedListener.setToolType(shape);
				setShapeActivated(shapeToolOptionsView, shape);
			}
		});

		starButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_star_btn);
		starButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shape = GeometricFillTool.BaseShape.STAR;
				onShapeToolOptionsChangedListener.setToolType(shape);
				setShapeActivated(shapeToolOptionsView, shape);
			}
		});
	}

	private void resetShapeActivated(View shapeToolOptionsView) {
		LinearLayout shapesContainer = (LinearLayout) shapeToolOptionsView.findViewById(R.id.shapes_container);
		for (int i = 0; i < shapesContainer.getChildCount(); i++) {
			shapesContainer.getChildAt(i).setBackgroundResource(R.color.transparent);
		}
	}

	private void setShapeActivated(View shapeToolOptionsView, GeometricFillTool.BaseShape shape) {
		resetShapeActivated(shapeToolOptionsView);
		TextView shapeToolDialogTitle = (TextView) shapeToolOptionsView.findViewById(R.id.shape_tool_dialog_title);
		switch (shape) {
			case RECTANGLE:
				shapeToolOptionsView.findViewById(R.id.shapes_square_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
				shapeToolDialogTitle.setText(R.string.shape_tool_dialog_rect_title);
				break;
			case OVAL:
				shapeToolOptionsView.findViewById(R.id.shapes_circle_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
				shapeToolDialogTitle.setText(R.string.shape_tool_dialog_ellipse_title);
				break;
			case STAR:
				shapeToolOptionsView.findViewById(R.id.shapes_star_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
				shapeToolDialogTitle.setText(R.string.shape_tool_dialog_star_title);
				break;
			case HEART:
				shapeToolOptionsView.findViewById(R.id.shapes_heart_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
				shapeToolDialogTitle.setText(R.string.shape_tool_dialog_heart_title);
				break;
			default:
				shapeToolOptionsView.findViewById(R.id.shapes_square_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
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
