package iuh.fit.ui.manager;

import iuh.fit.dto.UserDTO;
import iuh.fit.network.QuizClientService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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

public class UserManagerForm {
    private Stage stage;
    private StackPane rootStack;
    private TableView<UserDTO> userTable;
    private TextField searchField;
    private ComboBox<String> roleFilter;
    private ComboBox<String> statusFilter;

    private VBox formOverlay;
    private VBox deleteOverlay;
    private VBox resetPasswordOverlay;

    private TextField idField;
    private TextField nameField;
    private PasswordField passwordField;
    private ComboBox<String> roleField;
    private ComboBox<String> statusField;
    private Label formTitle;
    private Label deleteConfirmLabel;
    private Label resetPassLabel;
    private PasswordField newPasswordField;

    private UserDTO selectedUser;
    private boolean isEditMode = false;
    private String initialRoleFilter = "ALL";
    private boolean roleLocked = false;

    public static void show(Stage parentStage) {
        UserManagerForm form = new UserManagerForm();
        form.display(parentStage);
    }

    public static void show(Stage parentStage, String defaultRole) {
        UserManagerForm form = new UserManagerForm();
        form.initialRoleFilter = defaultRole == null ? "ALL" : defaultRole.toUpperCase();
        form.roleLocked = "CANDIDATE".equals(form.initialRoleFilter) || "LECTURER".equals(form.initialRoleFilter);
        form.display(parentStage);
    }

    private void display(Stage parentStage) {
        this.stage = new Stage();
        this.stage.initOwner(parentStage);
        this.stage.setTitle("Quản lý người dùng");

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        rootStack = new StackPane();

        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: #f5f5f5;");
        main.setTop(createHeader());
        main.setCenter(createCenter());
        main.setBottom(createButtonBar());

        formOverlay = buildFormModal();
        formOverlay.setVisible(false);
        deleteOverlay = buildDeleteModal();
        deleteOverlay.setVisible(false);
        resetPasswordOverlay = buildResetPasswordModal();
        resetPasswordOverlay.setVisible(false);

        rootStack.getChildren().addAll(main, formOverlay, deleteOverlay, resetPasswordOverlay);

        Scene scene = new Scene(rootStack, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.show();
        if ("ALL".equals(initialRoleFilter)) {
            loadUsers();
        } else {
            searchUsers();
        }
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setStyle("-fx-background-color: #667eea;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("\uD83D\uDC65");
        icon.setFont(Font.font("Arial", 26));

        Label title = new Label(getHeaderTitle());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("\u2190 Quay lại Dashboard");
        backBtn.setStyle(
                "-fx-background-color: white; -fx-text-fill: #667eea;" +
                        "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 8 18;" +
                        "-fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;");
        backBtn.setOnAction(e -> stage.close());

        header.getChildren().addAll(icon, title, spacer, backBtn);
        return header;
    }

    private VBox createCenter() {
        VBox center = new VBox(12);
        center.setPadding(new Insets(24, 30, 10, 30));

        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Tìm theo mã hoặc họ tên...");
        searchField.setPrefWidth(360);
        searchField.setStyle("-fx-font-size: 13; -fx-padding: 9;");
        searchField.setOnAction(e -> searchUsers());

        roleFilter = new ComboBox<>(FXCollections.observableArrayList("ALL", "MANAGER", "LECTURER", "CANDIDATE"));
        roleFilter.setValue(initialRoleFilter);
        roleFilter.setPrefWidth(160);
        roleFilter.setDisable(roleLocked);

        statusFilter = new ComboBox<>(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        statusFilter.setValue("ALL");
        statusFilter.setPrefWidth(140);

        Button searchBtn = mkBtn("Tìm kiếm", "#42a5f5", 120);
        searchBtn.setOnAction(e -> searchUsers());

        Button clearBtn = mkBtn("Xóa lọc", "#78909c", 110);
        clearBtn.setOnAction(e -> {
            searchField.clear();
            roleFilter.setValue(roleLocked ? initialRoleFilter : "ALL");
            statusFilter.setValue("ALL");
            if (roleLocked) {
                searchUsers();
            } else {
                loadUsers();
            }
        });

        searchBar.getChildren().addAll(new Label("Từ khóa:"), searchField);
        if (!roleLocked) {
            searchBar.getChildren().addAll(new Label("Vai trò:"), roleFilter);
        }
        searchBar.getChildren().addAll(new Label("Trạng thái:"), statusFilter, searchBtn, clearBtn);

        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userTable.setPlaceholder(new Label("Chưa có dữ liệu người dùng"));

        TableColumn<UserDTO, String> idCol = new TableColumn<>("Mã");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(160);

        TableColumn<UserDTO, String> nameCol = new TableColumn<>("Họ tên");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<UserDTO, String> roleCol = new TableColumn<>("Vai trò");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setMaxWidth(160);

        TableColumn<UserDTO, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setMaxWidth(160);

        userTable.getColumns().addAll(idCol, nameCol, roleCol, statusCol);
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> selectedUser = newVal);

        center.getChildren().addAll(searchBar, userTable);
        VBox.setVgrow(userTable, Priority.ALWAYS);
        return center;
    }

    private HBox createButtonBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(16, 30, 20, 30));
        bar.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");

        Button refreshBtn = mkBtn("\uD83D\uDD04 Tải lại", "#78909c", 130);
        refreshBtn.setOnAction(e -> loadUsers());

        Button resetPassBtn = mkBtn("\uD83D\uDD11 Reset mật khẩu", "#8d6e63", 160);
        resetPassBtn.setOnAction(e -> openResetPasswordModal());

        Button deleteBtn = mkBtn("\uD83D\uDDD1 Xóa", "#ef5350", 120);
        deleteBtn.setOnAction(e -> openDeleteModal());

        Button editBtn = mkBtn("\u270F Chỉnh sửa", "#42a5f5", 130);
        editBtn.setOnAction(e -> openEditModal());

        Button addBtn = mkBtn("\u2795 " + getAddButtonText(), "#667eea", 170);
        addBtn.setOnAction(e -> openAddModal());

        bar.getChildren().addAll(refreshBtn, resetPassBtn, deleteBtn, editBtn, addBtn);
        return bar;
    }

