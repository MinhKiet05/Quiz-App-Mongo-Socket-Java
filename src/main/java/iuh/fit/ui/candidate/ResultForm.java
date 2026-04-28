package iuh.fit.ui.candidate;

import iuh.fit.dto.SubmissionDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Line;

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

        // 1. Sử dụng StackPane làm gốc để căn giữa tuyệt đối mọi thứ
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #667eea;"); // Giữ màu nền xanh tím của bạn

        // 2. Thẻ Card màu trắng chứa nội dung
        VBox resultCard = new VBox(25);
        resultCard.setMaxWidth(500); // Giới hạn chiều rộng
        resultCard.setMaxHeight(VBox.USE_PREF_SIZE); // Không cho kéo dãn chiều cao
        resultCard.setAlignment(Pos.TOP_CENTER);
        // Style: Nền trắng, bo góc mềm, thêm Drop Shadow tạo chiều sâu
        resultCard.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-radius: 15; " +
                "-fx-background-radius: 15; " +
                "-fx-padding: 40 50; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 20, 0, 0, 10);"
        );

        // Header
        Label titleLabel = new Label("KẾT QUẢ BÀI THI");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#1e293b")); // Đen nhạt hiện đại

        // Nội dung chính
        VBox scoreBox = createScoreBox();
        VBox statsBox = createStatsBox();
        HBox buttonBox = createButtonBox();

        // Gắn các thành phần vào Card, dùng custom Separator cho mềm mại
        resultCard.getChildren().addAll(
                titleLabel,
                scoreBox,
                createCustomSeparator(),
                statsBox,
                createCustomSeparator(),
                buttonBox
        );

        root.getChildren().add(resultCard);

        // Hiển thị Fullscreen tinh tế
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        
        primaryStage.setTitle("Quiz App - Kết quả");
        primaryStage.setScene(scene);
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.show();
    }

    private VBox createScoreBox() {
        VBox scoreBox = new VBox(10);
        scoreBox.setAlignment(Pos.CENTER);
        
        boolean isPass = score >= 5.0;
        // Đổi màu nền của khối điểm số dựa trên kết quả
        String bgColor = isPass ? "#f0fdf4" : "#fef2f2"; // Xanh lá siêu nhạt hoặc đỏ siêu nhạt
        String textColorHex = isPass ? "#16a34a" : "#dc2626"; // Xanh đậm hoặc đỏ đậm
        
        scoreBox.setStyle(
                "-fx-padding: 30; " +
                "-fx-background-color: " + bgColor + "; " +
                "-fx-background-radius: 10;"
        );

        // Cấu hình nhãn Điểm số
        Label scoreLabel = new Label(String.format("%.2f", score));
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        scoreLabel.setTextFill(Color.web(textColorHex));

        Label outOfLabel = new Label("/ 10");
        outOfLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        outOfLabel.setTextFill(Color.web("#64748b")); // Xám trung tính

        HBox scoreDisplay = new HBox(10);
        scoreDisplay.setAlignment(Pos.BASELINE_CENTER); // Căn chân chữ ngang nhau
        scoreDisplay.getChildren().addAll(scoreLabel, outOfLabel);

        // Cấu hình nhãn Trạng thái (Đỗ/Trượt)
        String resultText = isPass ? "✓ CHÚC MỪNG BẠN ĐÃ ĐỖ" : "✗ RẤT TIẾC, BẠN CHƯA ĐẠT";
        Label resultLabel = new Label(resultText);
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        resultLabel.setTextFill(Color.web(textColorHex));

        scoreBox.getChildren().addAll(scoreDisplay, resultLabel);
        return scoreBox;
    }

    private VBox createStatsBox() {
        VBox statsBox = new VBox(15);
        statsBox.setAlignment(Pos.CENTER);

        Label statsTitle = new Label("Chi tiết thống kê");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        statsTitle.setTextFill(Color.web("#475569"));

        // Tính số câu đúng và sai từ submission details thực tế
        int correctCount = 0;
        int incorrectCount = 0;
        int totalCount = 0;
        
        if (submission != null && submission.getDetails() != null) {
            for (SubmissionDTO.SubmissionDetailDTO detail : submission.getDetails()) {
                totalCount++;
                if (detail.isCorrect()) {
                    correctCount++;
                } else {
                    incorrectCount++;
                }
            }
        }

        HBox statRow1 = createStatRow("Số câu đúng:", correctCount + " / " + totalCount, Color.web("#16a34a"));
        HBox statRow2 = createStatRow("Số câu sai:", incorrectCount + " / " + totalCount, Color.web("#dc2626"));

        statsBox.getChildren().addAll(statsTitle, statRow1, statRow2);
        return statsBox;
    }

    private HBox createStatRow(String label, String value, Color valueColor) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 8 20; -fx-background-color: #f8fafc; -fx-background-radius: 5;"); // Thêm nền xám cực nhạt cho từng dòng

        Label labelComponent = new Label(label);
        labelComponent.setFont(Font.font("Arial", 14));
        labelComponent.setTextFill(Color.web("#64748b"));
        
        // Thêm một Pane trống để đẩy Value sang góc phải (như Flexbox space-between)
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label valueComponent = new Label(value);
        valueComponent.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        valueComponent.setTextFill(valueColor);

        row.getChildren().addAll(labelComponent, spacer, valueComponent);
        return row;
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button backButton = new Button("Quay lại màn hình chính");
        backButton.setStyle(
                "-fx-font-size: 14; " +
                "-fx-padding: 12 25; " +
                "-fx-background-color: #3b82f6; " + // Xanh dương cho hành động chính
                "-fx-text-fill: white; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
        );
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        backButton.setOnAction(e -> CandidateDashboard.show(primaryStage));

        Button exitButton = new Button("Thoát");
        exitButton.setStyle(
                "-fx-font-size: 14; " +
                "-fx-padding: 12 35; " +
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #ef4444; " + 
                "-fx-border-color: #ef4444; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
        ); // Thiết kế nút thoát dạng Outline để giảm sự tập trung vào nó
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        exitButton.setOnAction(e -> handleExit());

        buttonBox.getChildren().addAll(backButton, exitButton);
        return buttonBox;
    }

    // Tiện ích tạo đường kẻ mỏng, tinh tế hơn Separator mặc định của JavaFX
    private StackPane createCustomSeparator() {
        Line line = new Line(0, 0, 400, 0);
        line.setStroke(Color.web("#e2e8f0")); // Xám rất nhạt
        StackPane pane = new StackPane(line);
        pane.setPadding(new Insets(10, 0, 10, 0));
        return pane;
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