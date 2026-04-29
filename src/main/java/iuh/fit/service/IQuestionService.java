package iuh.fit.service;

import iuh.fit.dto.QuestionDTO;

import java.util.List;

public interface IQuestionService {
    // Thêm câu hỏi mới
    void addQuestion(QuestionDTO questionDTO);

    // Cập nhật câu hỏi
    void updateQuestion(QuestionDTO questionDTO);

    // Xóa câu hỏi theo ID
    void deleteQuestion(String questionId);

    // Lấy danh sách câu hỏi dựa trên mã môn học (subject_id)
    List<QuestionDTO> findBySubjectId(String subjectId);

    // Lấy tất cả câu hỏi
    List<QuestionDTO> getAllQuestions();

    // Tìm kiếm câu hỏi theo nội dung
    List<QuestionDTO> searchByContent(String keyword);

    // Xóa tất cả câu hỏi của một môn
    void deleteBySubjectId(String subjectId);

    // Đếm số câu hỏi của một môn
    long countBySubjectId(String subjectId);
}