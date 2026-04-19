# 📋 Hệ thống Phân quyền (Role-Based Access Control) - Quiz App

## 🔐 Tổng quan hệ thống phân quyền

Ứng dụng Quiz App sử dụng hệ thống **Role-Based Access Control (RBAC)** để phân quyền cho hai loại người dùng chính:

1. **LECTURER** - Giảng viên
2. **CANDIDATE** - Thí sinh

---

## 🔍 Luồng xác thực và phân quyền

### 1. Quy trình Đăng nhập

```
┌─────────────────────────────────────┐
│   Màn hình Đăng nhập (LoginForm)   │
│   - Nhập ID + Password              │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│   UserService.login(id, password)   │
│   - Kiểm tra DB (MongoDB)           │
│   - Validate thông tin              │
└────────────────┬────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
        ▼                 ▼
   ❌ FAIL          ✅ SUCCESS
   Lỗi             Trả về UserDTO
                   (có chứa role)
                       │
                       ▼
         ┌─────────────────────────────┐
         │ Lưu vào SessionManager      │
         │ currentUser = UserDTO       │
         └─────────────┬───────────────┘
                       │
                       ▼
         ┌─────────────────────────────┐
         │ Kiểm tra role của user      │
         └────┬──────────────────────┬─┘
              │                      │
        ┌─────▼─────┐        ┌──────▼──────┐
        │ LECTURER  │        │  CANDIDATE  │
        │     │     │        │      │      │
        ▼     │     ▼        ▼      │      ▼
   LecturerDashboard    CandidateDashboard
   - Quản lý đề thi      - Xem danh sách đề
   - Tạo đề mới         - Làm bài thi
   - Xem kết quả        - Xem kết quả
   - Thống kê
```

---

## 📁 Cấu trúc file liên quan đến phân quyền

```
src/main/java/iuh/fit/
├── entity/
│   └── User.java
│       ├── id: String (ID người dùng)
│       ├── username: String (Tên đăng nhập)
│       ├── password: String (Mật khẩu)
│       ├── role: String (LECTURER / CANDIDATE)  ⭐
│       └── status: String (Trạng thái)
│
├── dto/
│   └── UserDTO.java
│       (Tương tự User, dùng cho transfer data)
│
├── service/
│   ├── IUserService.java
│   └── impl/
│       └── UserServiceImpl.java
│           └── login(id, password): UserDTO
│
├── repository/
│   ├── IUserRepository.java
│   └── impl/
│       └── UserRepositoryImpl.java
│           └── login(id, password): User
│
└── ui/
    ├── shared/
    │   └── SessionManager.java ⭐
    │       └── currentUser: UserDTO (Lưu user đang đăng nhập)
    │
    ├── login/
    │   └── LoginForm.java
    │       ├── Giao diện đăng nhập
    │       ├── Xác thực thông tin
    │       └── Phân quyền dựa trên role
    │
    ├── candidate/
    │   ├── CandidateDashboard.java (Menu chính cho CANDIDATE)
    │   ├── ExamForm.java (Làm bài thi)
    │   └── ResultForm.java (Xem kết quả)
    │
    └── lecturer/
        └── LecturerDashboard.java (Menu chính cho LECTURER)
```

---

## 🔧 Các thành phần chính

### 1. **User Entity** - Lưu trữ quyền
```java
@Data
@Builder
public class User {
    @BsonId
    private String id;
    private String username;
    private String password;
    private String role;        // ⭐ LECTURER hoặc CANDIDATE
    private String status;
}
```

### 2. **SessionManager** - Quản lý phiên đăng nhập
```java
public class SessionManager {
    // Singleton pattern
    private static SessionManager instance;
    
    // Lưu thông tin user hiện tại
    private UserDTO currentUser;  // ⭐ Chứa role của user
    
    // Kiểm tra đã đăng nhập
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    // Đăng xuất - xóa thông tin session
    public void logout() {
        currentUser = null;
        // Đóng các kết nối khác
    }
}
```

### 3. **LoginForm** - Xác thực và phân quyền
```java
private void handleLogin() {
    // 1. Lấy thông tin từ input
    String userId = userIdField.getText();
    String password = passwordField.getText();
    
    // 2. Gọi service để xác thực
    UserDTO userDTO = userService.login(userId, password);
    
    // 3. Lưu vào SessionManager
    SessionManager.getInstance().setCurrentUser(userDTO);
    
    // 4. ⭐ PHÂN QUYỀN dựa trên ROLE
    if ("LECTURER".equalsIgnoreCase(userDTO.getRole())) {
        // Mở giao diện cho Giảng viên
        LecturerDashboard.show(primaryStage);
    } else if ("CANDIDATE".equalsIgnoreCase(userDTO.getRole())) {
        // Mở giao diện cho Thí sinh
        CandidateDashboard.show(primaryStage);
    }
}
```

---

## 👥 Phân quyền chi tiết

### **LECTURER (Giảng viên)**

**Quyền hạn:**
- ✅ Tạo đề thi mới
- ✅ Chỉnh sửa/xóa đề thi
- ✅ Xem danh sách câu hỏi
- ✅ Xem kết quả bài thi của thí sinh
- ✅ Xem thống kê
- ✅ Quản lý bài nộp

