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
import android.os.Bundle;

import org.catrobat.paintroid.colorpicker.OnColorPickedListener;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.ImportTool;
import org.catrobat.paintroid.tools.options.ToolOptionsViewController;

import static org.catrobat.paintroid.tools.Tool.StateChange.NEW_IMAGE_LOADED;
import static org.catrobat.paintroid.tools.Tool.StateChange.RESET_INTERNAL_STATE;

public class DefaultToolController implements ToolController {
	private ToolReference toolReference;
	private ToolOptionsViewController toolOptionsViewController;
	private ToolFactory toolFactory;
	private CommandManager commandManager;
	private Workspace workspace;
	private ToolPaint toolPaint;
	private ContextCallback contextCallback;
	private OnColorPickedListener onColorPickedListener;

	public DefaultToolController(ToolReference toolReference, ToolOptionsViewController toolOptionsViewController,
				ToolFactory toolFactory, CommandManager commandManager, Workspace workspace, ToolPaint toolPaint,
				ContextCallback contextCallback) {
		this.toolReference = toolReference;
		this.toolOptionsViewController = toolOptionsViewController;
		this.toolFactory = toolFactory;
		this.commandManager = commandManager;
		this.workspace = workspace;
		this.toolPaint = toolPaint;
		this.contextCallback = contextCallback;
	}

	@Override
	public void setOnColorPickedListener(OnColorPickedListener onColorPickedListener) {
		this.onColorPickedListener = onColorPickedListener;
	}

	@Override
	public void switchTool(ToolType toolType, boolean backPressed) {
		switchTool(createAndSetupTool(toolType), backPressed);
	}

	@Override
	public boolean isDefaultTool() {
		return toolReference.getTool().getToolType() == ToolType.BRUSH;
	}

	@Override
	public void hideToolOptionsView() {
		toolOptionsViewController.hide();
	}

	@Override
	public void showToolOptionsView() {
		toolOptionsViewController.show();
	}

	@Override
	public boolean toolOptionsViewVisible() {
		return toolOptionsViewController.isVisible();
	}

	@Override
	public void resetToolInternalState() {
		toolReference.getTool().resetInternalState(RESET_INTERNAL_STATE);
	}

	@Override
	public void resetToolInternalStateOnImageLoaded() {
		toolReference.getTool().resetInternalState(NEW_IMAGE_LOADED);
	}

	@Override
	public int getToolColor() {
		return toolReference.getTool().getDrawPaint().getColor();
	}

	@Override
	public Tool getCurrentTool() {
		return toolReference.getTool();
	}

	private void switchTool(Tool tool, boolean backPressed) {
		Tool currentTool = toolReference.getTool();
		ToolType currentToolType = currentTool.getToolType();

		if ((currentToolType == ToolType.TEXT || currentToolType == ToolType.TRANSFORM
				|| currentToolType == ToolType.IMPORTPNG || currentToolType == ToolType.SHAPE
				|| currentToolType == ToolType.LINE) && !backPressed) {
			BaseToolWithShape toolToApply = (BaseToolWithShape) currentTool;
			toolToApply.onClickOnButton();
		}

		if (currentTool.getToolType() == tool.getToolType()) {
			Bundle toolBundle = new Bundle();
			currentTool.onSaveInstanceState(toolBundle);
			tool.onRestoreInstanceState(toolBundle);
		}
		toolReference.setTool(tool);

		workspace.invalidate();
	}

	@Override
	public ToolType getToolType() {
		return toolReference.getTool().getToolType();
	}

	@Override
	public void disableToolOptionsView() {
		toolOptionsViewController.disable();
	}

	@Override
	public void enableToolOptionsView() {
		toolOptionsViewController.enable();
	}

	@Override
	public void createTool() {
		if (toolReference.getTool() == null) {
			toolReference.setTool(createAndSetupTool(ToolType.BRUSH));
		} else {
			Bundle bundle = new Bundle();
			toolReference.getTool().onSaveInstanceState(bundle);
			toolReference.setTool(createAndSetupTool(toolReference.getTool().getToolType()));
			toolReference.getTool().onRestoreInstanceState(bundle);
		}
		workspace.invalidate();
	}

	private Tool createAndSetupTool(ToolType toolType) {
		Tool tool;

		if (!toolType.equals(ToolType.HAND)) {
			toolOptionsViewController.removeToolViews();
		}

		if (toolType == ToolType.TEXT
				|| toolType == ToolType.TRANSFORM
				|| toolType == ToolType.SHAPE
				|| toolType == ToolType.IMPORTPNG
				|| toolType == ToolType.LINE) {
			toolOptionsViewController.showCheckmark();
		} else {
			toolOptionsViewController.hideCheckmark();
		}

		tool = toolFactory.createTool(toolType, toolOptionsViewController, commandManager, workspace, toolPaint, contextCallback, onColorPickedListener);

		if (!toolType.equals(ToolType.HAND)) {
			toolOptionsViewController.resetToOrigin();
			toolOptionsViewController.show();
		}

		return tool;
	}

	@Override
	public void toggleToolOptionsView() {
		if (toolOptionsViewController.isVisible()) {
			toolOptionsViewController.hide();
		} else {
			toolOptionsViewController.show();
		}
	}

	@Override
	public boolean hasToolOptionsView() {
		return getToolType().hasOptions();
	}

	@Override
	public void setBitmapFromSource(Bitmap bitmap) {
		ImportTool importTool = (ImportTool) toolReference.getTool();
		importTool.setBitmapFromSource(bitmap);
	}
}
