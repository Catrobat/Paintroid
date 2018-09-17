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

package org.catrobat.paintroid.test.utils;

import android.graphics.PointF;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;

import static org.mockito.Mockito.doAnswer;

public final class PointFAnswer implements Answer {
	private final float pointX;
	private final float pointY;

	public PointFAnswer(float x, float y) {
		this.pointX = x;
		this.pointY = y;
	}

	@Override
	public Object answer(InvocationOnMock invocation) {
		PointF point = invocation.getArgument(0);
		point.x = pointX;
		point.y = pointY;
		return null;
	}

	public static Stubber setPointFTo(float x, float y) {
		return doAnswer(new PointFAnswer(x, y));
	}
}
