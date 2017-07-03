/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <jni.h>
#include <string>

#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <iostream>

#define  LOG_TAG    "native-lib"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct {
	union {
		struct {
			uint8_t a;
			uint8_t r;
			uint8_t g;
			uint8_t b;
		};
		uint32_t argb;
	};
} Color;

bool isPixelWithinColorTolerance(uint32_t pixel, uint32_t referenceColor) {
	int redDiff = (int) ((pixel >> 16) & 0xFF) - (int) ((referenceColor >> 16) & 0xFF);
	int greenDiff = (int) ((pixel >> 8) & 0xFF) - (int) ((referenceColor >> 8) & 0xFF);
	int blueDiff = (int) (pixel & 0xFF) - (int) (referenceColor & 0xFF);
	int alphaDiff = (int) (pixel >> 24) - (int) (referenceColor >> 24);

	return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff + alphaDiff * alphaDiff
		   <= 60000;
}

extern "C"
JNIEXPORT void JNICALL
Java_org_catrobat_paintroid_command_implementation_FillCommand_performFilling(JNIEnv *env,
																			  jobject obj,
																			  jintArray arr,
																			  jint x_size,
																			  jint y_size) {
	jsize len = env->GetArrayLength(arr);
	if (len != (x_size * y_size)) {
		LOGE("Array length is inconsistent with params: %d != %d*%d", len, x_size, y_size);
		return;
	}

	jint *c_ary = env->GetIntArrayElements(arr, 0);

	jint color_ref = 0xFF00FF00;
	color_ref = 0x3A5C7801;

	for (int y = 0; y < y_size; y++) {
		for (int x = 0; x < x_size; x++) {
			if (isPixelWithinColorTolerance(c_ary[y * x_size + x], color_ref)) {
				c_ary[y * x_size + x] = color_ref;
			}
		}
	}

	env->ReleaseIntArrayElements(arr, c_ary, 0);
}
