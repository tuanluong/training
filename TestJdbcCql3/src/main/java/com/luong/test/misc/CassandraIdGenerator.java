package com.luong.test.misc;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class CassandraIdGenerator extends JdbcDaoSupport{
	private static String TABLE = "CassandraId";
	private static String ADDRESSBOOK_ROW = "abId";
	private static String CONTACT_ROW = "coId";
	private static String NAME_COLUMN = "name";
	private static String ID_COLUMN = "id";
	public static enum comType {Addressbook, Contact}

	@Autowired
	public DataSourceTransactionManager transactionManager;

	public Long getNextId(comType type) {
		Long id = new Long(-1);
		TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
		String sqlQueryUpdate = null;
		String sqlQueryRead = null;
		switch(type) {
		case Addressbook:
			sqlQueryUpdate = String.format("UPDATE %s SET %s = %s + 1 WHERE %s = '%s';",
					TABLE, ID_COLUMN, ID_COLUMN, NAME_COLUMN, ADDRESSBOOK_ROW);
			sqlQueryRead = String.format("SELECT %s FROM %s WHERE %s = '%s';",
					ID_COLUMN, TABLE, NAME_COLUMN, ADDRESSBOOK_ROW);
			break;
		case Contact:
			sqlQueryUpdate = String.format("UPDATE %s SET %s = %s + 1 WHERE %s = '%s';",
					TABLE, ID_COLUMN, ID_COLUMN, NAME_COLUMN, CONTACT_ROW);
			sqlQueryRead = String.format("SELECT %s FROM %s WHERE %s = '%s';",
					ID_COLUMN, TABLE, NAME_COLUMN, CONTACT_ROW);
			break;
		}

		try{
			getJdbcTemplate().update(sqlQueryUpdate);
			id = getJdbcTemplate().queryForLong(sqlQueryRead);
			transactionManager.commit(status);
		}catch (CannotCreateTransactionException ex) {
			System.err.println("Database Server not found, please start hsqldb as server at localhost");
		}catch (Exception e){
			System.err.println("Database/Table didn't exist, please run again");
			transactionManager.rollback(status);
			initDatabase();
			return null;
		}
		return id;
	}

	public void resetIdCounter(comType type) throws SQLException {
		String sqlQuery = null;
		switch (type) {
		case Addressbook:
			sqlQuery = String.format("UPDATE %s SET %s = 0 WHERE %s = '%s'",
					TABLE, ID_COLUMN, NAME_COLUMN, ADDRESSBOOK_ROW);
			break;
		case Contact:
			sqlQuery = String.format("UPDATE %s SET %s = 0 WHERE %s = '%s'",
					TABLE, ID_COLUMN, NAME_COLUMN, CONTACT_ROW);
			break;
		}
		getJdbcTemplate().execute(sqlQuery);
	}

	private void initDatabase() {
		String createTableSql = String.format("DROP TABLE %s IF EXISTS;", TABLE);
		getJdbcTemplate().execute(createTableSql);
		createTableSql = String.format("CREATE TABLE %s (%s VARCHAR(30), %s BIGINT);", 
				TABLE, NAME_COLUMN, ID_COLUMN);
		getJdbcTemplate().execute(createTableSql);
		String insertSql = String.format("INSERT INTO %s (%s, %s) VALUES ('%s', '%s')", 
				TABLE,
				NAME_COLUMN,
				ID_COLUMN,
				ADDRESSBOOK_ROW,
				0);
		insertSql += String.format("INSERT INTO %s (%s, %s) VALUES ('%s', '%s')", 
				TABLE,
				NAME_COLUMN,
				ID_COLUMN,
				CONTACT_ROW,
				0);
			getJdbcTemplate().execute(insertSql);
	}

//	public static void main(String[] args) throws SQLException, IOException, AclFormatException {
//		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring/application-context.xml");
//		CassandraIdGenerator generator = (CassandraIdGenerator) ctx.getBean("cassandraIdGenerator");
//		System.out.println(CassandraIdGenerator.comType.Addressbook + ": " + generator.getNextId(CassandraIdGenerator.comType.Addressbook));
//		System.out.println(CassandraIdGenerator.comType.Contact + ": " + generator.getNextId(CassandraIdGenerator.comType.Contact));
//	}
}

