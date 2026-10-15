package ui;

import dao.SuggestionDAO;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.Suggestion;
import model.User;

/** Lets administrators review all suggestions and delete them after confirmation. */
public class AdminDashboard {
    private final javafx.stage.Stage stage;
    private final SuggestionDAO suggestionDAO = new SuggestionDAO();
    private final TableView<Suggestion> table = new TableView<>();

    public AdminDashboard(javafx.stage.Stage stage, User user) {
        if (user.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("The admin dashboard requires an administrator account.");
        }
        this.stage = stage;
    }

    public void show() {
        Label heading = new Label("All student suggestions");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #163a43;");
        Button refresh = new Button("Refresh");
        refresh.setOnAction(event -> loadSuggestions());
        Button delete = new Button("Delete selected");
        delete.setStyle("-fx-background-color: #a43d36; -fx-text-fill: white; -fx-font-weight: bold;");
        delete.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        delete.setOnAction(event -> deleteSelected());
        Button logout = new Button("Sign out");
        logout.setOnAction(event -> new LoginView(stage).show());
        HBox actions = new HBox(9, refresh, delete, logout);
        HBox header = new HBox(18, heading, actions);
        header.setPadding(new Insets(18, 22, 18, 22));
        header.setStyle("-fx-background-color: #e3eeea;");

        configureTable();
        BorderPane root = new BorderPane(table);
        root.setTop(header);
        BorderPane.setMargin(table, new Insets(20, 22, 22, 22));
        stage.setTitle("School Suggestion System | Administration");
        stage.setScene(new Scene(root, 1050, 620));
        stage.show();
        loadSuggestions();
    }

    private void configureTable() {
        TableColumn<Suggestion, String> studentColumn = new TableColumn<>("Student name");
        studentColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStudentName()));
        TableColumn<Suggestion, String> subjectColumn = new TableColumn<>("Subject");
        subjectColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSubject()));
        TableColumn<Suggestion, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMessage()));
        TableColumn<Suggestion, String> dateColumn = new TableColumn<>("Date submitted");
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFormattedDate()));
        table.getColumns().setAll(studentColumn, subjectColumn, messageColumn, dateColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("There are no suggestions to display."));
    }

    private void loadSuggestions() {
        try {
            table.setItems(FXCollections.observableArrayList(suggestionDAO.findAll()));
        } catch (SQLException exception) {
            showAlert(Alert.AlertType.ERROR, "Database error", exception.getMessage());
        }
    }

    private void deleteSelected() {
        Suggestion selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.initOwner(stage);
        confirmation.setTitle("Confirm deletion");
        confirmation.setHeaderText("Delete this suggestion?");
        confirmation.setContentText("This action cannot be undone.");
        if (confirmation.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
            return;
        }
        try {
            suggestionDAO.deleteById(selected.getId());
            loadSuggestions();
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
