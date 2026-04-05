package iuh.fit.service.impl;

import iuh.fit.dto.SubmissionDTO;
import iuh.fit.entity.Submission;
import iuh.fit.repository.ISubmissionRepository;
import iuh.fit.service.IQuizService;
import iuh.fit.service.ISubmissionService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SubmissionServiceImpl implements ISubmissionService {
    private final ISubmissionRepository submissionRepository;
    private final IQuizService quizService; // Gọi sang QuizService để tính điểm

    @Override
    public SubmissionDTO submitQuiz(SubmissionDTO submissionDto) {
        // 1. Gọi logic tính điểm từ QuizService
        double finalScore = quizService.calculateScore(submissionDto);
        submissionDto.setScore(finalScore);

        // Đặt thời gian nộp bài là ngay lúc này
        submissionDto.setSubmitTime(Instant.now().toString());

        // 2. Map từ DTO sang Entity để lưu xuống MongoDB
        Submission submission = Submission.builder()
                .quizId(submissionDto.getQuizId())
                .candidateId(submissionDto.getCandidateId())
                .startTime(submissionDto.getStartTime())
                .submitTime(submissionDto.getSubmitTime())
                .score(submissionDto.getScore())
                // Map từng chi tiết bài nộp
                .details(submissionDto.getDetails().stream().map(d ->
                        Submission.SubmissionDetail.builder()
                                .questionId(d.getQuestionId())
                                .selectedOption(d.getSelectedOption())
                                .isCorrect(d.isCorrect())
                                .build()
                ).collect(Collectors.toList()))
                .build();

        // 3. Lưu vào Database
        submissionRepository.save(submission);

        // Cập nhật ID mới sinh ra vào DTO để trả về cho Client
        submissionDto.setId(submission.getId());

        // 4. Trả về DTO (lúc này đã có Điểm số và trạng thái Đúng/Sai của từng câu)
        return submissionDto;
    }
}
