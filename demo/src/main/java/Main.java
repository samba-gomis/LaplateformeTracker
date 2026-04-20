import database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class Main extends Application {

    // Window settings
    private static final String APP_TITLE  = "La Plateforme Tracker";
    private static final int    WIDTH      = 900;
    private static final int    HEIGHT     = 600;
    private static final String LOGIN_FXML = "/view/LoginView.fxml";

    // JavaFX start method
    // Called automatically by JavaFX after launch()
    // Loads the login screen and displays the main window
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the login FXML view
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_FXML));
            Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);

            // Apply the global stylesheet
            scene.getStylesheets().add(getClass().getResource("/view/loginview.css").toExternalForm());

            // Configure the main window
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(700);
            primaryStage.setMinHeight(450);

            // Close the DB connection cleanly when the window is closed
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("Closing application...");
                DatabaseConnection.getInstance().closeConnection();
            });

            primaryStage.show();

        } catch (IOException e) {
            System.err.println(" Failed to load the view: " + e.getMessage());
            System.err.println(" Check that " + LOGIN_FXML + " exists in resources");
        }
    }

    // Application shutdown
    // Called when the application stops (backup safety close)
    // Ensures the database connection is always closed even if setOnCloseRequest was not triggered
     
    @Override
    public void stop() {
        DatabaseConnection.getInstance().closeConnection();
    }

    // Program entry point
    public static void main(String[] args) {
        launch(args);
    }
}