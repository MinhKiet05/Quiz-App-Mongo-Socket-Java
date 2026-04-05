package iuh.fit.repository;

import iuh.fit.entity.User;

public interface IUserRepository {
    // Đổi tên hàm và tham số đầu vào thành id
    User findByIdAndPassword(String id, String password);
}
