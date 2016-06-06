package org.catrobat.paintroid.eventlistener;

import org.catrobat.paintroid.tools.Layer;

/**
 * Created by dzombeast on 13.02.2016.
 */
public interface LayerEventListener
{
    void onLayerAdded(Layer layer);
    void onLayerRemoved(Layer layer);
}
