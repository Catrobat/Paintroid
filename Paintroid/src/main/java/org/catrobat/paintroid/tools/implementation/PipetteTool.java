/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.button.LayersAdapter;

public class PipetteTool extends BaseTool {

	public PipetteTool(Context context, ToolType toolType) {
		super(context, toolType);
	}

	@Override
	public void draw(Canvas canvas) {
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

	protected int blendComponent(int colorFG, int colorBG, int alphaFG, int alphaBG, int alphaOut) {
		return Math.round((colorFG * alphaFG +
				colorBG / 255f * alphaBG * (255f - alphaFG)) / alphaOut);
	}

	protected int blendColor(int colorFG, int colorBG) {
		int alphaFG = Color.alpha(colorFG);
		int alphaBG = Color.alpha(colorBG);
		int alpha = Math.round(alphaFG + alphaBG - alphaFG * alphaBG / 255f);
		if (alpha == Color.TRANSPARENT)
			return Color.TRANSPARENT;

		int r = blendComponent(Color.red(colorFG), Color.red(colorBG), alphaFG, alphaBG, alpha);
		int g = blendComponent(Color.green(colorFG), Color.green(colorBG), alphaFG, alphaBG, alpha);
		int b = blendComponent(Color.blue(colorFG), Color.blue(colorBG), alphaFG, alphaBG, alpha);
		return Color.argb(alpha, r, g, b);
	}

	protected boolean setColor(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}

		LayersAdapter adapter = LayerListener.getInstance().getAdapter();
		int color = Color.TRANSPARENT;
		for (int i = adapter.getCount() - 1; i >= 0; i--) {
			Layer layer = adapter.getLayer(i);
			int newColor = layer.getImage().getPixel((int) coordinate.x, (int) coordinate.y);
			color = blendColor(newColor, color);
		}

		ColorPickerDialog.getInstance().setInitialColor(color);
		changePaintColor(color);
		return true;
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void setupToolOptions() {
	}

}
