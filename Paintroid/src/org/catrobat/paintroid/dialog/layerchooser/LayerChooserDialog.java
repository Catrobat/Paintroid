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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public final class LayerChooserDialog extends BaseDialog {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "LayerChooserDialog has not been initialized. Call init() first!";

	private ArrayList<OnLayerPickedListener> mOnLayerPickedListener;

	private Button mButtonNewLayer;
	private Button mButtonLayerUp;
	private Button mButtonLayerDown;
	private Button mButtonAddLayer;
	private Button mButtonRemoveLayer;

	private ListView mListView;
	public static LayerRowAdapter adapter;
	public static ArrayList<LayerRow> layer_data;
	public static int mSelectedLayerIndex;

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
				updateLayerChange(mSelectedLayerIndex);
				dismiss();
			}
		});

		mButtonLayerUp = (Button) findViewById(R.id.btn_layerchooser_up);
		mButtonLayerUp.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundResource(0);
					if (mSelectedLayerIndex != 0) {

						switchLayerData(mSelectedLayerIndex,
								mSelectedLayerIndex - 1);
						mSelectedLayerIndex--;
						changeNewLayer(mSelectedLayerIndex);
					}
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
				if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundResource(0);
					if (mSelectedLayerIndex != layer_data.size() - 1) {

						switchLayerData(mSelectedLayerIndex,
								mSelectedLayerIndex + 1);
						mSelectedLayerIndex++;
						changeNewLayer(mSelectedLayerIndex);
					}
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

					addLayer();
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
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getContext());

					alertDialogBuilder.setTitle("Deleting Layer");
					alertDialogBuilder
							.setMessage("Are you sure?")
							.setCancelable(false)
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											removeLayer();
										}
									})
							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.color.abs__holo_blue_light);
					return true;
				}
				return false;
			}
		});

		layer_data = new ArrayList<LayerRow>();
		layer_data.add(0, new LayerRow(R.drawable.arrow, "Layer" + 0, true,
				true));

		layer_data.get(mSelectedLayerIndex).selected = true;

		adapter = new LayerRowAdapter(this.getContext(),
				R.layout.layerchooser_layer_row, layer_data);

		mListView = (ListView) findViewById(R.id.mListView);
		mListView.setVerticalScrollBarEnabled(true);
		mListView.setClickable(true);
		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				setSelected(position, arg0, arg1);
				Log.i("my", String.valueOf(position));
			}
		});

	}

	protected void addLayer() {
		if (layer_data.size() < 30) {
			layer_data.add(mSelectedLayerIndex + 1, new LayerRow(
					R.drawable.arrow, "NewLayer", true, false));
			adapter.notifyDataSetChanged();
		} else {
			return;
		}
	}

	protected void removeLayer() {
		if (layer_data.size() > 1) {
			layer_data.remove(mSelectedLayerIndex);
			if (layer_data.size() == mSelectedLayerIndex) {
				mSelectedLayerIndex--;
			}
			layer_data.get(mSelectedLayerIndex).selected = true;
			adapter.notifyDataSetChanged();
		} else {
			return;
		}
	}

	protected void switchLayerData(int a, int b) {
		LayerRow tmp = layer_data.get(a);
		layer_data.set(a, layer_data.get(b));
		layer_data.set(b, tmp);
		adapter.notifyDataSetChanged();
	}

	public void setInitialLayer(int layer) {
		updateLayerChange(layer);
		adapter.notifyDataSetChanged();
	}

	private static void setSelected(int position, View a, View b) {
		for (int i = 0; i < layer_data.size(); i++) {
			layer_data.get(i).selected = false;
		}
		layer_data.get(position).selected = true;
		mSelectedLayerIndex = position;
		adapter.notifyDataSetChanged();
	}

	private void changeNewLayer(int layer) {
		mSelectedLayerIndex = layer;
		mBaseButtonLayout.updateBackground();
	}
}
