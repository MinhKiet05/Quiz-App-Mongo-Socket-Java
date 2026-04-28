package iuh.fit.service.impl;

import iuh.fit.dto.QuizDTO;
import iuh.fit.dto.SubmissionDTO;
import iuh.fit.entity.Question;
import iuh.fit.entity.Quiz;
import iuh.fit.repository.IQuestionRepository;
import iuh.fit.repository.IQuizRepository;
import iuh.fit.service.IQuizService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Collections;

@RequiredArgsConstructor
public class QuizServiceImpl implements IQuizService {
    private final IQuizRepository quizRepository;
    private final IQuestionRepository questionRepository; // Cần gọi DB câu hỏi để tính điểm

    @Override
    public QuizDTO getQuizForCandidate(String quizId) {
        Quiz quiz = quizRepository.findById(quizId);
        if (quiz == null) throw new RuntimeException("Không tìm thấy đề thi!");

        // 1. Logic kiểm tra thời gian mở/đóng
        Instant now = Instant.now();
        Instant openTime = Instant.parse(quiz.getOpenTime());
        Instant closeTime = Instant.parse(quiz.getCloseTime());

        if (now.isBefore(openTime)) {
            throw new RuntimeException("Đề thi chưa đến giờ mở! Mở lúc: " + openTime);
        }
        if (now.isAfter(closeTime)) {
            throw new RuntimeException("Đề thi đã kết thúc! Đóng lúc: " + closeTime);
        }
        if (!"PUBLISHED".equals(quiz.getStatus()) && !"DRAFT".equals(quiz.getStatus())) {
            throw new RuntimeException("Đề thi chưa được công bố!");
        }

        // 2. Logic xáo trộn câu hỏi
        // Copy danh sách ID câu hỏi để không ảnh hưởng dữ liệu gốc
        java.util.List<String> shuffledQuestionIds = new java.util.ArrayList<>(quiz.getQuestionIds());
        Collections.shuffle(shuffledQuestionIds);

        // 3. Load QuestionDTOs từ questionIds từ MongoDB
        java.util.List<iuh.fit.dto.QuestionDTO> questions = new java.util.ArrayList<>();
        for (String questionId : shuffledQuestionIds) {
            Question question = questionRepository.findById(questionId);
            if (question != null) {
                questions.add(iuh.fit.dto.QuestionDTO.builder()
                        .id(question.getId())
                        .subjectId(question.getSubjectId())
                        .content(question.getContent())
                        .options(question.getOptions())
                        .difficulty(question.getDifficulty())
                        .build());
            } else {
                System.err.println("[Service] Question not found: " + questionId);
            }
        }

        if (questions.isEmpty()) {
            throw new RuntimeException("Không tìm thấy câu hỏi cho đề thi này!");
        }

        // Map sang DTO để gửi cho Client
        return QuizDTO.builder()
                .id(quiz.getId())
                .subjectId(quiz.getSubjectId())
                .title(quiz.getTitle())
                .durationMinutes(quiz.getDurationMinutes())
                .openTime(quiz.getOpenTime())
                .closeTime(quiz.getCloseTime())
                .status(quiz.getStatus())
                .questionIds(shuffledQuestionIds) // Gửi danh sách đã xáo trộn
                .questions(questions) // Gửi đầy đủ QuestionDTOs từ MongoDB
                .build();
    }

    @Override
    public java.util.List<QuizDTO> getAllQuizzes() {
        java.util.List<iuh.fit.entity.Quiz> quizzes = quizRepository.findAll();
        java.util.List<QuizDTO> dtos = new java.util.ArrayList<>();
        
        for (iuh.fit.entity.Quiz quiz : quizzes) {
            // Lấy tất cả các quizzes (bỏ filter status để hiển thị cả PUBLISHED và DRAFT)
            // Chỉ lấy những quizzes có thời gian mở hợp lệ
            try {
                java.time.Instant now = java.time.Instant.now();
                java.time.Instant closeTime = java.time.Instant.parse(quiz.getCloseTime());
                
                // Chỉ hiển thị nếu chưa qua thời gian đóng
                if (now.isBefore(closeTime)) {
                    dtos.add(QuizDTO.builder()
                            .id(quiz.getId())
                            .subjectId(quiz.getSubjectId())
                            .title(quiz.getTitle())
                            .durationMinutes(quiz.getDurationMinutes())
                            .openTime(quiz.getOpenTime())
                            .closeTime(quiz.getCloseTime())
                            .status(quiz.getStatus())
                            .questionIds(new java.util.ArrayList<>(quiz.getQuestionIds()))
                            .build());
                }
            } catch (Exception e) {
                System.err.println("[Service] Error parsing quiz times for " + quiz.getId() + ": " + e.getMessage());
            }
        }
        return dtos;
    }

    @Override
    public double calculateScore(SubmissionDTO submissionDto) {
        Quiz quiz = quizRepository.findById(submissionDto.getQuizId());
        if (quiz == null) return 0.0;

        int totalQuestions = quiz.getQuestionIds().size();
        int correctCount = 0;

        // Chấm điểm từng câu
        for (SubmissionDTO.SubmissionDetailDTO detail : submissionDto.getDetails()) {
            Question question = questionRepository.findById(detail.getQuestionId());
            if (question != null && question.getCorrectAnswer().equals(detail.getSelectedOption())) {
                correctCount++;
                detail.setCorrect(true); // Cập nhật trạng thái câu này là Đúng
            } else {
                detail.setCorrect(false); // Sai
            }
        }

        // Tính điểm hệ số 10 (làm tròn 2 chữ số thập phân nếu cần)
        double score = ((double) correctCount / totalQuestions) * 10;
        return Math.round(score * 100.0) / 100.0;
    }
}
