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
		//shadow = new ColorDrawable(Color.LTGRAY);

		Bitmap buffer = LayerListener.getInstance().getAdapter().getLayer(0).getImage();
		greyBitmap = Bitmap.createBitmap(buffer.getWidth(), buffer.getHeight(), buffer.getConfig());
		greyBitmap.eraseColor(Color.LTGRAY);
	}

	public void setDragPos(int pos) {
		dragPosition = pos;
	}

	@Override
	public void onProvideShadowMetrics (Point size, Point touch) {
		shadowBitmap = mergeBitmaps(greyBitmap, LayerListener.getInstance().getAdapter().getLayer(dragPosition).getImage());
		shadow = new BitmapDrawable(shadowBitmap);

		int width, height;

		width = getView().getWidth() / 1;
		height = getView().getHeight() / 1;

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
