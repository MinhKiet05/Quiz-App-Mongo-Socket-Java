package iuh.fit.repository.impl;

import com.mongodb.client.MongoCollection;
import iuh.fit.db.MongoDbConnection;
import iuh.fit.entity.Submission;
import iuh.fit.repository.ISubmissionRepository;

import java.util.UUID;

public class SubmissionRepositoryImpl implements ISubmissionRepository {
    private final MongoCollection<Submission> collection;

    public SubmissionRepositoryImpl() {
        this.collection = MongoDbConnection.getInstance()
                .getDatabase().getCollection("Submissions", Submission.class);
    }

    @Override
    public void save(Submission submission) {
        // Tự động sinh ID ngẫu nhiên nếu chưa có
        if (submission.getId() == null) {
            submission.setId("sub_" + UUID.randomUUID().toString().substring(0, 8));
        }
        collection.insertOne(submission);
    }
}