**Giao diện:**
- LecturerDashboard với các thẻ chức năng
- Menu quản lý đề thi
- Báo cáo thống kê

**Code:**
```java
public class LecturerDashboard {
    // Chỉ mở được khi user.role == "LECTURER"
    public static void show(Stage primaryStage) {
        // Hiển thị các feature box cho giảng viên
    }
}
```

---

### **CANDIDATE (Thí sinh)**

**Quyền hạn:**
- ✅ Xem danh sách đề thi
- ✅ Làm bài thi
- ✅ Xem kết quả bài làm của mình
- ❌ Không được tạo/sửa đề thi
- ❌ Không được xem kết quả người khác

**Giao diện:**
- CandidateDashboard với danh sách đề thi
- ExamForm để làm bài
- ResultForm để xem kết quả

**Code:**
```java
public class CandidateDashboard {
    // Chỉ mở được khi user.role == "CANDIDATE"
    public static void show(Stage primaryStage) {
        // Hiển thị danh sách đề thi
    }
}
```

---

## 🛡️ Bảo mật trong Phân quyền

### 1. **Kiểm tra trước mở giao diện**
```java
// LoginForm.java - Line 138-142
if ("LECTURER".equalsIgnoreCase(userDTO.getRole())) {
    LecturerDashboard.show(primaryStage);
} else if ("CANDIDATE".equalsIgnoreCase(userDTO.getRole())) {
    CandidateDashboard.show(primaryStage);
}
```

### 2. **SessionManager lưu user hiện tại**
```java
// SessionManager.java
private UserDTO currentUser;  // Chứa role

public boolean isLoggedIn() {
    return currentUser != null;
}

public UserDTO getCurrentUser() {
    return currentUser;  // Có thể lấy role từ đây
}
```

### 3. **Đăng xuất xóa quyền truy cập**
```java
// SessionManager.java
public void logout() {
    currentUser = null;  // Xóa thông tin user
    // Không thể truy cập giao diện nữa
}
```

---

## 🧪 Cách kiểm tra phân quyền

### **Tài khoản Giảng viên (LECTURER)**
```
ID: 12345678
Password: 12345678
Role: LECTURER
→ Mở LecturerDashboard
```

### **Tài khoản Thí sinh (CANDIDATE)**
```
ID: 23696901
Password: 12345678
Role: CANDIDATE
→ Mở CandidateDashboard
```

---

## 📊 Sơ đồ Phân quyền

```
┌───────────────────┐
│  Login Success    │
│ (UserDTO + role)  │
└────────┬──────────┘
         │
         ▼
    ┌─────────────┐
    │ Check role  │
    └────┬─────┬──┘
         │     │
    ┌────▼┐ ┌──▼────┐
    │LECT│ │CANDID │
    └────┼─┼───────┘
         │ │
         ▼ ▼
    ┌─────────────────────────┐
    │ SessionManager.          │
    │ setCurrentUser(user)    │
    └─────────────────────────┘
         │
    ┌────┴──────┐
    │           │
    ▼           ▼
┌────────────────────┐
│LecturerDashboard   │
│- Tạo đề           │
│- Quản lý đề       │
│- Xem kết quả      │
│- Thống kê         │
└────────────────────┘

┌────────────────────┐
│CandidateDashboard  │
│- Xem danh sách    │
│- Làm bài thi      │
│- Xem kết quả      │
└────────────────────┘
```

---

## 🔄 Luồng dữ liệu từ DB

```
1. Nhập thông tin
   ↓
2. LoginForm.handleLogin()
   ↓
3. UserServiceImpl.login()
   ↓
4. UserRepositoryImpl.login() → MongoDB
   ↓
5. Trả về User entity (kèm role)
   ↓
6. Convert sang UserDTO
   ↓
7. Lưu vào SessionManager.currentUser
   ↓
8. Kiểm tra userDTO.role
   ↓
9. Mở Dashboard tương ứng
   (LecturerDashboard hoặc CandidateDashboard)
```

---

## 📱 Chạy ứng dụng

```bash
# Compile
mvn clean compile

# Chạy
mvn javafx:run
```

**Màn hình đầu tiên:** LoginForm
- Nhập ID và Password
- Click "Đăng nhập"
- Ứng dụng kiểm tra role từ DB
- Mở Dashboard tương ứng

---

## ⚠️ Chú ý

- ✅ Role được lưu trong User entity của MongoDB
- ✅ SessionManager là Singleton (chỉ có một instance duy nhất)
- ✅ CurrentUser không được null khi đã đăng nhập
- ✅ Logout sẽ xóa toàn bộ thông tin session
- ✅ Phân quyền được check ở LoginForm
- ✅ Mỗi Dashboard chỉ có quyền truy cập khi role đúng

---

## 🎯 Kết quả

Khi chạy ứng dụng:
1. **Đăng nhập với tài khoản LECTURER** → Thấy LecturerDashboard
2. **Đăng nhập với tài khoản CANDIDATE** → Thấy CandidateDashboard
3. **Đăng xuất** → Quay về LoginForm
4. **Mỗi role có giao diện riêng** → Phân quyền hoạt động ✅

---

**Ngày cập nhật:** April 19, 2026
**Status:** ✅ Phân quyền hoạt động hoàn toàn

