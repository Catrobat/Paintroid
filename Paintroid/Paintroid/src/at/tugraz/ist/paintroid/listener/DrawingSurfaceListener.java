package at.tugraz.ist.paintroid.listener;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.ui.Perspective;
import at.tugraz.ist.paintroid.ui.Toolbar;

public class DrawingSurfaceListener implements OnTouchListener {
	private final Perspective drawingSurfacePerspective;
	private final Toolbar mainActivityToolbar;

	public DrawingSurfaceListener(Perspective perspective, Toolbar toolbar) {
		drawingSurfacePerspective = perspective;
		mainActivityToolbar = toolbar;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		// 1. MainActivity.getTool
		Tool currentTool = mainActivityToolbar.getCurrentTool();
		// 2. compute coordinates for tool (view -> canvas)
		Point coord = new Point((int) event.getX(), (int) event.getY());
		// drawingSurfacePerspective.translateScreenToCanvas(coord);
		PointF coordinate = new PointF(coord);
		// 3. call interface action -> tool
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceListener.onTouch DOWN");
			currentTool.handleDown(coordinate);
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceListener.onTouch MOVE");
			currentTool.handleMove(coordinate);
			break;
		case MotionEvent.ACTION_UP:
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceListener.onTouch UP");
			currentTool.handleUp(coordinate);
			// falls brush und kein move konsumiert
			// currentTool.handleTab(coordinate);
			break;
		}
		return true;
	}

}
