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
                .getCollection("questions", Question.class);
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

    @Override
    public void add(Question question) {
        // Thêm câu hỏi mới vào MongoDB
        // Tương đương SQL: INSERT INTO Questions (...) VALUES (...)
        collection.insertOne(question);
    }

    @Override
    public void update(Question question) {
        // Cập nhật câu hỏi theo ID
        // Tương đương SQL: UPDATE Questions SET ... WHERE _id = ?
        collection.replaceOne(Filters.eq("_id", question.getId()), question);
    }

    @Override
    public void deleteById(String id) {
        // Xóa câu hỏi theo ID
        // Tương đương SQL: DELETE FROM Questions WHERE _id = ?
        collection.deleteOne(Filters.eq("_id", id));
    }

    @Override
    public List<Question> searchByContent(String keyword) {
        // Tìm kiếm câu hỏi theo nội dung (content chứa keyword)
        // Sử dụng regex để tìm kiếm không phân biệt hoa thường
        return collection.find(
                Filters.regex("content", keyword, "i")
        ).into(new ArrayList<>());
    }

    @Override
    public List<Question> getAll() {
        // Lấy tất cả câu hỏi
        // Tương đương SQL: SELECT * FROM Questions
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void deleteBySubjectId(String subjectId) {
        // Xóa tất cả câu hỏi của một môn học
        // Tương đương SQL: DELETE FROM Questions WHERE subject_id = ?
        collection.deleteMany(Filters.eq("subject_id", subjectId));
    }

    @Override
    public long countBySubjectId(String subjectId) {
        // Đếm số lượng câu hỏi của một môn học
        // Tương đương SQL: SELECT COUNT(*) FROM Questions WHERE subject_id = ?
        return collection.countDocuments(Filters.eq("subject_id", subjectId));
    }
}