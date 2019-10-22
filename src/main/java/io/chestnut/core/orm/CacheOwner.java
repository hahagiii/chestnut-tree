package io.chestnut.core.orm;

import java.util.HashSet;

public class CacheOwner {
	public String cacheOwnerId;
	public String tableName;
	public HashSet<String> cacheKey;
}
