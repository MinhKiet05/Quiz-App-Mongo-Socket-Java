package iuh.fit.app;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class - Điểm khởi động ứng dụng JavaFX
 * Khởi động TestLauncher
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        TestLauncher launcher = new TestLauncher();
        launcher.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}