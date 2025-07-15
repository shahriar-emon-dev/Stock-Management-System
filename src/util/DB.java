package util;

import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {

    // Set up the Logger
    private static final Logger logger = Logger.getLogger(DB.class.getName());
    
    private static Connection connection;

    // Establish a database connection
    public static Connection getConnection() {
        if (connection == null || !isConnectionValid()) {
            try (FileInputStream input = new FileInputStream("src/config.properties")) { // Path to your config file
                Properties properties = new Properties();
                properties.load(input);
                
                // Get the database credentials from properties file
                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.username");
                String password = properties.getProperty("db.password");

                // Connect to the database
                connection = DriverManager.getConnection(url, user, password);
                logger.info("Database connection established successfully.");
                
            } catch (SQLException | IOException e) {
                logger.log(Level.SEVERE, "Connection Failed!", e);
            }
        }
        return connection;
    }

    // Check if the current connection is valid
    private static boolean isConnectionValid() {
        try {
            return connection != null && connection.isValid(2);  // Timeout of 2 seconds
        } catch (SQLException e) {
            return false;
        }
    }

    // Optionally, you can close the connection when it's no longer needed.
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed.");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing connection", e);
            }
        }
    }
}
