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

import org.catrobat.paintroid.colorpicker.OnColorPickedListener;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.BrushToolOptionsView;
import org.catrobat.paintroid.tools.options.FillToolOptionsView;
import org.catrobat.paintroid.tools.options.ShapeToolOptionsView;
import org.catrobat.paintroid.tools.options.StampToolOptionsView;
import org.catrobat.paintroid.tools.options.TextToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsViewController;
import org.catrobat.paintroid.tools.options.TransformToolOptionsView;
import org.catrobat.paintroid.ui.tools.DefaultBrushToolOptionsView;
import org.catrobat.paintroid.ui.tools.DefaultFillToolOptionsView;
import org.catrobat.paintroid.ui.tools.DefaultShapeToolOptionsView;
import org.catrobat.paintroid.ui.tools.DefaultStampToolOptionsView;
import org.catrobat.paintroid.ui.tools.DefaultTextToolOptionsView;
import org.catrobat.paintroid.ui.tools.DefaultTransformToolOptionsView;

public class DefaultToolFactory implements ToolFactory {

	@Override
	public Tool createTool(ToolType toolType, ToolOptionsViewController toolOptionsViewController, CommandManager commandManager, Workspace workspace, ToolPaint toolPaint, ContextCallback contextCallback, OnColorPickedListener onColorPickedListener) {
		ViewGroup toolLayout = toolOptionsViewController.getToolSpecificOptionsLayout();

		switch (toolType) {
			case CURSOR:
				return new CursorTool(createBrushToolOptionsView(toolLayout), contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
			case STAMP:
				return new StampTool(createStampToolOptionsView(toolLayout), contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
			case IMPORTPNG:
				return new ImportTool(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
			case PIPETTE:
				return new PipetteTool(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager, onColorPickedListener);
			case FILL:
				return new FillTool(createFillToolOptionsView(toolLayout), contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
			case TRANSFORM:
				return new TransformTool(createTransformToolOptionsView(toolLayout), contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
			case SHAPE:
				return new ShapeTool(createShapeToolOptionsView(toolLayout), contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
			case ERASER:
				return new EraserTool(createBrushToolOptionsView(toolLayout), contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
			case LINE:
				return new LineTool(createBrushToolOptionsView(toolLayout), contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
			case TEXT:
				return new TextTool(createTextToolOptionsView(toolLayout), contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
			case HAND:
				return new HandTool(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
			default:
				return new BrushTool(createBrushToolOptionsView(toolLayout), contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
		}
	}

	private BrushToolOptionsView createBrushToolOptionsView(ViewGroup toolLayout) {
		return new DefaultBrushToolOptionsView(toolLayout);
	}

	private FillToolOptionsView createFillToolOptionsView(ViewGroup toolLayout) {
		return new DefaultFillToolOptionsView(toolLayout);
	}

	private ShapeToolOptionsView createShapeToolOptionsView(ViewGroup toolLayout) {
		return new DefaultShapeToolOptionsView(toolLayout);
	}

	private TextToolOptionsView createTextToolOptionsView(ViewGroup toolLayout) {
		return new DefaultTextToolOptionsView(toolLayout);
	}

	private TransformToolOptionsView createTransformToolOptionsView(ViewGroup toolLayout) {
		return new DefaultTransformToolOptionsView(toolLayout);
	}

	private StampToolOptionsView createStampToolOptionsView(ViewGroup toolLayout) {
		return new DefaultStampToolOptionsView(toolLayout);
	}
}
