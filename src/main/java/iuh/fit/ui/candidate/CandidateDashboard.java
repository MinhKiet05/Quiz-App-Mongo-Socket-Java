package iuh.fit.ui.candidate;

import iuh.fit.dto.QuizDTO;
import iuh.fit.repository.impl.QuizRepositoryImpl;
import iuh.fit.service.impl.QuizServiceImpl;
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
import java.util.ArrayList;
import java.util.List;

public class CandidateDashboard {
    private Stage primaryStage;
    private ListView<QuizDTO> quizListView;
    private ProgressIndicator loadingIndicator;

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

        Scene scene = new Scene(rootLayout, 900, 700);
        primaryStage.setTitle("Quiz App - Bảng điều khiển thí sinh");
        primaryStage.setScene(scene);
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
        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(30));
        centerLayout.setStyle("-fx-background-color: #f5f5f5;");

        Label listTitleLabel = new Label("Danh sách các đề thi có sẵn");
        listTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        listTitleLabel.setTextFill(Color.web("#333333"));

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(50, 50);

        quizListView = new ListView<>();
        quizListView.setPrefHeight(400);
        quizListView.setStyle("-fx-border-radius: 5; -fx-control-inner-background: white;");
        quizListView.setCellFactory(param -> new QuizListCell());

        centerLayout.getChildren().addAll(listTitleLabel, loadingIndicator, quizListView);
        VBox.setVgrow(quizListView, Priority.ALWAYS);

        return centerLayout;
    }

    private void loadQuizzes() {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                List<QuizDTO> quizzes = new ArrayList<>();
                quizzes.add(QuizDTO.builder()
                    .id("quiz_001")
                    .title("Thi thử Lập trình mạng (Socket)")
                    .durationMinutes(15)
                    .status("PUBLISHED")
                    .questionIds(java.util.Arrays.asList("Q1", "Q2", "Q3"))
                    .build());
                quizzes.add(QuizDTO.builder()
                    .id("quiz_002")
                    .title("Bài tập trắc nghiệm RMI")
                    .durationMinutes(20)
                    .status("PUBLISHED")
                    .questionIds(java.util.Arrays.asList("Q1", "Q2", "Q3", "Q4"))
                    .build());

                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    quizListView.getItems().addAll(quizzes);
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
