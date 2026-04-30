package iuh.fit.repository.impl;

import com.mongodb.client.MongoCollection;
import iuh.fit.db.MongoDbConnection;
import iuh.fit.entity.Subject;
import iuh.fit.repository.ISubjectRepository;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.List;

public class SubjectRepositoryImpl implements ISubjectRepository {
    private final MongoCollection<Subject> collection;

    public SubjectRepositoryImpl() {
        this.collection = MongoDbConnection.getInstance()
                .getDatabase()
                .getCollection("subjects", Subject.class);
    }

    @Override
    public List<Subject> getAll() {
        // Lấy tất cả môn học
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public Subject findById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    @Override
    public void add(Subject subject) {
        collection.insertOne(subject);
    }

    @Override
    public void update(Subject subject) {
        collection.replaceOne(Filters.eq("_id", subject.getId()), subject);
    }

    @Override
    public void deleteById(String id) {
        collection.deleteOne(Filters.eq("_id", id));
    }
}
