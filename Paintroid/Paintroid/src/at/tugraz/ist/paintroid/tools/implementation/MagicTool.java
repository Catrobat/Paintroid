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
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.implementation.MagicCommand;

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
		PaintroidApplication.COMMAND_HANDLER.commitCommand(command);
		return true;
	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {
		if (buttonNumber == 0) {
			return R.drawable.ic_menu_more_magic_64;
		}
		return super.getAttributeButtonResource(buttonNumber);
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
