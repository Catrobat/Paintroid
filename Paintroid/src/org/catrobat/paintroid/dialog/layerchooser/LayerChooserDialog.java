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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.layer.SwitchLayerCommand;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public final class LayerChooserDialog extends DialogFragment implements
		DialogInterface.OnClickListener {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "LayerChooserDialog has not been initialized. Call init() first!";

	private ArrayList<OnLayerPickedListener> mOnLayerPickedListener;

	private Button mButtonLayerUp;
	private Button mButtonLayerDown;
	private Button mButtonAddLayer;
	private Button mButtonRemoveLayer;

	private ListView mListView;
	public static LayerRowAdapter adapter;
	public static ArrayList<LayerRow> layer_data;
	public static int mSelectedLayerIndex;

	private CheckeredTransparentLinearLayout mBaseButtonLayout;
	private Context mContext;

	static Paint mBackgroundPaint = new Paint();

	private static LayerChooserDialog instance;

	public interface OnLayerPickedListener {
		public void layerChanged(int layer);
	}

	private LayerChooserDialog(Context context) {
		mOnLayerPickedListener = new ArrayList<LayerChooserDialog.OnLayerPickedListener>();
		mContext = context;
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

	@TargetApi(11)
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflator = getActivity().getLayoutInflater();
		AlertDialog.Builder builder;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			builder = new AlertDialog.Builder(mContext);

		} else {
			builder = new AlertDialog.Builder(mContext,
					AlertDialog.THEME_HOLO_DARK);
		}

		// builder.setContentView(R.layout.layerchooser_dialog);
		View view = inflator.inflate(R.layout.layerchooser_dialog, null);
		builder.setTitle(R.string.layer_chooser_title);

		Bitmap backgroundBitmap = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.checkeredbg);
		BitmapShader mBackgroundShader = new BitmapShader(backgroundBitmap,
				TileMode.REPEAT, TileMode.REPEAT);

		mBackgroundPaint.setShader(mBackgroundShader);

		mBaseButtonLayout = (CheckeredTransparentLinearLayout) view
				.findViewById(R.id.layerchooser_ok_button_base_layout);

		builder.setNeutralButton(R.string.done, this);

		mButtonLayerUp = (Button) view.findViewById(R.id.btn_layerchooser_up);
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

		mButtonLayerDown = (Button) view
				.findViewById(R.id.btn_layerchooser_down);
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

		mButtonAddLayer = (Button) view.findViewById(R.id.btn_layerchooser_add);
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
		mButtonRemoveLayer = (Button) view
				.findViewById(R.id.btn_layerchooser_remove);
		mButtonRemoveLayer.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {

					v.setBackgroundResource(0);

					if (layer_data.size() > 1) {
						AlertDialog.Builder alertDialogBuilder;

						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
							alertDialogBuilder = new AlertDialog.Builder(
									mContext);

						} else {
							alertDialogBuilder = new AlertDialog.Builder(
									mContext, AlertDialog.THEME_HOLO_DARK);
						}

						alertDialogBuilder
								.setTitle(R.string.layer_delete_layer_title);
						alertDialogBuilder
								.setMessage(R.string.layer_delete_layer_message)
								.setCancelable(false)
								.setPositiveButton(R.string.yes,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int id) {
												removeLayer();
											}
										})
								.setNegativeButton(R.string.no,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}
				} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.color.abs__holo_blue_light);
					return true;
				}
				return false;
			}
		});

		adapter = new LayerRowAdapter(this.mContext,
				R.layout.layerchooser_layer_row, layer_data);

		mListView = (ListView) view.findViewById(R.id.mListView);
		mListView.setVerticalScrollBarEnabled(true);
		mListView.setClickable(true);
		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				setSelected(position, arg0, arg1);
			}
		});

		builder.setView(view);

		return builder.create();
	}

	protected void addLayer() {
		if (layer_data.size() < 30) {
			layer_data.add(
					mSelectedLayerIndex + 1,
					new LayerRow(R.drawable.arrow, mContext
							.getString(R.string.layer_new_layer_name), true,
							false));
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

		Command command = new SwitchLayerCommand(a, b);
		PaintroidApplication.commandManager.commitCommand(command);

		LayerRow tmp = layer_data.get(a);
		layer_data.set(a, layer_data.get(b));
		layer_data.set(b, tmp);
		adapter.notifyDataSetChanged();
	}

	public void setInitialLayer(int layer) {
		updateLayerChange(layer);
		mSelectedLayerIndex = layer;
		if (layer_data != null) {
			adapter.notifyDataSetChanged();
		} else {
			layer_data = new ArrayList<LayerRow>();
			layer_data.add(
					0,
					new LayerRow(R.drawable.arrow, mContext
							.getString(R.string.layer_new_layer_name), true,
							true));
		}
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

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case AlertDialog.BUTTON_NEUTRAL:
			updateLayerChange(mSelectedLayerIndex);
			dismiss();
			break;

		}
	}
}
