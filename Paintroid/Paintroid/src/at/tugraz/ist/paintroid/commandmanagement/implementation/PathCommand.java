package at.tugraz.ist.paintroid.commandmanagement.implementation;

import android.graphics.Paint;
import android.graphics.Path;

public class PathCommand extends BaseCommand {

	protected Path path;

	public PathCommand(Paint paint, Path path) {
		super(paint);
		this.path = new Path(path);
	}

	@Override
	protected void draw() {
		// TODO Auto-generated method stub

	}

}
