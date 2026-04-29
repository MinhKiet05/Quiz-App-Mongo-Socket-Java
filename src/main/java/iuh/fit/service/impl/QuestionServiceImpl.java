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
    public void addQuestion(QuestionDTO questionDTO) {
        System.out.println("[QuestionService] === RECEIVED ADD_QUESTION REQUEST ===");
        System.out.println("[QuestionService] DTO ID: " + questionDTO.getId());
        System.out.println("[QuestionService] DTO SubjectId: " + questionDTO.getSubjectId());
        System.out.println("[QuestionService] DTO CreatedBy: " + questionDTO.getCreatedBy());
        System.out.println("[QuestionService] DTO Content: " + questionDTO.getContent());
        System.out.println("[QuestionService] DTO Difficulty: " + questionDTO.getDifficulty());
        System.out.println("[QuestionService] DTO CorrectAnswer: " + questionDTO.getCorrectAnswer());
        
        // 1. Validate dữ liệu đầu vào
        if (questionDTO.getContent() == null || questionDTO.getContent().trim().isEmpty()) {
            throw new RuntimeException("Nội dung câu hỏi không được để trống");
        }

        if (questionDTO.getOptions() == null || questionDTO.getOptions().isEmpty()) {
            throw new RuntimeException("Phải có ít nhất một lựa chọn");
        }

        if (questionDTO.getCorrectAnswer() == null || questionDTO.getCorrectAnswer().trim().isEmpty()) {
            throw new RuntimeException("Đáp án đúng không được để trống");
        }

        // 2. Tạo ID nếu chưa có
        if (questionDTO.getId() == null || questionDTO.getId().trim().isEmpty()) {
            questionDTO.setId(UUID.randomUUID().toString());
        }

        // 3. Chuyển đổi DTO sang Entity - MANUAL MAPPING để đảm bảo tất cả fields được copy
        Question question = new Question();
        question.setId(questionDTO.getId());
        question.setContent(questionDTO.getContent());
        question.setOptions(questionDTO.getOptions());
        question.setDifficulty(questionDTO.getDifficulty());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
        question.setSubjectId(questionDTO.getSubjectId());  // ✅ Manual set subjectId
        question.setCreatedBy(questionDTO.getCreatedBy());   // ✅ Manual set createdBy
        
        System.out.println("[QuestionService] === AFTER MAPPING TO ENTITY ===");
        System.out.println("[QuestionService] Entity ID: " + question.getId());
        System.out.println("[QuestionService] Entity SubjectId: " + question.getSubjectId());
        System.out.println("[QuestionService] Entity CreatedBy: " + question.getCreatedBy());
        System.out.println("[QuestionService] Entity Content: " + question.getContent());
        System.out.println("[QuestionService] Entity Difficulty: " + question.getDifficulty());
        System.out.println("[QuestionService] Entity CorrectAnswer: " + question.getCorrectAnswer());
        System.out.println("[QuestionService] Entity Options: " + question.getOptions());

        // 4. Lưu vào MongoDB thông qua Repository
        questionRepository.add(question);

        System.out.println("[QuestionService] ✅ Question saved to MongoDB: " + question.getId());
    }

    @Override
    public void updateQuestion(QuestionDTO questionDTO) {
        // 1. Validate ID
        if (questionDTO.getId() == null || questionDTO.getId().trim().isEmpty()) {
            throw new RuntimeException("ID câu hỏi không được để trống");
        }

        // 2. Lấy câu hỏi hiện tại từ Database
        Question existingQuestion = questionRepository.findById(questionDTO.getId());
        if (existingQuestion == null) {
            throw new RuntimeException("Không tìm thấy câu hỏi với ID: " + questionDTO.getId());
        }

        // 3. Cập nhật các trường từ DTO (giữ lại trường chưa thay đổi)
        if (questionDTO.getContent() != null && !questionDTO.getContent().trim().isEmpty()) {
            existingQuestion.setContent(questionDTO.getContent());
        }

        if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
            existingQuestion.setOptions(questionDTO.getOptions());
        }

        if (questionDTO.getDifficulty() != null && !questionDTO.getDifficulty().trim().isEmpty()) {
            existingQuestion.setDifficulty(questionDTO.getDifficulty());
        }

        if (questionDTO.getCorrectAnswer() != null && !questionDTO.getCorrectAnswer().trim().isEmpty()) {
            existingQuestion.setCorrectAnswer(questionDTO.getCorrectAnswer());
        }

        // Chỉ cập nhật subjectId & createdBy nếu DTO cung cấp giá trị hợp lệ
        if (questionDTO.getSubjectId() != null && !questionDTO.getSubjectId().trim().isEmpty()) {
            existingQuestion.setSubjectId(questionDTO.getSubjectId());
            System.out.println("[QuestionService] Cập nhật subjectId: " + questionDTO.getSubjectId());
        }

        if (questionDTO.getCreatedBy() != null && !questionDTO.getCreatedBy().trim().isEmpty()) {
            existingQuestion.setCreatedBy(questionDTO.getCreatedBy());
            System.out.println("[QuestionService] Cập nhật createdBy: " + questionDTO.getCreatedBy());
        }

        // 4. Lưu lại vào MongoDB
        questionRepository.update(existingQuestion);

        System.out.println("[QuestionService] ✅ Cập nhật câu hỏi: " + existingQuestion.getId());
    }

    @Override
    public void deleteQuestion(String questionId) {
        // 1. Validate ID
        if (questionId == null || questionId.trim().isEmpty()) {
            throw new RuntimeException("ID câu hỏi không được để trống");
        }

        // 2. Kiểm tra câu hỏi có tồn tại
        Question question = questionRepository.findById(questionId);
        if (question == null) {
            throw new RuntimeException("Không tìm thấy câu hỏi với ID: " + questionId);
        }

        // 3. Xóa từ MongoDB
        questionRepository.deleteById(questionId);

        System.out.println("[QuestionService] ✅ Xóa câu hỏi: " + questionId);
    }

    @Override
    public List<QuestionDTO> findBySubjectId(String subjectId) {
        // 1. Gọi xuống tầng Repository để lấy dữ liệu Entity từ MongoDB
        List<Question> questions = questionRepository.findBySubjectId(subjectId);

        // 2. Sử dụng DataMapper để chuyển đổi List<Question> sang List<QuestionDTO>
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
    public List<QuestionDTO> searchByContent(String keyword) {
        // 1. Lấy kết quả tìm kiếm từ Repository
        List<Question> questions = questionRepository.searchByContent(keyword);

        // 2. Chuyển đổi sang DTO
        return DataMapper.mapList(questions, QuestionDTO.class);
    }

    @Override
    public void deleteBySubjectId(String subjectId) {
        questionRepository.deleteBySubjectId(subjectId);
        System.out.println("[QuestionService] ✅ Xóa tất cả câu hỏi của môn: " + subjectId);
    }

    @Override
    public long countBySubjectId(String subjectId) {
        return questionRepository.countBySubjectId(subjectId);
    }
}