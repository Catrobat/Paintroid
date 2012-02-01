package at.tugraz.ist.paintroid.listener;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.ui.Perspective;

public class DrawingSurfaceListener implements OnTouchListener {
	private final Perspective drawingSurfacePerspective;

	public DrawingSurfaceListener(Perspective perspective) {
		drawingSurfacePerspective = perspective;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		// 1. MainActivity.getTool
		// 2. compute coordinates for tool (view -> canvas)
		Point coord = new Point((int) event.getX(), (int) event.getY());
		drawingSurfacePerspective.translateScreenToCanvas(coord);
		PointF coordinate = new PointF(coord);
		// 3. call interface action -> tool
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceListener.onTouch DOWN");
			PaintroidApplication.CURRENT_TOOL.handleDown(coordinate);
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.d(PaintroidApplication.TAG, "DrawingSurfaceListener.onTouch MOVE");
			PaintroidApplication.CURRENT_TOOL.handleMove(coordinate);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceListener.onTouch UP");
			PaintroidApplication.CURRENT_TOOL.handleUp(coordinate);
			// falls brush und kein move konsumiert
			// currentTool.handleTab(coordinate);
			break;

		}
		return true;
	}

}
