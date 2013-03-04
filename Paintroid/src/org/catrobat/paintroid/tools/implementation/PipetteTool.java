/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Statusbar.ToolButtonIDs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;

public class PipetteTool extends BaseTool {

	public PipetteTool(Context context, ToolType toolType) {
		super(context, toolType);
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
		int color = PaintroidApplication.drawingSurface
				.getBitmapColor(coordinate);
		ColorPickerDialog.getInstance().setInitialColor(color);
		changePaintColor(color);
		return true;
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {

		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return getStrokeColorResource();
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {

		return super.getAttributeButtonColor(buttonNumber);

	}

	@Override
	public void resetInternalState() {

	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		// no clicks allowed
	}
}
