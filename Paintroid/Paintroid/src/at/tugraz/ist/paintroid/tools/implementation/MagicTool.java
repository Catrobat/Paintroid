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
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.MagicCommand;
import at.tugraz.ist.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

public class MagicTool extends BaseTool {

	public MagicTool(Context context, ToolType toolType) {
		super(context, toolType);
	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		Command command = new MagicCommand(bitmapPaint, coordinate);
		PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
		return true;
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {

		switch (buttonNumber) {
			case BUTTON_ID_PARAMETER_TOP_1:
			case BUTTON_ID_PARAMETER_BOTTOM_1:
				return Color.TRANSPARENT;

			default:
				return super.getAttributeButtonColor(buttonNumber);
		}
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
			case BUTTON_ID_PARAMETER_BOTTOM_2:
				return R.drawable.icon_menu_color_palette;
			case BUTTON_ID_PARAMETER_TOP_2:
				return getStrokeColorResource();
			default:
				return super.getAttributeButtonResource(buttonNumber);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
			case BUTTON_ID_PARAMETER_BOTTOM_2:
			case BUTTON_ID_PARAMETER_TOP_2:
				showColorPicker();
				break;
			default:
				break;
		}
	}

	@Override
	public void resetInternalState() {

	}
}
