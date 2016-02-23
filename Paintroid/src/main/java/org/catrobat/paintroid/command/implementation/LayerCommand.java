package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.provider.Telephony;

public class LayerCommand extends BaseCommand {

    private LayerAction mLayerAction;
    private int mLayerID;

    public enum LayerAction{
        ADD,
        REMOVE,
        MERGE
    }

    public LayerCommand(LayerAction layerAction)
    {
        switch (layerAction)
        {
            case ADD:
                mLayerAction = LayerAction.ADD;
                break;
            case REMOVE:
                mLayerAction = LayerAction.REMOVE;
                break;
            case MERGE:
                mLayerAction = LayerAction.MERGE;
                break;
            default:
                break;
        }
    }
    @Override
    public void run(Canvas canvas, Bitmap bitmap) {
        notifyStatus(NOTIFY_STATES.COMMAND_STARTED);






        setChanged();

        notifyStatus(NOTIFY_STATES.COMMAND_DONE);
    }
}