    private VBox buildFormModal() {
        VBox overlay = new VBox();
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");

        VBox card = new VBox(14);
        card.setMaxWidth(560);
        card.setPadding(new Insets(30, 36, 30, 36));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 24, 0.3, 0, 6);"
        );

        formTitle = new Label("Thêm " + getEntityLabelLower());
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        idField = styledInput("Mã đăng nhập (VD: 23690001)");
        nameField = styledInput("Họ và tên");
        passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu (>= 6 ký tự)");
        passwordField.setStyle("-fx-font-size: 13; -fx-padding: 9;");

        roleField = new ComboBox<>(FXCollections.observableArrayList("MANAGER", "LECTURER", "CANDIDATE"));
        roleField.setValue(roleLocked ? initialRoleFilter : "CANDIDATE");
        roleField.setPrefHeight(38);
        roleField.setDisable(roleLocked);

        statusField = new ComboBox<>(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        statusField.setValue("ACTIVE");
        statusField.setPrefHeight(38);

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Hủy");
        cancelBtn.setOnAction(e -> formOverlay.setVisible(false));
        cancelBtn.setStyle("-fx-background-color: #f1f1f1; -fx-padding: 9 16; -fx-font-weight: bold;");

        Button saveBtn = new Button("\uD83D\uDCBE Lưu");
        saveBtn.setOnAction(e -> {
            if (isEditMode) {
                doUpdate();
            } else {
                doAdd();
            }
        });
        saveBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-padding: 9 18; -fx-font-weight: bold;");

        btnRow.getChildren().addAll(cancelBtn, saveBtn);

        card.getChildren().addAll(
                formTitle,
                fieldGroup("Mã đăng nhập *", idField),
                fieldGroup("Họ và tên *", nameField),
                fieldGroup("Mật khẩu *", passwordField)
        );
        if (!roleLocked) {
            card.getChildren().add(fieldGroup("Vai trò *", roleField));
        }
        card.getChildren().addAll(fieldGroup("Trạng thái *", statusField), btnRow);
        overlay.getChildren().add(card);
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) {
                overlay.setVisible(false);
            }
        });
        return overlay;
    }

    private VBox buildDeleteModal() {
        VBox overlay = new VBox();
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");

        VBox card = new VBox(12);
        card.setMaxWidth(450);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(26, 30, 26, 30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Label title = new Label("Xác nhận xóa người dùng");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        deleteConfirmLabel = new Label();
        deleteConfirmLabel.setWrapText(true);
        deleteConfirmLabel.setFont(Font.font("Arial", 13));

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER);
        Button cancelBtn = new Button("Hủy");
        cancelBtn.setOnAction(e -> deleteOverlay.setVisible(false));
        Button deleteBtn = new Button("\uD83D\uDDD1 Xóa");
        deleteBtn.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> doDelete());
        btns.getChildren().addAll(cancelBtn, deleteBtn);

        card.getChildren().addAll(title, deleteConfirmLabel, btns);
        overlay.getChildren().add(card);
        return overlay;
    }

    private VBox buildResetPasswordModal() {
        VBox overlay = new VBox();
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");

        VBox card = new VBox(12);
        card.setMaxWidth(450);
        card.setPadding(new Insets(26, 30, 26, 30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Label title = new Label("Reset mật khẩu");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        resetPassLabel = new Label();
        resetPassLabel.setWrapText(true);
        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nhập mật khẩu mới");
        newPasswordField.setStyle("-fx-font-size: 13; -fx-padding: 9;");

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Hủy");
        cancel.setOnAction(e -> resetPasswordOverlay.setVisible(false));
        Button save = new Button("\uD83D\uDD11 Cập nhật");
        save.setStyle("-fx-background-color: #8d6e63; -fx-text-fill: white; -fx-font-weight: bold;");
        save.setOnAction(e -> doResetPassword());
        btns.getChildren().addAll(cancel, save);

        card.getChildren().addAll(title, resetPassLabel, newPasswordField, btns);
        overlay.getChildren().add(card);
        return overlay;
    }

    private void openAddModal() {
        isEditMode = false;
        selectedUser = null;
        formTitle.setText("\u2795 Thêm " + getEntityLabelLower() + " mới");
        idField.clear();
        idField.setEditable(true);
        nameField.clear();
        passwordField.clear();
        passwordField.setDisable(false);
        roleField.setValue(roleLocked ? initialRoleFilter : "CANDIDATE");
        roleField.setDisable(roleLocked);
        statusField.setValue("ACTIVE");
        formOverlay.setVisible(true);
    }

    private void openEditModal() {
        if (selectedUser == null) {
            showToast("Vui lòng chọn người dùng để chỉnh sửa");
            return;
        }
        isEditMode = true;
        formTitle.setText("\u270F Cập nhật " + getEntityLabelLower());
        idField.setText(selectedUser.getId());
        idField.setEditable(false);
        nameField.setText(selectedUser.getUsername());
        passwordField.clear();
        passwordField.setDisable(true);
        roleField.setValue(roleLocked ? initialRoleFilter : selectedUser.getRole());
        roleField.setDisable(roleLocked);
        statusField.setValue(selectedUser.getStatus());
        formOverlay.setVisible(true);
    }

    private void openDeleteModal() {
        if (selectedUser == null) {
            showToast("Vui lòng chọn người dùng để xóa");
            return;
        }
        deleteConfirmLabel.setText("Bạn có chắc chắn muốn xóa người dùng \"" + selectedUser.getUsername() + "\" (" + selectedUser.getId() + ")?");
        deleteOverlay.setVisible(true);
    }

    private void openResetPasswordModal() {
        if (selectedUser == null) {
            showToast("Vui lòng chọn người dùng để reset mật khẩu");
            return;
        }
        resetPassLabel.setText("Nhập mật khẩu mới cho: " + selectedUser.getUsername() + " (" + selectedUser.getId() + ")");
        newPasswordField.clear();
        resetPasswordOverlay.setVisible(true);
    }

    private void doAdd() {
        if (!validateFormForAdd()) {
            return;
        }
        UserDTO dto = UserDTO.builder()
                .id(idField.getText().trim())
                .username(nameField.getText().trim())
                .password(passwordField.getText().trim())
                .role(roleLocked ? initialRoleFilter : roleField.getValue())
                .status(statusField.getValue())
                .build();
        new Thread(() -> {
            try {
                QuizClientService.getInstance().addUser(dto);
                Platform.runLater(() -> {
                    formOverlay.setVisible(false);
                    loadUsers();
                    showToast("Thêm người dùng thành công");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showToast("Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    private void doUpdate() {
        if (idField.getText().isBlank() || nameField.getText().isBlank()) {
            showToast("Mã và họ tên không được để trống");
            return;
        }
        UserDTO dto = UserDTO.builder()
                .id(idField.getText().trim())
                .username(nameField.getText().trim())
                .role(roleLocked ? initialRoleFilter : roleField.getValue())
                .status(statusField.getValue())
                .build();
        new Thread(() -> {
            try {
                QuizClientService.getInstance().updateUser(dto);
                Platform.runLater(() -> {
                    formOverlay.setVisible(false);
                    loadUsers();
                    showToast("Cập nhật người dùng thành công");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showToast("Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    private void doDelete() {
        if (selectedUser == null) {
            return;
        }
        String id = selectedUser.getId();
        new Thread(() -> {
            try {
                QuizClientService.getInstance().deleteUser(id);
                Platform.runLater(() -> {
                    deleteOverlay.setVisible(false);
                    selectedUser = null;
                    loadUsers();
                    showToast("Xóa người dùng thành công");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showToast("Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    private void doResetPassword() {
        if (selectedUser == null) {
            return;
        }
        String newPass = newPasswordField.getText() == null ? "" : newPasswordField.getText().trim();
        if (newPass.length() < 6) {
            showToast("Mật khẩu mới phải có ít nhất 6 ký tự");
            return;
        }
        new Thread(() -> {
            try {
                QuizClientService.getInstance().resetUserPassword(selectedUser.getId(), newPass);
                Platform.runLater(() -> {
                    resetPasswordOverlay.setVisible(false);
                    showToast("Reset mật khẩu thành công");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showToast("Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    private void searchUsers() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();
        String role = roleLocked ? initialRoleFilter : roleFilter.getValue();
        String status = statusFilter.getValue();
        new Thread(() -> {
            try {
                List<UserDTO> users = QuizClientService.getInstance().searchUsers(keyword, role, status);
                Platform.runLater(() -> {
                    userTable.getItems().setAll(users);
                    selectedUser = null;
                });
            } catch (Exception e) {
                Platform.runLater(() -> showToast("Lỗi tìm kiếm: " + e.getMessage()));
            }
        }).start();
    }

    private void loadUsers() {
        userTable.getItems().clear();
        new Thread(() -> {
            try {
                List<UserDTO> users = QuizClientService.getInstance().getAllUsers();
                Platform.runLater(() -> {
                    userTable.getItems().setAll(users);
                    selectedUser = null;
                });
            } catch (Exception e) {
                Platform.runLater(() -> showToast("Không thể tải danh sách người dùng: " + e.getMessage()));
            }
        }).start();
    }

    private boolean validateFormForAdd() {
        if (idField.getText() == null || idField.getText().isBlank()) {
            showToast("Mã người dùng không được để trống");
            return false;
        }
        if (nameField.getText() == null || nameField.getText().isBlank()) {
            showToast("Họ tên không được để trống");
            return false;
        }
        if (passwordField.getText() == null || passwordField.getText().trim().length() < 6) {
            showToast("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Label toast = new Label(message);
        toast.setStyle(
                "-fx-background-color: rgba(50,50,50,0.88); -fx-text-fill: white;" +
                        "-fx-padding: 12 24; -fx-background-radius: 30; -fx-font-size: 13;");
        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 40, 0));
        rootStack.getChildren().add(toast);
        new Thread(() -> {
            try {
                Thread.sleep(2200);
            } catch (InterruptedException ignored) {
            }
            Platform.runLater(() -> rootStack.getChildren().remove(toast));
        }).start();
    }

    private TextField styledInput(String promptText) {
        TextField input = new TextField();
        input.setPromptText(promptText);
        input.setStyle("-fx-font-size: 13; -fx-padding: 9;");
        return input;
    }

    private VBox fieldGroup(String labelText, javafx.scene.Node node) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        box.getChildren().addAll(label, node);
        return box;
    }

    private Button mkBtn(String text, String bgColor, int width) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setStyle(
                "-fx-font-size: 13; -fx-font-weight: bold; -fx-padding: 10 0;" +
                        "-fx-background-color: " + bgColor + "; -fx-text-fill: white;" +
                        "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        return button;
    }

    private String getHeaderTitle() {
        if ("CANDIDATE".equals(initialRoleFilter)) {
            return "QUẢN LÝ THÍ SINH";
        }
        if ("LECTURER".equals(initialRoleFilter)) {
            return "QUẢN LÝ GIẢNG VIÊN";
        }
        return "QUẢN LÝ NGƯỜI DÙNG";
    }

    private String getEntityLabelLower() {
        if ("CANDIDATE".equals(initialRoleFilter)) {
            return "thí sinh";
        }
        if ("LECTURER".equals(initialRoleFilter)) {
            return "giảng viên";
        }
        return "người dùng";
    }

    private String getAddButtonText() {
        if ("CANDIDATE".equals(initialRoleFilter)) {
            return "Thêm thí sinh";
        }
        if ("LECTURER".equals(initialRoleFilter)) {
            return "Thêm giảng viên";
        }
        return "Thêm người dùng";
    }
}
