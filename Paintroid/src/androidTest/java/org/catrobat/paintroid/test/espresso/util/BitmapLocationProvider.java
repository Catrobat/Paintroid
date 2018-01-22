package org.catrobat.paintroid.test.espresso.util;

import android.support.test.espresso.action.CoordinatesProvider;
import android.view.View;

import org.catrobat.paintroid.ui.DrawingSurface;

public enum BitmapLocationProvider implements CoordinatesProvider{
	MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .5f, .5f);
		}
	};

	private static float[] calculatePercentageOffset(View view, float percentageX, float percentageY) {
		DrawingSurface drawingSurface = (DrawingSurface) view;
		float pointX = drawingSurface.getBitmapWidth() * percentageX;
		float pointY = drawingSurface.getBitmapHeight() * percentageY;
		return new float[] {pointX, pointY};
	}
}
