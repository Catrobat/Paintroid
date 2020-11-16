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
import android.graphics.PointF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.contract.LayerContracts;

import androidx.annotation.VisibleForTesting;

public class PointCommand implements Command {
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public Paint paint;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public PointF point;

	public PointCommand(Paint paint, PointF point) {
		this.paint = paint;
		this.point = point;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		canvas.drawPoint(point.x, point.y, paint);
	}

	@Override
	public void freeResources() {
	}
}
