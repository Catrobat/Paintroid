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

package org.catrobat.paintroid.test.junit.command;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.FillCommand;
import org.catrobat.paintroid.model.Layer;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.tools.helper.JavaFillAlgorithmFactory;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class FillCommandTest {

	@Test
	public void testCommandsDoNotLeakMemory() {
		List<Command> commands = new LinkedList<>();
		Bitmap testBitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
		Point clickedPixel = new Point(10, 10);
		Paint paint = new Paint();
		Canvas canvas = new Canvas();

		LayerModel layerModel = new LayerModel();
		Layer testLayer = new Layer(testBitmap);
		layerModel.addLayerAt(0, testLayer);
		layerModel.setCurrentLayer(testLayer);

		for (int i = 0; i < 100; i++) {
			FillCommand fillCommand = new FillCommand(new JavaFillAlgorithmFactory(), clickedPixel, paint, 0.5f);
			fillCommand.run(canvas, layerModel);
			commands.add(fillCommand);
		}
	}
}
