package iuh.fit.repository;

import iuh.fit.entity.Question;

import java.util.List;

public interface IQuestionRepository {
    // Lấy danh sách câu hỏi theo ID môn học
    List<Question> findBySubjectId(String subjectId);

    // Lấy chi tiết 1 câu hỏi theo ID
    Question findById(String id);

    // Thêm câu hỏi mới
    void add(Question question);

    // Cập nhật câu hỏi
    void update(Question question);

    // Xóa câu hỏi theo ID
    void deleteById(String id);

    // Tìm kiếm câu hỏi theo nội dung (content)
    List<Question> searchByContent(String keyword);

    // Lấy tất cả câu hỏi
    List<Question> getAll();

    // Xóa tất cả câu hỏi của một môn học
    void deleteBySubjectId(String subjectId);

    // Đếm số câu hỏi của một môn học
    long countBySubjectId(String subjectId);
}
