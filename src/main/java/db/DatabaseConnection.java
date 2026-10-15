package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Creates MySQL connections using environment-configurable connection settings. */
public final class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/school_suggestion_db?serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = setting("SCHOOL_DB_URL", DEFAULT_URL);
        String username = setting("SCHOOL_DB_USER", DEFAULT_USER);
        String password = setting("SCHOOL_DB_PASSWORD", DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, username, password);
    }

    private static String setting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null ? fallback : value;
    }
}
