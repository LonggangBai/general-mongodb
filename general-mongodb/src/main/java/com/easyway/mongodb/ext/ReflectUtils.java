package com.easyway.mongodb.ext;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author longgangbai
 * 2015-1-8  下午3:56:11
 */
public class ReflectUtils {
	/**
	 * 采用反射获取泛型信息
	 * @param bean
	 * @return
	 */
	public static Map<String,Object> bean2Map(Object bean){
		Class<?> clazz=bean.getClass();
        Map<String,Object> paramMap=new HashMap<String,Object>();
		Field[] fileds=clazz.getDeclaredFields();
		if(fileds!=null){
		
			for (Field field : fileds) {
				boolean flag=field.isAccessible();
				try {
					if(!flag){
						field.setAccessible(true);
					}
					Object value=field.get(bean);
					paramMap.put(field.getName(), value);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}finally{
					field.setAccessible(flag);
				}
			}
		}
		return paramMap;
	}

}
