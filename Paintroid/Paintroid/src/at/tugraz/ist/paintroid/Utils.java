package at.tugraz.ist.paintroid;

import android.content.Context;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;

public class Utils {

	public static Tool createTool(ToolType toolType, Context context) {
		switch (toolType) {
		case BRUSH:
			return new DrawTool(context);
		default:
			break;
		}
		return new DrawTool(context);
	}
}
