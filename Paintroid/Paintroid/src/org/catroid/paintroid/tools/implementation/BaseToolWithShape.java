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

package org.catroid.paintroid.tools.implementation;

import org.catroid.paintroid.PaintroidApplication;
import org.catroid.paintroid.tools.ToolWithShape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.Display;
import android.view.WindowManager;

public abstract class BaseToolWithShape extends BaseTool implements ToolWithShape {

	protected int primaryShapeColor = Color.BLACK;
	protected int secondaryShapeColor = Color.YELLOW;

	protected PointF toolPosition;

	public BaseToolWithShape(Context context, ToolType toolType) {
		super(context, toolType);
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		this.toolPosition = new PointF(display.getWidth() / 2f, display.getHeight() / 2f);
		PaintroidApplication.CURRENT_PERSPECTIVE.convertFromScreenToCanvas(toolPosition);
	}

	@Override
	public abstract void drawShape(Canvas canvas);

}
