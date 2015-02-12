/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools;

import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PointF;

public interface Tool {

	// standard stroke widths in pixels
	public static final int stroke1 = 1;
	public static final int stroke5 = 5;
	public static final int stroke15 = 15;
	public static final int stroke25 = 25;

	public enum StateChange {
		ALL, RESET_INTERNAL_STATE, NEW_IMAGE_LOADED, MOVE_CANCELED
	}

	public boolean handleDown(PointF coordinate);

	public boolean handleMove(PointF coordinate);

	public boolean handleUp(PointF coordinate);

	public void changePaintColor(int color);

	public void changePaintStrokeWidth(int strokeWidth);

	public void changePaintStrokeCap(Cap cap);

	public void setDrawPaint(Paint paint);

	public Paint getDrawPaint();

	public void draw(Canvas canvas);

	public ToolType getToolType();

	public int getAttributeButtonResource(ToolButtonIDs buttonNumber);

	public int getAttributeButtonColor(ToolButtonIDs buttonNumber);

	public void attributeButtonClick(ToolButtonIDs buttonNumber);

	public void resetInternalState(StateChange stateChange);

	public Point getAutoScrollDirection(float pointX, float pointY,
			int screenWidth, int screenHeight);
}
