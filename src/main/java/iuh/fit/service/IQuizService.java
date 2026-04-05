package iuh.fit.service;

import iuh.fit.dto.QuizDTO;
import iuh.fit.dto.SubmissionDTO;

public interface IQuizService {
    QuizDTO getQuizForCandidate(String quizId);
    double calculateScore(SubmissionDTO submissionDto);
}