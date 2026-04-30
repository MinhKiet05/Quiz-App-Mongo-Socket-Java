package iuh.fit.repository;

import iuh.fit.entity.Question;

import java.util.List;

public interface IQuestionRepository {
    // Thêm câu hỏi mới
    void add(Question question);

    // Cập nhật câu hỏi
    void update(Question question);

    // Xóa câu hỏi theo ID
    void deleteById(String id);

    // Lấy danh sách câu hỏi theo ID môn học
    List<Question> findBySubjectId(String subjectId);

    // Lấy chi tiết 1 câu hỏi theo ID
    Question findById(String id);

    // Lấy tất cả câu hỏi
    List<Question> getAll();

    // Tìm kiếm câu hỏi theo nội dung (regex search)
    List<Question> searchByContent(String keyword);

    // Xóa tất cả câu hỏi của một môn
    void deleteBySubjectId(String subjectId);

    // Đếm số câu hỏi của một môn
    long countBySubjectId(String subjectId);
}
