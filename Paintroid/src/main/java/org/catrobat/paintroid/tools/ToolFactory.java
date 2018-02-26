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

import android.app.Activity;

import org.catrobat.paintroid.tools.implementation.CursorTool;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.catrobat.paintroid.tools.implementation.EraserTool;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;
import org.catrobat.paintroid.tools.implementation.ImportTool;
import org.catrobat.paintroid.tools.implementation.LineTool;
import org.catrobat.paintroid.tools.implementation.PipetteTool;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.catrobat.paintroid.tools.implementation.TextTool;
import org.catrobat.paintroid.tools.implementation.TransformTool;

public final class ToolFactory {

	private ToolFactory() {
	}

	public static Tool createTool(Activity context, ToolType toolType) {
		Tool tool;
		switch (toolType) {
			case BRUSH:
				tool = new DrawTool(context, toolType);
				break;
			case CURSOR:
				tool = new CursorTool(context, toolType);
				break;
			case STAMP:
				tool = new StampTool(context, toolType);
				break;
			case IMPORTPNG:
				tool = new ImportTool(context, toolType);
				break;
			case PIPETTE:
				tool = new PipetteTool(context, toolType);
				break;
			case FILL:
				tool = new FillTool(context, toolType);
				break;
			case TRANSFORM:
				tool = new TransformTool(context, toolType);
				break;
			case SHAPE:
				tool = new GeometricFillTool(context, toolType);
				break;
			case ERASER:
				tool = new EraserTool(context, toolType);
				break;
			case LINE:
				tool = new LineTool(context, toolType);
				break;
			case TEXT:
				tool = new TextTool(context, toolType);
				break;
			default:
				tool = new DrawTool(context, ToolType.BRUSH);
				break;
		}
		tool.setupToolOptions();
		return tool;
	}
}
