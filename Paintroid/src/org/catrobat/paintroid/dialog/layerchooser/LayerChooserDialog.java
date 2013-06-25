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
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ListView;

public final class LayerChooserDialog extends BaseDialog implements
		OnTouchListener {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "LayerChooserDialog has not been initialized. Call init() first!";

	private LayerChooserView mLayerChooserView;
	private ArrayList<OnLayerPickedListener> mOnLayerPickedListener;
	static int mNewLayer;
	private Button mButtonNewLayer;
	private Button mButtonLayerUp;
	private Button mButtonLayerDown;
	private Button mButtonAddLayer;
	private Button mButtonRemoveLayer;

	private ListView mListView;

	private CheckeredTransparentLinearLayout mBaseButtonLayout;

	static Paint mBackgroundPaint = new Paint();

	private static LayerChooserDialog instance;

	public interface OnLayerPickedListener {
		public void layerChanged(int layer);
	}

	private LayerChooserDialog(Context context) {
		super(context);
		mOnLayerPickedListener = new ArrayList<LayerChooserDialog.OnLayerPickedListener>();
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

	public void addOnLayerPickedListener(OnLayerPickedListener listener) {
		mOnLayerPickedListener.add(listener);
	}

	public void removeOnLayerPickedListener(OnLayerPickedListener listener) {
		mOnLayerPickedListener.remove(listener);
	}

	private void updateLayerChange(int layer) {
		for (OnLayerPickedListener listener : mOnLayerPickedListener) {
			if (listener == null) {
				mOnLayerPickedListener.remove(listener);
			}
			listener.layerChanged(layer);
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

		mButtonNewLayer = (Button) findViewById(R.id.btn_layerchooser_ok);
		mButtonNewLayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateLayerChange(mNewLayer);
				dismiss();
			}
		});

		mButtonLayerUp = (Button) findViewById(R.id.btn_layerchooser_up);
		mButtonLayerUp.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundResource(0);
					mNewLayer++;
					changeNewLayer(mNewLayer);
					// mLayerChooserView.setSelectedLayer(mNewLayer);

					return true;
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.color.abs__holo_blue_light);
					return true;
				}
				return false;
			}
		});

		mButtonLayerDown = (Button) findViewById(R.id.btn_layerchooser_down);
		mButtonLayerDown.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundResource(0);
					mNewLayer--;
					changeNewLayer(mNewLayer);
					// mLayerChooserView.setSelectedLayer(mNewLayer);
					return true;
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.color.abs__holo_blue_light);
					return true;
				}
				return false;
			}
		});

		mButtonAddLayer = (Button) findViewById(R.id.btn_layerchooser_add);
		mButtonAddLayer.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundResource(0);
					return true;
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.color.abs__holo_blue_light);
					return true;
				}
				return false;
			}
		});
		mButtonRemoveLayer = (Button) findViewById(R.id.btn_layerchooser_remove);
		mButtonRemoveLayer.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundResource(0);
					return true;
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.color.abs__holo_blue_light);
					return true;
				}
				return false;
			}
		});

		// mLayerChooserView = (LayerChooserView)
		// findViewById(R.id.view_layerchooser);
		// mLayerChooserView
		// .setOnLayerChangedListener(new
		// LayerChooserView.OnLayerChangedListener() {
		// @Override
		// public void LayerChanged(int layer) {
		// changeNewLayer(layer);
		// updateLayerChange(layer);
		// }
		// });

		// Changing the background color of the dialog
		// int color = Color.argb(100, 100, 100, 100);
		// this.getWindow().setBackgroundDrawable(new ColorDrawable(color));
		LayerRow layer_data[] = new LayerRow[] {
				new LayerRow(R.drawable.arrow, "Cloudy", true),
				new LayerRow(R.drawable.arrow, "Cloudy", true),
				new LayerRow(R.drawable.arrow, "Cloudy", true),
				new LayerRow(R.drawable.arrow, "Cloudy", true),
				new LayerRow(R.drawable.arrow, "Cloudy", true),
				new LayerRow(R.drawable.arrow, "Cloudy", true),
				new LayerRow(R.drawable.arrow, "Cloudy", true),
				new LayerRow(R.drawable.arrow, "Cloudy", true),
				new LayerRow(R.drawable.arrow, "Sunny", true) };

		LayerRowAdapter adapter = new LayerRowAdapter(this.getContext(),
				R.layout.layerchooser_layer_row, layer_data);

		mListView = (ListView) findViewById(R.id.mListView);
		mListView.setAdapter(adapter);
		mListView.setVerticalScrollBarEnabled(true);

	}

	// public void setInitialLayer(int layer) {
	// updateLayerChange(layer);
	// if ((mButtonNewLayer != null) && (mLayerChooserView != null)) {
	// changeNewLayer(layer);
	// mLayerChooserView.setSelectedLayer(layer);
	// }
	// }

	private void changeNewLayer(int layer) {
		mNewLayer = layer;
		mBaseButtonLayout.updateBackground();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
