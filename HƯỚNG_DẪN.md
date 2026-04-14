# Quiz Application - Hướng dẫn sử dụng

## 📋 Tổng quan

Ứng dụng Quiz là một hệ thống trắc nghiệm online được phát triển bằng Java JavaFX, kết nối với MongoDB và sử dụng Socket TCP/IP để giao tiếp client-server.

## 🎯 Tính năng

### Cho Thí sinh (CANDIDATE):
- ✅ Đăng nhập với mã số sinh viên
- ✅ Xem danh sách đề thi có sẵn
- ✅ Làm bài thi với giới hạn thời gian
- ✅ Xem kết quả và điểm số ngay sau khi nộp bài
- ✅ Timer đếm ngược để quản lý thời gian

### Cho Giảng viên (LECTURER):
- ✅ Đăng nhập với mã số giảng viên
- ✅ Xem dashboard với các chức năng quản lý
- ✅ Tạo đề thi (tính năng sắp tới)
- ✅ Quản lý câu hỏi (tính năng sắp tới)

## 🚀 Yêu cầu hệ thống

- **Java**: JDK 21 trở lên
- **MongoDB**: Phiên bản 5.0 trở lên (chạy trên localhost:27017)
- **Maven**: 3.8+
- **OS**: Windows, macOS, hoặc Linux

## 📦 Cài đặt

### 1. Clone hoặc tải project
```bash
cd D:\project_phantanjava\Quiz-App-Mongo-Socket-Java
```

### 2. Cài đặt dependencies
```bash
mvn clean install
```

### 3. Khởi động MongoDB
Đảm bảo MongoDB đang chạy trên `localhost:27017`:
```bash
mongod
```

### 4. Import dữ liệu (nếu cần)
```bash
mongoimport --db QuizAppDB --collection users --file data/Users.json --jsonArray
mongoimport --db QuizAppDB --collection quizzes --file data/Quizzes.json --jsonArray
mongoimport --db QuizAppDB --collection questions --file data/Questions.json --jsonArray
mongoimport --db QuizAppDB --collection submissions --file data/Submissions.json --jsonArray
```

## ▶️ Chạy ứng dụng

### Từ IDE (IntelliJ IDEA hoặc Eclipse):
1. Mở project trong IDE
2. Chạy `Main.java` từ package `iuh.fit.app`
3. Chọn `Run` hoặc nhấn `Shift + F10` (IntelliJ)

### Từ Terminal:
```bash
mvn clean javafx:run
```

