import javafx.application.Application;
import javafx.stage.Stage;
import ui.LoginView;

/** Starts the School Suggestion System JavaFX application. */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        new LoginView(stage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
