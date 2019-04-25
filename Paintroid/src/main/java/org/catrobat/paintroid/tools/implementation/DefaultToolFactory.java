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

import android.view.ViewGroup;

import org.catrobat.paintroid.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.BrushToolOptions;
import org.catrobat.paintroid.tools.options.FillToolOptions;
import org.catrobat.paintroid.tools.options.ShapeToolOptions;
import org.catrobat.paintroid.tools.options.TextToolOptions;
import org.catrobat.paintroid.tools.options.ToolOptionsController;
import org.catrobat.paintroid.tools.options.TransformToolOptions;
import org.catrobat.paintroid.ui.tools.DefaultBrushToolOptions;
import org.catrobat.paintroid.ui.tools.DefaultFillToolOptions;
import org.catrobat.paintroid.ui.tools.DefaultShapeToolOptions;
import org.catrobat.paintroid.ui.tools.DefaultTextToolOptions;
import org.catrobat.paintroid.ui.tools.DefaultTransformToolOptions;

public class DefaultToolFactory implements ToolFactory {

	@Override
	public Tool createTool(ToolType toolType, ToolOptionsController toolOptionsController, CommandManager commandManager, Workspace workspace, ToolPaint toolPaint, ContextCallback contextCallback, ColorPickerDialog.OnColorPickedListener onColorPickedListener) {
		Tool tool;

		toolOptionsController.removeToolViews();
		toolOptionsController.setToolName(toolType.getNameResource());
		ViewGroup toolLayout = toolOptionsController.getToolSpecificOptionsLayout();

		switch (toolType) {
			case BRUSH:
				tool = new BrushTool(createBrushToolOptions(toolLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case CURSOR:
				tool = new CursorTool(createBrushToolOptions(toolLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case STAMP:
				tool = new StampTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case IMPORTPNG:
				tool = new ImportTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case PIPETTE:
				tool = new PipetteTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager, onColorPickedListener);
				break;
			case FILL:
				tool = new FillTool(createFillToolOptions(toolLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case TRANSFORM:
				tool = new TransformTool(createTransformToolOptions(toolLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case SHAPE:
				tool = new ShapeTool(createShapeToolOptions(toolLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case ERASER:
				tool = new EraserTool(createBrushToolOptions(toolLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case LINE:
				tool = new LineTool(createBrushToolOptions(toolLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case TEXT:
				tool = new TextTool(createTextToolOptions(toolLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case HAND:
				tool = new HandTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			default:
				tool = new BrushTool(createBrushToolOptions(toolLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
		}
		tool.setupToolOptions();
		toolOptionsController.resetToOrigin();
		return tool;
	}

	private BrushToolOptions createBrushToolOptions(ViewGroup toolSpecificOptionsLayout) {
		return new DefaultBrushToolOptions(toolSpecificOptionsLayout);
	}

	private FillToolOptions createFillToolOptions(ViewGroup toolSpecificOptionsLayout) {
		return new DefaultFillToolOptions(toolSpecificOptionsLayout);
	}

	private ShapeToolOptions createShapeToolOptions(ViewGroup toolSpecificOptionsLayout) {
		return new DefaultShapeToolOptions(toolSpecificOptionsLayout);
	}

	private TextToolOptions createTextToolOptions(ViewGroup toolSpecificOptionsLayout) {
		return new DefaultTextToolOptions(toolSpecificOptionsLayout);
	}

	private TransformToolOptions createTransformToolOptions(ViewGroup toolSpecificOptionsLayout) {
		return new DefaultTransformToolOptions(toolSpecificOptionsLayout);
	}
}
