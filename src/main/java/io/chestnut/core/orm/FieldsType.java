package io.chestnut.core.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldsType {
	public final static int BaseType = 1;
	public final static int ListType = 2;
	public final static int MapType = 3;
	public final static int ObjectType = 4;
	
	public FieldsType(String fieldName, Field field) {
		this.field = field;
		this.fieldName = fieldName;
		if(isBaseType(field.getType())) {
			fieldType = BaseType;
		}else if(isList(field.getType())) {
			fieldType = ListType;
		}else if(isMap(field.getType())) {
			fieldType = MapType;
		}else {
			fieldType = ObjectType;
		}
	}
	
	public Field field;
	public String fieldName;
	public int fieldType;

	public static boolean isBaseType(Class<?> className) {
	    if (className.equals(String.class)||
	    	className.equals(Integer.class) || className.equals(int.class) ||
	        className.equals(Byte.class) || className.equals(byte.class) ||
	        className.equals(Long.class) || className.equals(long.class) ||
	        className.equals(Short.class) || className.equals(short.class) ||
	        className.equals(Boolean.class)|| className.equals(boolean.class)) {
	        return true;
	    }
	    return false;
	}
	
	public static boolean isList(Class<?> className) {
	    if (className.equals(List.class) || className.equals(ArrayList.class)) {
	        return true;
	    }
	    return false;
	}
	
	public static boolean isMap(Class<?> className) {
	    if (className.equals(Map.class) || className.equals(HashMap.class)) {
	        return true;
	    }
	    return false;
	}
}

