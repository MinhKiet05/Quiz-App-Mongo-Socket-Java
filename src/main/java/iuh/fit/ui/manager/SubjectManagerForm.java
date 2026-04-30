package iuh.fit.ui.manager;

import iuh.fit.dto.SubjectDTO;
import iuh.fit.network.QuizClientService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;

/**
 * SubjectManagerForm - Quản lý danh mục môn học (CRUD) - Full Screen + Modal Overlay
 */
public class SubjectManagerForm {

    private Stage stage;
    private TableView<SubjectDTO> subjectTable;
    private StackPane rootStack;

    // Form fields (dùng chung cho Add & Edit modal)
    private TextField idField;
    private TextField nameField;
    private TextField courseCodeField;
    private TextArea descriptionField;
    private Label modalTitle;
    private VBox modalOverlay;
    private VBox deleteOverlay;
    private Label deleteConfirmLabel;

    private SubjectDTO selectedSubject = null;
    private boolean isEditMode = false;

    public static void show(Stage parentStage) {
        SubjectManagerForm form = new SubjectManagerForm();
        form.display(parentStage);
    }

    private void display(Stage parentStage) {
        stage = new Stage();
        stage.initOwner(parentStage);
        stage.setTitle("Quản lý môn học");

        // Full screen
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        // Root: StackPane to allow overlay modals
        rootStack = new StackPane();

        // ── Main content ──
        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: #f5f5f5;");
        main.setTop(createHeader());
        main.setCenter(createTablePanel());
        main.setBottom(createButtonBar());

        // ── Modals (hidden by default) ──
        modalOverlay = buildFormModal();
        modalOverlay.setVisible(false);

        deleteOverlay = buildDeleteModal();
        deleteOverlay.setVisible(false);

        rootStack.getChildren().addAll(main, modalOverlay, deleteOverlay);

        Scene scene = new Scene(rootStack, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.show();

        loadSubjects();
    }

    // ══════════════════════════════════════════
    //  MAIN LAYOUT
    // ══════════════════════════════════════════

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setStyle("-fx-background-color: #667eea;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("📖");
        icon.setFont(Font.font("Arial", 26));

        Label title = new Label("QUẢN LÝ MÔN HỌC");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("← Quay lại Dashboard");
        backBtn.setStyle(
            "-fx-background-color: white; -fx-text-fill: #667eea;" +
            "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 8 18;" +
            "-fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;");
        backBtn.setOnAction(e -> stage.close());

        header.getChildren().addAll(icon, title, spacer, backBtn);
        return header;
    }

    private VBox createTablePanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(25, 30, 10, 30));

        // Sub-header with count
        HBox subHeader = new HBox(10);
        subHeader.setAlignment(Pos.CENTER_LEFT);
        Label listTitle = new Label("Danh sách môn học");
        listTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        listTitle.setTextFill(Color.web("#333"));
        subHeader.getChildren().add(listTitle);

        subjectTable = new TableView<>();
        subjectTable.setStyle("-fx-font-size: 13; -fx-background-radius: 8;");
        subjectTable.setPlaceholder(new Label("Chưa có dữ liệu môn học"));
        subjectTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<SubjectDTO, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(140);

        TableColumn<SubjectDTO, String> nameCol = new TableColumn<>("Tên môn học");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<SubjectDTO, String> codeCol = new TableColumn<>("Mã môn học");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        codeCol.setMaxWidth(160);

        TableColumn<SubjectDTO, String> descCol = new TableColumn<>("Mô tả");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        subjectTable.getColumns().addAll(idCol, nameCol, codeCol, descCol);

        subjectTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, sel) -> { if (sel != null) selectedSubject = sel; }
        );

        panel.getChildren().addAll(subHeader, subjectTable);
        VBox.setVgrow(subjectTable, Priority.ALWAYS);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    private HBox createButtonBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(16, 30, 20, 30));
        bar.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        Button refreshBtn = mkBtn("🔄 Tải lại", "#78909c");
        refreshBtn.setOnAction(e -> loadSubjects());

        Button deleteBtn = mkBtn("🗑️ Xóa", "#ef5350");
        deleteBtn.setOnAction(e -> openDeleteModal());

        Button editBtn = mkBtn("✏️ Chỉnh sửa", "#42a5f5");
        editBtn.setOnAction(e -> openEditModal());

        Button addBtn = mkBtn("➕ Thêm môn học", "#667eea");
        addBtn.setPrefWidth(160);
        addBtn.setOnAction(e -> openAddModal());

        bar.getChildren().addAll(refreshBtn, deleteBtn, editBtn, addBtn);
        return bar;
    }

    // ══════════════════════════════════════════
    //  MODAL OVERLAY: Add / Edit Form
    // ══════════════════════════════════════════

    private VBox buildFormModal() {
        // Semi-transparent backdrop
        VBox overlay = new VBox();
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        overlay.setFillWidth(true);
        StackPane.setAlignment(overlay, Pos.CENTER);

        // White card
        VBox card = new VBox(20);
        card.setMaxWidth(560);
        card.setPadding(new Insets(36, 40, 36, 40));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 24, 0.3, 0, 6);"
        );

        // Modal header
        modalTitle = new Label();
        modalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        modalTitle.setTextFill(Color.web("#333"));

        Separator sep = new Separator();

        // ID field
        VBox idBox = fieldGroup("Mã ID", "id-box");
        idField = styledInput("Để trống → tự động tạo");
        idBox.getChildren().add(idField);

        // Name field
        VBox nameBox = fieldGroup("Tên môn học *", "name-box");
        nameField = styledInput("VD: Lập trình Java");
        nameBox.getChildren().add(nameField);

        // Course code field
        VBox codeBox = fieldGroup("Mã môn học *", "code-box");
        courseCodeField = styledInput("VD: JAVA101");
        codeBox.getChildren().add(courseCodeField);

        // Description field
        VBox descBox = fieldGroup("Mô tả", "desc-box");
        descriptionField = new TextArea();
        descriptionField.setPromptText("Nhập mô tả môn học...");
        descriptionField.setPrefHeight(90);
        descriptionField.setWrapText(true);
        descriptionField.setStyle("-fx-font-size: 13; -fx-padding: 8; -fx-border-color: #ddd; -fx-border-radius: 6; -fx-background-radius: 6;");
        descBox.getChildren().add(descriptionField);

        // Buttons
        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.setPadding(new Insets(8, 0, 0, 0));

        Button cancelBtn = new Button("Hủy");
        cancelBtn.setPrefWidth(110);
        cancelBtn.setStyle(
            "-fx-font-size: 13; -fx-padding: 10 0; -fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #555; -fx-border-radius: 6; -fx-background-radius: 6;" +
            "-fx-border-color: #ddd; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> hideFormModal());

        Button saveBtn = new Button("💾 Lưu");
        saveBtn.setPrefWidth(130);
        saveBtn.setStyle(
            "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 10 0;" +
            "-fx-background-color: #667eea; -fx-text-fill: white;" +
            "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> { if (isEditMode) doUpdate(); else doAdd(); });

        btnRow.getChildren().addAll(cancelBtn, saveBtn);

        card.getChildren().addAll(modalTitle, sep, idBox, nameBox, codeBox, descBox, btnRow);
        overlay.getChildren().add(card);

        // Close on backdrop click
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) hideFormModal();
        });

        return overlay;
    }

    // ══════════════════════════════════════════
    //  MODAL OVERLAY: Delete Confirm
    // ══════════════════════════════════════════

    private VBox buildDeleteModal() {
        VBox overlay = new VBox();
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        StackPane.setAlignment(overlay, Pos.CENTER);

        VBox card = new VBox(20);
        card.setMaxWidth(440);
        card.setPadding(new Insets(36, 40, 36, 40));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 24, 0.3, 0, 6);"
        );

        Label iconLbl = new Label("🗑️");
        iconLbl.setFont(Font.font("Arial", 40));

        Label titleLbl = new Label("Xác nhận xóa");
        titleLbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLbl.setTextFill(Color.web("#333"));

        deleteConfirmLabel = new Label();
        deleteConfirmLabel.setFont(Font.font("Arial", 14));
        deleteConfirmLabel.setTextFill(Color.web("#555"));
        deleteConfirmLabel.setWrapText(true);
        deleteConfirmLabel.setAlignment(Pos.CENTER);

        Label warnLbl = new Label("⚠️ Hành động này không thể hoàn tác!");
        warnLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        warnLbl.setTextFill(Color.web("#ef5350"));

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER);
        btnRow.setPadding(new Insets(8, 0, 0, 0));

        Button cancelBtn = new Button("Hủy");
        cancelBtn.setPrefWidth(120);
        cancelBtn.setStyle(
            "-fx-font-size: 13; -fx-padding: 10 0; -fx-background-color: #f5f5f5;" +
            "-fx-text-fill: #555; -fx-border-color: #ddd; -fx-border-radius: 6;" +
            "-fx-background-radius: 6; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> hideDeleteModal());

        Button confirmBtn = new Button("🗑️ Xóa");
        confirmBtn.setPrefWidth(120);
        confirmBtn.setStyle(
            "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 10 0;" +
            "-fx-background-color: #ef5350; -fx-text-fill: white;" +
            "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        confirmBtn.setOnAction(e -> doDelete());

        btnRow.getChildren().addAll(cancelBtn, confirmBtn);
        card.getChildren().addAll(iconLbl, titleLbl, deleteConfirmLabel, warnLbl, btnRow);
        overlay.getChildren().add(card);

        overlay.setOnMouseClicked(e -> { if (e.getTarget() == overlay) hideDeleteModal(); });

        return overlay;
    }

    // ══════════════════════════════════════════
    //  OPEN / HIDE MODALS
    // ══════════════════════════════════════════

    private void openAddModal() {
        isEditMode = false;
        modalTitle.setText("➕ Thêm môn học mới");
        idField.clear(); idField.setEditable(true); idField.setOpacity(1);
        nameField.clear(); courseCodeField.clear(); descriptionField.clear();
        modalOverlay.setVisible(true);
        nameField.requestFocus();
    }

    private void openEditModal() {
        if (selectedSubject == null) { showToast("Vui lòng chọn môn học cần chỉnh sửa!"); return; }
        isEditMode = true;
        modalTitle.setText("✏️ Chỉnh sửa môn học");
        idField.setText(selectedSubject.getId());
        idField.setEditable(false); idField.setOpacity(0.6);
        nameField.setText(selectedSubject.getName() != null ? selectedSubject.getName() : "");
        courseCodeField.setText(selectedSubject.getCourseCode() != null ? selectedSubject.getCourseCode() : "");
        descriptionField.setText(selectedSubject.getDescription() != null ? selectedSubject.getDescription() : "");
        modalOverlay.setVisible(true);
        nameField.requestFocus();
    }

    private void openDeleteModal() {
        if (selectedSubject == null) { showToast("Vui lòng chọn môn học cần xóa!"); return; }
        deleteConfirmLabel.setText("Bạn có chắc chắn muốn xóa môn học\n\"" + selectedSubject.getName() + "\" không?");
        deleteOverlay.setVisible(true);
    }

    private void hideFormModal() { modalOverlay.setVisible(false); }
    private void hideDeleteModal() { deleteOverlay.setVisible(false); }

    // ══════════════════════════════════════════
    //  CRUD OPERATIONS
    // ══════════════════════════════════════════

    private void doAdd() {
        if (nameField.getText().isBlank()) { highlightError(nameField); showToast("Tên môn học không được để trống!"); return; }
        if (courseCodeField.getText().isBlank()) { highlightError(courseCodeField); showToast("Mã môn học không được để trống!"); return; }

        SubjectDTO dto = SubjectDTO.builder()
                .id(idField.getText().isBlank() ? null : idField.getText().trim())
                .name(nameField.getText().trim())
                .courseCode(courseCodeField.getText().trim())
                .description(descriptionField.getText().trim())
                .build();

        new Thread(() -> {
            try {
                QuizClientService.getInstance().addSubject(dto);
                Platform.runLater(() -> {
                    hideFormModal();
                    loadSubjects();
                    showToast("✅ Thêm môn học thành công!");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showToast("❌ Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    private void doUpdate() {
        if (nameField.getText().isBlank()) { highlightError(nameField); showToast("Tên môn học không được để trống!"); return; }
        if (courseCodeField.getText().isBlank()) { highlightError(courseCodeField); showToast("Mã môn học không được để trống!"); return; }

        SubjectDTO dto = SubjectDTO.builder()
                .id(selectedSubject.getId())
                .name(nameField.getText().trim())
                .courseCode(courseCodeField.getText().trim())
                .description(descriptionField.getText().trim())
                .build();

        new Thread(() -> {
            try {
                QuizClientService.getInstance().updateSubject(dto);
                Platform.runLater(() -> {
                    hideFormModal();
                    selectedSubject = null;
                    loadSubjects();
                    showToast("✅ Cập nhật môn học thành công!");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showToast("❌ Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    private void doDelete() {
        String id = selectedSubject.getId();
        hideDeleteModal();
        new Thread(() -> {
            try {
                QuizClientService.getInstance().deleteSubject(id);
                Platform.runLater(() -> {
                    selectedSubject = null;
                    loadSubjects();
                    showToast("✅ Xóa môn học thành công!");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showToast("❌ Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    private void loadSubjects() {
        subjectTable.getItems().clear();
        new Thread(() -> {
            try {
                List<SubjectDTO> subjects = QuizClientService.getInstance().getAllSubjects();
                Platform.runLater(() -> subjectTable.getItems().addAll(subjects));
            } catch (Exception e) {
                Platform.runLater(() -> showToast("❌ Không thể tải danh sách: " + e.getMessage()));
            }
        }).start();
    }

    // ══════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════

    /** Toast notification ở bottom center */
    private void showToast(String message) {
        Label toast = new Label(message);
        toast.setStyle(
            "-fx-background-color: rgba(50,50,50,0.88); -fx-text-fill: white;" +
            "-fx-padding: 12 24; -fx-background-radius: 30; -fx-font-size: 13;");
        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 40, 0));
        rootStack.getChildren().add(toast);
        new Thread(() -> {
            try { Thread.sleep(2500); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> rootStack.getChildren().remove(toast));
        }).start();
    }

    private void highlightError(TextField field) {
        field.setStyle("-fx-font-size: 13; -fx-padding: 10; -fx-border-color: #ef5350; -fx-border-radius: 6; -fx-background-radius: 6;");
        field.textProperty().addListener((obs, o, n) ->
            field.setStyle("-fx-font-size: 13; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 6; -fx-background-radius: 6;"));
    }

    private VBox fieldGroup(String labelText, String id) {
        VBox group = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#444"));
        group.getChildren().add(lbl);
        return group;
    }

    private TextField styledInput(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-font-size: 13; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 6; -fx-background-radius: 6;");
        tf.setPrefHeight(40);
        return tf;
    }

    private Button mkBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(130);
        btn.setStyle(
            "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 10 0;" +
            "-fx-background-color: " + color + "; -fx-text-fill: white;" +
            "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        return btn;
    }
}
