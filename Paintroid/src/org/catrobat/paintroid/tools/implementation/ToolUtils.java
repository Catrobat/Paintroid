package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;

import android.content.Context;
import android.util.Log;

final class ToolUtils {

	static final float getStrokeWidthForZoom(final float defaultStrokeWidth,
			float minStrokeWidth, final float maxStrokeWidth,
			final Context context) {
		Log.i(PaintroidApplication.TAG, "Base Tool getStrokeWidthForZoom");
		float displayScale = context.getResources().getDisplayMetrics().density;
		Log.i(PaintroidApplication.TAG, "Base Tool getStrokeWidthForZoom 1");
		float strokeWidth = (defaultStrokeWidth * displayScale)
				/ PaintroidApplication.CURRENT_PERSPECTIVE.getScale();
		Log.i(PaintroidApplication.TAG,
				"Base Tool getStrokeWidthForZoom 2 strokeWidth:" + strokeWidth
						+ " minStrokeWidth:" + minStrokeWidth
						+ " maxStrokeWidth:" + maxStrokeWidth);
		if (strokeWidth < minStrokeWidth) {
			Log.i(PaintroidApplication.TAG,
					"Base Tool getStrokeWidthForZoom 2 strokeWidth < minStrokeWidth-> strokeWidth:"
							+ strokeWidth + " minStrokeWidth:" + minStrokeWidth
							+ " maxStrokeWidth:" + maxStrokeWidth);
			strokeWidth = minStrokeWidth;
		}
		Log.i(PaintroidApplication.TAG, "Base Tool getStrokeWidthForZoom 3");
		if (strokeWidth > maxStrokeWidth) {
			Log.i(PaintroidApplication.TAG,
					"Base Tool getStrokeWidthForZoom 3 strokeWidth > maxStrokeWidth-> strokeWidth:"
							+ strokeWidth + " minStrokeWidth:" + minStrokeWidth
							+ " maxStrokeWidth:" + maxStrokeWidth);
			strokeWidth = maxStrokeWidth;
		}
		Log.i(PaintroidApplication.TAG, "Base Tool getStrokeWidthForZoom 4");
		return strokeWidth;
	}

	static final float getInverselyProportionalSizeForZoom(
			final float defaultSize, final Context context) {
		float displayScale = context.getResources().getDisplayMetrics().density;
		float applicationScale = PaintroidApplication.CURRENT_PERSPECTIVE
				.getScale();
		return (defaultSize * displayScale) / applicationScale;
	}

}
