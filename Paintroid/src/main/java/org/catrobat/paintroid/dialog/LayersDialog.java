/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.paintroid.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.dialog.InfoDialog.DialogType;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.button.LayersAdapter;
import org.catrobat.paintroid.ui.button.ToolsAdapter;

public final class LayersDialog extends BaseDialog implements OnItemClickListener,
	OnItemLongClickListener, DialogInterface.OnDismissListener,
		SeekBar.OnSeekBarChangeListener{

	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "LayerDialog has not been initialized. Call init() first!";
	public static final String FRAGMENT_TRANSACTION_TAG_HELP = "helpdialogfragmenttag";

	private static LayersDialog instance;

	private LayersAdapter mLayerButtonAdapter;
    private ImageButton mNewLayerButton;
    private ImageButton mDeleteLayerButton;
	private ImageButton mNameLayerButton;
	private ImageButton mVisibleLayerButton;
	private ImageButton mLockLayerButton;
    private ImageButton mMergeLayerButton;
	private MainActivity mParent;
    private Layer currentLayer;
	private Context mContext;
    private Layer firstLayertoMerge;
    private Boolean mergeClicked;
	private SeekBar mOpacitySeekbar;
	private TextView mOpacitySeekbarLabel;

	public LayersAdapter getAdapter()
	{
		return mLayerButtonAdapter;
	}

	private LayersDialog(Context context, Bitmap first_layer) {
		super(context);
		mContext = context;
		mParent = (MainActivity) context;
        mLayerButtonAdapter = new LayersAdapter(context,
				PaintroidApplication.openedFromCatroid, first_layer);
	}


	public static LayersDialog getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}
	void InitCurrentLayer()
	{
		if(mLayerButtonAdapter == null)
		{
			mLayerButtonAdapter = new LayersAdapter(mContext,
					PaintroidApplication.openedFromCatroid, PaintroidApplication.drawingSurface.getBitmapCopy());
		}
		currentLayer = mLayerButtonAdapter.getLayer(0);
		if (currentLayer != null) {
			selectLayer(currentLayer);
			return;
		}
		Log.d("DEBUG", "CURRENT LAYER NOT INITIALIZED");

	}
	public static void init(MainActivity mainActivity, Bitmap first_layer) {
		instance = new LayersDialog(mainActivity, first_layer);
	}
	public Layer getCurrentLayer()
	{
		if(currentLayer == null)
		{
			InitCurrentLayer();
		}
		return currentLayer;
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

        mNewLayerButton = (ImageButton)
                findViewById(R.id.mButtonLayerNew);
        mNewLayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createLayer();
			}
		});

        mDeleteLayerButton = (ImageButton)
                findViewById(R.id.mButtonLayerDelete);
        mDeleteLayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLayer();
            }
        });

		mNameLayerButton = (ImageButton)
				findViewById(R.id.mButtonLayerRename);
		mNameLayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				renameLayer();
			}
		});

		mVisibleLayerButton = (ImageButton)
				findViewById(R.id.mButtonLayerVisible);
		mVisibleLayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleLayerVisible();
			}
		});

		mLockLayerButton = (ImageButton)
				findViewById(R.id.mButtonLayerLock);
		mLockLayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleLayerLocked();
			}
		});

		mOpacitySeekbarLabel = (TextView) findViewById(R.id.layer_opacity_seekbar_label	);
		mOpacitySeekbarLabel.setText(R.string.layer_opacity);

		mergeClicked = false;
        mMergeLayerButton = (ImageButton)
                findViewById(R.id.mButtonLayerMerge);
        mMergeLayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mergeClicked) {
                    mergeButtonDisabled();
                } else {
                    mergeButtonEnabled();
                    firstLayertoMerge = currentLayer;
                }

                mergeLayer();
            }
        });


		mOpacitySeekbar = (SeekBar) findViewById(R.id.seekbar_layer_opacity);
		mOpacitySeekbar.setOnSeekBarChangeListener(this);
		PaintroidApplication.drawingSurface.setLock(currentLayer.getLocked());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View button,
							int position, long id) {
		selectLayer(mLayerButtonAdapter.getLayer(position));

		AlertDialog.Builder alertChooseNewBuilder = new AlertDialog.Builder(this.getContext());
		alertChooseNewBuilder.setTitle(currentLayer.getName())

				.setItems(
						R.array.edit_layer, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
									case 0: //copy
										copyLayer();
										break;
									case 1:// move up
										moveUp();
										break;
									case 2://move down
										moveDown();
										break;
									case 3://move to top
										moveToTop();
										break;
									case 4: //move to bottom
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
	public void onItemClick(AdapterView<?> adapterView, View button,
			int position, long id) {
		selectLayer(mLayerButtonAdapter.getLayer(position));
        if(mergeClicked)
			mergeLayer();
		//dismiss();
	}

    public void createLayer()
    {
        boolean success = mLayerButtonAdapter.addLayer();
		refreshView();
        //if(!success)
          //TODO show error too many layers
		Command command = new LayerCommand(LayerCommand.LayerAction.ADD);
		PaintroidApplication.commandManager.commitCommand(command);
    }

    public void deleteLayer()
    {
		int current = mLayerButtonAdapter.getPosition(currentLayer.getLayerID());
		int new_position;
		if (current == mLayerButtonAdapter.getCount() - 1 && mLayerButtonAdapter.getCount() > 1) {
			new_position = current - 1;
		}
		else
			new_position = current;

		if(currentLayer != null) {
			mLayerButtonAdapter.removeLayer(currentLayer.getLayerID());
			refreshView();
		}

		selectLayer(mLayerButtonAdapter.getLayer(new_position));
		Command command = new LayerCommand(LayerCommand.LayerAction.REMOVE);
		PaintroidApplication.commandManager.commitCommand(command);
    }

	public void selectLayer(Layer toSelect) {
		if(currentLayer!=null)
		{
			currentLayer.setSelected(false);
			currentLayer.setImage(PaintroidApplication.drawingSurface.getBitmapCopy());
		}
		currentLayer = toSelect;
		currentLayer.setSelected(true);

		PaintroidApplication.drawingSurface.setLock(currentLayer.getLocked());
		PaintroidApplication.drawingSurface.setVisible(currentLayer.getVisible());
		PaintroidApplication.drawingSurface.setBitmap(currentLayer.getImage());
		refreshView();
		mOpacitySeekbar = (SeekBar) findViewById(R.id.seekbar_layer_opacity);
		if(mOpacitySeekbar != null)
		{
			mOpacitySeekbar.setProgress(currentLayer.getOpacity());
			return;
		}
		Log.d("DEBUG", "OPACITYSEEKBAR NOT INTIALIZED");
	}

	public void renameLayer()
	{
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext,
				                                             AlertDialog.THEME_DEVICE_DEFAULT_DARK);
		alertBuilder.setTitle(R.string.layer_rename_title);

		final EditText input = new EditText(mContext);
		input.setTextColor(Color.WHITE);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.requestFocus();
		final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

		alertBuilder.setView(input);
		alertBuilder.setPositiveButton(R.string.layer_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentLayer.setName(input.getText().toString());
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

	public void refreshView()
	{
		if(mLayerButtonAdapter != null)
		{
			GridView gridView = (GridView) findViewById(R.id.gridview_layers_menu);
			if(gridView != null)
			{
				gridView.setAdapter(mLayerButtonAdapter);
				return;
			}
			Log.d("DEBUG","LAYERGRIDVIEW NOT INITIALIZED");
		}else{
			Log.d("DEBUG","LAYERBUTTONADAPTER NOT INITIALIZED");
		}

	}
	public void toggleLayerVisible()
	{
		currentLayer.setVisible(!currentLayer.getVisible());
		PaintroidApplication.drawingSurface.setVisible(currentLayer.getVisible());
		refreshView();
	}

	public void toggleLayerLocked()
	{
		currentLayer.setLocked(!currentLayer.getLocked());
		PaintroidApplication.drawingSurface.setLock(currentLayer.getLocked());
		refreshView();
	}

    public void mergeLayer()
    {
		if(currentLayer.getLayerID() != firstLayertoMerge.getLayerID()){
			if(!currentLayer.getLocked()) {

				if (mLayerButtonAdapter.getPosition(currentLayer.getLayerID())
						 < mLayerButtonAdapter.getPosition(firstLayertoMerge.getLayerID())) {
					currentLayer.setName(currentLayer.getName() + "/" + firstLayertoMerge.getName());
					currentLayer.setImage(overlay(firstLayertoMerge, currentLayer));
				}
				else {
					currentLayer.setName(firstLayertoMerge.getName() + "/" + currentLayer.getName());
					currentLayer.setImage(overlay(currentLayer, firstLayertoMerge));
				}
				mLayerButtonAdapter.removeLayer(firstLayertoMerge.getLayerID());
				currentLayer.setOpacity(100);
				PaintroidApplication.drawingSurface.setBitmap(currentLayer.getImage());
				refreshView();
				mergeButtonDisabled();
				Command command = new LayerCommand(LayerCommand.LayerAction.MERGE);
				PaintroidApplication.commandManager.commitCommand(command);
        	}
	   	}
    }

    public static Bitmap overlay(Layer layer1, Layer layer2) {
		Bitmap bmp1 = layer1.getImage();
		Bitmap bmp2 = layer2.getImage();
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
		Paint overlayPaint = new Paint();
		overlayPaint.setAlpha(layer1.getScaledOpacity());
        canvas.drawBitmap(bmp1, new Matrix(), overlayPaint);
		overlayPaint.setAlpha(layer2.getScaledOpacity());
        canvas.drawBitmap(bmp2, 0, 0, overlayPaint);
        return bmOverlay;
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
		currentLayer.setOpacity(seekBar.getProgress());
		refreshView();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	public void resetLayer() {
		selectLayer(mLayerButtonAdapter.clearLayer());
		refreshView();
	}

	public void copyLayer() {
		mLayerButtonAdapter.copy(currentLayer.getLayerID());
		refreshView();
	}

	public void moveUp() {
		mLayerButtonAdapter.swapUp(currentLayer.getLayerID());
		refreshView();
	}

	public void moveDown() {
		mLayerButtonAdapter.swapDown(currentLayer.getLayerID());
		refreshView();
	}

	public void moveToTop() {
		mLayerButtonAdapter.swapTop(currentLayer.getLayerID());
		refreshView();
	}

	public void moveToBottom() {
		mLayerButtonAdapter.swapBottom(currentLayer.getLayerID());
		refreshView();
	}


	public void setMContext (Context context) {
		mContext = context;
	}
}
