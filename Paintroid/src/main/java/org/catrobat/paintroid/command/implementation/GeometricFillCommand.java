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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.tools.drawable.ShapeDrawable;

public class GeometricFillCommand implements Command {
	private final float boxRotation;
	private final RectF boxRect;
	private final int pointX;
	private final int pointY;
	private final Paint paint;
	private final ShapeDrawable shapeDrawable;

	public GeometricFillCommand(ShapeDrawable shapeDrawable, int pointX, int pointY, RectF boxRect,
			float boxRotation, Paint paint) {
		this.pointX = pointX;
		this.pointY = pointY;
		this.boxRect = boxRect;
		this.shapeDrawable = shapeDrawable;
		this.boxRotation = boxRotation;
		this.paint = paint;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		canvas.save();
		canvas.translate(pointX, pointY);
		canvas.rotate(boxRotation);
		shapeDrawable.draw(canvas, boxRect, paint);
		canvas.restore();
	}

	@Override
	public void freeResources() {
		//No resources to free
	}
}
