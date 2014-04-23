package chat;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;

public class DBCommunication
{
	//path to connect
	private String jdbcdriver = "com.mysql.jdbc.Driver";
	private String urldb = "jdbc:mysql://igor.gold.ac.uk/";

	//user details
	private String user;
	private String pass; 

	//connection data
	private Connection conn = null;
	private Statement stmt = null;

	//Info about current date
	private DateFormat dateformat;
	private Calendar cal;

	private boolean databaseExists(String dbname) throws Exception
	{
		ResultSet dbnames = conn.getMetaData().getCatalogs();

		while(dbnames.next())
		{
			String dbcandidate = dbnames.getString(1);
			if(dbcandidate.equals(dbname))
			{
				return true;
			}
		}
		return false;
	}

	private void newDatabase(String dbname) throws Exception
	{
		String sql = "CREATE DATABASE " + dbname;
		stmt.executeUpdate(sql);
	}

	private void newTable() throws Exception
	{
		String sql = "CREATE TABLE IF NOT EXISTS Messages" +
					 "(id INT NOT NULL AUTO_INCREMENT, " +
					 " time VARCHAR(30), " +	
					 " nickname VARCHAR(255), " +
					 " message VARCHAR(255), " +
					 " PRIMARY KEY ( id )) ";
		stmt.executeUpdate(sql);
	}

	DBCommunication(String urldb, String dbname, String user, String pass)
	{
		this.urldb = urldb;
		this.user = user;
		this.pass = pass;

		dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		try
		{
			Class.forName(jdbcdriver);
			System.out.println("Connecting to database...");
			System.out.println("at " + urldb);
			System.out.println("Checking if database " + dbname + " exists...");
			conn = DriverManager.getConnection(urldb,user,pass);

			if(!databaseExists(dbname))
			{
				//if not exists create a new DB
				System.out.println("Not Exists. Creating a new DB...");
				stmt = conn.createStatement();
				newDatabase(dbname);
			}
			System.out.println("Updating URL and reconnecting to DB...");
			urldb = urldb + dbname;
			conn = DriverManager.getConnection(urldb,user,pass);
			stmt = conn.createStatement();
			System.out.println("Connection successful.");
			System.out.println("Attempt to create table");
			newTable();
			System.out.println("DB Configuration successful");
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void writeMessage(Message m)
	{
		PreparedStatement pstmt;
		cal = Calendar.getInstance();

		try
		{
			String sql = "INSERT INTO Messages (time, nickname, message) " +
						 "VALUES(" + "'" + dateformat.format(cal.getTime()) +"',"
				 				   + "?,?)";
		//	System.out.println(sql);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, m.getUsername());
			pstmt.setString(2, m.getPlainText());
			pstmt.executeUpdate();
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void closeDB()
	{
		try
		{
			conn.close();
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}