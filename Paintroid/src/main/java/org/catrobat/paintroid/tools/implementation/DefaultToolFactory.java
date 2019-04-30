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
import android.view.ViewGroup;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.BrushToolOptions;
import org.catrobat.paintroid.tools.options.FillToolOptions;
import org.catrobat.paintroid.tools.options.TextToolOptions;
import org.catrobat.paintroid.tools.options.ToolOptionsController;
import org.catrobat.paintroid.tools.options.TransformToolOptions;
import org.catrobat.paintroid.ui.tools.DefaultBrushToolOptions;
import org.catrobat.paintroid.ui.tools.DefaultFillToolOptions;
import org.catrobat.paintroid.ui.tools.DefaultTextToolOptions;
import org.catrobat.paintroid.ui.tools.DefaultTransformToolOptions;

public class DefaultToolFactory implements ToolFactory {

	@Override
	public Tool createTool(ToolType toolType, ToolOptionsController toolOptionsController, Activity activity, CommandManager commandManager, Workspace workspace, ToolPaint toolPaint) {
		Tool tool;

		ContextCallback contextCallback = new DefaultContextCallback(activity.getApplicationContext());
		toolOptionsController.removeToolViews();
		toolOptionsController.setToolName(toolType.getNameResource());
		ViewGroup toolSpecificOptionsLayout = toolOptionsController.getToolSpecificOptionsLayout();

		switch (toolType) {
			case BRUSH:
				tool = new BrushTool(createBrushToolOptions(toolSpecificOptionsLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case CURSOR:
				tool = new CursorTool(createBrushToolOptions(toolSpecificOptionsLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case STAMP:
				tool = new StampTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case IMPORTPNG:
				tool = new ImportTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
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
				tool = new PipetteTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager, listener);
				break;
			case FILL:
				tool = new FillTool(createFillToolOptions(toolSpecificOptionsLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case TRANSFORM:
				tool = new TransformTool(createTransformToolOptions(toolSpecificOptionsLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case SHAPE:
				tool = new ShapeTool(contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case ERASER:
				tool = new EraserTool(createBrushToolOptions(toolSpecificOptionsLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case LINE:
				tool = new LineTool(createBrushToolOptions(toolSpecificOptionsLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			case TEXT:
				tool = new TextTool(createTextToolOptions(toolSpecificOptionsLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
			default:
				tool = new BrushTool(createBrushToolOptions(toolSpecificOptionsLayout), contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
				break;
		}
		tool.setupToolOptions();
		toolOptionsController.resetToOrigin();
		return tool;
	}

	private FillToolOptions createFillToolOptions(ViewGroup toolSpecificOptionsLayout) {
		return new DefaultFillToolOptions(toolSpecificOptionsLayout);
	}

	private TextToolOptions createTextToolOptions(ViewGroup toolSpecificOptionsLayout) {
		return new DefaultTextToolOptions(toolSpecificOptionsLayout);
	}

	private BrushToolOptions createBrushToolOptions(ViewGroup toolSpecificOptionsLayout) {
		return new DefaultBrushToolOptions(toolSpecificOptionsLayout);
	}

	private TransformToolOptions createTransformToolOptions(ViewGroup toolSpecificOptionsLayout) {
		return new DefaultTransformToolOptions(toolSpecificOptionsLayout);
	}
}
