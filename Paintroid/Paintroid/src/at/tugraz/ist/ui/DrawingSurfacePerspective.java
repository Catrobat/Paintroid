package at.tugraz.ist.ui;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * The purpose of this class is to provide an independent interface to manipulate the scale and
 * translation of the DrawingSurface. The direct manipulation of the Canvas is synchronized on the
 * SurfaceHolder on which the DrawingSurface must also synchronize its own drawing.
 */
public class DrawingSurfacePerspective {
	public static final float MIN_SCALE = 0.5f;

	private final SurfaceHolder surfaceHolder;
	private final PointF surfaceCenter;
	private final PointF surfaceTranslation;
	private final Rect surfaceFrame;
	private float surfaceScale;

	/**
	 * Initialize a Perspective object with the SurfaceHolder of the DrawingSurface.
	 * 
	 * @param surfaceHolder SurfaceHolder holding the DrawingSurface's Canvas.
	 */
	public DrawingSurfacePerspective(SurfaceHolder holder) {
		surfaceHolder = holder;
		surfaceFrame = holder.getSurfaceFrame();
		surfaceCenter = new PointF(surfaceFrame.exactCenterX(), surfaceFrame.exactCenterY());
		surfaceTranslation = new PointF(0, 0);
	}

	/**
	 * Apply a scale to the DrawingSurface's Canvas.
	 * 
	 * @param scale The amount to scale [1.0...*]
	 */
	public void scale(float scale) {
		if (scale >= MIN_SCALE) {
			surfaceScale = scale;
		} else {
			surfaceScale = MIN_SCALE;
		}
		applyToCanvas();
	}

	/**
	 * Performs a translation on the DrawingSurface's Canvas. The change is additive.
	 * 
	 * @param dy Translation-offset in x.
	 * @param dy Translation-offset in y.
	 */
	public void translate(float dx, float dy) {
		surfaceTranslation.offset(Math.round(dx / surfaceScale), Math.round(dy / surfaceScale));
		applyToCanvas();
	}

	private void applyToCanvas() {
		synchronized (surfaceHolder) {
			Canvas canvas = null;
			try {
				canvas = surfaceHolder.lockCanvas();
				canvas.scale(surfaceScale, surfaceScale, surfaceCenter.x, surfaceCenter.y);
				canvas.translate(surfaceTranslation.x, surfaceTranslation.y);
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	/**
	 * Translates screen-coordinates to coordinates on the SurfaceHolder's Canvas.
	 * 
	 * @param coords Screen-coordinates that will be translated.
	 */
	public void translateScreenToCanvas(Point coords) {
		coords.x = (int) ((coords.x - surfaceCenter.x) / surfaceScale + surfaceCenter.x - surfaceTranslation.x);
		coords.y = (int) ((coords.y - surfaceCenter.y) / surfaceScale + surfaceCenter.y - surfaceTranslation.y);
	}
}
