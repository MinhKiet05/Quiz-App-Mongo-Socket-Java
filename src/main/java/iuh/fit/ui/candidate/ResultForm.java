package iuh.fit.ui.candidate;

import iuh.fit.dto.SubmissionDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ResultForm {
    private Stage primaryStage;
    private SubmissionDTO submission;
    private double score;

    public static void show(Stage primaryStage, SubmissionDTO submission, double score) {
        ResultForm form = new ResultForm();
        form.display(primaryStage, submission, score);
    }

    private void display(Stage stage, SubmissionDTO submission, double score) {
        this.primaryStage = stage;
        this.submission = submission;
        this.score = score;

        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-background-color: #667eea;");
        mainLayout.setPadding(new Insets(40));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        VBox resultContainer = new VBox(20);
        resultContainer.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        resultContainer.setPadding(new Insets(40));
        resultContainer.setMaxWidth(600);

        Label titleLabel = new Label("Kết quả bài thi");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#333333"));

        VBox scoreBox = createScoreBox();
        VBox statsBox = createStatsBox();
        HBox buttonBox = createButtonBox();

        resultContainer.getChildren().addAll(
                titleLabel,
                new Separator(),
                scoreBox,
                new Separator(),
                statsBox,
                new Separator(),
                buttonBox
        );

        ScrollPane scrollPane = new ScrollPane(resultContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-control-inner-background: transparent;");

        mainLayout.getChildren().add(scrollPane);

        Scene scene = new Scene(mainLayout, 700, 600);
        primaryStage.setTitle("Quiz App - Kết quả");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createScoreBox() {
        VBox scoreBox = new VBox(15);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setStyle("-fx-padding: 20; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");

        Label scoreLabel = new Label(String.format("%.2f", score));
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        scoreLabel.setTextFill(score >= 5 ? Color.web("#27ae60") : Color.web("#e74c3c"));

        Label outOfLabel = new Label("/ 10");
        outOfLabel.setFont(Font.font("Arial", 18));
        outOfLabel.setTextFill(Color.web("#666666"));

        String resultText = score >= 5 ? "✓ ĐỖ" : "✗ KHÔNG ĐỖ";
        Label resultLabel = new Label(resultText);
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        resultLabel.setTextFill(score >= 5 ? Color.web("#27ae60") : Color.web("#e74c3c"));

        HBox scoreDisplay = new HBox(10);
        scoreDisplay.setAlignment(Pos.CENTER);
        scoreDisplay.getChildren().addAll(scoreLabel, outOfLabel);

        scoreBox.getChildren().addAll(scoreDisplay, resultLabel);
        return scoreBox;
    }

    private VBox createStatsBox() {
        VBox statsBox = new VBox(10);

        Label statsTitle = new Label("Thống kê");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        statsTitle.setTextFill(Color.web("#333333"));

        int correctCount = (int) (score / 10.0 * 10);
        int totalCount = 10;
        int incorrectCount = totalCount - correctCount;

        HBox statRow1 = createStatRow("Số câu đúng:", correctCount + "/" + totalCount, Color.web("#27ae60"));
        HBox statRow2 = createStatRow("Số câu sai:", incorrectCount + "/" + totalCount, Color.web("#e74c3c"));

        statsBox.getChildren().addAll(statsTitle, statRow1, statRow2);
        return statsBox;
    }

    private HBox createStatRow(String label, String value, Color color) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10;");

        Label labelComponent = new Label(label);
        labelComponent.setFont(Font.font("Arial", 12));
        labelComponent.setTextFill(Color.web("#666666"));
        labelComponent.setMinWidth(100);

        Label valueComponent = new Label(value);
        valueComponent.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        valueComponent.setTextFill(color);

        row.getChildren().addAll(labelComponent, valueComponent);
        return row;
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("Quay lại");
        backButton.setStyle("-fx-font-size: 12; -fx-padding: 10 30; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-border-radius: 5;");
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        backButton.setOnAction(e -> CandidateDashboard.show(primaryStage));

        Button exitButton = new Button("Thoát");
        exitButton.setStyle("-fx-font-size: 12; -fx-padding: 10 30; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 5;");
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        exitButton.setOnAction(e -> System.exit(0));

        buttonBox.getChildren().addAll(backButton, exitButton);
        return buttonBox;
    }
}
