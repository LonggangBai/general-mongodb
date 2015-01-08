package com.easyway.mongodb.dao;

import java.util.Map;

import com.easyway.mongodb.ext.ReflectUtils;
import com.easyway.mongodb.model.Hotel;
import com.mongodb.DBObject;
/**
 * 
 * @author longgangbai
 * 2015-1-8  下午3:45:44
 */
public class HotelDAO  extends BasicDAO<Hotel,String>{

	/**
	 * 
	 * @param hotel
	 */
	public void addHotel(Hotel hotel){
		Map<String,Object> params=ReflectUtils.bean2Map(hotel);
		System.out.println("params="+params);
		add(getCollectionName(),params);
	}

	/**
	 * 
	 * @param condMap
	 * @return
	 */
	public DBObject findBYID(Map<String,Object> condMap)
	{
		return findBYID(getCollectionName(),condMap);
	}



}
