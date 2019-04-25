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

package org.catrobat.paintroid.controller;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;

import org.catrobat.paintroid.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.ImportTool;
import org.catrobat.paintroid.tools.options.ToolOptionsController;

import static org.catrobat.paintroid.tools.Tool.StateChange.NEW_IMAGE_LOADED;
import static org.catrobat.paintroid.tools.Tool.StateChange.RESET_INTERNAL_STATE;

public class DefaultToolController implements ToolController {
	private ToolReference toolReference;
	private ToolOptionsController toolOptionsController;
	private ToolFactory toolFactory;
	private CommandManager commandManager;
	private Workspace workspace;
	private ToolPaint toolPaint;
	private ContextCallback contextCallback;
	private ColorPickerDialog.OnColorPickedListener onColorPickedListener;

	public DefaultToolController(ToolReference toolReference, ToolOptionsController toolOptionsController,
			ToolFactory toolFactory, CommandManager commandManager, Workspace workspace, ToolPaint toolPaint,
			ContextCallback contextCallback) {
		this.toolReference = toolReference;
		this.toolOptionsController = toolOptionsController;
		this.toolFactory = toolFactory;
		this.commandManager = commandManager;
		this.workspace = workspace;
		this.toolPaint = toolPaint;
		this.contextCallback = contextCallback;
	}

	@Override
	public void setOnColorPickedListener(ColorPickerDialog.OnColorPickedListener onColorPickedListener) {
		this.onColorPickedListener = onColorPickedListener;
	}

	@Override
	public void switchTool(ToolType toolType) {
		Tool tool = toolFactory.createTool(toolType, toolOptionsController, commandManager, workspace, toolPaint, contextCallback, onColorPickedListener);
		switchTool(tool);
	}

	@Override
	public boolean isDefaultTool() {
		return toolReference.get().getToolType() == ToolType.BRUSH;
	}

	@Override
	public void hideToolOptions() {
		toolOptionsController.hideAnimated();
	}

	@Override
	public boolean toolOptionsVisible() {
		return toolOptionsController.isVisible();
	}

	@Override
	public void resetToolInternalState() {
		toolReference.get().resetInternalState(RESET_INTERNAL_STATE);
	}

	@Override
	public void resetToolInternalStateOnImageLoaded() {
		toolReference.get().resetInternalState(NEW_IMAGE_LOADED);
	}

	@Override
	public int getToolColor() {
		return toolReference.get().getDrawPaint().getColor();
	}

	private void switchTool(Tool tool) {
		Bundle toolBundle = new Bundle();
		Tool currentTool = toolReference.get();
		Paint tempPaint = currentTool.getDrawPaint();

		currentTool.leaveTool();
		if (currentTool.getToolType() == tool.getToolType()) {
			currentTool.onSaveInstanceState(toolBundle);
			toolReference.set(tool);
			tool.onRestoreInstanceState(toolBundle);
		} else {
			toolBundle.clear();
			toolReference.set(tool);
		}
		tool.startTool();
		tool.setDrawPaint(tempPaint);
	}

	@Override
	public ToolType getToolType() {
		return toolReference.get().getToolType();
	}

	@Override
	public void disableToolOptions() {
		toolOptionsController.disable();
	}

	@Override
	public void enableToolOptions() {
		toolOptionsController.enable();
	}

	@Override
	public void createTool() {
		Bundle bundle = new Bundle();

		if (toolReference.get() == null) {
			toolReference.set(toolFactory.createTool(ToolType.BRUSH,
					toolOptionsController, commandManager, workspace, toolPaint, contextCallback, onColorPickedListener));
			toolReference.get().startTool();
		} else {
			Paint paint = toolReference.get().getDrawPaint();
			toolReference.get().leaveTool();
			toolReference.get().onSaveInstanceState(bundle);
			toolReference.set(toolFactory.createTool(toolReference.get().getToolType(),
					toolOptionsController, commandManager, workspace, toolPaint, contextCallback, onColorPickedListener));
			toolReference.get().onRestoreInstanceState(bundle);
			toolReference.get().startTool();
			toolReference.get().setDrawPaint(paint);
		}
	}

	@Override
	public void toggleToolOptions() {
		if (toolOptionsController.isVisible()) {
			toolOptionsController.hideAnimated();
		} else {
			toolOptionsController.showAnimated();
		}
	}

	@Override
	public boolean hasToolOptions() {
		return getToolType().hasOptions();
	}

	@Override
	public void setBitmapFromFile(Bitmap bitmap) {
		ImportTool importTool = (ImportTool) toolReference.get();
		importTool.setBitmapFromFile(bitmap);
	}
}
