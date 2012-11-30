package com.luong.test.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BaseDao {
	protected Connection connection;
	public BaseDao() {
		try {
			Class.forName("org.apache.cassandra.cql.jdbc.CassandraDriver");
			connection = DriverManager.getConnection("jdbc:cassandra://localhost:9160/coms");
		} catch (SQLException e) {
			//TODO: Deal with Exception here 
			e.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			//TODO: Deal with Exception here 
			cnfe.printStackTrace();
		};
	}
	
	public PreparedStatement prepareStatement(String sql) {
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			//TODO: Deal with Exception here 
			e.printStackTrace();
		}
		return null;
	}
}
