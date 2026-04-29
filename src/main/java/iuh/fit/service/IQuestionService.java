package iuh.fit.service;

import iuh.fit.dto.QuestionDTO;

import java.util.List;

public interface IQuestionService {
    // Lấy danh sách câu hỏi dựa trên mã môn học (subject_id)
    List<QuestionDTO> findBySubjectId(String subjectId);

    // Lấy chi tiết 1 câu hỏi theo ID
    QuestionDTO getById(String id);

    // Thêm câu hỏi mới
    void addQuestion(QuestionDTO questionDTO);

    // Cập nhật câu hỏi
    void updateQuestion(QuestionDTO questionDTO);

    // Xóa câu hỏi theo ID
    void deleteQuestion(String id);

    // Tìm kiếm câu hỏi theo nội dung
    List<QuestionDTO> searchQuestions(String keyword);

    // Lấy tất cả câu hỏi
    List<QuestionDTO> getAllQuestions();

    // Xóa tất cả câu hỏi của một môn học
    void deleteQuestionsBySubject(String subjectId);

    // Đếm số câu hỏi của một môn học
    long getQuestionCountBySubject(String subjectId);
}