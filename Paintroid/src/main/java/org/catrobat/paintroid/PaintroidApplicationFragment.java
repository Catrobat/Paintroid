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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;

import java.io.File;

public class PaintroidApplicationFragment extends Fragment {
	private DrawingSurface drawingSurface;
	private CommandManager commandManager;
	private Tool currentTool;
	private Perspective perspective;
	private LayerContracts.Model layerModel;
	private File cacheDir;
	private Bitmap checkeredBackgroundBitmap;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setRetainInstance(true);
	}

	public DrawingSurface getDrawingSurface() {
		return drawingSurface;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public Tool getCurrentTool() {
		return currentTool;
	}

	public Perspective getPerspective() {
		return perspective;
	}

	public LayerContracts.Model getLayerModel() {
		return layerModel;
	}

	public File getCacheDir() {
		return cacheDir;
	}

	public Bitmap getCheckeredBackgroundBitmap() {
		return checkeredBackgroundBitmap;
	}

	public void setDrawingSurface(DrawingSurface drawingSurface) {
		this.drawingSurface = drawingSurface;
		PaintroidApplication.drawingSurface = this.drawingSurface;
	}

	public void setCommandManager(CommandManager commandManager) {
		this.commandManager = commandManager;
		PaintroidApplication.commandManager = this.commandManager;
	}

	public void setCurrentTool(Tool currentTool) {
		this.currentTool = currentTool;
		PaintroidApplication.currentTool = this.currentTool;
	}

	public void setPerspective(Perspective perspective) {
		this.perspective = perspective;
		PaintroidApplication.perspective = this.perspective;
	}

	public void setLayerModel(LayerContracts.Model layerModel) {
		this.layerModel = layerModel;
		PaintroidApplication.layerModel = this.layerModel;
	}

	public void setCacheDir(File cacheDir) {
		this.cacheDir = cacheDir;
		PaintroidApplication.cacheDir = this.cacheDir;
	}

	public void setCheckeredBackgroundBitmap(Bitmap checkeredBackgroundBitmap) {
		this.checkeredBackgroundBitmap = checkeredBackgroundBitmap;
		PaintroidApplication.checkeredBackgroundBitmap = this.checkeredBackgroundBitmap;
	}
}
