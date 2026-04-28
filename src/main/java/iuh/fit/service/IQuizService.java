package iuh.fit.service;

import iuh.fit.dto.QuizDTO;
import iuh.fit.dto.SubmissionDTO;
import java.util.List;

public interface IQuizService {
    QuizDTO getQuizForCandidate(String quizId);
    List<QuizDTO> getAllQuizzes();
    double calculateScore(SubmissionDTO submissionDto);
}