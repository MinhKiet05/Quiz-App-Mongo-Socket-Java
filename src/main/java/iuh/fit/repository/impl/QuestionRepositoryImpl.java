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
        // Lấy collection "questions" và tự động map kết quả về class Question
        this.collection = MongoDbConnection.getInstance()
                .getDatabase()
                .getCollection("questions", Question.class);
    }

    @Override
    public void add(Question question) {
        // Thêm câu hỏi mới vào collection
        System.out.println("[QuestionRepository] === SAVING TO MONGODB ===");
        System.out.println("[QuestionRepository] Question ID: " + question.getId());
        System.out.println("[QuestionRepository] Question SubjectId: " + question.getSubjectId());
        System.out.println("[QuestionRepository] Question CreatedBy: " + question.getCreatedBy());
        System.out.println("[QuestionRepository] Question Content: " + question.getContent());
        System.out.println("[QuestionRepository] Question Difficulty: " + question.getDifficulty());
        System.out.println("[QuestionRepository] Question CorrectAnswer: " + question.getCorrectAnswer());
        System.out.println("[QuestionRepository] Question Options: " + question.getOptions());
        collection.insertOne(question);
        System.out.println("[QuestionRepository] ✓ Inserted: " + question.getId());
    }

    @Override
    public void update(Question question) {
        // Cập nhật câu hỏi (thay thế toàn bộ document)
        collection.replaceOne(Filters.eq("_id", question.getId()), question);
        System.out.println("[QuestionRepository] ✓ Updated: " + question.getId());
    }

    @Override
    public void deleteById(String id) {
        // Xóa câu hỏi theo ID
        collection.deleteOne(Filters.eq("_id", id));
        System.out.println("[QuestionRepository] ✓ Deleted: " + id);
    }

    @Override
    public List<Question> findBySubjectId(String subjectId) {
        // Tương đương câu SQL: SELECT * FROM questions WHERE subject_id = ?
        return collection.find(Filters.eq("subject_id", subjectId))
                .into(new ArrayList<>());
    }

    @Override
    public Question findById(String id) {
        // Tương đương câu SQL: SELECT * FROM questions WHERE _id = ? LIMIT 1
        return collection.find(Filters.eq("_id", id)).first();
    }

    @Override
    public List<Question> getAll() {
        // Lấy tất cả câu hỏi
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public List<Question> searchByContent(String keyword) {
        // Tìm kiếm theo nội dung câu hỏi (case-insensitive regex)
        return collection.find(Filters.regex("content", keyword, "i"))
                .into(new ArrayList<>());
    }

    @Override
    public void deleteBySubjectId(String subjectId) {
        // Xóa tất cả câu hỏi của một môn
        collection.deleteMany(Filters.eq("subject_id", subjectId));
        System.out.println("[QuestionRepository] ✓ Deleted all questions for subject: " + subjectId);
    }

    @Override
    public long countBySubjectId(String subjectId) {
        // Đếm số câu hỏi của một môn
        return collection.countDocuments(Filters.eq("subject_id", subjectId));
    }
}