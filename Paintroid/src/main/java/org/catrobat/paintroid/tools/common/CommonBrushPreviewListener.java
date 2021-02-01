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

package org.catrobat.paintroid.tools.common;

import android.graphics.Paint;

import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.options.BrushToolOptionsView;

public class CommonBrushPreviewListener implements BrushToolOptionsView.OnBrushPreviewListener {
	private ToolType toolType;
	private ToolPaint toolPaint;

	public CommonBrushPreviewListener(ToolPaint toolPaint, ToolType toolType) {
		this.toolType = toolType;
		this.toolPaint = toolPaint;
	}

	@Override
	public float getStrokeWidth() {
		return toolPaint.getStrokeWidth();
	}

	@Override
	public Paint.Cap getStrokeCap() {
		return toolPaint.getStrokeCap();
	}

	@Override
	public int getColor() {
		return toolPaint.getColor();
	}

	@Override
	public ToolType getToolType() {
		return toolType;
	}
}
