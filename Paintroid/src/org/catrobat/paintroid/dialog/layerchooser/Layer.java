/**
 * 
 */
package org.catrobat.paintroid.dialog.layerchooser;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author sven
 * 
 */
public class Layer {
	Canvas mCanvas;
	Color mColor;
	Paint mPaint;

	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	public Layer(Canvas mCanvas, Color col) {
		this.mCanvas = mCanvas;
		this.mColor = col;

	}

	public Layer() {
		this.mCanvas = new Canvas();
		this.mColor = new Color();
	}

	public Layer(Paint p) {
		this.mCanvas = new Canvas();
		this.mColor = new Color();
		this.mPaint = p;
	}

	public Color getmColor() {
		return mColor;
	}

	public void setmColor(Color mColor) {
		this.mColor = mColor;
	}

	public void setmCanvas(Canvas mCanvas) {
		this.mCanvas = mCanvas;
	}

	public Canvas getmCanvas() {
		return this.mCanvas;
	}
}
