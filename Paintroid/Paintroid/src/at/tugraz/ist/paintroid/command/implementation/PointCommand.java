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

package at.tugraz.ist.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;

public class PointCommand extends BaseCommand {

	protected PointF point;

	public PointCommand(Paint paint, PointF point) {
		super(paint);
		this.point = new PointF(point.x, point.y);
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		Log.d(PaintroidApplication.TAG, "PointCommand.run");
		canvas.drawPoint(point.x, point.y, paint);
	}

	@Override
	public boolean isUndoable() {
		return true;
	}

}
