package com.easyway.mongodb.app;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.easyway.mongodb.dao.HotelDAO;
import com.easyway.mongodb.model.Hotel;
import com.mongodb.DBObject;

/**
 * 测试mongodb的类
 * @author longgangbai
 * 2015-1-8  下午3:45:18
 */
public class HotelApp {
	public static void main(String[] args) {
	   ApplicationContext ctx=new ClassPathXmlApplicationContext("classpath:spring-config-mongodb.xml");
	   Hotel hotel=new Hotel();
	   hotel.setHotelName("model 168");
	   hotel.setMoney(899.00);
	   HotelDAO  hotelDAO=(HotelDAO)ctx.getBean("hotelDAO");
	   hotelDAO.addHotel(hotel);
	   List<DBObject> dbObjectlist=hotelDAO.query("hotelName", "model 168");
	   System.out.println("dbObject ="+dbObjectlist);
	}
}
