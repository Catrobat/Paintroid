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
#include <list>

#define  LOG_TAG    "native-lib"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)



//bool isPixelWithinColorTolerance(uint32_t pixel, uint32_t referenceColor) {
//	int redDiff = (int) ((pixel >> 16) & 0xFF) - (int) ((referenceColor >> 16) & 0xFF);
//	int greenDiff = (int) ((pixel >> 8) & 0xFF) - (int) ((referenceColor >> 8) & 0xFF);
//	int blueDiff = (int) (pixel & 0xFF) - (int) (referenceColor & 0xFF);
//	int alphaDiff = (int) (pixel >> 24) - (int) (referenceColor >> 24);
//
//	return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff +
//		   alphaDiff * alphaDiff
//		   <= 60000;
//}

#define DOWN false
#define UP true


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

class Range {
public:
	int line = 0;
	int start = 0;
	int end = 0;
	bool direction = false;

	Range() {
	};

	Range(int line, int start, int end, int direction) {
		this->line = line;
		this->start = start;
		this->end = end;
		this->direction = direction;
	}
};

typedef struct {
	int x = 0;
	int y = 0;
} Point;

class FillAlgorithm {
private:
	jint *pixels;
	Point start_point;
	jint x_size;
	jint y_size;
	jint target_color;
	jint replacement_color;
	jint color_tolerance_squared;
	bool* filled_pixels;

	std::list<Range*> ranges;

	inline int getIndex(int row, int col) {
		return (row*x_size + col);
	}

public:
	FillAlgorithm(jint *pixels, Point start_point, jint x_size, jint y_size, jint target_color, jint replacement_color,
				  jint color_tolerance_squared) {
		this->pixels = pixels;
		this->start_point = start_point;
		this->x_size = x_size;
		this->y_size = y_size;
		this->target_color = target_color;
		this->replacement_color = replacement_color;
		this->color_tolerance_squared = color_tolerance_squared;
		filled_pixels = new bool[x_size*y_size];
	}

	~FillAlgorithm() {
		delete(filled_pixels);
	}

	void run() {
		performFilling();
	}

private:
	bool isPixelWithinColorTolerance(uint32_t pixel, uint32_t referenceColor) {
		int redDiff = (int) ((pixel >> 16) & 0xFF) - (int) ((referenceColor >> 16) & 0xFF);
		int greenDiff = (int) ((pixel >> 8) & 0xFF) - (int) ((referenceColor >> 8) & 0xFF);
		int blueDiff = (int) (pixel & 0xFF) - (int) (referenceColor & 0xFF);
		int alphaDiff = (int) (pixel >> 24) - (int) (referenceColor >> 24);

		return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff +
			   alphaDiff * alphaDiff
			   <= color_tolerance_squared;
	}

	void performFilling() {
		Range *range = generateRangeAndReplaceColor(start_point.y, start_point.x, UP);
		ranges.push_back(range);
		ranges.push_back(new Range(range->line, range->start, range->end, DOWN));

		int row;
		while (!ranges.empty()) {
			range = ranges.front();

			if (range->direction == UP) {
				row = range->line - 1;
				if (row >= 0) {
					checkRangeAndGenerateNewRanges(range, row, UP);
				}
			} else {
				row = range->line + 1;
				if (row < y_size) {
					checkRangeAndGenerateNewRanges(range, row, DOWN);
				}
			}
			ranges.pop_front();
			delete(range);
		}
	}

	Range* generateRangeAndReplaceColor(int row, int col, bool direction) {
		Range *range = new Range();
		int i;
		int start;

		pixels[getIndex(row, col)] = target_color;
		filled_pixels[getIndex(row, col)] = true;

		// TODO: optimize multiple use of same pixels[] and filled_pixels[]
		for (i = col - 1; i >= 0; i--) {
			if (!filled_pixels[getIndex(row, i)] && (pixels[getIndex(row, i)] == replacement_color ||
													 (color_tolerance_squared && isPixelWithinColorTolerance(pixels[getIndex(row, i)], replacement_color)))) {
				pixels[getIndex(row, i)] = target_color;
				filled_pixels[getIndex(row, i)] = true;
			} else {
				break;
			}
		}
		start = i+1;

		for (i = col + 1; i < x_size; i++) {
			if (!filled_pixels[getIndex(row, i)] && (pixels[getIndex(row, i)] == replacement_color ||
													 (color_tolerance_squared && isPixelWithinColorTolerance(pixels[getIndex(row, i)], replacement_color)))) {
				pixels[getIndex(row, i)] = target_color;
				filled_pixels[getIndex(row, i)] = true;
			} else {
				break;
			}
		}

		range->line = row;
		range->start = start;
		range->end = i-1;
		range->direction = direction;

		//mBitmap.setPixels(mPixels[row], start, mWidth, start, row, i - start, 1); // TODO: not needed?

		return range;
	}

	void checkRangeAndGenerateNewRanges(Range *range, int row, bool directionUp) {
		for (int col = range->start; col <= range->end; col++) {
			if (!filled_pixels[getIndex(row, col)] && (pixels[getIndex(row, col)] == replacement_color ||
													   (color_tolerance_squared && isPixelWithinColorTolerance(pixels[getIndex(row, col)], replacement_color)))) {
				Range *newRange = generateRangeAndReplaceColor(row, col, directionUp);
				ranges.push_back(newRange);

				if (newRange->start <= range->start - 2) {
					ranges.push_back(new Range(row, newRange->start, range->start - 2, !directionUp));
				}
				if (newRange->end >= range->end + 2) {
					ranges.push_back(new Range(row, range->end + 2, newRange->end, !directionUp));
				}

				if (newRange->end >= range->end - 1) {
					break;
				} else {
					col = newRange->end + 1;
				}
			}
		}
	}
};


extern "C"
JNIEXPORT void JNICALL
Java_org_catrobat_paintroid_tools_helper_FillAlgorithm_performFilling(JNIEnv *env,
																	  jobject obj,
																	  jintArray arr,
																	  jint x_start,
																	  jint y_start,
																	  jint x_size,
																	  jint y_size,
																	  jint target_color,
																	  jint replacement_color,
																	  jint color_tolerance_squared) {
	jsize len = env->GetArrayLength(arr);
	if (len != (x_size * y_size)) {
		LOGE("Array length is inconsistent with params: %d != %d*%d", len, x_size, y_size);
		return;
	}

	jint *c_ary = env->GetIntArrayElements(arr, 0);
	Point startPoint;
	startPoint.x = x_start;
	startPoint.y = y_start;

	FillAlgorithm fill_algorithm(c_ary, startPoint, x_size, y_size, target_color, replacement_color, color_tolerance_squared);
	fill_algorithm.run();

	env->ReleaseIntArrayElements(arr, c_ary, 0);
}

// TODO: optimizations
// - use union instead of shifting
// - use double linked list insted of list --> no performance increase
// - use pointer to get less getIndex operations