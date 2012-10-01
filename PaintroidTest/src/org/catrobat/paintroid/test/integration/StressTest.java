/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.test.integration;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.junit.Test;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.util.Log;

public class StressTest extends BaseIntegrationTestClass {

	public StressTest() throws Exception {
		super();
	}

	@Test
	public void testPaintroidStressTest1() {
		stressTest("stress test 1:");
	}

	private void stressTest(String prefix) {
		final long startTime = System.currentTimeMillis();
		DecimalFormat decimalFormat = new DecimalFormat("00.00");
		final long maxLoopingTime = 110000;
		final int maxRandomSleep = 250;
		Random randomGenerator = new Random();
		int sleep = randomGenerator.nextInt(500);
		final int maxRuns = 100;
		for (int currentRun = 0; currentRun < maxRuns && (System.currentTimeMillis() - startTime) < maxLoopingTime; currentRun++) {
			Log.i("Paintroid", prefix + "Current run:" + currentRun);
			try {
				tearDown();
				System.gc();
				Log.i("Paintroid", prefix + "Current run " + currentRun + " teaDown sleeping" + sleep);
				Thread.sleep(sleep);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("Paintroid", prefix + "Current run " + currentRun + " teaDown done");
			sleep = randomGenerator.nextInt(maxRandomSleep);
			try {
				setUp();
				Log.i("Paintroid", prefix + "Current run " + currentRun + " suetUP sleeping " + sleep);
				// Thread.sleep(sleep);
				// memoryLogger();
				int numberOfTool = ToolType.values().length;
				int toolToSelect = currentRun % numberOfTool;
				if (ToolType.values()[toolToSelect] == ToolType.CROP
						|| ToolType.values()[toolToSelect] == ToolType.IMPORTPNG) {
					selectTool(ToolType.CURSOR);
					mSolo.sleep(1000);
					selectTool(ToolType.BRUSH);
				} else {
					selectTool(ToolType.values()[toolToSelect]);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("Paintroid", prefix + "Current run " + currentRun + " setup done");
			Log.i("Paintroid",
					prefix + "Current run " + currentRun + " total time consumed: "
							+ decimalFormat.format((System.currentTimeMillis() - startTime) / 1000.0));
		}
	}

	// http://stackoverflow.com/questions/2298208/how-to-discover-memory-usage-of-my-application-in-android
	protected void memoryLogger() {
		ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);

		Log.i(PaintroidApplication.TAG, " memoryInfo.availMem " + memoryInfo.availMem);
		Log.i(PaintroidApplication.TAG, " memoryInfo.lowMemory " + memoryInfo.lowMemory);
		Log.i(PaintroidApplication.TAG, " memoryInfo.threshold " + memoryInfo.threshold);

		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

		Map<Integer, String> pidMap = new TreeMap<Integer, String>();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			pidMap.put(runningAppProcessInfo.pid, runningAppProcessInfo.processName);
		}

		Collection<Integer> keys = pidMap.keySet();

		for (int key : keys) {
			int pids[] = new int[1];
			pids[0] = key;
			android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pids);
			for (android.os.Debug.MemoryInfo pidMemoryInfo : memoryInfoArray) {
				Log.i(PaintroidApplication.TAG,
						String.format("** MEMINFO in pid %d [%s] **\n", pids[0], pidMap.get(pids[0])));
				Log.i(PaintroidApplication.TAG,
						" pidMemoryInfo.getTotalPrivateDirty(): " + pidMemoryInfo.getTotalPrivateDirty());
				Log.i(PaintroidApplication.TAG, " pidMemoryInfo.getTotalPss(): " + pidMemoryInfo.getTotalPss());
				Log.i(PaintroidApplication.TAG,
						" pidMemoryInfo.getTotalSharedDirty(): " + pidMemoryInfo.getTotalSharedDirty());
			}
		}
	}

}
