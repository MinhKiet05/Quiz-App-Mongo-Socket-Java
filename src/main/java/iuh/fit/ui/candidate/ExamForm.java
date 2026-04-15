package iuh.fit.ui.candidate;

import iuh.fit.dto.QuizDTO;
import iuh.fit.dto.SubmissionDTO;
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
    private List<String> questions = new ArrayList<>();

    public static void show(Stage primaryStage, QuizDTO quiz) {
        ExamForm form = new ExamForm();
        form.display(primaryStage, quiz);
    }

    private void display(Stage stage, QuizDTO quiz) {
        this.primaryStage = stage;
        this.quiz = quiz;
        this.timeRemaining = quiz.getDurationMinutes() * 60;
        
        for (int i = 0; i < quiz.getQuestionIds().size(); i++) {
            questions.add("Câu hỏi " + (i + 1));
        }

        BorderPane rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #f5f5f5;");

        HBox header = createHeader();
        rootLayout.setTop(header);

        VBox centerLayout = createCenterLayout();
        rootLayout.setCenter(centerLayout);

        HBox footer = createFooter();
        rootLayout.setBottom(footer);

        Scene scene = new Scene(rootLayout, 1000, 700);
        primaryStage.setTitle("Quiz App - Làm bài thi: " + quiz.getTitle());
        primaryStage.setScene(scene);
        primaryStage.show();

        displayQuestion(0);
        startTimer();
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

        Label questionLabel = new Label((index + 1) + ". " + questions.get(index));
        questionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        questionLabel.setTextFill(Color.web("#333333"));
        questionLabel.setWrapText(true);

        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(15, 0, 0, 0));

        ToggleGroup toggleGroup = new ToggleGroup();
        String[] options = {"Đáp án A", "Đáp án B", "Đáp án C", "Đáp án D"};
        String selectedAnswer = answers.get(index);

        for (String option : options) {
            RadioButton radioButton = new RadioButton(option);
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setStyle("-fx-font-size: 12;");
            radioButton.setFont(Font.font("Arial", 12));

            if (option.equals(selectedAnswer)) {
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
            // Tính điểm (placeholder)
            int correctCount = 0;
            for (String ans : answers.values()) {
                if (ans != null && ans.startsWith("Đáp án A")) {
                    correctCount++;
                }
            }
            double score = (correctCount * 10.0) / questions.size();

            SubmissionDTO submission = new SubmissionDTO();
            submission.setQuizId(quiz.getId());
            submission.setScore(score);

            ResultForm.show(primaryStage, submission, score);
        }
    }

    private void startTimer() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    timeRemaining--;
                    updateTimerLabel();
                    if (timeRemaining <= 0) {
                        submitExam();
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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
