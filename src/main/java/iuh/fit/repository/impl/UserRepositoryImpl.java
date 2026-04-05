package iuh.fit.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import iuh.fit.db.MongoDbConnection;
import iuh.fit.entity.User;
import iuh.fit.repository.IUserRepository;

public class UserRepositoryImpl implements IUserRepository {
    private final MongoCollection<User> collection;

    public UserRepositoryImpl() {
        this.collection = MongoDbConnection.getInstance()
                .getDatabase().getCollection("Users", User.class);
    }

    @Override
    public User findByIdAndPassword(String id, String password) {
        // Tìm khớp _id (mã số) và password
        return collection.find(Filters.and(
                Filters.eq("_id", id),
                Filters.eq("password", password),
                Filters.eq("status", "ACTIVE")
        )).first();
    }
}