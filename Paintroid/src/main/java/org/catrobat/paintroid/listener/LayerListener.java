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

import android.app.Activity;
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
import org.catrobat.paintroid.command.implementation.CommandManagerImplementation;
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
	private static final String TAG = LayerListener.class.getSimpleName();
	private static final String NOT_INITIALIZED_ERROR_MESSAGE = "LayerListener has not been initialized. Call init() first!";
	private static final int ANIMATION_TIME = 300;
	private static final int LAYER_UNDO_LIMIT = 10;
	private static LayerListener instance;
	private LayersAdapter layersAdapter;
	private Context context;
	private Layer currentLayer;
	private NavigationView navigationView;
	private BrickDragAndDropLayerMenu brickLayer;

	private LayerListener(Context context, NavigationView view, Bitmap firstLayer) {
		setupLayerListener(view, context, firstLayer, false);
	}

	public static LayerListener getInstance() {
		if (instance == null) {
			throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
		}
		return instance;
	}

	public static void init(MainActivity mainActivity, NavigationView view, Bitmap firstLayer, boolean orientationChanged) {
		if (!orientationChanged) {
			instance = new LayerListener(mainActivity, view, firstLayer);
		} else {
			getInstance().setupLayerListener(view, mainActivity, null, true);
		}
	}

	public void setupLayerListener(NavigationView view, Context context, Bitmap firstLayer, boolean orientationChanged) {
		navigationView = view;
		this.context = context;

		if (!orientationChanged) {
			layersAdapter = new LayersAdapter(context,
					firstLayer);
			initCurrentLayer();
		}

		final ListView listView = (ListView) view.findViewById(R.id.nav_layer_list);

		brickLayer = new BrickDragAndDropLayerMenu(listView);
		OnDragListener dragListener = new OnDragListener(brickLayer);

		if (!orientationChanged) {
			listView.setAdapter(layersAdapter);
		}

		listView.setOnItemClickListener(this);
		listView.setOnDragListener(dragListener);
		listView.setLongClickable(true);

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView v, View arg1, int pos, long id) {

				listView.getChildAt(pos).setVisibility(View.INVISIBLE);
				if (!layersAdapter.getLayer(pos).getSelected()) {
					setCurrentLayer(layersAdapter.getLayer(pos));
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
				View layerItem = listView.getChildAt(layersAdapter.getPosition(getCurrentLayer().getLayerID()));
				Animation translateAnimation = new TranslateAnimation(0f, layerItem.getWidth(), 0f, 0f);
				translateAnimation.setDuration(ANIMATION_TIME);
				translateAnimation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						deleteLayer();
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}
				});

				if (layersAdapter.getCount() > 1) {
					layerItem.startAnimation(translateAnimation);
				}
			}
		});
		updateButtonResource();
		refreshView();
	}

	void initCurrentLayer() {
		if (layersAdapter == null) {
			Log.d(TAG, "ERROR, initCurrentLayer -> layerAdapter == null");
			layersAdapter = new LayersAdapter(context,
					PaintroidApplication.drawingSurface.getBitmapCopy());
		}
		currentLayer = layersAdapter.getLayer(0);
		if (currentLayer != null) {
			selectLayer(currentLayer);
			return;
		}
		Log.d(TAG, "CURRENT LAYER NOT INITIALIZED");
	}

	public LayersAdapter getAdapter() {
		return layersAdapter;
	}

	public void selectLayer(Layer toSelect) {
		if (currentLayer != null) {
			currentLayer.setSelected(false);
			currentLayer.setImage(PaintroidApplication.drawingSurface.getBitmapCopy());
		}
		currentLayer = toSelect;
		currentLayer.setSelected(true);

		PaintroidApplication.drawingSurface.setLock(currentLayer.getLocked());
		PaintroidApplication.drawingSurface.setVisible(currentLayer.getVisible());
		PaintroidApplication.drawingSurface.setBitmap(currentLayer.getImage());
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				refreshView();
			}
		});
	}

	public Layer getCurrentLayer() {
		if (currentLayer == null) {
			initCurrentLayer();
		}
		return currentLayer;
	}

	public void setCurrentLayer(Layer toSelect) {
		if (currentLayer != null) {
			currentLayer.setSelected(false);
			currentLayer.setImage(PaintroidApplication.drawingSurface.getBitmapCopy());
		}
		currentLayer = toSelect;
		currentLayer.setSelected(true);

		PaintroidApplication.drawingSurface.setLock(currentLayer.getLocked());
		PaintroidApplication.drawingSurface.setVisible(currentLayer.getVisible());
		PaintroidApplication.drawingSurface.setBitmap(currentLayer.getImage());
	}

	public void refreshView() {
		if (layersAdapter != null) {
			ListView listView = (ListView) navigationView.findViewById(R.id.nav_layer_list);
			if (listView != null) {
				layersAdapter.notifyDataSetChanged();
				listView.setAdapter(layersAdapter);
			} else {
				Log.d(TAG, "LAYERGRIDVIEW NOT INITIALIZED");
			}
		} else {
			Log.d(TAG, "LAYERBUTTONADAPTER NOT INITIALIZED");
		}
		refreshDrawingSurface();
	}

	public void updateButtonResource() {
		ImageButton addButton = (ImageButton) navigationView.findViewById(R.id.layer_side_nav_button_add);
		int addButtonResource = layersAdapter.getCount() < layersAdapter.MAX_LAYER
				? R.drawable.icon_layers_new : R.drawable.icon_layers_new_disabled;
		addButton.setBackgroundResource(addButtonResource);
		ImageButton deleteButton = (ImageButton) navigationView.findViewById(R.id.layer_side_nav_button_delete);
		int deleteButtonResource = layersAdapter.getCount() > 1
				? R.drawable.icon_layers_delete : R.drawable.icon_layers_delete_disabled;
		deleteButton.setBackgroundResource(deleteButtonResource);
	}

	public void createLayer() {
		if (layersAdapter.getLayerCounter() > LAYER_UNDO_LIMIT) {
			((CommandManagerImplementation) PaintroidApplication.commandManager).deleteCommandFirstDeletedLayer();
		}

		boolean success = layersAdapter.addLayer();
		if (success) {
			Layer layer = layersAdapter.getLayer(0);
			selectLayer(layer);
			PaintroidApplication.commandManager.commitAddLayerCommand(new LayerCommand(layer));
			UndoRedoManager.getInstance().update();
		} else {
			Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_too_many_layers,
					Toast.LENGTH_LONG).show();
		}
		updateButtonResource();
		refreshDrawingSurface();
	}

	public void deleteLayer() {

		int layerCount = layersAdapter.getCount();
		if (layerCount == 1 || currentLayer == null) {
			return;
		}

		int currentPosition = layersAdapter.getPosition(currentLayer.getLayerID());
		int newPosition = currentPosition;
		if (currentPosition == layerCount - 1 && layerCount > 1) {
			newPosition = currentPosition - 1;
		}

		PaintroidApplication.commandManager.commitRemoveLayerCommand(new LayerCommand(currentLayer));
		layersAdapter.removeLayer(currentLayer);
		selectLayer(layersAdapter.getLayer(newPosition));

		if (layersAdapter.checkAllLayerVisible()) {
			Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_invisible,
					Toast.LENGTH_LONG).show();
		}

		updateButtonResource();
		refreshView();
		refreshDrawingSurface();
	}

	public void moveLayer(int layerToMove, int targetPosition) {
		layersAdapter.swapLayer(layerToMove, targetPosition);
		refreshDrawingSurface();
	}

	public void mergeLayer(int firstLayer, int secondLayer) {
		if (layersAdapter.getLayer(firstLayer).getLayerID() != layersAdapter.getLayer(secondLayer).getLayerID()) {
			ArrayList<Integer> layerToMergeIds = new ArrayList<>();
			layerToMergeIds.add(layersAdapter.getLayer(firstLayer).getLayerID());
			layerToMergeIds.add(layersAdapter.getLayer(secondLayer).getLayerID());

			Layer layer = layersAdapter.mergeLayer(layersAdapter.getLayer(firstLayer), layersAdapter.getLayer(secondLayer));

			selectLayer(layer);
			updateButtonResource();
			refreshView();

			PaintroidApplication.commandManager.commitMergeLayerCommand(new LayerCommand(getCurrentLayer(), layerToMergeIds));
			Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_merged,
					Toast.LENGTH_LONG).show();

			refreshDrawingSurface();
		}
	}

	public void resetLayer() {
		Layer layer = layersAdapter.clearLayer();
		selectLayer(layer);
		PaintroidApplication.commandManager.commitAddLayerCommand(new LayerCommand(layer));
		updateButtonResource();
		refreshView();
	}

	public Bitmap getBitmapOfAllLayersToSave() {
		return layersAdapter.getBitmapToSave();
	}

	@Override
	public void onActiveLayerChanged(Layer layer) {
		Log.e(TAG, "onActiveLayerChanged");
		if (currentLayer.getLayerID() != layer.getLayerID()) {
			selectLayer(layer);
		}
	}

	@Override
	public void onLayerDialogRefreshView() {
		Log.d(TAG, "onLayerDialogRefreshView");

		refreshView();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectLayer(layersAdapter.getLayer(position));
		UndoRedoManager.getInstance().update();
	}

	public void refreshDrawingSurface() {
		PaintroidApplication.drawingSurface.refreshDrawingSurface();
	}
}
