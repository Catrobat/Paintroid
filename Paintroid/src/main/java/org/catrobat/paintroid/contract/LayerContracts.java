package org.catrobat.paintroid.contract;

import android.graphics.Bitmap;
import android.view.View;

import org.catrobat.paintroid.controller.DefaultToolController;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.viewholder.BottomNavigationViewHolder;

import java.util.List;
import java.util.ListIterator;

import androidx.annotation.StringRes;

public interface LayerContracts {
	interface Adapter {

		void notifyDataSetChanged();

		LayerViewHolder getViewHolderAt(int position);
	}

	interface Presenter {

		void onBindLayerViewHolderAtPosition(int position, LayerViewHolder viewHolder);

		void refreshLayerMenuViewHolder();

		int getLayerCount();

		Layer getLayerItem(int position);

		long getLayerItemId(int position);

		void addLayer();

		void removeLayer();

		void hideLayer(int position);

		void unhideLayer(int position, LayerViewHolder viewHolder);

		void setAdapter(LayerContracts.Adapter layerAdapter);

		void setDrawingSurface(DrawingSurface drawingSurface);

		void invalidate();

		void setDefaultToolController(DefaultToolController defaultToolController);

		void setbottomNavigationViewHolder(BottomNavigationViewHolder bottomNavigationViewHolder);

		LayerContracts.Presenter getPresenter();
	}

	interface LayerViewHolder {

		void setSelected(int position, BottomNavigationViewHolder bottomNavigationViewHolder, DefaultToolController defaultToolController);

		void setSelected();

		void setDeselected();

		void setBitmap(Bitmap bitmap);

		View getView();

		void setMergable();

		Bitmap getBitmap();

		void setCheckBox(boolean setTo);
	}

	interface LayerMenuViewHolder {

		void disableAddLayerButton();

		void enableAddLayerButton();

		void disableRemoveLayerButton();

		void enableRemoveLayerButton();
	}

	interface Layer {

		Bitmap getBitmap();

		void setBitmap(Bitmap bitmap);

		Bitmap getTransparentBitmap();

		void switchBitmaps(boolean isUnhide);

		void setCheckBox(boolean setTo);

		boolean getCheckBox();
	}

	interface Model {

		List<Layer> getLayers();

		Layer getCurrentLayer();

		void setCurrentLayer(Layer layer);

		int getWidth();

		void setWidth(int width);

		int getHeight();

		void setHeight(int height);

		void reset();

		int getLayerCount();

		Layer getLayerAt(int index);

		int getLayerIndexOf(Layer layer);

		void addLayerAt(int index, Layer layer);

		ListIterator<Layer> listIterator(int index);

		void setLayerAt(int position, Layer layer);

		void removeLayerAt(int position);
	}

	interface Navigator {
		void showToast(@StringRes int id, int length);
	}
}
