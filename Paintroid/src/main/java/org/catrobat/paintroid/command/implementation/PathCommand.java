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
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.VisibleForTesting;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.contract.LayerContracts;

public class PathCommand implements Command {
	@VisibleForTesting
	public Paint paint;
	@VisibleForTesting
	public Path path;
	private CommonFactory commonFactory;

	public PathCommand(Paint paint, Path path, CommonFactory commonFactory) {
		this.paint = paint;
		this.path = path;
		this.commonFactory = commonFactory;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		RectF bounds = commonFactory.createRectF();
		path.computeBounds(bounds, true);
		Rect boundsCanvas = canvas.getClipBounds();

		if (pathInCanvas(bounds, boundsCanvas)) {
			canvas.drawPath(path, paint);
		}
	}

	private boolean pathInCanvas(RectF rectangleBoundsPath, Rect rectangleBoundsCanvas) {
		float strokeWidth = paint.getStrokeWidth();

		rectangleBoundsPath.inset(-strokeWidth, -strokeWidth);
		return rectangleBoundsCanvas.left < rectangleBoundsPath.right
				&& rectangleBoundsPath.left < rectangleBoundsCanvas.right
				&& rectangleBoundsCanvas.top < rectangleBoundsPath.bottom
				&& rectangleBoundsPath.top < rectangleBoundsCanvas.bottom;
	}

	@Override
	public void freeResources() {
	}
}
