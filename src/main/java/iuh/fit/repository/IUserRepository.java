package iuh.fit.repository;

import iuh.fit.entity.User;
import java.util.List;

public interface IUserRepository {
    // Đổi tên hàm và tham số đầu vào thành id
    User findByIdAndPassword(String id, String password);
    List<User> findAll();
    List<User> search(String keyword, String role, String status);
    User findById(String id);
    void add(User user);
    void update(User user);
    void deleteById(String id);
}
