package iuh.fit.ui.candidate;

import iuh.fit.dto.QuestionDTO;
import iuh.fit.dto.QuizDTO;
import iuh.fit.dto.SubmissionDTO;
import iuh.fit.network.QuizClientService;
import iuh.fit.ui.shared.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.time.Instant;
import java.util.*;

public class ExamForm {
    private Stage primaryStage;
    private QuizDTO quiz;
    private Map<Integer, String> answers = new HashMap<>();
    private int currentQuestionIndex = 0;
    private Label timerLabel;
    private ProgressBar progressBar;
    private VBox questionContainer;
    private Button previousButton, nextButton;
    private int timeRemaining;
    private List<QuestionDTO> questions = new ArrayList<>();
    private Timeline timerTimeline;

    public static void show(Stage primaryStage, QuizDTO quiz) {
        ExamForm form = new ExamForm();
        form.display(primaryStage, quiz);
    }

    private void display(Stage stage, QuizDTO quiz) {
        this.primaryStage = stage;
        this.quiz = quiz;
        this.timeRemaining = quiz.getDurationMinutes() * 60;

        loadQuizFromServer(quiz.getId()); // Load quiz details từ server

        BorderPane rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #f5f5f5;");

        HBox header = createHeader();
        rootLayout.setTop(header);

        VBox centerLayout = createCenterLayout();
        rootLayout.setCenter(centerLayout);

        HBox footer = createFooter();
        rootLayout.setBottom(footer);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Scene scene = new Scene(rootLayout, bounds.getWidth(), bounds.getHeight());
        primaryStage.setTitle("Quiz App - Làm bài thi: " + quiz.getTitle());
        primaryStage.setScene(scene);
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.show();

        startTimer();
    }

    private void loadQuizFromServer(String quizId) {
        new Thread(() -> {
            try {
                QuizClientService client = QuizClientService.getInstance();
                QuizDTO fullQuiz = client.getQuizById(quizId);
                
                Platform.runLater(() -> {
                    this.quiz = fullQuiz;
                    this.questions = fullQuiz.getQuestions() != null ? fullQuiz.getQuestions() : new ArrayList<>();
                    
                    if (questions.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi");
                        alert.setHeaderText("Không có câu hỏi");
                        alert.setContentText("Đề thi này không có câu hỏi.");
                        alert.showAndWait();
                        return;
                    }
                    
                    displayQuestion(0);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText("Không thể tải đề thi");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
            }
        }).start();
    }
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setStyle("-fx-background-color: #667eea; -fx-padding: 15;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(quiz.getTitle());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        timerLabel = new Label("⏱ Thời gian: " + quiz.getDurationMinutes() + " phút");
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        timerLabel.setTextFill(Color.WHITE);

        header.getChildren().addAll(titleLabel, spacer, timerLabel);
        return header;
    }

    private VBox createCenterLayout() {
        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setStyle("-fx-background-color: #f5f5f5;");

        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        Label progressLabel = new Label("Tiến độ:");
        progressLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);
        progressBox.getChildren().addAll(progressLabel, progressBar);

        questionContainer = new VBox(15);
        questionContainer.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-padding: 20; -fx-background-color: white;");
        questionContainer.setPrefHeight(400);

        ScrollPane scrollPane = new ScrollPane(questionContainer);
        scrollPane.setFitToWidth(true);

        centerLayout.getChildren().addAll(progressBox, new Separator(), scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return centerLayout;
    }

    private HBox createFooter() {
        HBox footer = new HBox(15);
        footer.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");
        footer.setAlignment(Pos.CENTER_RIGHT);

        previousButton = new Button("⬅ Câu trước");
        previousButton.setStyle("-fx-font-size: 12; -fx-padding: 10 20; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-border-radius: 5;");
        previousButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        previousButton.setOnAction(e -> previousQuestion());

        nextButton = new Button("Câu tiếp theo ➜");
        nextButton.setStyle("-fx-font-size: 12; -fx-padding: 10 20; -fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5;");
        nextButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        nextButton.setOnAction(e -> nextQuestion());

        Button submitButton = new Button("Nộp bài");
        submitButton.setStyle("-fx-font-size: 12; -fx-padding: 10 30; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 5;");
        submitButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        submitButton.setOnAction(e -> submitExam());

        Button exitButton = new Button("Thoát");
        exitButton.setStyle("-fx-font-size: 12; -fx-padding: 10 20; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 5;");
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        exitButton.setOnAction(e -> handleExit());

        footer.getChildren().addAll(previousButton, nextButton, submitButton, exitButton);
        return footer;
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;

        currentQuestionIndex = index;

        double progress = (double) (index + 1) / questions.size();
        progressBar.setProgress(progress);

        questionContainer.getChildren().clear();

        QuestionDTO question = questions.get(index);

        Label questionLabel = new Label((index + 1) + ". " + question.getContent());
        questionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        questionLabel.setTextFill(Color.web("#333333"));
        questionLabel.setWrapText(true);

        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(15, 0, 0, 0));

        ToggleGroup toggleGroup = new ToggleGroup();
        String previousAnswer = answers.get(index); // Lấy đáp án đã chọn trước đó

        for (String option : question.getOptions()) {
            RadioButton radioButton = new RadioButton(option);
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setStyle("-fx-font-size: 12;");
            radioButton.setFont(Font.font("Arial", 12));

            // Nếu đáp án này trùng với đáp án đã chọn trước đó, hãy chọn nó
            if (option.equals(previousAnswer)) {
                radioButton.setSelected(true);
            }

            final String opt = option;
            radioButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    answers.put(index, opt);
                }
            });

