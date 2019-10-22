package io.chestnut.core.orm;
import java.lang.reflect.Field;
import java.util.List;

public class EntityReflectTable{
	public String tableName;
	public boolean needCache;
	public List<FieldsType> columnFields;
	public Field idField;
	
	public boolean needCache() {
		return false;
	}

	public FieldsType getField(String field) {
		for (FieldsType fieldsType : columnFields) {
			if(fieldsType.fieldName == field) {
				return fieldsType;
			}
		}
		return null;
	}
}