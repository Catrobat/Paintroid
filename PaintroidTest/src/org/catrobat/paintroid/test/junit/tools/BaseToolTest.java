package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.junit.stubs.BrushPickerStub;
import org.catrobat.paintroid.test.junit.stubs.ColorPickerStub;
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

	protected Tool mToolToTest;
	protected Paint mPaint;
	protected ColorPickerStub mColorPickerStub;
	protected BrushPickerStub mBrushPickerStub;
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
		mColorPickerStub = new ColorPickerStub(getActivity(), null);
		PrivateAccess.setMemberValue(BaseTool.class, mToolToTest, "mColorPickerDialog", mColorPickerStub);
		mBrushPickerStub = new BrushPickerStub(getActivity(), null, mPaint);
		PrivateAccess.setMemberValue(BaseTool.class, mToolToTest, "mBrushPickerDialog", mBrushPickerStub);
		PaintroidApplication.COMMAND_MANAGER = mCommandManagerStub;
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		PaintroidApplication.DRAWING_SURFACE.setBitmap(Bitmap.createBitmap(1, 1, Config.ALPHA_8));
		super.tearDown();
		System.gc();
		Utils.doWorkaroundSleepForDrawingSurfaceThreadProblem();

	}

}
