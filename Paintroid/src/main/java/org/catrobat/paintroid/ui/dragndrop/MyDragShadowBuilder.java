/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.dragndrop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.catrobat.paintroid.listener.LayerListener;

public class MyDragShadowBuilder extends View.DragShadowBuilder {

	private static Drawable shadow;
	private int dragPosition;
	private Bitmap greyBitmap;
	private Bitmap shadowBitmap;

	public MyDragShadowBuilder(View imageView) {
		super(imageView);

		Bitmap buffer = LayerListener.getInstance().getAdapter().getLayer(0).getImage();
		greyBitmap = Bitmap.createBitmap(buffer.getWidth(), buffer.getHeight(), buffer.getConfig());
		greyBitmap.eraseColor(Color.LTGRAY);
	}

	public void setDragPos(int pos) {
		dragPosition = pos;
	}

	@Override
	public void onProvideShadowMetrics(Point size, Point touch) {
		shadowBitmap = mergeBitmaps(greyBitmap, LayerListener.getInstance().getAdapter().getLayer(dragPosition).getImage());
		shadow = new BitmapDrawable(getView().getResources(), shadowBitmap);

		final View view = getView();
		final int width = view.getWidth();
		final int height = view.getHeight();

		shadow.setBounds(0, 0, width, height);

		size.set(width, height);
		touch.set(width / 2, height / 2);
	}

	@Override
	public void onDrawShadow(Canvas canvas) {
		shadow.draw(canvas);
	}

	private Bitmap mergeBitmaps(Bitmap first, Bitmap second) {
		Bitmap bmpOverlay = Bitmap.createBitmap(first.getWidth(), first.getHeight(), first.getConfig());
		Canvas canvas = new Canvas(bmpOverlay);

		Paint overlayPaint = new Paint();

		canvas.drawBitmap(first, new Matrix(), overlayPaint);
		canvas.drawBitmap(second, 0, 0, overlayPaint);

		return bmpOverlay;
	}
}
