/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.commandmanagement.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;

public class StampCommand extends BaseCommand {

	protected Point coordiante;
	protected float width;
	protected float height;
	protected float rotation;
	protected Bitmap bitmap;

	public StampCommand(Bitmap bitmap, Point position, float width, float height, float rotation) {
		super(new Paint(Paint.DITHER_FLAG));
		this.coordiante = new Point(position.x, position.y);
		this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		this.width = width;
		this.height = height;
		this.rotation = rotation;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		Log.d(PaintroidApplication.TAG, "StampCommand.run");

		canvas.save();
		canvas.translate(coordiante.x, coordiante.y);
		canvas.rotate(rotation);
		canvas.drawBitmap(this.bitmap, null, new RectF(-width / 2, -height / 2, width / 2, height / 2), paint);
		canvas.restore();
		this.bitmap.recycle();
	}

	@Override
	public boolean isUndoable() {
		return false;
	}

}
