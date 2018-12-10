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

package org.catrobat.paintroid.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.LayerContracts;

public class LayerAdapter extends BaseAdapter implements LayerContracts.Adapter {
	private final LayerContracts.Presenter presenter;
	private final SparseArray<LayerContracts.LayerViewHolder> viewHolders;

	public LayerAdapter(LayerContracts.Presenter presenter) {
		this.presenter = presenter;
		viewHolders = new SparseArray<>();
	}

	@Override
	public int getCount() {
		return presenter.getLayerCount();
	}

	@Override
	public Object getItem(int position) {
		return presenter.getLayerItem(position);
	}

	@Override
	public long getItemId(int position) {
		return presenter.getLayerItemId(position);
	}

	@Override
	public void notifyDataSetChanged() {
		viewHolders.clear();
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final LayerContracts.LayerViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.pocketpaint_item_layer, parent, false);
			viewHolder = new LayerViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (LayerContracts.LayerViewHolder) convertView.getTag();
		}

		viewHolders.put(position, viewHolder);
		presenter.onBindLayerViewHolderAtPosition(position, viewHolder);
		return convertView;
	}

	@Override
	public LayerContracts.LayerViewHolder getViewHolderAt(int position) {
		return viewHolders.get(position);
	}

	static class LayerViewHolder implements LayerContracts.LayerViewHolder {
		private final View itemView;
		private final LinearLayout layerBackground;
		private final ImageView imageView;

		LayerViewHolder(View itemView) {
			this.itemView = itemView;
			layerBackground = itemView.findViewById(R.id.pocketpaint_item_layer_background);
			imageView = itemView.findViewById(R.id.pocketpaint_item_layer_image);
		}

		@Override
		public void setSelected() {
			layerBackground.setBackgroundColor(Color.BLUE);
		}

		@Override
		public void setDeselected() {
			layerBackground.setBackgroundColor(Color.TRANSPARENT);
		}

		@Override
		public void setBitmap(Bitmap bitmap) {
			imageView.setImageBitmap(bitmap);
		}

		@Override
		public View getView() {
			return itemView;
		}

		@Override
		public void setMergable() {
			layerBackground.setBackgroundColor(Color.YELLOW);
		}
	}
}
