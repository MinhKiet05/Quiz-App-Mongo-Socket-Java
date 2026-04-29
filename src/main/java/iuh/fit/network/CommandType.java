package iuh.fit.network;

public enum CommandType {
    LOGIN,                              // Đăng nhập
    GET_ALL_QUIZZES,                    // Lấy tất cả đề thi
    GET_QUIZ_BY_ID,                     // Lấy thông tin đề thi và danh sách câu hỏi
    SUBMIT_QUIZ,                        // Nộp bài và nhận điểm
    CHANGE_PASSWORD,                    // Đổi mật khẩu
    
    // ============ Quản lý Câu hỏi ============
    ADD_QUESTION,                       // Thêm câu hỏi mới
    UPDATE_QUESTION,                    // Cập nhật câu hỏi
    DELETE_QUESTION,                    // Xóa câu hỏi theo ID
    GET_QUESTIONS_BY_SUBJECT,           // Lấy câu hỏi theo môn học
    SEARCH_QUESTIONS,                   // Tìm kiếm câu hỏi
    GET_ALL_QUESTIONS,                  // Lấy tất cả câu hỏi
    DELETE_QUESTIONS_BY_SUBJECT,        // Xóa tất cả câu hỏi của một môn học
    GET_QUESTION_COUNT_BY_SUBJECT,      // Đếm số câu hỏi theo môn học
    
    // ============ Quản lý Môn học ============
    GET_ALL_SUBJECTS                    // Lấy tất cả môn học
}
