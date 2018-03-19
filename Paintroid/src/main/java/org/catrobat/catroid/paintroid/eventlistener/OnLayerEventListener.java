package org.catrobat.catroid.paintroid.eventlistener;

import org.catrobat.catroid.paintroid.tools.Layer;

public interface OnLayerEventListener {
	void onLayerAdded(Layer layer);

	void onLayerRemoved(Layer layer);

	void onLayerMoved(int startPos, int targetPos);
}
