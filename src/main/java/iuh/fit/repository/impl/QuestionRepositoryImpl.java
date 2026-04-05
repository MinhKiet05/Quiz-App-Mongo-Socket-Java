package iuh.fit.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import iuh.fit.db.MongoDbConnection;
import iuh.fit.entity.Question;
import iuh.fit.repository.IQuestionRepository;

import java.util.ArrayList;
import java.util.List;

public class QuestionRepositoryImpl implements IQuestionRepository {

    private final MongoCollection<Question> collection;

    public QuestionRepositoryImpl() {
        // Lấy collection "Questions" và tự động map kết quả về class Question
        this.collection = MongoDbConnection.getInstance()
                .getDatabase()
                .getCollection("Questions", Question.class);
    }

    @Override
    public List<Question> findBySubjectId(String subjectId) {
        // Tương đương câu SQL: SELECT * FROM Questions WHERE subject_id = ?
        return collection.find(Filters.eq("subject_id", subjectId))
                .into(new ArrayList<>());
    }

    @Override
    public Question findById(String id) {
        // Tương đương câu SQL: SELECT * FROM Questions WHERE _id = ? LIMIT 1
        return collection.find(Filters.eq("_id", id)).first();
    }
}