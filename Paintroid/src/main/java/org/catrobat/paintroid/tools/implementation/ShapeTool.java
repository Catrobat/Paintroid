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

package org.catrobat.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.drawable.DrawableFactory;
import org.catrobat.paintroid.tools.drawable.DrawableShape;
import org.catrobat.paintroid.tools.drawable.DrawableStyle;
import org.catrobat.paintroid.tools.drawable.ShapeDrawable;
import org.catrobat.paintroid.tools.helper.Conversion;
import org.catrobat.paintroid.tools.options.ShapeToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

public class ShapeTool extends BaseToolWithRectangleShape {
	private static final float SHAPE_OFFSET = 10f;
	private static final int DEFAULT_OUTLINE_WIDTH = 25;

	private static final String BUNDLE_BASE_SHAPE = "BASE_SHAPE";
	private static final String BUNDLE_SHAPE_DRAW_TYPE = "SHAPE_DRAW_TYPE";
	private static final String BUNDLE_OUTLINE_WIDTH = "OUTLINE_WIDTH";

	private final ShapeToolOptionsView shapeToolOptionsView;

	private final Paint shapePreviewPaint = new Paint();
	private final Paint shapeBitmapPaint = new Paint();
	private final RectF shapePreviewRect = new RectF();
	private final DrawableFactory drawableFactory = new DrawableFactory();

	private DrawableShape baseShape = DrawableShape.RECTANGLE;
	private DrawableStyle shapeDrawType = DrawableStyle.FILL;
	private ShapeDrawable shapeDrawable = drawableFactory.createDrawable(baseShape);
	private int shapeOutlineWidth = DEFAULT_OUTLINE_WIDTH;

	public ShapeTool(ShapeToolOptionsView shapeToolOptionsView, ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController,
			ToolPaint toolPaint, final Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);

		this.rotationEnabled = true;

		this.shapeToolOptionsView = shapeToolOptionsView;
		this.shapeToolOptionsView.setCallback(
				new ShapeToolOptionsView.Callback() {
					@Override
					public void setToolType(DrawableShape shape) {
						setBaseShape(shape);
						workspace.invalidate();
					}

					@Override
					public void setDrawType(DrawableStyle drawType) {
						shapeDrawType = drawType;
						workspace.invalidate();
					}

					@Override
					public void setOutlineWidth(int outlineWidth) {
						shapeOutlineWidth = outlineWidth;
						workspace.invalidate();
					}
				});

		toolOptionsViewController.showDelayed();
	}

	public DrawableShape getBaseShape() {
		return baseShape;
	}

	public void setBaseShape(DrawableShape shape) {
		baseShape = shape;
		shapeDrawable = drawableFactory.createDrawable(shape);
	}

	public Paint getShapeBitmapPaint() {
		return shapeBitmapPaint;
	}

	@Override
	public void changePaintColor(int color) {
		super.changePaintColor(color);
		workspace.invalidate();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);

		bundle.putSerializable(BUNDLE_BASE_SHAPE, baseShape);
		bundle.putSerializable(BUNDLE_SHAPE_DRAW_TYPE, shapeDrawType);
		bundle.putInt(BUNDLE_OUTLINE_WIDTH, shapeOutlineWidth);
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);

		DrawableShape newBaseShape = (DrawableShape) bundle.getSerializable(BUNDLE_BASE_SHAPE);
		DrawableStyle newShapeDrawType = (DrawableStyle) bundle.getSerializable(BUNDLE_SHAPE_DRAW_TYPE);
		int newShapeOutlineWidth = bundle.getInt(BUNDLE_OUTLINE_WIDTH);

		if (newBaseShape != null && newShapeDrawType != null
				&& (this.baseShape != newBaseShape || this.shapeDrawType != newShapeDrawType)) {
			this.baseShape = newBaseShape;
			this.shapeDrawType = newShapeDrawType;
			this.shapeOutlineWidth = newShapeOutlineWidth;
			this.shapeDrawable = drawableFactory.createDrawable(newBaseShape);

			shapeToolOptionsView.setShapeActivated(newBaseShape);
			shapeToolOptionsView.setDrawTypeActivated(newShapeDrawType);
			shapeToolOptionsView.setShapeOutlineWidth(newShapeOutlineWidth);
		}
	}

	@Override
	protected void drawBitmap(Canvas canvas, float boxWidth, float boxHeight) {
		shapePreviewPaint.set(toolPaint.getPreviewPaint());
		preparePaint(shapePreviewPaint);
		prepareShapeRectangle(shapePreviewRect, boxWidth, boxHeight);

		shapeDrawable.draw(canvas, shapePreviewRect, shapePreviewPaint);
	}

	private void prepareShapeRectangle(RectF shapeRect, float boxWidth, float boxHeight) {
		shapeRect.setEmpty();
		shapeRect.inset(SHAPE_OFFSET - boxWidth / 2, SHAPE_OFFSET - boxHeight / 2);
		if (shapePreviewPaint.getStyle() == Style.STROKE) {
			shapeRect.inset(shapeOutlineWidth / 2f, shapeOutlineWidth / 2f);
		}
	}

	private void preparePaint(Paint paint) {
		switch (shapeDrawType) {
			case FILL:
				paint.setStyle(Style.FILL);
				paint.setStrokeJoin(Paint.Join.MITER);
				break;
			case STROKE:
				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(shapeOutlineWidth);
				paint.setStrokeCap(Paint.Cap.BUTT);
				paint.setStrokeJoin(Paint.Join.MITER);
				boolean antiAlias = (shapeOutlineWidth > 1);
				paint.setAntiAlias(antiAlias);
				break;
			default:
				break;
		}
	}

	@Override
	public ToolType getToolType() {
		return ToolType.SHAPE;
	}

	@Override
	public void onClickOnButton() {
		if (toolPosition.x - boxWidth / 2 <= workspace.getWidth()
				&& toolPosition.y - boxHeight / 2 <= workspace.getHeight()
				&& toolPosition.x + boxWidth / 2 >= 0
				&& toolPosition.y + boxHeight / 2 >= 0) {

			shapeBitmapPaint.set(toolPaint.getPaint());
			RectF shapeRect = new RectF();
			preparePaint(shapeBitmapPaint);
			prepareShapeRectangle(shapeRect, boxWidth, boxHeight);

			Command command = commandFactory.createGeometricFillCommand(shapeDrawable,
					Conversion.toPoint(toolPosition), shapeRect, boxRotation, shapeBitmapPaint);
			commandManager.addCommand(command);
			highlightBox();
		}
	}
}
