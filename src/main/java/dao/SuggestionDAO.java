package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Suggestion;

/** Creates, lists, and deletes suggestions while enforcing student ownership in reads. */
public class SuggestionDAO {
    public void create(int studentId, String subject, String message) throws SQLException {
        String sql = "INSERT INTO suggestions (student_id, subject, message) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setString(2, subject);
            statement.setString(3, message);
            statement.executeUpdate();
        }
    }

    public List<Suggestion> findByStudentId(int studentId) throws SQLException {
        String sql = "SELECT s.id, s.student_id, u.full_name, s.subject, s.message, s.date_submitted "
                + "FROM suggestions s JOIN users u ON u.id = s.student_id "
                + "WHERE s.student_id = ? ORDER BY s.date_submitted DESC, s.id DESC";
        List<Suggestion> suggestions = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    suggestions.add(readSuggestion(result));
                }
            }
        }
        return suggestions;
    }

    public List<Suggestion> findAll() throws SQLException {
        String sql = "SELECT s.id, s.student_id, u.full_name, s.subject, s.message, s.date_submitted "
                + "FROM suggestions s JOIN users u ON u.id = s.student_id "
                + "ORDER BY s.date_submitted DESC, s.id DESC";
        List<Suggestion> suggestions = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                suggestions.add(readSuggestion(result));
            }
        }
        return suggestions;
    }

    public boolean deleteById(int suggestionId) throws SQLException {
        String sql = "DELETE FROM suggestions WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, suggestionId);
            return statement.executeUpdate() == 1;
        }
    }

    private Suggestion readSuggestion(ResultSet result) throws SQLException {
        Timestamp submitted = result.getTimestamp("date_submitted");
        return new Suggestion(
                result.getInt("id"),
                result.getInt("student_id"),
                result.getString("full_name"),
                result.getString("subject"),
                result.getString("message"),
                submitted.toLocalDateTime());
    }
}
