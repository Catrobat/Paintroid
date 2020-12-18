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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import org.catrobat.paintroid.colorpicker.OnColorPickedListener;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

public class PipetteTool extends BaseTool {

	private Bitmap surfaceBitmap;
	private OnColorPickedListener listener;

	public PipetteTool(ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController,
			ToolPaint toolPaint, Workspace workspace, CommandManager commandManager, OnColorPickedListener listener) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
		this.listener = listener;

		updateSurfaceBitmap();
	}

	@Override
	public void draw(Canvas canvas) {
	}

	@Override
	public ToolType getToolType() {
		return ToolType.PIPETTE;
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		return setColor(coordinate);
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		return setColor(coordinate);
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		return setColor(coordinate);
	}

	protected boolean setColor(PointF coordinate) {
		if (coordinate == null || surfaceBitmap == null) {
			return false;
		}

		if (!workspace.contains(coordinate)) {
			return false;
		}

		int color = surfaceBitmap.getPixel((int) coordinate.x, (int) coordinate.y);

		listener.colorChanged(color);
		changePaintColor(color);
		return true;
	}

	public void updateSurfaceBitmap() {
		surfaceBitmap = workspace.getBitmapOfAllLayers();
	}

	@Override
	public void resetInternalState() {
		updateSurfaceBitmap();
	}
}
