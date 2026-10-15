package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import model.User;

/** Performs student registration and credential lookups against MySQL. */
public class UserDAO {
    public void registerStudent(String fullName, String username, char[] password) throws SQLException {
        String existsSql = "SELECT id FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (full_name, username, `password`, role) VALUES (?, ?, ?, 'STUDENT')";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement exists = connection.prepareStatement(existsSql)) {
            exists.setString(1, username);
            try (ResultSet result = exists.executeQuery()) {
                if (result.next()) {
                    throw new IllegalArgumentException("That username is already in use.");
                }
            }

            try (PreparedStatement insert = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insert.setString(1, fullName);
                insert.setString(2, username);
                insert.setString(3, PasswordHasher.hash(password));
                insert.executeUpdate();
            }
        } catch (SQLException exception) {
            if ("23000".equals(exception.getSQLState())) {
                throw new IllegalArgumentException("That username is already in use.", exception);
            }
            throw exception;
        }
    }

    public Optional<User> login(String username, char[] password, User.Role role) throws SQLException {
        String sql = "SELECT id, full_name, username, `password`, role FROM users WHERE username = ? AND role = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, role.name());
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next() || !PasswordHasher.verify(password, result.getString("password"))) {
                    return Optional.empty();
                }
                User user = new User(
                        result.getInt("id"),
                        result.getString("full_name"),
                        result.getString("username"),
                        User.Role.valueOf(result.getString("role")));
                return Optional.of(user);
            }
        }
    }
}
