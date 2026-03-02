package com.bank.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection{
    private static DBConnection instance;
    private Connection connection;

    private final String url;
    private final String username;
    private final String password;

    // Private constructor — loads credentials from db.properties
    private DBConnection() {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException("db.properties file not found in resources.");
            }

            Properties props = new Properties();
            props.load(input);

            this.url      = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");

            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully.");

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database.", e);
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Database reconnected successfully.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reconnect to the database.", e);
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

