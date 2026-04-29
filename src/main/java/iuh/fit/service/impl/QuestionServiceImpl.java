package iuh.fit.service.impl;

import iuh.fit.dto.QuestionDTO;
import iuh.fit.entity.Question;
import iuh.fit.mapper.DataMapper;
import iuh.fit.repository.IQuestionRepository;
import iuh.fit.service.IQuestionService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

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

    @Override
    public QuestionDTO getById(String id) {
        // 1. Gọi Repository để lấy câu hỏi từ MongoDB
        Question question = questionRepository.findById(id);

        if (question == null) {
            throw new RuntimeException("Không tìm thấy câu hỏi với ID: " + id);
        }

        // 2. Chuyển đổi Entity sang DTO
        return DataMapper.map(question, QuestionDTO.class);
    }

    @Override
    public void addQuestion(QuestionDTO questionDTO) {
        // Validation
        if (questionDTO == null || questionDTO.getContent() == null || questionDTO.getContent().isEmpty()) {
            throw new RuntimeException("Nội dung câu hỏi không được để trống!");
        }

        if (questionDTO.getOptions() == null || questionDTO.getOptions().size() == 0) {
            throw new RuntimeException("Câu hỏi phải có ít nhất 1 đáp án!");
        }

        if (questionDTO.getCorrectAnswer() == null || questionDTO.getCorrectAnswer().isEmpty()) {
            throw new RuntimeException("Phải chọn đáp án đúng!");
        }

        // 1. Tạo ID tự động nếu chưa có
        if (questionDTO.getId() == null || questionDTO.getId().isEmpty()) {
            questionDTO.setId(UUID.randomUUID().toString());
        }

        // 2. Chuyển đổi DTO sang Entity
        Question question = DataMapper.map(questionDTO, Question.class);

        // 3. Lưu vào MongoDB thông qua Repository
        questionRepository.add(question);
    }

    @Override
    public void updateQuestion(QuestionDTO questionDTO) {
        // Validation
        if (questionDTO == null || questionDTO.getId() == null || questionDTO.getId().isEmpty()) {
            throw new RuntimeException("ID câu hỏi không được để trống!");
        }

        if (questionDTO.getContent() == null || questionDTO.getContent().isEmpty()) {
            throw new RuntimeException("Nội dung câu hỏi không được để trống!");
        }

        if (questionDTO.getOptions() == null || questionDTO.getOptions().size() == 0) {
            throw new RuntimeException("Câu hỏi phải có ít nhất 1 đáp án!");
        }

        if (questionDTO.getCorrectAnswer() == null || questionDTO.getCorrectAnswer().isEmpty()) {
            throw new RuntimeException("Phải chọn đáp án đúng!");
        }

        // 1. Kiểm tra xem câu hỏi có tồn tại không
        Question existingQuestion = questionRepository.findById(questionDTO.getId());
        if (existingQuestion == null) {
            throw new RuntimeException("Không tìm thấy câu hỏi để cập nhật!");
        }

        // 2. Cập nhật các trường cho phép thay đổi từ DTO
        existingQuestion.setContent(questionDTO.getContent());
        existingQuestion.setOptions(questionDTO.getOptions());
        existingQuestion.setDifficulty(questionDTO.getDifficulty());
        existingQuestion.setCorrectAnswer(questionDTO.getCorrectAnswer());
        
        // Nếu DTO có subjectId, cập nhật; nếu không, giữ lại giá trị cũ
        if (questionDTO.getSubjectId() != null && !questionDTO.getSubjectId().isEmpty()) {
            existingQuestion.setSubjectId(questionDTO.getSubjectId());
        }
        
        // Nếu DTO có createdBy, cập nhật; nếu không, giữ lại giá trị cũ
        if (questionDTO.getCreatedBy() != null && !questionDTO.getCreatedBy().isEmpty()) {
            existingQuestion.setCreatedBy(questionDTO.getCreatedBy());
        }

        // 3. Cập nhật vào MongoDB
        questionRepository.update(existingQuestion);
    }

    @Override
    public void deleteQuestion(String id) {
        // Validation
        if (id == null || id.isEmpty()) {
            throw new RuntimeException("ID câu hỏi không được để trống!");
        }

        // 1. Kiểm tra xem câu hỏi có tồn tại không
        Question question = questionRepository.findById(id);
        if (question == null) {
            throw new RuntimeException("Không tìm thấy câu hỏi để xóa!");
        }

        // 2. Xóa từ MongoDB
        questionRepository.deleteById(id);
    }

    @Override
    public List<QuestionDTO> searchQuestions(String keyword) {
        // Validation
        if (keyword == null || keyword.isEmpty()) {
            throw new RuntimeException("Từ khóa tìm kiếm không được để trống!");
        }

        // 1. Tìm kiếm câu hỏi từ Repository
        List<Question> questions = questionRepository.searchByContent(keyword);

        // 2. Chuyển đổi sang DTO
        return DataMapper.mapList(questions, QuestionDTO.class);
    }

    @Override
    public List<QuestionDTO> getAllQuestions() {
        // 1. Lấy tất cả câu hỏi từ Repository
        List<Question> questions = questionRepository.getAll();

        // 2. Chuyển đổi sang DTO
        return DataMapper.mapList(questions, QuestionDTO.class);
    }

    @Override
    public void deleteQuestionsBySubject(String subjectId) {
        // Validation
        if (subjectId == null || subjectId.isEmpty()) {
            throw new RuntimeException("ID môn học không được để trống!");
        }

        // Xóa tất cả câu hỏi của môn học từ MongoDB
        questionRepository.deleteBySubjectId(subjectId);
    }

    @Override
    public long getQuestionCountBySubject(String subjectId) {
        // Validation
        if (subjectId == null || subjectId.isEmpty()) {
            throw new RuntimeException("ID môn học không được để trống!");
        }

        // Đếm số câu hỏi của môn học từ Repository
        return questionRepository.countBySubjectId(subjectId);
    }
}