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

package org.catrobat.paintroid.command;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection;
import org.catrobat.paintroid.command.implementation.RotateCommand.RotateDirection;
import org.catrobat.paintroid.tools.drawable.ShapeDrawable;

public interface CommandFactory {

	Command createInitCommand(int width, int height);

	Command createInitCommand(Bitmap bitmap);

	Command createResetCommand();

	Command createAddLayerCommand();

	Command createSelectLayerCommand(int position);

	Command createRemoveLayerCommand(int index);

	Command createReorderLayersCommand(int position, int swapWith);

	Command createMergeLayersCommand(int position, int mergeWith);

	Command createRotateCommand(RotateDirection rotateDirection);

	Command createFlipCommand(FlipDirection flipDirection);

	Command createCropCommand(int resizeCoordinateXLeft, int resizeCoordinateYTop,
			int resizeCoordinateXRight, int resizeCoordinateYBottom,
			int maximumBitmapResolution);

	Command createPointCommand(Paint paint, PointF coordinate);

	Command createFillCommand(int x, int y, Paint paint, float colorTolerance);

	Command createGeometricFillCommand(ShapeDrawable shapeDrawable, Point position, RectF box, float boxRotation, Paint paint);

	Command createPathCommand(Paint paint, Path path);

	Command createTextToolCommand(String[] multilineText, Paint textPaint, int boxOffset, float boxWidth, float boxHeight, PointF toolPosition, float boxRotation);

	Command createResizeCommand(int newWidth, int newHeight);

	Command createStampCommand(Bitmap bitmap, PointF toolPosition, float boxWidth, float boxHeight, float boxRotation);

	Command createCutCommand(PointF toolPosition, float boxWidth, float boxHeight, float boxRotation);
}
