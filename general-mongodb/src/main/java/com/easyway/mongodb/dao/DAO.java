package com.easyway.mongodb.dao;

import java.util.List;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
/**
 * Mongodb访问底层mongo的方法接口
 * @author longgangbai
 * 2015-1-8  下午3:45:37
 * @param <T>
 * @param <K>
 */
public interface DAO<T, K> {

	public abstract String getCollectionName();

	public abstract void setCollectionName(String collectionName);


	public abstract DB getDB();

	public abstract DBRef getDBRef(String collectionName, String id);

	/**
	 * 
	 * @param collectionName
	 * @param condMap
	 * @return
	 */
	public abstract DBCursor find(String collectionName,
			Map<String, Object> condMap);

	/**
	 * 
	 * @param collectionName
	 * @param condMap
	 * @param isFindNormalStatus
	 * @return
	 */
	public abstract DBCursor find(String collectionName,
			Map<String, Object> condMap, boolean isFindNormalStatus);

	/**
	 * 
	 * @param collectionName
	 * @param condMap
	 * @return
	 */
	public abstract DBObject findBYID(String collectionName,
			Map<String, Object> condMap);

	/**
	 * 
	 * @param collectionName
	 * @param fieldMap
	 */
	public abstract void add(String collectionName, Map<String, Object> fieldMap);

	public abstract void add(String collectionName, DBObject obj);

	/**
	 * 
	 * @param collectionName
	 * @param condMap
	 * @return
	 */
	public abstract boolean remove(String collectionName,
			Map<String, Object> condMap);

	/**
	 * 
	 * @param collectionName
	 * @param cond
	 * @return
	 */
	public abstract boolean remove(String collectionName, DBObject cond);

	/**
	 * 
	 * @param collectionName
	 * @param condMap
	 * @return
	 */
	public abstract boolean removeIn(String collectionName,
			Map<String, Object[]> condMap);

	/**
	 * 
	 * @param collectionName
	 * @param id
	 * @param fieldMap
	 * @return
	 */
	public abstract int update(String collectionName, String id,
			Map<String, Object> fieldMap);

	/**
	 * 
	 * @param collectionName
	 * @param id
	 * @param fieldMap
	 * @param isUpdateModifyTime
	 * @return
	 */
	public abstract int update(String collectionName, String id,
			Map<String, Object> fieldMap, boolean isUpdateModifyTime);

	/**
	 * 
	 * @param collectionName
	 * @param id
	 * @return
	 */
	public abstract DBObject findOne(String collectionName, String id);

	public abstract DBObject findOne(String collectionName, String id,
			boolean isFindNormalStatus);

	/**
	 * @param collectionName
	 * @param condMap
	 * @return
	 */
	public abstract long count(String collectionName,
			Map<String, Object> condMap);

	/**
	 * @param collectionName
	 * @param condMap
	 * @return
	 */
	public abstract DBCursor find(String collectionName,
			Map<String, Object[]> inCondMap, Map<String, Object> queryMap,
			Map<String, Object> startEndTimeFieldNameMap);

	public abstract DBCursor find(String collectionName,
			Map<String, Object[]> inCondMap, Map<String, Object> queryMap,
			Map<String, Object> startEndTimeFieldNameMap, boolean isFindStatus);

	/**
	 * @param collectionName
	 * @param condMap
	 * @return
	 */
	public abstract DBCursor findIn(String collectionName,
			Map<String, Object[]> condMap);

	/**
	 * @param collectionName
	 * @param condMap
	 * @return
	 */
	public abstract DBCursor findIn(String collectionName,
			Map<String, Object[]> inCondMap, Map<String, Object> queryMap,
			Map<String, Object> pageInfo);

	/**
	 * @param collectionName
	 * @param condMap
	 * @return
	 */
	public abstract long findIn(String collectionName,
			Map<String, Object[]> inCondMap, Map<String, Object> queryMap);

	/**
	 * 
	 * @param collectionName
	 * @param condMap
	 * @return
	 */
	public abstract DBCursor findPage(String collectionName,
			Map<String, Object> condMap);

	public abstract DBObject DBRefToDBObject(DBRef ref);

	public abstract List<DBObject> DBRefDBObject(List<DBRef> refList);

	public abstract DBObject getQueryCodeByStartTimeAndEndTime(long startTime,
			long endTime);

	/**
	 * 
	 * @param list
	 * @return
	 */
	public abstract List<DBRef> removeRepeat(List<DBRef> list);

	public abstract List<String> removeRepeatString(List<String> list);

	/**
	 * 
	 * @param list
	 * @param removeId
	 * @return
	 */
	public abstract List<DBRef> removeRef(List<DBRef> list, String removeId);

	/**
	 * 
	 * @param collectionName
	 * @param obj
	 */
	public abstract void save(String collectionName, DBObject obj);

	public abstract void save(String collectionName, DBObject obj,
			boolean isUpdateModifyTime);

	/**
	 * 
	 * @param collectionName
	 * @param id
	 * @param isFindNormalStatus
	 * @return
	 */
	public abstract boolean hasDBObject(String collectionName, String id,
			boolean isFindNormalStatus);

	/**
	 * 
	 * @param collectionName
	 * @param id
	 * @return
	 */
	public abstract boolean hasDBObject(String collectionName, String id);

	/**
	 * @param collectionName
	 * @param id
	 * @param cond
	 * @return
	 */
	public abstract boolean hasDBObject(String collectionName, String id,
			Map<String, Object> cond);

	/**
	 * @param collectionName
	 * @param id
	 * @param cond
	 * @return
	 */
	public abstract boolean hasDBObject(String collectionName, String id,
			Map<String, Object> cond, boolean isFindNormalStatus);

	public abstract boolean hasDBObject(String collectionName,
			Map<String, Object> cond);

	public abstract boolean hasDBObject(String collectionName,
			Map<String, Object> cond, boolean isOne);

	public abstract List<DBRef> ObjectToListDBRef(String collectionName,
			Object obj);

	public abstract List<DBRef> ObjectToListDBRef(Object obj);

	public abstract DBRef ObjectToDBRef(Object obj);

	public abstract DBRef ObjectToDBRef(String collectionName, Object obj);

	public abstract String[] DBRefListToStringArr(List<DBRef> refList);

	public abstract List<DBRef> DBObjectListToDBRefList(List<DBObject> list,
			String collectionName);

	public abstract boolean isDBObjectList(Object obj);

	/**
	 * @param obj
	 * @return
	 */
	public abstract List<DBObject> objectToListDBObject(Object obj);

	public abstract DBObject objectToDBObject(Object obj);

}