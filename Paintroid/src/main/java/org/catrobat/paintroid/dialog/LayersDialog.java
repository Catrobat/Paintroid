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

package org.catrobat.paintroid.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.eventlistener.OnActiveLayerChangedListener;
import org.catrobat.paintroid.eventlistener.OnRefreshLayerDialogListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.ui.button.LayersAdapter;

import java.util.ArrayList;

public final class LayersDialog extends BaseDialog implements OnItemClickListener,
		OnItemLongClickListener, DialogInterface.OnDismissListener,
		SeekBar.OnSeekBarChangeListener, OnRefreshLayerDialogListener, OnActiveLayerChangedListener {

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "LayerDialog has not been initialized. Call init() first!";

	private static LayersDialog instance;

	private LayersAdapter mLayerButtonAdapter;
	private ImageButton mNewLayerButton;
	private ImageButton mDeleteLayerButton;
	private ImageButton mNameLayerButton;
	private ImageButton mVisibleLayerButton;
	private ImageButton mLockLayerButton;
	private ImageButton mMergeLayerButton;
	private Layer mCurrentLayer;
	private Context mContext;
	private Layer firstLayertoMerge;
	private Boolean mergeClicked;
	private SeekBar mOpacitySeekbar;
	private TextView mOpacitySeekbarLabel;

	public LayersAdapter getAdapter() {
		return mLayerButtonAdapter;
	}

	private LayersDialog(Context context, Bitmap first_layer) {
		super(context);
		mContext = context;
		mLayerButtonAdapter = new LayersAdapter(context,
				PaintroidApplication.openedFromCatroid, first_layer);
	}

	public static LayersDialog getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	void InitCurrentLayer() {
		if (mLayerButtonAdapter == null) {
			mLayerButtonAdapter = new LayersAdapter(mContext,
					PaintroidApplication.openedFromCatroid, PaintroidApplication.drawingSurface.getBitmapCopy());
		}
		mCurrentLayer = mLayerButtonAdapter.getLayer(0);
		if (mCurrentLayer != null) {
			selectLayer(mCurrentLayer);
			return;
		}
		Log.d("DEBUG", "CURRENT LAYER NOT INITIALIZED");

	}

	public static void init(MainActivity mainActivity, Bitmap first_layer) {
			instance = new LayersDialog(mainActivity, first_layer);
	}

	public Layer getCurrentLayer() {
		if (mCurrentLayer == null) {
			InitCurrentLayer();
		}
		return mCurrentLayer;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layers_menu);
		setTitle(R.string.layers_title);
		setCanceledOnTouchOutside(true);
		setOnDismissListener(this);
		GridView gridView = (GridView) findViewById(R.id.gridview_layers_menu);

		gridView.setAdapter(mLayerButtonAdapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemLongClickListener(this);

		InitCurrentLayer();

		mNewLayerButton = (ImageButton) findViewById(R.id.mButtonLayerNew);
		mNewLayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createLayer();
			}
		});

		mDeleteLayerButton = (ImageButton) findViewById(R.id.mButtonLayerDelete);
		mDeleteLayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteLayer();
			}
		});

		mNameLayerButton = (ImageButton) findViewById(R.id.mButtonLayerRename);
		mNameLayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				renameLayer();
			}
		});

		mVisibleLayerButton = (ImageButton) findViewById(R.id.mButtonLayerVisible);
		mVisibleLayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleLayerVisible();
			}
		});

		mLockLayerButton = (ImageButton) findViewById(R.id.mButtonLayerLock);
		mLockLayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleLayerLocked();
			}
		});

		mOpacitySeekbarLabel = (TextView) findViewById(R.id.layer_opacity_seekbar_label);
		mOpacitySeekbarLabel.setText(R.string.layer_opacity);

		mergeClicked = false;
		mMergeLayerButton = (ImageButton) findViewById(R.id.mButtonLayerMerge);
		mMergeLayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mergeClicked) {
					mergeButtonDisabled();
				} else {
					mergeButtonEnabled();
					firstLayertoMerge = mCurrentLayer;
					Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_merge_info, Toast.LENGTH_LONG).show();
				}

				mergeLayer();
			}
		});

		mOpacitySeekbar = (SeekBar) findViewById(R.id.seekbar_layer_opacity);
		mOpacitySeekbar.setOnSeekBarChangeListener(this);
		PaintroidApplication.drawingSurface.setLock(mCurrentLayer.getLocked());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View button, int position, long id) {
		selectLayer(mLayerButtonAdapter.getLayer(position));

		AlertDialog.Builder alertChooseNewBuilder = new AlertDialog.Builder(this.getContext());
		alertChooseNewBuilder.setTitle(mCurrentLayer.getName()).setItems(
				R.array.edit_layer, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								copyLayer();
								break;
							case 1:
								moveUp();
								break;
							case 2:
								moveDown();
								break;
							case 3:
								moveToTop();
								break;
							case 4:
								moveToBottom();
								break;
						}
					}
				});
		AlertDialog alertNew = alertChooseNewBuilder.create();
		alertNew.show();

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View button, int position, long id) {
		selectLayer(mLayerButtonAdapter.getLayer(position));
		if (mergeClicked)
			mergeLayer();
	}

	public void createLayer() {
		boolean success = mLayerButtonAdapter.addLayer();
		Layer layer = mLayerButtonAdapter.getLayer(0);
		selectLayer(layer);
		refreshView();

		if (!success) {
			Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_too_many_layers,
					Toast.LENGTH_LONG).show();
		}

		PaintroidApplication.commandManager.commitAddLayerCommand(new LayerCommand(layer));
	}

	public void deleteLayer() {

		int layerCount = mLayerButtonAdapter.getCount();
		if (layerCount == 1 || mCurrentLayer == null)
			return;

		int currentPosition = mLayerButtonAdapter.getPosition(mCurrentLayer.getLayerID());
		int newPosition = currentPosition;
		if (currentPosition == layerCount - 1 && layerCount > 1) {
			newPosition = currentPosition - 1;
		}

		mLayerButtonAdapter.removeLayer(mCurrentLayer);
		PaintroidApplication.commandManager.commitRemoveLayerCommand(new LayerCommand(mCurrentLayer));
		selectLayer(mLayerButtonAdapter.getLayer(newPosition));

		if (mLayerButtonAdapter.checkAllLayerVisible())
			Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_invisible,
					Toast.LENGTH_LONG).show();

		refreshView();
	}

	public void selectLayer(Layer toSelect) {
		if (mCurrentLayer != null) {
			mCurrentLayer.setSelected(false);
			mCurrentLayer.setImage(PaintroidApplication.drawingSurface.getBitmapCopy());
		}
		mCurrentLayer = toSelect;
		mCurrentLayer.setSelected(true);

		PaintroidApplication.drawingSurface.setLock(mCurrentLayer.getLocked());
		PaintroidApplication.drawingSurface.setVisible(mCurrentLayer.getVisible());
		PaintroidApplication.drawingSurface.setBitmap(mCurrentLayer.getImage());
		refreshView();
		mOpacitySeekbar = (SeekBar) findViewById(R.id.seekbar_layer_opacity);
		if (mOpacitySeekbar != null)
			mOpacitySeekbar.setProgress(mCurrentLayer.getOpacity());
		else
			Log.d("DEBUG", "OPACITY SEEKBAR NOT INITIALIZED");
	}

	public void renameLayer() {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext,
				AlertDialog.THEME_DEVICE_DEFAULT_DARK);
		alertBuilder.setTitle(R.string.layer_rename_title);

		final EditText input = new EditText(mContext);
		input.setTextColor(Color.WHITE);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.requestFocus();
		input.setText(mCurrentLayer.getName(), TextView.BufferType.EDITABLE);
		int maxLength = 30;
		input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
		final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

		alertBuilder.setView(input);
		alertBuilder.setPositiveButton(R.string.layer_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				LayerCommand layerCommand = new LayerCommand(mCurrentLayer, mCurrentLayer.getName());
				mCurrentLayer.setName(input.getText().toString());
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				PaintroidApplication.commandManager.commitRenameLayerCommand(layerCommand);
				refreshView();
			}
		});
		alertBuilder.setNegativeButton(R.string.layer_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				dialog.cancel();
			}
		});

		alertBuilder.show();
	}

	public void refreshView() {
		if (mLayerButtonAdapter != null) {
			GridView gridView = (GridView) findViewById(R.id.gridview_layers_menu);
			if (gridView != null)
				gridView.setAdapter(mLayerButtonAdapter);
			else
				Log.d("DEBUG", "LAYERGRIDVIEW NOT INITIALIZED");
		} else {
			Log.d("DEBUG", "LAYERBUTTONADAPTER NOT INITIALIZED");
		}

	}

	public void toggleLayerVisible() {
		mCurrentLayer.setVisible(!mCurrentLayer.getVisible());
		PaintroidApplication.drawingSurface.setVisible(mCurrentLayer.getVisible());
		PaintroidApplication.commandManager.commitLayerVisibilityCommand(new LayerCommand(mCurrentLayer));

		if (mLayerButtonAdapter.checkAllLayerVisible())
			Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_invisible, Toast.LENGTH_LONG).show();

		refreshView();
	}

	public void toggleLayerLocked() {
		mCurrentLayer.setLocked(!mCurrentLayer.getLocked());
		PaintroidApplication.drawingSurface.setLock(mCurrentLayer.getLocked());
		PaintroidApplication.commandManager.commitLayerLockCommand(new LayerCommand(mCurrentLayer));
		refreshView();
	}

	public void mergeLayer() {
		if (mCurrentLayer.getLayerID() != firstLayertoMerge.getLayerID()) {

			Layer layer = mLayerButtonAdapter.mergeLayer(mCurrentLayer, firstLayertoMerge);

			ArrayList<Integer> layerToMergeIds = new ArrayList<Integer>();
			layerToMergeIds.add(mCurrentLayer.getLayerID());
			layerToMergeIds.add(firstLayertoMerge.getLayerID());

			mergeButtonDisabled();
			selectLayer(layer);
			refreshView();

			PaintroidApplication.commandManager.commitMergeLayerCommand(new LayerCommand(mCurrentLayer, layerToMergeIds));
		}
	}

	public void onDismiss(DialogInterface dialog) {
		mergeButtonDisabled();
	}

	private void mergeButtonEnabled() {
		mMergeLayerButton.setBackgroundColor(Color.rgb(0, 180, 241));
		mergeClicked = true;
	}

	private void mergeButtonDisabled() {
		mMergeLayerButton.setBackgroundColor(Color.BLACK);
		mergeClicked = false;
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		mCurrentLayer.setOpacity(seekBar.getProgress());
		refreshView();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	public void resetLayer() {
		Layer layer = mLayerButtonAdapter.clearLayer();
		selectLayer(layer);
		PaintroidApplication.commandManager.commitAddLayerCommand(new LayerCommand(layer));
		refreshView();
	}

	public void copyLayer() {
		mLayerButtonAdapter.copy(mCurrentLayer.getLayerID());
		Layer layer = mLayerButtonAdapter.getLayer(0);
		PaintroidApplication.commandManager.commitAddLayerCommand(new LayerCommand(layer));
		refreshView();
	}

	public void moveUp() {
		mLayerButtonAdapter.swapUp(mCurrentLayer.getLayerID());
		refreshView();
	}

	public void moveDown() {
		mLayerButtonAdapter.swapDown(mCurrentLayer.getLayerID());
		refreshView();
	}

	public void moveToTop() {
		mLayerButtonAdapter.swapTop(mCurrentLayer.getLayerID());
		refreshView();
	}

	public void moveToBottom() {
		mLayerButtonAdapter.swapBottom(mCurrentLayer.getLayerID());
		refreshView();
	}

	@Override
	public void onLayerDialogRefreshView() {
		refreshView();
	}

	@Override
	public void onActiveLayerChanged(Layer layer) {
		if (mCurrentLayer.getLayerID() != layer.getLayerID()) {
			selectLayer(layer);
		}
		refreshView();
	}

	public Bitmap getBitmapOfAllLayersToSave() {
		return mLayerButtonAdapter.getBitmapToSave();
	}

}
