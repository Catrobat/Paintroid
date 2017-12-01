/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.utils;

import android.app.Activity;
import android.support.test.runner.AndroidJUnitRunner;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

public class CustomAndroidJUnitRunner extends AndroidJUnitRunner {
	private static final String TAG = CustomAndroidJUnitRunner.class.getSimpleName();
	private static final int MAX_MESSAGE_LENGTH = 4000;
	private static final int NUMBER_OF_RETRIES = 10;

	private AtomicInteger startedActivityCounter = new AtomicInteger(0);

	@Override
	protected void waitForActivitiesToComplete() {
		Log.d(TAG, "......waitForActivitiesToComplete.....");
		for (int i = 0; i < NUMBER_OF_RETRIES && startedActivityCounter.get() > 0; i++) {
			super.waitForActivitiesToComplete();
		}
		Log.d(TAG, "......completed.");
	}

	@Override
	public void callActivityOnStart(Activity activity) {
		startedActivityCounter.incrementAndGet();
		try {
			super.callActivityOnStart(activity);
		} catch (RuntimeException runtimeException) {
			startedActivityCounter.decrementAndGet();
			throw runtimeException;
		}
	}

	@Override
	public void callActivityOnStop(Activity activity) {
		try {
			super.callActivityOnStop(activity);
		} finally {
			startedActivityCounter.decrementAndGet();
		}
	}

	@Override
	protected void dumpThreadStateToOutputs(String outputFileName) {
		String threadState = getThreadState();
		largeErrorLog("THREAD_STATE", threadState);
	}

	private void largeErrorLog(String tag, String message) {
		if (message.length() > MAX_MESSAGE_LENGTH) {
			Log.e(tag, message.substring(0, MAX_MESSAGE_LENGTH));
			largeErrorLog(tag, message.substring(MAX_MESSAGE_LENGTH));
		} else {
			Log.e(tag, message);
		}
	}
}
