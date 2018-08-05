package org.catrobat.paintroid.contract;

import android.graphics.Bitmap;
import android.support.annotation.StringRes;
import android.view.View;

import java.util.List;
import java.util.ListIterator;

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

		void onLongClickLayerAtPosition(int position, LayerViewHolder viewHolder);

		void onClickLayerAtPosition(int position, LayerViewHolder viewHolder);

		void setAdapter(LayerContracts.Adapter layerAdapter);

		void invalidate();
	}

	interface LayerViewHolder {

		void setSelected();

		void setDeselected();

		void setBitmap(Bitmap bitmap);

		View getView();

		void setMergable();
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
