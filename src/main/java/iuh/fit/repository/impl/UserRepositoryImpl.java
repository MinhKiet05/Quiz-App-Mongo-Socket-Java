package iuh.fit.repository.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import iuh.fit.db.MongoDbConnection;
import iuh.fit.entity.User;
import iuh.fit.repository.IUserRepository;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserRepositoryImpl implements IUserRepository {
    private final MongoCollection<User> collection;

    public UserRepositoryImpl() {
        this.collection = MongoDbConnection.getInstance()
                .getDatabase().getCollection("users", User.class);
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

    @Override
    public List<User> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public List<User> search(String keyword, String role, String status) {
        List<Bson> conditions = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            Pattern regex = Pattern.compile(Pattern.quote(keyword.trim()), Pattern.CASE_INSENSITIVE);
            conditions.add(Filters.or(
                    Filters.regex("_id", regex),
                    Filters.regex("username", regex)
            ));
        }
        if (role != null && !role.isBlank() && !"ALL".equalsIgnoreCase(role)) {
            conditions.add(Filters.eq("role", role.trim().toUpperCase()));
        }
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            conditions.add(Filters.eq("status", status.trim().toUpperCase()));
        }

        Bson query = conditions.isEmpty() ? new org.bson.Document() : Filters.and(conditions);
        return collection.find(query).into(new ArrayList<>());
    }

    @Override
    public User findById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    @Override
    public void add(User user) {
        collection.insertOne(user);
    }

    @Override
    public void update(User user) {
        // Replace the existing user document with the new one
        collection.replaceOne(Filters.eq("_id", user.getId()), user);
    }

    @Override
    public void deleteById(String id) {
        collection.deleteOne(Filters.eq("_id", id));
    }
}