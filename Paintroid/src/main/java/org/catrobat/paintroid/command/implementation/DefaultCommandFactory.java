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

package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection;
import org.catrobat.paintroid.command.implementation.RotateCommand.RotateDirection;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.tools.drawable.ShapeDrawable;
import org.catrobat.paintroid.tools.helper.Conversion;
import org.catrobat.paintroid.tools.helper.JavaFillAlgorithmFactory;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class DefaultCommandFactory implements CommandFactory {

	private CommonFactory commonFactory = new CommonFactory();

	@Override
	public Command createInitCommand(int width, int height) {
		CompositeCommand command = new CompositeCommand();
		command.addCommand(new SetDimensionCommand(width, height));
		command.addCommand(new AddLayerCommand(commonFactory));
		return command;
	}

	@Override
	public Command createInitCommand(Bitmap bitmap) {
		CompositeCommand command = new CompositeCommand();
		command.addCommand(new SetDimensionCommand(bitmap.getWidth(), bitmap.getHeight()));
		command.addCommand(new LoadCommand(bitmap.copy(ARGB_8888, false)));
		return command;
	}

	@Override
	public Command createResetCommand() {
		CompositeCommand command = new CompositeCommand();
		command.addCommand(new ResetCommand());
		command.addCommand(new AddLayerCommand(commonFactory));
		return command;
	}

	@Override
	public Command createAddLayerCommand() {
		return new AddLayerCommand(commonFactory);
	}

	@Override
	public Command createSelectLayerCommand(int position) {
		return new SelectLayerCommand(position);
	}

	@Override
	public Command createRemoveLayerCommand(int index) {
		return new RemoveLayerCommand(index);
	}

	@Override
	public Command createReorderLayersCommand(int position, int swapWith) {
		return new ReorderLayersCommand(position, swapWith);
	}

	@Override
	public Command createMergeLayersCommand(int position, int mergeWith) {
		return new MergeLayersCommand(position, mergeWith);
	}

	@Override
	public Command createRotateCommand(RotateDirection rotateDirection) {
		return new RotateCommand(rotateDirection);
	}

	@Override
	public Command createFlipCommand(FlipDirection flipDirection) {
		return new FlipCommand(flipDirection);
	}

	@Override
	public Command createCropCommand(int resizeCoordinateXLeft, int resizeCoordinateYTop, int resizeCoordinateXRight, int resizeCoordinateYBottom, int maximumBitmapResolution) {
		return new CropCommand(resizeCoordinateXLeft, resizeCoordinateYTop, resizeCoordinateXRight, resizeCoordinateYBottom, maximumBitmapResolution);
	}

	@Override
	public Command createPointCommand(Paint paint, PointF coordinate) {
		return new PointCommand(
				commonFactory.createPaint(paint),
				commonFactory.createPointF(coordinate)
		);
	}

	@Override
	public Command createFillCommand(int x, int y, Paint paint, float colorTolerance) {
		return new FillCommand(new JavaFillAlgorithmFactory(),
				commonFactory.createPoint(x, y),
				commonFactory.createPaint(paint), colorTolerance);
	}

	@Override
	public Command createGeometricFillCommand(ShapeDrawable shapeDrawable, Point position, RectF box, float boxRotation, Paint paint) {
		RectF destRectF = commonFactory.createRectF(box);
		return new GeometricFillCommand(shapeDrawable, position.x, position.y, destRectF, boxRotation, commonFactory.createPaint(paint));
	}

	@Override
	public Command createPathCommand(Paint paint, Path path) {
		return new PathCommand(commonFactory.createPaint(paint), commonFactory.createPath(path));
	}

	@Override
	public Command createTextToolCommand(String[] multilineText, Paint textPaint, int boxOffset, float boxWidth, float boxHeight, PointF toolPosition, float boxRotation) {
		return new TextToolCommand(multilineText, commonFactory.createPaint(textPaint),
				boxOffset, boxWidth, boxHeight, commonFactory.createPointF(toolPosition),
				boxRotation);
	}

	@Override
	public Command createResizeCommand(int newWidth, int newHeight) {
		return new ResizeCommand(newWidth, newHeight);
	}

	@Override
	public Command createStampCommand(Bitmap bitmap, PointF toolPosition, float width, float height, float rotation) {
		return new StampCommand(bitmap, Conversion.toPoint(toolPosition), width, height, rotation);
	}

	@Override
	public Command createCutCommand(PointF toolPosition, float boxWidth, float boxHeight, float boxRotation) {
		return new CutCommand(Conversion.toPoint(toolPosition), boxWidth, boxHeight, boxRotation);
	}
}
