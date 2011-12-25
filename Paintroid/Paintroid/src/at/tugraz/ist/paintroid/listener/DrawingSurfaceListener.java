package at.tugraz.ist.paintroid.listener;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import at.tugraz.ist.paintroid.ui.Perspective;

public class DrawingSurfaceListener implements OnTouchListener {
	private final Perspective drawingSurfacePerspective;

	public DrawingSurfaceListener(Perspective perspective) {
		drawingSurfacePerspective = perspective;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		// TODO
		// 1. MainActivity.getTool
		// 2. compute coordinates for tool (view -> canvas)
		// 3. call interface action -> tool
		return false;
	}

}
