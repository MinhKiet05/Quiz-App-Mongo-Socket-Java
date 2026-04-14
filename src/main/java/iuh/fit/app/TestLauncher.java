package iuh.fit.app;

import iuh.fit.dto.QuizDTO;
import iuh.fit.dto.SubmissionDTO;
import iuh.fit.dto.UserDTO;
import iuh.fit.ui.candidate.CandidateDashboard;
import iuh.fit.ui.candidate.ExamForm;
import iuh.fit.ui.candidate.ResultForm;
import iuh.fit.ui.lecturer.LecturerDashboard;
import iuh.fit.ui.login.LoginForm;
import iuh.fit.ui.shared.SessionManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Test Launcher - Chọn form để test trực tiếp
 */
public class TestLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-background-color: #667eea;");
        mainLayout.setPadding(new Insets(40));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("🧪 Test Launcher - Chọn Form");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Label descLabel = new Label("Chọn form bạn muốn test:");
        descLabel.setFont(Font.font("Arial", 14));
        descLabel.setTextFill(Color.WHITE);

        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.TOP_CENTER);
        buttonBox.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-border-radius: 10;");
        buttonBox.setPrefWidth(400);

        // Login Form
        Button loginBtn = createButton("1️⃣ LoginForm - Đăng nhập", e -> {
            LoginForm.show(primaryStage);
        });

        // Candidate Dashboard
        Button dashboardBtn = createButton("2️⃣ CandidateDashboard - Dashboard Thí sinh", e -> {
            setupTestUser("CANDIDATE");
            CandidateDashboard.show(primaryStage);
        });

        // Exam Form
        Button examBtn = createButton("3️⃣ ExamForm - Làm bài thi", e -> {
            setupTestUser("CANDIDATE");
            QuizDTO quiz = QuizDTO.builder()
                    .id("quiz_001")
                    .title("Test Quiz - Lập trình Java")
                    .durationMinutes(15)
                    .status("PUBLISHED")
                    .questionIds(java.util.Arrays.asList("Q1", "Q2", "Q3", "Q4", "Q5"))
                    .build();
            ExamForm.show(primaryStage, quiz);
        });

        // Result Form
        Button resultBtn = createButton("4️⃣ ResultForm - Kết quả bài thi", e -> {
            setupTestUser("CANDIDATE");
            SubmissionDTO submission = new SubmissionDTO();
            submission.setQuizId("quiz_001");
            submission.setScore(7.5);
            ResultForm.show(primaryStage, submission, 7.5);
        });

        // Lecturer Dashboard
        Button lecturerBtn = createButton("5️⃣ LecturerDashboard - Dashboard Giảng viên", e -> {
            setupTestUser("LECTURER");
            LecturerDashboard.show(primaryStage);
        });

        buttonBox.getChildren().addAll(
                loginBtn,
                dashboardBtn,
                examBtn,
                resultBtn,
                lecturerBtn
        );

        mainLayout.getChildren().addAll(titleLabel, descLabel, buttonBox);

        Scene scene = new Scene(mainLayout, 600, 600);
        primaryStage.setTitle("Quiz App - Test Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-font-size: 14; -fx-padding: 15; -fx-background-color: #667eea; " +
                "-fx-text-fill: white; -fx-border-radius: 5;");
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setPrefWidth(350);
        btn.setWrapText(true);
        btn.setOnAction(action);
        return btn;
    }

    private void setupTestUser(String role) {
        UserDTO testUser = UserDTO.builder()
                .id("test123")
                .username("Người Dùng Test")
                .role(role)
                .status("ACTIVE")
                .build();
        SessionManager.getInstance().setCurrentUser(testUser);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

