package com.luong.test.misc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.luong.test.dao.BaseDao;

public class DBUtils extends BaseDao{
	public static String KEYSPACE = "coms";
	public static String CF_CONTACTS = "contacts";
	public static String CF_VALUES= "values";
	public static String CF_UASABMAPPING = "uasabmapping";


	public static void main(String[] args) {
		DBUtils util = new DBUtils();
//		util.renewKeyspace();
		util.makeColumnFamilies();
	}

	private void renewKeyspace() {
		String dropKeyspace = String.format("DROP KEYSPACE %s;", KEYSPACE);
		String createKeyspace = String.format(
				"CREATE KEYSPACE %s WITH replication = {'class': 'SimpleStrategy', 'replication_factor':1};",
				KEYSPACE);
		executeCQL3Statement(dropKeyspace);
		executeCQL3Statement(createKeyspace);
	}

	private void executeCQL3Statement(String sql) {
		PreparedStatement statement = prepareStatement(sql);
		try {
			statement.execute(sql);
		} catch (SQLException e) {
			//TODO: Deal with Exception
			e.printStackTrace();
		}
	}

	private void makeColumnFamilies() {
		String makeTableContacts = "CREATE TABLE contacts (" +
				"abid bigint, contactid bigint," +
				"firstname text," +
				"lastname text," +
				"emails map<bigint, text>," +
				"PRIMARY KEY (abid,contactid));";
		executeCQL3Statement(makeTableContacts);
	}

	// Batch in CQL3-Jdbc not yet implemented
	//	private void executeBatchCQL3Statement(String[] dmlStatements) {
	//		PreparedStatement statement = prepareStatement(dmlStatements[0]);
	//		try {
	//			for (int i = 1; i < dmlStatements.length; i++) {
	//				statement.addBatch(dmlStatements[i]);
	//			}
	//			statement.executeBatch();
	//		} catch(SQLException e) {
	//			//TODO: Deal with Exception
	//			e.printStackTrace();
	//		}
	//	}
}
