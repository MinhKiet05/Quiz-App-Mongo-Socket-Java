package iuh.fit.service;


import iuh.fit.dto.QuestionDTO;

import java.util.List;

public interface IQuestionService {
    // Lấy danh sách câu hỏi dựa trên mã môn học (subject_id)
    List<QuestionDTO> findBySubjectId(String subjectId);

    // Bạn có thể định nghĩa thêm các hàm khác ở đây sau này
    // Ví dụ: boolean addQuestion(QuestionDTO questionDTO);
}