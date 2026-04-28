package iuh.fit.ui.login;

import iuh.fit.dto.UserDTO;
import iuh.fit.network.QuizClientService;
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
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

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
        userIdLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        userIdLabel.setMaxWidth(Double.MAX_VALUE);
        userIdLabel.setAlignment(Pos.CENTER_LEFT);
        userIdField = new TextField();
        userIdField.setPromptText("Nhập mã số");
        userIdField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-radius: 5;");

        Label passwordLabel = new Label("Mật khẩu:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        passwordLabel.setMaxWidth(Double.MAX_VALUE);
        passwordLabel.setAlignment(Pos.CENTER_LEFT);
        passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu");
        passwordField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-radius: 5;");

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11;");
        messageLabel.setWrapText(true);

        VBox buttonContainer = new VBox(10);
        VBox.setMargin(buttonContainer, new Insets(-20, 0, 0, 0));
        buttonContainer.setAlignment(Pos.CENTER);

        loginButton = new Button("Đăng nhập");
        loginButton.setStyle("-fx-font-size: 14; -fx-padding: 10 50; -fx-background-color: #667eea; " +
                "-fx-text-fill: white; -fx-border-radius: 5; -fx-cursor: hand;");
        loginButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        loginButton.setMaxWidth(Double.MAX_VALUE); 
        loginButton.setOnAction(e -> handleLogin());

        Button exitButton = new Button("Thoát");
        exitButton.setStyle(
                "-fx-font-size: 14; " +
                "-fx-padding: 10 50; " +
                "-fx-background-color: transparent; " + // Nền trong suốt
                "-fx-text-fill: #e74c3c; " +           // Chữ màu đỏ (mã màu cũ của bạn)
                "-fx-border-color: #e74c3c; " +         // Viền màu đỏ
                "-fx-border-width: 1.5; " +             // Độ dày viền
                "-fx-border-radius: 5; " +              // Bo góc (khớp với TextField)
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
        );
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Giữ nguyên yêu cầu về độ rộng của bạn
        exitButton.setMaxWidth(Double.MAX_VALUE); 

        // Gọi hàm handleExit để hiển thị thông báo xác nhận
        exitButton.setOnAction(e -> handleExit());

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

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Scene scene = new Scene(mainLayout, bounds.getWidth(), bounds.getHeight());
        primaryStage.setTitle("Quiz App - Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
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
        messageLabel.setText("Đang kết nối tới server...");
        messageLabel.setTextFill(Color.BLUE);

        new Thread(() -> {
            try {
                // Kết nối tới QuizServer
                QuizClientService client = QuizClientService.getInstance();
                if (!client.isConnected()) {
                    client.connect();
                }

                // Gửi request đăng nhập
                UserDTO userDTO = client.login(userId, password);

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
                    String errorMsg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định";
                    messageLabel.setText(errorMsg);
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setWrapText(true);
                    loginButton.setDisable(false);

                    // In chi tiết lỗi ra console
                    System.err.println("[LoginForm] Error: " + errorMsg);
                    e.printStackTrace();
                });
            }
        }).start();
    }
    
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận thoát");
        alert.setHeaderText(null); // Để null nếu bạn muốn giao diện gọn gàng hơn
        alert.setContentText("Bạn có chắc chắn muốn đóng ứng dụng không?");

        // Tùy chỉnh nút bấm Tiếng Việt (nếu muốn)
        ButtonType buttonTypeYes = new ButtonType("Có", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("Không", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        if (alert.showAndWait().get() == buttonTypeYes) {
            System.exit(0);
        }
    }
}
