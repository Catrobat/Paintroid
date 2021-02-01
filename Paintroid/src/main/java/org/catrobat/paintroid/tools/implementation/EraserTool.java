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

import android.graphics.Color;
import android.graphics.Paint;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.BrushToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

public class EraserTool extends BrushTool {
	private Paint previewPaint = new Paint();
	private Paint bitmapPaint = new Paint();

	public EraserTool(BrushToolOptionsView brushToolOptionsView, ContextCallback contextCallback,
			ToolOptionsVisibilityController toolOptionsViewController, ToolPaint toolPaint, Workspace workspace,
			CommandManager commandManager) {
		super(brushToolOptionsView, contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
	}

	@Override
	protected Paint getPreviewPaint() {
		previewPaint.set(super.getPreviewPaint());
		previewPaint.setColor(Color.BLACK);
		previewPaint.setShader(toolPaint.getCheckeredShader());
		return previewPaint;
	}

	@Override
	protected Paint getBitmapPaint() {
		bitmapPaint.set(super.getBitmapPaint());
		bitmapPaint.setXfermode(toolPaint.getEraseXfermode());
		bitmapPaint.setAlpha(0);
		return bitmapPaint;
	}

	@Override
	public ToolType getToolType() {
		return ToolType.ERASER;
	}
}
