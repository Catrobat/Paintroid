package at.tugraz.ist.paintroid.listener;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.tools.Tool;
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
		Tool currentTool = MainActivity.getCurrentTool();
		// 2. compute coordinates for tool (view -> canvas)
		Point coord = new Point((int) event.getX(), (int) event.getY());
		drawingSurfacePerspective.translateScreenToCanvas(coord);
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
		case MotionEvent.ACTION_CANCEL:
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceListener.onTouch UP");
			currentTool.handleUp(coordinate);
			// falls brush und kein move konsumiert
			// currentTool.handleTab(coordinate);
			break;

		}
		return true;
	}

}
