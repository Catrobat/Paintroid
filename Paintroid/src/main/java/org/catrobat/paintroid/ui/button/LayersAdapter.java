/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.button;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.eventlistener.OnLayerEventListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.ui.DrawingSurface;

import java.util.ArrayList;
import java.util.Collections;

public class LayersAdapter extends BaseAdapter implements OnLayerEventListener {
	public static final int MAX_LAYER = 4;

	private ArrayList<Layer> layerList;
	private int layerCounter = 0;

	public LayersAdapter(Bitmap firstLayer) {
		initLayers(firstLayer);
	}

	private void initLayers(Bitmap firstLayer) {
		layerList = new ArrayList<>();
		layerList.add(new Layer(0, firstLayer));
		layerCounter++;
	}

	@Override
	public int getCount() {
		return layerList.size();
	}

	@Override
	public Object getItem(int position) {
		return layerList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public Layer getLayer(int position) {
		return layerList.get(position);
	}

	public int getPosition(int layerID) {
		int i;
		for (i = 0; i < layerList.size(); i++) {
			if (layerList.get(i).getLayerID() == layerID) {
				break;
			}
		}
		return i;
	}

	public ArrayList<Layer> getLayers() {
		return layerList;
	}

	public boolean addLayer() {
		if (layerList.size() < MAX_LAYER) {
			DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
			Bitmap image = Bitmap.createBitmap(drawingSurface.getBitmapWidth(),
					drawingSurface.getBitmapHeight(), Bitmap.Config.ARGB_8888);
			layerList.add(0, new Layer(layerCounter, image));

			layerCounter++;
			notifyDataSetChanged();
			return true;
		}

		return false;
	}

	public boolean addLayer(Layer existingLayer) {
		if (layerList.size() < MAX_LAYER) {
			layerList.add(0, existingLayer);
			return true;
		}

		return false;
	}

	public void removeLayer(Layer layer) {
		if (layerList.size() > 0) {
			layerList.remove(layer);
		}
	}

	public Layer mergeLayer(Layer firstLayer, Layer secondLayer) {
		Bitmap mergedBitmap = null;

		if (getPosition(firstLayer.getLayerID()) > getPosition(secondLayer.getLayerID())) {
			mergedBitmap = mergeBitmaps(firstLayer, secondLayer);
		} else {
			mergedBitmap = mergeBitmaps(secondLayer, firstLayer);
		}

		removeLayer(firstLayer);
		removeLayer(secondLayer);

		Layer layer = new Layer(layerCounter++, mergedBitmap);
		layer.setOpacity(100);
		addLayer(layer);

		return layer;
	}

	private Bitmap mergeBitmaps(Layer firstLayer, Layer secondLayer) {

		Bitmap firstBitmap = firstLayer.getImage();
		Bitmap secondBitmap = secondLayer.getImage();

		Bitmap bmpOverlay = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(), firstBitmap.getConfig());
		Canvas canvas = new Canvas(bmpOverlay);

		Paint overlayPaint = new Paint();
		overlayPaint.setAlpha(firstLayer.getScaledOpacity());

		canvas.drawBitmap(firstBitmap, new Matrix(), overlayPaint);
		overlayPaint.setAlpha(secondLayer.getScaledOpacity());
		canvas.drawBitmap(secondBitmap, 0, 0, overlayPaint);

		return bmpOverlay;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			Context context = parent.getContext();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.layer_button, parent, false);
			LinearLayout layerButton = (LinearLayout) convertView.findViewById(R.id.layer_button);

			if (layerList.get(position).getSelected()) {
				layerButton.setBackgroundColor(
						ContextCompat.getColor(context, R.color.color_chooser_blue1));
			} else {
				layerButton.setBackgroundColor(
						ContextCompat.getColor(context, R.color.custom_background_color));
			}
			ImageView imageView = (ImageView) convertView.findViewById(R.id.layer_button_image);
			imageView.setImageBitmap(layerList.get(position).getImage());
		}
		return convertView;
	}

	public Layer clearLayer() {
		if (layerList.size() >= 1) {
			for (int i = layerList.size() - 1; i >= 0; i--) {
				layerList.remove(i);
			}
		}
		layerCounter = 0;
		addLayer();
		return layerList.get(0);
	}

	public void copy(int currentLayer) {

		if (layerList.size() < MAX_LAYER) {
			Bitmap image = layerList.get(getPosition(currentLayer)).getImage().copy(layerList.get(currentLayer).getImage().getConfig(), true);
			layerList.add(0, new Layer(layerCounter, image));
			layerCounter++;
			notifyDataSetChanged();
		}
	}

	public void swapLayer(int posMarkedLayer, int targetPosition) {
		if (posMarkedLayer >= 0 && posMarkedLayer < layerList.size()
				&& targetPosition >= 0 && targetPosition < layerList.size()) {
			if (posMarkedLayer < targetPosition) {
				for (int i = posMarkedLayer; i < targetPosition; i++) {
					Collections.swap(layerList, i, i + 1);
				}
			} else if (posMarkedLayer > targetPosition) {
				for (int i = posMarkedLayer; i > targetPosition; i--) {
					Collections.swap(layerList, i, i - 1);
				}
			}
		}
	}

	@Override
	public void onLayerAdded(Layer layer) {
		addLayer(layer);
	}

	@Override
	public void onLayerRemoved(Layer layer) {
		removeLayer(layer);
	}

	@Override
	public void onLayerMoved(int startPos, int targetPos) {
		swapLayer(startPos, targetPos);
	}

	public Bitmap getBitmapToSave() {
		Bitmap firstBitmap = layerList.get(layerList.size() - 1).getImage();
		Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(), firstBitmap.getConfig());
		Canvas canvas = new Canvas(bitmap);
		Paint overlayPaint = new Paint();
		overlayPaint.setAlpha(layerList.get(layerList.size() - 1).getScaledOpacity());
		canvas.drawBitmap(firstBitmap, new Matrix(), overlayPaint);

		if (layerList.size() > 1) {
			for (int i = layerList.size() - 2; i >= 0; i--) {
				overlayPaint.setAlpha(layerList.get(i).getScaledOpacity());
				canvas.drawBitmap(layerList.get(i).getImage(), 0, 0, overlayPaint);
			}
		}

		return bitmap;
	}

	public boolean checkAllLayerVisible() {

		for (Layer layer : layerList) {
			if (layer.getVisible()) {
				return false;
			}
		}

		return true;
	}

	public int getLayerCounter() {
		return layerCounter;
	}
}
