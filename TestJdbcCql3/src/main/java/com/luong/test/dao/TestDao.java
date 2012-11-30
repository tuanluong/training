package com.luong.test.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.cassandra.thrift.Cassandra.AsyncClient.prepare_cql3_query_call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.luong.test.misc.CassandraIdGenerator;
import com.luong.test.misc.CassandraIdGenerator.comType;

import de.web.services.coms.service.rest.data.BaseCEP.Classifier;
import de.web.services.coms.service.rest.data.Contact;
import de.web.services.coms.service.rest.data.Email;
import de.web.services.coms.service.rest.data.Emails;

public class TestDao extends BaseDao{
	@Autowired
	CassandraIdGenerator generator;

	//	public void createContactUsingPreparedStatementCallback(final Contact contact) {
	//		final String sql = "INSERT INTO contacts (abid, contactid, firstname, lastname) VALUES (?, ?, ?, ?)";
	//		KeyHolder keyHolder = new GeneratedKeyHolder();
	//		
	//		getJdbcTemplate().update(new PreparedStatementCreator() {
	//			@Override
	//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	//				PreparedStatement statement = con.prepareStatement(sql);
	//				statement.setLong(1, generator.getNextId(comType.Addressbook));
	//				statement.setLong(2, generator.getNextId(comType.Contact));
	//				statement.setString(3, contact.getFirstName());
	//				statement.setString(4, contact.getName());
	//				return statement;
	//			}
	//		}, keyHolder);
	//	  }

	public void createContact(Contact contact) {
		String insertSql = "INSERT INTO contacts (abid, contactid, firstname, lastname, emails) " +
				"VALUES (?, ?, ?, ?, ?)";
		PreparedStatement statement = prepareStatement(insertSql);
		try {
			statement.setLong(1, generator.getNextId(comType.Addressbook));
			statement.setLong(2, generator.getNextId(comType.Contact));
			statement.setString(3, contact.getFirstName());
			statement.setString(4, contact.getName());
			//Emails
//			byte[] b = getObjectAsBytes(contact.getEmails());
//			Blob blob = new SerialBlob(b);
//			statement.setBlob(5, blob);
			statement.setString(5, "{1:'test@test.com'}");
			statement.executeUpdate();

			statement.close();
		} catch (SQLException e) {
			//TODO: Deal with Exception
			e.printStackTrace();
		}
	}

	//	  public List<Person> select(String firstname, String lastname) {
	//	    JdbcTemplate select = new JdbcTemplate(dataSource);
	//	    return select
	//	        .query("select  FIRSTNAME, LASTNAME from PERSON where FIRSTNAME = ? AND LASTNAME= ?",
	//	            new Object[] { firstname, lastname },
	//	            new PersonRowMapper());
	//	  }
	//
	//	  public List<Person> selectAll() {
	//	    JdbcTemplate select = new JdbcTemplate(dataSource);
	//	    return select.query("select FIRSTNAME, LASTNAME from PERSON",
	//	        new PersonRowMapper());
	//	  }
	//
	//	  public void deleteAll() {
	//	    JdbcTemplate delete = new JdbcTemplate(dataSource);
	//	    delete.update("DELETE from PERSON");
	//	  }
	//
	//	  public void delete(String firstName, String lastName) {
	//	    JdbcTemplate delete = new JdbcTemplate(dataSource);
	//	    delete.update("DELETE from PERSON where FIRSTNAME= ? AND LASTNAME = ?",
	//	        new Object[] { firstName, lastName });
	//	  }

	private byte[] getObjectAsBytes(final Object obj) {  
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();  
		ObjectOutputStream oos = null;  
		try {  
			oos = new ObjectOutputStream(bos);  
			oos.writeObject(obj);  
			oos.flush();  
			oos.close();  
			bos.close();  
			return bos.toByteArray();  
		} catch (IOException e) {  
			// ignore  
		}  
		return new byte[0];  
	}  

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Contact contact = new Contact();
		contact.setFirstName("Tuan123");
		contact.setName("Luong456");
		Emails emails = new Emails();
		Email email = new Email("1235@example.com", Classifier.BUSINESS);
		emails.addEmail(email);
		contact.addEmail(email);
		////////////////////////////////////////
		ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
		TestDao testDao = (TestDao) context.getBean("testDao");
		testDao.createContact(contact);
	}

}
