package org.catrobat.paintroid.tools;

import android.graphics.Bitmap;

import org.catrobat.paintroid.PaintroidApplication;

public class Layer {
	private static final String LAYER_PREFIX = "Layer ";
	private int layerID;
	private Bitmap bitmap;
	private boolean isSelected;
	private String layerName;
	private boolean isLocked;
	private boolean isVisible;
	private int opacity;

	public Layer(int layerId, Bitmap image) {
		layerID = layerId;
		bitmap = image;
		setSelected(false);
		layerName = LAYER_PREFIX + layerId;
		isLocked = false;
		isVisible = true;
		opacity = 100;
	}

	public boolean getSelected() {
		return isSelected;
	}

	public void setSelected(boolean toSet) {
		isSelected = toSet;
	}

	public int getOpacity() {
		return opacity;
	}

	public void setOpacity(int newOpacity) {
		opacity = newOpacity;
	}

	public int getScaledOpacity() {
		return Math.round((opacity * 255) / 100);
	}

	public boolean getLocked() {
		return isLocked;
	}

	public void setLocked(boolean setTo) {
		isLocked = setTo;
	}

	public boolean getVisible() {
		return isVisible;
	}

	public void setVisible(boolean setTo) {
		isVisible = setTo;
	}

	public String getName() {
		return layerName;
	}

	public void setName(String nameTo) {
		if (nameTo.length() > 0) {
			layerName = nameTo;
		}
	}

	public int getLayerID() {
		return layerID;
	}

	public Bitmap getImage() {
		return bitmap;
	}

	public void setImage(Bitmap image) {
		bitmap = image;

		if (getSelected() && PaintroidApplication.drawingSurface != null) {
			PaintroidApplication.drawingSurface.setBitmap(image);
		}
	}

	public Layer getLayer() {
		return this;
	}
}
