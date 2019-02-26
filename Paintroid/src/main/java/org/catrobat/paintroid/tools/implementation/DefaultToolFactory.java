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

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsControllerContract;

public class DefaultToolFactory implements ToolFactory {
	private final MainActivity activity;

	public DefaultToolFactory(MainActivity mainActivity) {
		this.activity = mainActivity;
	}

	@Override
	public Tool createTool(ToolType toolType, ToolOptionsControllerContract toolOptionsController, CommandManager commandManager, Workspace workspace, ToolPaint toolPaint) {
		Tool tool;

		toolOptionsController.removeToolViews();
		toolOptionsController.setToolName(toolType.getNameResource());

		ContextCallback contextCallback = new DefaultContextCallback(activity.getApplicationContext());
		CommandFactory commandFactory = new DefaultCommandFactory();

		switch (toolType) {
			case CURSOR:
				tool = new CursorTool(toolOptionsController.createBrushPickerView(), contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
				break;
			case STAMP:
				tool = new StampTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
				break;
			case IMPORTPNG:
				tool = new ImportTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
				break;
			case PIPETTE:
				final MainActivityContracts.Presenter presenter = activity.getPresenter();
				ColorPickerDialog.OnColorPickedListener listener = new ColorPickerDialog.OnColorPickedListener() {
					@Override
					public void colorChanged(int color) {
						presenter.setTopBarColor(color);
					}
				};
				tool = new PipetteTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager, listener, commandFactory);
				break;
			case FILL:
				tool = new FillTool(toolOptionsController.createFillToolOptions(), contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
				break;
			case TRANSFORM:
				tool = new TransformTool(toolOptionsController.createTransformToolOptions(), contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
				break;
			case SHAPE:
				tool = new ShapeTool(toolOptionsController.createShapeToolOptions(), contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
				break;
			case ERASER:
				tool = new EraserTool(toolOptionsController.createBrushPickerView(), toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
				break;
			case LINE:
				tool = new LineTool(toolOptionsController.createBrushPickerView(), contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
				break;
			case TEXT:
				tool = new TextTool(toolOptionsController.createTextToolOptions(), contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
				break;
			case BRUSH:
			default:
				tool = new BrushTool(toolOptionsController.createBrushPickerView(), toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
				break;
		}

		if (toolType.showOptionsInitially()) {
			toolOptionsController.showAnimated();
		} else {
			toolOptionsController.hide();
		}

		return tool;
	}
}
