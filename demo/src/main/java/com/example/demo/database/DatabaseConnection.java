package com.example.demo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/sahacare";  // Replace with your database URL
    private static final String USER = "root";  // Replace with your database username
    private static final String PASSWORD = "Kakashi123/";  // Replace with your database password

    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver (optional, modern JDBC might auto-load)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }
}
