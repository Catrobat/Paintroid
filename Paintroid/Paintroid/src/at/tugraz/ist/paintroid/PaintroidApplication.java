package at.tugraz.ist.paintroid;

import android.app.Application;
import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;
import at.tugraz.ist.paintroid.tools.Tool;

public class PaintroidApplication extends Application {
	public static final String TAG = "PAINTROID";
	public static final float MOVE_TOLLERANCE = 5;

	public static CommandHandler COMMAND_HANDLER;
	public static Tool CURRENT_TOOL;

	@Override
	public void onCreate() {
		super.onCreate();

		// mDisplay = ((WindowManager)
		// getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// int width = display.getWidth();
		// int height = display.getHeight();
	}
}