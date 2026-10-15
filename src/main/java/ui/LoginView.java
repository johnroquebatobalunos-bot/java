package ui;

import dao.UserDAO;
import java.sql.SQLException;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.User;

/** Displays the shared student and administrator sign-in screen. */
public class LoginView {
    private final Stage stage;
    private final UserDAO userDAO = new UserDAO();

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label title = new Label("School Suggestions");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #163a43;");
        Label subtitle = new Label("Sign in to continue");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #587178;");

        ComboBox<User.Role> role = new ComboBox<>();
        role.getItems().setAll(User.Role.values());
        role.setPromptText("Select an account type");
        role.setMaxWidth(Double.MAX_VALUE);
        role.setConverter(new StringConverter<>() {
            @Override
            public String toString(User.Role value) {
                if (value == null) {
                    return "";
                }
                return value == User.Role.ADMIN ? "Administrator" : "Student";
            }

            @Override
            public User.Role fromString(String value) {
                return null;
            }
        });
        Label roleLabel = new Label("Sign in as");
        roleLabel.setLabelFor(role);

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        Button login = new Button("Sign in");
        login.setDefaultButton(true);
        login.setMaxWidth(Double.MAX_VALUE);
        login.setStyle("-fx-background-color: #176b67; -fx-text-fill: white; -fx-font-weight: bold;");
        Label registrationPrompt = new Label("New student?");
        Button register = new Button("Create an account");
        register.setStyle("-fx-background-color: transparent; -fx-text-fill: #176b67;");

        login.setOnAction(event -> authenticate(role.getValue(), username.getText(), password.getText()));
        password.setOnAction(event -> authenticate(role.getValue(), username.getText(), password.getText()));
        register.setOnAction(event -> new RegisterView(stage).show());

        VBox root = new VBox(13, title, subtitle, roleLabel, role, username, password, login, registrationPrompt, register);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(36));
        root.setStyle("-fx-background-color: #f1f6f3;");
        root.setPrefWidth(430);
        stage.setTitle("School Suggestion System | Sign In");
        stage.setScene(new Scene(root));
        stage.setMinWidth(400);
        stage.setMinHeight(410);
        stage.show();
    }

    private void authenticate(User.Role role, String username, String password) {
        if (role == null) {
            showAlert(Alert.AlertType.WARNING, "Account type required", "Select Student or Administrator.");
            return;
        }
        if (username.isBlank() || password.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing information", "Enter both your username and password.");
            return;
        }
        try {
            Optional<User> result = userDAO.login(username.trim(), password.toCharArray(), role);
            if (result.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Sign-in failed", "The username or password is incorrect.");
                return;
            }
            User user = result.get();
            if (user.getRole() == User.Role.ADMIN) {
                new AdminDashboard(stage, user).show();
            } else {
                new StudentDashboard(stage, user).show();
            }
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
