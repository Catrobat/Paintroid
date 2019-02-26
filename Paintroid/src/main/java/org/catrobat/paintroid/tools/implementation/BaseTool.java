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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsControllerContract;

public abstract class BaseTool implements Tool {
	protected int scrollTolerance;
	protected PointF movedDistance;
	protected PointF previousEventCoordinate;
	protected CommandFactory commandFactory;
	protected ToolOptionsControllerContract toolOptionsController;
	protected CommandManager commandManager;
	protected Workspace workspace;
	protected ToolPaint toolPaint;
	protected ContextCallback contextCallback;

	public BaseTool(ContextCallback contextCallback, ToolOptionsControllerContract toolOptionsController, ToolPaint toolPaint, Workspace workspace, CommandManager commandManager, CommandFactory commandFactory) {
		this.contextCallback = contextCallback;
		this.toolOptionsController = toolOptionsController;
		this.toolPaint = toolPaint;
		this.workspace = workspace;
		this.commandManager = commandManager;
		this.commandFactory = commandFactory;

		scrollTolerance = toolOptionsController.getScrollTolerance();

		movedDistance = new PointF(0f, 0f);
		previousEventCoordinate = new PointF(0f, 0f);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle bundle) {
	}

	@Override
	public void onRestoreInstanceState(@NonNull Bundle bundle) {
	}

	@Override
	public void changePaintColor(@ColorInt int color) {
		toolPaint.setColor(color);
	}

	@Override
	public void changePaintStrokeWidth(int strokeWidth) {
		toolPaint.setStrokeWidth(strokeWidth);
	}

	@Override
	public void changePaintStrokeCap(Cap cap) {
		toolPaint.setStrokeCap(cap);
	}

	@Override
	public Paint getDrawPaint() {
		return new Paint(toolPaint.getPaint());
	}

	@Override
	public void setDrawPaint(Paint paint) {
		toolPaint.setPaint(paint);
	}

	@Override
	public abstract void draw(Canvas canvas);

	protected abstract void resetInternalState();

	@Override
	public void resetInternalState(StateChange stateChange) {
		if (getToolType().shouldReactToStateChange(stateChange)) {
			resetInternalState();
		}
	}

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY, int viewWidth, int viewHeight) {
		int deltaX = 0;
		int deltaY = 0;

		if (pointX < scrollTolerance) {
			deltaX = 1;
		}
		if (pointX > viewWidth - scrollTolerance) {
			deltaX = -1;
		}

		if (pointY < scrollTolerance) {
			deltaY = 1;
		}

		if (pointY > viewHeight - scrollTolerance) {
			deltaY = -1;
		}

		return new Point(deltaX, deltaY);
	}
}
