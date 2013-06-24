/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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
/**
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *        Copyright (C) 2011 Devmil (Michael Lamers) 
 *        Mail: develmil@googlemail.com
 *
 *        Licensed under the Apache License, Version 2.0 (the "License");
 *        you may not use this file except in compliance with the License.
 *        You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing, software
 *        distributed under the License is distributed on an "AS IS" BASIS,
 *        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *        See the License for the specific language governing permissions and
 *        limitations under the License.
 */

package org.catrobat.paintroid.dialog.layerchooser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class LayerChooserView extends LinearLayout {

	private int mSelectedLayer;

	private OnLayerChangedListener mListener;

	public LayerChooserView(Context context) {
		super(context);
		init();
	}

	public LayerChooserView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setSelectedLayer(int layer) {
		setSelectedLayer(layer, null);
	}

	private void setSelectedLayer(int layer, View sender) {
		if (this.mSelectedLayer == layer) {
			return;
		}
		this.mSelectedLayer = layer;
		onLayerChanged();
	}

	public int getSelectedLayer() {
		return mSelectedLayer;
	}

	private void init() {

	}

	private void onLayerChanged() {
		if (mListener != null) {
			mListener.LayerChanged(getSelectedLayer());
		}
	}

	public void setOnLayerChangedListener(OnLayerChangedListener listener) {
		this.mListener = listener;
	}

	public interface OnLayerChangedListener {
		public void LayerChanged(int layer);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
	}
}
