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

import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.common.PointScrollBehavior;
import org.catrobat.paintroid.tools.common.ScrollBehavior;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

import androidx.annotation.ColorInt;

public abstract class BaseTool implements Tool {
	protected final PointF movedDistance;
	protected ScrollBehavior scrollBehavior;
	protected PointF previousEventCoordinate;
	protected CommandFactory commandFactory = new DefaultCommandFactory();
	protected CommandManager commandManager;
	protected Workspace workspace;
	protected ContextCallback contextCallback;
	protected ToolOptionsVisibilityController toolOptionsViewController;
	protected ToolPaint toolPaint;

	public BaseTool(ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController,
			ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		this.contextCallback = contextCallback;
		this.toolOptionsViewController = toolOptionsViewController;
		this.toolPaint = toolPaint;
		this.workspace = workspace;
		this.commandManager = commandManager;

		int scrollTolerance = contextCallback.getScrollTolerance();
		scrollBehavior = new PointScrollBehavior(scrollTolerance);

		movedDistance = new PointF(0f, 0f);
		previousEventCoordinate = new PointF(0f, 0f);
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
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
	public abstract void draw(Canvas canvas);

	protected void resetInternalState() {
	}

	@Override
	public void resetInternalState(StateChange stateChange) {
		if (getToolType().shouldReactToStateChange(stateChange)) {
			resetInternalState();
		}
	}

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY, int viewWidth, int viewHeight) {
		return scrollBehavior.getScrollDirection(pointX, pointY, viewWidth, viewHeight);
	}

	public boolean handToolMode() {
		return false;
	}
}
