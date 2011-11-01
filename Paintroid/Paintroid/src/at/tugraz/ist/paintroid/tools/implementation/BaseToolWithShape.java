package at.tugraz.ist.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.Color;
import at.tugraz.ist.paintroid.tools.ToolWithShape;

public abstract class BaseToolWithShape extends BaseTool implements ToolWithShape {

	protected final int primaryShapeColor = Color.BLACK;
	protected final int secundaryShapeColor = Color.YELLOW;

	public void drawShape(Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
