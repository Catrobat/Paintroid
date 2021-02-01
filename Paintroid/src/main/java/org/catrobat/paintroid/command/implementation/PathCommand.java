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

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.contract.LayerContracts;

import androidx.annotation.VisibleForTesting;

public class PathCommand implements Command {
	@VisibleForTesting
	public Paint paint;
	@VisibleForTesting
	public Path path;

	public PathCommand(Paint paint, Path path) {
		this.paint = paint;
		this.path = path;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		canvas.drawPath(path, paint);
	}

	@Override
	public void freeResources() {
	}
}
