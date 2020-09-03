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

package org.catrobat.paintroid.tools;

import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

import androidx.annotation.ColorInt;

public interface ToolPaint {

	Paint getPaint();

	void setPaint(Paint paint);

	Paint getPreviewPaint();

	int getColor();

	void setColor(@ColorInt int color);

	PorterDuffXfermode getEraseXfermode();

	int getPreviewColor();

	float getStrokeWidth();

	void setStrokeWidth(float strokeWidth);

	Paint.Cap getStrokeCap();

	void setStrokeCap(Paint.Cap strokeCap);

	Shader getCheckeredShader();
}
