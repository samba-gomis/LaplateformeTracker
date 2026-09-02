package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// DatabaseConnection - Manages the unique connection to the PostgreSQL database

public class DatabaseConnection {

    // Database configuration
    // FIX : credentials used to be hardcoded (including a real password),
    // and this file is committed to a public git repo. Read them from
    // environment variables instead, with safe local-dev defaults.
    private static final String URL      = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/laplateformetracker");
    private static final String USER     = System.getenv().getOrDefault("DB_USER", "postgres");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");

    // Unique instance 
    private static DatabaseConnection instance;
    private Connection connection;

    // Private constructor
    private DatabaseConnection() {
        try {
            // Explicitly load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" Database connection established successfully");

        } catch (ClassNotFoundException e) {
            System.err.println(" PostgreSQL driver not found: " + e.getMessage());
            System.err.println(" Make sure postgresql-XX.jar is in your classpath");
        } catch (SQLException e) {
            System.err.println(" Failed to connect to the database: " + e.getMessage());
            System.err.println(" Check your URL, username and password");
        }
    }

    // Get the unique instance
    //  Returns the unique instance of DatabaseConnection
    //  Creates the connection if it does not exist yet
     
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Get the SQL connection
    // Returns the Connection object used to execute SQL queries
    // Attempts to reconnect automatically if the connection is closed or invalid
    // FIX : this used to swallow SQLException and return null on failure,
    // which made every DAO call blow up with an uncaught NullPointerException
    // (instead of the SQLException callers already catch) whenever the
    // database was unreachable — including during MainController.initialize(),
    // which crashed app startup entirely. Let the exception propagate instead.

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println(" Reconnecting to the database...");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    // Close the connection
    // Cleanly closes the database connection
    // Should be called when the application shuts down

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println(" Connection closed cleanly");
            } catch (SQLException e) {
                System.err.println(" Error while closing the connection: " + e.getMessage());
            }
        }
    }
}