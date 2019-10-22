package io.chestnut.core.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;



public class EntityReflectMrg {
    public void addCache(Object returnObj) {
		
	}
    
	public HashMap<String, EntityReflectTable> ref = new HashMap<>();
	
	public EntityReflectTable initEntityClazz(Class<?> entityClazz) throws Exception {
		Entity entity = entityClazz.getAnnotation(Entity.class);
		String className = entityClazz.getName();
		if(ref.get(className) != null) {
			throw new Exception(className + " error");
		}
		EntityReflectTable entityReflectTable = new EntityReflectTable();

		if(entity == null) {
			entityReflectTable.tableName = entityClazz.getSimpleName();
		}else if(entity.name() != null && !entity.name().equals("")) {
			entityReflectTable.tableName = entity.name();
		}else {
			entityReflectTable.tableName = entityClazz.getSimpleName();
		}
		
		List<Field> fieldList = new ArrayList<>() ;
		Class<?> tempClass = entityClazz;
		while (tempClass != null && ! tempClass.getName().toLowerCase().equals("java.lang.object")) {
		      fieldList.addAll(Arrays.asList(tempClass .getDeclaredFields()));
		      tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
		}
		entityReflectTable.columnFields = new ArrayList<>(fieldList.size()) ;
		for (Field field : fieldList) {
			Id id = field.getAnnotation(Id.class);
			if(id != null) {
				entityReflectTable.idField = field;
				field.setAccessible(true);
				continue;
			}
			Column column = field.getAnnotation(Column.class);
			if(column == null) {
				continue;
			}
			String columnName = field.getName();
			if(column.name() != null&&!column.name().equals("")) {
				columnName = column.name();
			}
			field.setAccessible(true);
			entityReflectTable.columnFields.add(new FieldsType(columnName,field));
		}
		ref.put(className, entityReflectTable);
		return entityReflectTable;
		
	}
	public String getTableName(Class<?> entityClazz) throws Exception {
		EntityReflectTable entityReflectTable = ref.get(entityClazz.getName());
		if(entityReflectTable != null) {
			return entityReflectTable.tableName;
		}
		entityReflectTable = initEntityClazz(entityClazz);
		return entityReflectTable.tableName;
	}

	public List<FieldsType> getColumnFields(Class<?> entityClazz) throws Exception {
		EntityReflectTable entityReflectTable = ref.get(entityClazz.getName());
		if(entityReflectTable != null) {
			return entityReflectTable.columnFields;
		}
		entityReflectTable = initEntityClazz(entityClazz);
		return entityReflectTable.columnFields;
	}
	public Field getIdField(Class<?> entityClazz) throws Exception {
		EntityReflectTable entityReflectTable = ref.get(entityClazz.getName());
		if(entityReflectTable != null) {
			return entityReflectTable.idField;
		}
		entityReflectTable = initEntityClazz(entityClazz);
		return entityReflectTable.idField;
	}
	public FieldsType getField(Class<?> entityClass, String field) throws Exception {
		EntityReflectTable entityReflectTable = ref.get(entityClass.getName());
		if(entityReflectTable != null) {
			return entityReflectTable.getField(field);
		}
		entityReflectTable = initEntityClazz(entityClass);
		return entityReflectTable.getField(field);
	}
	public EntityReflectTable getEntityReflectTable(Class<?> entityClazz) throws Exception {
		EntityReflectTable entityReflectTable = ref.get(entityClazz.getName());
		if(entityReflectTable != null) {
			return entityReflectTable;
		}
		entityReflectTable = initEntityClazz(entityClazz);
		return entityReflectTable;
	}

	
}
