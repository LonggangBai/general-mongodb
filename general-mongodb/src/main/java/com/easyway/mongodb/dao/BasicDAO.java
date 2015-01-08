package com.easyway.mongodb.dao;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.QueryOperators;

/**
 * Mongodb 基本操作的类
 * 
 * @author longgangbai 2015-1-8 下午3:45:25
 * @param <T>
 * @param <K>
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class BasicDAO<T, K> implements DAO<T, K> {

    private Logger logger = LoggerFactory.getLogger(BasicDAO.class);

    private DB mongodb;

    public DB getMongodb() {
	return mongodb;
    }

    public void setMongodb(DB mongodb) {
	this.mongodb = mongodb;
    }

    private String collectionName;

    public BasicDAO() {
	initType(((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]));
    }

    protected void initType(Class<T> type) {
	collectionName = type.getSimpleName().toLowerCase();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#getCollectionName()
     */
    @Override
    public String getCollectionName() {
	return collectionName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#setCollectionName(java.lang.String)
     */
    public void setCollectionName(String collectionName) {
	this.collectionName = collectionName;
    }

    @Override
    public DB getDB() {
	return mongodb;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#getDBRef(java.lang.String,
     * java.lang.String)
     */
    @Override
    public DBRef getDBRef(String collectionName, String id) {
	try {
	    return new DBRef(getDB(), collectionName, new ObjectId(id));
	} catch (Exception e) {
	    this.logger.error("======获取对象失败" + id);
	    e.printStackTrace();
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#find(java.lang.String, java.util.Map)
     */
    @Override
    public DBCursor find(String collectionName, Map<String, Object> condMap) {
	return this.find(collectionName, condMap, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#find(java.lang.String, java.util.Map,
     * boolean)
     */
    @Override
    public DBCursor find(String collectionName, Map<String, Object> condMap, boolean isFindNormalStatus) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	DBCursor cur = null;
	if (condMap == null || condMap.size() == 0) {
	    cur = coll.find();
	} else {
	    DBObject cond = new BasicDBObject();
	    if (condMap.get("_id") != null) {
		try {
		    condMap.put("_id", new ObjectId(condMap.get("tableName").toString()));
		} catch (Exception e) {
		    return null;
		}
	    }
	    cond.putAll(condMap);
	    cur = coll.find(cond);
	}
	return cur;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#findBYID(java.lang.String,
     * java.util.Map)
     */
    @Override
    public DBObject findBYID(String collectionName, Map<String, Object> condMap) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	String id = (String) condMap.get("_id");
	if (id == null)
	    return null;
	DBObject cond = new BasicDBObject();
	try {
	    cond.put("_id", new ObjectId(id));
	} catch (Exception e) {
	    return null;
	}
	DBObject obj = coll.findOne(cond);
	if (obj != null) {
	    return obj;
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#add(java.lang.String, java.util.Map)
     */
    @Override
    public void add(String collectionName, Map<String, Object> fieldMap) {
	DBObject row = new BasicDBObject();
	row.putAll(fieldMap);
	add(collectionName, row);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#add(java.lang.String,
     * com.mongodb.DBObject)
     */
    @Override
    public void add(String collectionName, DBObject obj) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	coll.save(obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#remove(java.lang.String, java.util.Map)
     */
    @Override
    public boolean remove(String collectionName, Map<String, Object> condMap) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	DBObject cond = new BasicDBObject();
	cond.putAll(condMap);
	coll.remove(cond);
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#remove(java.lang.String,
     * com.mongodb.DBObject)
     */
    @Override
    public boolean remove(String collectionName, DBObject cond) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	coll.remove(cond);
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#removeIn(java.lang.String,
     * java.util.Map)
     */
    @Override
    public boolean removeIn(String collectionName, Map<String, Object[]> condMap) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	BasicDBObject query = new BasicDBObject();
	Iterator<String> _keys = condMap.keySet().iterator();
	// ������ѯ
	while (_keys.hasNext()) {
	    String _key = _keys.next();
	    BasicDBObject in = new BasicDBObject();
	    in.put(com.mongodb.QueryOperators.IN, condMap.get(_key));
	    if (query.size() == 0) {
		query.put(_key, in);
	    } else {
		query.append(_key, in);
	    }
	}
	coll.remove(query);
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#update(java.lang.String,
     * java.lang.String, java.util.Map)
     */
    @Override
    public int update(String collectionName, String id, Map<String, Object> fieldMap) {
	return this.update(collectionName, id, fieldMap, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#update(java.lang.String,
     * java.lang.String, java.util.Map, boolean)
     */
    @Override
    public int update(String collectionName, String id, Map<String, Object> fieldMap, boolean isUpdateModifyTime) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	BasicDBObject cond = new BasicDBObject();
	try {
	    cond.put("_id", new ObjectId(id));
	} catch (Exception e) {
	    return 1;
	}
	DBObject q = coll.findOne(cond);
	if (q == null)
	    return 1;
	BasicDBObject o = new BasicDBObject();
	if (fieldMap.containsKey("_id")) {
	    fieldMap.remove("_id");
	}
	Map oldMap = q.toMap();
	oldMap.putAll(fieldMap);
	o.putAll(oldMap);// Ҫ��ԭ�ȵļ�¼ֵҲҪ�ӽ�����������º��ֻ��fieldMap����ֶ���
	coll.update(q, o);
	return 0;

    }

    public List<DBObject> query(String key, String value) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	// 模糊查询
	Pattern pattern = Pattern.compile("^.*" + value + ".*$", Pattern.CASE_INSENSITIVE);
	BasicDBObject cond = new BasicDBObject("$regex", pattern);
	DBObject query = new BasicDBObject();
	query.put(key, cond);
	DBCursor cursor = coll.find(query);
	List<DBObject> dbList = new ArrayList<DBObject>();
	try {
	    while (cursor.hasNext()) {
		dbList.add(cursor.next());
	    }
	} finally {
	    cursor.close();
	}
	return dbList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#findOne(java.lang.String,
     * java.lang.String)
     */
    @Override
    public DBObject findOne(String collectionName, String id) {
	return this.findOne(collectionName, id, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#findOne(java.lang.String,
     * java.lang.String, boolean)
     */
    @Override
    public DBObject findOne(String collectionName, String id, boolean isFindNormalStatus) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	BasicDBObject cond = new BasicDBObject();
	try {
	    cond.put("_id", new ObjectId(id));
	} catch (Exception e) {
	    return null;
	}
	DBObject q = coll.findOne(cond);
	return q;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#count(java.lang.String, java.util.Map)
     */
    @Override
    public long count(String collectionName, Map<String, Object> condMap) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	if (condMap == null || condMap.size() == 0) {
	    return coll.count();
	} else {
	    DBObject cond = new BasicDBObject(condMap);
	    return coll.count(cond);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#find(java.lang.String, java.util.Map,
     * java.util.Map, java.util.Map)
     */
    @Override
    public DBCursor find(String collectionName, Map<String, Object[]> inCondMap, Map<String, Object> queryMap,
	    Map<String, Object> startEndTimeFieldNameMap) {
	return this.find(collectionName, inCondMap, queryMap, startEndTimeFieldNameMap, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#find(java.lang.String, java.util.Map,
     * java.util.Map, java.util.Map, boolean)
     */
    @Override
    public DBCursor find(String collectionName, Map<String, Object[]> inCondMap, Map<String, Object> queryMap,
	    Map<String, Object> startEndTimeFieldNameMap, boolean isFindStatus) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	BasicDBObject query = new BasicDBObject();
	if (inCondMap != null) {
	    Iterator<String> _keys = inCondMap.keySet().iterator();
	    // ������ѯ
	    while (_keys.hasNext()) {
		String _key = _keys.next().toString();
		BasicDBObject in = new BasicDBObject();
		in.put(com.mongodb.QueryOperators.IN, inCondMap.get(_key));
		if (query.size() == 0) {
		    query.put(_key, in);
		} else {
		    query.append(_key, in);
		}
	    }
	}
	if (queryMap != null) {
	    query.putAll(queryMap);
	}
	DBCursor cur = coll.find(query);
	return cur;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#findIn(java.lang.String, java.util.Map)
     */
    @Override
    public DBCursor findIn(String collectionName, Map<String, Object[]> condMap) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	BasicDBObject query = new BasicDBObject();
	Iterator<String> _keys = condMap.keySet().iterator();
	// ������ѯ
	while (_keys.hasNext()) {
	    String _key = _keys.next().toString();
	    BasicDBObject in = new BasicDBObject();
	    in.put(com.mongodb.QueryOperators.IN, condMap.get(_key));
	    if (query.size() == 0) {
		query.put(_key, in);
	    } else {
		query.append(_key, in);
	    }
	}
	DBCursor cur = coll.find(query);
	return cur;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#findIn(java.lang.String, java.util.Map,
     * java.util.Map, java.util.Map)
     */
    @Override
    public DBCursor findIn(String collectionName, Map<String, Object[]> inCondMap, Map<String, Object> queryMap, Map<String, Object> pageInfo) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	BasicDBObject query = new BasicDBObject();
	Iterator<String> _keys = inCondMap.keySet().iterator();
	// ������ѯ
	while (_keys.hasNext()) {
	    String _key = _keys.next().toString();
	    BasicDBObject in = new BasicDBObject();
	    in.put(com.mongodb.QueryOperators.IN, inCondMap.get(_key));
	    if (query.size() == 0) {
		query.put(_key, in);
	    } else {
		query.append(_key, in);
	    }
	}
	if (queryMap != null) {
	    query.putAll(queryMap);
	}
	DBCursor cur = coll.find(query);
	System.out.println("count==" + cur.count());
	return cur;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#findIn(java.lang.String, java.util.Map,
     * java.util.Map)
     */
    @Override
    public long findIn(String collectionName, Map<String, Object[]> inCondMap, Map<String, Object> queryMap) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	BasicDBObject query = new BasicDBObject();
	Iterator<String> _keys = inCondMap.keySet().iterator();
	// ������ѯ
	while (_keys.hasNext()) {
	    String _key = _keys.next().toString();
	    BasicDBObject in = new BasicDBObject();
	    in.put(com.mongodb.QueryOperators.IN, inCondMap.get(_key));
	    if (query.size() == 0) {
		query.put(_key, in);
	    } else {
		query.append(_key, in);
	    }
	}
	if (queryMap != null) {
	    query.putAll(queryMap);
	}
	return coll.count(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#findPage(java.lang.String,
     * java.util.Map)
     */
    @Override
    public DBCursor findPage(String collectionName, Map<String, Object> condMap) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	BasicDBObject query = (BasicDBObject) condMap.get("query");// ��ѯ����
	BasicDBObject sort = new BasicDBObject();
	DBCursor cur = coll.find(query).sort(sort);// .skip(pageInfo.getPerPageSize());
	return cur;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#DBRefToDBObject(com.mongodb.DBRef)
     */
    @Override
    public DBObject DBRefToDBObject(DBRef ref) {
	if (ref == null)
	    return null;
	return ref.fetch();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#DBRefDBObject(java.util.List)
     */
    @Override
    public List<DBObject> DBRefDBObject(List<DBRef> refList) {
	if (refList == null)
	    return null;
	List<DBObject> resultList = new ArrayList<DBObject>();
	for (DBRef ref : refList) {
	    resultList.add(ref.fetch());
	}
	return resultList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#getQueryCodeByStartTimeAndEndTime(long,
     * long)
     */
    @Override
    public DBObject getQueryCodeByStartTimeAndEndTime(long startTime, long endTime) {
	BasicDBObject cond = new BasicDBObject();
	cond.put(QueryOperators.GT, startTime);
	cond.put(QueryOperators.LTE, endTime);
	return cond;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#removeRepeat(java.util.List)
     */
    @Override
    public List<DBRef> removeRepeat(List<DBRef> list) {
	if (list == null)
	    return null;
	List<DBRef> result = new ArrayList<DBRef>();
	Map<String, String> tempMap = new HashMap<String, String>();
	for (int i = 0; i < list.size(); i++) {
	    DBRef ref = list.get(i);
	    String id = ref.getId().toString();
	    if (tempMap.get(id) == null) {
		tempMap.put(id, id);
		result.add(ref);
	    }
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#removeRepeatString(java.util.List)
     */
    @Override
    public List<String> removeRepeatString(List<String> list) {
	if (list == null)
	    return null;
	List<String> result = new ArrayList<String>();
	Map<String, String> tempMap = new HashMap<String, String>();
	for (int i = 0; i < list.size(); i++) {
	    String str = list.get(i);
	    if (tempMap.get(str) == null) {
		tempMap.put(str, str);
		result.add(str);
	    }
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#removeRef(java.util.List,
     * java.lang.String)
     */
    @Override
    public List<DBRef> removeRef(List<DBRef> list, String removeId) {
	if (list == null)
	    return null;
	List<DBRef> result = new ArrayList<DBRef>();
	for (DBRef ref : list) {
	    if (removeId.equals(ref.getId().toString())) {
		continue;
	    }
	    result.add(ref);
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#save(java.lang.String,
     * com.mongodb.DBObject)
     */
    @Override
    public void save(String collectionName, DBObject obj) {
	this.save(collectionName, obj, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#save(java.lang.String,
     * com.mongodb.DBObject, boolean)
     */
    @Override
    public void save(String collectionName, DBObject obj, boolean isUpdateModifyTime) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	coll.save(obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#hasDBObject(java.lang.String,
     * java.lang.String, boolean)
     */
    @Override
    public boolean hasDBObject(String collectionName, String id, boolean isFindNormalStatus) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	DBObject query = new BasicDBObject();
	try {
	    query.put("_id", new ObjectId(id));
	} catch (Exception e) {
	    return false;
	}
	long l = coll.count(query);
	if (l == 1)
	    return true;
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#hasDBObject(java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean hasDBObject(String collectionName, String id) {
	return hasDBObject(collectionName, id, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#hasDBObject(java.lang.String,
     * java.lang.String, java.util.Map)
     */
    @Override
    public boolean hasDBObject(String collectionName, String id, Map<String, Object> cond) {
	return hasDBObject(collectionName, id, cond, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#hasDBObject(java.lang.String,
     * java.lang.String, java.util.Map, boolean)
     */
    @Override
    public boolean hasDBObject(String collectionName, String id, Map<String, Object> cond, boolean isFindNormalStatus) {
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	DBObject query = new BasicDBObject();
	if (cond != null) {
	    query.putAll(cond);
	}
	try {
	    query.put("_id", new ObjectId(id));
	} catch (Exception e) {
	}
	long l = coll.count(query);
	if (l == 1)
	    return true;
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#hasDBObject(java.lang.String,
     * java.util.Map)
     */
    @Override
    public boolean hasDBObject(String collectionName, Map<String, Object> cond) {
	return this.hasDBObject(collectionName, cond, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#hasDBObject(java.lang.String,
     * java.util.Map, boolean)
     */
    @Override
    public boolean hasDBObject(String collectionName, Map<String, Object> cond, boolean isOne) {
	if (cond == null)
	    return false;
	DB db = getDB();
	DBCollection coll = db.getCollection(collectionName);
	DBObject query = new BasicDBObject();
	query.putAll(cond);
	long l = coll.count(query);
	if (isOne) {
	    if (l == 1)
		return true;
	} else {
	    if (l > 0)
		return true;
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#ObjectToListDBRef(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public List<DBRef> ObjectToListDBRef(String collectionName, Object obj) {
	if (obj == null)
	    return null;
	List<DBRef> result = new ArrayList<DBRef>();
	if (obj instanceof Object[]) {
	    Object[] arr = (Object[]) obj;
	    for (int i = 0; i < arr.length; i++) {
		Object arrobj = arr[i];
		if (arrobj instanceof DBRef) {
		    result.add((DBRef) arrobj);
		} else if (collectionName != null && arrobj instanceof String) {
		    if (this.hasDBObject(collectionName, arrobj.toString()))
			result.add(this.getDBRef(collectionName, arrobj.toString()));// new
										     // DBRef(this.getDB(),collectionName,new
										     // ObjectId(arrobj.toString()))
		}
	    }
	} else if (obj instanceof List) {
	    List list = (List) obj;
	    for (int i = 0; i < list.size(); i++) {
		Object listobj = list.get(i);
		if (listobj instanceof DBRef) {
		    result.add((DBRef) listobj);
		} else if (collectionName != null && listobj instanceof String) {
		    if (this.hasDBObject(collectionName, listobj.toString()))
			result.add(this.getDBRef(collectionName, listobj.toString()));// new
										      // DBRef(this.getDB(),collectionName,new
										      // ObjectId(listobj.toString()))
		}
	    }
	} else if (obj instanceof DBRef) {
	    result.add((DBRef) obj);
	} else if (collectionName != null && obj instanceof String) {
	    if (this.hasDBObject(collectionName, obj.toString()))
		result.add(this.getDBRef(collectionName, obj.toString()));// new
									  // DBRef(this.getDB(),collectionName,new
									  // ObjectId(obj.toString()))
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#ObjectToListDBRef(java.lang.Object)
     */
    @Override
    public List<DBRef> ObjectToListDBRef(Object obj) {
	return this.ObjectToListDBRef(null, obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#ObjectToDBRef(java.lang.Object)
     */
    @Override
    public DBRef ObjectToDBRef(Object obj) {
	return this.ObjectToDBRef(null, obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#ObjectToDBRef(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public DBRef ObjectToDBRef(String collectionName, Object obj) {
	if (obj == null)
	    return null;
	if (obj instanceof DBRef) {
	    return (DBRef) obj;
	} else if (obj instanceof List) {
	    if (((List) obj).size() > 0)
		this.ObjectToDBRef(collectionName, ((List) obj).get(0));
	} else if (obj instanceof Object[]) {
	    if (((Object[]) obj).length > 0)
		this.ObjectToDBRef(collectionName, ((Object[]) obj)[0]);
	} else if (collectionName != null && obj instanceof String) {
	    return this.getDBRef(collectionName, obj.toString());
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#DBRefListToStringArr(java.util.List)
     */
    @Override
    public String[] DBRefListToStringArr(List<DBRef> refList) {
	if (refList != null) {
	    String[] ids = new String[refList.size()];
	    for (int i = 0; i < ids.length; i++) {
		DBRef ref = refList.get(i);
		ids[i] = ref.getId().toString();
	    }
	    return ids;
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#DBObjectListToDBRefList(java.util.List,
     * java.lang.String)
     */
    @Override
    public List<DBRef> DBObjectListToDBRefList(List<DBObject> list, String collectionName) {
	if (list != null) {
	    List<DBRef> refList = new ArrayList<DBRef>();
	    for (DBObject dbObject : list) {
		refList.add(this.getDBRef(collectionName, dbObject.get("_id").toString()));
	    }
	    return refList;
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#isDBObjectList(java.lang.Object)
     */
    @Override
    public boolean isDBObjectList(Object obj) {
	if (obj instanceof Object[]) {
	    Object[] array = (Object[]) obj;
	    if (array.length > 0) {
		for (int i = 0; i < array.length; i++) {
		    if (!(array[i] instanceof DBObject)) {
			return false;
		    }
		}
		return true;
	    }
	} else if (obj instanceof List) {
	    List array = (List) obj;
	    if (array.size() > 0) {
		for (int i = 0; i < array.size(); i++) {
		    if (!(array.get(i) instanceof DBObject)) {
			return false;
		    }
		}
		return true;
	    }
	} else if (obj instanceof DBObject) {
	    return true;
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#objectToListDBObject(java.lang.Object)
     */
    @Override
    public List<DBObject> objectToListDBObject(Object obj) {
	if (obj == null) {
	    return null;
	}
	List<DBObject> result = new ArrayList<DBObject>();
	if (obj instanceof Object[]) {
	    Object[] array = (Object[]) obj;
	    for (int i = 0; i < array.length; i++) {
		Object o = array[i];
		if (o instanceof DBObject) {
		    DBObject dbObj = (DBObject) o;
		    result.add(dbObj);
		} else if (o instanceof Map) {
		    Map map = (Map) o;
		    DBObject dbo = new BasicDBObject();
		    dbo.putAll(map);
		    result.add(dbo);
		}
	    }
	} else if (obj instanceof List) {
	    List array = (List) obj;
	    for (int i = 0; i < array.size(); i++) {
		Object o = array.get(i);
		if (o instanceof DBObject) {
		    DBObject dbObj = (DBObject) o;
		    result.add(dbObj);
		} else if (o instanceof Map) {
		    Map map = (Map) o;
		    DBObject dbo = new BasicDBObject();
		    dbo.putAll(map);
		    result.add(dbo);
		}
	    }
	} else if (obj instanceof DBObject) {
	    DBObject dbObj = (DBObject) obj;
	    result.add(dbObj);
	} else if (obj instanceof Map) {
	    Map map = (Map) obj;
	    DBObject dbo = new BasicDBObject();
	    dbo.putAll(map);
	    result.add(dbo);
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.easyway.mongodb.dao.DAO#objectToDBObject(java.lang.Object)
     */
    @Override
    public DBObject objectToDBObject(Object obj) {
	List<DBObject> list = this.objectToListDBObject(obj);
	if (list == null || list.size() == 0)
	    return null;
	return list.get(0);
    }
}
