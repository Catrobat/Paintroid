/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;

public interface Tool {

	boolean handleDown(PointF coordinate);

	boolean handleMove(PointF coordinate);

	boolean handleUp(PointF coordinate);

	boolean handleTouch(PointF coordinate, int motionEventType);

	void changePaintColor(int color);

	void changePaintStrokeWidth(int strokeWidth);

	void changePaintStrokeCap(Cap cap);

	Paint getDrawPaint();

	void setDrawPaint(Paint paint);

	void draw(Canvas canvas);

	ToolType getToolType();

	void resetInternalState(StateChange stateChange);

	Point getAutoScrollDirection(float pointX, float pointY,
			int screenWidth, int screenHeight);

	void hide();

	void toggleShowToolOptions();

	void onSaveInstanceState(Bundle bundle);

	void onRestoreInstanceState(Bundle bundle);

	void setupToolOptions();

	boolean getToolOptionsAreShown();

	void startTool();

	void leaveTool();

	enum StateChange {
		ALL, RESET_INTERNAL_STATE, NEW_IMAGE_LOADED, MOVE_CANCELED
	}
}
