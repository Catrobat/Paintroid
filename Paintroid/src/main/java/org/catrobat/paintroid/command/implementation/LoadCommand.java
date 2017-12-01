/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.Layer;

public class LoadCommand extends BaseCommand {

	private Bitmap loadedImage;

	public LoadCommand(Bitmap newBitmap) {
		loadedImage = newBitmap.copy(Bitmap.Config.ARGB_8888, true);
	}

	@Override
	public void run(Canvas canvas, Layer layer) {

		notifyStatus(NotifyStates.COMMAND_STARTED);
		Bitmap buffer = loadedImage.copy(Bitmap.Config.ARGB_8888, loadedImage.isMutable());
		PaintroidApplication.drawingSurface.resetBitmap(buffer);
		layer.setImage(buffer);

		notifyStatus(NotifyStates.COMMAND_DONE);
	}
}
