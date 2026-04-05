package iuh.fit.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import iuh.fit.db.MongoDbConnection;
import iuh.fit.entity.Quiz;
import iuh.fit.repository.IQuizRepository;

public class QuizRepositoryImpl implements IQuizRepository {
    private final MongoCollection<Quiz> collection;

    public QuizRepositoryImpl() {
        this.collection = MongoDbConnection.getInstance()
                .getDatabase().getCollection("Quizzes", Quiz.class);
    }

    @Override
    public Quiz findById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }
}