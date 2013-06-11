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

import java.util.ArrayList;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.BaseDialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.widget.Button;

public final class LayerChooserDialog extends BaseDialog {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "LayerChooserDialog has not been initialized. Call init() first!";

	private LayerChooserView mLayerChooserView;
	private ArrayList<OnLayerChosenListener> mOnLayerChosenListener;
	static int mNewColor;
	private Button mButtonNewColor;
	private CheckeredTransparentLinearLayout mBaseButtonLayout;

	static Paint mBackgroundPaint = new Paint();

	private static LayerChooserDialog instance;

	public interface OnLayerChosenListener {
		public void colorChanged(int color);
	}

	private LayerChooserDialog(Context context) {
		super(context);
		mOnLayerChosenListener = new ArrayList<LayerChooserDialog.OnLayerChosenListener>();
	}

	public static LayerChooserDialog getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	public static void init(Context context) {
		instance = new LayerChooserDialog(context);
	}

	public void addOnLayerChosenListener(OnLayerChosenListener listener) {
		mOnLayerChosenListener.add(listener);
	}

	public void removeOnLayerChosenListener(OnLayerChosenListener listener) {
		mOnLayerChosenListener.remove(listener);
	}

	private void updateColorChange(int color) {
		for (OnLayerChosenListener listener : mOnLayerChosenListener) {
			if (listener == null) {
				mOnLayerChosenListener.remove(listener);
			}
			listener.colorChanged(color);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layerchooser_dialog);
		setTitle(R.string.layer_chooser_title);

		Bitmap backgroundBitmap = BitmapFactory.decodeResource(getContext()
				.getResources(), R.drawable.checkeredbg);
		BitmapShader mBackgroundShader = new BitmapShader(backgroundBitmap,
				TileMode.REPEAT, TileMode.REPEAT);

		mBackgroundPaint.setShader(mBackgroundShader);

		mBaseButtonLayout = (CheckeredTransparentLinearLayout) findViewById(R.id.layerchooser_ok_button_base_layout);

		// mButtonNewColor = (Button) findViewById(R.id.btn_layerchooser_ok);
		// mButtonNewColor.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// updateLayerChange(mNewColor);
		// dismiss();
		// }
		// });

		mLayerChooserView = (LayerChooserView) findViewById(R.id.view_layerchooser);
		mLayerChooserView
				.setOnLayerChangedListener(new LayerChooserView.OnLayerChangedListener() {
					@Override
					public void layerChanged(int color) {
						changeNewColor(color);
						updateColorChange(color);
					}
				});

	}

	public void setInitialColor(int color) {
		updateColorChange(color);
		if ((mButtonNewColor != null) && (mLayerChooserView != null)) {
			changeNewColor(color);
			mLayerChooserView.setSelectedColor(color);
		}
	}

	private void changeNewColor(int color) {
		mNewColor = color;
		mBaseButtonLayout.updateBackground();
		int referenceColor = (Color.red(color) + Color.blue(color) + Color
				.green(color)) / 3;
		if (referenceColor <= 128 && Color.alpha(color) > 5) {
			mButtonNewColor.setTextColor(Color.WHITE);
		} else {
			mButtonNewColor.setTextColor(Color.BLACK);
		}
		mButtonNewColor.setBackgroundColor(color);
	}
}
