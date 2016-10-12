package org.catrobat.paintroid.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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


public final class LayerListener implements OnRefreshLayerDialogListener, OnActiveLayerChangedListener, AdapterView.OnItemClickListener {

    private static final String NOT_INITIALIZED_ERROR_MESSAGE = "LayerListener has not been initialized. Call init() first!";
    private static LayerListener instance;
    private LayersAdapter mLayersAdapter;
    private Context mContext;
    private Layer mCurrentLayer;
    private NavigationView mNavigationView;

    private LayerListener(Context context, NavigationView view, Bitmap firstLayer) {
        mContext = context;
        mNavigationView = view;
        mLayersAdapter = new LayersAdapter(context,
                PaintroidApplication.openedFromCatroid, firstLayer);
        InitCurrentLayer();

        ListView listView = (ListView) view.findViewById(R.id.nav_layer_list);
        listView.setAdapter(mLayersAdapter);
        listView.setOnItemClickListener(this);

        ImageButton button = (ImageButton) view.findViewById(R.id.layer_side_nav_button_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(PaintroidApplication.TAG, "add new Layer!");
                createLayer();
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
