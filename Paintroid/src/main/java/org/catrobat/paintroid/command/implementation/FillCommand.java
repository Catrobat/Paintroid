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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.tools.helper.FillAlgorithm;
import org.catrobat.paintroid.tools.helper.FillAlgorithmFactory;

public class FillCommand implements Command {
	private Paint paint;
	private float colorTolerance;
	private FillAlgorithmFactory fillAlgorithmFactory;
	private Point clickedPixel;

	public FillCommand(FillAlgorithmFactory fillAlgorithmFactory, Point clickedPixel, Paint paint, float colorTolerance) {
		this.fillAlgorithmFactory = fillAlgorithmFactory;
		this.clickedPixel = clickedPixel;
		this.paint = paint;
		this.colorTolerance = colorTolerance;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		Bitmap bitmap = layerModel.getCurrentLayer().getBitmap();

		int replacementColor = bitmap.getPixel(clickedPixel.x, clickedPixel.y);
		int targetColor = paint.getColor();

		FillAlgorithm fillAlgorithm = fillAlgorithmFactory.createFillAlgorithm();

		fillAlgorithm.setParameters(bitmap, clickedPixel, targetColor, replacementColor, colorTolerance);
		fillAlgorithm.performFilling();
	}

	@Override
	public void freeResources() {
	}
}
