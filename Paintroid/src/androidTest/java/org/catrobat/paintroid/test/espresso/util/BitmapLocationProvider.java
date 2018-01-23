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
	}, MIDDLE_RIGHT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, 1f, .5f);
		}
	}, OUTSIDE_MIDDLE_RIGHT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, 1.5f, .5f);
		}
	}, HALFWAY_RIGHT_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .75f, .5f);
		}
	};

	private static float[] calculatePercentageOffset(View view, float percentageX, float percentageY) {
		DrawingSurface drawingSurface = (DrawingSurface) view;
		float pointX = (drawingSurface.getBitmapWidth() - 1) * percentageX;
		float pointY = drawingSurface.getBitmapHeight() * percentageY;
		return new float[] {pointX, pointY};
	}
}
