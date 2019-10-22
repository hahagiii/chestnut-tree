package io.chestnut.core.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;


public class EntityMrg {
	public EntityReflectMrg entityReflectMrg;
	public MongoMappingConverter mongoMappingConverter;
	public MongoClient mongoClient;
	public String dbName;
	public MongoDatabase mongoDatabase;
	
	public Map<String, CacheOwner> cacheKeyMap = new HashMap<>();
	public Map<String, TableCache> cache = new HashMap<>();
	
	public EntityMrg(String dataBaseUrl,int port, String user,String password,String dbName) {
		entityReflectMrg = new EntityReflectMrg();
		mongoMappingConverter = new MongoMappingConverter();
		mongoMappingConverter.setEntityReflectMrg(entityReflectMrg);
		this.dbName = dbName;
		ServerAddress serverAddress = new ServerAddress(dataBaseUrl, port);
		MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(10).threadsAllowedToBlockForConnectionMultiplier(10).build();
		if(password == null || password.equals("")) {
			mongoClient = new MongoClient(serverAddress, options);
		}else {
			String validateDataBaseName = "admin";
			MongoCredential credential = MongoCredential.createScramSha1Credential(user, validateDataBaseName,password.toCharArray());
			mongoClient = new MongoClient(serverAddress, credential, options);
		}
		mongoDatabase = mongoClient.getDatabase(dbName);
	}
	
	
	public <T> ArrayList<T> getEntity(Class<T> entityClazz) throws Exception{
		String tableName = entityReflectMrg.getTableName(entityClazz);
		MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);
		FindIterable<Document> findIterable = collection.find();
		ArrayList<T> list = new ArrayList<>();
		for (Document document : findIterable) {
			try {
				T returnObj = mongoMappingConverter.documentToObj(entityClazz,document);
				list.add(returnObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEntityById(Class<T> entityClazz, String id) throws Exception{
		EntityReflectTable table = entityReflectMrg.getEntityReflectTable(entityClazz);
		String tableName = entityReflectMrg.getTableName(entityClazz);
		if(table.needCache()) {
			TableCache tableCache = cache.get(tableName);
			if(tableCache != null) {
				Object mongoEntity = tableCache.mongoEntityMap.get(id);
				if(mongoEntity != null) {
					return (T) mongoEntity;
				}
			}
		}
		
		MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);
		FindIterable<Document> findIterable = collection.find(new Document("_id",id));
		Document document = findIterable.first();
		if(document == null) {
			return null;
		}
		try {
			T returnObj = mongoMappingConverter.documentToObj(entityClazz,document);
			if(table.needCache()) {
				entityReflectMrg.addCache(returnObj);
			}
			return (T) returnObj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public <T> List<T> getEntityByField(String id, String field,Object value){
		return null;
	}
	
	public static  <T> List<T>  getEntityList(String keyfield, String keyValue) {
		return null;		
	}
	
	public <T> List<T> getEntityByField(String id, Map<String, Object> fieldMap){
		return null;
	}

	public void addEntity(Object entity) throws Exception {
		Class<?> entityClass = entity.getClass();
		EntityReflectTable table = entityReflectMrg.getEntityReflectTable(entityClass);
		if(table.needCache) {
			entityReflectMrg.addCache(entity);
		}
		String tableName = table.tableName;
		MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);
		collection.insertOne((Document) mongoMappingConverter.objToDocument(entity));
	}

	
	@SuppressWarnings("unused")
	public void updateField(Object entity, String fieldName) throws Exception {
		Class<?> entityClass = entity.getClass();
		String tableName = entityReflectMrg.getTableName(entityClass);
		MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);
		Field Idfield = entityReflectMrg.getIdField(entityClass);
		String id = (String) Idfield.get(entity);
		Document upDocument = mongoMappingConverter.objToDocument(entity,fieldName);
		UpdateResult  updateResult  = collection.updateOne(Filters.eq("_id", id), new Document("$set",upDocument));
	}
	
}

