/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.ui.DrawingSurface;
import at.tugraz.ist.paintroid.ui.button.ToolbarButton;

public class PipetteTool extends BaseTool {

	DrawingSurface drawingSurface;

	public PipetteTool(Context context, ToolType toolType, DrawingSurface drawingSurface) {
		super(context, toolType);
		this.drawingSurface = drawingSurface;
	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
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
		changePaintColor(color);
		return true;
	}

	@Override
	public void attributeButtonClick(int buttonNumber) {

	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {

		switch (buttonNumber) {
			case ToolbarButton.BUTTON_ID_PARAMETER_1:
				return NO_BUTTON_RESOURCE;
			default:
				return super.getAttributeButtonResource(buttonNumber);
		}
	}

	@Override
	public int getAttributeButtonColor(int buttonNumber) {

		switch (buttonNumber) {
			case ToolbarButton.BUTTON_ID_PARAMETER_1:
				return Color.TRANSPARENT;
			default:
				return super.getAttributeButtonColor(buttonNumber);
		}

	}

	@Override
	public void resetInternalState() {

	}
}
