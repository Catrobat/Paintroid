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
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;

import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.ui.Perspective;

public class DefaultWorkspace implements Workspace {

	private final LayerContracts.Model layerModel;
	private final Perspective perspective;
	private final Listener listener;

	public DefaultWorkspace(LayerContracts.Model layerModel, Perspective perspective, Listener listener) {
		this.layerModel = layerModel;
		this.perspective = perspective;
		this.listener = listener;
	}

	public Perspective getPerspective() {
		return perspective;
	}

	@Override
	public boolean contains(PointF point) {
		return point.x < getWidth() && point.x >= 0 && point.y < getHeight() && point.y >= 0;
	}

	@Override
	public boolean intersectsWith(RectF rectangle) {
		return 0 < rectangle.right
				&& rectangle.left < getWidth()
				&& 0 < rectangle.bottom
				&& rectangle.top < getHeight();
	}

	@Override
	public int getHeight() {
		return layerModel.getHeight();
	}

	@Override
	public int getWidth() {
		return layerModel.getWidth();
	}

	@Override
	public int getSurfaceWidth() {
		return perspective.getSurfaceWidth();
	}

	@Override
	public int getSurfaceHeight() {
		return perspective.getSurfaceHeight();
	}

	@Override
	public Bitmap getBitmapOfAllLayers() {
		return LayerModel.getBitmapOfAllLayersToSave(layerModel.getLayers());
	}

	@Override
	public Bitmap getBitmapOfCurrentLayer() {
		return Bitmap.createBitmap(layerModel.getCurrentLayer().getBitmap());
	}

	@Override
	public int getCurrentLayerIndex() {
		return layerModel.getLayerIndexOf(layerModel.getCurrentLayer());
	}

	@Override
	public void resetPerspective() {
		perspective.setBitmapDimensions(getWidth(), getHeight());
		perspective.resetScaleAndTranslation();
	}

	@Override
	public PointF getCanvasPointFromSurfacePoint(PointF surfacePoint) {
		return perspective.getCanvasPointFromSurfacePoint(surfacePoint);
	}

	@Override
	public void invalidate() {
		listener.invalidate();
	}

	@Override
	public int getPixelOfCurrentLayer(PointF coordinate) {
		if (coordinate.x >= 0 && coordinate.y >= 0 && coordinate.x < getWidth() && coordinate.y < getHeight()) {
			Bitmap bitmap = layerModel.getCurrentLayer().getBitmap();
			return bitmap.getPixel((int) coordinate.x, (int) coordinate.y);
		}
		return Color.TRANSPARENT;
	}

	@Override
	public void setScale(float zoomFactor) {
		perspective.setScale(zoomFactor);
	}

	@Override
	public float getScaleForCenterBitmap() {
		return perspective.getScaleForCenterBitmap();
	}

	@Override
	public float getScale() {
		return perspective.getScale();
	}

	@Override
	public PointF getSurfacePointFromCanvasPoint(PointF canvasPoint) {
		return perspective.getSurfacePointFromCanvasPoint(canvasPoint);
	}

	public interface Listener {
		void invalidate();
	}
}
