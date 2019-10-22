package io.chestnut.core.orm;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

public class MongoMappingConverter {
	
	private EntityReflectMrg entityReflectMrg;
	
	public EntityReflectMrg getEntityReflectMrg() {
		return entityReflectMrg;
	}

	public void setEntityReflectMrg(EntityReflectMrg entityReflectMrg) {
		this.entityReflectMrg = entityReflectMrg;
	}


	public static Object getBaseObj(Class<?> clazz, Object value) {
		if(value instanceof String) {
			String v = (String) value;
			if(clazz.equals(Integer.class)||clazz.equals(int.class)) {
				return Integer.valueOf(v);
			}
			if(clazz.equals(Long.class)||clazz.equals(long.class)) {
				return Long.valueOf(v);
			}
			if(clazz.equals(Short.class)||clazz.equals(short.class)) {
				return Short.valueOf(v);
			}	
			if(clazz.equals(String.class)) {
				return v;
			}
		}
		if(value instanceof Integer) {
			Integer v = (Integer) value;
			int vx = v;
			if(clazz.equals(Integer.class)||clazz.equals(int.class)) {
				return v;
			}
			if(clazz.equals(Long.class)||clazz.equals(long.class)) {
				return Long.valueOf(v);
			}
			if(clazz.equals(Short.class)||clazz.equals(short.class)) {
				return (short)(vx);
			}	
			if(clazz.equals(String.class)) {
				return String.valueOf(v);
			}
		}
		if(value instanceof Long) {
			return value;
		}
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Object> readList(ParameterizedType parameterizedType, List<Object> list) throws Exception {
		ArrayList<Object> valueList = new ArrayList<>(list.size());
		Type listType = parameterizedType.getActualTypeArguments()[0];
		if(listType instanceof ParameterizedType) { //list嵌套复杂对象
			for (Object listObject : list) {
				if(listObject instanceof Document) {
					Object valueObj = readMap((ParameterizedType)listType, (Document)listObject);
					valueList.add(valueObj);
				}else {
					Object valueObj = readList((ParameterizedType)listType, (List<Object>)listObject);
					valueList.add(valueObj);
				}
				
			}
		}else {
			Class<?> simpleClass = (Class<?>) listType;
			if(FieldsType.isBaseType(simpleClass)) {
				for (Object listObject : list) {
					Object valueObj = getBaseObj(simpleClass, listObject);
					valueList.add(valueObj);
				}
			}else {
				for (Object listObject : list) {
					Object valueObj = documentToObj(simpleClass, (Document)listObject);
					valueList.add(valueObj);
				}
			}
			
		}
		return valueList;
	}
	@SuppressWarnings("unchecked")
	public HashMap<Object,Object> readMap(ParameterizedType parameterizedType, Document document) throws Exception {
		HashMap<Object, Object> map = new HashMap<>();
		Class<?> keyClassz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		Type valueClassz = parameterizedType.getActualTypeArguments()[1];
		
		if(valueClassz instanceof Class) {
			Class<?> simpleClass = (Class<?>) valueClassz;
			if(FieldsType.isBaseType(simpleClass)) {
				for(Map.Entry<?, ?> entry : document.entrySet()){
					Object mapKey = entry.getKey();
					Object mapValue = entry.getValue();
					Object keyObj = getBaseObj(keyClassz, mapKey);
					Object mapObj = getBaseObj(simpleClass, mapValue);
					map.put(keyObj, mapObj);
				}
			}else {
				for(Map.Entry<?, ?> entry : document.entrySet()){
					Object mapKey = entry.getKey();
					Object mapValue = entry.getValue();
					Object keyObj = getBaseObj(keyClassz, mapKey);
					Object mapObj = documentToObj(simpleClass, (Document)mapValue);
					map.put(keyObj, mapObj);
				}
				
			}
		}else {
			ParameterizedType valueParameterizedType = (ParameterizedType) valueClassz;
			Class<?> rawTypeClazz  = (Class<?>) valueParameterizedType.getRawType();
			if(FieldsType.isList(rawTypeClazz)) {
				for(Map.Entry<?, ?> entry : document.entrySet()){
					Object mapKey = entry.getKey();
					Object mapValue = entry.getValue();
					Object keyObj = getBaseObj(keyClassz, mapKey);
					Object mapObj = readList(valueParameterizedType, (List<Object>)mapValue);
					map.put(keyObj, mapObj);
				}
			}else if(FieldsType.isMap(rawTypeClazz)){
				for(Map.Entry<?, ?> entry : document.entrySet()){
					Object mapKey = entry.getKey();
					Object mapValue = entry.getValue();
					Object keyObj = getBaseObj(keyClassz, mapKey);
					Object mapObj = readMap(valueParameterizedType, (Document)mapValue);
					map.put(keyObj, mapObj);
				}
			}else {
				throw new Exception("un rawTypeClazz" + rawTypeClazz.getName());
			}
			
		}
		return map;
	}

	
	@SuppressWarnings("unchecked")
	public  <T> T documentToObj(Class<T> clazz, Document document) throws Exception {
		Object newIns = clazz.newInstance();
		if(document.getString("_id") != null) {
			Field idField = getEntityReflectMrg().getIdField(clazz);
			idField.set(newIns, document.getString("_id"));
		}
		for (FieldsType fieldsType : getEntityReflectMrg().getColumnFields(clazz)) {
			Object objectValue = document.get(fieldsType.fieldName);
			if(objectValue == null) {
				continue;
			}
			Class<?> fieldClassz =  fieldsType.field.getType();
			switch (fieldsType.fieldType) {
			case FieldsType.ObjectType:
				fieldsType.field.set(newIns, documentToObj(fieldClassz, (Document) objectValue));
				break;
			case FieldsType.BaseType:
				Object value = getBaseObj(fieldClassz, objectValue);
				fieldsType.field.set(newIns, value);
				break;
			case FieldsType.ListType:{
				ParameterizedType parameterizedType =(ParameterizedType) fieldsType.field.getGenericType();
				ArrayList<Object> valueList = readList(parameterizedType, (List<Object>) objectValue);
				fieldsType.field.set(newIns, valueList);
			}break;
			case FieldsType.MapType:
				Document mapDocument = (Document) objectValue;
				ParameterizedType parameterizedType = (ParameterizedType) fieldsType.field.getGenericType();
				HashMap<Object, Object> map = readMap(parameterizedType, mapDocument);
				fieldsType.field.set(newIns, map);
			default:
				break;
			}

		}
		return (T) newIns;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<Object> writeList(ParameterizedType parameterizedType, List<Object> objList) throws Exception {
		ArrayList<Object> valueList = new ArrayList<>(objList.size());
		Type listType = parameterizedType.getActualTypeArguments()[0];
		if(listType instanceof ParameterizedType) { //list嵌套复杂对象
			for (Object listObject : objList) {
				if(listObject instanceof Map) {
					Document valueObj = writeMap((ParameterizedType)listType, (Map)listObject);
					valueList.add(valueObj);
				}else {
					ArrayList<Object> valueObj = writeList((ParameterizedType)listType, (List<Object>)listObject);
					valueList.add(valueObj);
				}
				
			}
		}else {
			Class<?> simpleClass = (Class<?>) listType;
			if(FieldsType.isBaseType(simpleClass)) {
				for (Object listObject : objList) {
					valueList.add(listObject);
				}
			}else {
				for (Object listObject : objList) {
					Object valueObj = objToDocument(listObject);
					valueList.add(valueObj);
				}
			}
			
		}
		return valueList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Document writeMap(ParameterizedType parameterizedType, Map<Object,Object> listObject) throws Exception {
		Document document = new Document();
		Type valueClassz = parameterizedType.getActualTypeArguments()[1];		
		if(valueClassz instanceof Class) {
			Class<?> simpleClass = (Class<?>) valueClassz;
			if(FieldsType.isBaseType(simpleClass)) {
				for(Map.Entry<?, ?> entry : document.entrySet()){
					Object mapKey = entry.getKey();
					Object mapValue = entry.getValue();
					document.append(String.valueOf(mapKey), mapValue);
				}
			}else {
				for(Map.Entry<?, ?> entry : document.entrySet()){
					Object mapKey = entry.getKey();
					Object mapValue = entry.getValue();
					document.append(String.valueOf(mapKey), objToDocument(mapValue));
				}
			}
		}else {
			ParameterizedType valueParameterizedType = (ParameterizedType) valueClassz;
			Class<?> rawTypeClazz  = (Class<?>) valueParameterizedType.getRawType();
			if(FieldsType.isList(rawTypeClazz)) {
				for(Map.Entry<?, ?> entry : document.entrySet()){
					Object mapKey = entry.getKey();
					Object mapValue = entry.getValue();
					document.append(String.valueOf(mapKey), writeList(valueParameterizedType, (List<Object>)mapValue));
				}
			}else if(FieldsType.isMap(rawTypeClazz)){
				for(Map.Entry<?, ?> entry : document.entrySet()){
					Object mapKey = entry.getKey();
					Object mapValue = entry.getValue();
					document.append(String.valueOf(mapKey), writeMap(valueParameterizedType, (Map)mapValue));
				}
			}else {
				throw new Exception("un rawTypeClazz" + rawTypeClazz.getName());
			}
		}
		return document;
	}

	
	@SuppressWarnings("unchecked")
	public Object objToDocument(Object srcObject) throws Exception {
		Class<?> clz = srcObject.getClass();
		if(FieldsType.isBaseType(clz)) {
			return srcObject;
		}else if(srcObject.getClass().equals(List.class)||srcObject.getClass().equals(ArrayList.class)) {
			List<Object> returnList = new ArrayList<>();
			List<Object> list = (List<Object>) srcObject;
			for (Object listObject : list) {
				returnList.add(objToDocument(listObject));
			}
			return returnList;	
		}else if(srcObject.getClass().equals(Map.class)||srcObject.getClass().equals(HashMap.class)) {
			Document document = new Document();
			Map<?,?> mapObj = (Map<?,?>) srcObject;
			for(Map.Entry<?, ?> entry : mapObj.entrySet()){
			    Object mapKey = entry.getKey();
			    Object mapValue = entry.getValue();
			    document.append(String.valueOf(mapKey), objToDocument(mapValue));
			}
			return document;	
		}
		Document document = new Document();
		Field field = getEntityReflectMrg().getIdField(clz);
		if(field != null) {
			document.append("_id", field.get(srcObject));
		}
		for (FieldsType fieldsType : getEntityReflectMrg().getColumnFields(clz)) {
			Object fieldsObject = fieldsType.field.get(srcObject);
			if(fieldsObject == null) {
				continue;
			}
			switch (fieldsType.fieldType) {
			case FieldsType.ObjectType:
				document.append(fieldsType.fieldName, objToDocument(fieldsObject));
				break;
			case FieldsType.BaseType:
				document.append(fieldsType.fieldName, fieldsObject);
				break;
			case FieldsType.ListType:
				List<?> list = (List<?>) fieldsObject;
				ArrayList<Object> documentList = new ArrayList<>(list.size());
				for (Object object : list) {
					documentList.add(objToDocument(object));
				}
				document.append(fieldsType.fieldName, documentList);
				break;
			case FieldsType.MapType:
				Map<?, ?> map = (Map<?, ?>) fieldsObject;
				Document mapDocument = new Document();
			    document.append(fieldsType.fieldName, mapDocument);
				for(Map.Entry<?, ?> entry : map.entrySet()){
				    Object mapKey = entry.getKey();
				    Object mapValue = entry.getValue();
				    mapDocument.append(String.valueOf(mapKey), objToDocument(mapValue));
				}
				document.append(fieldsType.fieldName, mapDocument);
				break;
			default:
				break;
			}
		}
		return document;
		
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Document objToDocument(Object entity, String fieldName) throws Exception {
		Class<?> clz = entity.getClass();
		FieldsType fieldsType = entityReflectMrg.getField(clz, fieldName);
		Document document = new Document();
		Object value = fieldsType.field.get(entity);
		switch (fieldsType.fieldType) {
		case FieldsType.BaseType:
			document.append(fieldName, value);
			break;
		case FieldsType.ObjectType:
			document.append(fieldName, objToDocument(value));
			break;
		case FieldsType.ListType:{
			List<Object> srcList = (List<Object>) value;
			ParameterizedType parameterizedType =(ParameterizedType) fieldsType.field.getGenericType();
			ArrayList<Object> docList = writeList(parameterizedType, srcList);
			document.append(fieldName, docList);
		}break;
		case FieldsType.MapType:
			ParameterizedType parameterizedType =(ParameterizedType) fieldsType.field.getGenericType();
			Document mapDocument = writeMap(parameterizedType, (Map) value);
			document.append(fieldName, mapDocument);
			break;
		default:
			break;
		}
		return document;
	}

	
	

}
