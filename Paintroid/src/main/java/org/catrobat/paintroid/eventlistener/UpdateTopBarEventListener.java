package org.catrobat.paintroid.eventlistener;

/**
 * Created by dzombeast on 13.02.2016.
 */
public interface UpdateTopBarEventListener {
    void onUndoEnabled(boolean enabled);
    void onRedoEnabled(boolean enabled);
}
