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

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;

public class DefaultToolFactory implements ToolFactory {

	@Override
	public Tool createTool(ToolType toolType, Activity activity, CommandManager commandManager, Workspace workspace, ToolPaint toolPaint) {
		Tool tool;

		switch (toolType) {
			case BRUSH:
				tool = new DrawTool(activity, toolPaint, workspace, commandManager);
				break;
			case CURSOR:
				tool = new CursorTool(activity, toolPaint, workspace, commandManager);
				break;
			case STAMP:
				tool = new StampTool(activity, toolPaint, workspace, commandManager);
				break;
			case IMPORTPNG:
				tool = new ImportTool(activity, toolPaint, workspace, commandManager);
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
				tool = new PipetteTool(activity, toolPaint, workspace, commandManager, listener);
				break;
			case FILL:
				tool = new FillTool(activity, toolPaint, workspace, commandManager);
				break;
			case TRANSFORM:
				tool = new TransformTool(activity, toolPaint, workspace, commandManager);
				break;
			case SHAPE:
				tool = new GeometricFillTool(activity, toolPaint, workspace, commandManager);
				break;
			case ERASER:
				tool = new EraserTool(activity, toolPaint, workspace, commandManager);
				break;
			case LINE:
				tool = new LineTool(activity, toolPaint, workspace, commandManager);
				break;
			case TEXT:
				tool = new TextTool(activity, toolPaint, workspace, commandManager);
				break;
			default:
				tool = new DrawTool(activity, toolPaint, workspace, commandManager);
				break;
		}
		tool.setupToolOptions();
		return tool;
	}
}
