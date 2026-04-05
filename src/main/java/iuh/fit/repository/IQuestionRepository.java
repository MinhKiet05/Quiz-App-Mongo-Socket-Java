package iuh.fit.repository;

import iuh.fit.entity.Question;

import java.util.List;

public interface IQuestionRepository {
    // Lấy danh sách câu hỏi theo ID môn học
    List<Question> findBySubjectId(String subjectId);

    // Lấy chi tiết 1 câu hỏi theo ID
    Question findById(String id);
}
