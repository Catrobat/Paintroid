package org.catrobat.paintroid.test.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.Method;

// Taken from https://gist.github.com/xrigau/11284124 | changed afterwards
public class SystemAnimations {

	private static final float DISABLED = 0.0f;
	private static final float DEFAULT = 1.0f;

	private final Context context;

	public SystemAnimations(Context context) {
		this.context = context;
	}

	private boolean isPermissionGranted() {
		int permStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.SET_ANIMATION_SCALE);

		return (permStatus == PackageManager.PERMISSION_GRANTED);
	}

	private boolean setPermissionIfGranted(final float value) {
		final boolean permissionIsGranted = isPermissionGranted();
		if (permissionIsGranted) {
			setSystemAnimationsScale(value);
		} else {
			Log.e("SystemAnimations", "SET_ANIMATION_SCALE permission is not granted");
		}

		return permissionIsGranted;
	}

	public void disableAll() {
		setPermissionIfGranted(DISABLED);
	}

	public void enableAll() {
		setPermissionIfGranted(DEFAULT);
	}

	private void setSystemAnimationsScale(float animationScale) {
		try {
			Class<?> windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
			Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder.class);
			Class<?> serviceManagerClazz = Class.forName("android.os.ServiceManager");
			Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
			Class<?> windowManagerClazz = Class.forName("android.view.IWindowManager");
			Method setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales", float[].class);
			Method getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales");

			IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
			Object windowManagerObj = asInterface.invoke(null, windowManagerBinder);
			float[] currentScales = (float[]) getAnimationScales.invoke(windowManagerObj);
			for (int i = 0; i < currentScales.length; i++) {
				currentScales[i] = animationScale;
			}
			setAnimationScales.invoke(windowManagerObj, new Object[]{currentScales});
		} catch (Exception e) {
			Log.e("SystemAnimations", "Could not change animation scale to " + animationScale + " :'(");
		}
	}
}
