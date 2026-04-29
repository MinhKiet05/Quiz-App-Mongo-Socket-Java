package iuh.fit.ui.profile;

import iuh.fit.dto.UserDTO;
import iuh.fit.network.QuizClientService;
import iuh.fit.ui.shared.SessionManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Modality;

/**
 * ProfileForm - Hiển thị thông tin cá nhân và cung cấp chức năng đổi mật khẩu
 */
public class ProfileForm {
    private Stage dialogStage;
    private UserDTO currentUser;

    public static void show(Stage parentStage) {
        ProfileForm form = new ProfileForm();
        form.display(parentStage);
    }

    private void display(Stage parentStage) {
        this.currentUser = SessionManager.getInstance().getCurrentUser();

        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Thông tin cá nhân");
        dialogStage.setWidth(550);
        dialogStage.setHeight(700);
        dialogStage.setResizable(false);

        VBox rootLayout = new VBox(20);
        rootLayout.setPadding(new Insets(30));
        rootLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Header
        HBox headerBox = createHeader();
        rootLayout.getChildren().add(headerBox);

        // Separator
        Separator separator1 = new Separator();
        rootLayout.getChildren().add(separator1);

        // Section 1: Thông tin cá nhân
        VBox profileSection = createProfileSection();
        rootLayout.getChildren().add(profileSection);

        // Separator
        Separator separator2 = new Separator();
        rootLayout.getChildren().add(separator2);

        // Section 2: Đổi mật khẩu
        VBox passwordSection = createPasswordSection();
        rootLayout.getChildren().add(passwordSection);

        // Wrap rootLayout in ScrollPane
        ScrollPane scrollPane = new ScrollPane(rootLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5; -fx-control-inner-background: #f5f5f5;");

        Scene scene = new Scene(scrollPane);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private HBox createHeader() {
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(15));
        headerBox.setStyle("-fx-background-color: #667eea; -fx-border-radius: 5;");

        Label titleLabel = new Label("👤 Thông tin cá nhân");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);

        headerBox.getChildren().add(titleLabel);
        return headerBox;
    }

    private VBox createProfileSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: white;");

        Label sectionTitle = new Label("Thông tin người dùng");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        sectionTitle.setTextFill(Color.web("#333333"));
        section.getChildren().add(sectionTitle);

        // Mã số
        HBox idBox = createReadOnlyField("Mã số:", currentUser.getId());
        section.getChildren().add(idBox);

        // Họ tên
        HBox usernameBox = createReadOnlyField("Họ tên:", currentUser.getUsername());
        section.getChildren().add(usernameBox);

        // Vai trò
        HBox roleBox = createReadOnlyField("Vai trò:", getRoleDisplayName(currentUser.getRole()));
        section.getChildren().add(roleBox);

        // Trạng thái
        HBox statusBox = createReadOnlyField("Trạng thái:", currentUser.getStatus());
        section.getChildren().add(statusBox);