            optionsBox.getChildren().add(radioButton);
        }

        previousButton.setDisable(currentQuestionIndex == 0);
        nextButton.setDisable(currentQuestionIndex == questions.size() - 1);

        questionContainer.getChildren().addAll(
                questionLabel,
                new Separator(),
                optionsBox
        );
    }

    private void previousQuestion() {
        if (currentQuestionIndex > 0) {
            displayQuestion(currentQuestionIndex - 1);
        }
    }

    private void nextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            displayQuestion(currentQuestionIndex + 1);
        }
    }

    private void submitExam() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận nộp bài");
        confirmAlert.setHeaderText("Nộp bài thi");
        confirmAlert.setContentText("Bạn có chắc chắn muốn nộp bài?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            // Dừng timer
            if (timerTimeline != null) {
                timerTimeline.stop();
            }

            // Tạo SubmissionDTO với details
            SubmissionDTO submission = new SubmissionDTO();
            submission.setQuizId(quiz.getId());
            submission.setCandidateId(SessionManager.getInstance().getCurrentUser().getId());
            submission.setStartTime(Instant.now().toString());
            submission.setSubmitTime(Instant.now().toString());
            
            // Tạo danh sách details từ answers
            List<SubmissionDTO.SubmissionDetailDTO> details = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                SubmissionDTO.SubmissionDetailDTO detail = new SubmissionDTO.SubmissionDetailDTO();
                detail.setQuestionId(questions.get(i).getId());
                detail.setSelectedOption(answers.getOrDefault(i, "")); // Lấy đáp án đã chọn
                details.add(detail);
            }
            submission.setDetails(details);

            // Gửi submission tới server
            new Thread(() -> {
                try {
                    QuizClientService client = QuizClientService.getInstance();
                    SubmissionDTO gradedSubmission = client.submitQuiz(submission);

                    Platform.runLater(() -> {
                        ResultForm.show(primaryStage, gradedSubmission, gradedSubmission.getScore());
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Lỗi");
                        errorAlert.setHeaderText("Lỗi nộp bài");
                        errorAlert.setContentText(e.getMessage());
                        errorAlert.showAndWait();
                    });
                }
            }).start();
        }
    }

    private void startTimer() {
        timerTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    timeRemaining--;
                    updateTimerLabel();
                    if (timeRemaining <= 0) {
                        timerTimeline.stop();
                        submitExam();
                    }
                })
        );
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void updateTimerLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("⏱ Thời gian: %02d:%02d", minutes, seconds));

        if (timeRemaining <= 300) {
            timerLabel.setStyle("-fx-text-fill: #f39c12;");
        }
        if (timeRemaining <= 60) {
            timerLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Thoát");
        alert.setHeaderText("Thoát khỏi bài thi");
        alert.setContentText("Bạn chưa nộp bài. Bạn có chắc chắn muốn thoát?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            CandidateDashboard.show(primaryStage);
        }
    }
}
