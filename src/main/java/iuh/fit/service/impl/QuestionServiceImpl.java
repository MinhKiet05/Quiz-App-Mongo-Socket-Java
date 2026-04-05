package iuh.fit.service.impl;

import iuh.fit.dto.QuestionDTO;
import iuh.fit.entity.Question;
import iuh.fit.mapper.DataMapper;
import iuh.fit.repository.IQuestionRepository;
import iuh.fit.service.IQuestionService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class QuestionServiceImpl implements IQuestionService {

    // Lombok sẽ tự động tiêm (inject) IQuestionRepository vào thông qua constructor
    private final IQuestionRepository questionRepository;

    @Override
    public List<QuestionDTO> findBySubjectId(String subjectId) {
        // 1. Gọi xuống tầng Repository để lấy dữ liệu Entity từ MongoDB
        List<Question> questions = questionRepository.findBySubjectId(subjectId);

        // 2. Sử dụng DataMapper để chuyển đổi List<Question> sang List<QuestionDTO>
        return DataMapper.mapList(questions, QuestionDTO.class);
    }
}