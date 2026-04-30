package iuh.fit.ui.lecturer;

import iuh.fit.dto.QuestionDTO;
import iuh.fit.dto.SubjectDTO;
import iuh.fit.network.QuizClientService;
import iuh.fit.ui.shared.SessionManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * QuestionBankForm - Quản lý ngân hàng câu hỏi theo môn học
 */
public class QuestionBankForm {
    private Stage stage;
    private ComboBox<SubjectItem> subjectComboBox;
    private TableView<QuestionDTO> questionTable;
    private TextArea contentField;
    private ComboBox<String> difficultyComboBox;
    private TextField optionAField;
    private TextField optionBField;
    private TextField optionCField;
    private TextField optionDField;
    private ComboBox<String> correctAnswerComboBox;
    
    private List<QuestionDTO> currentQuestions = new ArrayList<>();
    private QuestionDTO selectedQuestion = null;

    public static void show(Stage parentStage) {
        QuestionBankForm form = new QuestionBankForm();
        form.display(parentStage);
    }

    private void display(Stage parentStage) {
        stage = new Stage();
        stage.initOwner(parentStage);
        stage.setTitle("Quản lý ngân hàng câu hỏi");

        VBox rootLayout = new VBox(20);
        rootLayout.setPadding(new Insets(20));
        rootLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Header
        HBox headerBox = createHeaderBox();
        rootLayout.getChildren().add(headerBox);

        // Main content with HBox (Table on left, Form on right)
        HBox mainContent = new HBox(20);
        mainContent.setPrefHeight(500);

        // Left side: Table
        VBox leftPanel = createTablePanel();
        HBox.setHgrow(leftPanel, javafx.scene.layout.Priority.ALWAYS);
        mainContent.getChildren().add(leftPanel);

        // Right side: Form
        VBox rightPanel = createFormPanel();
        rightPanel.setPrefWidth(300);
        mainContent.getChildren().add(rightPanel);

        rootLayout.getChildren().add(mainContent);

        // Bottom: Buttons
        HBox buttonBox = createButtonBox();
        rootLayout.getChildren().add(buttonBox);

        // Full screen setup (like LecturerDashboard)
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Scene scene = new Scene(rootLayout, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.show();

        // Load danh sách môn học
        loadSubjects();
    }

    private HBox createHeaderBox() {
        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(15));
        headerBox.setStyle("-fx-background-color: #667eea; -fx-border-radius: 5;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("📚 QUẢN LÝ NGÂN HÀNG CÂU HỎI");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);

        Label subjectLabel = new Label("Chọn môn học:");
        subjectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        subjectLabel.setTextFill(Color.WHITE);

        subjectComboBox = new ComboBox<>();
        subjectComboBox.setPrefWidth(250);
        subjectComboBox.setStyle(
            "-fx-font-size: 12; -fx-padding: 8; -fx-background-color: white;"
        );
        subjectComboBox.setOnAction(e -> handleSubjectChange());

        headerBox.getChildren().addAll(titleLabel, subjectLabel, subjectComboBox);
        HBox.setHgrow(headerBox, javafx.scene.layout.Priority.ALWAYS);

        return headerBox;
    }

    private VBox createTablePanel() {
        VBox tablePanel = new VBox(10);
        tablePanel.setPadding(new Insets(15));
        tablePanel.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        Label tableTitle = new Label("Danh sách câu hỏi");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        tableTitle.setTextFill(Color.web("#333333"));

        questionTable = new TableView<>();
        questionTable.setStyle("-fx-font-size: 11;");

        // Tạo các cột
        TableColumn<QuestionDTO, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);

        TableColumn<QuestionDTO, String> contentCol = new TableColumn<>("Nội dung");
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        contentCol.setPrefWidth(300);

        TableColumn<QuestionDTO, String> difficultyCol = new TableColumn<>("Mức độ");
        difficultyCol.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        difficultyCol.setPrefWidth(80);

        TableColumn<QuestionDTO, String> correctAnswerCol = new TableColumn<>("Đáp án");
        correctAnswerCol.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
        correctAnswerCol.setPrefWidth(80);

        questionTable.getColumns().addAll(idCol, contentCol, difficultyCol, correctAnswerCol);

        // Handle row selection
        questionTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedQuestion = newSelection;
                    populateFormFields(newSelection);
                }
            }
        );

        tablePanel.getChildren().addAll(tableTitle, questionTable);
        VBox.setVgrow(questionTable, javafx.scene.layout.Priority.ALWAYS);

        return tablePanel;
    }

    private VBox createFormPanel() {
        VBox formPanel = new VBox(12);
        formPanel.setPadding(new Insets(15));
        formPanel.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        Label formTitle = new Label("Thông tin câu hỏi");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        formTitle.setTextFill(Color.web("#333333"));
        formPanel.getChildren().add(formTitle);

        // Nội dung
        Label contentLabel = new Label("Nội dung:");
        contentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        contentField = new TextArea();
        contentField.setPrefHeight(80);
        contentField.setWrapText(true);
        contentField.setStyle(
            "-fx-font-size: 11; -fx-padding: 8; -fx-background-color: white; " +
            "-fx-border-color: #ddd; -fx-border-radius: 3;"
        );
        formPanel.getChildren().addAll(contentLabel, contentField);

        // Mức độ
        Label difficultyLabel = new Label("Mức độ:");
        difficultyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        difficultyComboBox = new ComboBox<>();
        difficultyComboBox.getItems().addAll("Dễ", "Trung bình", "Khó");
        difficultyComboBox.setStyle("-fx-font-size: 11; -fx-padding: 8;");
        formPanel.getChildren().addAll(difficultyLabel, difficultyComboBox);

        // Đáp án A
        Label optionALabel = new Label("Đáp án A:");
        optionALabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        optionAField = new TextField();
        optionAField.setPromptText("Nhập đáp án A");
        optionAField.setStyle("-fx-font-size: 11; -fx-padding: 8;");
        formPanel.getChildren().addAll(optionALabel, optionAField);

        // Đáp án B
        Label optionBLabel = new Label("Đáp án B:");
        optionBLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        optionBField = new TextField();
        optionBField.setPromptText("Nhập đáp án B");
        optionBField.setStyle("-fx-font-size: 11; -fx-padding: 8;");
        formPanel.getChildren().addAll(optionBLabel, optionBField);

        // Đáp án C
        Label optionCLabel = new Label("Đáp án C:");
        optionCLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        optionCField = new TextField();
        optionCField.setPromptText("Nhập đáp án C");
        optionCField.setStyle("-fx-font-size: 11; -fx-padding: 8;");
        formPanel.getChildren().addAll(optionCLabel, optionCField);

        // Đáp án D
        Label optionDLabel = new Label("Đáp án D:");
        optionDLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        optionDField = new TextField();
        optionDField.setPromptText("Nhập đáp án D");
        optionDField.setStyle("-fx-font-size: 11; -fx-padding: 8;");
        formPanel.getChildren().addAll(optionDLabel, optionDField);

        // Đáp án đúng
        Label correctAnswerLabel = new Label("Đáp án đúng:");
        correctAnswerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        correctAnswerComboBox = new ComboBox<>();
        correctAnswerComboBox.getItems().addAll("A", "B", "C", "D");
        correctAnswerComboBox.setStyle("-fx-font-size: 11; -fx-padding: 8;");
        formPanel.getChildren().addAll(correctAnswerLabel, correctAnswerComboBox);

        VBox.setVgrow(formPanel, javafx.scene.layout.Priority.ALWAYS);
        return formPanel;
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15));

        Button addButton = new Button("➕ Thêm");
        addButton.setPrefWidth(120);
        addButton.setStyle(
            "-fx-font-size: 12; -fx-padding: 10; -fx-background-color: #667eea; " +
            "-fx-text-fill: white; -fx-border-radius: 5; -fx-font-weight: bold;"
        );
        addButton.setOnAction(e -> handleAddQuestion());

        Button updateButton = new Button("✏️ Sửa");
        updateButton.setPrefWidth(120);
        updateButton.setStyle(
            "-fx-font-size: 12; -fx-padding: 10; -fx-background-color: #42a5f5; " +
            "-fx-text-fill: white; -fx-border-radius: 5; -fx-font-weight: bold;"
        );
        updateButton.setOnAction(e -> handleUpdateQuestion());

        Button deleteButton = new Button("🗑️ Xóa");
        deleteButton.setPrefWidth(120);
        deleteButton.setStyle(
            "-fx-font-size: 12; -fx-padding: 10; -fx-background-color: #ef5350; " +
            "-fx-text-fill: white; -fx-border-radius: 5; -fx-font-weight: bold;"
        );
        deleteButton.setOnAction(e -> handleDeleteQuestion());

        Button refreshButton = new Button("🔄 Làm mới");
        refreshButton.setPrefWidth(120);
        refreshButton.setStyle(
            "-fx-font-size: 12; -fx-padding: 10; -fx-background-color: #66bb6a; " +
            "-fx-text-fill: white; -fx-border-radius: 5; -fx-font-weight: bold;"
        );
        refreshButton.setOnAction(e -> handleRefresh());

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, refreshButton);
        return buttonBox;
    }

    // ============ Event Handlers ============

    private void handleSubjectChange() {
        SubjectItem selected = subjectComboBox.getValue();
        if (selected != null) {
            System.out.println("[QuestionBank] Selected subject: " + selected.getName() + " (ID: " + selected.getId() + ")");
            loadQuestionsForSubject(selected.getId());
        }
    }

    private void loadQuestionsForSubject(String subjectId) {
        clearForm();
        selectedQuestion = null;
        questionTable.getItems().clear();

        new Thread(() -> {
            try {
                System.out.println("[QuestionBank] Loading questions for subject: " + subjectId);
                // Gọi Socket để lấy câu hỏi từ server
                List<QuestionDTO> questions = QuizClientService.getInstance().getQuestionsBySubject(subjectId);
                
                Platform.runLater(() -> {
                    currentQuestions = questions;
                    questionTable.getItems().addAll(questions);
                    showInfo("Thông báo", "Đã tải " + questions.size() + " câu hỏi");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Lỗi", "Lỗi khi tải câu hỏi: " + e.getMessage());
                });
            }
        }).start();
    }

    private void populateFormFields(QuestionDTO question) {
        contentField.setText(question.getContent());
        
        // Map difficulty từ English sang Vietnamese để hiển thị
        String difficultyDisplay = mapDifficultyToVietnamese(question.getDifficulty());
        difficultyComboBox.setValue(difficultyDisplay);
        
        // Map correctAnswer từ nội dung sang "A", "B", "C", "D" để hiển thị
        if (question.getOptions() != null && question.getOptions().size() >= 4) {
            optionAField.setText(question.getOptions().get(0));
            optionBField.setText(question.getOptions().get(1));
            optionCField.setText(question.getOptions().get(2));
            optionDField.setText(question.getOptions().get(3));
            
            // Tìm index của correctAnswer trong mảng options
            String correctContent = question.getCorrectAnswer();
            for (int i = 0; i < question.getOptions().size(); i++) {
                if (question.getOptions().get(i).equals(correctContent)) {
                    correctAnswerComboBox.setValue(String.valueOf((char) ('A' + i)));
                    break;
                }
            }
        }
    }

    private void clearForm() {
        contentField.clear();
        difficultyComboBox.setValue(null);
        optionAField.clear();
        optionBField.clear();
        optionCField.clear();
        optionDField.clear();
        correctAnswerComboBox.setValue(null);
    }

    private void handleAddQuestion() {
        // Validation
        if (contentField.getText().isEmpty() || difficultyComboBox.getValue() == null) {
            showError("Lỗi", "Vui lòng điền đầy đủ nội dung và mức độ!");
            return;
        }

        if (optionAField.getText().isEmpty() || optionBField.getText().isEmpty() ||
            optionCField.getText().isEmpty() || optionDField.getText().isEmpty()) {
            showError("Lỗi", "Vui lòng điền đầy đủ 4 đáp án!");
            return;
        }

        if (correctAnswerComboBox.getValue() == null) {
            showError("Lỗi", "Vui lòng chọn đáp án đúng!");
            return;
        }

        SubjectItem subject = subjectComboBox.getValue();
        if (subject == null) {
            showError("Lỗi", "Vui lòng chọn môn học!");
            return;
        }

        // Lấy các option từ form
        List<String> options = List.of(
            optionAField.getText(),
            optionBField.getText(),
            optionCField.getText(),
            optionDField.getText()
        );
        
        // Convert correctAnswer từ "A", "B", "C", "D" sang nội dung thực tế
        String selectedAnswerKey = correctAnswerComboBox.getValue();
        int answerIndex = selectedAnswerKey.charAt(0) - 'A';
        String correctAnswerContent = options.get(answerIndex);
        
        // Map difficulty từ Vietnamese sang English
        String difficultyEnglish = mapDifficultyToEnglish(difficultyComboBox.getValue());
        
        // Lấy createdBy từ SessionManager (ID của người đăng nhập)
        String createdBy = SessionManager.getInstance().getCurrentUser().getId();
        
        // Generate ID cho câu hỏi mới
        String questionId = UUID.randomUUID().toString();

        // Build QuestionDTO
        QuestionDTO newQuestion = QuestionDTO.builder()
                .id(questionId)  // ✅ Set ID ngay từ client
                .content(contentField.getText())
                .difficulty(difficultyEnglish)  // ✅ Sử dụng tiếng Anh
                .options(options)
                .correctAnswer(correctAnswerContent)  // ✅ Sử dụng nội dung thực tế
                .subjectId(subject.getId())
                .createdBy(createdBy)  // ✅ Thêm createdBy
                .build();

        System.out.println("[QuestionBank] Adding question: " + newQuestion.getContent());
        System.out.println("[QuestionBank] ID: " + questionId);
        System.out.println("[QuestionBank] SubjectId: " + subject.getId());
        System.out.println("[QuestionBank] CreatedBy: " + createdBy);
        System.out.println("[QuestionBank] Difficulty: " + difficultyEnglish);
        System.out.println("[QuestionBank] CorrectAnswer: " + correctAnswerContent);

        new Thread(() -> {
            try {
                // Gọi Socket để thêm câu hỏi vào server
                QuizClientService.getInstance().addQuestion(newQuestion);
                
                Platform.runLater(() -> {
                    showInfo("Thành công", "Thêm câu hỏi thành công!");
                    clearForm();
                    loadQuestionsForSubject(subject.getId()); // Reload danh sách
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Lỗi", "Lỗi khi thêm câu hỏi: " + e.getMessage());
                });
            }
        }).start();
    }

    private void handleUpdateQuestion() {
        if (selectedQuestion == null) {
            showError("Lỗi", "Vui lòng chọn câu hỏi để sửa!");
            return;
        }

        if (contentField.getText().isEmpty() || difficultyComboBox.getValue() == null) {
            showError("Lỗi", "Vui lòng điền đầy đủ nội dung và mức độ!");
            return;
        }

        if (optionAField.getText().isEmpty() || optionBField.getText().isEmpty() ||
            optionCField.getText().isEmpty() || optionDField.getText().isEmpty()) {
            showError("Lỗi", "Vui lòng điền đầy đủ 4 đáp án!");
            return;
        }

        if (correctAnswerComboBox.getValue() == null) {
            showError("Lỗi", "Vui lòng chọn đáp án đúng!");
            return;
        }

        // Lấy các option từ form
        List<String> options = List.of(
            optionAField.getText(),
            optionBField.getText(),
            optionCField.getText(),
            optionDField.getText()
        );
        
        // Convert correctAnswer từ "A", "B", "C", "D" sang nội dung thực tế
        String selectedAnswerKey = correctAnswerComboBox.getValue();
        int answerIndex = selectedAnswerKey.charAt(0) - 'A';
        String correctAnswerContent = options.get(answerIndex);
        
        // Map difficulty từ Vietnamese sang English
        String difficultyEnglish = mapDifficultyToEnglish(difficultyComboBox.getValue());

        // BẢO TOÀN: Chỉ cập nhật các trường cho phép thay đổi
        // Giữ lại subjectId và createdBy từ dòng được chọn (selectedQuestion)
        selectedQuestion.setContent(contentField.getText());
        selectedQuestion.setDifficulty(difficultyEnglish);  // ✅ Sử dụng tiếng Anh
        selectedQuestion.setOptions(options);
        selectedQuestion.setCorrectAnswer(correctAnswerContent);  // ✅ Sử dụng nội dung thực tế
        // KHÔNG ghi đè subjectId và createdBy - giữ nguyên từ selectedQuestion

        System.out.println("[QuestionBank] Updating question: " + selectedQuestion.getId());
        System.out.println("[QuestionBank] Difficulty: " + difficultyEnglish);
        System.out.println("[QuestionBank] CorrectAnswer: " + correctAnswerContent);
        System.out.println("[QuestionBank] Preserving subjectId: " + selectedQuestion.getSubjectId());
        System.out.println("[QuestionBank] Preserving createdBy: " + selectedQuestion.getCreatedBy());

        new Thread(() -> {
            try {
                // Gọi Socket để cập nhật câu hỏi trên server
                QuizClientService.getInstance().updateQuestion(selectedQuestion);
                
                Platform.runLater(() -> {
                    showInfo("Thành công", "Cập nhật câu hỏi thành công!");
                    clearForm();
                    loadQuestionsForSubject(subjectComboBox.getValue().getId()); // Reload danh sách
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Lỗi", "Lỗi khi cập nhật câu hỏi: " + e.getMessage());
                });
            }
        }).start();
    }

    private void handleDeleteQuestion() {
        if (selectedQuestion == null) {
            showError("Lỗi", "Vui lòng chọn câu hỏi để xóa!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận");
        confirmAlert.setHeaderText("Xóa câu hỏi");
        confirmAlert.setContentText("Bạn có chắc chắn muốn xóa câu hỏi này không?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            System.out.println("[QuestionBank] Deleting question: " + selectedQuestion.getId());

            new Thread(() -> {
                try {
                    // Gọi Socket để xóa câu hỏi trên server
                    QuizClientService.getInstance().deleteQuestion(selectedQuestion.getId());
                    
                    Platform.runLater(() -> {
                        showInfo("Thành công", "Xóa câu hỏi thành công!");
                        clearForm();
                        selectedQuestion = null;
                        SubjectItem subject = subjectComboBox.getValue();
                        if (subject != null) {
                            loadQuestionsForSubject(subject.getId());
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showError("Lỗi", "Lỗi khi xóa câu hỏi: " + e.getMessage());
                    });
                }
            }).start();
        }
    }

    private void handleRefresh() {
        SubjectItem subject = subjectComboBox.getValue();
        if (subject != null) {
            loadQuestionsForSubject(subject.getId());
        } else {
            showError("Lỗi", "Vui lòng chọn môn học!");
        }
    }

    // ============ Helper Methods ============

    private void loadSubjects() {
        new Thread(() -> {
            try {
                System.out.println("[QuestionBank] Loading subjects from server...");
                // Gọi Socket để lấy danh sách môn học từ server
                List<SubjectDTO> subjects = QuizClientService.getInstance().getAllSubjects();
                
                Platform.runLater(() -> {
                    subjectComboBox.getItems().clear();
                    for (SubjectDTO subject : subjects) {
                        subjectComboBox.getItems().add(new SubjectItem(subject.getId(), subject.getName()));
                    }
                    System.out.println("[QuestionBank] Loaded " + subjects.size() + " subjects");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.err.println("[QuestionBank] Error loading subjects: " + e.getMessage());
                    e.printStackTrace();
                    // Fallback: Load dữ liệu mô phỏng
                    subjectComboBox.getItems().addAll(
                        new SubjectItem("SUBJ001", "Toán học"),
                        new SubjectItem("SUBJ002", "Tiếng Anh"),
                        new SubjectItem("SUBJ003", "Vật lý"),
                        new SubjectItem("SUBJ004", "Hóa học")
                    );
                });
            }
        }).start();
    }

private String mapDifficultyToVietnamese(String difficulty) {
    if (difficulty == null) return "Trung bình";
    return switch (difficulty.trim().toLowerCase()) {
        case "easy" -> "Dễ";
        case "medium" -> "Trung bình";
        case "hard" -> "Khó";
        default -> difficulty;
    };
}

private String mapDifficultyToEnglish(String difficulty) {
    if (difficulty == null) return "Medium";
    return switch (difficulty.trim().toLowerCase()) {
        case "dễ" -> "Easy";
        case "trung bình" -> "Medium";
        case "khó" -> "Hard";
        default -> difficulty;
    };
}    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ============ Helper Methods for Data Conversion ============

    /**
     * Map difficulty từ Vietnamese sang English
     * "Dễ" → "Easy"
     * "Trung bình" → "Medium"
     * "Khó" → "Hard"
     */
    private String mapDifficultyToEnglish(String vietnameseDifficulty) {
        if (vietnameseDifficulty == null) return "Medium";
        return switch (vietnameseDifficulty) {
            case "Dễ" -> "Easy";
            case "Khó" -> "Hard";
            default -> "Medium";  // Trung bình
        };
    }

    /**
     * Map difficulty từ English sang Vietnamese (ngược lại)
     * "Easy" → "Dễ"
     * "Medium" → "Trung bình"
     * "Hard" → "Khó"
     */
    private String mapDifficultyToVietnamese(String englishDifficulty) {
        if (englishDifficulty == null) return "Trung bình";
        return switch (englishDifficulty) {
            case "Easy" -> "Dễ";
            case "Hard" -> "Khó";
            default -> "Trung bình";  // Medium
        };
    }


    static class SubjectItem {
        private String id;
        private String name;

        public SubjectItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
