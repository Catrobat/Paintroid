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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
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

	private Context mContext;
	private ArrayList<Layer> mLayerList;
	private int mLayerCounter = 0;
	private int mMaxLayer = 4;

	public LayersAdapter(Context context, boolean fromCatrobat, Bitmap first_layer) {
		this.mContext = context;
		initLayers(fromCatrobat, first_layer);
	}

	private void initLayers(boolean fromCatrobat, Bitmap first_layer) {

		mLayerList = new ArrayList<Layer>();
		mLayerList.add(new Layer(0, first_layer));
		mLayerCounter++;

	}

	@Override
	public int getCount() {
		return mLayerList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public Layer getLayer(int position) {
		return mLayerList.get(position);
	}

	public int getPosition(int layerID) {
		int i;
		for (i = 0; i < mLayerList.size(); i++) {
			if (mLayerList.get(i).getLayerID() == layerID)
				break;
		}
		return i;
	}

	public ArrayList<Layer> getLayers() {
		return mLayerList;
	}

	public boolean addLayer() {
		if (mLayerList.size() < mMaxLayer) {
			DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
			Bitmap image = Bitmap.createBitmap(drawingSurface.getBitmapWidth(),
					drawingSurface.getBitmapHeight(), Bitmap.Config.ARGB_8888);
			mLayerList.add(0, new Layer(mLayerCounter, image));

			mLayerCounter++;
			notifyDataSetChanged();
			return true;
		} else
			return false;
	}

	public boolean addLayer(Layer existingLayer) {
		if (mLayerList.size() < mMaxLayer) {
			mLayerList.add(0, existingLayer);
			notifyDataSetChanged();
			return true;
		} else
			return false;
	}

	public void removeLayer(Layer layer) {
		if (mLayerList.size() > 0) {
			mLayerList.remove(layer);
			notifyDataSetChanged();
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

		Layer layer = new Layer(mLayerCounter++, mergedBitmap);
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
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			rowView = inflater.inflate(R.layout.layer_button, null);
			LinearLayout linear_layout = (LinearLayout) rowView.findViewById(R.id.layer_button);

			if (mLayerList.get(position).getSelected()) {
				linear_layout.setBackgroundColor(
						mContext.getResources().getColor(R.color.color_chooser_blue1));
			} else {
				linear_layout.setBackgroundColor(
						mContext.getResources().getColor(R.color.custom_background_color));
			}
			ImageView imageView = (ImageView) rowView
					.findViewById(R.id.layer_button_image);
			imageView.setImageBitmap(mLayerList.get(position).getImage());
		}
		return rowView;
	}

	public Layer clearLayer() {
		if (mLayerList.size() >= 1) {
			for (int i = mLayerList.size() - 1; i >= 0; i--) {
				mLayerList.remove(i);
			}
		}
		mLayerCounter = 0;
		addLayer();
		return mLayerList.get(0);
	}

	public void copy(int currentLayer) {

		if (mLayerList.size() < mMaxLayer) {
			Bitmap image = mLayerList.get(getPosition(currentLayer)).getImage().copy(mLayerList.get(currentLayer).getImage().getConfig(), true);
			mLayerList.add(0, new Layer(mLayerCounter, image));
			mLayerCounter++;
			notifyDataSetChanged();
		}

	}

	public void swapUp(int IDcurrentLayer) {
		int PositionCurrentLayer = getPosition(IDcurrentLayer);
		if (PositionCurrentLayer > 0)
			Collections.swap(mLayerList, PositionCurrentLayer, PositionCurrentLayer - 1);

	}

	public void swapDown(int IDcurrentLayer) {
		int PositionCurrentLayer = getPosition(IDcurrentLayer);
		if (PositionCurrentLayer < mLayerList.size() - 1)
			Collections.swap(mLayerList, PositionCurrentLayer, PositionCurrentLayer + 1);
	}

	public void swapTop(int IDcurrentLayer) {
		int PositionCurrentLayer = getPosition(IDcurrentLayer);
		if (PositionCurrentLayer > 0)
			Collections.swap(mLayerList, PositionCurrentLayer, 0);
	}

	public void swapBottom(int IDcurrentLayer) {
		int PositionCurrentLayer = getPosition(IDcurrentLayer);
		if (PositionCurrentLayer < mLayerList.size() - 1)
			Collections.swap(mLayerList, PositionCurrentLayer, mLayerList.size() - 1);
	}

	public void swapLayer(int posMarkedLayer, int targetPosition) {
		if (posMarkedLayer >= 0 && posMarkedLayer < mLayerList.size() &&
				targetPosition >= 0 && targetPosition < mLayerList.size()) {
			if (posMarkedLayer < targetPosition) {
				for (int i = posMarkedLayer; i < targetPosition; i++)
					Collections.swap(mLayerList, i, i + 1);
			} else if (posMarkedLayer > targetPosition) {
				for (int i = posMarkedLayer; i > targetPosition; i--)
					Collections.swap(mLayerList, i, i - 1);
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

	public Bitmap getBitmapToSave() {
		Bitmap firstBitmap = mLayerList.get(mLayerList.size() - 1).getImage();
		Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(), firstBitmap.getConfig());
		Canvas canvas = new Canvas(bitmap);
		Paint overlayPaint = new Paint();
		overlayPaint.setAlpha(mLayerList.get(mLayerList.size() - 1).getScaledOpacity());
		canvas.drawBitmap(firstBitmap, new Matrix(), overlayPaint);

		if (mLayerList.size() > 1) {
			for (int i = mLayerList.size() - 2; i >= 0; i--) {
				overlayPaint.setAlpha(mLayerList.get(i).getScaledOpacity());
				canvas.drawBitmap(mLayerList.get(i).getImage(), 0, 0, overlayPaint);
			}
		}

		return bitmap;
	}

	public boolean checkAllLayerVisible() {

		for (Layer layer : mLayerList) {
			if (layer.getVisible())
				return false;
		}

		return true;
	}

}
