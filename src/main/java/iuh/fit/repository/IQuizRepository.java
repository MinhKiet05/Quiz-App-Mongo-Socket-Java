package iuh.fit.repository;

import iuh.fit.entity.Quiz;
import java.util.List;

public interface IQuizRepository {
    Quiz findById(String id);
    List<Quiz> findAll();
}