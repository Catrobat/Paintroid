/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.utils;

import java.lang.reflect.Field;

public class Reflection {

	public static Object getPrivateField(Object object, String fieldName) {
		if (object == null) {
			throw new IllegalArgumentException("Object is null");
		}

		return Reflection.getPrivateField(object.getClass(), object, fieldName);
	}

	public static Object getPrivateField(Class<?> clazz, String fieldName) {
		return Reflection.getPrivateField(clazz, null, fieldName);
	}

	public static Object getPrivateField(Class<?> clazz, Object object, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void setPrivateField(Object object, String fieldName, Object value) {
		if (object == null) {
			throw new IllegalArgumentException("Object is null");
		}

		Reflection.setPrivateField(object.getClass(), object, fieldName, value);
	}

	public static void setPrivateField(Class<?> fieldOwnerType, String fieldName, Object value) {
		Reflection.setPrivateField(fieldOwnerType, null, fieldName, value);
	}

	public static void setPrivateField(Class<?> fieldOwnerType, Object object, String fieldName, Object value) {
		try {
			Field field = fieldOwnerType.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	// public static Object invokeMethod(Object object, String methodName, Object... parameters) {
	// if (object == null) {
	// throw new IllegalArgumentException("Object is null");
	// }
	//
	// return Reflection.invokeMethod(object.getClass(), object, methodName, parameters);
	// }
	//
	// public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[]
	// parameters) {
	// if (object == null) {
	// throw new IllegalArgumentException("Object is null");
	// }
	//
	// return Reflection.invokeMethod(object.getClass(), object, methodName, parameterTypes, parameters);
	// }
	//
	// public static Object invokeMethod(Class<?> clazz, String methodName, Object... parameters) {
	// return Reflection.invokeMethod(clazz, null, methodName, parameters);
	// }
	//
	// public static Object invokeMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[]
	// parameters) {
	// return Reflection.invokeMethod(clazz, null, methodName, parameterTypes, parameters);
	// }
	//
	// public static Object invokeMethod(Class<?> clazz, Object object, String methodName, Object... parameters) {
	// return Reflection.invokeMethod(clazz, object, methodName, getParameterTypes(parameters), parameters);
	// }
	//
	// public static Object invokeMethod(Class<?> clazz, Object object, String methodName, Class<?>[] parameterTypes,
	// Object[] parameters) {
	// try {
	// Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
	// method.setAccessible(true);
	// return method.invoke(object, parameters);
	// } catch (Exception exception) {
	// throw new RuntimeException(exception);
	// }
	// }
	//
	// public static Class<?>[] getParameterTypes(Object[] parameters) {
	// Class<?>[] arguments = new Class<?>[parameters.length];
	// for (int index = 0; index < parameters.length; index++) {
	// Class<?> currentParameterClass = parameters[index].getClass();
	// if (currentParameterClass == Boolean.class) {
	// currentParameterClass = boolean.class;
	// } else if (currentParameterClass == Byte.class) {
	// currentParameterClass = byte.class;
	// } else if (currentParameterClass == Character.class) {
	// currentParameterClass = char.class;
	// } else if (currentParameterClass == Double.class) {
	// currentParameterClass = double.class;
	// } else if (currentParameterClass == Float.class) {
	// currentParameterClass = float.class;
	// } else if (currentParameterClass == Integer.class) {
	// currentParameterClass = int.class;
	// } else if (currentParameterClass == Long.class) {
	// currentParameterClass = long.class;
	// } else if (currentParameterClass == Short.class) {
	// currentParameterClass = short.class;
	// }
	//
	// arguments[index] = currentParameterClass;
	// }
	//
	// return arguments;
	// }

}
