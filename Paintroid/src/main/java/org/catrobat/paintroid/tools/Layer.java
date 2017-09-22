package org.catrobat.paintroid.tools;

import android.graphics.Bitmap;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

public class Layer {
	private static final String LAYER_PREFIX = PaintroidApplication.applicationContext.
			getResources().getString(R.string.layer_prefix);
	private int mLayerID;
	private Bitmap mBitmap;
	private boolean mIsSelected;
	private String mLayerName;
	private boolean mIsLocked;
	private boolean mIsVisible;
	private int mOpacity;

	public Layer(int layer_id, Bitmap image) {
		mLayerID = layer_id;
		mBitmap = image;
		setSelected(false);
		mLayerName = LAYER_PREFIX + layer_id;
		mIsLocked = false;
		mIsVisible = true;
		mOpacity = 100;
	}

	public void setSelected(boolean toSet) {
		mIsSelected = toSet;
	}

	public boolean getSelected() {
		return mIsSelected;
	}

	public void setOpacity(int newOpacity) {
		mOpacity = newOpacity;
	}

	public int getOpacity() {
		return mOpacity;
	}

	public int getScaledOpacity() {
		return Math.round((mOpacity * 255) / 100);
	}

	public void setLocked(boolean setTo) {
		mIsLocked = setTo;
	}

	public void setVisible(boolean setTo) {
		mIsVisible = setTo;
	}

	public boolean getLocked() {
		return mIsLocked;
	}

	public boolean getVisible() {
		return mIsVisible;
	}

	public String getName() {
		return mLayerName;
	}

	public void setName(String nameTo) {
		if (nameTo.length() > 0) {
			mLayerName = nameTo;
		}
	}

	public int getLayerID() {
		return mLayerID;
	}

	public Bitmap getImage() {
		return mBitmap;
	}

	public void setImage(Bitmap image) {
		mBitmap = image;

	}

	public Layer getLayer() {
		return this;
	}

}
