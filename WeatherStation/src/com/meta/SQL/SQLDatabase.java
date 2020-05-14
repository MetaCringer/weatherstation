package com.meta.SQL;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDatabase {
	String url;
	private static SQLDatabase instance;
	public static SQLDatabase getInstance() {
		if(instance == null) {
			try {
				instance = new SQLDatabase();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return instance;
	}
	private SQLDatabase() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException  {
		
		url = "jdbc:sqlite:database.db";
		Class.forName("org.sqlite.JDBC").newInstance();
		getConnection().close();
		
		createWorldTables();
	}
	
	public Connection getConnection() throws SQLException {
			return DriverManager.getConnection(url);
	}
	
	
	public void createWorldTables() throws SQLException{
		Connection c = getConnection();
		Statement s = c.createStatement();
		s.executeUpdate("CREATE TABLE IF NOT EXISTS logs ('timestamp' INTEGER NOT NULL, `ip` VARCHAR(24), `data` TEXT NOT NULL);");
		s.executeUpdate("CREATE TABLE IF NOT EXISTS weather ('timestamp' INTEGER NOT NULL, `data` TEXT NOT NULL);");
		s.close();
		c.close();
	}
	
}
