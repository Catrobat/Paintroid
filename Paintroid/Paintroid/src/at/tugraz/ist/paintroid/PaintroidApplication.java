package at.tugraz.ist.paintroid;

import android.app.Application;

public class PaintroidApplication extends Application {
	public static final String TAG = "PAINTROID";
	public static final float MOVE_TOLLERANCE = 5;

	@Override
	public void onCreate() {
		super.onCreate();

		// mDisplay = ((WindowManager)
		// getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// int width = display.getWidth();
		// int height = display.getHeight();
	}
}