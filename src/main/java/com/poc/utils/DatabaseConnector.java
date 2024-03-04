package com.poc.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnector {
	public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(Constants.JDBC_DRIVER); 
        return DriverManager.getConnection(Constants.url, Constants.username, Constants.password);
    }
}
