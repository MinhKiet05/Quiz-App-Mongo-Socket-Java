package iuh.fit.network;

public enum CommandType {
    LOGIN,                  // Đăng nhập
    GET_ALL_QUIZZES,        // Lấy tất cả đề thi
    GET_QUIZ_BY_ID,         // Lấy thông tin đề thi và danh sách câu hỏi
    SUBMIT_QUIZ             // Nộp bài và nhận điểm
}
