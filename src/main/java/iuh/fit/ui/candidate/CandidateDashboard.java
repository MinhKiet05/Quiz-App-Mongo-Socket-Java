package iuh.fit.ui.candidate;

import iuh.fit.dto.QuizDTO;
import iuh.fit.network.QuizClientService;
import iuh.fit.ui.login.LoginForm;
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
import java.util.List;

public class CandidateDashboard {
    private Stage primaryStage;
    private ListView<QuizDTO> quizListView;
    private ProgressIndicator loadingIndicator;
    private List<QuizDTO> allQuizzes = new java.util.ArrayList<>();

    public static void show(Stage primaryStage) {
        CandidateDashboard dashboard = new CandidateDashboard();
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
        primaryStage.setTitle("Quiz App - Bảng điều khiển thí sinh");
        primaryStage.setScene(scene);
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.show();

        loadQuizzes();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setStyle("-fx-background-color: #667eea; -fx-padding: 20;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Bảng điều khiển Thí sinh");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Label welcomeLabel = new Label("Xin chào: " + 
            (SessionManager.getInstance().getCurrentUser() != null ? 
            SessionManager.getInstance().getCurrentUser().getUsername() : ""));
        welcomeLabel.setFont(Font.font("Arial", 14));
        welcomeLabel.setTextFill(Color.WHITE);

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
    // 1. Giảm spacing tổng thể từ 20 xuống 10
    VBox centerLayout = new VBox(10); 
    centerLayout.setPadding(new Insets(30));
    centerLayout.setStyle("-fx-background-color: #f5f5f5;");

    Label listTitleLabel = new Label("Danh sách các đề");
    listTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    listTitleLabel.setTextFill(Color.web("#333333"));
    
    // Tạo thêm khoảng trống nhỏ dưới tiêu đề để nó không dính vào thanh tìm kiếm
    VBox.setMargin(listTitleLabel, new Insets(0, 0, 5, 0));

    TextField searchField = new TextField();
    searchField.setPromptText("🔍 Tìm kiếm đề thi theo tên...");
    searchField.setStyle(
            "-fx-font-size: 13; -fx-padding: 12; -fx-background-color: white; " +
            "-fx-border-color: #667eea; -fx-border-radius: 5; -fx-border-width: 2; " +
            "-fx-text-fill: #333333; -fx-prompt-text-fill: #999999;"
    );
    searchField.setPrefHeight(40);

    // 2. Loại bỏ khoảng trống thừa của LoadingIndicator khi không hiển thị
    loadingIndicator = new ProgressIndicator();
    loadingIndicator.setPrefSize(40, 40);
    loadingIndicator.setManaged(true); // Đảm bảo nó vẫn chiếm chỗ khi xoay

    quizListView = new ListView<>();
    quizListView.setPrefHeight(410);
    quizListView.setStyle("-fx-border-radius: 5; -fx-control-inner-background: white;");
    quizListView.setCellFactory(param -> new QuizListCell());

    // 3. ĐẶC BIỆT: Giảm khoảng cách giữa searchField và loading/listView
    // Nếu loadingIndicator không hiện, danh sách sẽ nhích lên sát searchField
    VBox.setMargin(searchField, new Insets(0, 0, 0, 0)); 

    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
        filterQuizzes(newValue.toLowerCase());
    });

    centerLayout.getChildren().addAll(listTitleLabel, searchField, loadingIndicator, quizListView);
    VBox.setVgrow(quizListView, Priority.ALWAYS);

    return centerLayout;
}

    private void filterQuizzes(String searchText) {
        quizListView.getItems().clear();
        
        if (searchText.isEmpty()) {
            quizListView.getItems().addAll(allQuizzes);
        } else {
            for (QuizDTO quiz : allQuizzes) {
                if (quiz.getTitle().toLowerCase().contains(searchText)) {
                    quizListView.getItems().add(quiz);
                }
            }
        }
    }

    private void loadQuizzes() {
        new Thread(() -> {
            try {
                QuizClientService client = QuizClientService.getInstance();
                allQuizzes = client.getAllQuizzes();

                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    quizListView.getItems().addAll(allQuizzes);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText("Lỗi tải danh sách");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
            }
        }).start();
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

    private class QuizListCell extends ListCell<QuizDTO> {
        @Override
        protected void updateItem(QuizDTO quiz, boolean empty) {
            super.updateItem(quiz, empty);

            if (empty || quiz == null) {
                setGraphic(null);
                setText(null);
            } else {
                VBox container = new VBox(8);
                container.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: white;");
                container.setPrefHeight(120);

                Label titleLabel = new Label(quiz.getTitle());
                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                titleLabel.setTextFill(Color.web("#333333"));

                HBox infoBox = new HBox(20);
                infoBox.setAlignment(Pos.CENTER_LEFT);

                Label durationLabel = new Label("⏱ Thời gian: " + quiz.getDurationMinutes() + " phút");
                durationLabel.setFont(Font.font("Arial", 12));
                durationLabel.setTextFill(Color.web("#666666"));

                Label statusLabel = new Label("📌 Trạng thái: " + quiz.getStatus());
                statusLabel.setFont(Font.font("Arial", 12));
                statusLabel.setTextFill(Color.web("#666666"));

                Label questionsLabel = new Label("❓ Số câu: " + quiz.getQuestionIds().size());
                questionsLabel.setFont(Font.font("Arial", 12));
                questionsLabel.setTextFill(Color.web("#666666"));

                infoBox.getChildren().addAll(durationLabel, statusLabel, questionsLabel);

                Button startButton = new Button("Bắt đầu làm bài");
                startButton.setStyle("-fx-font-size: 12; -fx-padding: 8 20; -fx-background-color: #667eea; -fx-text-fill: white; -fx-border-radius: 5;");
                startButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                startButton.setOnAction(e -> ExamForm.show(primaryStage, quiz));

                HBox.setHgrow(infoBox, Priority.ALWAYS);
                container.getChildren().addAll(titleLabel, infoBox, startButton);

                setGraphic(container);
                setText(null);
            }
        }
    }
}
