package ui;

import dao.UserDAO;
import java.sql.SQLException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Provides student-only account registration and input validation. */
public class RegisterView {
    private final Stage stage;
    private final UserDAO userDAO = new UserDAO();

    public RegisterView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        javafx.scene.control.Label title = new javafx.scene.control.Label("Create student account");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: #163a43;");
        TextField fullName = new TextField();
        fullName.setPromptText("Full name");
        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm password");
        Button create = new Button("Create account");
        create.setDefaultButton(true);
        create.setMaxWidth(Double.MAX_VALUE);
        create.setStyle("-fx-background-color: #176b67; -fx-text-fill: white; -fx-font-weight: bold;");
        Button back = new Button("Back to sign in");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #176b67;");
        create.setOnAction(event -> register(fullName.getText(), username.getText(),
                password.getText(), confirmPassword.getText()));
        back.setOnAction(event -> new LoginView(stage).show());

        VBox root = new VBox(13, title, fullName, username, password, confirmPassword, create, back);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(36));
        root.setStyle("-fx-background-color: #f1f6f3;");
        root.setPrefWidth(460);
        stage.setTitle("School Suggestion System | Register");
        stage.setScene(new Scene(root));
        stage.setMinWidth(420);
        stage.setMinHeight(440);
        stage.show();
    }

    private void register(String fullName, String username, String password, String confirmPassword) {
        if (fullName.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing information", "Complete every field.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Passwords do not match", "Enter the same password in both fields.");
            return;
        }
        try {
            userDAO.registerStudent(fullName.trim(), username.trim(), password.toCharArray());
            showAlert(Alert.AlertType.INFORMATION, "Account created", "Your student account is ready. Please sign in.");
            new LoginView(stage).show();
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.WARNING, "Username unavailable", exception.getMessage());
        } catch (SQLException exception) {
            showAlert(Alert.AlertType.ERROR, "Database error", exception.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message == null ? "An unexpected error occurred." : message);
        alert.showAndWait();
    }
}
