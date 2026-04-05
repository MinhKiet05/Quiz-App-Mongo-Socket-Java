package iuh.fit.repository;

import iuh.fit.entity.Quiz;

public interface IQuizRepository {
    Quiz findById(String id);
}