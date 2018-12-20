/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.app.Activity;

import org.catrobat.paintroid.ContextActivityWrapper;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolType;

public class DefaultToolFactory implements ToolFactory {
	@Override
	public Tool createTool(Activity activity, ToolType toolType) {
		ContextActivityWrapper contextActivityWrapper = new ContextActivityWrapper(activity);
		Tool tool;
		switch (toolType) {
			case BRUSH:
				tool = new DrawTool(contextActivityWrapper, activity, toolType);
				break;
			case CURSOR:
				tool = new CursorTool(contextActivityWrapper, activity, toolType);
				break;
			case STAMP:
				tool = new StampTool(contextActivityWrapper, activity, toolType);
				break;
			case IMPORTPNG:
				tool = new ImportTool(contextActivityWrapper, activity, toolType);
				break;
			case PIPETTE:
				final MainActivity mainActivity = (MainActivity) activity;
				final MainActivityContracts.Presenter presenter = mainActivity.getPresenter();
				ColorPickerDialog.OnColorPickedListener listener = new ColorPickerDialog.OnColorPickedListener() {
					@Override
					public void colorChanged(int color) {
						presenter.setTopBarColor(color);
					}
				};
				tool = new PipetteTool(contextActivityWrapper, activity, listener, toolType);
				break;
			case FILL:
				tool = new FillTool(contextActivityWrapper, activity, toolType);
				break;
			case TRANSFORM:
				tool = new TransformTool(contextActivityWrapper, activity, toolType);
				break;
			case SHAPE:
				tool = new GeometricFillTool(contextActivityWrapper, activity, toolType);
				break;
			case ERASER:
				tool = new EraserTool(contextActivityWrapper, activity, toolType);
				break;
			case LINE:
				tool = new LineTool(contextActivityWrapper, activity, toolType);
				break;
			case TEXT:
				tool = new TextTool(contextActivityWrapper, activity, toolType);
				break;
			default:
				tool = new DrawTool(contextActivityWrapper, activity, ToolType.BRUSH);
				break;
		}
		tool.setupToolOptions();
		return tool;
	}
}
