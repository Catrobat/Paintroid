/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test.utils;

import java.lang.reflect.Field;

public class PrivateAccess {

	public static Object getMemberValue(Class<?> classFromObject, Object object, String fieldName)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = classFromObject.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(object);
	}

	public static void setMemberValue(Class<?> classFromObject, Object object, String fieldName, Object value)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = classFromObject.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(object, value);
	}

	public static boolean getMemberValueBoolean(Class<?> classFromObject, Object object, String fieldName)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = classFromObject.getDeclaredField(fieldName);
		field.setAccessible(true);

		return field.getBoolean(object);
	}

	public static void setMemberValue(Class<?> classFromObject, Object object, String fieldName, boolean value)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = classFromObject.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.setBoolean(object, value);
		field.setAccessible(false);
	}
}
