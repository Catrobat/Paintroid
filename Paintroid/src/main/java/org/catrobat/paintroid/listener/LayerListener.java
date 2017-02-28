package org.catrobat.paintroid.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
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
    private static LayerListener instance;
    private LayersAdapter mLayersAdapter;
    private Context mContext;
    private Layer mCurrentLayer;
    private NavigationView mNavigationView;
	private BrickDragAndDropLayerMenu brickLayer;
	private ImageView imageView;

    private LayerListener(Context context, NavigationView view, Bitmap firstLayer) {
		Log.e("---Constuctor called: ", "LayerListener ---");
        mContext = context;
        mNavigationView = view;
        mLayersAdapter = new LayersAdapter(context,
                PaintroidApplication.openedFromCatroid, firstLayer);
        InitCurrentLayer();

        final ListView listView = (ListView) view.findViewById(R.id.nav_layer_list);

		brickLayer = new BrickDragAndDropLayerMenu(listView);
		OnDragListener dragListener = new OnDragListener(brickLayer);

        listView.setAdapter(mLayersAdapter);
        listView.setOnItemClickListener(this);
		listView.setOnDragListener(dragListener);
		listView.setLongClickable(true);



		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView v, View arg1, int pos, long id) {

				//int[] colors = {0, 0xFFFF0000, 0};
				//listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors));
				//listView.setDivider(new ColorDrawable(0x99F10529));
				//listView.setDivider(new ColorDrawable(0x99F10529));
				//listView.setDividerHeight(3);

				//listView.getChildAt(pos).setBackgroundColor(Color.YELLOW);
				//listView.getChildAt(pos).setVisibility(View.INVISIBLE);
				listView.getChildAt(pos).setAlpha((float)0.5);

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
                Log.e(PaintroidApplication.TAG, "add new Layer!");
                createLayer();
            }
        });
        ImageButton delButton = (ImageButton) view.findViewById(R.id.layer_side_nav_button_delete);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(PaintroidApplication.TAG, "delete Layer!");
                deleteLayer();
            }
        });

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

    public void createLayer() {
        boolean success = mLayersAdapter.addLayer();
        Layer layer = mLayersAdapter.getLayer(0);
        selectLayer(layer);
        //refreshView();

        if (!success) {
            Toast.makeText(PaintroidApplication.applicationContext, R.string.layer_too_many_layers,
                    Toast.LENGTH_LONG).show();
        }

        PaintroidApplication.commandManager.commitAddLayerCommand(new LayerCommand(layer));
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
			refreshView();

			PaintroidApplication.commandManager.commitMergeLayerCommand(new LayerCommand(getCurrentLayer(), layerToMergeIds));
			//PaintroidApplication.commandManager.commitMergeLayerCommand(new LayerCommand(mLayersAdapter.getLayer(firstLayer), layerToMergeIds));
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
        //refreshView();
    }
}
