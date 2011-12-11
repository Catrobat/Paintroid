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
}
