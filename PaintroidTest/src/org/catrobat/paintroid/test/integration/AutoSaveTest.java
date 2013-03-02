package org.catrobat.paintroid.test.integration;

import java.io.File;
import java.io.IOException;

import org.catrobat.paintroid.AutoSave;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.Utils;
import org.catrobat.paintroid.test.utils.Reflection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class AutoSaveTest extends ActivityInstrumentationTestCase2<MainActivity> {

	String mAutoSave = "autosave";
	String mAutoSaveDirectory;
	File autoSaveDir;
	File mTestFile1;
	File mTestFile2;
	File mTestFile3;
	File mAutoSaveFile;

	public AutoSaveTest() throws Exception {
		super(MainActivity.class);
	}

	@Override
	@Before
	public void setUp() {
		int setup = 0;
		Log.d("AutoSaveTest test", "setup " + setup++);
		mAutoSaveDirectory = (String) Reflection.getPrivateField(AutoSave.class, "mAutoSaveDirectory");
		Log.d("AutoSaveTest test", "setup" + setup++);
		autoSaveDir = new File(mAutoSaveDirectory);
		Log.d("AutoSaveTest test", "setup" + setup++);
		if (autoSaveDir != null && autoSaveDir.listFiles() != null) {
			for (File f : autoSaveDir.listFiles()) {
				f.delete();
			}
		}
		Log.d("AutoSaveTest test", "setup " + setup++);
		mTestFile1 = new File(mAutoSaveDirectory + "f1" + ".png");
		mTestFile2 = new File(mAutoSaveDirectory + "f2" + ".png");
		mTestFile3 = new File(mAutoSaveDirectory + "f3" + ".png");
		Log.d("AutoSaveTest test", "setup" + setup++);
		mAutoSaveFile = new File(mAutoSaveDirectory + mAutoSave + ".png");
		Log.d(PaintroidApplication.TAG, "set up end");
	}

	@Override
	@After
	public void tearDown() throws Exception {
		int step = 0;
		Log.i(PaintroidApplication.TAG, "td " + step++);
		Reflection.setPrivateField(AutoSave.class, "mAutoSaveCounter", 0);
		Log.i(PaintroidApplication.TAG, "td " + step++);
		if (autoSaveDir != null && autoSaveDir.listFiles() != null) {
			for (File f : autoSaveDir.listFiles()) {
				f.delete();
			}
		}
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
		super.tearDown();
		Log.i(PaintroidApplication.TAG, "td finish " + step++);
	}

	@Test
	public void testTrigger() {
		int mAutoSaveCounter = (Integer) Reflection.getPrivateField(AutoSave.class, "mAutoSaveCounter");
		assertEquals(0, mAutoSaveCounter);
		AutoSave.trigger();
		mAutoSaveCounter = (Integer) Reflection.getPrivateField(AutoSave.class, "mAutoSaveCounter");
		assertEquals(1, mAutoSaveCounter);
		AutoSave.trigger();
		mAutoSaveCounter = (Integer) Reflection.getPrivateField(AutoSave.class, "mAutoSaveCounter");
		assertEquals(2, mAutoSaveCounter);
		AutoSave.trigger();
		mAutoSaveCounter = (Integer) Reflection.getPrivateField(AutoSave.class, "mAutoSaveCounter");
		assertEquals(3, mAutoSaveCounter);
	}

	@Test
	public void testClear() {
		try {
			mTestFile1.createNewFile();
			mTestFile2.createNewFile();
			mTestFile3.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(3, autoSaveDir.listFiles().length);
		AutoSave.clear(mTestFile1);
		assertEquals(1, autoSaveDir.listFiles().length);
		try {
			mTestFile2.createNewFile();
			mTestFile3.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(3, autoSaveDir.listFiles().length);
		AutoSave.clear();
		assertEquals(0, autoSaveDir.listFiles().length);
	}

	public void testautoSaveImageExists() {
		Activity a = null;
		String location = null;

		assertFalse(AutoSave.autoSaveImageExists(location, a));
		try {
			mAutoSaveFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(AutoSave.autoSaveImageExists(location, a));

		for (File f : autoSaveDir.listFiles()) {
			f.delete();
		}

		String pngPath = "TestPath/for/Image";
		String checksum = Utils.md5Checksum(pngPath);
		String autoSaveFile = mAutoSaveDirectory + checksum;

		assertFalse(AutoSave.autoSaveImageExists(pngPath, a));

		File hashNameAutoSavePicture = new File(autoSaveFile + ".png");
		try {
			hashNameAutoSavePicture.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(AutoSave.autoSaveImageExists(pngPath, a));

		for (File f : autoSaveDir.listFiles()) {
			f.delete();
		}
		assertFalse(AutoSave.autoSaveImageExists(pngPath, a));
	}

	public void testHandleBitmap() {
		String location = "test";
		assertFalse(AutoSave.autoSaveImageExists(location, getActivity()));
		File pngFile = new File(mAutoSaveDirectory + "test" + ".png");
		try {
			pngFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 10; i++)
			AutoSave.trigger();
		assertTrue(AutoSave.autoSaveImageExists(location, getActivity()));
	}
}