Hoặc compile và chạy:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="iuh.fit.app.Main"
```

## 👥 Tài khoản test

Dùng các tài khoản này để test ứng dụng:

### Thí sinh (CANDIDATE):
- **ID**: 23696901, **Mật khẩu**: 12345678 → Trần Huỳnh Minh Kiệt
- **ID**: 23679011, **Mật khẩu**: 12345678 → Nguyễn Huy Hoàng
- **ID**: 23697291, **Mật khẩu**: 12345678 → Đinh Tấn Khiêm

### Giảng viên (LECTURER):
- **ID**: 12345678, **Mật khẩu**: 12345678 → Trần Huỳnh Minh Kiệt

## 🎮 Hướng dẫn sử dụng

### 1. Đăng nhập
- Nhập Mã số / ID
- Nhập Mật khẩu
- Nhấn "Đăng nhập" hoặc Enter
- Hệ thống sẽ xác thực và chuyển hướng dựa trên vai trò

### 2. Dashboard Thí sinh
- Hiển thị danh sách tất cả đề thi có sẵn
- Mỗi đề thi hiển thị:
  - Tên đề thi
  - Thời gian làm bài (phút)
  - Trạng thái (PUBLISHED, DRAFT)
  - Số lượng câu hỏi
- Nhấn "Bắt đầu làm bài" để bắt đầu

### 3. Làm bài thi
- **Các thành phần**:
  - Header: Tên đề thi + Timer đếm ngược
  - Nội dung: Hiển thị câu hỏi hiện tại
  - Footer: Nút điều hướng + Nộp bài
  
- **Cách làm bài**:
  - Chọn một đáp án cho câu hỏi hiện tại
  - Nhấn "Câu tiếp theo" hoặc "Câu trước" để di chuyển
  - Thay đổi câu hỏi tùy ý, đáp án được lưu tự động
  - Nhấn "Nộp bài" khi hoàn thành

- **Lưu ý**:
  - Timer sẽ đếm ngược từ giới hạn thời gian
  - Khi hết thời gian, bài thi sẽ tự động nộp
  - Không thể sửa sau khi nộp bài

### 4. Xem kết quả
- Hiển thị điểm số (0-10)
- Trạng thái (✓ ĐỖ / ✗ KHÔNG ĐỖ)
- Thống kê chi tiết:
  - Số câu đúng / tổng câu
  - Số câu sai / tổng câu
  - Tỷ lệ trả lời đúng
- Có thể quay lại dashboard hoặc thoát

### 5. Dashboard Giảng viên
- Xem các chức năng quản lý
- Hiện tại có 4 tính năng (một số sắp tới)
- Đăng xuất bất kỳ lúc nào

## 📂 Cấu trúc thư mục

```
src/main/java/iuh/fit/
├── app/
│   ├── Main.java (Điểm khởi động)
│   ├── db/
│   │   └── MongoDbConnection.java (Kết nối DB)
│   ├── dto/ (Data Transfer Objects)
│   ├── entity/ (Models)
│   ├── mapper/ (Mapping)
│   ├── network/ (Socket communication)
│   ├── repository/ (Database layer)
│   ├── service/ (Business logic)
│   └── ui/ (JavaFX UI)
│       ├── login/
│       │   └── LoginForm.java
│       ├── candidate/
│       │   ├── CandidateDashboard.java
│       │   ├── ExamForm.java
│       │   └── ResultForm.java
│       ├── lecturer/
│       │   └── LecturerDashboard.java
│       └── shared/
│           └── SessionManager.java
```

## 🔧 Cấu hình

### MongoDB Connection
Mở file `MongoDbConnection.java` để thay đổi:
- URI: `mongodb://localhost:27017`
- Database: `QuizAppDB`

### JavaFX Configuration
- Phiên bản JavaFX: 21.0.1
- Platform: Tự động hỗ trợ Windows, macOS, Linux

## 🎨 Giao diện

- **Màu chủ đạo**: Tím (#667eea) và Xanh (#764ba2)
- **Font**: Arial 12-14px
- **Theme**: Modern Flat Design với gradient backgrounds
- **Responsive**: Tự động điều chỉnh kích thước cửa sổ

## 🐛 Gỡ lỗi

### Vấn đề: MongoDB không kết nối được
```
Solution: Kiểm tra MongoDB đang chạy: mongod
```

### Vấn đề: Không nhập được dữ liệu
```
Solution: Import lại từ data/json files bằng mongoimport
```

### Vấn đề: JavaFX không tải được
```
Solution: Đảm bảo JDK 21+ và cài đặt đầy đủ dependencies
mvn clean install
```

## 📝 Ghi chú

- Điểm được tính hệ số 10 (số câu đúng / tổng câu * 10)
- Điểm >= 5 là ĐỖ, < 5 là KHÔNG ĐỖ
- Câu hỏi được xáo trộn ngẫu nhiên mỗi khi vào làm bài
- Thời gian làm bài có giới hạn, hết giờ sẽ tự động nộp

## 🚀 Tính năng sắp tới

- [ ] Tạo đề thi cho giảng viên
- [ ] Quản lý câu hỏi
- [ ] Xem thống kê chi tiết cho giảng viên
- [ ] Xuất kết quả ra PDF
- [ ] Hỗ trợ đa ngôn ngữ
- [ ] Dark mode

## 📞 Hỗ trợ

Nếu gặp vấn đề, vui lòng kiểm tra:
1. MongoDB có đang chạy?
2. Các dependency đã được cài đặt đầy đủ?
3. Java version có >= 21?
4. Port 27017 (MongoDB) có sẵn sàng?

## 📄 License

Dự án cho mục đích học tập.

---

**Phiên bản**: 1.0
**Cập nhật**: April 14, 2026

