package ui;

import dao.SuggestionDAO;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Suggestion;
import model.User;

/** Lets a student submit suggestions and view only their own submission history. */
public class StudentDashboard {
    private final Stage stage;
    private final User user;
    private final SuggestionDAO suggestionDAO = new SuggestionDAO();
    private final TableView<Suggestion> table = new TableView<>();

    public StudentDashboard(Stage stage, User user) {
        if (user.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException("The student dashboard requires a student account.");
        }
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        Label heading = new Label("Student suggestions");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #163a43;");
        Label greeting = new Label("Welcome, " + user.getFullName());
        Button logout = new Button("Sign out");
        logout.setOnAction(event -> new LoginView(stage).show());
        HBox top = new HBox(14, heading, greeting, logout);
        top.setPadding(new Insets(18, 22, 18, 22));
        top.setStyle("-fx-background-color: #e3eeea;");
        HBox.setHgrow(greeting, Priority.ALWAYS);

        TextField subject = new TextField();
        subject.setPromptText("Suggestion subject");
        TextArea message = new TextArea();
        message.setPromptText("Write your suggestion");
        message.setWrapText(true);
        message.setPrefRowCount(3);
        Button submit = new Button("Submit suggestion");
        submit.setStyle("-fx-background-color: #176b67; -fx-text-fill: white; -fx-font-weight: bold;");
        submit.setOnAction(event -> submit(subject.getText(), message.getText()));
        VBox form = new VBox(9, new Label("New suggestion"), subject, message, submit);
        form.setPadding(new Insets(18, 22, 12, 22));

        configureTable();
        Label historyTitle = new Label("Your past suggestions");
        historyTitle.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #163a43;");
        VBox history = new VBox(9, historyTitle, table);
        history.setPadding(new Insets(4, 22, 20, 22));
        VBox.setVgrow(table, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(new VBox(form, history));
        stage.setTitle("School Suggestion System | Student");
        stage.setScene(new Scene(root, 900, 650));
        stage.show();
        refresh();
    }

    private void configureTable() {
        TableColumn<Suggestion, String> subjectColumn = new TableColumn<>("Subject");
        subjectColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSubject()));
        TableColumn<Suggestion, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMessage()));
        TableColumn<Suggestion, String> dateColumn = new TableColumn<>("Date submitted");
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFormattedDate()));
        table.getColumns().setAll(subjectColumn, messageColumn, dateColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("You have not submitted any suggestions yet."));
    }

    private void submit(String subject, String message) {
        if (subject.isBlank() || message.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing information", "Enter both a subject and a message.");
            return;
        }
        try {
            suggestionDAO.create(user.getId(), subject.trim(), message.trim());
            refresh();
            showAlert(Alert.AlertType.INFORMATION, "Suggestion submitted", "Your suggestion has been saved.");
        } catch (SQLException exception) {
            showAlert(Alert.AlertType.ERROR, "Database error", exception.getMessage());
        }
    }

    private void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(suggestionDAO.findByStudentId(user.getId())));
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
