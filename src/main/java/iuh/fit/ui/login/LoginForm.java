package iuh.fit.ui.login;

import iuh.fit.dto.UserDTO;
import iuh.fit.repository.impl.UserRepositoryImpl;
import iuh.fit.service.impl.UserServiceImpl;
import iuh.fit.ui.candidate.CandidateDashboard;
import iuh.fit.ui.lecturer.LecturerDashboard;
import iuh.fit.ui.shared.SessionManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginForm {
    private Stage primaryStage;
    private TextField userIdField;
    private PasswordField passwordField;
    private Label messageLabel;
    private Button loginButton;

    public static void show(Stage primaryStage) {
        LoginForm form = new LoginForm();
        form.display(primaryStage);
    }

    private void display(Stage stage) {
        this.primaryStage = stage;

        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-background-color: #667eea;");
        mainLayout.setPadding(new Insets(40));
        mainLayout.setAlignment(Pos.CENTER);

        VBox formContainer = new VBox(20);
        formContainer.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        formContainer.setPadding(new Insets(40));
        formContainer.setMaxWidth(400);
        formContainer.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Quiz Application");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#333333"));

        Label subtitleLabel = new Label("Đăng nhập để tiếp tục");
        subtitleLabel.setFont(Font.font("Arial", 12));
        subtitleLabel.setTextFill(Color.web("#666666"));

        Label userIdLabel = new Label("Mã số / ID:");
        userIdLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        userIdField = new TextField();
        userIdField.setPromptText("Nhập mã số");
        userIdField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-radius: 5;");

        Label passwordLabel = new Label("Mật khẩu:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu");
        passwordField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-radius: 5;");

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11;");
        messageLabel.setWrapText(true);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        loginButton = new Button("Đăng nhập");
        loginButton.setStyle("-fx-font-size: 14; -fx-padding: 10 50; -fx-background-color: #667eea; " +
                "-fx-text-fill: white; -fx-border-radius: 5; -fx-cursor: hand;");
        loginButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        loginButton.setPrefWidth(150);
        loginButton.setOnAction(e -> handleLogin());

        Button exitButton = new Button("Thoát");
        exitButton.setStyle("-fx-font-size: 14; -fx-padding: 10 50; -fx-background-color: #e74c3c; " +
                "-fx-text-fill: white; -fx-border-radius: 5; -fx-cursor: hand;");
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        exitButton.setPrefWidth(150);
        exitButton.setOnAction(e -> System.exit(0));

        buttonContainer.getChildren().addAll(loginButton, exitButton);

        formContainer.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                new Separator(),
                userIdLabel,
                userIdField,
                passwordLabel,
                passwordField,
                messageLabel,
                buttonContainer
        );

        mainLayout.getChildren().add(formContainer);

        Scene scene = new Scene(mainLayout, 600, 700);
        primaryStage.setTitle("Quiz App - Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.show();

        passwordField.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String password = passwordField.getText().trim();

        if (userId.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Vui lòng nhập đầy đủ thông tin!");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        loginButton.setDisable(true);
        messageLabel.setText("Đang xử lý...");
        messageLabel.setTextFill(Color.BLUE);

        new Thread(() -> {
            try {
                UserServiceImpl userService = new UserServiceImpl(new UserRepositoryImpl());
                UserDTO userDTO = userService.login(userId, password);

                SessionManager.getInstance().setCurrentUser(userDTO);

                Platform.runLater(() -> {
                    if ("LECTURER".equalsIgnoreCase(userDTO.getRole())) {
                        LecturerDashboard.show(primaryStage);
                    } else if ("CANDIDATE".equalsIgnoreCase(userDTO.getRole())) {
                        CandidateDashboard.show(primaryStage);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    messageLabel.setText("Lỗi: " + e.getMessage());
                    messageLabel.setTextFill(Color.RED);
                    loginButton.setDisable(false);
                });
            }
        }).start();
    }
}