        return section;
    }

    private HBox createReadOnlyField(String label, String value) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);

        Label labelControl = new Label(label);
        labelControl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        labelControl.setTextFill(Color.web("#666666"));
        labelControl.setPrefWidth(100);

        TextField valueField = new TextField(value);
        valueField.setEditable(false);
        valueField.setStyle(
            "-fx-font-size: 12; -fx-padding: 8; -fx-background-color: #f9f9f9; " +
            "-fx-text-fill: #333333; -fx-border-color: #ddd;"
        );
        HBox.setHgrow(valueField, javafx.scene.layout.Priority.ALWAYS);

        box.getChildren().addAll(labelControl, valueField);
        return box;
    }

    private VBox createPasswordSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: white;");

        Label sectionTitle = new Label("Đổi mật khẩu");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        sectionTitle.setTextFill(Color.web("#333333"));
        section.getChildren().add(sectionTitle);

        // Old Password
        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Nhập mật khẩu cũ");
        oldPasswordField.setStyle(
            "-fx-font-size: 12; -fx-padding: 10; -fx-background-color: white; " +
            "-fx-border-color: #667eea; -fx-border-radius: 3; -fx-border-width: 1; " +
            "-fx-text-fill: #333333;"
        );
        oldPasswordField.setPrefHeight(40);

        VBox oldPasswordBox = new VBox(5);
        Label oldPasswordLabel = new Label("Mật khẩu cũ:");
        oldPasswordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        oldPasswordLabel.setTextFill(Color.web("#333333"));
        oldPasswordBox.getChildren().addAll(oldPasswordLabel, oldPasswordField);

        // New Password
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nhập mật khẩu mới");
        newPasswordField.setStyle(
            "-fx-font-size: 12; -fx-padding: 10; -fx-background-color: white; " +
            "-fx-border-color: #667eea; -fx-border-radius: 3; -fx-border-width: 1; " +
            "-fx-text-fill: #333333;"
        );
        newPasswordField.setPrefHeight(40);

        VBox newPasswordBox = new VBox(5);
        Label newPasswordLabel = new Label("Mật khẩu mới:");
        newPasswordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        newPasswordLabel.setTextFill(Color.web("#333333"));
        newPasswordBox.getChildren().addAll(newPasswordLabel, newPasswordField);

        // Confirm Password
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Xác nhận mật khẩu mới");
        confirmPasswordField.setStyle(
            "-fx-font-size: 12; -fx-padding: 10; -fx-background-color: white; " +
            "-fx-border-color: #667eea; -fx-border-radius: 3; -fx-border-width: 1; " +
            "-fx-text-fill: #333333;"
        );
        confirmPasswordField.setPrefHeight(40);

        VBox confirmPasswordBox = new VBox(5);
        Label confirmPasswordLabel = new Label("Xác nhận mật khẩu:");
        confirmPasswordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        confirmPasswordLabel.setTextFill(Color.web("#333333"));
        confirmPasswordBox.getChildren().addAll(confirmPasswordLabel, confirmPasswordField);

        section.getChildren().addAll(oldPasswordBox, newPasswordBox, confirmPasswordBox);

        // Button box
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelButton = new Button("Hủy");
        cancelButton.setStyle(
            "-fx-font-size: 12; -fx-padding: 10 30; -fx-background-color: #cccccc; " +
            "-fx-text-fill: white; -fx-border-radius: 5; -fx-font-weight: bold;"
        );
        cancelButton.setOnAction(e -> dialogStage.close());

        Button submitButton = new Button("Đổi mật khẩu");
        submitButton.setStyle(
            "-fx-font-size: 12; -fx-padding: 10 30; -fx-background-color: #667eea; " +
            "-fx-text-fill: white; -fx-border-radius: 5; -fx-font-weight: bold;"
        );
        submitButton.setOnAction(e -> handleChangePassword(
            oldPasswordField.getText(),
            newPasswordField.getText(),
            confirmPasswordField.getText(),
            oldPasswordField,
            newPasswordField,
            confirmPasswordField
        ));

        buttonBox.getChildren().addAll(cancelButton, submitButton);
        section.getChildren().add(buttonBox);

        return section;
    }

    private void handleChangePassword(String oldPassword, String newPassword, String confirmPassword,
                                      PasswordField oldField, PasswordField newField, PasswordField confirmField) {
        // Client-side validation
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Lỗi", "Vui lòng điền đầy đủ tất cả các trường!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Lỗi", "Mật khẩu mới và xác nhận không trùng khớp!");
            confirmField.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            showError("Lỗi", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            newField.requestFocus();
            return;
        }

        // Send request to server in a separate thread
        new Thread(() -> {
            try {
                QuizClientService client = QuizClientService.getInstance();
                boolean success = client.changePassword(currentUser.getId(), oldPassword, newPassword);

                Platform.runLater(() -> {
                    if (success) {
                        showInfo("Thành công", "Chúc mừng! Bạn đã đổi mật khẩu thành công.");
                        // Clear password fields
                        oldField.clear();
                        newField.clear();
                        confirmField.clear();
                    } else {
                        showError("Thất bại", "Mật khẩu cũ không chính xác!");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Lỗi", "Lỗi: " + e.getMessage());
                });
            }
        }).start();
    }

    private void showInfo(String title, String message) {
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

    private String getRoleDisplayName(String role) {
        return switch (role) {
            case "CANDIDATE" -> "Thí sinh";
            case "LECTURER" -> "Giảng viên";
            case "MANAGER" -> "Quản lý";
            default -> role;
        };
    }
}
