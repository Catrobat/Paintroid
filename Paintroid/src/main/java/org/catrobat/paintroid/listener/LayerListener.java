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

package org.catrobat.paintroid.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.eventlistener.OnActiveLayerChangedListener;
import org.catrobat.paintroid.eventlistener.OnRefreshLayerDialogListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.ui.button.LayersAdapter;
import org.catrobat.paintroid.ui.dragndrop.BrickDragAndDropLayerMenu;
import org.catrobat.paintroid.ui.dragndrop.MyDragShadowBuilder;
import org.catrobat.paintroid.ui.dragndrop.OnDragListener;

import java.util.ArrayList;


public final class LayerListener implements OnRefreshLayerDialogListener, OnActiveLayerChangedListener, AdapterView.OnItemClickListener {

    private static final String NOT_INITIALIZED_ERROR_MESSAGE = "LayerListener has not been initialized. Call init() first!";
	private static final int ANIMATION_TIME = 300;
    private static LayerListener instance;
    private LayersAdapter mLayersAdapter;
    private Context mContext;
    private Layer mCurrentLayer;
    private NavigationView mNavigationView;
	private BrickDragAndDropLayerMenu brickLayer;

	public LayerListener() {
		//only for testing purposes
	}

    private LayerListener(Context context, NavigationView view, Bitmap firstLayer) {
		setupLayerListener(view, context, firstLayer, false);
    }

