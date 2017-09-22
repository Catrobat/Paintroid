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
import org.catrobat.paintroid.listener.LayerListener;

public class LoadCommand extends BaseCommand {

	private Bitmap mLoadedImage;

	public LoadCommand(Bitmap newBitmap) {
		mLoadedImage = newBitmap.copy(Bitmap.Config.ARGB_8888, true);
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {

		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		Bitmap buffer = mLoadedImage.copy(Bitmap.Config.ARGB_8888, mLoadedImage.isMutable());
		PaintroidApplication.drawingSurface.resetBitmap(buffer);
		LayerListener.getInstance().getCurrentLayer().setImage(buffer);

		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
