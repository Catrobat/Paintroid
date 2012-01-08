package at.tugraz.ist.paintroid.ui;

import at.tugraz.ist.paintroid.tools.Tool;

public interface Toolbar {
	public Tool getCurrentTool();

	void setTool(Tool tool);
}
