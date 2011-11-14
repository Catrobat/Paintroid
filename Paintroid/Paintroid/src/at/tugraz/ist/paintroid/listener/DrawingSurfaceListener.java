package at.tugraz.ist.paintroid.listener;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawingSurfaceListener implements OnTouchListener {

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		// TODO
		// 1. MainActivity.getTool
		// 2. compute coordinates for tool (view -> canvas)
		// 3. call interface action -> tool
		return false;
	}

}
