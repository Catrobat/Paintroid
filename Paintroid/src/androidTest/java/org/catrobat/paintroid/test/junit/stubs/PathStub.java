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

package org.catrobat.paintroid.test.junit.stubs;

import android.graphics.Path;

import static org.mockito.Mockito.mock;

public final class PathStub extends Path {
	private Path stub;

	public PathStub() {
		super();
		stub = mock(Path.class);
	}

	public Path getStub() {
		return stub;
	}

	@Override
	public void reset() {
		stub.reset();
	}

	@Override
	public void rewind() {
		stub.rewind();
	}

	@Override
	public void moveTo(float x, float y) {
		stub.moveTo(x, y);
	}

	@Override
	public void quadTo(float x1, float y1, float x2, float y2) {
		stub.quadTo(x1, y1, x2, y2);
	}

	@Override
	public void lineTo(float x, float y) {
		stub.lineTo(x, y);
	}
}
