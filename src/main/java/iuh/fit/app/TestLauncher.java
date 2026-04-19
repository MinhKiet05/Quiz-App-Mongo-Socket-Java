package iuh.fit.app;

import iuh.fit.ui.login.LoginForm;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * TestLauncher - Khởi động form đăng nhập
 */
public class TestLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        LoginForm.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

