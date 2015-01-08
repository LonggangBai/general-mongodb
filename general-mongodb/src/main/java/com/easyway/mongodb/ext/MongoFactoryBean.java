package com.easyway.mongodb.ext;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

/**
 *  
 *  
 *  <bean id="mongoOne" name="mongoDef" class="com.easyway.mongodb.ext.MongoFactoryBean">
	</bean>
	<bean id="mongoOneV" class="com.easyway.mongodb.ext.MongoFactoryBean">
		<property name="address">
			<value>localhost</value>
		</property>
	</bean>

	<bean id="mongoOnePort" class="com.easyway.mongodb.ext.MongoFactoryBean">
		<property name="address">
			<value>localhost:27017</value>
		</property>
	</bean>

	<bean id="mongoMultiCSPort" class="com.easyway.mongodb.ext.MongoFactoryBean">
		<property name="multiAddress">
			<value>localhost:27017, localhost:27027</value>
		</property>
	</bean>

	<bean id="mongoMultiPort" class="com.easyway.mongodb.ext.MongoFactoryBean">
		<property name="multiAddress">
			<list>
				<value>localhost:27017</value>
				<value>localhost:27027</value>
			</list>
		</property>
	</bean>
	
 * @author longgangbai
 * 2015-1-8  下午3:55:54
 */
public class MongoFactoryBean extends AbstractFactoryBean<Mongo> {

	private List<ServerAddress> replicaSetSeeds = new ArrayList<ServerAddress>();
	private MongoOptions mongoOptions;
	
	@Override
	public Class<?> getObjectType() {
		return Mongo.class;
	}


	@SuppressWarnings("deprecation")
	@Override
	public  Mongo createInstance() throws Exception {
		if (replicaSetSeeds.size() > 0) {
			if (mongoOptions != null) {
				return new Mongo(replicaSetSeeds, mongoOptions);
			}
			return new Mongo(replicaSetSeeds);
		}
		return new Mongo();
	}

	public void setMultiAddress(String[] serverAddresses) {
		replSeeds(serverAddresses);
	}

	private void replSeeds(String... serverAddresses) {
		try {
			replicaSetSeeds.clear();
			for (String addr : serverAddresses) {
				String[] a = addr.split(":");
				String host = a[0];
				if (a.length > 2) {
					throw new IllegalArgumentException("Invalid Server Address : " + addr);
				}else if(a.length == 2) {
					replicaSetSeeds.add(new ServerAddress(host, Integer.parseInt(a[1])));
				}else{
					replicaSetSeeds.add(new ServerAddress(host));
				}
			}
		} catch (Exception e) {
			throw new BeanCreationException("Error while creating replicaSetAddresses",e);
		}
	}

	public void setAddress(String serverAddress) {
		replSeeds(serverAddress);
	}

}