	public void setupLayerListener(NavigationView view, Context context, Bitmap firstLayer, boolean orientationChanged) {
		mNavigationView = view;
		mContext = context;

		view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

		if (!orientationChanged) {
			mLayersAdapter = new LayersAdapter(context,
					PaintroidApplication.openedFromCatroid, firstLayer);
			InitCurrentLayer();
		}

		final ListView listView = (ListView) view.findViewById(R.id.nav_layer_list);

		brickLayer = new BrickDragAndDropLayerMenu(listView);
		OnDragListener dragListener = new OnDragListener(brickLayer);

		if (!orientationChanged) {
			listView.setAdapter(mLayersAdapter);
		}

		listView.setOnItemClickListener(this);
		listView.setOnDragListener(dragListener);
		listView.setLongClickable(true);


		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView v, View arg1, int pos, long id) {

				listView.getChildAt(pos).setVisibility(View.INVISIBLE);
				if (!mLayersAdapter.getLayer(pos).getSelected()) {
					setmCurrentLayer(mLayersAdapter.getLayer(pos));
				}
				brickLayer.setDragStartPosition(pos);

				MyDragShadowBuilder myShadow = new MyDragShadowBuilder(listView.getChildAt(pos));
				myShadow.setDragPos(pos);

				v.startDrag(null,  // the data to be dragged (dragData)
						myShadow,  // the drag shadow builder
						null,      // no need to use local data
						0          // flags (not currently used, set to 0)
				);

				return true;
			}
		});

		ImageButton addButton = (ImageButton) view.findViewById(R.id.layer_side_nav_button_add);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createLayer();
			}
		});
		ImageButton delButton = (ImageButton) view.findViewById(R.id.layer_side_nav_button_delete);
		delButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View layerItem = listView.getChildAt(mLayersAdapter.getPosition(getCurrentLayer().getLayerID()));
				Animation translateAnimation = new TranslateAnimation(0f, layerItem.getWidth(), 0f, 0f);
				translateAnimation.setDuration(ANIMATION_TIME);
				translateAnimation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {	}

					@Override
					public void onAnimationEnd(Animation animation) {
						deleteLayer();
					}

					@Override
					public void onAnimationRepeat(Animation animation) { }
				});

				if (mLayersAdapter.getCount() > 1)
					layerItem.startAnimation(translateAnimation);
			}
		});
		updateButtonResource();
		refreshView();
	}

    public static LayerListener getInstance() {
        if (instance == null) {
            throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
        }
        return instance;
    }

    public static void init(MainActivity mainActivity, NavigationView view, Bitmap firstLayer) {
        if(instance == null)
            instance = new LayerListener(mainActivity, view, firstLayer);
		else
			LayerListener.getInstance().setupLayerListener(view, mainActivity, null, true);
    }

    void InitCurrentLayer() {
        if (mLayersAdapter == null) {
            Log.e(PaintroidApplication.TAG, "ERROR, InitCurrentLayer -> mLayerAdapter == null");
            mLayersAdapter = new LayersAdapter(mContext,
                    PaintroidApplication.openedFromCatroid, PaintroidApplication.drawingSurface.getBitmapCopy());
        }
        mCurrentLayer = mLayersAdapter.getLayer(0);
        if (mCurrentLayer != null) {
            selectLayer(mCurrentLayer);
            return;
        }
        Log.d("DEBUG", "CURRENT LAYER NOT INITIALIZED");

    }

    public LayersAdapter getAdapter() {
        return mLayersAdapter;
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
    }

    public void setmCurrentLayer(Layer toSelect) {
		if (mCurrentLayer != null) {
			mCurrentLayer.setSelected(false);
			mCurrentLayer.setImage(PaintroidApplication.drawingSurface.getBitmapCopy());
		}
		mCurrentLayer = toSelect;
		mCurrentLayer.setSelected(true);

		PaintroidApplication.drawingSurface.setLock(mCurrentLayer.getLocked());
		PaintroidApplication.drawingSurface.setVisible(mCurrentLayer.getVisible());
		PaintroidApplication.drawingSurface.setBitmap(mCurrentLayer.getImage());
	}

    public Layer getCurrentLayer() {
        if (mCurrentLayer == null) {
            InitCurrentLayer();
        }
        return mCurrentLayer;
    }

    public void refreshView() {
        if (mLayersAdapter != null) {
            ListView listView = (ListView) mNavigationView.findViewById(R.id.nav_layer_list);
            if (listView != null) {
                mLayersAdapter.notifyDataSetChanged();
                listView.setAdapter(mLayersAdapter);
            }
            else
                Log.d("DEBUG", "LAYERGRIDVIEW NOT INITIALIZED");
        } else {
            Log.d("DEBUG", "LAYERBUTTONADAPTER NOT INITIALIZED");
        }

    }

	protected void updateButtonResource() {
		ImageButton addButton = (ImageButton) mNavigationView.findViewById(R.id.layer_side_nav_button_add);
		int addButtonResource = mLayersAdapter.getCount() < mLayersAdapter.getMaxLayerCount() ?
				R.drawable.icon_layers_new : R.drawable.icon_layers_new_disabled;
		addButton.setBackgroundResource(addButtonResource);
		ImageButton deleteButton = (ImageButton) mNavigationView.findViewById(R.id.layer_side_nav_button_delete);
		int deleteButtonResource = mLayersAdapter.getCount() > 1 ?
				R.drawable.icon_layers_delete : R.drawable.icon_layers_delete_disabled;
		deleteButton.setBackgroundResource(deleteButtonResource);
	}

    public void createLayer() {
        boolean success = mLayersAdapter.addLayer();
		if (success) {
			Layer layer = mLayersAdapter.getLayer(0);
			selectLayer(layer);
			PaintroidApplication.commandManager.commitAddLayerCommand(new LayerCommand(layer));
			UndoRedoManager.getInstance().update();
		} else {
            Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_too_many_layers,
                    Toast.LENGTH_LONG).show();
        }
		updateButtonResource();
    }

    public void deleteLayer() {

        int layerCount = mLayersAdapter.getCount();
        if (layerCount == 1 || mCurrentLayer == null)
            return;

        int currentPosition = mLayersAdapter.getPosition(mCurrentLayer.getLayerID());
        int newPosition = currentPosition;
        if (currentPosition == layerCount - 1 && layerCount > 1) {
            newPosition = currentPosition - 1;
        }

        mLayersAdapter.removeLayer(mCurrentLayer);
        PaintroidApplication.commandManager.commitRemoveLayerCommand(new LayerCommand(mCurrentLayer));
        selectLayer(mLayersAdapter.getLayer(newPosition));

        if (mLayersAdapter.checkAllLayerVisible())
            Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_invisible,
                    Toast.LENGTH_LONG).show();

        updateButtonResource();
        refreshView();
    }

	public void moveLayer(int layerToMove, int targetPosition) {
		mLayersAdapter.swapLayer(layerToMove, targetPosition);
	}

	public void mergeLayer(int firstLayer, int secondLayer) {
		if (mLayersAdapter.getLayer(firstLayer).getLayerID() != mLayersAdapter.getLayer(secondLayer).getLayerID()) {
			ArrayList<Integer> layerToMergeIds = new ArrayList<Integer>();
			layerToMergeIds.add(mLayersAdapter.getLayer(firstLayer).getLayerID());
			layerToMergeIds.add(mLayersAdapter.getLayer(secondLayer).getLayerID());

			Layer layer = mLayersAdapter.mergeLayer(mLayersAdapter.getLayer(firstLayer), mLayersAdapter.getLayer(secondLayer));

			selectLayer(layer);
			updateButtonResource();
			refreshView();

			PaintroidApplication.commandManager.commitMergeLayerCommand(new LayerCommand(getCurrentLayer(), layerToMergeIds));
			Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_merged,
					Toast.LENGTH_LONG).show();
		}
	}

	public void resetLayer() {
		Layer layer = mLayersAdapter.clearLayer();
		selectLayer(layer);
		PaintroidApplication.commandManager.commitAddLayerCommand(new LayerCommand(layer));
		refreshView();
	}

	public Bitmap getBitmapOfAllLayersToSave() {
		return mLayersAdapter.getBitmapToSave();
	}

    @Override
    public void onActiveLayerChanged(Layer layer) {
        Log.e(PaintroidApplication.TAG, "onActiveLayerChanged");
        if (mCurrentLayer.getLayerID() != layer.getLayerID()) {
            selectLayer(layer);
        }
        refreshView();
    }

    @Override
    public void onLayerDialogRefreshView() {
        Log.e(PaintroidApplication.TAG, "onLayerDialogRefreshView");

        refreshView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectLayer(mLayersAdapter.getLayer(position));
        UndoRedoManager.getInstance().update();
    }
}
