/*
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

package org.catrobat.paintroid.tools.options;

import org.catrobat.paintroid.ui.tools.NumberRangeFilter;

public interface TransformToolOptionsView {
	void setWidthFilter(NumberRangeFilter numberRangeFilter);

	void setHeightFilter(NumberRangeFilter numberRangeFilter);

	void setCallback(Callback callback);

	void setWidth(int width);

	void setHeight(int height);

	interface Callback {
		void autoCropClicked();

		void rotateCounterClockwiseClicked();

		void rotateClockwiseClicked();

		void flipHorizontalClicked();

		void flipVerticalClicked();

		void applyResizeClicked(int resizePercentage);

		void setBoxWidth(float boxWidth);

		void setBoxHeight(float boxHeight);

		void hideToolOptions();
	}
}
