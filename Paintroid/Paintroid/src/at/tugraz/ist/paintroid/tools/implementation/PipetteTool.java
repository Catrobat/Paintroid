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

package at.tugraz.ist.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.ui.DrawingSurface;

public class PipetteTool extends BaseTool {

	DrawingSurface drawingSurface;

	public PipetteTool(Context context, ToolType toolType, DrawingSurface drawingSurface) {
		super(context, toolType);
		this.drawingSurface = drawingSurface;
	}

	@Override
	public void draw(Canvas canvas) {

	}

	@Override
	public boolean handleDown(PointF coordinate) {
		return setColor(coordinate);
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		return setColor(coordinate);
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		return setColor(coordinate);
	}

	protected boolean setColor(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		int color = drawingSurface.getBitmapColor(coordinate);
		drawPaint.setColor(color);
		super.setChanged();
		super.notifyObservers();
		return true;
	}

	@Override
	public void attributeButtonClick(int buttonNumber) {

	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {
		if (buttonNumber == 0) {
			return R.drawable.ic_menu_more_eyedropper_64;
		} else if (buttonNumber == 1) {
			if (drawPaint.getColor() == Color.TRANSPARENT) {
				return R.drawable.transparent_64;
			}
		}
		return 0;
	}

	@Override
	public int getAttributeButtonColor(int buttonNumber) {
		if (buttonNumber == 2) {
			return Color.TRANSPARENT;
		}
		return super.getAttributeButtonColor(buttonNumber);
	}

	@Override
	public void resetInternalState() {

	}
}
