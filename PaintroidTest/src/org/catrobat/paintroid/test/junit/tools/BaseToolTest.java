package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.junit.stubs.CommandManagerStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.junit.After;
import org.junit.Before;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.test.ActivityInstrumentationTestCase2;

public class BaseToolTest extends ActivityInstrumentationTestCase2<MainActivity> {
	protected static final float MOVE_TOLERANCE = BaseTool.MOVE_TOLERANCE;
	private static final int DEFAULT_BRUSH_WIDTH = 25;
	private static final Cap DEFAULT_BRUSH_CAP = Cap.ROUND;
	private static final int DEFAULT_COLOR = Color.BLACK;

	protected Tool mToolToTest;
	protected Paint mPaint;
	protected CommandManagerStub mCommandManagerStub;

	public BaseToolTest() {

		super(MainActivity.class);
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		System.gc();
		mCommandManagerStub = new CommandManagerStub();
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeCap(Cap.ROUND);
		mPaint.setStrokeWidth(mToolToTest.stroke25);
		PaintroidApplication.commandManager = mCommandManagerStub;
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		PaintroidApplication.drawingSurface.setBitmap(Bitmap.createBitmap(1, 1, Config.ALPHA_8));
		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mCanvasPaint"))
				.setStrokeWidth(DEFAULT_BRUSH_WIDTH);
		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mCanvasPaint"))
				.setStrokeCap(DEFAULT_BRUSH_CAP);
		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mCanvasPaint"))
				.setColor(DEFAULT_COLOR);

		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint"))
				.setStrokeWidth(DEFAULT_BRUSH_WIDTH);
		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint"))
				.setStrokeCap(DEFAULT_BRUSH_CAP);
		((Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint"))
				.setColor(DEFAULT_COLOR);
		super.tearDown();
		System.gc();
		Utils.doWorkaroundSleepForDrawingSurfaceThreadProblem();

	}

}
