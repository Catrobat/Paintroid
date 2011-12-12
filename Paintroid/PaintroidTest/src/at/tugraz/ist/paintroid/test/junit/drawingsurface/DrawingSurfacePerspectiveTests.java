package at.tugraz.ist.paintroid.test.junit.drawingsurface;

import junit.framework.TestCase;
import android.view.SurfaceHolder;
import at.tugraz.ist.paintroid.ui.DrawingSurfacePerspective;

public class DrawingSurfacePerspectiveTests extends TestCase {
	protected SurfaceHolder mockSurfaceHolder;
	protected DrawingSurfacePerspective drawingSurfacePerspective;

	@Override
	public void setUp() {
		// mockSurfaceHolder = createMock(SurfaceHolder.class);
		// drawingSurfacePerspective = new DrawingSurfacePerspective(mockSurfaceHolder);
	}

	public void testScalePerspective() {
		drawingSurfacePerspective.scale(2.0f);
	}
}
