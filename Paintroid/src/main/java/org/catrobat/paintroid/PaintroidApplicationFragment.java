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

package org.catrobat.paintroid;

import android.os.Bundle;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.ui.Perspective;

import androidx.fragment.app.Fragment;

public class PaintroidApplicationFragment extends Fragment {
	private CommandManager commandManager;
	private ToolReference currentTool;
	private Perspective perspective;
	private LayerContracts.Model layerModel;
	private ToolPaint toolPaint;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setRetainInstance(true);
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public void setCommandManager(CommandManager commandManager) {
		this.commandManager = commandManager;
	}

	public ToolReference getCurrentTool() {
		return currentTool;
	}

	public void setCurrentTool(ToolReference currentTool) {
		this.currentTool = currentTool;
	}

	public Perspective getPerspective() {
		return perspective;
	}

	public void setPerspective(Perspective perspective) {
		this.perspective = perspective;
	}

	public LayerContracts.Model getLayerModel() {
		return layerModel;
	}

	public void setLayerModel(LayerContracts.Model layerModel) {
		this.layerModel = layerModel;
	}

	public ToolPaint getToolPaint() {
		return toolPaint;
	}

	public void setToolPaint(ToolPaint toolPaint) {
		this.toolPaint = toolPaint;
	}
}
