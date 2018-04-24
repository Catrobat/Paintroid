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
#include <cstring>
#include <iostream>
#include <list>

#include <android/log.h>

#define  LOG_TAG    "native-lib"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define DOWN false
#define UP true


typedef struct {
	union {
		uint32_t argb;
		struct {
			uint8_t b;
			uint8_t g;
			uint8_t r;
			uint8_t a;
		};
	};
} Color;

class Range {
public:
	int line;
	int start;
	int end;
	bool direction;

	Range() {
		this->line = 0;
		this->start = 0;
		this->end = 0;
		this->direction = false;
	}

	Range(int line, int start, int end, bool direction) {
		this->line = line;
		this->start = start;
		this->end = end;
		this->direction = direction;
	}
};

typedef struct {
	int x;
	int y;
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
		memset(filled_pixels, false, x_size*y_size);
	}

	~FillAlgorithm() {
		delete(filled_pixels);
	}

	void run() {
		performFilling();
	}

private:
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

		int index = getIndex(row, col);
		jint *pixel_ptr = pixels + index;
		bool *filled_ptr = filled_pixels + index;

		*pixel_ptr = target_color;
		*filled_ptr = true;

		for (i = col - 1; i >= 0; i--) {
			pixel_ptr--;
			filled_ptr--;
			if (!*filled_ptr && (*pixel_ptr == replacement_color ||
					(color_tolerance_squared && isPixelWithinColorTolerance(*pixel_ptr, replacement_color)))) {
				*pixel_ptr = target_color;
				*filled_ptr = true;
			} else {
				break;
			}
		}
		start = i+1;

		pixel_ptr = pixels + index;
		filled_ptr = filled_pixels + index;
		for (i = col + 1; i < x_size; i++) {
			pixel_ptr++;
			filled_ptr++;
			if (!*filled_ptr && (*pixel_ptr == replacement_color ||
					(color_tolerance_squared && isPixelWithinColorTolerance(*pixel_ptr, replacement_color)))) {
				*pixel_ptr = target_color;
				*filled_ptr = true;
			} else {
				break;
			}
		}

		range->line = row;
		range->start = start;
		range->end = i-1;
		range->direction = direction;

		return range;
	}

	void checkRangeAndGenerateNewRanges(Range *range, int row, bool directionUp) {
		int index = getIndex(row, range->start) - 1;
		jint *pixel_ptr = pixels + index;
		bool *filled_ptr = filled_pixels + index;
		for (int col = range->start; col <= range->end; col++) {
			pixel_ptr++;
			filled_ptr++;
			if (!*filled_ptr && (*pixel_ptr == replacement_color ||
					(color_tolerance_squared && isPixelWithinColorTolerance(*pixel_ptr, replacement_color)))) {
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
					int skip = newRange->end + 1 - col;
					pixel_ptr += skip;
					filled_ptr += skip;
					col += skip;
				}
			}
		}
	}

	bool isPixelWithinColorTolerance(uint32_t pixel_color, uint32_t reference_color) {
		Color pixel = { pixel_color };
		Color referenceColor = { reference_color };
		int redDiff = pixel.r - referenceColor.r;
		int greenDiff = pixel.g - referenceColor.g;
		int blueDiff = pixel.b - referenceColor.b;
		int alphaDiff = pixel.a - referenceColor.a;

		return redDiff*redDiff + greenDiff*greenDiff + blueDiff*blueDiff +
			   alphaDiff * alphaDiff
			   <= color_tolerance_squared;
	}
};


extern "C"
JNIEXPORT void JNICALL
Java_org_catrobat_paintroid_command_implementation_FillCommand_performFilling(JNIEnv *env,
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
