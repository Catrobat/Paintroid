package org.catrobat.paintroid.eventlistener;

import org.catrobat.paintroid.tools.Layer;

public interface OnLayerEventListener {
	void onLayerAdded(Layer layer);

	void onLayerRemoved(Layer layer);

	void onLayerMoved(int startPos, int targetPos);
}
