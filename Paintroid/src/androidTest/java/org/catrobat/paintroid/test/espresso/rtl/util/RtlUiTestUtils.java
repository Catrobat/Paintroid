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

package org.catrobat.paintroid.test.espresso.rtl.util;

public final class RtlUiTestUtils {
	private RtlUiTestUtils() {
		throw new AssertionError();
	}

	public static boolean checkTextDirection(String string) {
		return Character.getDirectionality(string.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT
				|| Character.getDirectionality(string.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	}
}
