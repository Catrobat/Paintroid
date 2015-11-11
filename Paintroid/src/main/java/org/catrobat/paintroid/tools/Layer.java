package org.catrobat.paintroid.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.catrobat.paintroid.MainActivity;

public class Layer {
    private int mLayerID;
    private Bitmap mImage;
    private boolean isSelected;
    private String myName;
    private boolean isLocked;
    private boolean isVisible;
    private int opacity;
    private int opacity_old;
    private Bitmap opacified_bitmap;

    public void setSelected(boolean toSet) {
        isSelected = toSet;
    }
    public boolean getSelected()
    {
        return isSelected;
    }

    public Layer(int layer_id, Bitmap image) {
        mLayerID = layer_id;
        mImage = image;
        setSelected(false);
        myName = "Layer " + layer_id;
        isLocked = false;
        isVisible = true;
        opacity = 100;
    }
    public void setOpacity(int newOpacity){
        opacity = newOpacity;
    }
    public int getOpacity(){ return opacity; }

    public int getScaledOpacity(){
        return Math.round((opacity * 255)/100);
    }

    public void setLocked(boolean setTo) {isLocked = setTo;}
    public void setVisible(boolean setTo)
    {
        isVisible = setTo;
    }
    public boolean getLocked()
    {
        return isLocked;
    }
    public boolean getVisible()
    {
        return isVisible;
    }

    public String getName() {
        return myName;
    }

    public void setName(String nameTo) {
        if(nameTo.length()>0)
        {
            myName = nameTo;
        }
    }

    public int getLayerID() {
        return mLayerID;
    }

    public Bitmap getImage()
    {
        return mImage;
    }

    public void setImage(Bitmap image)
    {
        mImage = image;
    }

    public Layer getLayer() { return this; }

}
