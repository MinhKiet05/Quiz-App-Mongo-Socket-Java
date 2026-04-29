package iuh.fit.ui.lecturer;

import iuh.fit.ui.login.LoginForm;
import iuh.fit.ui.profile.ProfileForm;
import iuh.fit.ui.shared.SessionManager;
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

public class LecturerDashboard {
    private Stage primaryStage;

    public static void show(Stage primaryStage) {
        LecturerDashboard dashboard = new LecturerDashboard();
        dashboard.display(primaryStage);
    }

    private void display(Stage stage) {
        this.primaryStage = stage;

        BorderPane rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #f5f5f5;");

        HBox header = createHeader();
        rootLayout.setTop(header);

        VBox centerLayout = createCenterLayout();
        rootLayout.setCenter(centerLayout);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Scene scene = new Scene(rootLayout, bounds.getWidth(), bounds.getHeight());
        primaryStage.setTitle("Quiz App - Bảng điều khiển giảng viên");
        primaryStage.setScene(scene);
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setStyle("-fx-background-color: #667eea; -fx-padding: 20;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Bảng điều khiển Giảng viên");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Label welcomeLabel = new Label("Xin chào: " + 
            (SessionManager.getInstance().getCurrentUser() != null ? 
            SessionManager.getInstance().getCurrentUser().getUsername() : ""));
        welcomeLabel.setFont(Font.font("Arial", 14));
        welcomeLabel.setTextFill(Color.WHITE);
        // Add underline and make it clickable
        welcomeLabel.setStyle("-fx-text-decoration: underline;");
        welcomeLabel.setCursor(javafx.scene.Cursor.HAND);
        welcomeLabel.setOnMouseClicked(e -> ProfileForm.show(primaryStage));

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Đăng xuất");
        logoutButton.setStyle("-fx-font-size: 12; -fx-padding: 8 20; -fx-background-color: white; -fx-text-fill: #667eea; -fx-border-radius: 5;");
        logoutButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        logoutButton.setOnAction(e -> handleLogout());

        header.getChildren().addAll(titleLabel, welcomeLabel, spacer, logoutButton);
        return header;
    }

    private VBox createCenterLayout() {
        VBox centerLayout = new VBox(30);
        centerLayout.setPadding(new Insets(40));
        centerLayout.setStyle("-fx-background-color: #f5f5f5;");
        centerLayout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Chức năng Quản lý");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#333333"));

        VBox featureBox = createFeatureBox("📚 Quản lý ngân hàng câu hỏi", "Quản lý các câu hỏi theo từng môn học");

        centerLayout.getChildren().addAll(titleLabel, featureBox);
        return centerLayout;
    }

    private VBox createFeatureBox(String title, String description) {
        VBox box = new VBox(15);
        box.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-padding: 20; -fx-background-color: white;");
        box.setPrefSize(300, 180);
        box.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web("#333333"));

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", 12));
        descLabel.setTextFill(Color.web("#666666"));
        descLabel.setWrapText(true);

        Button actionButton = new Button("Truy cập");
        actionButton.setStyle("-fx-font-size: 12; -fx-padding: 10 20; -fx-background-color: #667eea; -fx-text-fill: white; -fx-border-radius: 5;");
        actionButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        actionButton.setOnAction(e -> QuestionBankForm.show(primaryStage));

        box.getChildren().addAll(titleLabel, descLabel, actionButton);
        return box;
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText("Thông tin");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Đăng xuất");
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            SessionManager.getInstance().logout();
            LoginForm.show(primaryStage);
        }
    }
}
